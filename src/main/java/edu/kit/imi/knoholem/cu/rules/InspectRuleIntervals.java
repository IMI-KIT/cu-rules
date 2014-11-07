package edu.kit.imi.knoholem.cu.rules;

import edu.kit.imi.knoholem.cu.rules.atoms.Predicate;
import edu.kit.imi.knoholem.cu.rules.atoms.SensitivityAnalysisRule;
import edu.kit.imi.knoholem.cu.rules.atoms.processing.PredicateMap;
import edu.kit.imi.knoholem.cu.rules.atoms.processing.PredicateMapEntry;
import edu.kit.imi.knoholem.cu.rules.functions.Collect;
import edu.kit.imi.knoholem.cu.rules.functions.Monad;
import edu.kit.imi.knoholem.cu.rules.parser.RuleParserConfiguration;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

/**
 * @author <a href="mailto:kiril.tonev@kit.edu">Kiril Tonev</a>
 */
public class InspectRuleIntervals {
    public static void main(String[] args) {
        String url = args[0];
        String user = args[1];
        String password = args[2];

        String sensorsHistoryTable = args[3];
        String setpointsHistoryTable = args[4];

        String sensorsTable = args[5];
        String setpointsTable = args[6];

        String sensorColumn = args[7];
        String setpointColumn = args[8];

        List<String> files = Arrays.asList(args).subList(9, args.length);
        RuleParserConfiguration ruleParserConfiguration = RuleParserConfiguration.getDefaultConfiguration();
        SensorsDatabase sensorDatabase = new SensorsDatabase(url, user, password, sensorsHistoryTable, setpointsHistoryTable, sensorsTable, setpointsTable, sensorColumn, setpointColumn);
        History history = new History(sensorDatabase);

        Connection connection = null;
        try {
            connection = sensorDatabase.initializeConnection();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (connection == null) {
                System.err.println("Could not initialize connection.");
                System.exit(1);
            }
        }

        Set<String> sensorsInDatabase = null;
        try {
            sensorsInDatabase = sensorDatabase.fetchAllNames(connection);
        } catch (SQLException e) {
            System.err.println("Could not fetch rules.");
            e.printStackTrace();
            System.exit(1);
        }

        System.err.println("Fetched " + sensorsInDatabase.size() + " sensors.");
        InspectingParser ruleParser = new InspectingParser(ruleParserConfiguration, sensorsInDatabase, history);
        try {
            Collect collector = new ConvertRules.MultipleRuleFileParser(files, ruleParser).execute();
            Monad<SensitivityAnalysisRule> rules = collector.getRules();
            Map<String, Integer> warnings = ruleParser.getWarnings();
            for (String sensorName : warnings.keySet()) {
                System.out.println(sensorName + ", " + warnings.get(sensorName) + ", " + rules.size());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    protected static class InspectingParser extends AlignRules.AlignedRuleParser {

        private final History history;
        private final Map<String, DescriptiveStatistics> sensorStats;
        private final Map<String, Integer> warnings;

        private InspectingParser(RuleParserConfiguration configuration, Collection<String> sensorsInDatabase, History history) {
            super(configuration, sensorsInDatabase);
            this.history = history;
            this.sensorStats = new HashMap<String, DescriptiveStatistics>();
            this.warnings = new HashMap<String, Integer>();
        }

        public Map<String, Integer> getWarnings() {
            return warnings;
        }

        @Override
        protected List<Predicate> collectPredicates(List<String> ruleBodyTokens) {
            List<Predicate> predicates = super.collectPredicates(ruleBodyTokens);
            for (PredicateMapEntry mapEntry : new PredicateMap(predicates).byLeftOperand()) {
                String sensor = mapEntry.getClassifier().asString();
                if (raiseWarning(mapEntry, getSensorStatistics(sensor))) {
                    incrementWarning(sensor);
                }
            }
            return predicates;
        }

        protected void incrementWarning(String sensorName) {
            if (warnings.containsKey(sensorName)) {
                warnings.put(sensorName, warnings.get(sensorName) + 1);
            } else {
                warnings.put(sensorName, 1);
            }
        }

        protected DescriptiveStatistics getSensorStatistics(String sensorName) {
            DescriptiveStatistics statistics;
            if (sensorStats.containsKey(sensorName)) {
                statistics = sensorStats.get(sensorName);
            } else {
                try {
                    System.err.println("Fetching statistics for `" + sensorName + "'.");
                    statistics = history.sensorStats(sensorName);
                    sensorStats.put(sensorName, statistics);
                } catch (SQLException e) {
                    throw new RuntimeException("Could not fetch statistics for `" + sensorName + "'", e);
                }
            }
            return statistics;
        }

        protected boolean raiseWarning(PredicateMapEntry predicateMapEntry, DescriptiveStatistics statistics) {
            for (Predicate predicate : predicateMapEntry.getPredicates()) {
                double predicateValue = predicate.getRightOperand().asDouble();
                return predicateValue < statistics.getPercentile(25) || predicateValue > statistics.getPercentile(75);
            }
            return false;
        }
    }
}

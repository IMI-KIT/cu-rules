package edu.kit.imi.knoholem.cu.rules;

import edu.kit.imi.knoholem.cu.rules.atoms.Predicate;
import edu.kit.imi.knoholem.cu.rules.atoms.SensitivityAnalysisRule;
import edu.kit.imi.knoholem.cu.rules.atoms.processing.PredicateMap;
import edu.kit.imi.knoholem.cu.rules.atoms.processing.PredicateMapEntry;
import edu.kit.imi.knoholem.cu.rules.functions.Collect;
import edu.kit.imi.knoholem.cu.rules.functions.Monad;
import edu.kit.imi.knoholem.cu.rules.parser.RuleParserConfiguration;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

/**
 * @author <a href="mailto:kiril.tonev@kit.edu">Kiril Tonev</a>
 */
public class EvaluateRules {
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

        BuildingState buildingState = new BuildingState(sensorDatabase);
        FailingPredicatesRecorder ruleParser = new FailingPredicatesRecorder(ruleParserConfiguration, sensorsInDatabase, buildingState);
        try {
            Collect collector = new ConvertRules.MultipleRuleFileParser(files, ruleParser).execute();
            Monad<SensitivityAnalysisRule> rules = collector.getRules();
            Map<String, List<Predicate>> failedPredicates = ruleParser.getFailedPredicates();
            Map<String, Integer> warnings = ruleParser.getWarnings();
            for (String sensorName : warnings.keySet()) {
                System.out.println(sensorName + ", " + warnings.get(sensorName) + ", " + rules.size());
            }
            for (Predicate predicate : failedPredicates.get("17_8_GRFMET_17")) {
                System.out.println(predicate);
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

    protected static class FailingPredicatesRecorder extends AlignRules.AlignedRuleParser {

        private final Map<String, Double> sensorValues;
        private final Map<String, Integer> warnings;
        private final Map<String, List<Predicate>> failedPredicates;

        private FailingPredicatesRecorder(RuleParserConfiguration configuration, Collection<String> sensorsInDatabase, BuildingState buildingState) {
            super(configuration, sensorsInDatabase);
            try {
                this.sensorValues = buildingState.fetchSensorValues();
            } catch (Exception e) {
                throw new RuntimeException("Could not calculate building state. Error: " + e.getMessage());
            }
            this.warnings = new HashMap<String, Integer>();
            this.failedPredicates = new HashMap<String, List<Predicate>>();
        }

        public Map<String, Integer> getWarnings() {
            return warnings;
        }

        @Override
        protected List<Predicate> collectPredicates(List<String> ruleBodyTokens) {
            List<Predicate> predicates = super.collectPredicates(ruleBodyTokens);
            for (PredicateMapEntry mapEntry : new PredicateMap(predicates).byLeftOperand()) {

                for (Predicate predicate : mapEntry.getPredicates()) {
                    if (!evaluatePredicate(predicate)) {
                        System.err.println("False evaluation: " + predicate.toString());

                        recordFail(predicate);
                        incrementWarning(predicate.getLeftOperand().asString());
                    }

                }
            }
            return predicates;
        }

        public Map<String, List<Predicate>> getFailedPredicates() {
            return failedPredicates;
        }

        private boolean recordFail(Predicate predicate) {
            String sensorName = predicate.getLeftOperand().asString();
            List<Predicate> recordedPredicates;
            if (failedPredicates.containsKey(sensorName)) {
                recordedPredicates = failedPredicates.get(sensorName);
            } else {
                recordedPredicates = new LinkedList<Predicate>();
                failedPredicates.put(sensorName, recordedPredicates);
            }
            return recordedPredicates.add(predicate);
        }

        protected void incrementWarning(String sensorName) {
            if (warnings.containsKey(sensorName)) {
                warnings.put(sensorName, warnings.get(sensorName) + 1);
            } else {
                warnings.put(sensorName, 1);
            }
        }

        protected boolean evaluatePredicate(Predicate predicate) {
            Double sensorValue = sensorValues.get(predicate.getLeftOperand().asString());
            Double rightOperand = predicate.getRightOperand().asDouble();
            switch (predicate.getOperator()) {
                case GREATER_THAN_OR_EQUAL:
                    return sensorValue >= rightOperand;
                case LESS_THAN_OR_EQUAL:
                    return sensorValue <= rightOperand;
                default:
                    throw new RuntimeException("Operator not implementeed: " + predicate.getOperator().literal);
            }
        }
    }

    protected static class EvaluatingParser extends AlignRules.AlignedRuleParser {

        private int firedRules;

        private final Map<String, Double> sensorValues;
        private final Map<String, Integer> warnings;
        private final Map<String, List<Predicate>> failedPredicates;

        private EvaluatingParser(RuleParserConfiguration configuration, Collection<String> sensorsInDatabase, BuildingState buildingState) {
            super(configuration, sensorsInDatabase);
            try {
                this.sensorValues = buildingState.fetchSensorValues();
            } catch (Exception e) {
                throw new RuntimeException("Could not calculate building state. Error: " + e.getMessage());
            }
            this.warnings = new HashMap<String, Integer>();
            this.failedPredicates = new HashMap<String, List<Predicate>>();
        }

        @Override
        protected List<Predicate> collectPredicates(List<String> ruleBodyTokens) {
            List<Predicate> predicates = super.collectPredicates(ruleBodyTokens);

            boolean evaluation = true;
            for (Predicate predicate : predicates) {
                evaluation = evaluation && evaluatePredicate(predicate);
            }
            if (evaluation) {
                firedRules = firedRules + 1;
            }

            return predicates;
        }

        public Map<String, List<Predicate>> getFailedPredicates() {
            return failedPredicates;
        }

        protected boolean evaluatePredicate(Predicate predicate) {
            Double sensorValue = sensorValues.get(predicate.getLeftOperand().asString());
            Double rightOperand = predicate.getRightOperand().asDouble();
            switch (predicate.getOperator()) {
                case GREATER_THAN_OR_EQUAL:
                    return sensorValue >= rightOperand;
                case LESS_THAN_OR_EQUAL:
                    return sensorValue <= rightOperand;
                default:
                    throw new RuntimeException("Operator not implementeed: " + predicate.getOperator().literal);
            }
        }
    }

}

package edu.kit.imi.knoholem.cu.rules;

import edu.kit.imi.knoholem.cu.rules.atoms.Predicate;
import edu.kit.imi.knoholem.cu.rules.atoms.SensitivityAnalysisRule;
import edu.kit.imi.knoholem.cu.rules.functions.Collect;
import edu.kit.imi.knoholem.cu.rules.functions.Monad;
import edu.kit.imi.knoholem.cu.rules.parser.FalsePredicatesRecorder;
import edu.kit.imi.knoholem.cu.rules.parser.RuleParserConfiguration;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Evaluates individual rule predicates against the current building state reporting the failing ones.
 *
 * @author <a href="mailto:kiril.tonev@kit.edu">Kiril Tonev</a>
 */
public class FalsePredicates {
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
        SensorsDatabase sensorDatabase = new SensorsDatabase(url, user, password,
                sensorsHistoryTable, setpointsHistoryTable,
                sensorsTable, setpointsTable,
                sensorColumn, setpointColumn);

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
            sensorsInDatabase = sensorDatabase.fetchAllCurrentNames(connection);
        } catch (SQLException e) {
            System.err.println("Could not fetch rules.");
            e.printStackTrace();
            System.exit(1);
        }

        System.err.println("Fetched " + sensorsInDatabase.size() + " sensors.");

        BuildingState buildingState = new BuildingState(sensorDatabase);
        FalsePredicatesRecorder ruleParser = null;
        try {
            ruleParser = new FalsePredicatesRecorder(ruleParserConfiguration, buildingState.fetchSensorValues());
        } catch (Exception e) {
            System.err.println("Cannot initialize parser. Reason: " + e.getMessage());
            System.exit(1);
        }

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

}

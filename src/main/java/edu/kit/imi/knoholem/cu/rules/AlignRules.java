package edu.kit.imi.knoholem.cu.rules;

import edu.kit.imi.knoholem.cu.rules.functions.Collect;
import edu.kit.imi.knoholem.cu.rules.functions.Monad;
import edu.kit.imi.knoholem.cu.rules.parser.AlignedRuleParser;
import edu.kit.imi.knoholem.cu.rules.parser.RuleParserConfiguration;
import edu.kit.imi.knoholem.cu.rules.parser.RulePrinter;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * @author <a href="mailto:kiril.tonev@kit.edu">Kiril Tonev</a>
 */
public class AlignRules {

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
        SensorsDatabase alignRules = new SensorsDatabase(url, user, password,
                sensorsHistoryTable, setpointsHistoryTable,
                sensorsTable, setpointsTable,
                sensorColumn, setpointColumn);

        Connection connection = null;
        try {
            connection = alignRules.initializeConnection();
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
            sensorsInDatabase = alignRules.fetchAllCurrentNames(connection);
        } catch (SQLException e) {
            System.err.println("Could not fetch rules. " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }

        System.err.println("Fetched " + sensorsInDatabase.size() + " sensors.");
        AlignedRuleParser ruleParser = new AlignedRuleParser(ruleParserConfiguration, sensorsInDatabase);
        try {
            Collect collector = new ConvertRules.MultipleRuleFileParser(files, ruleParser).execute();
            Monad<String> alignedRules = collector.getRules().map(new RulePrinter(ruleParserConfiguration, new DecimalFormat("0.00")));
            for (String name : alignedRules) {
                System.out.println(name);
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

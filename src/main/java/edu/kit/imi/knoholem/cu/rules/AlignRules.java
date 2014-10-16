package edu.kit.imi.knoholem.cu.rules;

import edu.kit.imi.knoholem.cu.rules.atoms.Predicate;
import edu.kit.imi.knoholem.cu.rules.functions.Collect;
import edu.kit.imi.knoholem.cu.rules.functions.Monad;
import edu.kit.imi.knoholem.cu.rules.parser.RuleParser;
import edu.kit.imi.knoholem.cu.rules.parser.RuleParserConfiguration;
import edu.kit.imi.knoholem.cu.rules.parser.RulePrinter;

import java.io.IOException;
import java.sql.*;
import java.text.DecimalFormat;
import java.util.*;

/**
 * @author <a href="mailto:kiril.tonev@kit.edu">Kiril Tonev</a>
 */
public class AlignRules {

    protected final String url;
    protected final String user;
    protected final String password;

    protected final String sensorsTable;
    protected final String setpointsTable;

    protected final String sensorColumn;
    protected final String setpointColumn;

    public static void main(String[] args) {
        String url = args[0];
        String user = args[1];
        String password = args[2];

        String sensorsTable = args[3];
        String setpointsTable = args[4];

        String sensorColumn = args[5];
        String setpointColumn = args[6];

        List<String> files = Arrays.asList(args).subList(7, args.length);
        RuleParserConfiguration ruleParserConfiguration = RuleParserConfiguration.getDefaultConfiguration();
        AlignRules alignRules = new AlignRules(url, user, password, sensorsTable, setpointsTable, sensorColumn, setpointColumn);

        Connection connection = null;
        try {
            connection = alignRules.initializeConnection();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (connection == null) {
                System.exit(1);
            }
        }

        Set<String> sensorsInDatabase = null;
        try {
            sensorsInDatabase = alignRules.fetchAllNames(connection);
        } catch (SQLException e) {
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

    public AlignRules(String url, String user, String password, String sensorsTable, String setpointsTable, String sensorColumn, String setpointColumn) {
        this.url = url;
        this.user = user;
        this.password = password;

        this.sensorsTable = sensorsTable;
        this.setpointsTable = setpointsTable;

        this.sensorColumn = sensorColumn;
        this.setpointColumn = setpointColumn;
    }

    protected Connection initializeConnection() throws ClassNotFoundException, SQLException {
        return initializeConnection("com.mysql.jdbc.Driver", url, user, password);
    }

    protected Connection initializeConnection(String driverClass, String url, String user, String password) throws ClassNotFoundException, SQLException {
        Class.forName(driverClass);

        return DriverManager.getConnection(url, user, password);
    }

    protected Set<String> fetchAllNames(Connection connection) throws SQLException {
        Set<String> allNames = new HashSet<String>();

        allNames.addAll(fetchSensorNames(connection));
        allNames.addAll(fetchSetpointNames(connection));

        return allNames;
    }

    protected Set<String> fetchSetpointNames(Connection connection) throws SQLException {
        return collectNames(connection, setpointsQuery(setpointColumn, setpointsTable));
    }

    protected Set<String> fetchSensorNames(Connection connection) throws SQLException {
        return collectNames(connection, sensorsQuery(sensorColumn, setpointsTable));
    }

    protected String sensorsQuery(String sensorColumn, String sensorsTable) {
        return "SELECT DISTINCT(`" + sensorColumn + "`) FROM `" + sensorsTable + "`";
    }

    protected String setpointsQuery(String setpointColumn, String setpointsTable) {
        return "SELECT DISTINCT(`" + setpointColumn + "`) FROM `" + setpointsTable + "`";
    }

    protected Set<String> collectNames(Connection connection, String query) throws SQLException {
        Set<String> sensorNames = new HashSet<String>();
        PreparedStatement statement = connection.prepareStatement(query);
        for (ResultSet resultSet = statement.executeQuery(); resultSet.next(); ) {
            sensorNames.add(resultSet.getString(1));
        }
        return sensorNames;
    }

    protected static class AlignedRuleParser extends RuleParser {

        private final Collection<String> sensorsInDatabase;

        private AlignedRuleParser(RuleParserConfiguration configuration, Collection<String> sensorsInDatabase) {
            super(configuration);
            this.sensorsInDatabase = sensorsInDatabase;
        }

        @Override
        protected List<Predicate> collectPredicates(List<String> ruleBodyTokens) {
            List<Predicate> predicates = new LinkedList<Predicate>();
            for (String token : ruleBodyTokens) {
                Predicate predicate = parsePredicate(token);
                if (sensorsInDatabase.contains(predicate.getLeftOperand().asString())) {
                    predicates.add(predicate);
                }
            }
            return predicates;
        }
    }
}

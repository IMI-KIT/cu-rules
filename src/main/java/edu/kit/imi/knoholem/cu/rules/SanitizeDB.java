package edu.kit.imi.knoholem.cu.rules;

import edu.kit.imi.knoholem.cu.rules.dataquality.Criterion;
import edu.kit.imi.knoholem.cu.rules.dataquality.DataQualityEvaluationContext;
import edu.kit.imi.knoholem.cu.rules.dataquality.DeclaredIndividual;
import edu.kit.imi.knoholem.cu.rules.dataquality.Templates;
import edu.kit.imi.knoholem.cu.rules.functions.Function;
import edu.kit.imi.knoholem.cu.rules.functions.Monad;
import edu.kit.imi.knoholem.cu.rules.functions.Monads;
import edu.kit.imi.knoholem.cu.rules.ontology.OntologyContext;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import java.io.File;
import java.sql.*;
import java.util.HashSet;
import java.util.Set;

/**
 * @author <a href="mailto:kiril.tonev@kit.edu">Kiril Tonev</a>
 */
public class SanitizeDB {

    private final String url;
    private final String user;
    private final String password;
    private final String tableName;
    private final String columnName;

    public static void main(String[] args) throws ClassNotFoundException, SQLException, OWLOntologyCreationException {
        String ontology = args[0];
        String url = args[1];
        String user = args[2];
        String password = args[3];
        String tableName = args[4];
        String columnName = args[5];

        Templates templates = Templates.loadTemplates();
        OntologyContext ontologyContext = OntologyContext.load(new File(ontology));
        DataQualityEvaluationContext evaluationContext = new DataQualityEvaluationContext(ontologyContext);

        Class.forName("com.mysql.jdbc.Driver");

        SanitizeDB db = new SanitizeDB(url, user, password, tableName, columnName);
        Connection connection = db.obtainConnection();

        Monad<String> sensorNames = db.sensorNames(connection);
        System.err.println("Collected " + sensorNames.size() + " sensor names.");

        Monad<String> invalidNames = sensorNames.reject(new ValidIndividualName());
        Monad<String> validNames = sensorNames.select(new ValidIndividualName());
        Monad<Criterion> criteria = db.collectCriteria(validNames, templates);

        Monad<Criterion> violations = criteria.reject(evaluationContext);

        System.out.println("Invalid sensor names:");
        for (String invalidName : invalidNames) {
            System.out.println(invalidName);
        }

        System.out.println("Criteria violations:");
        for (Criterion criterion : violations) {
            System.out.println(criterion);
        }
    }

    public SanitizeDB(String url, String user, String password, String tableName, String columnName) {
        this.url = url;
        this.user = user;
        this.password = password;
        this.tableName = tableName;
        this.columnName = columnName;
    }

    private Connection obtainConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }

    private Monad<Criterion> collectCriteria(Monad<String> sensorNames, Templates templates) {
        Set<Criterion> criteria = new HashSet<Criterion>();

        for (String sensorName : sensorNames) {
            criteria.add(new DeclaredIndividual(sensorName, templates));
        }

        return Monads.list(criteria);
    }

    private Monad<String> sensorNames(Connection connection) throws SQLException {
        Set<String> sensorNames = new HashSet<String>();

        for (ResultSet resultSet = sensorNamesQuery(connection).executeQuery(); resultSet.next(); ) {
            sensorNames.add(resultSet.getString(1));
        }

        return Monads.list(sensorNames);
    }

    private PreparedStatement sensorNamesQuery(Connection connection) throws SQLException {
        return connection.prepareStatement(selectSensorsQuery(columnName, tableName));
    }

    private String selectSensorsQuery(String columnName, String tableName) {
        return "SELECT DISTINCT(`" + columnName + "`) FROM `" + tableName + "`";
    }

    private static class ValidIndividualName implements Function<String, Boolean> {
        @Override
        public Boolean apply(String input) {
            boolean containsWhitespace = false;
            for (int i = 0; i < input.length() && !containsWhitespace; i++) {
                if (Character.isWhitespace(input.charAt(i))) {
                    containsWhitespace = true;
                }
            }
            return !containsWhitespace;
        }
    }
}

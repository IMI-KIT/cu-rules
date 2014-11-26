package edu.kit.imi.knoholem.cu.rules;

import edu.kit.imi.knoholem.cu.rules.ontology.OntologyContext;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

/**
 * Checks whether all sensors in the building database are populated in the ontology.
 *
 * @author <a href="mailto:kiril.tonev@kit.edu">Kiril Tonev</a>
 */
public class DatabasePopulation {

    private final SensorsDatabase sensorsDatabase;
    private final OntologyContext context;

    public DatabasePopulation(SensorsDatabase sensorsDatabase, OntologyContext context) {
        this.sensorsDatabase = sensorsDatabase;
        this.context = context;
    }

    public Set<String> fetchMissingEntities() {
        Connection connection = initializeConnection();

        Set<String> result = new HashSet<String>();
        try {
            result.addAll(fetchMissingSensors(connection));
            result.addAll(fetchMissingSetpoints(connection));
        } catch (SQLException e) {
            throw new RuntimeException("Could not fetch entities from database.", e);
        }

        return result;
    }

    public Set<String> fetchMissingSensors() {
        Connection connection = initializeConnection();

        try {
            return fetchMissingSensors(connection);
        } catch (SQLException e) {
            throw new RuntimeException("Could not fetch missing sensors.", e);
        } finally {
            closeConnection(connection);
        }
    }

    public Set<String> fetchMissingSensors(Connection connection) throws SQLException {
        Set<String> result = new HashSet<String>();
        for (String setpointId : sensorsDatabase.fetchCurrentSensorNames(connection)) {
            if (!context.containsIndividual(setpointId)) {
                result.add(setpointId);
            }
        }
        return result;
    }

    public Set<String> fetchMissingSetpoints() {
        Connection connection = initializeConnection();

        try {
            return fetchMissingSetpoints(connection);
        } catch (SQLException e) {
            throw new RuntimeException("Could not fetch missing setpoints.", e);
        } finally {
            closeConnection(connection);
        }
    }

    public Set<String> fetchMissingSetpoints(Connection connection) throws SQLException {
        Set<String> result = new HashSet<String>();
        for (String setpointId : sensorsDatabase.fetchCurrentSetpointNames(connection)) {
            if (!context.containsIndividual(setpointId)) {
                result.add(setpointId);
            }
        }
        return result;
    }

    protected Connection initializeConnection() {
        try {
            return sensorsDatabase.initializeConnection();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Could not load the database driver.", e);
        } catch (SQLException e) {
            throw new RuntimeException("Could not initialize a database connection.", e);
        }
    }

    protected static void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                throw new RuntimeException("Could not close connection.", e);
            }
        }
    }

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

        SensorsDatabase sensorsDatabase = new SensorsDatabase(url, user, password,
                sensorsHistoryTable, setpointsHistoryTable,
                sensorsTable, setpointsTable,
                sensorColumn, setpointColumn);

        OntologyContext ontologyContext = null;
        try {
            ontologyContext = OntologyContext.load(new File(args[9]));
        } catch (OWLOntologyCreationException e) {
            System.err.println("Could not load ontology in `" + args[9] + "'. " + e.getMessage());
            System.exit(1);
        }

        Connection connection = null;
        try {
            connection = sensorsDatabase.initializeConnection();
        } catch (ClassNotFoundException e) {
            System.err.println("Could not load the database driver. " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("Could not initialize a database connection. " + e.getMessage());
        } finally {
            if (connection == null) {
                System.exit(1);
            }
        }

        DatabasePopulation databasePopulation = new DatabasePopulation(sensorsDatabase, ontologyContext);
        try {
            System.out.println("Missing sensors:");
            for (String sensorName : databasePopulation.fetchMissingSensors(connection)) {
                System.out.println(sensorName);
            }

            System.out.println("\nMissing setpoints:");
            for (String setpointId : databasePopulation.fetchMissingSetpoints(connection)) {
                System.out.println(setpointId);
            }
        } catch (SQLException e) {
            System.err.println("Could not query sensor names. " + e.getMessage());
        } finally {
            closeConnection(connection);
        }
    }
}

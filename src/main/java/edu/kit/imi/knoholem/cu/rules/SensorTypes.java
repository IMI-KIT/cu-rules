package edu.kit.imi.knoholem.cu.rules;

import edu.kit.imi.knoholem.cu.rules.ontology.OntologyContext;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Set;

/**
 * @author <a href="mailto:kiril.tonev@kit.edu">Kiril Tonev</a>
 */
public class SensorTypes {

    public static void main(String[] args) {
        String ontology = args[0];
        String url = args[1];
        String user = args[2];
        String password = args[3];

        String sensorsTable = args[4];
        String setpointsTable = args[5];

        String sensorColumn = args[6];
        String setpointColumn = args[7];

        OntologyContext ontologyContext = null;
        try {
            ontologyContext = OntologyContext.load(new File(ontology));
        } catch (OWLOntologyCreationException e) {
            System.exit(1);
        }

        SensorsDatabase sensorsDatabase = new SensorsDatabase(url, user, password, sensorsTable, setpointsTable, sensorColumn, setpointColumn);
        Connection connection = null;
        try {
            connection = sensorsDatabase.initializeConnection();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (connection == null) {
                System.exit(1);
            }
        }

        Set<String> sensors = null;
        try {
            sensors = sensorsDatabase.fetchAllNames(connection);
        } catch (SQLException e) {
            e.printStackTrace();
            System.exit(1);
        }

        for (String sensorName : sensors) {
            System.out.println(sensorName + ";" + ontologyContext.getRepresentativeClass(sensorName));
        }
    }
}

package edu.kit.imi.knoholem.cu.rules;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:kiril.tonev@kit.edu">Kiril Tonev</a>
 */
public class BuildingState {
    private final SensorsDatabase sensorsDatabase;

    public BuildingState(SensorsDatabase sensorsDatabase) {
        this.sensorsDatabase = sensorsDatabase;
    }

    public Map<String, Double> fetchSensorValues() throws SQLException, ClassNotFoundException {
        Map<String, Double> values = new HashMap<String, Double>();
        Connection connection = sensorsDatabase.initializeConnection();

        for (String sensorName : sensorsDatabase.fetchSensorNames(connection)) {
            values.put(sensorName, sensorsDatabase.fetchCurrentSensorValue(connection, sensorName));
        }

        return values;
    }

}

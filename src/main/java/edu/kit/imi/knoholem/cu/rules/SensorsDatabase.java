package edu.kit.imi.knoholem.cu.rules;

import java.sql.*;
import java.util.HashSet;
import java.util.Set;

/**
 * @author <a href="mailto:kiril.tonev@kit.edu">Kiril Tonev</a>
 */
public class SensorsDatabase {

    protected final String url;
    protected final String user;
    protected final String password;

    protected final String sensorsTable;
    protected final String setpointsTable;

    protected final String sensorColumn;
    protected final String setpointColumn;

    public SensorsDatabase(String url, String user, String password, String sensorsTable, String setpointsTable, String sensorColumn, String setpointColumn) {
        this.url = url;
        this.user = user;
        this.password = password;

        this.sensorsTable = sensorsTable;
        this.setpointsTable = setpointsTable;

        this.sensorColumn = sensorColumn;
        this.setpointColumn = setpointColumn;
    }

    public Connection initializeConnection() throws ClassNotFoundException, SQLException {
        return initializeConnection("com.mysql.jdbc.Driver", url, user, password);
    }

    public Connection initializeConnection(String driverClass, String url, String user, String password) throws ClassNotFoundException, SQLException {
        Class.forName(driverClass);

        return DriverManager.getConnection(url, user, password);
    }

    public Set<String> fetchAllNames(Connection connection) throws SQLException {
        Set<String> allNames = new HashSet<String>();

        allNames.addAll(fetchSensorNames(connection));
        allNames.addAll(fetchSetpointNames(connection));

        return allNames;
    }

    public Set<String> fetchSetpointNames(Connection connection) throws SQLException {
        return collectNames(connection, setpointsQuery(setpointColumn, setpointsTable));
    }

    public Set<String> fetchSensorNames(Connection connection) throws SQLException {
        return collectNames(connection, sensorsQuery(sensorColumn, setpointsTable));
    }

    public String sensorsQuery(String sensorColumn, String sensorsTable) {
        return "SELECT DISTINCT(`" + sensorColumn + "`) FROM `" + sensorsTable + "`";
    }

    public String setpointsQuery(String setpointColumn, String setpointsTable) {
        return "SELECT DISTINCT(`" + setpointColumn + "`) FROM `" + setpointsTable + "`";
    }

    public Set<String> collectNames(Connection connection, String query) throws SQLException {
        Set<String> sensorNames = new HashSet<String>();
        PreparedStatement statement = connection.prepareStatement(query);
        for (ResultSet resultSet = statement.executeQuery(); resultSet.next(); ) {
            sensorNames.add(resultSet.getString(1));
        }
        return sensorNames;
    }

}

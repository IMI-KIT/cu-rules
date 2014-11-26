package edu.kit.imi.knoholem.cu.rules;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:kiril.tonev@kit.edu">Kiril Tonev</a>
 */
public class History {

    private SensorsDatabase sensorsDatabase;
    private Connection connection;

    public static void main(String[] args) throws IOException, SQLException {
        String url = args[0];
        String user = args[1];
        String password = args[2];

        String sensorsHistoryTable = args[3];
        String setpointsHistoryTable = args[4];

        String sensorsTable = args[5];
        String setpointsTable = args[6];

        String sensorColumn = args[7];
        String setpointColumn = args[8];

        SensorsDatabase sensorsDatabase = new SensorsDatabase(url, user, password, sensorsHistoryTable, setpointsHistoryTable, sensorsTable, setpointsTable, sensorColumn, setpointColumn);
        new History(sensorsDatabase).writeStatistics(System.out);
    }

    public History(SensorsDatabase sensorsDatabase) {
        this.sensorsDatabase = sensorsDatabase;
        try {
            this.connection = sensorsDatabase.initializeConnection();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void writeStatistics(File file) throws IOException, SQLException {
        FileWriter fileWriter = new FileWriter(file);
        writeStatistics(fileWriter);
    }

    public void writeStatistics(Appendable appendable) throws IOException, SQLException {
        CSVPrinter printer = new CSVPrinter(appendable, CSVFormat.EXCEL);

        printer.printRecord(header());
        System.err.println("Fetching sensors...");
        for (String sensorName : sensorsDatabase.fetchCurrentSensorNames(connection)) {
            System.err.println("Calculating statistics for `" + sensorName + "'");
            printer.printRecord(statisticsRecord(sensorName, sensorStats(sensorName)));
        }

        System.err.println("Fetching setpoints...");
        for (String setpoint : sensorsDatabase.fetchCurrentSetpointNames(connection)) {
            System.err.println("Calculating statistics for `" + setpoint + "'");
            printer.printRecord(statisticsRecord(setpoint, setpointStats(setpoint)));
        }

        printer.flush();
    }

    public DescriptiveStatistics sensorStats(String sensorName) throws SQLException {
        return fetchStats(sensorQuery(sensorName));
    }

    public DescriptiveStatistics setpointStats(String setpointName) throws SQLException {
        return fetchStats(setpointQuery(setpointName));
    }

    public DescriptiveStatistics fetchStats(String query) throws SQLException {
        DescriptiveStatistics statistics = new DescriptiveStatistics();

        ResultSet resultSet = connection.prepareStatement(query).executeQuery();
        for (Double value : sensorValues(resultSet)) {
            statistics.addValue(value);
        }

        return statistics;
    }

    Iterable<?> header() {
        List<Object> record = new ArrayList<Object>();
        record.add("Sensor name");
        record.add("Mean");
        record.add("Variance");
        record.add("Min");
        record.add("Max");
        record.add("0.25-Percentile");
        record.add("Median");
        record.add("0.75-Percentile");
        record.add("N");
        return record;
    }

    Iterable<?> statisticsRecord(String sensorName, DescriptiveStatistics sensorData) {
        List<Object> stats = new ArrayList<Object>();
        stats.add(sensorName);
        stats.add(sensorData.getMean());
        stats.add(sensorData.getVariance());
        stats.add(sensorData.getMin());
        stats.add(sensorData.getMax());
        stats.add(sensorData.getPercentile(25));
        stats.add(sensorData.getPercentile(50));
        stats.add(sensorData.getPercentile(75));
        stats.add(sensorData.getN());
        return stats;
    }

    String sensorQuery(String sensorName) {
        return "SELECT `" + sensorsDatabase.getValueColumn()
                + "` FROM `" + sensorsDatabase.getSensorsHistoryTable()
                + "` WHERE `" + sensorsDatabase.getSensorColumn() + "` = '" + sensorName + "'";
    }

    String setpointQuery(String setpointName) {
        return "SELECT `" + sensorsDatabase.getValueColumn()
                + "` FROM `" + sensorsDatabase.getSetpointsHistoryTable()
                + "` WHERE `" + sensorsDatabase.getSetpointColumn() + "` = '" + setpointName + "'";
    }

    List<Double> sensorValues(ResultSet resultSet) throws SQLException {
        List<Double> values = new ArrayList<Double>();
        while (resultSet.next()) {
            try {
                values.add(resultSet.getDouble(1));
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        }
        return values;
    }
}

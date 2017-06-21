package io.github.siketyan.monitor.task;

import io.github.siketyan.monitor.TempMonitor;
import io.github.siketyan.monitor.object.DataSet;
import io.github.siketyan.monitor.util.ISensor;
import io.github.siketyan.monitor.util.Logger;
import io.github.siketyan.monitor.util.SQLManager;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;

public class CronTask implements Runnable {
    private static final String NOW_INSERT = "INSERT INTO `records` VALUES(NULL, ?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String PHOUR_SELECT
        = "SELECT AVG(`temp`) AS temp, AVG(`hum`) AS hum, AVG(`pres`) AS pres"
              +" FROM `records` WHERE `year` = ? AND `month` = ? AND `day` = ? AND `hour` = ?";
    private static final String PHOUR_INSERT = "INSERT INTO `hours` VALUES(NULL, ?, ?, ?, ?, ?, ?, ?)";
    private static final String PDAY_SELECT
        = "SELECT AVG(`temp`) AS temp, AVG(`hum`) AS hum, AVG(`pres`) AS pres"
              +" FROM `hours` WHERE `year` = ? AND `month` = ? AND `day` = ?";
    private static final String PDAY_INSERT = "INSERT INTO `days` VALUES(NULL, ?, ?, ?, ?, ?, ?)";
    private static final String PMONTH_SELECT
        = "SELECT AVG(`temp`) AS temp, AVG(`hum`) AS hum, AVG(`pres`) AS pres"
              +" FROM `days` WHERE `year` = ? AND `month` = ?";
    private static final String PMONTH_INSERT = "INSERT INTO `months` VALUES(NULL, ?, ?, ?, ?, ?)";
    private static final String PYEAR_SELECT
        = "SELECT AVG(`temp`) AS temp, AVG(`hum`) AS hum, AVG(`pres`) AS pres FROM `days` WHERE `year` = ?";
    private static final String PYEAR_INSERT = "INSERT INTO `months` VALUES(NULL, ?, ?, ?, ?, ?)";
    
    private ISensor sensor;
    private SQLManager sql;
    
    public CronTask() {
        this.sensor = TempMonitor.getSensor();
        this.sql = TempMonitor.getSQL();
    }
    
    @Override
    public void run() {
        try {
            /*
                Now
             */
            
            DataSet data = sensor.getData();
            Date now = new Date();
            Calendar cal = Calendar.getInstance();
            cal.setTime(now);
    
            try (PreparedStatement stmt = sql.getPreparedStatement(NOW_INSERT)) {
                stmt.setInt(1, cal.get(Calendar.YEAR));
                stmt.setInt(2, cal.get(Calendar.MONTH) + 1);
                stmt.setInt(3, cal.get(Calendar.DATE));
                stmt.setInt(4, cal.get(Calendar.HOUR_OF_DAY));
                stmt.setInt(5, cal.get(Calendar.MINUTE));
                stmt.setDouble(6, data.getTemperature());
                stmt.setDouble(7, data.getHumidity());
                stmt.setDouble(8, data.getPressure());
                stmt.executeUpdate();
            }
    
            Logger.success(
                "Inserted data to `records`: "
                    + data.getTemperature() + ", "
                    + data.getHumidity() + ", "
                    + data.getPressure()
            );

            cal.setTime(now);
            if (cal.get(Calendar.MINUTE) != 0) return;

            /*
                Previous Hour
             */
            
            double hTemp, hHum, hPres;
            cal.add(Calendar.HOUR_OF_DAY, -1);
    
            try (PreparedStatement stmt = sql.getPreparedStatement(PHOUR_SELECT)) {
                stmt.setInt(1, cal.get(Calendar.YEAR));
                stmt.setInt(2, cal.get(Calendar.MONTH) + 1);
                stmt.setInt(3, cal.get(Calendar.DATE));
                stmt.setInt(4, cal.get(Calendar.HOUR_OF_DAY));
        
                try (ResultSet rs = stmt.executeQuery()) {
                    rs.next();
                    hTemp = rs.getDouble("temp");
                    hHum = rs.getDouble("hum");
                    hPres = rs.getDouble("pres");
                }
            }
            
            try (PreparedStatement stmt = sql.getPreparedStatement(PHOUR_INSERT)) {
                stmt.setInt(1, cal.get(Calendar.YEAR));
                stmt.setInt(2, cal.get(Calendar.MONTH) + 1);
                stmt.setInt(3, cal.get(Calendar.DATE));
                stmt.setInt(4, cal.get(Calendar.HOUR_OF_DAY));
                stmt.setDouble(5, hTemp);
                stmt.setDouble(6, hHum);
                stmt.setDouble(7, hPres);
                stmt.executeUpdate();
            }
    
            Logger.success(
                "Inserted data to `hours`: "
                    + hTemp + ", " + hHum + ", " + hPres
            );
            
            /*
                Previous Day
             */
    
            cal.setTime(now);
            if (cal.get(Calendar.HOUR_OF_DAY) != 0) return;
            double dTemp, dHum, dPres;
            cal.add(Calendar.DATE, -1);
    
            try (PreparedStatement stmt = sql.getPreparedStatement(PDAY_SELECT)) {
                stmt.setInt(1, cal.get(Calendar.YEAR));
                stmt.setInt(2, cal.get(Calendar.MONTH) + 1);
                stmt.setInt(3, cal.get(Calendar.DATE));
        
                try (ResultSet rs = stmt.executeQuery()) {
                    rs.next();
                    dTemp = rs.getDouble("temp");
                    dHum = rs.getDouble("hum");
                    dPres = rs.getDouble("pres");
                }
            }
    
            try (PreparedStatement stmt = sql.getPreparedStatement(PDAY_INSERT)) {
                stmt.setInt(1, cal.get(Calendar.YEAR));
                stmt.setInt(2, cal.get(Calendar.MONTH) + 1);
                stmt.setInt(3, cal.get(Calendar.DATE));
                stmt.setDouble(4, dTemp);
                stmt.setDouble(5, dHum);
                stmt.setDouble(6, dPres);
                stmt.executeUpdate();
            }
            
            Logger.success(
                "Inserted data to `days`: "
                    + dTemp + ", " + dHum + ", " + dPres
            );
            
            /*
                Previous Month
             */
    
            cal.setTime(now);
            if (cal.get(Calendar.DATE) != 1) return;
            double mTemp, mHum, mPres;
            cal.add(Calendar.MONTH, -1);
    
            try (PreparedStatement stmt = sql.getPreparedStatement(PMONTH_SELECT)) {
                stmt.setInt(1, cal.get(Calendar.YEAR));
                stmt.setInt(2, cal.get(Calendar.MONTH) + 1);
        
                try (ResultSet rs = stmt.executeQuery()) {
                    rs.next();
                    mTemp = rs.getDouble("temp");
                    mHum = rs.getDouble("hum");
                    mPres = rs.getDouble("pres");
                }
            }
    
            try (PreparedStatement stmt = sql.getPreparedStatement(PMONTH_INSERT)) {
                stmt.setInt(1, cal.get(Calendar.YEAR));
                stmt.setInt(2, cal.get(Calendar.MONTH) + 1);
                stmt.setDouble(3, mTemp);
                stmt.setDouble(4, mHum);
                stmt.setDouble(5, mPres);
                stmt.executeUpdate();
            }
            
            Logger.success(
                "Inserted data to `hours`: "
                    + mTemp + ", " + mHum + ", " + mPres
            );
            
            /*
                Previous Year
             */
    
            cal.setTime(now);
            if (cal.get(Calendar.MONTH) != 0) return;
            double yTemp, yHum, yPres;
            cal.add(Calendar.YEAR, -1);
    
            try (PreparedStatement stmt = sql.getPreparedStatement(PYEAR_SELECT)) {
                stmt.setInt(1, cal.get(Calendar.YEAR));
        
                try (ResultSet rs = stmt.executeQuery()) {
                    rs.next();
                    yTemp = rs.getDouble("temp");
                    yHum = rs.getDouble("hum");
                    yPres = rs.getDouble("pres");
                }
            }
    
            try (PreparedStatement stmt = sql.getPreparedStatement(PYEAR_INSERT)) {
                stmt.setInt(1, cal.get(Calendar.YEAR));
                stmt.setDouble(3, yTemp);
                stmt.setDouble(4, yHum);
                stmt.setDouble(5, yPres);
                stmt.executeUpdate();
            }
    
            Logger.success(
                "Inserted data to `years`: "
                    + yTemp + ", " + yHum + ", " + yPres
            );
        } catch (SQLException e) {
            Logger.error("Failed SQL operation.");
            e.printStackTrace();
        }
    }
}
package com.server.webduino.core;

import com.server.webduino.servlet.SendPushMessages;

import java.net.URL;
import java.sql.*;
import java.util.*;
import java.util.logging.Logger;

/**
 * Created by Giacomo Span� on 08/11/2015.
 */
public class Sensors {

    private static final Logger LOGGER = Logger.getLogger(Sensors.class.getName());

    private static ArrayList<TemperatureSensor> mTemperatureSensorList = new ArrayList<TemperatureSensor>();

    public Sensors() {
    }

    public TemperatureSensor getSensorFromId(int id) {
        Iterator<TemperatureSensor> iterator = mTemperatureSensorList.iterator();
        while (iterator.hasNext()) {
            TemperatureSensor temperatureSensor = iterator.next();
            if (temperatureSensor.id == id)
                return temperatureSensor;
        }
        return null;
    }

    public ArrayList<TemperatureSensor> getSensorList() {

        return mTemperatureSensorList;
    }

    public static TemperatureSensor get(int index) {

        if (index < 0 || index >= mTemperatureSensorList.size())
            return null;

        return mTemperatureSensorList.get(index);
    }

    public void update() {

        java.util.Date date = new java.util.Date();

        Iterator<TemperatureSensor> iterator = mTemperatureSensorList.iterator();
        while (iterator.hasNext()) {
            TemperatureSensor temperatureSensor = iterator.next();
            LOGGER.info("SENSOR " + temperatureSensor.mURL.toString() + " Call updateStatus");
            String txt = temperatureSensor.updateStatus();

            if (txt == null) {
                LOGGER.severe("temperatureSensor " + temperatureSensor.mURL.toString() + " OFFLINE");
                Core.sendPushNotification(SendPushMessages.notification_error,"errore","temperatureSensor " + temperatureSensor.mURL.toString() + " OFFLINE","0");
            } else {
                LOGGER.info(txt);

                //SensorDataLog dl = new SensorDataLog();
                //dl.writelog(temperatureSensor.id, temperatureSensor.getTemperature(), temperatureSensor.getAvTemperature(), date);
                //writelog(temperatureSensor.mData, date);
                //
            }
        }
    }

    public void read() {

        try {
            // Register JDBC driver
            Class.forName("com.mysql.jdbc.Driver");
            // Open a connection
            Connection conn = DriverManager.getConnection(Core.DB_URL, Core.USER, Core.PASS);
            // Execute SQL query
            Statement stmt = conn.createStatement();
            String sql;
            sql = "SELECT id, url, name FROM sensors";
            ResultSet rs = stmt.executeQuery(sql);

            // Extract data from result set
            while (rs.next()) {

                String str = rs.getString("url");
                URL url = new URL(str);
                int id = rs.getInt("id");
                String name = rs.getString("name");
                TemperatureSensor temperatureSensor = new TemperatureSensor(url, id, name);

                mTemperatureSensorList.add(temperatureSensor);
            }
            // Clean-up environment
            rs.close();
            stmt.close();
            conn.close();

        } catch (SQLException se) {
            //Handle errors for JDBC
            se.printStackTrace();

        } catch (Exception e) {
            //Handle errors for Class.forName
            e.printStackTrace();
        }
    }
}

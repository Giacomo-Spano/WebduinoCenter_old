package com.server.webduino.core;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;
import java.util.logging.Logger;

/**
 * Created by Giacomo Span� on 08/11/2015.
 */
public class Shields {

    interface ShieldsListener {
        void addedActuator(Actuator actuator);
        void addedSensor(SensorBase sensor);
        void addedShield(Shield shield);

        void updatedActuator(Actuator actuator);
        void updatedSensor(SensorBase sensor);
        void updatedShield(Shield shield);
    }

    protected List<ShieldsListener> listeners = new ArrayList<>();

    public void addListener(ShieldsListener toAdd) {
        listeners.add(toAdd);
    }

    public void addTemeratureSensorListener(TemperatureSensor.TemperatureSensorListener toAdd) {

        for (SensorBase sensor : mSensors.getLastSensorData()) {
            //if (sensor.type.equalsIgnoreCase("TemperatureSensor")) {
                try { // aggiungi un listener solo se è un sensore di temperatura
                    TemperatureSensor ts = (TemperatureSensor) sensor;
                    ts.addListener(toAdd);
                } catch (ClassCastException e) {
                    e.printStackTrace();
                }
            //}
        }
    }

    public void updateStatus() {

        List<Shield> shields = getShields();
        for (Shield s : shields) {

            if (s.sensorsIsNotUpdated()) {

                httpClient.Result result = s.callGet("", "/sensorstatus", s.url);
                if (result.response != null) {
                    try {
                        JSONObject json = new JSONObject(result.response);
                        //int shieldid = json.getInt("id");
                        if (json.has("sensors")) {
                            JSONArray jsonArray = json.getJSONArray("sensors");
                            updateSensors(s.id, jsonArray);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            if (s.actuatorsIsNotUpdated()) {

                httpClient.Result result = s.callGet("", "/actuatorstatus", s.url);
                if (result.response != null) {
                    try {
                        JSONObject json = new JSONObject(result.response);
                        //int shieldid = json.getInt("id");
                        if (json.has("actuators")) {
                            JSONArray jsonArray = json.getJSONArray("actuators");
                            updateActuators(s.id, jsonArray);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private static final Logger LOGGER = Logger.getLogger(Shields.class.getName());

    private static List<TemperatureSensor> mTemperatureSensorList = new ArrayList<TemperatureSensor>();
    private static Actuators mActuators;
    public Sensors mSensors;

    public Shields() {

        mActuators = new Actuators();
        mSensors = new Sensors();

        addListener(mActuators);
    }

    public ArrayList<Actuator> getActuators() {
        return mActuators.getActuatorList();
    }

    public List<SensorBase> getLastSensorData() {
        return mSensors.getLastSensorData();
    }

    boolean updateSensors(int shieldid, JSONArray jsonArray) {
        return mSensors.updateSensors(shieldid,jsonArray);
    }

    boolean updateActuators(int shieldid, JSONArray jsonArray) {
        return mActuators.updateActuators(shieldid,jsonArray);
    }

    public Actuator getFromShieldId(int shieldid, String subaddress) {
        return mActuators.getFromShieldId(shieldid, subaddress); }

    public Actuator getFromId(int id) {
        return mActuators.getFromId(id); }

    public List<TemperatureSensor> getSensorList() {
        //return mTemperatureSensorList;
        return mTemperatureSensorList;
    }

    public int register(Shield shield) {

        int lastid = -1;
        String sql;
        Integer affectedRows = 0;
        try {
            // Register JDBC driver
            Class.forName("com.mysql.jdbc.Driver");
            // Open a connection
            Connection conn = DriverManager.getConnection(Core.getDbUrl(), Core.getUser(), Core.getPassword());

            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String date = "NULL";
            date = "'" + df.format(shield.lastUpdate) + "'";
            sql = "INSERT INTO shields (lastupdate, url, macaddress, boardname)" +
                    " VALUES ("
                    + date + ",\""
                    + shield.url + "\",\""
                    + shield.MACAddress + "\",\""
                    + shield.boardName + "\" ) " +
                    "ON DUPLICATE KEY UPDATE lastupdate=" + date
                    + ",url=\"" + shield.url + "\""
                    + ",macaddress=\"" + shield.MACAddress + "\""
                    + ",boardname=\"" + shield.boardName + "\""
                    + ";";

            Statement stmt = conn.createStatement();
            affectedRows = stmt.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                lastid = rs.getInt(1);
            } else {
                lastid = -1;
            }


            for (SensorBase sensor : shield.sensors) {
                sql = "INSERT INTO sensors (shieldid, type, subaddress, name)" +
                        " VALUES ("
                        + "\"" + lastid + "\","
                        + "\"" + sensor.type + "\","
                        + "\"" + sensor.subaddress + "\","
                        + "\"" + sensor.name + "\" ) " +
                        "ON DUPLICATE KEY UPDATE "
                        + "shieldid=\"" + lastid + "\","
                        + "type=\"" + sensor.type + "\","
                        + "subaddress=\"" + sensor.subaddress + "\","
                        + "name=\"" + sensor.name + "\""
                        + ";";
                stmt.executeUpdate(sql);
            }

            for (Actuator actuator : shield.actuators) {
                sql = "INSERT INTO actuators (shieldid, type, subaddress, name)" +
                        " VALUES ("
                        + "\"" + lastid + "\","
                        + "\"" + actuator.type + "\","
                        + "\"" + actuator.subaddress + "\","
                        + "\"" + actuator.name + "\" ) " +
                        "ON DUPLICATE KEY UPDATE "
                        + "shieldid=\"" + lastid + "\","
                        + "type=\"" + actuator.type + "\","
                        + "subaddress=\"" + actuator.subaddress + "\","
                        + "name=\"" + actuator.name + "\""
                        + ";";
                stmt.executeUpdate(sql);
            }

            stmt.close();
            conn.close();
        } catch (SQLException se) {
            //Handle errors for JDBC
            se.printStackTrace();
            LOGGER.severe(se.toString());
            return 0;

        } catch (Exception e) {
            //Handle errors for Class.forName
            e.printStackTrace();
            LOGGER.severe(e.toString());
            return 0;
        }

        if (affectedRows == 2) { // row updated
            shield.id = lastid;
            for(ShieldsListener listener : listeners) {
                listener.updatedShield(shield);
            }
        } else if (affectedRows == 1) { // row inserted
            shield.id = lastid;
            for(ShieldsListener listener : listeners) {
                listener.addedShield(shield);
                for(Actuator actuator: shield.actuators) {
                    listener.addedActuator(actuator);
                }
                for(SensorBase sensor: shield.sensors) {
                    listener.addedSensor(sensor);
                }
            }
        } else { // error

        }

        return lastid;

    }

    public TemperatureSensor getSensorFromId(int id) {
        for (TemperatureSensor ts : mTemperatureSensorList) {
            if (ts.id == id)
                return ts;
        }
        return null;
    }

    public List<TemperatureSensor> ____getLastSensorData() {

        List<TemperatureSensor> list = new ArrayList<>();
        try {
            // Register JDBC driver
            Class.forName("com.mysql.jdbc.Driver");
            // Open a connection
            Connection conn = DriverManager.getConnection(Core.getDbUrl(), Core.getUser(), Core.getPassword());
            // Execute SQL query
            Statement stmt = conn.createStatement();
            String sql;
            //sql = "SELECT id, url, name FROM sensors";
            sql = "SELECT * FROM (\n" +
                    "(SELECT max(date) as maxdate,id, shieldid FROM sensordatalog GROUP BY id) as lastdata\n" +
                    ")\n" +
                    "INNER JOIN shields ON shields.id = lastdata.shieldid\n" +
                    "INNER JOIN sensordatalog ON sensordatalog.shieldid = shields.id AND lastdata.maxdate = sensordatalog.date\n ";
            //;//"WHERE subaddress = 0";
            ResultSet rs = stmt.executeQuery(sql);

            // Extract data from result set
            while (rs.next()) {

                String str = rs.getString("url");
                URL url = null;
                if (str != null) {
                    try {
                        url = new URL(str);
                    } catch (Exception e) {
                        //Handle errors for Class.forName
                        e.printStackTrace();
                    }
                }
                int id = rs.getInt("id");
                int shieldid = rs.getInt("shieldid");
                Timestamp timestamp = rs.getTimestamp("lastupdate");
                Date date = null;
                if (timestamp != null)
                    date = new Date(timestamp.getTime());
                //Date date = rs.getDate("lastupdate");
                String name = rs.getString("name");
                String MACAddress = rs.getString("MACAddress");
                String subaddress = rs.getString("subaddress");
                String boardName = rs.getString("boardName");
                Double temperature = rs.getDouble("temperature");
                Double avtemperature = rs.getDouble("avtemperature");
                Double pressure = rs.getDouble("pressure");
                Double humidity = rs.getDouble("humidity");
                TemperatureSensor temperatureSensor = new TemperatureSensor();

                list.add(temperatureSensor);
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
        return list;
    }

    public List<Shield> getShields() {

        List<Shield> list = new ArrayList<>();
        try {
            // Register JDBC driver
            Class.forName("com.mysql.jdbc.Driver");
            // Open a connection
            Connection conn = DriverManager.getConnection(Core.getDbUrl(), Core.getUser(), Core.getPassword());
            // Execute SQL query
            Statement stmt = conn.createStatement();
            String sql;
            sql = "SELECT * FROM shields";
            ResultSet rs = stmt.executeQuery(sql);

            // Extract data from result set
            while (rs.next()) {

                Shield shield = new Shield();
                shield.id = rs.getInt("id");
                if (rs.getString("macaddress") != null)
                    shield.MACAddress = rs.getString("MACAddress");
                if (rs.getString("boardname") != null)
                    shield.boardName = rs.getString("boardname");
                if (rs.getString("url") != null)
                    shield.url = new URL(rs.getString("url"));
                list.add(shield);
            }
            // Clean-up environment
            rs.close();

            for (Shield shield : list) {
                sql = "SELECT * FROM shields " +
                        "INNER JOIN sensordatalog " +
                        "ON shields.id = sensordatalog.id " +
                        "WHERE shields.id = " + shield.id + " " +
                        "GROUP BY sensordatalog.subaddress";

                ResultSet sensorRs = stmt.executeQuery(sql);
                while (sensorRs.next()) {
                    SensorBase sensor = new SensorBase();
                    if (sensorRs.getString("subaddress") != null)
                        sensor.subaddress = sensorRs.getString("subaddress");
                    if (sensorRs.getString("name") != null)
                        sensor.name = sensorRs.getString("name");

                    shield.sensors.add(sensor);
                }
                sensorRs.close();
            }

            stmt.close();
            conn.close();

        } catch (SQLException se) {
            //Handle errors for JDBC
            se.printStackTrace();

        } catch (Exception e) {
            //Handle errors for Class.forName
            e.printStackTrace();
        }
        return list;
    }


    public static TemperatureSensor get(int index) {

        if (index < 0 || index >= mTemperatureSensorList.size())
            return null;

        return mTemperatureSensorList.get(index);
    }

    /*
    public int update(TemperatureSensor sensor) {

        int lastid;
        String sql;
        try {
            // Register JDBC driver
            Class.forName("com.mysql.jdbc.Driver");
            // Open a connection
            Connection conn = DriverManager.getConnection(Core.getDbUrl(), Core.getUser(), Core.getPassword());

            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String date = "NULL";
            date = "'" + df.format((sensor.getLastUpdate())) + "'";

            sql = "INSERT INTO shields (id, lastupdate, name, url, macaddress, boardname)" +
                    " VALUES (" + "" + sensor.id
                    + "," + date + ",\""
                    + sensor.name + "\",\""
                    + sensor.url + "\",\""
                    + sensor.MACAddress + "\",\""
                    + sensor.boardName + "\" ) " +
                    "ON DUPLICATE KEY UPDATE lastupdate=" + date
                    + ",name=\"" + sensor.name + "\""
                    + ",url=\"" + sensor.url + "\""
                    + ",macaddress=\"" + sensor.MACAddress + "\""
                    + ",boardname=\"" + sensor.boardName + "\""
                    + ",url=\"" + sensor.url + "\""
                    + ";";

            Statement stmt = conn.createStatement();
            Integer numero = stmt.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                lastid = rs.getInt(1);

                for (ShieldsListener listener : listeners) {// TODO da verificare se serve
                    listener.addedSensor(sensor);
                }

            } else {
                lastid = sensor.id;
            }
            stmt.close();
            conn.close();
        } catch (SQLException se) {
            //Handle errors for JDBC
            se.printStackTrace();
            LOGGER.severe(se.toString());
            return 0;

        } catch (Exception e) {
            //Handle errors for Class.forName
            e.printStackTrace();
            LOGGER.severe(e.toString());
            return 0;
        }
        return lastid;
    }
*/
    public List<TemperatureSensor> getTemperatureSensorList() {

        List<TemperatureSensor> list = new ArrayList<>();
        try {
            // Register JDBC driver
            Class.forName("com.mysql.jdbc.Driver");
            // Open a connection
            Connection conn = DriverManager.getConnection(Core.getDbUrl(), Core.getUser(), Core.getPassword());
            // Execute SQL query
            Statement stmt = conn.createStatement();
            String sql;
            //sql = "SELECT id, url, name FROM sensors";
            sql = "SELECT * FROM shields";
            ResultSet rs = stmt.executeQuery(sql);

            list = new ArrayList<>();
            // Extract data from result set
            while (rs.next()) {

                String str = rs.getString("url");
                URL url = new URL(str);
                int id = rs.getInt("id");
                int shieldid = rs.getInt("shieldid");
                Date date = rs.getDate("lastupdate");
                String name = rs.getString("name");
                String MACAddress = rs.getString("MACAddress");
                String boardName = rs.getString("boardName");
                TemperatureSensor temperatureSensor = new TemperatureSensor();

                list.add(temperatureSensor);
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
        return list;
    }

    public URL getURL(int id) {

        URL url = null;
        try {
            // Register JDBC driver
            Class.forName("com.mysql.jdbc.Driver");
            // Open a connection
            Connection conn = DriverManager.getConnection(Core.getDbUrl(), Core.getUser(), Core.getPassword());
            // Execute SQL query
            Statement stmt = conn.createStatement();
            String sql;
            //sql = "SELECT id, url, name FROM sensors";
            sql = "SELECT * FROM shields WHERE id=" + id;
            ResultSet rs = stmt.executeQuery(sql);
            if (rs.next()) {
                String strurl = rs.getString("url");
                strurl = strurl.replace("http://","");
                url = new URL("http://" + strurl);
            }
            // Clean-up environment
            rs.close();
            stmt.close();
            conn.close();
        } catch (SQLException se) {
            //Handle errors for JDBC
            se.printStackTrace();
            return null;

        } catch (Exception e) {
            //Handle errors for Class.forName
            e.printStackTrace();
            return null;
        }
        return url;
    }
}

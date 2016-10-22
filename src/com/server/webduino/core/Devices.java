package com.server.webduino.core;

import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Logger;

/**
 * Created by Giacomo Span� on 08/11/2015.
 */
public class Devices {

    private static final Logger LOGGER = Logger.getLogger(Devices.class.getName());

    private static ArrayList<Device> mDeviceList = new ArrayList<Device>();

    public Devices() {

    }

    public ArrayList<Device> getList() {

        return mDeviceList;
    }

    public Device getFromId(int id) {
        Iterator<Device> iterator = mDeviceList.iterator();
        while (iterator.hasNext()) {
            Device device = iterator.next();
            if (device.id == id)
                return device;
        }
        return null;
    }

    public void read() {

        LOGGER.info(" read devices");

        try {
            // Register JDBC driver
            Class.forName("com.mysql.jdbc.Driver");
            // Open a connection
            Connection conn = DriverManager.getConnection(Core.getDbUrl(), Core.getUser(), Core.getPassword());
            // Execute SQL query
            Statement stmt = conn.createStatement();
            String sql;
            sql = "SELECT id, name, regid, date FROM devices";
            ResultSet rs = stmt.executeQuery(sql);

            // Extract data from result set
            while (rs.next()) {


                Device device = new Device();
                device.id = rs.getInt("id");
                device.regId = rs.getString("regid");
                device.name = rs.getString("name");
                device.date = rs.getDate("date");

                mDeviceList.add(device);
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

    public int insert(Device device) {

        int lastid;
        try {
            // Register JDBC driver
            Class.forName("com.mysql.jdbc.Driver");
            // Open a connection
            Connection conn = DriverManager.getConnection(Core.getDbUrl(), Core.getUser(), Core.getPassword());
            // Execute SQL query
            //Statement stmt = conn.createStatement();

            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            String date = "NULL";
            date = "'" + df.format((device.date)) + "'";



            String sql;

            sql = "INSERT INTO devices (id, regid, date, name)" +
                    " VALUES (" + device.id + ",\"" + device.regId + "\"," + date + ",\"" + device.name + "\") " +
                    "ON DUPLICATE KEY UPDATE id=" + device.id + ", regid=\"" + device.regId + "\", date=" + date + ", name=\"" + device.name + "\"";

            Statement stmt = conn.createStatement();
            Integer numero = stmt.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                lastid = rs.getInt(1);
            } else {
                lastid = device.id;
            }

            stmt.close();

            conn.close();
        } catch (SQLException se) {
            //Handle errors for JDBC
            se.printStackTrace();
            return 0;

        } catch (Exception e) {
            //Handle errors for Class.forName
            e.printStackTrace();
            return 0;
        }

        read(); // reload data
        return lastid;
    }

}

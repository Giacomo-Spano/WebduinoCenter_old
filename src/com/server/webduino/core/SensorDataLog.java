package com.server.webduino.core;

import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class SensorDataLog extends DataLog {
    //public Date date = new Date();
    //public Date time = new Date();
    public Double temperature = 0.0;
    public Double avTemperature = 0.0;

    public void writelog(int shieldid, String subaddress, java.util.Date date, double temperature, double avTemperature) {

        try {

            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String strDate = "NULL";
            strDate = "'" + df.format(date) + "'";

            /*DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String strdate = dateFormat.format(date);
            DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
            String strtime = timeFormat.format(date);*/

            // Register JDBC driver
            Class.forName("com.mysql.jdbc.Driver");
            // Open a connection
            Connection conn = DriverManager.getConnection(Core.getDbUrl(), Core.getUser(), Core.getPassword());
            // Execute SQL query
            Statement stmt = conn.createStatement();
            String sql;
            sql = "INSERT INTO sensordatalog (shieldid, subaddress, date, temperature, avtemperature) VALUES ("+ shieldid + ",'" + subaddress + "',"  + strDate + "," + temperature + "," + avTemperature + ");";
            stmt.executeUpdate(sql);

            // Extract data from result set
            // Clean-up environment
            //rs.close();
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

    @Override
    public ArrayList<DataLog> getDataLog(int id, Date startDate, Date endDate) {

        ArrayList<DataLog> list = new ArrayList<DataLog>();
        try {
            // Register JDBC driver
            Class.forName("com.mysql.jdbc.Driver");
            // Open a connection
            Connection conn = DriverManager.getConnection(Core.getDbUrl(), Core.getUser(), Core.getPassword());
            // Execute SQL query
            Statement stmt = conn.createStatement();

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String start = dateFormat.format(startDate);
            String end = dateFormat.format(endDate);


            String sql;
            //sql = "SELECT id, date, time, temperature, avtemperature FROM sensordatalog WHERE id = " + id +" AND date BETWEEN '2016-02-27 12:00:00' AND '2016-02-28 06:00:00'";
            sql = "SELECT id, shieldid, date, time, temperature, avtemperature FROM sensordatalog WHERE id = " + id +" AND TIMESTAMP(date, time) BETWEEN '" + start + "' AND '" + end + "'";

            //YYYY-MM-DD HH:MI:SS
            ResultSet rs = stmt.executeQuery(sql);


            while (rs.next()) {
                SensorDataLog data = new SensorDataLog();
                data.date = rs.getDate("date");
                data.time = rs.getTime("time");
                data.temperature = rs.getDouble("temperature");
                data.avTemperature = rs.getDouble("avtemperature");
                list.add(data);
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

}

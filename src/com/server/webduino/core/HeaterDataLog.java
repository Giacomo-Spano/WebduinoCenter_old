package com.server.webduino.core;

import java.sql.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class HeaterDataLog extends DataLog {
    //public Date date = new Date();
    //public Date time = new Date();
    public Boolean releStatus = false;
    public String status = "";
    public double localTemperature;
    public double remoteTemperature;
    public double targetTemperature;
    public int activeProgram;
    public int activeTimerange;



    public void writelog(int id, Date date, HeaterActuator heater/*boolean releStatus*/) {

        try {

            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String strdate = dateFormat.format(date);

            // Register JDBC driver
            Class.forName("com.mysql.jdbc.Driver");
            // Open a connection
            Connection conn = DriverManager.getConnection(Core.getDbUrl(), Core.getUser(), Core.getPassword());
            // Execute SQL query
            Statement stmt = conn.createStatement();
            String sql;
            sql = "INSERT INTO heaterdatalog (id, date, relestatus, status, localtemperature, remotetemperature, targettemperature, activeprogram, activetimerange, activesensor) " +
                    "                                   VALUES (" + id + ", '" +
                    strdate + "'," +
                    heater.releStatus + ",'" +
                    heater.getStatus() + "'," +
                    heater.getAvTemperature() + "," +
                    heater.remoteTemperature + "," +
                    heater.targetTemperature + "," +
                    heater.activeProgramID + "," +
                    heater.activeTimeRangeID + "," +
                    heater.activeSensorID + "" +
                    ");";
            stmt.executeUpdate(sql);

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
            //sql = "SELECT id, date, time, relestatus FROM heaterdatalog WHERE id = " + id +" AND TIMESTAMP(date, time) BETWEEN '" + start + "' AND '" + end + "'";
            sql = "SELECT * FROM heaterdatalog WHERE id = " + id + " AND TIMESTAMP(date, time) BETWEEN '" + start + "' AND '" + end + "'" + "ORDER BY TIMESTAMP(date, time) ASC";

            //YYYY-MM-DD HH:MI:SS
            ResultSet rs = stmt.executeQuery(sql);


            while (rs.next()) {
                HeaterDataLog data = new HeaterDataLog();
                data.date = rs.getDate("date");
                data.releStatus = rs.getBoolean("relestatus");
                data.status = rs.getString("status");
                data.localTemperature = rs.getDouble("localtemperature");
                data.remoteTemperature = rs.getDouble("remotetemperature");
                data.targetTemperature = rs.getDouble("targettemperature");
                data.activeProgram = rs.getInt("activeprogram");
                data.activeTimerange = rs.getInt("activetimerange");
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
    @Override
    DataLog getInterpolatedDataLog(Date t, DataLog dataA, DataLog dataB)
    {


        HeaterDataLog dlA = (HeaterDataLog) dataA, dlB = (HeaterDataLog) dataB;
        HeaterDataLog interpolatedDataLog = new HeaterDataLog();

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");

        try {
            interpolatedDataLog.date = dateFormat.parse(dateFormat.format(t));
            interpolatedDataLog.time = timeFormat.parse(timeFormat.format(t));
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }


        long xa = dataA.getDatetime().getTime(), xb = dataB.getDatetime().getTime(), x = t.getTime();
        if (xa == xb) {
            interpolatedDataLog.localTemperature = dlA.localTemperature;
            interpolatedDataLog.remoteTemperature = dlA.remoteTemperature;
            interpolatedDataLog.targetTemperature = dlA.targetTemperature;
            interpolatedDataLog.releStatus = dlA.releStatus;

        } else {
            interpolatedDataLog.localTemperature = dlA.localTemperature * (x - xb) / (xa - xb) - dlB.localTemperature * (x - xa) / (xa - xb);
            interpolatedDataLog.remoteTemperature = dlA.remoteTemperature * (x - xb) / (xa - xb) - dlB.remoteTemperature * (x - xa) / (xa - xb);
            interpolatedDataLog.targetTemperature = dlA.targetTemperature * (x - xb) / (xa - xb) - dlB.targetTemperature * (x - xa) / (xa - xb);
            interpolatedDataLog.releStatus = dlA.releStatus;
        }
        return interpolatedDataLog;
    }
}

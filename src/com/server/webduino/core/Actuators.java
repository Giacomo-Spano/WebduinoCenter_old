package com.server.webduino.core;

import com.server.webduino.servlet.SendPushMessages;

import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Logger;

/**
 * Created by Giacomo Span� on 08/11/2015.
 */
public class Actuators {

    private static final Logger LOGGER = Logger.getLogger(Actuators.class.getName());

    private static ArrayList<Actuator> mActuatorList = new ArrayList<Actuator>();

    public Actuators() {

    }

    public ArrayList<Actuator> getActuatorList() {

        return mActuatorList;
    }

    public /*synchronized*/ Actuator getFromId(int id) {
        Iterator<Actuator> iterator = mActuatorList.iterator();
        while (iterator.hasNext()) {
            Actuator actuator = iterator.next();
            if (actuator.id == id)
                return actuator;
        }
        return null;
    }

   /*void sendCommand() {  // da cambiare

        java.util.Date date = new java.util.Date();

        Iterator<Actuator> iterator = mActuatorList.iterator();
        while (iterator.hasNext()) {
            Actuator actuator = iterator.next();
            String txt = actuator.updateStatus();

            LOGGER.info(txt);
        }
    }*/

    public void read() {

        try {
            // Register JDBC driver
            Class.forName("com.mysql.jdbc.Driver");
            // Open a connection
            Connection conn = DriverManager.getConnection(Core.getDbUrl(), Core.getUser(), Core.getPassword());
            // Execute SQL query
            Statement stmt = conn.createStatement();
            String sql;
            sql = "SELECT id, url, name FROM actuators";
            ResultSet rs = stmt.executeQuery(sql);

            // Extract data from result set
            while (rs.next()) {

                String str = rs.getString("url");
                URL url = new URL(str);
                int id = rs.getInt("id");
                String name = rs.getString("name");
                HeaterActuator actuator = new HeaterActuator(url,id,name);

                mActuatorList.add(actuator);
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
    public /*synchronized*/ void update() {

        //java.util.Date date = new java.util.Date();

        Iterator<Actuator> iterator = mActuatorList.iterator();
        while (iterator.hasNext()) {
            Actuator actuator = iterator.next();
            LOGGER.info("ACTUATOR " + actuator.mURL.toString() + " Call getstatus");
            String txt = actuator.updateStatus();

            LOGGER.info(txt);

            if (txt == null) {
                LOGGER.severe("sensor " + actuator.mURL.toString() + " OFFLINE");
                Core.sendPushNotification(SendPushMessages.notification_error,"errore","ACTUATOR " + actuator.mURL.toString() + " OFFLINE","0");
            } else {
                LOGGER.info(txt);
                //
            }

            //writelog(sensor.mData, date);
        }
    }


    //public void alarmEvent() {

       // update();

        /*Thread myThread = new Thread(new Thread(new Runnable() {
            public void run() {
                //Do whatever
                update();
            }
        }));
        myThread.setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            public void uncaughtException(Thread myThread, Throwable e) {
                LOGGER.severe(myThread.getName() + " throws exception: " + e);
            }
        });
        // this will call run() function
        myThread.start();*/
    //}
}

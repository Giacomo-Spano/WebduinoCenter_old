package com.server.webduino.core;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;
import java.util.logging.Logger;

/**
 * Created by Giacomo Span� on 08/11/2015.
 */
public class Core {

    private static final Logger LOGGER = Logger.getLogger(Core.class.getName());

    static final String USER = "root";
    static final String PASS = "giacomo";
    static /*final*/ String DB_URL = "jdbc:mysql://127.0.0.1:3306/webduino";

    public /*static */Sensors mSensors;// = new Sensors();
    public /*static */Actuators mActuators;// = new Actuators();
    public /*static */Programs mPrograms;// = new Programs();

    public static Devices mDevices = new Devices();

    public Core() {

        mSensors = new Sensors();
        mActuators = new Actuators();
        mPrograms = new Programs();

        String var = System.getenv("debug");
        if (var != null && var.equals("true"))
            DB_URL = "jdbc:mysql://127.0.0.1:3306/webduinodebug";
    }

    public void init() {
        loadData();
    }

    public static /*synchronized*/ void sendPushNotification(String type, String title, String description, String value) {

        LOGGER.info("sendPushNotification type=" + type + "title=" + title + "value=" + value);
        new PushNotificationThread(type,title,description,value).start();

        LOGGER.info("sendPushNotification sent");
    }

    public void loadData() {
        mSensors.read();
        mActuators.read();
        for (int i = 0; i < mSensors.getSensorList().size(); i++) {
            mSensors.getSensorList().get(i).addListener(mPrograms);
        }
        mPrograms.init(getActuatorFromId(1));
        mPrograms.read(); // caricare actuator prima di program!!
        mDevices.read();
    }

    public /*static */ArrayList<Actuator> getActuators() {
        return mActuators.getActuatorList();
    }

    public /*static */ArrayList<Program> getPrograms() {
        return mPrograms.getProgramList();
    }

    public /*static */ArrayList<TemperatureSensor> getSensors() {
        return mSensors.getSensorList();
    }
    public ArrayList<ActiveProgram> getNextActiveProgramlist() {
        return mPrograms.getActiveProgramList();
    }
    public Program getProgramFromId(int id) {
        return mPrograms.getProgramFromId(id);
    }
    public ActiveProgram getActiveProgram() {
        return mPrograms.getActiveProgram();
    }
    public Date getLastActiveProgramUpdate() {
        return mPrograms.getLastActiveProgramUpdate();
    }
    public  /*synchronized*/ TemperatureSensor getSensorFromId(int id) {
        return mSensors.getSensorFromId(id);
    }
    public /*synchronized*/ Actuator getActuatorFromId(int id) { return mActuators.getFromId(id); }
    public /*static */int deleteProgram(int id) {
        return mPrograms.delete(id);
    }
    public/* static*/ int updatePrograms(Program program) {
        return mPrograms.insert(program);
    }

    public /*synchronized*/ static Date getDate() {

        LOGGER.info("getDate");
        Date date = new Date();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        df.setTimeZone(TimeZone.getTimeZone("Europe/Rome"));

        final String dateInString = df.format(date);

        Date newDate = null;
        try {

            newDate = df.parse(dateInString);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return newDate;
    }
}

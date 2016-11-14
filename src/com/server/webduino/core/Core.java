package com.server.webduino.core;

import org.json.JSONArray;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.logging.Logger;

/**
 * Created by Giacomo Span� on 08/11/2015.
 */
public class Core {

    private static final Logger LOGGER = Logger.getLogger(Core.class.getName());
    protected static String appDNS_envVar;
    protected static String mysqlDBHost_envVar;
    protected static String mysqlDBPort_envVar;
    protected static String tmpDir_envVar;
    protected static String dataDir_envVar;
    private static String version = "0.11";

    public static String APP_DNS_OPENSHIFT = "webduinocenter.rhcloud.com";
    public static String APP_DNS_OPENSHIFTTEST = "webduinocenterbeta-giacomohome.rhcloud.com";

    public Shields mShields;

    public Programs mPrograms;

    public static Devices mDevices = new Devices();

    public Core() {
        appDNS_envVar = System.getenv("OPENSHIFT_APP_DNS");
        mysqlDBHost_envVar = System.getenv("OPENSHIFT_MYSQL_DB_HOST");
        mysqlDBPort_envVar = System.getenv("OPENSHIFT_MYSQL_DB_PORT");
        tmpDir_envVar = System.getenv("OPENSHIFT_TMP_DIR");
        dataDir_envVar = System.getenv("OPENSHIFT_DATA_DIR");
    }

    public static String getUser() {
        if (appDNS_envVar != null && appDNS_envVar.equals(APP_DNS_OPENSHIFT))
            return "adminUp6Qw2f";
        else if (appDNS_envVar != null && appDNS_envVar.equals(APP_DNS_OPENSHIFTTEST))
            return "adminjNm7VUk";
            //return "adminw8ZVVu2";
        else
            //return "adminzdVX5dl";// production
            return "root";
    }

    public static String getPassword() {
        if (appDNS_envVar != null && appDNS_envVar.equals(APP_DNS_OPENSHIFT))
            return "rmIf9KYneg1C";
        else if (appDNS_envVar != null && appDNS_envVar.equals(APP_DNS_OPENSHIFTTEST))
            return "xX1MAIXQLLHq";
            //return "MhbY-61ZlqU4";
        else
            //return "eEySMcJ6WCj4"; //production
            return "giacomo";
    }

    public static String getDbUrl() {
        if (appDNS_envVar != null && appDNS_envVar.equals(APP_DNS_OPENSHIFT)) { // production
            return "jdbc:mysql://" + mysqlDBHost_envVar + ":" + mysqlDBPort_envVar + "/" + "webduino";
        } else if (appDNS_envVar != null && appDNS_envVar.equals(APP_DNS_OPENSHIFTTEST)) { // test
            return "jdbc:mysql://" + mysqlDBHost_envVar + ":" + mysqlDBPort_envVar + "/" + "webduino";
            //return "jdbc:mysql://" + mysqlDBHost_envVar + ":" + mysqlDBPort_envVar + "/" + "jbossews";
        } else
        //test
        return "jdbc:mysql://127.0.0.1:3306/webduino";
        //return "jdbc:mysql://127.0.0.1:3307/jbossews"; // production
    }

    public void init() {

        mShields = new Shields();
        mPrograms = new Programs();

        //mSensors.addListener(mPrograms);

        mShields.addListener(mPrograms); /// TODO quetso a cosa serve????

        mShields.addTemeratureSensorListener(mPrograms);// metti program in ascolto di ogni variazione di temperatura

        Settings settings = new Settings();
        mPrograms.init((HeaterActuator)getFromId(settings.HeaterActuatorId));
        mPrograms.read(); // caricare actuator prima di program!!
        mDevices.read();
    }

    public static void sendPushNotification(String type, String title, String description, String value) {

        LOGGER.info("sendPushNotification type=" + type + "title=" + title + "value=" + value);
        new PushNotificationThread(type,title,description,value).start();

        LOGGER.info("sendPushNotification sent");
    }

    public ArrayList<Actuator> getActuators() {
        return mShields.getActuators();
    }

    public List<SensorBase> getLastSensorData() {
        return mShields.getLastSensorData();
    }

    boolean updateSensors(int shieldid, JSONArray jsonArray) {
        return mShields.updateSensors(shieldid,jsonArray);
    }

    public Actuator getFromShieldId(int shieldid, String subaddress) {
        return mShields.getFromShieldId(shieldid, subaddress); }

    public Actuator getFromId(int id) {
        return mShields.getFromId(id); }

    /*public SensorBase getSensorFromShieldIdAndSubadress(int shieldid, String subaddress) {
        return mSensors.getFromShieldIdandSubaddress(shieldid, subaddress); }*/




    public ArrayList<Program> getPrograms() {
        return mPrograms.getProgramList();
    }

    public List<TemperatureSensor> getSensors() {
        return mShields.getSensorList();
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
    public TemperatureSensor getSensorFromId(int id) {
        return mShields.getSensorFromId(id);
    }

    public int deleteProgram(int id) {
        return mPrograms.delete(id);
    }
    public int updatePrograms(Program program) {
        return mPrograms.insert(program);
    }

    public static Date getDate() {

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

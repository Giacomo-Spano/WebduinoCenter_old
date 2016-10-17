package com.server.webduino.core;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class HeaterActuator extends Actuator /*implements TemperatureSensor.TemperatureSensorListener*/ {
    private static final Logger LOGGER = Logger.getLogger(HeaterActuator.class.getName());

    protected boolean releStatus;
    protected double avTemperature;
    protected double temperature;
    protected double remoteTemperature;
    protected int duration;
    protected int remaining;
    protected boolean localSensor;
    protected double targetTemperature;
    protected int activeProgramID;
    protected int activeTimeRangeID;

    protected int activeSensorID; //  questo valore non è letto dal sensore ma rimane solo sul server
    //protected String activeSensorName; //  questo valore non è letto dal sensore ma rimane solo sul server


    public HeaterActuator(URL url, int id, String name) {
        super(url, id, name);

        //listeners = new ArrayList<HeaterActuatorListener>();
    }

    interface HeaterActuatorListener extends ActuatorListener {
        void changeStatus(String newStatus, String oldStatus);

        void changeReleStatus(boolean newReleStatus, boolean oldReleStatus);

        void changeProgram(HeaterActuator heater, int newProgram, int oldProgram, int newTimerange, int oldTimerange);


    }

    @Override
    public void addListener(ActuatorListener toAdd) {
        listeners.add((HeaterActuatorListener) toAdd);
    }

    public int getDuration() {
        return duration;
    }

    protected void setDuration(int duration) {
        this.duration = duration;
    }

    public long getRemaining() {

        if (getStatus().equals(Actuator.STATUS_MANUALMODE) && lastUpdate != null) {

            Date currentDate = Core.getDate();
            long diff = currentDate.getTime() - lastUpdate.getTime();//as given
            long secondDiff = TimeUnit.MILLISECONDS.toSeconds(diff);
            //long minutes = TimeUnit.MILLISECONDS.toMinutes(diff);
            return remaining - secondDiff;
        } else {
            return 0;
        }
    }

    protected void setRemaining(int remaining) {
        this.remaining = remaining;
    }

    public boolean isLocalSensor() {
        return localSensor;
    }

    protected void setLocalSensor(boolean localSensor) {
        this.localSensor = localSensor;
    }

    public int getActiveSensorID() {
        return activeSensorID;
    }

    protected void setActiveSensorID(int activeSensorID) { //  questo valore non è letto dal sensore ma rimane solo sul server
        this.activeSensorID = activeSensorID;
    }

    /*public String getActiveSensorName() {
        return activeSensorName;
    }
    protected void setActiveSensorID(String activeSensorName) { //  questo valore non è letto dal sensore ma rimane solo sul server
        this.activeSensorName = activeSensorName;
    }*/

    public double getTargetTemperature() {
        return targetTemperature;
    }

    protected void setTargetTemperature(double targetTemperature) {
        this.targetTemperature = targetTemperature;
    }

    public int getActiveProgramID() {
        return activeProgramID;
    }

    protected void setActiveProgramID(int activeProgramID) {
        this.activeProgramID = activeProgramID;
    }

    public double getRemoteTemperature() {
        return remoteTemperature;
    }

    protected void setRemoteTemperature(double remoteTemperature) {
        this.remoteTemperature = remoteTemperature;
    }

    public int getActiveTimeRangeID() {
        return activeTimeRangeID;
    }

    protected void setActiveTimeRangeID(int activeTimeRangeID) {
        this.activeTimeRangeID = activeTimeRangeID;
    }

    protected void setReleStatus(boolean releStatus) {

        //boolean oldReleStatus = this.releStatus;
        this.releStatus = releStatus;

        /*if (releStatus != oldReleStatus) {
            // Notify everybody that may be interested.
            for (ActuatorListener hl : listeners)
                hl.changeReleStatus(releStatus, oldReleStatus);
        }*/
    }

    public boolean getReleStatus() {
        return releStatus;
    }

    public void writeDataLog() {
        HeaterDataLog dl = new HeaterDataLog();
        Date date = Core.getDate();
        dl.writelog(this.id, date, this/*releStatus*/);
    }

    protected void setAvTemperature(double temperature) {
        this.avTemperature = temperature;
    }

    public double getAvTemperature() {
        return avTemperature;
    }

    protected void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public double getTemperature() {
        return temperature;
    }

    public Boolean sendCommand(int command, long duration, double temperature, boolean localSensor, int activeProgramID, int activeTimeRangeID, int activeSensorID, double activeSensorTemperature) {
        // sendcommand è usata anche da actuatorservlet per mandare i command dalle app

        String postParam = "";
        String path = "";

        LOGGER.info("sendCommand command=" + command + ",duration=" + duration + ",temperature=" + temperature + ",localSensor=" + localSensor +
                ",activeProgramID=" + activeProgramID + ",activeTimeRangeID=" + activeTimeRangeID + ",activeSensorID=" + activeSensorID + "activeSensorTemperature=" + activeSensorTemperature);

        setActiveSensorID(activeSensorID);

        if (command == Command_Program_On) {
            path = "/rele";
            postParam = "status=1";
            postParam += "&duration=" + duration;
            postParam += "&target=" + temperature;
            if (localSensor == true) // local sensor
                postParam += "&sensor=0";
            else
                postParam += "&sensor=1";// remote sensor (non inviare sensor id!)
            postParam += "&program=" + activeProgramID;
            postParam += "&timerange=" + activeTimeRangeID;
            postParam += "&temperature=" + activeSensorTemperature;
            postParam += "&json=1";

        } else if (command == Command_Program_Off) {
            path = "/rele";
            postParam = "status=0";
            postParam += "&duration=" + duration;
            postParam += "&target=" + temperature;
            if (localSensor == true)
                postParam += "&sensor=0";
            else
                postParam += "&sensor=1";
            postParam += "&program=" + activeProgramID;
            postParam += "&timerange=" + activeTimeRangeID;
            postParam += "&temperature=" + activeSensorTemperature;
            postParam += "&json=1";

        } else if (command == Command_Manual_Start) {
            path = "/rele";
            postParam = "status=1";
            postParam += "&duration=" + duration;
            if (localSensor == true) {
                postParam += "&sensor=0";
                postParam += "&target=" + temperature;
            } else {
                postParam += "&sensor=1";

            }
            postParam += "&manual=1";
            postParam += "&temperature=" + activeSensorTemperature;
            postParam += "&json=1";

        } else if (command == Command_Manual_Stop) {
            path = "/rele";
            postParam = "status=0";
            /*postParam += "&duration=" + duration;
            if (sensor == 0) {
                postParam += "&sensor=0";
            } else {
                postParam += "&sensor=1";
                postParam += "&target=" + temperature;
            }*/
            postParam += "&manual=1";
            postParam += "&temperature=" + temperature;
            postParam += "&json=1";

        } else if (command == Command_Send_Temperature) {
            path = "/temp";
            postParam = "temperature=" + activeSensorTemperature;

        }
        boolean res = postCommand(postParam, path);
        if (res) {
            Date date = Core.getDate();
            setLastUpdate(date);
            writeDataLog();
        }
        return res;
    }

    @Override
    public void updateFromJson(JSONObject json) {

        boolean oldReleStatus = this.releStatus;
        int oldProgramId = activeProgramID;
        int oldTimerangeId = activeTimeRangeID;
        int oldsensorId = activeSensorID;
        double oldTargetId = targetTemperature;
        String oldStatus = getStatus();

        Date date = Core.getDate();
        lastUpdate = date;
        try {
            LOGGER.info("received jsonResultSring=" + jsonResultSring);

            if (json.has("temperature"))
                setTemperature(json.getDouble("temperature"));
            if (json.has("avtemperature"))
                setAvTemperature(json.getDouble("avtemperature"));
            if (json.has("remotetemperature"))
                setRemoteTemperature(json.getDouble("remotetemperature"));
            if (json.has("relestatus"))
                setReleStatus(json.getBoolean("relestatus"));
            if (json.has("status"))
                setStatus(json.getString("status"));
            if (json.has("name"))
                setName(json.getString("name"));
            if (json.has("sensorid"))
                setId(json.getInt("sensorid"));
            if (json.has("duration"))
                setDuration(duration = json.getInt("duration"));
            if (json.has("remaining"))
                setRemaining(remaining = json.getInt("remaining"));
            if (json.has("localsensor"))
                setLocalSensor(localSensor = json.getBoolean("localsensor"));
            if (json.has("target"))
                setTargetTemperature(targetTemperature = json.getDouble("target"));
            if (json.has("program"))
                setActiveProgramID(activeProgramID = json.getInt("program"));
            if (json.has("timerange"))
                setActiveTimeRangeID(activeTimeRangeID = json.getInt("timerange"));
        } catch (JSONException e) {
            e.printStackTrace();
            LOGGER.info("json error: " + e.toString());
        }

        if (releStatus != oldReleStatus) {
            // Notify everybody that may be interested.
            for (ActuatorListener hl : listeners) {
                ((HeaterActuatorListener) hl).changeReleStatus(releStatus, oldReleStatus);
            }
        }
        if (activeProgramID != oldProgramId || activeTimeRangeID != oldTimerangeId) {
            // Notify everybody that may be interested.
            for (ActuatorListener hl : listeners)
                ((HeaterActuatorListener) hl).changeProgram(this, activeProgramID, oldProgramId, activeTimeRangeID, oldTimerangeId);
        }
        if (!getStatus().equals(oldStatus)) {
            // Notify everybody that may be interested.
            for (ActuatorListener hl : listeners)
                ((HeaterActuatorListener) hl).changeStatus(getStatus(), oldStatus);
        }

    }

    @Override
    public JSONObject getJson() {
        JSONObject json = new JSONObject();
        try {
            json.put("id", id);
            json.put("temperature", temperature);
            json.put("avtemperature", avTemperature);
            json.put("remotetemperature", remoteTemperature);
            json.put("url", mURL);
            json.put("name", name);
            json.put("status", getStatus());
            json.put("duration", duration);
            json.put("remaining", getRemaining());
            json.put("relestatus", getReleStatus());
            json.put("lastupdate", lastUpdate);
            json.put("localsensor", localSensor);

            json.put("target", targetTemperature);

            json.put("program", activeProgramID);
            json.put("timerange", activeTimeRangeID);
            Date currentDate = Core.getDate();

            Locale.setDefault(Locale.ITALIAN);
            SimpleDateFormat sdf = new SimpleDateFormat("EEEE dd MMMM yyyy");
            String strDate = sdf.format(currentDate);
            json.put("fulldate", strDate);

            sdf = new SimpleDateFormat("dd-MM-yyyy");
            strDate = sdf.format(currentDate);
            json.put("date", strDate);

            sdf = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ssZ" );
            sdf.setTimeZone( TimeZone.getTimeZone("Europe/Rome"));
            strDate = sdf.format(currentDate);
            json.put("UTCdate", strDate);

            sdf = new SimpleDateFormat("hh:mm:ss");
            String strTime = sdf.format(currentDate);
            json.put("time", strTime);

            json.put("sensorID", activeSensorID);



        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }

    /*@Override
    public void changeTemperature(int ID, double temperature) {

    }

    @Override
    public void changeAvTemperature(int ID, double avTemperature) {


    }*/
}

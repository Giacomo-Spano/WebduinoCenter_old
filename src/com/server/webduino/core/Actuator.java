package com.server.webduino.core;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

public class Actuator extends SensorBase {

    private static final Logger LOGGER = Logger.getLogger(Actuator.class.getName());

    static final String STATUS_IDLE = "idle";
    static final String STATUS_PROGRAMACTIVE = "program";
    static final String STATUS_MANUALMODE = "manual";
    static final String STATUS_DISABLED = "disabled";

    public static final int Command_Program_Off = 0;
    public static final int Command_Program_On = 1;
    public static final int Command_Send_Temperature = 2;
    public static final int Command_Manual_Start = 3;
    public static final int Command_Manual_Stop = 4;

    static final int relestatus_off = 0;
    static final int relestatus_on = 1;
    static final int relestatus_disabled = 2;
    static final int relestatus_enabled = 3;

    private String status = "";

    public Actuator() {
    }

    public void SetData(int id, int shieldid, String subaddress, String name, Date lastupdate) {
        super.setData(shieldid, subaddress, name, lastUpdate);

        this.id = id;
        //this.name = name;
        listeners = new ArrayList<ActuatorListener>();
    }

    interface ActuatorListener {
        void changeStatus(String newStatus, String oldStatus);
    }

    protected List<ActuatorListener> listeners = new ArrayList<ActuatorListener>();

    public void addListener(ActuatorListener toAdd) {
        listeners.add(toAdd);
    }

    public void setStatus(String status) {

        String oldStatus = this.status;
        this.status = status;

    }

    public String getStatus() {
        return status;
    }

    protected Boolean postCommand(String postParam, String path) {
            // questa per ora è usata solo dat heater actuator

        //String result = callPost(path, postParam);
        Result result = call("POST", path, postParam);
        if (result.res == false) {
            for (int i = 0; i < 2; i++) {

                LOGGER.info("retry..." + i);
                //result = callPost(path, postParam);
                result = call("POST", path, postParam);
                if (result != null)
                    break;
            }
        }
        if (result.res == true) {
            //updateStatus();
            try {
                JSONObject json = new JSONObject(result.response);
                updateFromJson(json);
            } catch (JSONException e) {
                e.printStackTrace();
                LOGGER.severe("json error ");
            }
            LOGGER.info("command sent");
            return true;
        } else {
            LOGGER.severe("command FAILED");
            return false;
        }
    }


    void updateFromJson(JSONObject json) {
    }

    public JSONObject getJson() {

        return null;
    }
}

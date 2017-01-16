package com.server.webduino.core;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.logging.Logger;

/**
 * Created by Giacomo Spanò on 29/12/2016.
 */
public class HeaterActuatorCommand extends ActuatorCommand {

    private static final Logger LOGGER = Logger.getLogger(HeaterActuatorCommand.class.getName());

    public static final String Command_Program_ReleOff = "0"; // "programoff";
    public static final String Command_Program_ReleOn = "1"; // "programon";
    public static final String Command_Send_Disabled = "2"; // "sendtemperature";
    public static final String Command_Send_Enabled = "3"; // "sendtemperature";
    public static final String Command_Manual_Off = "4"; // "manualoff";
    public static final String Command_Manual_Auto = "5"; // "manual";
    public static final String Command_Manual_End = "6"; // "endmanual";
    public static final String Command_Send_Temperature = "7"; // "sendtemperature";


    public static String getCommandName(String n) {

        switch (n) {
            case Command_Program_ReleOff:
                return "programoff";
            case Command_Program_ReleOn:
                return "programon";
            case  Command_Send_Disabled:
                return "sendtemperature";
            case Command_Send_Enabled:
                return "sendtemperature";
            case Command_Manual_Off:
                return "manualoff";
            case Command_Manual_Auto:
                return "manualauto";
            case Command_Manual_End:
                return"endmanual";
            case Command_Send_Temperature:
                return "sendtemperature";
        }
        return "";
    }

    public long duration;
    public double targetTemperature;
    public boolean remoteSensor;
    public int activeProgramID;
    public int activeTimeRangeID;
    public int activeSensorID;
    public double activeSensorTemperature;

    @Override
    public boolean fromJson(JSONObject json) {

        try {
            if (json.has("command"))
                command = json.getString("command");
            if (json.has("duration"))
                duration = json.getInt("duration");
            if (json.has("target"))
                targetTemperature = json.getDouble("target");
            if (json.has("sensorid"))
                activeSensorID = json.getInt("sensorid");
            if (json.has("remote"))
                remoteSensor = json.getBoolean("remote");

            return true;

        } catch (JSONException e) {
            e.printStackTrace();
            LOGGER.info("json error: " + e.toString());
            return false;
        }
    }
}

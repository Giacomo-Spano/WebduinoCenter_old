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

    /*const int command_ProgramOff = 0;
	const int command_ProgramOn = 1;
	const int command_disabled = 2;
	const int command_enabled = 3;
	const int command_ManualOff = 4;
	const int command_Manual = 5;
	const int command_ManualEnd = 6;*/

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

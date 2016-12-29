package com.server.webduino.core;

/**
 * Created by Giacomo Spanò on 29/12/2016.
 */
public class HeaterActuatorCommand extends ActuatorCommand {
    public long duration;
    public double targetTemperature;
    public boolean remoteSensor;
    public int activeProgramID;
    public int activeTimeRangeID;
    public int activeSensorID;
    public double activeSensorTemperature;
}

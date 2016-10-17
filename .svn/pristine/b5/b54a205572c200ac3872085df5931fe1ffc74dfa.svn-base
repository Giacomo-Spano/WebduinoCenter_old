package com.server.webduino.core;
import java.sql.Time;

/**
 * Created by Giacomo Spanò on 07/11/2015.
 */
public class TimeRange {

    public int ID;
    public String name;
    //Time startTime;
    public Time endTime;
    public Double temperature;
    public int sensorID;
    public int programID;
    public int priority;

    public TimeRange() {
    }

    public TimeRange(int ID, String name, Time endTime, Double temperature, int sensorID, int programID) {
        this.ID = ID;
        this.name = name;
        //this.startTime = startTime;
        this.endTime = endTime;
        this.temperature = temperature;
        this.sensorID = sensorID;
        this.programID = programID;
    }

    public TimeRange(TimeRange tr) {
        this.ID = tr.ID;
        this.name = tr.name;
        this.endTime = tr.endTime;
        this.temperature = tr.temperature;
        this.sensorID = tr.sensorID;
        this.programID = tr.programID;
    }
}

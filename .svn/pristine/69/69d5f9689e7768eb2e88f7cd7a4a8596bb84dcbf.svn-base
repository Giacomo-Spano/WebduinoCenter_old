package com.server.webduino.core;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Logger;

public class TemperatureSensor extends SensorBase {

    private static Logger LOGGER = Logger.getLogger(TemperatureSensor.class.getName());

    private double temperature;
    private double avTemperature;
    public String humidity;
    public String pressure;

    public interface TemperatureSensorListener {
        void changeTemperature(int ID, double temperature);
        void changeAvTemperature(int ID, double avTemperature);
    }

    private List<TemperatureSensorListener> listeners = new ArrayList<TemperatureSensorListener>();

    public void addListener(TemperatureSensorListener toAdd) {
        listeners.add(toAdd);
    }

    public TemperatureSensor(URL url, int id, String name) {
        super(url);

        this.url = url;
        this.id = id;
        this.name = name;
    }

    public void setData(Date date, double temperature, double avTemperature) {

        setLastUpdate(date);
        setTemperature(temperature);
        setAvTemperature(avTemperature);

        SensorDataLog dl = new SensorDataLog();
        dl.writelog(id, /*temperature, avTemperature, */date, this);
    }

    public void setAvTemperature(double avTemperature) {

        LOGGER.info("setAvTemperature");

        double oldAvTemperature = this.avTemperature;
        this.avTemperature = avTemperature;

 //       if (avTemperature!= oldAvTemperature) {
            // Notify everybody that may be interested.
            for (TemperatureSensorListener hl : listeners)
                hl.changeAvTemperature(id, avTemperature);
   //     }
    }

    public double getAvTemperature() {
        return avTemperature;
    }

    public /*synchronized*/ void setTemperature(double temperature) {

        double oldtemperature = this.temperature;
        this.temperature = temperature;

        if (temperature!= oldtemperature) {
            // Notify everybody that may be interested.
            for (TemperatureSensorListener hl : listeners)
                hl.changeTemperature(id, temperature);
        }
    }

    public double getTemperature() {
        return temperature;
    }

    @Override
    void updateFromJson(JSONObject json) {

        try {
            Date date = Core.getDate();
            lastUpdate = date;
            if (json.has("temperature"))
                setTemperature(json.getDouble("temperature"));
            if (json.has("avtemperature"))
                setAvTemperature(json.getDouble("avtemperature"));
            if (json.has("humidity"))
                humidity = json.getString("humidity");
            if (json.has("name"))
                boardName = json.getString("name");
            if (json.has("sensorid"))
                id = json.getInt("sensorid");
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            LOGGER.info("json error: " + e.toString());
        }
    }

    @Override
    public JSONObject getJson() {
        JSONObject json = new JSONObject();
        try {
            json.put("id", getId());
            json.put("temperature", getTemperature());
            json.put("avtemperature", getAvTemperature());
            json.put("url", getUrl());
            json.put("humidity", humidity);
            json.put("name", getName());
            json.put("pressure", pressure);
            json.put("lastupdate", getLastUpdate());

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }
}

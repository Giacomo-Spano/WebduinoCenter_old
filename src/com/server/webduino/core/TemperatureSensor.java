package com.server.webduino.core;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.*;
import java.util.*;
import java.util.logging.Logger;

public class TemperatureSensor extends SensorBase {

    private static Logger LOGGER = Logger.getLogger(TemperatureSensor.class.getName());

    private double temperature;
    private double avTemperature;


    public interface TemperatureSensorListener {
        void changeTemperature(int shieldId, String subAddress, double temperature);

        void changeAvTemperature(int sensorId, double avTemperature);
    }

    private List<TemperatureSensorListener> listeners = new ArrayList<TemperatureSensorListener>();

    public void addListener(TemperatureSensorListener toAdd) {
        listeners.add(toAdd);
    }

    /*public TemperatureSensor(int shieldid, String subaddress, String name, Date lastupdate, double temperature, double avTemperature) {
        super(shieldid, subaddress, name, lastupdate);

        this.temperature = temperature;
        this.avTemperature = avTemperature;


    }*/

    public TemperatureSensor() {
    }

    public void setData(int shieldid, String subaddress, String name, Date date, double temperature, double avTemperature) {
        super.setData(shieldid, subaddress, name, date);
        //lastUpdate = date;
        temperature = temperature;
        avTemperature = avTemperature;
        SensorDataLog dl = new SensorDataLog();
        dl.writelog(shieldid, subaddress, date, temperature,avTemperature);
    }


    public void setAvTemperature(double avTemperature) {

        LOGGER.info("setAvTemperature");

        this.avTemperature = avTemperature;
        // Notify everybody that may be interested.
        for (TemperatureSensorListener hl : listeners)
            hl.changeAvTemperature(id, avTemperature);
    }

    public double getAvTemperature() {
        return avTemperature;
    }

    public /*synchronized*/ void setTemperature(double temperature) {

        double oldtemperature = this.temperature;
        this.temperature = temperature;

        if (temperature != oldtemperature) {
            // Notify everybody that may be interested.
            for (TemperatureSensorListener hl : listeners)
                hl.changeTemperature(id, subaddress, temperature);
        }
    }

    public double getTemperature() {
        return temperature;
    }

    @Override
    void updateFromJson(JSONObject json) {

        double oldAvTemperature = avTemperature;
        try {
            Date date = Core.getDate();
            lastUpdate = date;
            if (json.has("temperature"))
                setTemperature(json.getDouble("temperature"));
            if (json.has("avtemperature"))
                setAvTemperature(json.getDouble("avtemperature"));
            if (json.has("name"))
                name = json.getString("name");
            setData(shieldid, subaddress, name, date, temperature, avTemperature);

            if (oldAvTemperature != avTemperature) {
                for (TemperatureSensorListener listener : listeners) {
                    listener.changeAvTemperature(id,avTemperature);
                }
            }

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
            json.put("shieldid", shieldid);
            json.put("subaddress", subaddress);
            json.put("temperature", getTemperature());
            json.put("avtemperature", getAvTemperature());
            json.put("name", getName());
            json.put("lastupdate", getLastUpdate());
            json.put("type", type);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }
}

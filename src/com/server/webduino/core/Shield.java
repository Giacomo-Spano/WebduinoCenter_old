package com.server.webduino.core;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import static com.server.webduino.core.SensorBase.Status_Offline;

public class Shield extends httpClient {

    private static Logger LOGGER = Logger.getLogger(Shield.class.getName());

    protected int id;
    protected String MACAddress;
    protected String boardName;
    protected Date lastUpdate;
    List<SensorBase> sensors = new ArrayList<>();
    List<Actuator> actuators = new ArrayList<>();
    public URL url;

    public Shield(JSONObject jsonObj) {
        FromJson(jsonObj);
    }

    public Shield() {
    }

    public boolean sensorsIsNotUpdated() {

        Date currentDate = Core.getDate();
        boolean res = false;
        for (SensorBase s : sensors) {
            if (s.lastUpdate == null || (currentDate.getTime() - s.lastUpdate.getTime()) > (30*1000) ) {
                s.onlinestatus = Status_Offline;
                res = true;
            }
        }
        return res;
    }

    public boolean actuatorsIsNotUpdated() {

        Date currentDate = Core.getDate();
        boolean res = false;
        for (SensorBase s : actuators) {
            if (s.lastUpdate == null || (currentDate.getTime() - s.lastUpdate.getTime()) > (30*1000) ) {
                s.onlinestatus = Status_Offline;
                res = true;
            }
        }
        return res;
    }

    void FromJson(JSONObject json) {

        try {
            Date date = Core.getDate();
            lastUpdate = date;
            if (json.has("MAC"))
                MACAddress = json.getString("MAC");
            if (json.has("boardname"))
                boardName = json.getString("boardname");
            if (json.has("localIP")) {
                try {
                    url = new URL("http://" + json.getString("localIP"));
                } catch (Exception e) {
                    e.printStackTrace();
                    LOGGER.info("url error: " + e.toString());
                }
            }
            if (json.has("sensors")) {
                JSONArray jsonArray = json.getJSONArray("sensors");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject j = jsonArray.getJSONObject(i);
                    if (j.has("type")) {
                        String type = j.getString("type");
                        if (type.equals("TemperatureSensor")) {
                            TemperatureSensor sensor = new TemperatureSensor();
                            if (j.has("name"))
                                sensor.name = j.getString("name");
                            if (j.has("addr"))
                                sensor.subaddress = j.getString("addr");
                            if (j.has("type"))
                                sensor.type = j.getString("type");
                            sensors.add(sensor);
                        }
                    }
                }
            }
            if (json.has("actuators")) {
                JSONArray jsonArray = json.getJSONArray("actuators");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject j = jsonArray.getJSONObject(i);
                    if (j.has("type")) {
                        String type = j.getString("type");
                        if (type.equals("HeaterActuator")) {
                            HeaterActuator actuator = new HeaterActuator();
                            if (j.has("name"))
                                actuator.name = j.getString("name");
                            if (j.has("addr"))
                                actuator.subaddress = j.getString("addr");
                            if (j.has("type"))
                                actuator.type = j.getString("type");
                            actuators.add(actuator);
                        }
                    }
                }
            }

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            LOGGER.info("json error: " + e.toString());
        }
    }

    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        try {
            json.put("id", id);
            if (boardName != null)
                json.put("boardname", boardName);
            if (MACAddress != null)
                json.put("macaddres", MACAddress);
            if (url != null)
                json.put("url", url);
            JSONArray jarray = new JSONArray();
            for (SensorBase sensor : sensors) {
                JSONObject jsensor = new JSONObject();
                jsensor.put("subaddress", sensor.subaddress);
                jsensor.put("name", sensor.name);
                jarray.put(jsensor);
            }
            json.put("sensors", jarray);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }
}

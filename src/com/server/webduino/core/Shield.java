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
    List<Integer> sensorIds = new ArrayList<>();
    List<Integer> actuatorIds = new ArrayList<>();
    public URL url;

    /*public Shield(JSONObject jsonObj) {
        //FromJson(jsonObj);
    }*/

    public Shield() {
    }

    public boolean sensorsIsNotUpdated() {

        Date currentDate = Core.getDate();
        boolean res = false;
        for (Integer id : sensorIds) {
            SensorBase s = Shields.getSensorFromId(id);
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
        for (Integer id : actuatorIds) {
            SensorBase s = Shields.getActuatorFromId(id);
            if (s.lastUpdate == null || (currentDate.getTime() - s.lastUpdate.getTime()) > (30*1000) ) {
                s.onlinestatus = Status_Offline;
                res = true;
            }
        }
        return res;
    }

    public boolean FromJson(JSONObject json) {

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
                    if (url.equals(new URL("http://0.0.0.0"))) {
                        LOGGER.info("url error: " + url.toString());
                        return false;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    LOGGER.info("url error: " + e.toString());
                    return false;
                }
            }
            if (json.has("sensorIds")) {
                JSONArray jsonArray = json.getJSONArray("sensorIds");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject j = jsonArray.getJSONObject(i);
                    if (j.has("type")) {
                        String type = j.getString("type");
                        if (type.equals("temperature")) {
                            TemperatureSensor sensor = new TemperatureSensor();
                            if (j.has("name"))
                                sensor.name = j.getString("name");
                            if (j.has("addr"))
                                sensor.subaddress = j.getString("addr");
                            if (j.has("type"))
                                sensor.type = j.getString("type");
                            sensorIds.add(sensor.id);
                        }
                    }
                }
            }
            if (json.has("actuatorIds")) {
                JSONArray jsonArray = json.getJSONArray("actuatorIds");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject j = jsonArray.getJSONObject(i);
                    if (j.has("type")) {
                        String type = j.getString("type");
                        if (type.equals("heater")) {
                            HeaterActuator actuator = new HeaterActuator();
                            if (j.has("name"))
                                actuator.name = j.getString("name");
                            if (j.has("addr"))
                                actuator.subaddress = j.getString("addr");
                            if (j.has("type"))
                                actuator.type = j.getString("type");
                            actuatorIds.add(actuator.id);
                        }
                    }
                }
            }

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            LOGGER.info("json error: " + e.toString());
            return false;
        }
        return true;
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
            for (Integer id : sensorIds) {
                SensorBase sensor = Shields.getSensorFromId(id);
                if (sensor != null)
                    jarray.put(sensor.getJson());
            }
            json.put("sensorIds", jarray);

            jarray = new JSONArray();
            for (Integer id : actuatorIds) {
                SensorBase actuator = Shields.getActuatorFromId(id);
                if (actuator != null)
                    jarray.put(actuator.getJson());
            }
            json.put("actuatorIds", jarray);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }
}

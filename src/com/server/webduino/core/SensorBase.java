package com.server.webduino.core;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SensorBase extends httpClient {

    private static Logger LOGGER = Logger.getLogger(SensorBase.class.getName());

    public static final String Status_Offline = "OFFLINE";
    public static final String Status_Online = "ONLINE";

    protected int shieldid;
    protected String onlinestatus = Status_Offline;
    protected String subaddress;
    protected String name; // valore letto dal db
    protected Date lastUpdate;
    protected String type;
    protected int id;


    public SensorBase() {

    }

    public void setData(int shieldid, String subaddress, String name, Date date) {

        this.shieldid = shieldid;
        this.subaddress = subaddress;
        this.name = name;
        this.lastUpdate = date;

    }

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public String getStrLastUpdate() {
        if (lastUpdate == null)
            return "";
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return df.format(lastUpdate);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public void setLastUpdate(Date date) {
        LOGGER.info("setLastUpdate");
        lastUpdate = date;
    }

    public Date getLastUpdate(Date date) {
        return lastUpdate;
    }

    public String updateStatus() { //

        LOGGER.info("updateStatus");

        Result result = call("GET", "", "/status");
        if (!result.res)
            return null;

        for (int i = 0; i < 2; i++) {

            LOGGER.log(Level.WARNING, "retry..." + (i + 1));
            result = call("GET", "", "/status");
            if (result != null)
                return result.response;
        }
        LOGGER.info("end updateStatus");
        return null;
    }

    void updateFromJson(JSONObject json) {
    }

    public JSONObject getJson() {

        return null;
    }

    protected Result call(String method, String param, String path) {

        LOGGER.info("call: " + method + "," + param + "," + path);

        Shields shields = new Shields();
        URL url = shields.getURL(shieldid);


        LOGGER.info("url: " + url.toString());
        //boolean res;

        Result result = null;
        if (method.equals("GET")) {

            result = callGet(param, path, url);
            if (result.res) {
                try {
                    JSONObject json = new JSONObject(result.response);
                    updateFromJson(json);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } else if (method.equals("POST")) {
            result = callPost(param, path, url);
        }

        LOGGER.info("end call");
        return result;
    }
}

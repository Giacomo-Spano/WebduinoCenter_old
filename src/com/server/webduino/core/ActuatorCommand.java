package com.server.webduino.core;

import org.json.JSONObject;

/**
 * Created by Giacomo Spanò on 29/12/2016.
 */
public class ActuatorCommand {
    public String command;

    public boolean fromJson(JSONObject json) {
        return false;
    }
}

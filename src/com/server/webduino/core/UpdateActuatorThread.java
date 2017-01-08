package com.server.webduino.core;

import com.quartz.QuartzListener;
import org.json.JSONException;
import org.json.JSONObject;

import javax.servlet.ServletContext;
import java.util.logging.Logger;

/**
 * Created by Giacomo Spanò on 15/02/2016.
 */
public class UpdateActuatorThread extends Thread {

    private static final Logger LOGGER = Logger.getLogger(UpdateActuatorThread.class.getName());

    //int shieldid;
    String subaddress;
    ServletContext context;
    JSONObject json;

    public UpdateActuatorThread(ServletContext context, JSONObject json) {
        super("str");

        this.json = json;
        this.context = context;
        //this.shieldid = shieldId;

    }
    public void run() {

        LOGGER.info("UpdateActuatorThread -START");
        Core core = (Core)context.getAttribute(QuartzListener.CoreClass);

        if (json.has("shieldid") && json.has("addr")) {
            try {
                String subaddress = json.getString("addr");
                int shieldid = json.getInt("shieldid");

                Actuator actuator = (HeaterActuator) core.getFromShieldId(shieldid, subaddress);
                if (actuator != null) {
                    actuator.updateFromJson(json);
                    actuator.writeDataLog();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        LOGGER.info("UpdateActuatorThread - END");
    }
}


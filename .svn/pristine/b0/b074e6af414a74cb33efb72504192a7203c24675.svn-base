package com.server.webduino.core;

import com.quartz.QuartzListener;
import org.json.JSONObject;

import javax.servlet.ServletContext;
import java.util.logging.Logger;

/**
 * Created by Giacomo Spanò on 15/02/2016.
 */
public class UpdateActuatorThread extends Thread {

    private static final Logger LOGGER = Logger.getLogger(UpdateActuatorThread.class.getName());

    int id;
    /*double avTemperature;
    String status;
    Boolean releStatus;*/
    ServletContext context;
    JSONObject json;

    public UpdateActuatorThread(ServletContext context, int id, JSONObject json/*, String status, boolean relestatus, double avtemperature*/) {
        super("str");

        this.json = json;
        this.context = context;
        this.id = id;
        /*this.avTemperature = avtemperature;
        this.status = status;
        this.releStatus = relestatus;*/

    }
    public void run() {

        LOGGER.info("UpdateActuatorThread -START");
        Core core = (Core)context.getAttribute(QuartzListener.CoreClass);

        /*HeaterActuator actuator = (HeaterActuator) core.getActuatorFromId(id);
        actuator.setAvTemperature(avTemperature);
        actuator.setStatus(status);*/
        //Date date = Core.getDate();
        HeaterActuator heater = (HeaterActuator) core.getActuatorFromId(id);
        //heater.setLastUpdate(date);
        //heater.setReleStatus(releStatus);
        heater.updateFromJson(json);
        heater.writeDataLog();

        LOGGER.info("UpdateActuatorThread - END");
    }
}


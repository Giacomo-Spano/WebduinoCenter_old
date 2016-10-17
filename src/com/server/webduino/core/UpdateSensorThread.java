package com.server.webduino.core;

import com.quartz.QuartzListener;
import com.server.webduino.servlet.SendPushMessages;

import javax.servlet.ServletContext;
import java.util.Date;
import java.util.logging.Logger;

/**
 * Created by Giacomo Spanò on 15/02/2016.
 */
public class UpdateSensorThread extends Thread {

    private static final Logger LOGGER = Logger.getLogger(UpdateSensorThread.class.getName());

    int id;
    double avTemperature;
    double temperature;
    String value;
    ServletContext context;

    public UpdateSensorThread(ServletContext context, int id, double avTemperature, double temperature) {
        super("str");

        this.context = context;
        this.id = id;
        this.avTemperature = avTemperature;
        this.temperature = temperature;
    }

    public void run() {

        LOGGER.info("UpdateSensorThread -START");
        Core core = (Core)context.getAttribute(QuartzListener.CoreClass);
        TemperatureSensor sensor = core.getSensorFromId(id);
        Date date = Core.getDate();
        sensor.setData(date,temperature,avTemperature);
        LOGGER.info("UpdateSensorThread - END");
    }
}


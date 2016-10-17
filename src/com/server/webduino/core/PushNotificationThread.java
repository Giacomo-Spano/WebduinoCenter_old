package com.server.webduino.core;

import com.server.webduino.servlet.SendPushMessages;

import java.util.logging.Logger;

/**
 * Created by Giacomo Spanò on 15/02/2016.
 */
public class PushNotificationThread extends Thread {

    private static final Logger LOGGER = Logger.getLogger(PushNotificationThread.class.getName());

    String type;
    String title;
    String description;
    String value;

    public PushNotificationThread(String type, String title, String description, String value) {
        super("str");

        this.type = type;
        this.title = title;
        this.description = description;
        this.value = value;


    }
    public void run() {

        LOGGER.info("PushNotificationThread type=" + type + "title=" + title + "value=" + value);
        SendPushMessages sp = new SendPushMessages(type, title, description, value);
        sp.send();
        LOGGER.info("PushNotificationThread type=" + type + "title=" + title + "value=" + value);
    }
}


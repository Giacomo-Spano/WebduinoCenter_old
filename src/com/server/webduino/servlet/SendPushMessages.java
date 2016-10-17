package com.server.webduino.servlet;

import com.google.android.gcm.server.*;
import com.server.webduino.core.Core;
import com.server.webduino.core.Device;
import com.server.webduino.core.Devices;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SendPushMessages {

    public static final String App_webduino = "Webduino";
    public static final String App_GPSAlarm = "GPSAlarm";
    public static final String App_WindAlarm = "WindAlarm";

    public static final String notification_statuschange = "1";
    public static final String notification_restarted = "2";
    public static final String notification_programchange = "3";
    public static final String notification_relestatuschange = "4";
    public static final String notification_offline = "5";
    public static final String notification_error = "6";
    public static final String notification_register = "7";


    private static final Logger LOGGER = Logger.getLogger(SendPushMessages.class.getName());

    private static final int MULTICAST_SIZE = 1000;

    public Sender sender;
    private Message mMessage;
    //String mNotificationtype;

    public SendPushMessages() {
    }

    public SendPushMessages(String type, String title, String description, String value) {

        mMessage = new Message.Builder()
                .addData("title", title)
                .addData("description", description)
                .addData("value", value)
                .addData("notificationtype", type)
                .build();


        init();
        //send(message);
    }

    public void init() /*throws ServletException*/ {

        sender = newSender();
    }

    protected Sender newSender() {

        String key = "AIzaSyCVaM1D21srrg8-0gzPC8e_4EpznChipW4";
        return new Sender(key);
    }

    public void send() {


        String status;

        LOGGER.log(Level.INFO, "send message", mMessage);

        if (Core.mDevices.getList().size() == 0) {
            status = "Message ignored as there is no device registered!";
            LOGGER.info(status);
        } else {
            // NOTE: check below is for demonstration purposes; a real
            // application
            // could always send a multicast, even for just one recipient
            if (false/* devices.size() == 1 */) {
                // send a single message using plain post
                //String registrationId = list.get(0).regId;
                // Message message = new Message.Builder().build();

                //Result result = sendSingleMessage(registrationId,message);
                //status = "Sent message to one device: " + result;
                LOGGER.info(status);
            } else {
                // send a multicast message using JSON
                // must split in chunks of 1000 devices (GCM limit)
                int total = Core.mDevices.getList().size();
                List<String> partialDevices = new ArrayList<String>(total);
                int counter = 0;
                int tasks = 0;
                for (Device device : Core.mDevices.getList()) {
                    counter++;
                    LOGGER.info("regid:" + device.regId + " name:" + device.name);
                    partialDevices.add(device.regId);

                    int partialSize = partialDevices.size();
                    if (partialSize == MULTICAST_SIZE || counter == total) {
                        //asyncSend(partialDevices, message);  non si possono usare i threa con google app engine
                        syncSend(partialDevices, mMessage);
                        partialDevices.clear();
                        tasks++;
                    }
                }
                status = "Asynchronously sendingxx " + tasks
                        + " multicast messages to " + total + " devices";
            }
            LOGGER.info(status);
        }
    }

    public Result sendSingleMessage(String registrationId, Message message) {

        LOGGER.info("sendSingleMessage message=" + message);

        Result result = null;// = new Result();
        try {
            result = sender.send(message, registrationId, 5);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return result;
    }

    private void syncSend(List<String> partialDevices, Message mess) {
        // make a copy
        final List<String> devices = new ArrayList<String>(partialDevices);
        final Message message = mess;// new Message.Builder().build();

        // Message message = new Message.Builder().build();
        MulticastResult multicastResult;
        try {
            multicastResult = sender.send(message, devices, 5);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error posting messages", e);
            return;
        }
        List<Result> results = multicastResult.getResults();
        // analyze the results
        for (int i = 0; i < devices.size(); i++) {
            String regId = devices.get(i);
            Result result = results.get(i);
            String messageId = result.getMessageId();
            if (messageId != null) {
                /*logger.fine("Succesfully sent message to device: " + regId
						+ "; messageId = " + messageId);*/
                String canonicalRegId = result.getCanonicalRegistrationId();
                if (canonicalRegId != null) {
                    // same device has more than on registration id:
                    // update it
                    LOGGER.severe("same device has more than on registration id: - update it");
                    //logger.info("canonicalRegId " + canonicalRegId);

                    //Datastore.updateRegistration(regId, canonicalRegId);  //XXXXXXXXXXXXXXXXXXXXXXXXX
                }
            } else {
                String error = result.getErrorCodeName();
                if (error.equals(Constants.ERROR_NOT_REGISTERED) ||
                        error.equals(Constants.ERROR_INVALID_REGISTRATION)) {
                    // application has been removed from device -
                    // unregister it
                    LOGGER.severe("application has been removed from device - unregister it");
                    //logger.info("Unregistered device: " + regId);
                    //Datastore.unregister(regId);//XXXXXXXXXXXXXXXXXXXXXXXXXXXXX
                } else {
                    LOGGER.severe("Error sending message to " + regId + ": "
                            + error);
                }
            }
        }
    }

	/*private void asyncSend(List<String> partialDevices, Message mess) {
		// make a copy
		final List<String> devices = new ArrayList<String>(partialDevices);
		final Message message = mess;// new Message.Builder().build();

		threadPool.execute(new Runnable() {

			public void run() {
				// Message message = new Message.Builder().build();
				MulticastResult multicastResult;
				try {
					multicastResult = data.sender.send(message, devices, 5);
				} catch (IOException e) {
					logger.log(Level.SEVERE, "Error posting messages", e);
					return;
				}
				List<Result> results = multicastResult.getResults();
				// analyze the results
				for (int i = 0; i < devices.size(); i++) {
					String regId = devices.get(i);
					Result result = results.get(i);
					String messageId = result.getMessageId();
					if (messageId != null) {
						logger.fine("Succesfully sent message to device: "
								+ regId + "; messageId = " + messageId);
						String canonicalRegId = result
								.getCanonicalRegistrationId();
						if (canonicalRegId != null) {
							// same device has more than on registration id:
							// update it
							logger.info("canonicalRegId " + canonicalRegId);
							Datastore.updateRegistration(regId, canonicalRegId);
						}
					} else {
						String error = result.getErrorCodeName();
						if (error.equals(Constants.ERROR_NOT_REGISTERED)) {
							// application has been removed from device -
							// unregister it
							logger.info("Unregistered device: " + regId);
							Datastore.unregister(regId);
						} else {
							logger.severe("Error sending message to " + regId
									+ ": " + error);
						}
					}
				}
			}
		});
	}*/

}
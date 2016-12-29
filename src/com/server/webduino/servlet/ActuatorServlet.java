package com.server.webduino.servlet;

import com.quartz.QuartzListener;
import com.server.webduino.core.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Logger;

/**
 * Created by Giacomo Span� on 08/11/2015.
 */
//@WebServlet(name = "SensorServlet")
public class ActuatorServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(ActuatorServlet.class.getName());

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {


        String id = request.getParameter("id");

        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");
        PrintWriter out = response.getWriter();

        Core core = (Core) getServletContext().getAttribute(QuartzListener.CoreClass);

        if (id != null) {

            Actuator actuator = core.getFromShieldId(Integer.valueOf(id), null);
            JSONObject json = actuator.getJson();
            out.print(json.toString());

        } else {

            ArrayList<Actuator> list = core.getActuators();
            //create Json Object
            JSONArray jsonarray = new JSONArray();
            Iterator<Actuator> iterator = list.iterator();
            while (iterator.hasNext()) {
                Actuator actuator = iterator.next();
                JSONObject json = actuator.getJson();
                jsonarray.put(json);
            }
            out.print(jsonarray.toString());
        }

    }


    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //questa servlet riceve command dalla app, dalle pagine wed e riceve status update dagli actuator diorettamente

        StringBuffer jb = new StringBuffer();
        String line = null;
        int actuatorId;

        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");
        PrintWriter out = response.getWriter();

        JSONObject jsonResult = new JSONObject();

        try {
            BufferedReader reader = request.getReader();
            while ((line = reader.readLine()) != null)
                jb.append(line);
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        Core core = (Core) getServletContext().getAttribute(QuartzListener.CoreClass);

        try {
            JSONObject json = new JSONObject(jb.toString());

            boolean remote = false, res = false;
            int duration = 0, sensorId = 0;
            double target = 0;
            actuatorId = json.getInt("id");
            String command = "";
            if (json.has("command"))
                command = json.getString("command");

            if (actuatorId > 0 && command != null) {

                if (command.equals("status")) { // receive status update
                    out.print(json.toString());
                    updateActuator(actuatorId, json);
                    response.setStatus(HttpServletResponse.SC_OK);
                    return;

                } else if (command.equals("start")) {
                    try {
                        if (json.has("duration"))
                            duration = json.getInt("duration");
                        if (json.has("target"))
                            target = json.getDouble("target");
                        if (json.has("sensorid"))
                            sensorId = json.getInt("sensorid");
                        if (json.has("remote"))
                            remote = json.getBoolean("remote");

                        HeaterActuatorCommand cmd = new HeaterActuatorCommand();
                        cmd.command = Actuator.Command_Manual_Auto;
                        cmd.duration = duration;
                        cmd.targetTemperature = target;
                        cmd.remoteSensor = remote;
                        cmd.activeProgramID = 0;
                        cmd.activeTimeRangeID = 0;
                        cmd.activeSensorID = sensorId;
                        cmd.activeSensorTemperature = 0;
                        new SendActuatorCommandThread(actuatorId, cmd).start();

                        res = true;
                        response.setStatus(HttpServletResponse.SC_OK);

                    } catch (JSONException e1) {
                        e1.printStackTrace();
                    }

                } else if (command.equals("stop")) {
                    //try {
                    if (json.has("duration"))
                        duration = json.getInt("duration");
                        /*if (json.has("target"))
                            target = json.getDouble("target");
                        if (json.has("sensorid"))
                            sensorId = json.getInt("sensorid");
                        if (json.has("remote"))
                            remote = json.getBoolean("remote");*/
                    //HeaterActuator actuator = (HeaterActuator) core.getFromId(actuatorId);
                    //res = actuator.sendCommand(Actuator.Command_Manual_End, duration, 0, true, 0, 0, 0, 0);

                    HeaterActuatorCommand cmd = new HeaterActuatorCommand();
                    cmd.command = Actuator.Command_Manual_End;
                    cmd.duration = 0;
                    cmd.targetTemperature = 0;
                    cmd.remoteSensor = false;
                    cmd.activeProgramID = 0;
                    cmd.activeTimeRangeID = 0;
                    cmd.activeSensorID = -1;
                    cmd.activeSensorTemperature = 0;
                    new SendActuatorCommandThread(actuatorId, cmd).start();

                    res = true;
                    response.setStatus(HttpServletResponse.SC_OK);

                } else {
                    LOGGER.severe("command not found");
                    res = false;
                }
            }

            try {

                if (res) { // questo dovrebbe essere messo a false se sono sbagliati i parametri del command
                    jsonResult.put("answer", "success");
                    /*jsonResult.put("id", actuatorId);
                    Actuator actuator = core.getFromId(actuatorId);
                    if (actuator == null)
                        LOGGER.severe("actuator == null");
                    JSONObject actuatorJson = actuator.getJson();
                    if (actuatorJson == null)
                        LOGGER.severe("actuator == null");
                    jsonResult.put("actuator", actuatorJson.toString());*/

                } else {

                    jsonResult.put("answer", "error");
                }
                // finally output the json string
                out.print(jsonResult.toString());

            } catch (JSONException e) {
                e.printStackTrace();

            }

        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);

            LOGGER.severe("BAD REQUEST");
            return;
        }


    }

    private void updateActuator(int id, JSONObject json) throws JSONException {

        LOGGER.info("SensorServlet:updateActuator - start");

        /*double avtemperature = json.getDouble("avtemperature");
        String status = json.getString("status");
        Boolean relestatus = json.getBoolean("relestatus");*/

        new UpdateActuatorThread(getServletContext(), json).start();

        LOGGER.info("SensorServlet:updateActuator - end");
    }
}

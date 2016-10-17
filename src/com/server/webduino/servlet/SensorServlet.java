package com.server.webduino.servlet;

import com.quartz.QuartzListener;
import com.server.webduino.core.*;
//import com.server.webduino.core.SensorData;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import sun.management.Sensor;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.logging.Logger;

/**
 * Created by Giacomo Span� on 08/11/2015.
 */
//@WebServlet(name = "SensorServlet")
public class SensorServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(SensorServlet.class.getName());

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        LOGGER.info("SensorServlet:doPost");



        StringBuffer jb = new StringBuffer();
        String line = null;

        try {
            BufferedReader reader = request.getReader();
            while ((line = reader.readLine()) != null)
                jb.append(line);
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        try {
            JSONObject jsonObj = new JSONObject(jb.toString());

            LOGGER.info("SensorServlet:doPost" + jb.toString());

            int id = jsonObj.getInt("id");
            double avTemperature = jsonObj.getDouble("avtemperature");
            double temperature = jsonObj.getDouble("temperature");


            out.println("<HTML>");
            out.println("<HEAD><TITLE>Hello World</TITLE></HEAD>");
            out.println("<BODY>");
            out.println("result=1");
            out.println("</BODY></HTML>");

            updateSensor(id, avTemperature, temperature);

            //}

        } catch (JSONException e) {
            e.printStackTrace();
            out.println("<HTML>");
            out.println("<HEAD><TITLE>Hello World</TITLE></HEAD>");
            out.println("<BODY>");
            out.println("result=-1");
            out.println("</BODY></HTML>");
        }
    }

    private void updateSensor(int id, double avTemperature, double temperature) {

        LOGGER.info("SensorServlet:updateSensor - start");
        new UpdateSensorThread(getServletContext(),id,avTemperature,temperature).start();

        LOGGER.info("SensorServlet:updateSensor - end");
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String id = request.getParameter("id");

        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");
        PrintWriter out = response.getWriter();

        //ArrayList<TemperatureSensor> list = Core.getSensors();

        Core core = (Core)getServletContext().getAttribute(QuartzListener.CoreClass);


        //create Json Object
        JSONArray jsonarray = new JSONArray();

        if (id != null) {

            TemperatureSensor sensor = core.getSensorFromId(Integer.valueOf(id));
            JSONObject json = sensor.getJson();
            out.print(json.toString());

        } else {

            ArrayList<TemperatureSensor> list = core.getSensors();
                Iterator<TemperatureSensor> iterator = list.iterator();
                while (iterator.hasNext()) {
                    TemperatureSensor sd = iterator.next();
                    JSONObject json = sd.getJson();
                    /*JSONObject json = new JSONObject();
                    json.put("id", sd.getId());
                    json.put("temperature", sd.getTemperature());
                    json.put("avtemperature", sd.getAvTemperature());
                    json.put("url", sd.getUrl());
                    json.put("humidity", sd.humidity);
                    json.put("name", sd.getName());
                    json.put("pressure", sd.pressure);
                    json.put("lastupdate", sd.getLastUpdate());*/

                    jsonarray.put(json);
                }


            // finally output the json string
            out.print(jsonarray.toString());
        }
    }
}

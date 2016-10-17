package com.server.webduino.core;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.*;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SensorBase {

    private static Logger LOGGER = Logger.getLogger(SensorBase.class.getName());

    protected URL url;
    protected int id;
    protected String name; // valore letto dal db
    protected String boardName; // valore letto dalla board (non dal db)

    public URL getUrl() {
        return url;
    }

    public void setUrl(URL url) {
        this.url = url;
    }

    protected Date lastUpdate;

    protected String jsonResultSring = "";
    URL mURL;

    public Date getLastUpdate() {
        return lastUpdate;
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



    public SensorBase(URL url) {
        mURL = url;
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

            String ret = call("GET", "", "/status");
            if (ret != null)
                return ret;

            for (int i = 0; i < 2; i++) {

                LOGGER.log(Level.WARNING, "retry..." + (i + 1));
                ret = call("GET", "", "/status");
                if (ret != null)
                    return ret;
            }
        LOGGER.info("end updateStatus");
        return null;
    }

    protected String call(String method, String param, String path) {

        LOGGER.info("call: " + method + "," + param + "," + path);

        String result = "";
        //synchronized (this) {//synchronized block
            //LOGGER.info("synchronized block start: " + method + "," + param + "," + path);

            if (method.equals("GET"))
                result = callGet(param, path);
            else if (method.equals("POST"))
                result = callPost(param, path);
            //LOGGER.info("synchronized block end: " + result);
        //}

        LOGGER.info("end call");
        return result;
    }


    protected /*synchronized*/ String callGet(String param, String path) {

        InputStreamReader responseInputStream;
        try {
            URL jsonurl = new URL(mURL.toString() + path);

            HttpURLConnection mConnection = (HttpURLConnection) jsonurl.openConnection();

            mConnection.setDoOutput(false);
            mConnection.setRequestProperty("Content-Type", "application/json");
            mConnection.setRequestMethod("GET");
            mConnection.setConnectTimeout(2000); //set timeout to 10 seconds

            int res = mConnection.getResponseCode();
            if (res == HttpURLConnection.HTTP_OK) {


                responseInputStream = new InputStreamReader(
                        mConnection.getInputStream());
                BufferedReader rd = new BufferedReader(responseInputStream);
                String line = "";
                jsonResultSring = "";
                while ((line = rd.readLine()) != null) {
                    jsonResultSring += line;
                }
                responseInputStream.close();

                JSONObject json = new JSONObject(jsonResultSring);
                updateFromJson(json);
                return jsonResultSring;


            } else {
                // Server returned HTTP error code.
                LOGGER.severe("Server returned HTTP error code" + res);
                return null;
            }

        } catch (MalformedURLException e) {
            LOGGER.severe("error: MalformedURLException" + e.toString());
            //e.printStackTrace();
            return null;
        } catch (NoRouteToHostException e) {
            LOGGER.severe("error: NoRouteToHostException" + e.toString());
            //e.printStackTrace();
            return null;
        } catch (SocketTimeoutException e) {
            LOGGER.severe("error: SocketTimeoutException" + e.toString());
            //e.printStackTrace();
            return null;
        } catch (Exception e) {
            LOGGER.severe("error: Exception" + e.toString());
            //e.printStackTrace();
            return null;
        }
        //LOGGER.info("jsonResultSring = " + jsonResultSring);
        //return jsonResultSring;

    }

    protected /*synchronized*/ String callPost(String path, String urlParameters) {
        String htmlResultString;

        //LOGGER.info("callPost ..");
        try {
            URL jsonurl = new URL(mURL.toString() + path);

            HttpURLConnection connection = (HttpURLConnection) jsonurl.openConnection();
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "text/html");
            connection.setRequestMethod("POST");
            connection.setConnectTimeout(2000); //set timeout to 10 seconds
            connection.setInstanceFollowRedirects(false);
            connection.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
            wr.writeBytes(urlParameters);
            wr.flush();
            wr.close();

            InputStreamReader reader = new InputStreamReader(connection.getInputStream());
            BufferedReader rd = new BufferedReader(reader);
            String line = "";
            htmlResultString = "";
            while ((line = rd.readLine()) != null) {
                htmlResultString += line;
            }

            reader.close();
            int res = connection.getResponseCode();

            if (res == HttpURLConnection.HTTP_OK) {
                // OK
                return htmlResultString;
            } else {
                // Server returned HTTP error code.
                LOGGER.severe("Server returned HTTP error code" + res);
                return null;
            }

        } catch (MalformedURLException e) {
            LOGGER.severe("error: MalformedURLException" + e.toString());
            //e.printStackTrace();
            return null;
        } catch (NoRouteToHostException e) {
            LOGGER.severe("error: NoRouteToHostException" + e.toString());
            //e.printStackTrace();
            return null;
        } catch (SocketTimeoutException e) {
            LOGGER.severe("error: SocketTimeoutException" + e.toString());
            //e.printStackTrace();
            return null;
        } catch (Exception e) {
            LOGGER.severe("error: Exception" + e.toString());
            //e.printStackTrace();
            return null;
        }
    }

    void updateFromJson(JSONObject json) {
    }

    public JSONObject getJson() {

        return null;
    }


}

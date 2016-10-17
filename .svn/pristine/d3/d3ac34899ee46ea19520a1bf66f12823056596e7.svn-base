package com.server.webduino.core;

import java.sql.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class DataLog {
    public Date date = new Date();
    public Date time = new Date();

    public Date getDatetime() {

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        try {
            SimpleDateFormat dfDate = new SimpleDateFormat("yyyyy-MM-dd");
            SimpleDateFormat dfTime = new SimpleDateFormat("HH:mm:ss");
            String datestr = dfDate.format(this.date) + " " + dfTime.format(this.time);
            date = format.parse(datestr);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
        return date;
    }

    public void writelog(int id, Date date, HeaterActuator heater/*boolean releStatus*/) {


    }
    public ArrayList<DataLog> getDataLog(int id, Date startDate, Date endDate) {
        return null;
    }

    DataLog getInterpolatedDataLog(Date t, DataLog dataA, DataLog dataB)
    {

        return this;
    }
}

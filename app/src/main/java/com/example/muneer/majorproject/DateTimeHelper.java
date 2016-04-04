package com.example.muneer.majorproject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by Muneer on 27-03-2016.
 */
public class DateTimeHelper {

    String getDateInWords(String inputDateString) {
        String dateInWords = "";

        DateFormat displayFormat = new SimpleDateFormat("EEE, MMM d, yyyy");
        displayFormat.setTimeZone(TimeZone.getTimeZone("Asia/Calcutta"));

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Date inputDateObject = new Date();
        try {
            inputDateObject = df.parse(inputDateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        dateInWords = displayFormat.format(inputDateObject).toString();

        return dateInWords;
    }

    String get12HourTime(String inputTimeString) {

        String timeIn12HourFormat = "";

        try {
            String _24HourTime = inputTimeString;
            SimpleDateFormat _24HourSDF = new SimpleDateFormat("HH:mm");
            SimpleDateFormat _12HourSDF = new SimpleDateFormat("hh:mm a");
            Date _24HourDt = _24HourSDF.parse(_24HourTime);
            //System.out.println(_24HourDt);
            timeIn12HourFormat = _12HourSDF.format(_24HourDt);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return timeIn12HourFormat;
    }

}

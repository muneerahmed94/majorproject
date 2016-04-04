package com.example.muneer.majorproject;

/**
 * Created by Muneer on 06-03-2016.
 */
public class SharedRide
{
    String from, to, date, time;

    public SharedRide(String from, String to, String date, String time)
    {
        this.setFrom(from);
        this.setTo(to);
        this.setDate(date);
        this.setTime(time);
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}

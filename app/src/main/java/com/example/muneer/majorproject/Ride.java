package com.example.muneer.majorproject;

import java.sql.Time;
import java.util.Date;

/**
 * Created by Muneer on 23-02-2016.
 */
public class Ride
{
    String rideId;
    String email;
    String mobile;
    String name;
    String from;
    String to;
    String date;
    String time;
    String carNo;
    String carName;
    int noOfSeats;
    int price;

    public Ride(String email, String mobile,String name, String from, String to, String date, String time, String carNo, String carName, int noOfSeats, int price)
    {
        rideId = "";
        this.email = email;
        this.mobile = mobile;
        this.name = name;
        this.from = from;
        this.to = to;
        this.date = date;
        this.time = time;
        this.carNo = carNo;
        this.carName = carName;
        this.noOfSeats = noOfSeats;
        this.price = price;
    }
}

package com.example.muneer.majorproject;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;

public class ShareRideActivity extends AppCompatActivity {

    TextView TV_WELCOME;
    UserLocalStore userLocalStore;

    EditText ET_FROM, ET_TO, ET_DATE, ET_TIME, ET_CAR_NO, ET_CAR_NAME, ET_NO_OF_SEATS, ET_PRICE;

    String from, to, date, time, carNo, carName, currentUserEmail, currentUserName, currentUserMobile;
    int noOfSeats, price;

    Integer year_x, month_x, day_x;
    Integer hour_x, minute_x;
    String dateFromCal, timeFromClock, insertDate, insertTime;

    static final int DIALOG_ID_DATE = 0, DIALOG_ID_TIME = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_ride);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final Calendar calendar = Calendar.getInstance();
        year_x = calendar.get(Calendar.YEAR);
        month_x = calendar.get(Calendar.MONTH);
        day_x = calendar.get(Calendar.DAY_OF_MONTH);
        hour_x = calendar.get(Calendar.HOUR_OF_DAY);
        minute_x = calendar.get(Calendar.MINUTE);


        TV_WELCOME =(TextView) findViewById(R.id.textViewWelcome);
        userLocalStore = new UserLocalStore(this);

        ET_FROM = (EditText) findViewById(R.id.editTextFrom);
        ET_TO = (EditText) findViewById(R.id.editTextTo);
        ET_DATE = (EditText) findViewById(R.id.editTextDate);
        ET_TIME = (EditText) findViewById(R.id.editTextTime);
        ET_CAR_NO = (EditText) findViewById(R.id.editTextCarNo);
        ET_CAR_NAME = (EditText) findViewById(R.id.editTextCarName);
        ET_NO_OF_SEATS = (EditText) findViewById(R.id.editTextNoOfSeats);
        ET_PRICE = (EditText) findViewById(R.id.editTextPrice);

        ET_DATE.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) {
                    showDialog(DIALOG_ID_DATE);
                }
            }
        });

        ET_TIME.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) {
                    showDialog(DIALOG_ID_TIME);
                }
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (authenticate() == true) {
            displayUserDetails();
        }
    }

    public boolean authenticate()
    {
        if(userLocalStore.getLoggedInUser() == null)
        {
            Intent loginIntent = new Intent(this, LoginActivity.class);
            startActivity(loginIntent);
            return false;
        }
        return true;
    }

    public void displayUserDetails()
    {
        User user = userLocalStore.getLoggedInUser();
        String welcome = "Welcome " + user.name;
        currentUserEmail = user.email;
        currentUserMobile = user.mobile;
        currentUserName = user.name;
        TV_WELCOME.setText(welcome);
    }

    public void logoutSR(View v)
    {
        userLocalStore.clearUserData();
        userLocalStore.setUserLoggedIn(false);
        Intent loginIntent = new Intent(this, LoginActivity.class);
        startActivity(loginIntent);
    }

    public void shareRide(View v)
    {
        Ride ride = createRide();
        storeRide(ride);
    }

    public void storeRide(Ride ride)
    {
        ServerRequests serverRequest = new ServerRequests(this);
        serverRequest.storeRideInBackground(ride, new GetRideCallback() {
            @Override
            public void done(Ride returnedRide) {
                Intent welcomeIntent = new Intent(ShareRideActivity.this, WelcomeActivity.class);
                startActivity(welcomeIntent);
            }
        });
    }

    Ride createRide()
    {
        from = ET_FROM.getText().toString();
        to = ET_TO.getText().toString();
        date = insertDate;
        time = insertTime;
        carNo = ET_CAR_NO.getText().toString();
        carName = ET_CAR_NAME.getText().toString();

        if(ET_NO_OF_SEATS.getText().toString().length() > 0)
            noOfSeats = Integer.parseInt(ET_NO_OF_SEATS.getText().toString());
        else
            noOfSeats = 0;

        if(ET_PRICE.getText().toString().length() > 0)
            price = Integer.parseInt(ET_PRICE.getText().toString());
        else
            price = 0;

        Ride ride = new Ride(currentUserEmail, currentUserMobile, currentUserName, from, to, date, time, carNo, carName, noOfSeats, price);
        return ride;
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        if(id == DIALOG_ID_DATE)
            return new DatePickerDialog(ShareRideActivity.this, datePickerListener, year_x, month_x, day_x);
        else if(id == DIALOG_ID_TIME)
            return new TimePickerDialog(ShareRideActivity.this, timePickerListener, hour_x, minute_x, false);
        return null;
    }

    protected DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener(){
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            year_x = year;
            month_x = monthOfYear + 1;
            day_x = dayOfMonth;

            dateFromCal = year_x.toString() + "-" + month_x.toString() + "-" + day_x.toString();

            insertDate = dateFromCal;

            ET_DATE.setText(new DateTimeHelper().getDateInWords(dateFromCal));
        }
    };

    protected TimePickerDialog.OnTimeSetListener timePickerListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            hour_x = hourOfDay;
            minute_x = minute;

            timeFromClock = hour_x.toString() + ":" + minute_x;
            insertTime = timeFromClock;

            ET_TIME.setText(new DateTimeHelper().get12HourTime(timeFromClock));
        }
    };
}

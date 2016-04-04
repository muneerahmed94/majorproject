package com.example.muneer.majorproject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

public class MyRidesDetailsActivity extends AppCompatActivity {

    TextView TV_WELCOME;
    UserLocalStore userLocalStore;

    String json_string;
    int position;

    String email;
    String mobile;
    String name;
    String from;
    String to;
    String date;
    String time;
    String carNo;
    String carName;
    int noOfSeatsBooked, noOfSeatsLeft, noOfSeatsShared;
    int price;
    String status;
    String noOfSeatsNew;

    String RideID, PassengerName, PassengerEmail, PassengerMobile;

    Button buttonCancelRide, buttonStartRide;
    ProgressDialog progressDialog;

    JSONObject jsonObject;
    JSONArray jsonArray;

    KeyValueAdapter keyValueAdapter;
    ListView listViewRideDetailsMyRides;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ride_details_my_rides);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        TV_WELCOME =(TextView) findViewById(R.id.textViewWelcome);

        userLocalStore = new UserLocalStore(this);

        progressDialog = new ProgressDialog(MyRidesDetailsActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Processing...");
        progressDialog.setMessage("Please wait...");

        buttonCancelRide = (Button) findViewById(R.id.buttonCancelRide);
        buttonCancelRide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(email.equals(userLocalStore.getLoggedInUser().email)) { //if the user is driver
                    String message = name + " just cancelled the ride from " + from + " to " + to + "\nRide Date: " + new DateTimeHelper().getDateInWords(date) + "\nRide Time: " + new DateTimeHelper().get12HourTime(time) + "\nMobile: " + userLocalStore.getLoggedInUser().mobile;
                    new BackgroundTaskCancelRideDriver(RideID, message).execute();
                }
                else {
                    String message = userLocalStore.getLoggedInUser().name + " just cancelled the ride from " + from + " to " + to + "\nRide Date: " + new DateTimeHelper().getDateInWords(date) + "\nRide Time: " + new DateTimeHelper().get12HourTime(time) + "\nMobile: " + userLocalStore.getLoggedInUser().mobile;
                    new BackgroundTaskCancelRidePassenger(RideID, message, noOfSeatsNew).execute();
                }
            }
        });

        buttonStartRide = (Button) findViewById(R.id.buttonStartRide);
        buttonStartRide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = name + " just started the ride from " + from + " to " + to + "\nRide Date: " + new DateTimeHelper().getDateInWords(date) + "\nRide Time: " + new DateTimeHelper().get12HourTime(time) + "\nMobile: " + userLocalStore.getLoggedInUser().mobile;
                new BackgroundTaskStartRide(RideID, message).execute();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (authenticate() == true) {
            displayUserDetails();
            displayRideDetails();
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
        TV_WELCOME.setText(welcome);

        PassengerName = user.name;
        PassengerEmail = user.email;
        PassengerMobile = user.mobile;
    }

    public void displayRideDetails()
    {
        position = getIntent().getExtras().getInt("position");
        json_string = getIntent().getExtras().getString("json_string");
        JSONArray jsonArray = new JSONHelper().createJSONArray(json_string);
        JSONObject jsonObject = null;
        try {
            jsonObject = jsonArray.getJSONObject(position);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            RideID = jsonObject.getString("RideID");
            name = jsonObject.getString("DriverName");
            email = jsonObject.getString("DriverEmail");
            mobile = jsonObject.getString("DriverMobile");
            from = jsonObject.getString("From");
            to = jsonObject.getString("To");
            date = jsonObject.getString("Date");
            time = jsonObject.getString("Time");
            carNo = jsonObject.getString("CarNo");
            carName = jsonObject.getString("CarName");
            noOfSeatsBooked = Integer.parseInt(jsonObject.getString("NoOfSeatsBooked"));
            noOfSeatsLeft = Integer.parseInt(jsonObject.getString("NoOfSeats"));
            noOfSeatsNew = new Integer(noOfSeatsBooked + noOfSeatsLeft).toString();
            noOfSeatsShared = new Integer(jsonObject.getString("NoOfSeatsShared"));
            price = Integer.parseInt(jsonObject.getString("Price"));
            status = jsonObject.getString("Status");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        keyValueAdapter = new KeyValueAdapter(this, R.layout.key_value_layout);

        listViewRideDetailsMyRides = (ListView) findViewById(R.id.listViewInRideDetailsMyRides);
        listViewRideDetailsMyRides.setAdapter(keyValueAdapter);

        KeyValue keyValue1 = new KeyValue("Driver Name", name);
        keyValueAdapter.add(keyValue1);

        KeyValue keyValue2 = new KeyValue("Driver Email", email);
        keyValueAdapter.add(keyValue2);

        KeyValue keyValue3 = new KeyValue("Driver Mobile", mobile);
        keyValueAdapter.add(keyValue3);

        KeyValue keyValue4 = new KeyValue("From", from);
        keyValueAdapter.add(keyValue4);

        KeyValue keyValue5 = new KeyValue("To", to);
        keyValueAdapter.add(keyValue5);

        KeyValue keyValue6 = new KeyValue("Date", new DateTimeHelper().getDateInWords(date));
        keyValueAdapter.add(keyValue6);

        KeyValue keyValue7 = new KeyValue("Time", new DateTimeHelper().get12HourTime(time));
        keyValueAdapter.add(keyValue7);

        KeyValue keyValue8 = new KeyValue("Car No", carNo);
        keyValueAdapter.add(keyValue8);

        KeyValue keyValue9 = new KeyValue("Car Name", carName);
        keyValueAdapter.add(keyValue9);

        if(noOfSeatsShared != 0) {
            KeyValue keyValue10 = new KeyValue("Seats Shared", new Integer(noOfSeatsShared).toString());
            keyValueAdapter.add(keyValue10);

            KeyValue keyValue12 = new KeyValue("Seats Left", new Integer(noOfSeatsLeft).toString());
            keyValueAdapter.add(keyValue12);
        }
        else {
            KeyValue keyValue10 = new KeyValue("Seats Booked", new Integer(noOfSeatsBooked).toString());
            keyValueAdapter.add(keyValue10);
        }

        KeyValue keyValue11 = new KeyValue("Price", new Integer(price).toString());
        keyValueAdapter.add(keyValue11);

        if(status.equals("complete")) {
            buttonStartRide.setVisibility(View.INVISIBLE);
            buttonCancelRide.setVisibility(View.INVISIBLE);
        }
        else {
            if(!email.equals(userLocalStore.getLoggedInUser().email)) { //do not show start ride button if the user is not the driver
                buttonStartRide.setVisibility(View.INVISIBLE);
            }
        }
    }

    public void logoutRDMR(View v)
    {
        userLocalStore.clearUserData();
        userLocalStore.setUserLoggedIn(false);
        Intent loginIntent = new Intent(this, LoginActivity.class);
        startActivity(loginIntent);
    }

    public class BackgroundTaskStartRide extends AsyncTask<Void, Void, String> {

        String RideID, message;
        String json_url, json_string;

        public BackgroundTaskStartRide(String RideID, String message) {
            this.RideID = RideID;
            this.message = message;
        }

        @Override
        protected void onPreExecute() {
            json_url = "http://majorproject-jntuhceh.rhcloud.com/myrides/start_ride.php";
            progressDialog.show();
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                URL url = new URL(json_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                OutputStream os = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter((new OutputStreamWriter(os, "UTF-8")));
                String data = URLEncoder.encode("RideID", "UTF-8") + "=" + URLEncoder.encode(RideID, "UTF-8");
                bufferedWriter.write(data);
                bufferedWriter.flush();
                bufferedWriter.close();
                os.close();

                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder stringBuilder = new StringBuilder();
                while((json_string = bufferedReader.readLine()) != null)
                {
                    stringBuilder.append(json_string + "\n");
                }
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();
                return stringBuilder.toString().trim();
            }
            catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String json_string) {
            progressDialog.dismiss();

            ArrayList<String> receivers = new ArrayList<String>();

            //String print = "";

            try {
                jsonObject = new JSONObject(json_string);
                jsonArray = jsonObject.getJSONArray("server_response");
                int count = 0;
                while(count < jsonArray.length())
                {
                    JSONObject jo = jsonArray.getJSONObject(count);
                    receivers.add(jo.getString("PassengerMobile"));
                    //print += jo.getString("PassengerMobile") + "\n";
                    count++;
                }
            }catch (JSONException e) {
                e.printStackTrace();
            }

            Toast.makeText(MyRidesDetailsActivity.this, "Ride started and the members(if any) have been notified via SMS", Toast.LENGTH_LONG).show();
            new SMSSender().send(message, receivers);
        }
    }

    public class BackgroundTaskCancelRideDriver extends AsyncTask<Void, Void, String> {

        String RideID, message;
        String json_url, json_string;

        public BackgroundTaskCancelRideDriver(String RideID, String message) {
            this.RideID = RideID;
            this.message = message;
        }

        @Override
        protected void onPreExecute() {
            json_url = "http://majorproject-jntuhceh.rhcloud.com/myrides/cancel_ride_driver.php";
            progressDialog.show();
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                URL url = new URL(json_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                OutputStream os = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter((new OutputStreamWriter(os, "UTF-8")));
                String data = URLEncoder.encode("RideID", "UTF-8") + "=" + URLEncoder.encode(RideID, "UTF-8");
                bufferedWriter.write(data);
                bufferedWriter.flush();
                bufferedWriter.close();
                os.close();

                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder stringBuilder = new StringBuilder();
                while((json_string = bufferedReader.readLine()) != null)
                {
                    stringBuilder.append(json_string + "\n");
                }
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();
                return stringBuilder.toString().trim();
            }
            catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String json_string) {
            progressDialog.dismiss();

            ArrayList<String> receivers = new ArrayList<String>();

            //String print = "";

            try {
                jsonObject = new JSONObject(json_string);
                jsonArray = jsonObject.getJSONArray("server_response");
                int count = 0;
                while(count < jsonArray.length())
                {
                    JSONObject jo = jsonArray.getJSONObject(count);
                    receivers.add(jo.getString("PassengerMobile"));
                    //print += jo.getString("PassengerMobile") + "\n";
                    count++;
                }
            }catch (JSONException e) {
                e.printStackTrace();
            }

            Toast.makeText(MyRidesDetailsActivity.this, "Ride cancelled and the members(if any) have been notified via SMS", Toast.LENGTH_LONG).show();
            new SMSSender().send(message, receivers);
            Intent intent = new Intent(MyRidesDetailsActivity.this, WelcomeActivity.class);
            startActivity(intent);
        }
    }

    public class BackgroundTaskCancelRidePassenger extends AsyncTask<Void, Void, String> {

        String RideID, message, noOfSeatNew;
        String json_url, json_string;


        public BackgroundTaskCancelRidePassenger(String RideID, String message, String noOfSeatsNew) {
            this.noOfSeatNew = noOfSeatsNew;
            this.RideID = RideID;
            this.message = message;
        }

        @Override
        protected void onPreExecute() {
            json_url = "http://majorproject-jntuhceh.rhcloud.com/myrides/cancel_ride_passenger.php";
            progressDialog.show();
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                URL url = new URL(json_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                OutputStream os = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter((new OutputStreamWriter(os, "UTF-8")));
                String data = URLEncoder.encode("RideID", "UTF-8") + "=" + URLEncoder.encode(RideID, "UTF-8") + "&" +
                                URLEncoder.encode("NoOfSeatsNew", "UTF-8") + "=" + URLEncoder.encode(noOfSeatNew, "UTF-8") + "&" +
                                URLEncoder.encode("PassengerEmail", "UTF-8") + "=" + URLEncoder.encode(userLocalStore.getLoggedInUser().email, "UTF-8");
                bufferedWriter.write(data);
                bufferedWriter.flush();
                bufferedWriter.close();
                os.close();

                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder stringBuilder = new StringBuilder();
                while((json_string = bufferedReader.readLine()) != null)
                {
                    stringBuilder.append(json_string + "\n");
                }
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();
                return null;
            }
            catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String json_string) {
            progressDialog.dismiss();

            ArrayList<String> receivers = new ArrayList<String>();
            receivers.add(mobile);

            Toast.makeText(MyRidesDetailsActivity.this, "Ride cancelled and the driver has been notified via SMS", Toast.LENGTH_LONG).show();
            new SMSSender().send(message, receivers);
            Intent intent = new Intent(MyRidesDetailsActivity.this, WelcomeActivity.class);
            startActivity(intent);
        }
    }
}

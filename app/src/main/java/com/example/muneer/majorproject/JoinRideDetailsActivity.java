package com.example.muneer.majorproject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
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

public class JoinRideDetailsActivity extends AppCompatActivity {

    TextView TV_WELCOME, TV_NAME, TV_EMAIL, TV_MOBILE, TV_FROM, TV_TO, TV_DATE, TV_TIME, TV_CAR_NO, TV_CAR_NAME, TV_NO_OF_SEATS, TV_PRICE;
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
    int noOfSeats, noOfSeatsSelected;
    int price;

    EditText ET_NO_OF_SEATS;

    String RideID, NoOfSeatsNew, PassengerName, PassengerEmail, PassengerMobile, NoOfSeatsBooked;

    ProgressDialog progressDialog;

    KeyValueAdapter keyValueAdapter;
    ListView listViewRideDetailsMyRides;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ride_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        TV_WELCOME =(TextView) findViewById(R.id.textViewWelcome);
        ET_NO_OF_SEATS =(EditText) findViewById(R.id.editTextNoOfSeats);

        userLocalStore = new UserLocalStore(this);

        progressDialog = new ProgressDialog(JoinRideDetailsActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Processing...");
        progressDialog.setMessage("Please wait...");
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
            noOfSeats = Integer.parseInt(jsonObject.getString("NoOfSeats"));
            price = Integer.parseInt(jsonObject.getString("Price"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        keyValueAdapter = new KeyValueAdapter(this, R.layout.key_value_layout);

        listViewRideDetailsMyRides = (ListView) findViewById(R.id.listViewInRideDetails);
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

        KeyValue keyValue10 = new KeyValue("No of Seats", new Integer(noOfSeats).toString());
        keyValueAdapter.add(keyValue10);

        KeyValue keyValue11 = new KeyValue("Price", new Integer(price).toString());
        keyValueAdapter.add(keyValue11);
    }

    public void logoutRD(View v)
    {
        userLocalStore.clearUserData();
        userLocalStore.setUserLoggedIn(false);
        Intent loginIntent = new Intent(this, LoginActivity.class);
        startActivity(loginIntent);
    }

    public void onClickJoinRide(View v)
    {
        NoOfSeatsBooked = ET_NO_OF_SEATS.getText().toString();
        if(Integer.parseInt(NoOfSeatsBooked) <= noOfSeats)
        {
            NoOfSeatsNew = new Integer(noOfSeats - Integer.parseInt(NoOfSeatsBooked)).toString();
            new JoinRideUpdate().execute();
        }
        else
        {
            Toast.makeText(JoinRideDetailsActivity.this, "Only " + noOfSeats + " seats are available", Toast.LENGTH_SHORT).show();
        }
    }

    public class JoinRideUpdate extends AsyncTask<Void, Void, String>
    {
        String my_url;

        @Override
        protected void onPreExecute() {
            my_url = "https://majorproject-jntuhceh.rhcloud.com/joinride/join_ride_update.php";
            progressDialog.show();
        }

        @Override
        protected String doInBackground(Void... params)
        {
            try
            {
                URL url = new URL(my_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                OutputStream os = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter((new OutputStreamWriter(os, "UTF-8")));
                String data = URLEncoder.encode("RideID", "UTF-8") + "=" + URLEncoder.encode(RideID, "UTF-8") + "&" +
                        URLEncoder.encode("NoOfSeatsNew", "UTF-8") + "=" + URLEncoder.encode(NoOfSeatsNew, "UTF-8") + "&" +
                        URLEncoder.encode("PassengerName", "UTF-8") + "=" + URLEncoder.encode(PassengerName, "UTF-8") + "&" +
                        URLEncoder.encode("PassengerMobile", "UTF-8") + "=" + URLEncoder.encode(PassengerMobile, "UTF-8") + "&" +
                        URLEncoder.encode("NoOfSeatsBooked", "UTF-8") + "=" + URLEncoder.encode(NoOfSeatsBooked, "UTF-8") + "&" +
                        URLEncoder.encode("PassengerEmail", "UTF-8") + "=" + URLEncoder.encode(PassengerEmail, "UTF-8");
                bufferedWriter.write(data);
                bufferedWriter.flush();
                bufferedWriter.close();
                os.close();

                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream,"iso-8859-1"));
                String response = "";
                String line = "";
                while ((line = bufferedReader.readLine())!=null)
                {
                    response += line;
                }
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();
                return response;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            progressDialog.dismiss();
            if(result.equals(" Join Ride Successfull!"))
            {
                Toast.makeText(JoinRideDetailsActivity.this, "Join Ride Successful and the Driver has been Notified via SMS", Toast.LENGTH_LONG).show();

                SMSSender smsSender = new SMSSender();
                ArrayList<String> receivers = new ArrayList<>();
                receivers.add(mobile);
                String message = userLocalStore.getLoggedInUser().name + " just joined the ride from " + from + " to "+ to + "\nNo of Seats Booked: " + NoOfSeatsBooked + "\nRide Date: " + new DateTimeHelper().getDateInWords(date) + "\nRide Time: " + new DateTimeHelper().get12HourTime(time) + "\nMobile: " + userLocalStore.getLoggedInUser().mobile;
                smsSender.send(message, receivers);

                Intent intent = new Intent(JoinRideDetailsActivity.this, WelcomeActivity.class);
                startActivity(intent);

            }
            else
                Toast.makeText(JoinRideDetailsActivity.this, "Unable to join ride", Toast.LENGTH_LONG).show();
        }
    }
}

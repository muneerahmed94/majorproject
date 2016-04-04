package com.example.muneer.majorproject;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JoinRideActivity extends AppCompatActivity {

    TextView TV_WELCOME;
    UserLocalStore userLocalStore;

    String json_string, currentUserEmail;
    JSONObject jsonObject;
    JSONArray jsonArray;
    RideAdapter rideAdapter;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_ride);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        TV_WELCOME =(TextView) findViewById(R.id.textViewWelcome);
        userLocalStore = new UserLocalStore(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (authenticate() == true) {
            displayUserDetails();
            displayRides();
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
        TV_WELCOME.setText(welcome);
    }

    public void logoutJR(View v)
    {
        userLocalStore.clearUserData();
        userLocalStore.setUserLoggedIn(false);
        Intent loginIntent = new Intent(this, LoginActivity.class);
        startActivity(loginIntent);
    }

    public void displayRides()
    {
        json_string = getIntent().getExtras().getString("json_data");
        rideAdapter = new RideAdapter(this, R.layout.row_layout);
        listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(rideAdapter);

        try {
            jsonObject = new JSONObject(json_string);
            jsonArray = jsonObject.getJSONArray("server_response");

            int count = 0;
            String from, to, date, time;
            while(count < jsonArray.length())
            {
                JSONObject jo = jsonArray.getJSONObject(count);
                from = jo.getString("From");
                to = jo.getString("To");
                date = jo.getString("Date");
                time = jo.getString("Time");

                SharedRide sharedRide = new SharedRide(from, to, date, time);
                rideAdapter.add(sharedRide);
                count++;
            }
        }catch (JSONException e) {
            e.printStackTrace();
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                JSONObject jo = null;
                String RIDE_ID = "";
                try {
                    jo = jsonArray.getJSONObject(position);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    RIDE_ID = jo.getString("RideID");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Intent intent = new Intent(JoinRideActivity.this, JoinRideDetailsActivity.class);
                intent.putExtra("position", position);
                intent.putExtra("RideID",RIDE_ID);
                intent.putExtra("json_string", json_string);
                startActivity(intent);
            }
        });
    }
}

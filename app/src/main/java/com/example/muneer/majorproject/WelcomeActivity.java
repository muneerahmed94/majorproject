package com.example.muneer.majorproject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

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

public class WelcomeActivity extends AppCompatActivity {

    TextView TV_WELCOME;
    UserLocalStore userLocalStore;

    String JSON_STRING, json_string,currentUserEmail;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        TV_WELCOME =(TextView) findViewById(R.id.textViewWelcome);
        userLocalStore = new UserLocalStore(this);

        progressDialog = new ProgressDialog(WelcomeActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Processing...");
        progressDialog.setMessage("Please wait...");
    }

    public void goToJoinRide(View v)
    {
        new BackgroundTaskJoinRide().execute();
    }

    public void goToShareRide(View v)
    {
        Intent intent = new Intent(this, ShareRideActivity.class);
        startActivity(intent);
    }

    public void goToMyRides(View v)
    {
        new BackgroundTaskMyRides().execute();
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
        TV_WELCOME.setText(welcome);
    }

    public void logout(View v)
    {
        userLocalStore.clearUserData();
        userLocalStore.setUserLoggedIn(false);
        Intent loginIntent = new Intent(this, LoginActivity.class);
        startActivity(loginIntent);
    }

    class BackgroundTaskJoinRide extends AsyncTask<Void, Void, String>
    {
        String json_url;

        @Override
        protected void onPreExecute() {
            json_url = "https://majorproject-jntuhceh.rhcloud.com/joinride/json_get_data.php";
            progressDialog.show();
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                URL url = new URL(json_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder stringBuilder = new StringBuilder();
                while((JSON_STRING = bufferedReader.readLine()) != null)
                {
                    stringBuilder.append(JSON_STRING + "\n");
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
        protected void onPostExecute(String result) {
            //Toast.makeText(WelcomeActivity.this, result, Toast.LENGTH_LONG).show();
            progressDialog.dismiss();
            json_string = result;

            if(json_string == null)
                Toast.makeText(WelcomeActivity.this, "First get json", Toast.LENGTH_SHORT).show();
            else
            {
                Intent intent = new Intent(WelcomeActivity.this, JoinRideActivity.class);
                intent.putExtra("json_data", json_string);
                startActivity(intent);
            }
        }
    }

    class BackgroundTaskMyRides extends AsyncTask<Void, Void, String>
    {
        String json_url;

        @Override
        protected void onPreExecute() {
            json_url = "https://majorproject-jntuhceh.rhcloud.com/myrides/json_get_data.php";
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
                String data = URLEncoder.encode("email", "UTF-8") + "=" + URLEncoder.encode(currentUserEmail, "UTF-8");
                bufferedWriter.write(data);
                bufferedWriter.flush();
                bufferedWriter.close();
                os.close();

                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder stringBuilder = new StringBuilder();
                while((JSON_STRING = bufferedReader.readLine()) != null)
                {
                    stringBuilder.append(JSON_STRING + "\n");
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
        protected void onPostExecute(String result) {
            //Toast.makeText(WelcomeActivity.this, result, Toast.LENGTH_LONG).show();
            progressDialog.dismiss();
            json_string = result;

            if(json_string == null)
                Toast.makeText(WelcomeActivity.this, "First get json", Toast.LENGTH_SHORT).show();
            else
            {
                Intent intent = new Intent(WelcomeActivity.this, MyRidesActivity.class);
                intent.putExtra("json_data", json_string);
                startActivity(intent);
            }
        }
    }
}

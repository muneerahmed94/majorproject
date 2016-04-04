package com.example.muneer.majorproject;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;


import org.apache.commons.io.IOUtils;

import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

/**
 * Created by Muneer on 14-02-2016.
 */
public class ServerRequests
{
    public static final int CONNECTION_TIMEOUT = 1000 * 15;
    ProgressDialog progressDialog;
    Context context;

    public ServerRequests(Context context)
    {
        this.context = context;
        progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Processing...");
        progressDialog.setMessage("Please wait...");
    }

    public void storeUserDataInBackground(User user, GetUserCallback userCallBack)
    {
        progressDialog.show();
        new StoreUserDataAsyncTask(user, userCallBack).execute();
    }

    public void fetchUserDataAsyncTask(User user, GetUserCallback userCallBack)
    {
        progressDialog.show();
        new fetchUserDataAsyncTask(user, userCallBack).execute();
    }

    public void storeRideInBackground(Ride ride, GetRideCallback rideCallback)
    {
        progressDialog.show();
        new StoreRideAsyncTask(ride, rideCallback).execute();
    }

    public class StoreUserDataAsyncTask extends AsyncTask<Void, Void, String>
    {
        User user;
        GetUserCallback userCallBack;

        public StoreUserDataAsyncTask(User user, GetUserCallback userCallBack) {
            this.user = user;
            this.userCallBack = userCallBack;
        }

        @Override
        protected String doInBackground(Void... params)
        {
            String register_url = "https://majorproject-jntuhceh.rhcloud.com/register/index.php";
            try
            {
                URL url = new URL(register_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                OutputStream os = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter((new OutputStreamWriter(os, "UTF-8")));
                String data = URLEncoder.encode("name", "UTF-8") + "=" + URLEncoder.encode(user.name, "UTF-8") + "&" +
                        URLEncoder.encode("email", "UTF-8") + "=" + URLEncoder.encode(user.email, "UTF-8") + "&" +
                        URLEncoder.encode("mobile", "UTF-8") + "=" + URLEncoder.encode(user.mobile, "UTF-8") + "&" +
                        URLEncoder.encode("password", "UTF-8") + "=" + URLEncoder.encode(user.password, "UTF-8");
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

            if(result.equals("Registration Successfull"))
            {
                Toast.makeText(context, result, Toast.LENGTH_LONG).show();
                userCallBack.done(null);
            }
            else
            {
                String msg = "Email already exists";
                Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
            }
        }
    }

    public class fetchUserDataAsyncTask extends AsyncTask<Void, Void, User> {
        User user;
        GetUserCallback userCallBack;

        public fetchUserDataAsyncTask(User user, GetUserCallback userCallBack) {
            this.user = user;
            this.userCallBack = userCallBack;
        }

        @Override
        protected User doInBackground(Void... params)
        {
            User returnedUser;
            String login_url = "https://majorproject-jntuhceh.rhcloud.com/login/FetchUserData.php";
            try
            {

                URL url = new URL(login_url);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                Uri.Builder builder = new Uri.Builder().appendQueryParameter("email", user.email).appendQueryParameter("password", user.password);
                String query = builder.build().getEncodedQuery();
                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                writer.write(query);
                writer.flush();
                writer.close();
                os.close();
                conn.connect();
                InputStream in = new BufferedInputStream(conn.getInputStream());
                String response = IOUtils.toString(in, "UTF-8");
                JSONObject jResponse = new JSONObject(response);

                if (jResponse.length() == 0)
                {
                    returnedUser = null;
                }
                else
                {
                    String name = jResponse.getString("name");
                    String mobile = jResponse.getString("mobile");
                    returnedUser = new User(name, user.email, mobile, user.password);
                }

                return returnedUser;

            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(User returnedUser) {
            super.onPostExecute(returnedUser);
            progressDialog.dismiss();
            userCallBack.done(returnedUser);
        }
    }

    public class StoreRideAsyncTask extends AsyncTask<Void, Void, String>
    {
        Ride ride;
        GetRideCallback rideCallBack;

        public StoreRideAsyncTask(Ride ride, GetRideCallback rideCallBack) {
            this.ride = ride;
            this.rideCallBack = rideCallBack;
        }

        @Override
        protected String doInBackground(Void... params) {
            String register_url = "https://majorproject-jntuhceh.rhcloud.com/shareride/index.php";
            try
            {
                URL url = new URL(register_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                OutputStream os = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter((new OutputStreamWriter(os, "UTF-8")));
                String data = URLEncoder.encode("email", "UTF-8") + "=" + URLEncoder.encode(ride.email, "UTF-8") + "&" +
                        URLEncoder.encode("mobile", "UTF-8") + "=" + URLEncoder.encode(ride.mobile, "UTF-8") + "&" +
                        URLEncoder.encode("name", "UTF-8") + "=" + URLEncoder.encode(ride.name, "UTF-8") + "&" +
                        URLEncoder.encode("from", "UTF-8") + "=" + URLEncoder.encode(ride.from, "UTF-8") + "&" +
                        URLEncoder.encode("to", "UTF-8") + "=" + URLEncoder.encode(ride.to, "UTF-8") + "&" +
                        URLEncoder.encode("date", "UTF-8") + "=" + URLEncoder.encode(ride.date, "UTF-8") + "&" +
                        URLEncoder.encode("time", "UTF-8") + "=" + URLEncoder.encode(ride.time, "UTF-8") + "&" +
                        URLEncoder.encode("carno", "UTF-8") + "=" + URLEncoder.encode(ride.carNo, "UTF-8") + "&" +
                        URLEncoder.encode("carname", "UTF-8") + "=" + URLEncoder.encode(ride.carName, "UTF-8") + "&" +
                        URLEncoder.encode("noofseats", "UTF-8") + "=" + URLEncoder.encode(new Integer(ride.noOfSeats).toString(), "UTF-8") + "&" +
                        URLEncoder.encode("price", "UTF-8") + "=" + URLEncoder.encode(new Integer(ride.price).toString(), "UTF-8") + "&";
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

            if(result.equals("Ride Shared!"))
            {
                Toast.makeText(context, result, Toast.LENGTH_LONG).show();
                rideCallBack.done(null);
            }
            else
            {
                String msg = "Unable to share ride";
                Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
            }
        }
    }
}

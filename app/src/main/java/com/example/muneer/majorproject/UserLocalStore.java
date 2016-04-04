package com.example.muneer.majorproject;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Muneer on 14-02-2016.
 */
public class UserLocalStore
{
    public static final String SP_NAME = "userDetails";
    SharedPreferences userLocalDatabase;

    public UserLocalStore(Context context)
    {
        userLocalDatabase = context.getSharedPreferences(SP_NAME, 0);
    }

    public void storeUserData(User user)
    {
        SharedPreferences.Editor userLocalDatabaseEditor = userLocalDatabase.edit();
        userLocalDatabaseEditor.putString("name", user.name);
        userLocalDatabaseEditor.putString("email", user.email);
        userLocalDatabaseEditor.putString("mobile", user.mobile);
        userLocalDatabaseEditor.putString("password", user.password);
        userLocalDatabaseEditor.commit();
    }

    public void setUserLoggedIn(boolean loggedIn)
    {
        SharedPreferences.Editor userLocalDatabaseEditor = userLocalDatabase.edit();
        userLocalDatabaseEditor.putBoolean("loggedIn", loggedIn);
        userLocalDatabaseEditor.commit();
    }

    public void clearUserData()
    {
        SharedPreferences.Editor userLocalDatabaseEditor = userLocalDatabase.edit();
        userLocalDatabaseEditor.clear();
        userLocalDatabaseEditor.commit();
    }

    public User getLoggedInUser()
    {
        if (userLocalDatabase.getBoolean("loggedIn", false) == false)
        {
            return null;
        }

        String name = userLocalDatabase.getString("name", "");
        String email = userLocalDatabase.getString("email", "");
        String password = userLocalDatabase.getString("password", "");
        String mobile = userLocalDatabase.getString("mobile", "");

        User user = new User(name, email, mobile, password);
        return user;
    }
}

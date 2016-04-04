package com.example.muneer.majorproject;

/**
 * Created by Muneer on 14-02-2016.
 */
public class User {
    String name, email, mobile, password;

    public User(String name, String email, String mobile, String password)
    {
        this.name = name;
        this.email = email;
        this.password = password;
        this.mobile = mobile;
    }

    public User(String email,String password)
    {
        this.name = "";
        this.mobile = "";
        this.email= email;
        this.password = password;
    }
}

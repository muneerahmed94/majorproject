package com.example.muneer.majorproject;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class RegisterActivity extends AppCompatActivity {

    EditText ET_NAME, ET_EMAIL, ET_MOBILE, ET_PASSWORD, ET_CONFIRM_PASSWORD;
    String name, email, mobile, password, confirmPassword;
    TextView loginHere;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ET_NAME = (EditText)findViewById(R.id.editTextName);
        ET_EMAIL = (EditText)findViewById(R.id.editTextEmail);
        ET_MOBILE = (EditText) findViewById(R.id.editTextMobile);
        ET_PASSWORD = (EditText)findViewById(R.id.editTextPassword);
        ET_CONFIRM_PASSWORD = (EditText)findViewById(R.id.editTextConfirmPassword);

        loginHere = (TextView) findViewById(R.id.textViewLoginHere);
        loginHere.setPaintFlags(loginHere.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        loginHere.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }

    public void register(View v)
    {
        name = ET_NAME.getText().toString();
        email = ET_EMAIL.getText().toString();
        mobile = ET_MOBILE.getText().toString();
        password = ET_PASSWORD.getText().toString();
        confirmPassword = ET_CONFIRM_PASSWORD.getText().toString();

        if(!password.equals(confirmPassword))
        {
            showPasswordConfirmPasswordError();
        }
        else
        {
            User user = new User(name, email, mobile, password);
            registerUser(user);
        }
    }

    public void registerUser(User user)
    {
        ServerRequests serverRequest = new ServerRequests(this);
        serverRequest.storeUserDataInBackground(user, new GetUserCallback() {
            @Override
            public void done(User returnedUser) {
                Intent loginIntent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(loginIntent);
            }
        });
    }

    public void showPasswordConfirmPasswordError()
    {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(RegisterActivity.this);
        dialogBuilder.setMessage("Password and Confirm Password should match");
        dialogBuilder.setPositiveButton("OK", null);
        dialogBuilder.show();
    }
}

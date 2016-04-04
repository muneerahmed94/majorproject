package com.example.muneer.majorproject;

import java.util.ArrayList;
import android.telephony.SmsManager;
import android.widget.Toast;


/**
 * Created by Muneer on 25-03-2016.
 */
public class SMSSender {

    public void send(String message, ArrayList<String> receivers) {
        for(String receiver : receivers) {
            try {
                SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(receiver, null, message, null, null);
                    //Toast.makeText(context, "OTP forwarded successfully", Toast.LENGTH_LONG).show();
            } catch (Exception ex) {
                //Toast.makeText(context, ex.getMessage().toString(), Toast.LENGTH_LONG).show();
                ex.printStackTrace();
            }
        }
    }
}

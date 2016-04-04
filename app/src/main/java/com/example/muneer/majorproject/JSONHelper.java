package com.example.muneer.majorproject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Muneer on 06-03-2016.
 */
public class JSONHelper
{
    public JSONArray createJSONArray(String json_string)
    {
        JSONObject jsonObject;
        JSONArray jsonArray = null;

        try {
            jsonObject = new JSONObject(json_string);
            jsonArray = jsonObject.getJSONArray("server_response");
        }catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonArray;
    }
}

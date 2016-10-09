package com.example.wouter.tvprogress.model.API;

import android.content.Context;
import android.content.SharedPreferences;
import com.example.wouter.tvprogress.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/**
 * Created by 52 on 09-10-2016.
 */
public class CallAPIAccessToken {
    private Context mContext;
    private String urlString = "";

    public CallAPIAccessToken(Context context){
        mContext = context;
    }

    private String GetSaveAccessToken(){
        String token;
        BufferedReader reader = null;

        try {
            URL url = new URL("https://api.thetvdb.com/login");

            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-Type","application/json");
            urlConnection.setDoOutput(true);
            urlConnection.setDoInput(true);
            urlConnection.connect();

            //Create JSONObject here
            JSONObject jsonParam = new JSONObject();
            jsonParam.put("apikey", "FBC62AD06C1EC710");

            OutputStreamWriter out = new   OutputStreamWriter(urlConnection.getOutputStream());
            out.write(jsonParam.toString());
            out.close();

            int responseCode = urlConnection.getResponseCode();

            if(responseCode == 401) {
                System.err.println("Get Access token :Not Authorized");
                return null;
            }

            InputStream inStream = urlConnection.getInputStream();
            reader = new BufferedReader(new InputStreamReader(inStream));
        }
        catch (Exception ex) {
            System.err.println(ex);
            System.err.println("GetSaveAccessToken connection ex:" + ex.getMessage());
            return null;
        }

        try{
            StringBuilder responseStrBuilder = new StringBuilder();

            String inputStr;
            while ((inputStr = reader.readLine()) != null)
                responseStrBuilder.append(inputStr);

            JSONObject jsonResource = new JSONObject(responseStrBuilder.toString());

            token = jsonResource.getString("token");
        } catch(Exception ex) {
            System.err.println("JSON Object = " + ex.getMessage());
            return null;
        }

        long currentTimeMillis = System.currentTimeMillis();

        SharedPreferences sharedPref = mContext.getSharedPreferences(mContext.getString(R.string.preference_file), Context.MODE_PRIVATE);
        SharedPreferences.Editor sharedPrefEditor = sharedPref.edit();
        sharedPrefEditor.putString(mContext.getString(R.string.access_token_string), token);
        sharedPrefEditor.putLong(mContext.getString(R.string.access_token_time), currentTimeMillis);
        sharedPrefEditor.apply();

        return token;
    }

    public String GetAccessToken(){
        SharedPreferences sharedPref = mContext.getSharedPreferences(mContext.getString(R.string.preference_file), Context.MODE_PRIVATE);

        if(!sharedPref.contains(mContext.getString(R.string.access_token_string))){
            return GetSaveAccessToken();
        }

        String key = sharedPref.getString(mContext.getString(R.string.access_token_string), "");
        long timeStamp = sharedPref.getLong(mContext.getString(R.string.access_token_time), 0);
        long currentTime = System.currentTimeMillis();
        long difference = currentTime - timeStamp;

        if(difference >= TimeUnit.MILLISECONDS.convert(1, TimeUnit.DAYS)){
            key = GetSaveAccessToken();
        }

        return key;
    }
}

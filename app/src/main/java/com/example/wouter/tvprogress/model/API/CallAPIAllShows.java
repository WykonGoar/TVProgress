package com.example.wouter.tvprogress.model.API;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Wouter on 22-11-2015.
 */
public class CallAPIAllShows extends AsyncTask<String, String, ArrayList<ShowResource>>{

    private Context mContext;
    private String urlString = "https://api.thetvdb.com/search/series";
    private iOnTaskCompleted mCurrentActivity;
    private String mTitle;

    public CallAPIAllShows(Context context, iOnTaskCompleted currentActivity, String title) {
        mContext = context;
        mCurrentActivity = currentActivity;
        mTitle = title;
    }

    @Override
    protected ArrayList<ShowResource> doInBackground(String... params) {
        CallAPIAccessToken mCallAPIAccessToken = new CallAPIAccessToken(mContext);
        String token = mCallAPIAccessToken.GetAccessToken();

        System.out.println("Token = " + token);
        if(token.isEmpty()){
            System.err.println("Token empty");
            return null;
        }

        BufferedReader reader = null;
        ArrayList<ShowResource> result = null;
        // HTTP Get
        try {

            mTitle = mTitle.replace(" ", "%20");
            System.out.println(mTitle);
            URL url = new URL(urlString + "?name=" + mTitle);

            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setRequestProperty("Authorization", "Bearer " + token);

            int responseCode = urlConnection.getResponseCode();
            if(responseCode == 401) {
                System.err.println("Get shows : Not Authorized");
                return null;
            }
            else if(responseCode == 404) {
                System.err.println("Get shows : Not found");
                return null;
            }

            reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), "UTF-8"));

        } catch (Exception e ){
            System.err.println(e);
            System.err.println("Raise exception : " + e.getMessage());
            return null;
        }

        try{
            StringBuilder responseStrBuilder = new StringBuilder();

            String inputStr;
            while ((inputStr = reader.readLine()) != null)
                responseStrBuilder.append(inputStr);

            result = readShowsArray(responseStrBuilder.toString());
        } catch(Exception ex){
            System.err.println(ex.getMessage());
            return null;
        }

        return result;
    }

    @Override
    protected void onPostExecute(ArrayList<ShowResource> showResources) {
        mCurrentActivity.onTaskCompleted(showResources);
    }

    public ArrayList<ShowResource> readShowsArray(String jsonString) throws IOException{
        ArrayList<ShowResource> resources = new ArrayList<ShowResource>();

        try {
            JSONObject jsonResource = new JSONObject(jsonString);

            JSONArray list = jsonResource.getJSONArray("data");

            for (int i = 0; i < list.length(); i++) {
                JSONObject object = list.getJSONObject(i);

                String title = object.getString("seriesName");
                String status = object.getString("status");
                long id = object.getLong("id");

                ShowResource newResource = new ShowResource();
                newResource.setTitle(title);
                newResource.setStatus(status);
                newResource.setURL("https://api.thetvdb.com/series/" + id + "/episodes");

                resources.add(newResource);
            }
        } catch (JSONException ex){
            System.err.println("JSON loop = " + ex.getMessage());
        }

        return resources;
    }
}

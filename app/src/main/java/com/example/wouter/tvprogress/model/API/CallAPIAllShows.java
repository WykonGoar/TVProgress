package com.example.wouter.tvprogress.model.API;

import android.content.Context;
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
public class CallAPIAllShows extends AsyncTask<Integer, String, ArrayList<ShowResource>>{

    private Context mContext;
    private String urlString = "https://epguides.frecar.no/show/";
    private iOnTaskCompleted mCurrentActivity;

    public CallAPIAllShows(Context context, iOnTaskCompleted currentActivity) {
        mContext = context;
        mCurrentActivity = currentActivity;
    }

    @Override
    protected ArrayList<ShowResource> doInBackground(Integer... params) {
        BufferedReader reader = null;
        ArrayList<ShowResource> result = null;
        // HTTP Get
        try {

            URL url = new URL(urlString);

            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

            reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), "UTF-8"));

        } catch (Exception e ) {
            System.out.println(e.getMessage());
            return null;
        }

        try{
            StringBuilder responseStrBuilder = new StringBuilder();

            String inputStr;
            while ((inputStr = reader.readLine()) != null)
                responseStrBuilder.append(inputStr);

            JSONArray root = new JSONArray(responseStrBuilder.toString());

            result = readShowsArray(root);
        } catch(Exception ex){

        }

        return result;
    }

    @Override
    protected void onPostExecute(ArrayList<ShowResource> showResources) {
        mCurrentActivity.onTaskCompleted(showResources);
    }

    public ArrayList<ShowResource> readShowsArray(JSONArray rootObject) throws IOException{
        ArrayList<ShowResource> resources = new ArrayList<ShowResource>();


        try {
            for(int i=0; i < rootObject.length(); i++) {
                JSONObject jsonResource = rootObject.getJSONObject(i);

                String title = jsonResource.getString("title");
                String url = jsonResource.getString("episodes");

                ShowResource newResource = new ShowResource();
                newResource.setTitle(title);
                newResource.setURL(url);

                resources.add(newResource);

            }
        } catch (JSONException ex){

        }

        return resources;
    }
}

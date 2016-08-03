package com.example.wouter.tvprogress.model.API;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by Wouter on 25-11-2015.
 */
public class WiFiConnection {
    private Context mContext;

    public WiFiConnection(Context context)
    {
        mContext= context;
    }

    public boolean isConnectedToWiFi(){
        ConnectivityManager connManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        return networkInfo.isConnected();
    }
}

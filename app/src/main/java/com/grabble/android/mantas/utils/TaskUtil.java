package com.grabble.android.mantas.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by Mantas on 24/01/2017.
 */

public class TaskUtil {

    private Context context;

    public TaskUtil(Context context) {
        this.context = context;
    }

    /**
     *  Checks if the user has access to network.
     *
     *  Code from: http://stackoverflow.com/questions/1560788/how-to-check-internet-access-on-android-inetaddress-never-times-out
     */
    public boolean isOnline() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }
}

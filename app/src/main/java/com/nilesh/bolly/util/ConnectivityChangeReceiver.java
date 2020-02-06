package com.nilesh.bolly.util;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.nilesh.bolly.MainActivity;
import com.nilesh.bolly.dialogs.NoconnectionDialog;

public class ConnectivityChangeReceiver extends BroadcastReceiver {

    FragmentManager manager;
    NoconnectionDialog dialog;


    public ConnectivityChangeReceiver(FragmentManager supportFragmentManager) {
        manager = supportFragmentManager;
        dialog = new NoconnectionDialog();
        dialog.setCancelable(false);
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        if(!isConnected(context)){
            Log.d("CONNECTION", "disconnected");

            if(!dialog.isVisible()){
                manager.beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).add(android.R.id.content, dialog, "dialog").addToBackStack("stack").commit();
            }
        }else{
            Log.d("CONNECTION", "connected");
            if(dialog != null && dialog.isVisible()) {
                Fragment prev = manager.findFragmentByTag("dialog");
                if(prev != null){
                    ((NoconnectionDialog)prev).dismiss();
                }
            }
        }
    }

    public  boolean isConnected(Context context) {
        if(context == null)  return false;


        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager != null) {

            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.getActiveNetwork());
                if (capabilities != null) {
                    if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                        return true;
                    } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                        return true;
                    }  else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)){
                        return true;
                    }
                }
            }

        }
        return false;
    }

}
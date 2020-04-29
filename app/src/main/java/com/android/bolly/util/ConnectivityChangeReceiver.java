package com.android.bolly.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.android.bolly.dialogs.NoconnectionDialog;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

public class ConnectivityChangeReceiver extends BroadcastReceiver {

    FragmentManager manager;
    NoconnectionDialog dialog;
    private static long lastConnectionChange = 0;


    public ConnectivityChangeReceiver(FragmentManager supportFragmentManager) {
        manager = supportFragmentManager;
        dialog = new NoconnectionDialog();
        dialog.setCancelable(false);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        //so that dialog is not continuously added and removed...

        if(System.currentTimeMillis() - lastConnectionChange <= 2000)
            return;
        else
            isConnected(context.getApplicationContext());

        lastConnectionChange = System.currentTimeMillis();

    }

    private void showDialog(){

        if(manager != null){
            Fragment prev = manager.findFragmentByTag("dialog");
            if(prev != null && prev instanceof NoconnectionDialog){
                manager.beginTransaction().show((NoconnectionDialog)prev);
            }else{
                manager.beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).add(android.R.id.content, dialog, "dialog").addToBackStack("stack").commitAllowingStateLoss();
            }
        }
    }

    private void removeDialog() {
        if(manager != null) {
            Fragment prev = manager.findFragmentByTag("dialog");
            if(prev != null){
                Log.d("FRAGMENT", "previous fragment found.. dismissing");
                dialog.dismiss();
            }
        }
    }

    public  void isConnected(Context context) {
        new InternetCheck( internet -> {
            if(internet) removeDialog();
            else showDialog();
        });

    }

    static class InternetCheck extends AsyncTask<Void,Void,Boolean> {

        private Consumer mConsumer;
        public interface Consumer { void accept(Boolean internet); }

        public  InternetCheck(Consumer consumer) { mConsumer = consumer; execute(); }

        @Override protected Boolean doInBackground(Void... voids) { try {
            Socket sock = new Socket();
            sock.connect(new InetSocketAddress("8.8.8.8", 53), 5000);
            sock.close();
            return true;
        } catch (IOException e) { return false; } }

        @Override protected void onPostExecute(Boolean internet) { mConsumer.accept(internet); }
    }

}
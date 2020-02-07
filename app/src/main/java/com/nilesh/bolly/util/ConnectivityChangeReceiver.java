package com.nilesh.bolly.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.nilesh.bolly.dialogs.NoconnectionDialog;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

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

        isConnected(context.getApplicationContext());
    }

    private void showDialog(){
        if(dialog != null && !dialog.isVisible()) {
            removeDialog(); //to remove any existing dialogs and then create new
            manager.beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).add(android.R.id.content, dialog, "dialog").addToBackStack("stack").commit();
        }
    }

    private void removeDialog() {
        if(manager != null) {
            Fragment prev = manager.findFragmentByTag("dialog");
            if(prev != null){
                Log.d("FRAGMENT", "previous fragment found.. dismissing");
                manager.beginTransaction().remove(((NoconnectionDialog)prev)).commit();
                //((NoconnectionDialog)prev).dismiss();
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
            sock.connect(new InetSocketAddress("8.8.8.8", 53), 1500);
            sock.close();
            return true;
        } catch (IOException e) { return false; } }

        @Override protected void onPostExecute(Boolean internet) { mConsumer.accept(internet); }
    }

}
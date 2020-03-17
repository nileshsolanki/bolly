package com.mobile.bolly.util;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.mobile.bolly.BuildConfig;
import com.mobile.bolly.fragments.UpdateFragment;
import com.mobile.bolly.models.UpdateLog;
import com.mobile.bolly.networking.RetrofitSingleton;
import com.mobile.bolly.tv.UpdateActivityTv;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Common {

    public static void fullScreen(Activity activity) {
            View decorView = activity.getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
    }



    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }


    public static void checkUpdate(FragmentManager fragmentManager) {

        RetrofitSingleton.getUpdateService().checkUpdates().enqueue(new Callback<UpdateLog>() {
            @Override
            public void onResponse(Call<UpdateLog> call, Response<UpdateLog> response) {
                if(response.body().getLatestVersion() != null){

                    if(BuildConfig.VERSION_CODE < response.body().getLatestVersionCode() ){

                        Bundle args = new Bundle();
                        args.putString("downloadUrl", response.body().getUrl());
                        args.putString("version", response.body().getLatestVersion());

                        UpdateFragment updateFragment = new UpdateFragment();
                        updateFragment.setArguments(args);

                        fragmentManager.beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                                .add(android.R.id.content, updateFragment, "update")
                                .addToBackStack("stack")
                                .commitAllowingStateLoss();

                    }


                }
            }

            @Override
            public void onFailure(Call<UpdateLog> call, Throwable t) {

            }
        });


    }


    public static void checkUpdateTv(Activity activity) {

        RetrofitSingleton.getUpdateService().checkUpdates().enqueue(new Callback<UpdateLog>() {
            @Override
            public void onResponse(Call<UpdateLog> call, Response<UpdateLog> response) {
                if(response.body().getLatestVersion() != null){

                    if(BuildConfig.VERSION_CODE < response.body().getLatestVersionCode() ){

                        Intent intent = new Intent(activity, UpdateActivityTv.class);
                        intent.putExtra("downloadUrl", response.body().getUrl());
                        activity.startActivity(intent);

                    }


                }
            }

            @Override
            public void onFailure(Call<UpdateLog> call, Throwable t) {

            }
        });


    }



}

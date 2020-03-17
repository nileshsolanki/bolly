package com.mobile.bolly.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mobile.bolly.models.Result;

import java.util.List;

public class SharedPrefHelper {

    Context context;
    public static final String TYPE_RECENTS = "recents", TYPE_TOPRATED = "toprated";
    public SharedPrefHelper(Context context){
        this.context = context;
    }


    public void putSearchResults(String type, List<Result> results){
        SharedPreferences defaultPref = context.getSharedPreferences("default", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = defaultPref.edit();
        editor.putString(type, new Gson().toJson(results));
        editor.apply();
    }


    public List<Result> getSearchResults(String type){
        SharedPreferences defaultPref = context.getSharedPreferences("default", Context.MODE_PRIVATE);
        String resultString = defaultPref.getString(type, null);
        if(resultString == null)
            return null;
        return new Gson().fromJson(resultString, new TypeToken<List<Result>>(){}.getType());
    }


    public void putFetchTimeStamp(Long time){
        SharedPreferences defaultPref = context.getSharedPreferences("default", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = defaultPref.edit();
        editor.putLong("timestamp", time);
        editor.apply();
    }


    public Long getLastFetchTimeStamp(){
        SharedPreferences defaultPref = context.getSharedPreferences("default", Context.MODE_PRIVATE);
        return defaultPref.getLong("timestamp", System.currentTimeMillis() - 2*60*60*1000);
    }



}

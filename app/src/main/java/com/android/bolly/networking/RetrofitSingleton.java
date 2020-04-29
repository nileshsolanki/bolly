package com.android.bolly.networking;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.android.bolly.constants.Tmdb.SERVER_BASE_URL;
import static com.android.bolly.constants.Tmdb.TMDB_BASE_URL;
import static com.android.bolly.constants.Tmdb.UPDATE_SERVICE_BASE_URL;

public class RetrofitSingleton {

    private static TmdbService tmdbService = null;
    private static BollyService bollyService = null;
    private static UpdateService updateService = null;

    public static TmdbService getTmdbService(){
        if(tmdbService == null){
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(TMDB_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            tmdbService = retrofit.create(TmdbService.class);
        }

        return tmdbService;
    }


    public static BollyService getBollyService(){
        if(bollyService == null){
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(SERVER_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            bollyService = retrofit.create(BollyService.class);
        }

        return bollyService;
    }


    public static UpdateService getUpdateService(){
        if(updateService == null){
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(UPDATE_SERVICE_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            updateService = retrofit.create(UpdateService.class);
        }

        return updateService;
    }


    public static void postReport(int id, int reported, int played){
        HashMap<String, Integer> body = new HashMap<>();
        body.put("reported", reported);
        body.put("played", played);
        getBollyService().postReport(id, body).enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) { }

            @Override
            public void onFailure(Call<Object> call, Throwable t) { }
        });
    }





}

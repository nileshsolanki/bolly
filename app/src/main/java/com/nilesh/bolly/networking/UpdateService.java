package com.nilesh.bolly.networking;

import com.nilesh.bolly.models.UpdateLog;

import retrofit2.Call;
import retrofit2.http.GET;

public interface UpdateService {

    @GET("ff80cc1b967f6d29185d")
    Call<UpdateLog> checkUpdates();

    //"https://api.npoint.io/ff80cc1b967f6d29185d"
}

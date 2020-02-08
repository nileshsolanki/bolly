package com.mobile.bolly.networking;

import com.mobile.bolly.models.Movie;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface BollyService {


    @GET ("/{id}")
    Call<Movie> getMovieDetails(
            @Path("id") String id
    );
}

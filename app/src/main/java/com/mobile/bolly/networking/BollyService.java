package com.mobile.bolly.networking;

import com.mobile.bolly.models.Movie;
import com.mobile.bolly.models.Suggestion;

import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface BollyService {


    @GET ("/{id}")
    Call<Movie> getMovieDetails(
            @Path("id") String id
    );


    @GET ("/search")
    Call<List<Suggestion>> getSuggestions(
            @Query("query") String query
    );
}

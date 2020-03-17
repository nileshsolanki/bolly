package com.mobile.bolly.networking;

import com.mobile.bolly.models.Movie;
import com.mobile.bolly.models.MovieDetails;
import com.mobile.bolly.models.Result;
import com.mobile.bolly.models.Suggestion;

import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface BollyService {


    @GET ("/{id}")
    Call<Movie> getMovieDetails(
            @Path("id") int id
    );


    @GET ("/search")
    Call<List<Suggestion>> getSuggestions(
            @Query("query") String query
    );




    @POST ("/reports/{id}")
    Call<Object> postReport(
            @Path("id") int id,
            @Body HashMap<String, Integer> body
    );


    @GET("/recent")
    Call<List<MovieDetails>> getRecents();

    @GET ("/toprated")
    Call<List<Result>> getTopRated();

    @GET ("/year/{year}")
    Call<List<Result>> getForYear(@Path("year") int year);

    @GET ("/genre/{genre}")
    Call<List<Result>> getForGenre(@Path("genre") int genre);

}

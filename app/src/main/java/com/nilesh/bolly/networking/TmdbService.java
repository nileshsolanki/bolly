package com.nilesh.bolly.networking;

import android.net.Uri;

import com.nilesh.bolly.models.MovieDetails;
import com.nilesh.bolly.models.MovieDiscover;
import com.nilesh.bolly.models.MovieExternalIds;
import com.nilesh.bolly.models.MovieNowPlaying;
import com.nilesh.bolly.models.MovieSearch;
import com.nilesh.bolly.models.MovieTopRated;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface TmdbService {

    @GET("/3/movie/now_playing")
    Call<MovieNowPlaying> nowPlaying(
            @Query("api_key") String apiKey,
            @Query("with_original_language") String ogLang,
            @Query("region") String region,
            @Query("page") int page
    );


    @GET("/3/movie/top_rated")
    Call<MovieTopRated> topRated(
            @Query("api_key") String apiKey,
            @Query("with_original_language") String ogLang,
            @Query("region") String region,
            @Query("page") int page
    );

    @GET("/3/discover/movie")
    Call<MovieDiscover> year(
        @Query("api_key") String apiKey,
        @Query("with_original_language") String ogLang,
        @Query("region") String region,
        @Query("page") int page,
        @Query("include_adult") boolean adultMovies,
        @Query("year") int year,
        @Query("sort_by") String sortBy,
        @Query("certification_country") String certificationCountry,
        @Query("release_date.lte") String dateToday
    );


    @GET("/3/discover/movie")
    Call<MovieDiscover> genre(
            @Query("api_key") String apiKey,
            @Query("with_original_language") String ogLang,
            @Query("region") String region,
            @Query("page") int page,
            @Query("include_adult") boolean adultMovies,
            @Query("with_genres") String genre,
            @Query("sort_by") String sortBy,
            @Query("certification_country") String certificationCountry,
            @Query("release_date.lte") String dateToday,
            @Query("release_date.gte") String dateInitial
    );


    @GET("/3/search/movie")
    Call<MovieSearch> searchMovie(
            @Query("api_key") String apiKey,
            @Query("query") String query,
            @Query("with_original_language") String ogLang,
            @Query("region") String region,
            @Query("include_adult") boolean includeAdult,
            @Query("page") int page
    );

    @GET("/3/movie/{movie_id}/external_ids")
    Call<MovieExternalIds> externalIds(
            @Path("movie_id") int movieId,
            @Query("api_key") String apiKey
    );



    @GET("/3/movie/{movie_id}")
    Call<MovieDetails> details(
            @Path("movie_id") int movieId,
            @Query("api_key") String apiKey
    );




}

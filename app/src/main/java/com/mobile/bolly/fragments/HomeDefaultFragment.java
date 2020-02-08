package com.mobile.bolly.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.mobile.bolly.R;
import com.mobile.bolly.MovieGridActivity;
import com.mobile.bolly.adapter.MovieConciseAdapter;
import com.mobile.bolly.adapter.MovieGenreAdapter;
import com.mobile.bolly.adapter.MovieYearAdapter;
import com.mobile.bolly.models.Movie;
import com.mobile.bolly.models.MovieExternalIds;
import com.mobile.bolly.models.MovieNowPlaying;
import com.mobile.bolly.models.MovieSearch;
import com.mobile.bolly.models.MovieTopRated;
import com.mobile.bolly.models.Result;
import com.mobile.bolly.networking.RetrofitSingleton;
import com.mobile.bolly.networking.TmdbService;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.mobile.bolly.constants.Tmdb.APIKEY;

public class HomeDefaultFragment extends Fragment implements View.OnClickListener {

    View view;
    RecyclerView rvMovieNowpalying, rvMovieToprated, rvMovieConciseYear, rvMovieConciseGenre;
    MovieConciseAdapter movieNowplayingAdapter, movieTopratedAdapter;
    MovieGenreAdapter movieGenreAdapter;
    TextView tvShowingMore, tvRatedMore;
    private String RESPONSE = "Response";
    List<Result> ratedMovies = new ArrayList<>();
    List<Result> nowPlayingMovies = new ArrayList<>();




    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home_default, container, false);

        tvShowingMore = view.findViewById(R.id.tv_more_showing);
        tvRatedMore = view.findViewById(R.id.tv_more_rated);
        rvMovieNowpalying = view.findViewById(R.id.rv_movie_concise_nowplaying);
        rvMovieToprated = view.findViewById(R.id.rv_movie_concise_popular);
        rvMovieConciseYear = view.findViewById(R.id.rv_movie_concise_year);
        rvMovieConciseGenre = view.findViewById(R.id.rv_movie_concise_genre);
        rvMovieNowpalying.setHasFixedSize(true);
        rvMovieToprated.setHasFixedSize(true);
        rvMovieConciseYear.setAdapter(new MovieYearAdapter(getContext()));

        movieGenreAdapter = new MovieGenreAdapter(getContext());
        rvMovieConciseGenre.setHasFixedSize(true);
        rvMovieConciseGenre.setAdapter(movieGenreAdapter);

        tvShowingMore.setOnClickListener(this);
        tvRatedMore.setOnClickListener(this);



        movieTopratedAdapter = new MovieConciseAdapter(ratedMovies, getActivity());
        rvMovieToprated.setAdapter(movieTopratedAdapter);

        movieNowplayingAdapter = new MovieConciseAdapter(nowPlayingMovies, getActivity());
        rvMovieNowpalying.setAdapter(movieNowplayingAdapter);


        createSerivces();



        return view;
    }

    private void createSerivces() {

        TmdbService service = RetrofitSingleton.getTmdbService();
        service.nowPlaying(APIKEY, "hi", "IN", 1).enqueue(new Callback<MovieNowPlaying>() {
            @Override
            public void onResponse(Call<MovieNowPlaying> call, Response<MovieNowPlaying> response) {
                if(response.body().getResults() != null)

                    for(Result movie : response.body().getResults()){

                        RetrofitSingleton.getTmdbService().externalIds(movie.getId(), APIKEY).enqueue(new Callback<MovieExternalIds>() {
                            @Override
                            public void onResponse(Call<MovieExternalIds> call, Response<MovieExternalIds> response) {
                                if(response.body() != null){
                                    RetrofitSingleton.getBollyService().getMovieDetails(response.body().getImdbId()).enqueue(new Callback<Movie>() {
                                        @Override
                                        public void onResponse(Call<Movie> call, Response<Movie> response) {
                                            if(response.body() != null) {
                                                nowPlayingMovies.add(movie);
                                                movieNowplayingAdapter.notifyDataSetChanged();
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<Movie> call, Throwable t) {

                                        }
                                    });
                                }
                            }

                            @Override
                            public void onFailure(Call<MovieExternalIds> call, Throwable t) {

                            }
                        });

                    }

            }

            @Override
            public void onFailure(Call<MovieNowPlaying> call, Throwable t) {

            }
        });


        service.topRated(APIKEY, "hi", "IN", 1).enqueue(new Callback<MovieTopRated>() {
            @Override
            public void onResponse(Call<MovieTopRated> call, Response<MovieTopRated> response) {

                for(Result result : response.body().getResults()){
                    if(Integer.parseInt(result.getReleaseDate().split("-")[0])>= 2000) {
                        RetrofitSingleton.getTmdbService().externalIds(result.getId(), APIKEY).enqueue(new Callback<MovieExternalIds>() {
                            @Override
                            public void onResponse(Call<MovieExternalIds> call, Response<MovieExternalIds> response) {
                                if(response.body() != null){

                                    RetrofitSingleton.getBollyService().getMovieDetails(response.body().getImdbId()).enqueue(new Callback<Movie>() {
                                        @Override
                                        public void onResponse(Call<Movie> call, Response<Movie> response) {
                                            if(response.body() != null){
                                                ratedMovies.add(result);
                                                movieTopratedAdapter.notifyDataSetChanged();
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<Movie> call, Throwable t) {

                                        }
                                    });



                                }
                            }

                            @Override
                            public void onFailure(Call<MovieExternalIds> call, Throwable t) {

                            }
                        });


                    }

                }



            }

            @Override
            public void onFailure(Call<MovieTopRated> call, Throwable t) {

            }
        });


        String encodedQuery = "";

        try {
            encodedQuery = URLEncoder.encode("street dancer", "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }


        service.searchMovie(APIKEY, encodedQuery, "hi", "IN", true, 1).enqueue(new Callback<MovieSearch>() {
            @Override
            public void onResponse(Call<MovieSearch> call, Response<MovieSearch> response) {
                for(Result result : response.body().getResults()){
                    Log.d(RESPONSE, result.getTitle());
                }
            }

            @Override
            public void onFailure(Call<MovieSearch> call, Throwable t) {

            }
        });

    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(getContext(), MovieGridActivity.class);
        switch (view.getId()){

            case R.id.tv_more_showing:
                intent.putExtra("category", 1);
                startActivity(intent);
                break;

            case R.id.tv_more_rated:
                intent.putExtra("category", 2);
                startActivity(intent);
                break;

        }


    }
}

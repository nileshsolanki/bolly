package com.mobile.bolly;

import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mobile.bolly.adapter.GridSpacingItemDecoration;
import com.mobile.bolly.adapter.MovieConciseAdapter;
import com.mobile.bolly.models.MovieDetails;
import com.mobile.bolly.models.MovieDiscover;
import com.mobile.bolly.models.MovieNowPlaying;
import com.mobile.bolly.models.MovieTopRated;
import com.mobile.bolly.models.Result;
import com.mobile.bolly.networking.BollyService;
import com.mobile.bolly.networking.RetrofitSingleton;
import com.mobile.bolly.util.ConnectivityChangeReceiver;
import com.mobile.bolly.util.SharedPrefHelper;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.mobile.bolly.constants.Tmdb.APIKEY;
import static com.mobile.bolly.util.Common.fullScreen;

public class MovieGridActivity extends AppCompatActivity {

    RecyclerView gridRecycler;
    MovieConciseAdapter adapter;
    TextView tvCollectionType;
    public static final int categoryRecent = 1, categoryToprated = 2, categoryGenre = 3, categoryYear = 4;
    List<Result> movies = new ArrayList<Result>();
    int page = 1;
    int category;
    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    ImageButton btnBack;
    ConnectivityChangeReceiver connectivityReceiver;


    @Override
    protected void onPause() {
        super.onPause();
        if(connectivityReceiver != null)
            unregisterReceiver(connectivityReceiver);

    }


    @Override
    protected void onStart() {
        super.onStart();
        fullScreen(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        connectivityReceiver = new ConnectivityChangeReceiver(getSupportFragmentManager());
        registerReceiver(connectivityReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_grid);


        fullScreen(this);
        tvCollectionType = findViewById(R.id.tv_collection_type);
        btnBack = findViewById(R.id.imgbtn_back);
        gridRecycler = findViewById(R.id.recycler_grid_view);
        GridLayoutManager layoutManager = new GridLayoutManager(this, calculateNoOfColumns(this, 120) -1 );
        gridRecycler.addItemDecoration(new GridSpacingItemDecoration(layoutManager.getSpanCount(), 24, true));
        gridRecycler.setLayoutManager(layoutManager);

        category = getIntent().getIntExtra("category", 0);

        fetchMoviesByCategory(category);

        adapter = new MovieConciseAdapter(movies, MovieGridActivity.this);
        gridRecycler.setAdapter(adapter);

    }




    private void fetchMoviesByCategory(int categoryType){

        switch (categoryType){

            case categoryRecent:
                tvCollectionType.setText("Recent Additions");

                BollyService service = RetrofitSingleton.getBollyService();
                service.getRecents().enqueue(new Callback<List<MovieDetails>>() {
                    @Override
                    public void onResponse(Call<List<MovieDetails>> call, Response<List<MovieDetails>> response) {
                        if(response.body() != null){
                            for(MovieDetails details: response.body()){
                                Result movie = new Result();
                                movie.setVoteAverage(details.getVoteAverage());
                                movie.setTitle(details.getTitle());
                                movie.setReleaseDate(details.getReleaseDate());
                                movie.setOverview(details.getOverview());
                                movie.setOriginalLanguage(details.getOriginalLanguage());
                                movie.setPosterPath((String) details.getPosterPath());
                                movie.setAdult(details.getAdult());
                                movie.setBackdropPath(details.getBackdropPath());
                                movie.setId(details.getId());
                                movie.setPopularity(details.getPopularity());
                                movie.setVideo(details.getVideo());
                                movie.setVoteCount(details.getVoteCount());

                                movies.add(movie);
                            }
                            adapter.notifyDataSetChanged();

                        }
                    }

                    @Override
                    public void onFailure(Call<List<MovieDetails>> call, Throwable t) {

                    }
                });

                break;


            case categoryToprated:
                tvCollectionType.setText("Top Rated");
                RetrofitSingleton.getTmdbService().topRated(APIKEY, "hi", "IN", page)
                        .enqueue(new Callback<MovieTopRated>() {
                            @Override
                            public void onResponse(Call<MovieTopRated> call, Response<MovieTopRated> response) {
                                if(response.body().getResults() != null){

                                    for(Result result: response.body().getResults()){
                                        if(Integer.parseInt(result.getReleaseDate().split("-")[0] ) >= 2000)
                                            movies.add(result);
                                    }
                                    adapter.notifyDataSetChanged();

                                    Log.d("PageTotal", response.body().getTotalPages() + "");
                                    Log.d("Page", response.body().getPage() + "");



                                    if(response.body().getPage() < response.body().getTotalPages()){
                                        page += 1;
                                        fetchMoviesByCategory(category);
                                    }
                                }
                            }

                            @Override
                            public void onFailure(Call<MovieTopRated> call, Throwable t) {

                            }
                        });

                break;


            case categoryGenre:
                tvCollectionType.setText(getIntent().getStringExtra("genre"));
                int genre = getIntent().getIntExtra("genre_id", 0);

                RetrofitSingleton.getTmdbService().genre(APIKEY, "hi", "IN", page, true, genre + "", "release_date.desc", "IN", dateFormat.format(new Date()), "2001-01-01")
                        .enqueue(new Callback<MovieDiscover>() {
                            @Override
                            public void onResponse(Call<MovieDiscover> call, Response<MovieDiscover> response) {
                                if(response.body().getResults() != null){
                                    movies.addAll(response.body().getResults());
                                    adapter.notifyDataSetChanged();

                                    Log.d("PageTotal", response.body().getTotalPages() + "");
                                    Log.d("Page", response.body().getPage() + "");



                                    if(response.body().getPage() < response.body().getTotalPages()){
                                        page += 1;
                                        fetchMoviesByCategory(category);
                                    }
                                }
                            }

                            @Override
                            public void onFailure(Call<MovieDiscover> call, Throwable t) {

                            }
                        });

                break;


            case categoryYear:
                int year = getIntent().getIntExtra("year", 2019);
                tvCollectionType.setText("" + year);
                RetrofitSingleton.getTmdbService()
                .year(APIKEY, "hi", "IN", page, true, year, "release_date.desc", "IN", dateFormat.format(new Date()))
                .enqueue(new Callback<MovieDiscover>() {
                    @Override
                    public void onResponse(Call<MovieDiscover> call, Response<MovieDiscover> response) {
                        if(response.body().getResults() != null){
                            movies.addAll(response.body().getResults());
                            adapter.notifyDataSetChanged();

                            Log.d("PageTotal", response.body().getTotalPages() + "");
                            Log.d("Page", response.body().getPage() + "");



                            if(response.body().getPage() < response.body().getTotalPages()){
                                page += 1;
                                fetchMoviesByCategory(category);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<MovieDiscover> call, Throwable t) {

                    }
                });
                break;


            default:
                tvCollectionType.setText("Movies");
                break;


        }



        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MovieGridActivity.super.onBackPressed();
            }
        });


    }


    public int calculateNoOfColumns(Context context, float columnWidthDp) { // For example columnWidthdp=180
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float screenWidthDp = (displayMetrics.widthPixels - 48) / displayMetrics.density;
        float spacing = 48 + (displayMetrics.widthPixels % displayMetrics.density);
        int noOfColumns = (int) (screenWidthDp / columnWidthDp + 0.5); // +0.5 for correct rounding to int.
        return noOfColumns;
    }
}

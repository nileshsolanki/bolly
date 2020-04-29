package com.mobile.bolly.phone.grid;

import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mobile.bolly.R;
import com.mobile.bolly.phone.home.MovieConciseAdapter;
import com.mobile.bolly.models.MovieDetails;
import com.mobile.bolly.models.Result;
import com.mobile.bolly.networking.BollyService;
import com.mobile.bolly.networking.RetrofitSingleton;
import com.mobile.bolly.util.ConnectivityChangeReceiver;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.mobile.bolly.util.Util.fullScreen;

public class MovieGridActivity extends AppCompatActivity {

    RecyclerView gridRecycler;
    MovieConciseAdapter adapter;
    TextView tvCollectionType;
    public static final int CATEGORY_RECENT = 1, CATEGORY_TOPRATED = 2, CATEGORY_GENRE = 3, CATEGORY_YEAR = 4;
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

            case CATEGORY_RECENT:
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
                    public void onFailure(Call<List<MovieDetails>> call, Throwable t) { }
                });

                break;


            case CATEGORY_TOPRATED:
                tvCollectionType.setText("Top Rated");
                RetrofitSingleton.getBollyService().getTopRated().enqueue(new Callback<List<Result>>() {
                    @Override
                    public void onResponse(Call<List<Result>> call, Response<List<Result>> response) {
                        if(response.body() != null){
                            movies.clear();
                            for(Result result: response.body())
                                movies.add(result);

                            adapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Result>> call, Throwable t) { }
                });

                break;


            case CATEGORY_GENRE:
                tvCollectionType.setText(getIntent().getStringExtra("genre"));
                int genre = getIntent().getIntExtra("genre_id", 0);


                RetrofitSingleton.getBollyService().getForGenre(genre).enqueue(new Callback<List<Result>>() {
                    @Override
                    public void onResponse(Call<List<Result>> call, Response<List<Result>> response) {
                        if(response.body() != null){
                            movies.clear();
                            movies.addAll(response.body());
                            adapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Result>> call, Throwable t) { }
                });


                break;


            case CATEGORY_YEAR:
                int year = getIntent().getIntExtra("year", 2019);
                tvCollectionType.setText("" + year);

                RetrofitSingleton.getBollyService().getForYear(year).enqueue(new Callback<List<Result>>() {
                    @Override
                    public void onResponse(Call<List<Result>> call, Response<List<Result>> response) {
                        movies.clear();
                        movies.addAll(response.body());
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onFailure(Call<List<Result>> call, Throwable t) { }
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
        float screenWidthDp = ((displayMetrics.widthPixels) / displayMetrics.density) - 48;
        float spacing = (displayMetrics.widthPixels % displayMetrics.density);
        int noOfColumns = (int) (screenWidthDp / columnWidthDp + 0.5); // +0.5 for correct rounding to int.
        return noOfColumns;
    }
}

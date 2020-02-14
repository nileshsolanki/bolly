package com.mobile.bolly;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.mobile.bolly.R;
import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.mobile.bolly.models.MovieDetails;
import com.mobile.bolly.models.Result;
import com.mobile.bolly.networking.RetrofitSingleton;
import com.mobile.bolly.util.ConnectivityChangeReceiver;

import retrofit2.Call;
import retrofit2.Response;

import static com.mobile.bolly.constants.Tmdb.APIKEY;
import static com.mobile.bolly.constants.Tmdb.POSTER_DOMAIN_500;
import static com.mobile.bolly.util.Common.fullScreen;

public class MovieDetailActivity extends AppCompatActivity{

    BottomSheetBehavior bottomSheetBehavior;
    LinearLayout bottmSheet;
    TextView tvRating, tvTitle, tvRuntime, tvGenre, tvLanguage, tvYear, tvDescription;
    ImageView ivPoster;
    FloatingActionButton fabBookmark;
    MaterialButton btnWatchNow;
    private String RESPONSE = "response";
    MovieDetails details;
    ImageButton btnBack;
    ConnectivityChangeReceiver connectivityReceiver;
    final String [] months = new String[] { "", "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};



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
        setContentView(R.layout.activity_movie_detail);


        btnBack = findViewById(R.id.imgbtn_back);
        ivPoster = findViewById(R.id.iv_poster);
        tvTitle = findViewById(R.id.tv_title);
        tvRuntime = findViewById(R.id.tv_runtime);
        tvLanguage = findViewById(R.id.tv_language);
        tvYear = findViewById(R.id.tv_year);
        tvDescription = findViewById(R.id.tv_description);
        tvGenre = findViewById(R.id.tv_genre);
        tvRating = findViewById(R.id.tv_rating);
        btnWatchNow = findViewById(R.id.btn_watchnow);
        bottmSheet = findViewById(R.id.bottom_sheet);
        bottomSheetBehavior = BottomSheetBehavior.from(bottmSheet);
        fabBookmark = findViewById(R.id.fab_bookmark);

        Intent intent = getIntent();
        Result result = (Result) intent.getSerializableExtra("result");
        if(result != null){

            tvTitle.setText(result.getTitle());
            tvRating.setText(result.getVoteAverage() + "");
            String dateObjs [] = result.getReleaseDate().split("-");
            tvYear.setText(dateObjs[0] + " " + months[Integer.parseInt(dateObjs[1])]);
            tvDescription.setText(result.getOverview());
            tvLanguage.setText(result.getOriginalLanguage().toUpperCase());



        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            String imageTransitionName = result.getId() + "";
            ivPoster.setTransitionName(imageTransitionName);
        }

        Glide.with(this).load(POSTER_DOMAIN_500 + result.getPosterPath()).into(ivPoster);
        fetchDetails(result);


        fabBookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make((CoordinatorLayout)btnBack.getParent(), "Comming Soon", Snackbar.LENGTH_SHORT).show();
            }
        });


        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View view, int newState) {
                switch (newState){
                    case BottomSheetBehavior.STATE_HIDDEN:
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED:
                        bottmSheet.setBackground(getResources().getDrawable(R.color.colorPrimary));
                        tvDescription.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                        break;
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        bottmSheet.setBackground(getResources().getDrawable(R.drawable.bottom_sheet_bg));
                        tvDescription.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dpToPx(72)));
                        break;
                    case BottomSheetBehavior.STATE_DRAGGING:
                        bottmSheet.setBackground(getResources().getDrawable(R.color.colorPrimary));
                        break;
                    case BottomSheetBehavior.STATE_SETTLING:
                        bottmSheet.setBackground(getResources().getDrawable(R.drawable.bottom_sheet_bg));
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View view, float v) {

            }
        });

    }

    public static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    private void fetchDetails(Result result) {

        RetrofitSingleton.getTmdbService().details(result.getId(), APIKEY).enqueue(new retrofit2.Callback<MovieDetails>() {
            @Override
            public void onResponse(Call<MovieDetails> call, Response<MovieDetails> response) {
                details = response.body();
                Log.d(RESPONSE, details.getRuntime() + "");
                tvRuntime.setText(details.getRuntime() + "m");
                if(details.getGenres().size() >= 1)
                    tvGenre.setText(details.getGenres().get(0).getName());


                btnWatchNow.setOnClickListener(onWatchClick(response.body().getImdbId()));
            }

            @Override
            public void onFailure(Call<MovieDetails> call, Throwable t) {

            }
        });


        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MovieDetailActivity.super.onBackPressed();
            }
        });

    }


    private View.OnClickListener onWatchClick(String id){
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent watchIntent = new Intent(MovieDetailActivity.this, WatchActivity.class);
                watchIntent.putExtra("id", id);
                startActivity(watchIntent);
            }
        };
    }

}
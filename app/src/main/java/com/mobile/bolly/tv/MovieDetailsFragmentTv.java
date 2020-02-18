package com.mobile.bolly.tv;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.DisplayMetrics;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.leanback.app.BackgroundManager;
import androidx.leanback.app.DetailsSupportFragment;
import androidx.leanback.widget.Action;
import androidx.leanback.widget.ArrayObjectAdapter;
import androidx.leanback.widget.ClassPresenterSelector;
import androidx.leanback.widget.DetailsOverviewRow;
import androidx.leanback.widget.FullWidthDetailsOverviewRowPresenter;
import androidx.leanback.widget.HeaderItem;
import androidx.leanback.widget.ListRow;
import androidx.leanback.widget.ListRowPresenter;
import androidx.leanback.widget.OnActionClickedListener;
import androidx.leanback.widget.SparseArrayObjectAdapter;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.mobile.bolly.WatchActivity;
import com.mobile.bolly.constants.Tmdb;
import com.mobile.bolly.models.MovieExternalIds;
import com.mobile.bolly.models.Result;
import com.mobile.bolly.networking.RetrofitSingleton;
import com.mobile.bolly.util.Util;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.mobile.bolly.constants.Tmdb.APIKEY;

public class MovieDetailsFragmentTv extends DetailsSupportFragment {

    private static final int DETAIL_THUMB_WIDTH = 274, DETAIL_THUMB_HEIGHT = 274;
    public static final String MOVIE = "Movie";


    private FullWidthDetailsOverviewRowPresenter mFwdorPresenter;
    private SimpleBackgroundManager simpleBackgroundManager;

    private Result selectedMovie;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        selectedMovie = (Result) getActivity().getIntent().getSerializableExtra("result");
        simpleBackgroundManager = new SimpleBackgroundManager(getActivity());
        mFwdorPresenter = new FullWidthDetailsOverviewRowPresenter(new DetailsDescriptionPresenter());

        detailsRowBuilder(selectedMovie);
        simpleBackgroundManager.setBackground(Tmdb.BACKDROP_URL_1280 + selectedMovie.getBackdropPath());

        if(selectedMovie != null)
            fetchMovieDetails(selectedMovie.getId());

    }



    private void detailsRowBuilder(Result selectedMovie) {

        Context applicationContext = getActivity().getApplicationContext();
        DetailsOverviewRow detailsOverviewRow = new DetailsOverviewRow(selectedMovie);
        Glide.with(getActivity())
                .load(Tmdb.POSTER_DOMAIN_500 + selectedMovie.getPosterPath())
                .override(Util.convertDpToPixel(applicationContext, DETAIL_THUMB_WIDTH), Util.convertDpToPixel(applicationContext, DETAIL_THUMB_HEIGHT))
                .centerCrop()
                .into(new CustomTarget<Drawable>() {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                        detailsOverviewRow.setImageDrawable(resource);
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) { }
                });

        SparseArrayObjectAdapter actions = new SparseArrayObjectAdapter();
        actions.set(0, new Action(707, "Watch Now"));
//        actions.set(1, new Action(1, "Report Incorrect File"));
//        actions.set(2, new Action(2, "Report No File"));

        detailsOverviewRow.setActionsAdapter(actions);

        mFwdorPresenter.setInitialState(FullWidthDetailsOverviewRowPresenter.STATE_HALF);

        ArrayObjectAdapter adapter = new ArrayObjectAdapter(mFwdorPresenter);
        adapter.add(detailsOverviewRow);

        setAdapter(adapter);
    }


    private void fetchMovieDetails(Integer tmdbId) {

        RetrofitSingleton.getTmdbService().externalIds(tmdbId, APIKEY).enqueue(new Callback<MovieExternalIds>() {
            @Override
            public void onResponse(Call<MovieExternalIds> call, Response<MovieExternalIds> response) {
                if(response.body() != null)
                    setupActionListeners(mFwdorPresenter, response.body().getImdbId());
            }

            @Override
            public void onFailure(Call<MovieExternalIds> call, Throwable t) {

            }
        });

    }

    private void setupActionListeners(FullWidthDetailsOverviewRowPresenter mFwdorPresenter, String imdbId) {

        mFwdorPresenter.setOnActionClickedListener(new OnActionClickedListener() {
            @Override
            public void onActionClicked(Action action) {
                if(action.getId() == (long)707){

                    Intent watch = new Intent(getActivity(), WatchActivity.class);
                    watch.putExtra("id", imdbId);
                    startActivity(watch);

                }
            }
        });

    }
}

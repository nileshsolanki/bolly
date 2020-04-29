package com.mobile.bolly.television.detail;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.leanback.app.DetailsSupportFragment;
import androidx.leanback.widget.Action;
import androidx.leanback.widget.ArrayObjectAdapter;
import androidx.leanback.widget.DetailsOverviewRow;
import androidx.leanback.widget.FullWidthDetailsOverviewRowPresenter;
import androidx.leanback.widget.OnActionClickedListener;
import androidx.leanback.widget.SparseArrayObjectAdapter;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.mobile.bolly.phone.watch.WatchActivity;
import com.mobile.bolly.constants.Tmdb;
import com.mobile.bolly.models.Result;
import com.mobile.bolly.networking.RetrofitSingleton;
import com.mobile.bolly.television.common.SimpleBackgroundManager;
import com.mobile.bolly.util.DownloadingForegroundService;
import com.mobile.bolly.util.Util;

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

        mFwdorPresenter.setOnActionClickedListener(new ActionClickedListener());

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
        actions.set(0, new Action(9022, "Watch Now"));
        actions.set(1, new Action(9023, "Download"));
        actions.set(2, new Action(9024, "Report Incorrect or No Play"));

        detailsOverviewRow.setActionsAdapter(actions);

        mFwdorPresenter.setInitialState(FullWidthDetailsOverviewRowPresenter.STATE_HALF);

        ArrayObjectAdapter adapter = new ArrayObjectAdapter(mFwdorPresenter);
        adapter.add(detailsOverviewRow);

        setAdapter(adapter);
    }


    private class ActionClickedListener implements OnActionClickedListener{

        @Override
        public void onActionClicked(Action action) {
            switch ((int)action.getId()){

                case 9022:
                    if(selectedMovie == null){
                        Util.showToast(getContext(),"Please Check Internet");
                        return;
                    }
                    Intent watch = new Intent(getActivity(), WatchActivity.class);
                    watch.putExtra("id", selectedMovie.getId());
                    startActivity(watch);
                    break;

                case 9023:
                    Util.showToast(getContext(), "Starting Download");
                    Intent downloader = new Intent(getContext(), DownloadingForegroundService.class);
                    downloader.putExtra("id", selectedMovie.getId());
                    ContextCompat.startForegroundService(getContext(), downloader);
                    break;

                case 9024:
                    RetrofitSingleton.postReport(selectedMovie.getId(), 1, 0);
                    Util.showToast(getContext(), "ThankYou");
                    action.setId(System.currentTimeMillis());
                    break;


            }
        }
    }
}

package com.mobile.bolly.tv;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.leanback.app.BackgroundManager;
import androidx.leanback.app.BrowseSupportFragment;
import androidx.leanback.widget.ArrayObjectAdapter;
import androidx.leanback.widget.HeaderItem;
import androidx.leanback.widget.ListRow;
import androidx.leanback.widget.ListRowPresenter;
import androidx.leanback.widget.OnItemViewClickedListener;
import androidx.leanback.widget.OnItemViewSelectedListener;
import androidx.leanback.widget.Presenter;
import androidx.leanback.widget.Row;
import androidx.leanback.widget.RowPresenter;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.mobile.bolly.R;
import com.mobile.bolly.constants.Tmdb;
import com.mobile.bolly.models.Result;

import java.util.Arrays;

public class MainFragmentTv extends BrowseSupportFragment {

    public static final String BACKDROP_URL_1280 = "https://image.tmdb.org/t/p/w1280";

    ArrayObjectAdapter allCategoryRows;
    SimpleBackgroundManager simpleBackgroundManager;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setupUIElements();
        loadRows();
        setupEventListeners();
        simpleBackgroundManager = new SimpleBackgroundManager(getActivity());

    }

    private void setupEventListeners() {
        setOnItemViewSelectedListener( new ItemViewSelectedListener() );
        setOnItemViewClickedListener( new ItemViewClickedListener() );
    }

    private void loadRows() {
        //Object adapter for all categories
        allCategoryRows = new ArrayObjectAdapter( new ListRowPresenter());

        //header items
        HeaderItem recentAdditionsHeader = new HeaderItem(0, "Recent Additions");
        HeaderItem topRatedHeader = new HeaderItem(1, "Top Rated");
        HeaderItem genreHeader = new HeaderItem(2, "Hot Genres");
        HeaderItem yearHeader = new HeaderItem(3, "Year");

        //row adapters
        ArrayObjectAdapter recentMoviesRowAdapter = MovieCardPresenter.getRecentMoviesRowAdapter(getContext());
        ArrayObjectAdapter topRatedMoviesRowAdapter = MovieCardPresenter.getTopRatedMoviesRowAdapter(getContext());
        ArrayObjectAdapter genreRowAdapter = GenreItemPresenter.getGenreRowAdapter(getContext());
        ArrayObjectAdapter yearRowAdapter = YearItemPresenter.getYearRowAdapter(getContext());


        //adding all rows
        allCategoryRows.add(new ListRow(recentAdditionsHeader, recentMoviesRowAdapter));
        allCategoryRows.add(new ListRow(topRatedHeader, topRatedMoviesRowAdapter));
        allCategoryRows.add(new ListRow(genreHeader, genreRowAdapter));
        allCategoryRows.add(new ListRow(yearHeader, yearRowAdapter));
        setAdapter(allCategoryRows);

    }


    private void setupUIElements() {
         setBadgeDrawable(getActivity().getResources().getDrawable(R.drawable.logo));
        //setTitle("bolly"); // Badge, when set, takes precedent
        // over title
        setHeadersState(HEADERS_ENABLED);
        setHeadersTransitionOnBackEnabled(true);

        // set fastLane (or headers) background color
        //setBrandColor(getResources().getColor(R.color.fastlane_background));

        // set search icon color
        setSearchAffordanceColor(getResources().getColor(R.color.lb_tv_white));
    }




    private class ItemViewClickedListener implements OnItemViewClickedListener{

        @Override
        public void onItemClicked(Presenter.ViewHolder itemViewHolder, Object item, RowPresenter.ViewHolder rowViewHolder, Row row) {




            switch ((int)row.getHeaderItem().getId()){

                case 0:
                    //recent movie card
                    //allow fall
                    //top rated
                    Intent recentDetail = new Intent(getActivity(), DetailsActivity.class);
                    Result recentMovie = (Result) item;
                    recentDetail.putExtra("category", 1);
                    recentDetail.putExtra("result", recentMovie);
                    getActivity().startActivity(recentDetail);

                case 1:
                    //top rated
                    Intent topratedDetail = new Intent(getActivity(), DetailsActivity.class);
                    Result topMovie = (Result) item;
                    topratedDetail.putExtra("category", 2);
                    topratedDetail.putExtra("result", topMovie);
                    getActivity().startActivity(topratedDetail);
                    break;

                case 2:
                    //genre
                    Intent genreGrid = new Intent(getActivity(), MovieGridActivityTv.class);
                    genreGrid.putExtra("category", 3);
                    genreGrid.putExtra("genre", (String)item);
                    int genreIndex = Arrays.asList(Tmdb.genres).indexOf((String)item);
                    genreGrid.putExtra("genre_id", Tmdb.genreIds[genreIndex]);
                    startActivity(genreGrid);
                    break;

                case 3:
                    //year
                    Intent yearGrid = new Intent(getActivity(), MovieGridActivityTv.class);
                    yearGrid.putExtra("category", 4);
                    yearGrid.putExtra("year", (String) item);
                    startActivity(yearGrid);
                    break;

            }
        }
    }



    private class ItemViewSelectedListener implements OnItemViewSelectedListener{

        @Override
        public void onItemSelected(Presenter.ViewHolder itemViewHolder, Object item, RowPresenter.ViewHolder rowViewHolder, Row row) {
            if(item instanceof String){
                simpleBackgroundManager.clearBackground();
            }
            else if(item instanceof Result){
                simpleBackgroundManager.setBackground(BACKDROP_URL_1280 + ((Result)item).getBackdropPath());
            }
        }
    }
}

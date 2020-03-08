package com.mobile.bolly.tv;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Gravity;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;
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
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.mobile.bolly.R;
import com.mobile.bolly.constants.Tmdb;
import com.mobile.bolly.models.Result;
import com.mobile.bolly.util.DownloadingForegroundService;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static com.mobile.bolly.util.DownloadingForegroundService.ACTION_STOP_DOWNLOAD;
import static com.mobile.bolly.util.Util.startMxPlayer;

public class MainFragmentTv extends BrowseSupportFragment {

    private String TAG = getClass().getSimpleName();
    public static final String BACKDROP_URL_1280 = "https://image.tmdb.org/t/p/w1280";

    ArrayObjectAdapter allCategoryRows;
    SimpleBackgroundManager simpleBackgroundManager;

    DownloadedItemPresenter downloadedItemPresenter;
    DownloadingItemPresenter downloadingItemPresenter;


    BroadcastReceiver progressReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if(intent.getAction().equals(ACTION_STOP_DOWNLOAD)){

                if(downloadedItemPresenter != null) {
                    downloadedItemPresenter.updateTitle(null);
                    downloadingItemPresenter.updateProgress(null, null, null);
                }

            }else if(intent.getAction().equals(DownloadingForegroundService.ACTION_DOWNLOAD_PROGRESS)) {
                String title = intent.getStringExtra("title");
                String progress = intent.getStringExtra("progress");
                String bytesString = intent.getStringExtra("downloadedVsTotal");

                Log.d(TAG, "title: " + title + " progress: " + progress);
                if(downloadedItemPresenter != null && downloadingItemPresenter != null) {
                    downloadedItemPresenter.updateTitle(title);
                    downloadingItemPresenter.updateProgress(title, progress, bytesString);
                }


            }

        }
    };



    @Override
    public void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter();
        filter.addAction(DownloadingForegroundService.ACTION_DOWNLOAD_PROGRESS);
        filter.addAction(ACTION_STOP_DOWNLOAD);

        LocalBroadcastManager.getInstance(getContext()).registerReceiver(progressReceiver, filter);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setupUIElements();
        loadRows();
        setupEventListeners();
        simpleBackgroundManager = new SimpleBackgroundManager(getActivity());
        downloadedItemPresenter = new DownloadedItemPresenter(getContext());
        downloadingItemPresenter = new DownloadingItemPresenter(getContext());


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
        HeaderItem downloadedHeader = new HeaderItem(4, "Saved");
        HeaderItem downloadingHeader = new HeaderItem(5, "Downloading");

        //row adapters
        ArrayObjectAdapter recentMoviesRowAdapter = MovieCardPresenter.getRecentMoviesRowAdapter(getContext());
        ArrayObjectAdapter topRatedMoviesRowAdapter = MovieCardPresenter.getTopRatedMoviesRowAdapter(getContext());
        ArrayObjectAdapter genreRowAdapter = GenreItemPresenter.getGenreRowAdapter(getContext());
        ArrayObjectAdapter yearRowAdapter = YearItemPresenter.getYearRowAdapter(getContext());
        ArrayObjectAdapter downloadedRowAdaper = DownloadedItemPresenter.getDownloadedRowAdapter(getContext());
        ArrayObjectAdapter downloadingRowAdapter = DownloadingItemPresenter.getDownloadingRowAdapter(getContext());


        //adding all rows
        allCategoryRows.add(new ListRow(recentAdditionsHeader, recentMoviesRowAdapter));
        allCategoryRows.add(new ListRow(topRatedHeader, topRatedMoviesRowAdapter));
        allCategoryRows.add(new ListRow(genreHeader, genreRowAdapter));
        allCategoryRows.add(new ListRow(yearHeader, yearRowAdapter));
        allCategoryRows.add(new ListRow(downloadedHeader, downloadedRowAdaper));
        allCategoryRows.add(new ListRow(downloadingHeader, downloadingRowAdapter));
        setAdapter(allCategoryRows);

    }


    private void setupUIElements() {
         setBadgeDrawable(getActivity().getResources().getDrawable(R.drawable.logo_white));
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


                case 4:
                    //saved movies
                    if(((String)item).equals("Your downloaded files will appear here"))
                        return;
                    AlertDialog savedActions = downloadedItemPresenter.getSavedActionsAsAlert(getContext(), item);
                    savedActions.getWindow().setGravity(Gravity.BOTTOM);
                    savedActions.show();
                    savedActions.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

                    break;


                case 5:
                    //downloading

                    HashMap<String, String> progressVar = (HashMap<String, String>)item;
                    String progress = progressVar.get("progress");
                    if((progress == null || progress.isEmpty()))
                        return;
                    AlertDialog downloadingActions = downloadingItemPresenter.getDownloadingActionsAsAlert(getContext());
                    downloadingActions.getWindow().setGravity(Gravity.BOTTOM);
                    downloadingActions.show();
                    downloadingActions.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);


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


    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(progressReceiver);
    }
}

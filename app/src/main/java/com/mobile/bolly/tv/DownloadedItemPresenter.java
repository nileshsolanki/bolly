package com.mobile.bolly.tv;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Environment;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.leanback.widget.ArrayObjectAdapter;
import androidx.leanback.widget.Presenter;

import com.mobile.bolly.R;
import com.mobile.bolly.adapter.MovieSavedAdapter;
import com.mobile.bolly.util.Util;

import java.io.File;
import java.util.List;

import static com.mobile.bolly.util.Util.startMxPlayer;

public class DownloadedItemPresenter extends Presenter {

    Context context;
    public static final int CARD_WIDTH = 400, CARD_HEIGHT = 160;
    static String downloadingTitle = null;
    static ArrayObjectAdapter downloadedRowAdapter;

    public DownloadedItemPresenter(Context context) {
        this.context = context;
    }


    public void updateTitle(String title){
        this.downloadingTitle = title;
        populateAdapter(context, downloadedRowAdapter);

    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent) {
        TextView view = new TextView(parent.getContext());
        view.setPadding(72, 0, 72, 0);
        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, CARD_HEIGHT));
        view.setMaxWidth(CARD_WIDTH);
        view.setFocusable(true);
        view.setFocusableInTouchMode(true);
        view.setBackgroundColor(context.getResources().getColor(R.color.default_background_card));
        view.setTextColor(context.getResources().getColor(android.R.color.white));
        view.setGravity(Gravity.CENTER);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Object item) {
        String title = (String) item;
        int lastIndexOfDot = title.lastIndexOf(".");
        if(lastIndexOfDot > 0)
            ((TextView)viewHolder.view).setText(title.substring(0, lastIndexOfDot));
        else
            ((TextView)viewHolder.view).setText(title);
    }

    @Override
    public void onUnbindViewHolder(ViewHolder viewHolder) {

    }



    public static ArrayObjectAdapter getDownloadedRowAdapter(Context context){

        DownloadedItemPresenter downloadedItemPresenter = new DownloadedItemPresenter(context);
        downloadedRowAdapter = new ArrayObjectAdapter(downloadedItemPresenter);


        populateAdapter(context, downloadedRowAdapter);

        return downloadedRowAdapter;

    }


    private static void populateAdapter(Context context, ArrayObjectAdapter downloadedRowAdapter){
        downloadedRowAdapter.clear();
        List<String> savedMovies = MovieSavedAdapter.searchSavedMovies(context, downloadingTitle);
        for (String movieTitle : savedMovies)
            downloadedRowAdapter.add(movieTitle);

        //downloadedRowAdapter.notifyArrayItemRangeChanged(0, savedMovies.size());

        if(downloadedRowAdapter.size() == 0)
            downloadedRowAdapter.add("Your downloaded files will appear here");

    }



    public AlertDialog getSavedActionsAsAlert(Context context, Object item){
        String title = (String) item;
        AlertDialog savedActions = new AlertDialog.Builder(context, R.style.Theme_AppCompat_Dialog)
                .setTitle((title.substring( 0, title.lastIndexOf(".") )))
                .setItems(
                        new String[]{"Play", "Remove", "Dismiss"},
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                switch (i){
                                    case 0:
                                        //startMxPlayer
                                        File file = new File(context.getExternalFilesDir(Environment.DIRECTORY_MOVIES) + "/" + (String)item);
                                        startMxPlayer(context, Util.getUriForFile(context, file).toString(), null);
                                        break;

                                    case 1:
                                        //delete
                                        File fileToDelete = new File(context.getExternalFilesDir(Environment.DIRECTORY_MOVIES) + "/" + (String)item);
                                        Util.deleteFile(fileToDelete);
                                        break;

                                    case 2:
                                        //dismiss
                                        dialogInterface.dismiss();
                                }
                            }
                        })
                .create();


        return savedActions;
    }



}

package com.mobile.bolly.tv;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;
import androidx.leanback.widget.ArrayObjectAdapter;
import androidx.leanback.widget.Presenter;

import com.mobile.bolly.R;
import com.mobile.bolly.util.Util;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static com.mobile.bolly.util.DownloadingForegroundService.ACTION_STOP_DOWNLOAD;
import static com.mobile.bolly.util.Util.startMxPlayer;


public class DownloadingItemPresenter extends Presenter {


    Context context;
    private static final int CARD_WIDTH = 600, CARD_HEIGHT = 300;
    static HashMap<String, String> progressVars;
    static ArrayObjectAdapter downloadingRowAdapter;


    public DownloadingItemPresenter(Context context) {
        this.context = context;
    }


    public static void updateProgress(String title, String progress, String downloadedVsTotal){

        progressVars = new HashMap<>();
        progressVars.put("title", title);
        progressVars.put("progress", progress);
        progressVars.put("downloadedVsTotal", downloadedVsTotal);

        downloadingRowAdapter.clear();
        if(title != null && progressVars.get("title") != null)
            downloadingRowAdapter.add(progressVars);


        if(downloadingRowAdapter.size() == 0){
            HashMap<String, String> empty = new HashMap<>();
            empty.put("title", "0 dowload processes");
            empty.put("progress", "");
            empty.put("downloadedVsTotal", "");

            downloadingRowAdapter.add(empty);
        }

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent) {

        LinearLayout linearLayout = new LinearLayout(parent.getContext());
        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(CARD_WIDTH, CARD_HEIGHT));
        int padding = Util.convertDpToPixel(context, 12);
        linearLayout.setPadding(padding, padding, padding, padding);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setGravity(Gravity.CENTER_HORIZONTAL);
        linearLayout.setFocusable(true);
        linearLayout.setFocusableInTouchMode(true);
        linearLayout.setBackgroundColor(context.getResources().getColor(R.color.default_background_card));


        TextView tvTitle = new TextView(parent.getContext());
        tvTitle.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        tvTitle.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        tvTitle.setTextSize(Util.convertDpToPixel(context, 9));
        tvTitle.setTextColor(context.getResources().getColor(android.R.color.white));



        TextView tvProgressPercent = new TextView(parent.getContext());
        tvProgressPercent.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
        tvProgressPercent.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        tvProgressPercent.setGravity(Gravity.CENTER);
        tvProgressPercent.setTextSize(Util.convertDpToPixel(context, 14));
        tvProgressPercent.setTextColor(context.getResources().getColor(android.R.color.white));



        TextView tvProgressBytes = new TextView(parent.getContext());
        tvProgressBytes.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        tvProgressBytes.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        tvProgressBytes.setTextSize(Util.convertDpToPixel(context, 7));
        tvProgressBytes.setTextColor(context.getResources().getColor(android.R.color.white));


        linearLayout.addView(tvTitle);
        linearLayout.addView(tvProgressPercent);
        linearLayout.addView(tvProgressBytes);

        return new ViewHolder(linearLayout);

    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Object item) {

        HashMap<String, String> progressVars = (HashMap<String, String>)item;
        LinearLayout linearLayout = (LinearLayout) viewHolder.view;

        ((TextView)linearLayout.getChildAt(0)).setText(progressVars.get("title"));
        ((TextView)linearLayout.getChildAt(1)).setText(progressVars.get("progress") + "%");
        ((TextView)linearLayout.getChildAt(2)).setText(progressVars.get("downloadedVsTotal"));


    }

    @Override
    public void onUnbindViewHolder(ViewHolder viewHolder) {

    }



    public static ArrayObjectAdapter getDownloadingRowAdapter(Context context){


        DownloadingItemPresenter downloadingItemPresenter = new DownloadingItemPresenter(context);
        downloadingRowAdapter = new ArrayObjectAdapter(downloadingItemPresenter);

        updateProgress(null, null, null);

        return downloadingRowAdapter;

    }



    public AlertDialog getDownloadingActionsAsAlert(Context context){
        AlertDialog downloadingActions = new AlertDialog.Builder(context, R.style.Theme_AppCompat_Dialog_Alert)
                .setTitle(progressVars.get("title"))
                .setItems(
                        new String[]{ "Play", "Stop", "Dismiss"},
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                switch (i){
                                    case 0:
                                        //start Mxplayer
                                        String []filesList = new File(context.getExternalFilesDir(Environment.DIRECTORY_MOVIES) + "").list();
                                        String title = progressVars.get("title");
                                        for(String fileName: filesList){
                                            if(title != null && fileName.contains(title)){
                                                String extension = fileName.substring(fileName.lastIndexOf("."));
                                                File file = new File(context.getExternalFilesDir(Environment.DIRECTORY_MOVIES) + "/" + title + extension);
                                                startMxPlayer(context, Util.getUriForFile(context, file).toString(), null);
                                                break;
                                            }
                                        }

                                        break;

                                    case 1:
                                        //stop download
                                        Intent stopIntent = new Intent().setAction(ACTION_STOP_DOWNLOAD);
                                        context.sendBroadcast(stopIntent);
                                        break;

                                    case 2:
                                        dialogInterface.dismiss();
                                }

                            }
                        }).create();


        return downloadingActions;
    }
}

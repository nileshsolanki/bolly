package com.mobile.bolly.phone.home;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.mobile.bolly.R;
import com.mobile.bolly.phone.home.MovieSavedAdapter;
import com.mobile.bolly.util.DownloadingForegroundService;

import java.io.File;

import static com.mobile.bolly.util.Util.startMxPlayer;

public class BookmarkFragment extends Fragment implements View.OnClickListener {
    View v;
    private String TAG = this.getClass().getSimpleName();
    private static String title = null;

    MaterialCardView cvCurrentDownload;
    TextView tvMovieTitle, tvProgress, tvDownloadedVsTotal;
    MaterialButton btnStartPlayback;

    MovieSavedAdapter adapter;
    RecyclerView rvSavedMovies;

    BroadcastReceiver progressReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(DownloadingForegroundService.ACTION_STOP_DOWNLOAD)){
                cvCurrentDownload.setVisibility(View.GONE);
                adapter.updateTitle(null);
            }else if(intent.getAction().equals(DownloadingForegroundService.ACTION_DOWNLOAD_PROGRESS)) {
                title = intent.getStringExtra("title");
                String progress = intent.getStringExtra("progress");
                String downloadedVsTotal = intent.getStringExtra("downloadedVsTotal");

                cvCurrentDownload.setVisibility(View.VISIBLE);
                tvDownloadedVsTotal.setText(downloadedVsTotal);
                tvMovieTitle.setText(title);
                tvProgress.setText(progress + "%");
                adapter.updateTitle(title);
            }else if(intent.getAction().equals(DownloadingForegroundService.ACTION_COMPLETE_DOWNLOAD)){

                cvCurrentDownload.setVisibility(View.GONE);
                adapter.updateTitle(null);

            }

        }
    };


    @Override
    public void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter();
        filter.addAction(DownloadingForegroundService.ACTION_DOWNLOAD_PROGRESS);
        filter.addAction(DownloadingForegroundService.ACTION_STOP_DOWNLOAD);
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(progressReceiver, filter);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_bookmark, container, false);
        rvSavedMovies = v.findViewById(R.id.rv_saved_movies);
        cvCurrentDownload = v.findViewById(R.id.cv_current_download);
        tvDownloadedVsTotal = v.findViewById(R.id.tv_downloaded_vs_total);
        tvMovieTitle = v.findViewById(R.id.tv_movie_title);
        tvProgress = v.findViewById(R.id.tv_progress);
        btnStartPlayback = v.findViewById(R.id.btn_start_playback);
        btnStartPlayback.setOnClickListener(this);

        adapter = new MovieSavedAdapter(getContext(), title);
        rvSavedMovies.setHasFixedSize(true);
        rvSavedMovies.setAdapter(adapter);

        return v;
    }




    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(progressReceiver);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){

            case R.id.btn_start_playback:
                for(String fileName : new File(getContext().getExternalFilesDir(Environment.DIRECTORY_MOVIES) + "").list()){
                    if(fileName.contains(title)){

                        Uri fileUri;
                        File file = new File(getContext().getExternalFilesDir(Environment.DIRECTORY_MOVIES) + "/" + fileName);
                        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                            fileUri = FileProvider.getUriForFile(getContext().getApplicationContext(), getContext().getOpPackageName() + ".provider", file);
                        }else{
                            fileUri = Uri.fromFile(file);
                        }

                        startMxPlayer(getContext(), fileUri.toString(), v);
                        break;
                    }
                }


        }
    }
}

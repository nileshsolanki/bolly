package com.mobile.bolly;

import android.Manifest;
import android.app.UiModeManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.google.android.material.button.MaterialButton;
import com.mobile.bolly.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.frostwire.jlibtorrent.TorrentInfo;
import com.github.se_bastiaan.torrentstream.StreamStatus;
import com.github.se_bastiaan.torrentstream.Torrent;
import com.github.se_bastiaan.torrentstream.TorrentOptions;
import com.github.se_bastiaan.torrentstreamserver.TorrentServerListener;
import com.github.se_bastiaan.torrentstreamserver.TorrentStreamNotInitializedException;
import com.github.se_bastiaan.torrentstreamserver.TorrentStreamServer;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.google.android.material.snackbar.Snackbar;
import com.mobile.bolly.models.Movie;
import com.mobile.bolly.networking.RetrofitSingleton;
import com.mobile.bolly.util.DownloadingForegroundService;

import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Target;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;

import okhttp3.Connection;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Url;

import static android.view.View.GONE;
import static com.mobile.bolly.util.Common.fullScreen;
import static com.google.android.exoplayer2.Player.DISCONTINUITY_REASON_SEEK;
import static com.mobile.bolly.util.Util.showToast;
import static com.mobile.bolly.util.Util.startMxPlayer;

public class WatchActivity extends AppCompatActivity implements TorrentServerListener {

    PlayerView exoplayerView;
    ImageView ivLoading;
    TorrentStreamServer torrentStreamServer;
    TextView tvProgress;
    FrameLayout flRoot;
    private String TORRENT = "torrent";
    SimpleExoPlayer player = null;
    private static int currentProgress = 0;
    ImageButton btnBack;
    String id;

    LinearLayout llLoading;
    ViewSwitcher viewSwitcher;
    MaterialButton btnReport;


    @Override
    public void onBackPressed() {
        if(exoplayerView.getVisibility() == View.VISIBLE){
            exoplayerView.setVisibility(GONE);
            releaseExoplayer();
            releaseTorrentStream();
            return;
        }

        super.onBackPressed();


    }

    @Override
    protected void onStart() {
        super.onStart();
        fullScreen(this);
    }

    //todo : perform check for pause
    @Override
    protected void onPause() {
        super.onPause();
//        releaseExoplayer();
//        releaseTorrentStream();
    }


    @Override
    protected void onStop() {
        super.onStop();
//        releaseExoplayer();
//        releaseTorrentStream();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watch);


        flRoot = findViewById(R.id.fl_root);
        tvProgress = findViewById(R.id.tv_progress);
        btnBack = findViewById(R.id.imgbtn_back);
        exoplayerView = findViewById(R.id.exoplayer);
        ivLoading = findViewById(R.id.iv_loading);

        llLoading = findViewById(R.id.ll_loading);
        viewSwitcher = findViewById(R.id.view_switcher);
        btnReport = findViewById(R.id.btn_report);

        delayedViewSwitcherDisplay();

        //setUiConfigurations();

        Glide.with(WatchActivity.this).load(R.drawable.popcorn_comming).into(ivLoading);


        String id = getIntent().getStringExtra("id");
        if(id != null){
            this.id = id;
            String ipAddress = "127.0.0.1";
            try {
                InetAddress inetAddress = getIpAddress(this);
                if (inetAddress != null) {
                    ipAddress = inetAddress.getHostAddress();
                }
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }

            TorrentOptions torrentOptions = new TorrentOptions.Builder()
                    .saveLocation(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS))
                    .removeFilesAfterStop(false)
                    .autoDownload(true)
                    .build();

            torrentStreamServer = TorrentStreamServer.getInstance();
            torrentStreamServer.setTorrentOptions(torrentOptions);
            torrentStreamServer.setServerHost(ipAddress);
            torrentStreamServer.setServerPort( (int) (Math.random() * (65535 - 49152)) + 49152 );
            torrentStreamServer.startTorrentStream();
            torrentStreamServer.addListener(this);
            fetchTorrent(id);


        }else{
            exit("We are sorry this content is not available. Exiting");
        }



        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WatchActivity.super.onBackPressed();
            }
        });


        btnReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RetrofitSingleton.postReport(id, 1, 0);
                viewSwitcher.showNext();
            }
        });
    }

    private void delayedViewSwitcherDisplay() {

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if(viewSwitcher != null)
                    viewSwitcher.setVisibility(View.VISIBLE);
            }
        };

        Handler handler = new Handler();
        handler.postDelayed(runnable, 20*1000);


    }

    private void exit(String message) {
        llLoading.setVisibility(View.INVISIBLE);
        showToast(this, message);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                WatchActivity.super.onBackPressed();
            }
        }, 5000);

    }


    private void setUiConfigurations(){
        UiModeManager uiModeManager = (UiModeManager) getSystemService(UI_MODE_SERVICE);
        if (uiModeManager.getCurrentModeType() == Configuration.UI_MODE_TYPE_TELEVISION) {
            flRoot.setBackground(new ColorDrawable(getResources().getColor(R.color.colorSecondaryText)));
        }
    }



    private void fetchTorrent(String id){
        Log.d(TORRENT, "fetching for id "+ id);
        RetrofitSingleton.getBollyService().getMovieDetails(id).enqueue(new retrofit2.Callback<Movie>() {
            @Override
            public void onResponse(Call<Movie> call, Response<Movie> response) {

                if(response.body() == null){
                    exit("Not yet available! Stay tuned. Exiting");
                    return;
                }

                Log.d(TORRENT, response.body().getSelectedTorrent().getTitle());
                String magnet = response.body().getSelectedTorrent().getMagnet();
                try {
                    torrentStreamServer.startStream(magnet);
                } catch (IOException | TorrentStreamNotInitializedException e) {
                    e.printStackTrace();
                    Toast.makeText(WatchActivity.this, "Error!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Movie> call, Throwable t) {
                Log.d(TORRENT, t.getMessage());

            }
        });
    }




    private void playVideo(Uri url){
        player = new SimpleExoPlayer.Builder(this).build();
        exoplayerView.setShowBuffering(PlayerView.SHOW_BUFFERING_WHEN_PLAYING);
        exoplayerView.setPlayer(player);
        player.addListener(new Player.EventListener() {
            @Override
            public void onPositionDiscontinuity(int reason) {
                if(reason == DISCONTINUITY_REASON_SEEK)
                    setInterestedBytes();
            }
        });

        // Produces DataSource instances through which media data is loaded.
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(this, Util.getUserAgent(this, "bolly"));
        // This is the MediaSource representing the media to be played.
        MediaSource videoSource = new ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(url);
        // Prepare the player with the source.
        player.prepare(videoSource);
        exoplayerView.setVisibility(View.VISIBLE);
        player.setPlayWhenReady(true);
    }

    private void setInterestedBytes() {

        TorrentInfo torrentInfo = torrentStreamServer.getCurrentTorrent().getTorrentHandle().torrentFile();
        int numFiles = torrentInfo.numFiles();
        for(int i = 0; i < numFiles; i++){
            if(torrentInfo.files().fileName(i).equals(torrentStreamServer.getCurrentTorrent().getVideoFile().getName())){
                long offset = torrentInfo.files().fileOffset(i);
                long offsetInVideo = (player.getCurrentPosition() / player.getDuration() ) * torrentInfo.files().fileSize(i);
                long totalOffset = offset + offsetInVideo;
                torrentStreamServer.getCurrentTorrent().setInterestedBytes(totalOffset);
            }
        }

    }


    public static InetAddress getIpAddress(Context context) throws UnknownHostException {
        WifiManager wifiMgr = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
        int ip = wifiInfo.getIpAddress();

        if (ip == 0) {
            return null;
        } else {
            byte[] ipAddress = convertIpAddress(ip);
            return InetAddress.getByAddress(ipAddress);
        }
    }



    private static byte[] convertIpAddress(int ip) {
        return new byte[]{
                (byte) (ip & 0xFF),
                (byte) ((ip >> 8) & 0xFF),
                (byte) ((ip >> 16) & 0xFF),
                (byte) ((ip >> 24) & 0xFF)};
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releaseExoplayer();
        releaseTorrentStream();
    }



    private void releaseTorrentStream(){

        if(torrentStreamServer.getCurrentTorrent() != null){
            try {
                torrentStreamServer.getCurrentTorrent().getVideoStream().close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            torrentStreamServer.stopTorrentStream();
        }

        if(torrentStreamServer != null){
            torrentStreamServer.stopStream();
            torrentStreamServer = null;
        }



    }

    private void releaseExoplayer(){
        if(player != null){
            player.release();
            player = null;
        }
    }

    @Override
    public void onServerReady(String url) {

        Log.d(TORRENT, "onServerReady: " + url);
        llLoading.setVisibility(GONE);
        //todo play video is disabled for now; mx player required
        //playVideo(Uri.parse(url));

        RetrofitSingleton.postReport(id, 0, 1);

        startMxPlayer(WatchActivity.this, url, btnBack.getRootView());

    }



    @Override
    public void onStreamPrepared(Torrent torrent) {
        Log.d(TORRENT, "OnStreamPrepared");

    }

    @Override
    public void onStreamStarted(Torrent torrent) {
        Log.d(TORRENT, "OnStreamStarted");
    }

    @Override
    public void onStreamError(Torrent torrent, Exception e) {
        Log.d(TORRENT, "OnStreamError" + e.getMessage());
        e.printStackTrace();
    }

    @Override
    public void onStreamReady(Torrent torrent) {
        Log.d(TORRENT, "stream ready");

    }

    @Override
    public void onStreamProgress(Torrent torrent, StreamStatus status) {
        //Log.d("FILE SIZE", torrent.getVideoFile().getAbsoluteFile().length() + " bytes");
        if(status.bufferProgress <= 100 && currentProgress < status.bufferProgress && currentProgress != status.bufferProgress) {
            tvProgress.setText("Baking... "+ status.bufferProgress + "%");

        }
    }

    @Override
    public void onStreamStopped() {
        Log.d(TORRENT, "onStreamStopped");
    }

}

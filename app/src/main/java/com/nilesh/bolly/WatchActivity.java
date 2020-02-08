package com.nilesh.bolly;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.transition.DrawableCrossFadeTransition;
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
import com.nilesh.bolly.models.Movie;
import com.nilesh.bolly.networking.RetrofitSingleton;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import retrofit2.Call;
import retrofit2.Response;

import static android.view.View.GONE;
import static com.google.android.exoplayer2.Player.DISCONTINUITY_REASON_SEEK;
import static com.nilesh.bolly.util.Common.fullScreen;

public class WatchActivity extends AppCompatActivity implements TorrentServerListener {

    PlayerView exoplayerView;
    ImageView ivLoading;
    TorrentStreamServer torrentStreamServer;
    TextView tvProgress;
    private String TORRENT = "torrent";
    SimpleExoPlayer player = null;
    private static int currentProgress = 0;
    ImageButton btnBack;


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
    protected void onResume() {
        super.onResume();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watch);



        tvProgress = findViewById(R.id.tv_progress);
        btnBack = findViewById(R.id.imgbtn_back);
        exoplayerView = findViewById(R.id.exoplayer);
        ivLoading = findViewById(R.id.iv_loading);
        Glide.with(WatchActivity.this)
                .load(R.drawable.popcorn_running)
                .transition(new DrawableTransitionOptions().crossFade())
                .into(ivLoading);
        String id = getIntent().getStringExtra("id");
        if(id != null){

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
                    .removeFilesAfterStop(true)
                    .build();

            torrentStreamServer = TorrentStreamServer.getInstance();
            torrentStreamServer.setTorrentOptions(torrentOptions);
            torrentStreamServer.setServerHost(ipAddress);
            torrentStreamServer.setServerPort( (int) (Math.random() * (65535 - 49152)) + 49152 );
            torrentStreamServer.startTorrentStream();
            torrentStreamServer.addListener(this);
            fetchTorrent(id);


        }



        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WatchActivity.super.onBackPressed();
            }
        });
    }



    private void fetchTorrent(String id){
        Log.d(TORRENT, "fetching for id "+ id);
        RetrofitSingleton.getBollyService().getMovieDetails(id).enqueue(new retrofit2.Callback<Movie>() {
            @Override
            public void onResponse(Call<Movie> call, Response<Movie> response) {

                if(response.body() == null){
                    Toast.makeText(WatchActivity.this, "No media file found", Toast.LENGTH_SHORT).show();
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
        ivLoading.setVisibility(GONE);
        //todo play video is disabled for now; mx player required
//        playVideo(Uri.parse(url));



        startMxPlayer(url);

    }

    private void startMxPlayer(String url) {
        final String MX_AD = "com.mxtech.videoplayer.ad";
        final String MX_PRO = "com.mxtech.videoplayer.pro";

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        intent.setData(Uri.parse(url));


        final PackageManager packageManager = WatchActivity.this.getPackageManager();
        Intent pro = packageManager.getLaunchIntentForPackage(MX_PRO);
        Intent ad = packageManager.getLaunchIntentForPackage(MX_AD);

        if(pro != null){
            intent.setPackage(MX_PRO);
            startActivity(intent);
        }else if(ad != null){
            intent.setPackage(MX_AD);
            startActivity(intent);
        }else{
            Snackbar.make(btnBack.getRootView(), "MX Player required", Snackbar.LENGTH_INDEFINITE)
                    .setAction("Install", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            try {
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + MX_AD)));
                            } catch (android.content.ActivityNotFoundException anfe) {
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + MX_AD)));
                            }
                        }
                    })
                    .setActionTextColor(getResources().getColor(R.color.colorAccentYellow)).show();
        }





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
        if(status.bufferProgress <= 100 && currentProgress < status.bufferProgress && currentProgress != status.bufferProgress) {
            tvProgress.setText("Baking... "+ status.bufferProgress + "%");

        }
    }

    @Override
    public void onStreamStopped() {
        Log.d(TORRENT, "onStreamStopped");
    }
}

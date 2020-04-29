package com.android.bolly.util;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.github.se_bastiaan.torrentstream.StreamStatus;
import com.github.se_bastiaan.torrentstream.Torrent;
import com.github.se_bastiaan.torrentstream.TorrentOptions;
import com.github.se_bastiaan.torrentstreamserver.TorrentServerListener;
import com.github.se_bastiaan.torrentstreamserver.TorrentStreamNotInitializedException;
import com.github.se_bastiaan.torrentstreamserver.TorrentStreamServer;
import com.android.bolly.phone.home.MainActivity;
import com.android.bolly.R;
import com.android.bolly.constants.TSSConfig;
import com.android.bolly.models.Movie;
import com.android.bolly.networking.RetrofitSingleton;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Response;

import static com.android.bolly.phone.watch.WatchActivity.getIpAddress;

public class DownloadingForegroundService extends Service implements TorrentServerListener {

    private boolean isRunning;

    public static final String CHANNEL_ID = "ForegroundServiceChannel";
    public static final String ACTION_DOWNLOAD_PROGRESS = "download-progress";
    public static final String ACTION_STOP_DOWNLOAD = "stop-download";
    public static final String ACTION_COMPLETE_DOWNLOAD = "finish-download";
    private static final String TORRENT = "DownloadForeground";
    private Messenger messenger;
    private Downloader downloader;
    Notification notification = null;
    NotificationCompat.Builder notificationBuilder = null;

    private String movieTitle = "<Please Wait>", progressPercent = "0";
    private long downloadedBytes = 0, totalBytes = 0;


    Intent stop = new Intent(ACTION_STOP_DOWNLOAD);
    private TorrentStreamServer torrentStreamServer;

    Timer timer;
    PowerManager.WakeLock wakeLock;


    @Override
    public void onCreate() {
        super.onCreate();


        isRunning = false;

        messenger = new Messenger();
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_STOP_DOWNLOAD);
        filter.addAction(ACTION_COMPLETE_DOWNLOAD);
        registerReceiver(messenger, filter);
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if(isRunning){

            Util.showToast(this, "Other download in progress...");

        }else {

            Util.showToast(this, "Starting download process...");
            isRunning = !isRunning;
            notification = getNotification("<Please wait>", 0);
            acquireWakeLock(); //get wakelock before staring service
            startForeground(1, notification);
            TSSConfig.setDownloading();
            int id = intent.getIntExtra("id", 0);
            downloader = new Downloader();


            if (id != 0) {
                setUpTorrentStream();
                fetchTorrent(id);

            } else {
                Util.showToast(getApplicationContext(), "Not yet available! Stay tuned");
                sendBroadcast(stop);
            }


            timer = new Timer();
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {

                    sendMessage();


                    int progress = Integer.parseInt(progressPercent);
                    if (progress == 100) return;
                    Notification notification = getNotification(movieTitle, progress);
                    NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    manager.notify(1, notification);

                }
            },150, 5000);

        }


        return START_REDELIVER_INTENT;

    }


    private void fetchTorrent(int id){
        Log.d(TORRENT, "fetching for id "+ id);
        RetrofitSingleton.getBollyService().getMovieDetails(id).enqueue(new retrofit2.Callback<Movie>() {
            @Override
            public void onResponse(Call<Movie> call, Response<Movie> response) {

                if(response.body() == null){
                    Util.showToast(getApplicationContext(), "Not yet available! Stay tuned");
                    sendBroadcast(stop);
                    return;
                }


                Log.d(TORRENT, response.body().getSelectedTorrent().getTitle());
                String magnet = response.body().getSelectedTorrent().getMagnet();
                movieTitle = response.body().getTitle();


                try {
                    torrentStreamServer.startStream(magnet);
                } catch (IOException | TorrentStreamNotInitializedException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Error!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Movie> call, Throwable t) {
                Log.d(TORRENT, t.getMessage());

            }
        });
    }


    private Notification getNotification(String title, int progress){
        int downloaded = (int) (downloadedBytes / 1024) / 1024;
        int total = (int) (totalBytes / 1024) / 1024;

        if(notification == null || notificationBuilder == null) {
            createNotificationChannel();
            Intent notificationIntent = new Intent(this, MainActivity.class);
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            Intent stopIntent = new Intent().setAction(ACTION_STOP_DOWNLOAD);
            PendingIntent pendingStop = PendingIntent.getBroadcast(getApplicationContext(), 1, stopIntent, PendingIntent.FLAG_CANCEL_CURRENT);


            notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setOngoing(true)
                    .setContentTitle(title)
                    .setContentText(progress + "% | (" + downloaded + "/" + total + " MB)")
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setContentIntent(pendingIntent)
                    .setProgress(100, 0, false)
                    .addAction(0, "STOP", pendingStop)
                    .setOnlyAlertOnce(true);
            notification = notificationBuilder.build();
            return notification;

        }
        return notificationBuilder
                .setContentTitle(title)
                .setProgress(100, progress, false)
                .setContentText(progress + "% | (" + downloaded + "/" + total + " MB)").build();

    }


    private void setUpTorrentStream() {

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
                .autoDownload(true)
                .build();

        torrentStreamServer = TorrentStreamServer.getInstance();
        torrentStreamServer.setTorrentOptions(torrentOptions);
        torrentStreamServer.setServerHost(ipAddress);
        torrentStreamServer.setServerPort( (int) (Math.random() * (65535 - 49152)) + 49152 );
        torrentStreamServer.startTorrentStream();
        torrentStreamServer.addListener(this);

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("DESTROY", "on destroy called");
        releaseTorrentStream();
        if(messenger != null)
            unregisterReceiver(messenger);

        if(timer != null)
            timer.cancel();

        releaseWakeLock(); //release wake lock after service
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Foreground Downloader",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }


    }


    //torrent server listener overrides
    //=========================================================================================
    //========================================================================================
    //==========================================================================================

    @Override
    public void onServerReady(String url) {

        Torrent torrent = torrentStreamServer.getCurrentTorrent();
        if(torrent != null) {
            String size = torrent.getVideoFile().length() + "";
            String path = torrent.getVideoFile().getAbsolutePath();

            if (torrent.getVideoFile().length() >= torrent.getSaveLocation().getUsableSpace()) {

                Util.showToast(getApplicationContext(), "Not enough space on disk. Aborting...");
                sendBroadcast(stop);
                return;
            }


            //start downloading
            downloader.execute(url, size, path);
        }

    }

    @Override
    public void onStreamPrepared(Torrent torrent) { }

    @Override
    public void onStreamStarted(Torrent torrent) { }

    @Override
    public void onStreamError(Torrent torrent, Exception e) { }

    @Override
    public void onStreamReady(Torrent torrent) { }

    @Override
    public void onStreamProgress(Torrent torrent, StreamStatus status) { }

    @Override
    public void onStreamStopped() { }

    //=============================================================
    //=========================================================






    private void releaseTorrentStream(){
        if(torrentStreamServer != null) {
            torrentStreamServer.removeListener(this);
            if (torrentStreamServer.getCurrentTorrent() != null) {
                try {
                    torrentStreamServer.getCurrentTorrent().getVideoStream().close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                torrentStreamServer.stopTorrentStream();
            }

            if (torrentStreamServer != null) {
                torrentStreamServer.stopStream();
                torrentStreamServer = null;
            }

        }



    }



    private void sendMessage() {
        Intent intent = new Intent().setAction(ACTION_DOWNLOAD_PROGRESS);
        intent.putExtra("title", movieTitle);
        intent.putExtra("progress", progressPercent);
        String downloadedVsTotal = downloadedBytes / (1024 * 1024) + "/" + totalBytes / (1024 * 1024) + " MB";
        intent.putExtra("downloadedVsTotal", downloadedVsTotal);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }



    private class Messenger extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            Util.showToast(context, "action " + intent.getAction());
            switch (intent.getAction()){

                case ACTION_STOP_DOWNLOAD:
                    LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(new Intent(ACTION_STOP_DOWNLOAD));
                    stopBackgroundDownload();
                    if(torrentStreamServer != null
                            && torrentStreamServer.getCurrentTorrent() != null
                            && torrentStreamServer.getCurrentTorrent().getVideoFile() != null) {
                        String filepath = torrentStreamServer.getCurrentTorrent().getVideoFile().getAbsolutePath();
                        String extension = filepath.substring(filepath.lastIndexOf("."));

                        File file = new File(getExternalFilesDir(Environment.DIRECTORY_MOVIES) + "/" + movieTitle + extension);

                        try {
                            file.getCanonicalFile().delete();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    releaseTorrentStream();
                    TSSConfig.resetDownloading();
                    stopForeground(true);
                    stopSelf();
                    break;



                case ACTION_COMPLETE_DOWNLOAD:
                    LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(new Intent(ACTION_COMPLETE_DOWNLOAD));
                    TSSConfig.resetDownloading();
                    releaseTorrentStream();
                    Util.showToast(context, "Download Complete: You might need to restart your app");
                    stopForeground(true);
                    stopSelf();
                    break;


            }
        }
    }




    //async task for downloading and cancel
    //===========================================================================
    //=============================================================================
    //=============================================================================


    private class Downloader extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... strings) {
            String url = strings[0];
            String size = strings[1];
            String path = strings[2];


            long lenghtOfFile = Long.parseLong(size);
            totalBytes = lenghtOfFile;
            Log.d("FILE LENGTH", lenghtOfFile + " bytes");
            byte[] data = new byte[1024];
            long total = 0;
            int count;
            InputStream input = null;
            OutputStream output = null;
            String filepath = path;
            long lastMessage = 0;
            try {
                String extension = filepath.substring(filepath.lastIndexOf("."));
                URL mUrl = new URL(url);
                URLConnection connection = mUrl.openConnection();
                connection.connect();

                input = new BufferedInputStream(mUrl.openStream(), 8192);


                output = new FileOutputStream(getExternalFilesDir(Environment.DIRECTORY_MOVIES) + "/"+ movieTitle + extension);
                int oldProgress = 0;
                while ((count = input.read(data)) != -1) {
                    total += count;

                    downloadedBytes = total;
                    int progress = (int) ((total * 100) / lenghtOfFile);

                    if (oldProgress != progress) {
                        Log.d("PROGRESS", progress + "");
                        oldProgress = progress;
                        progressPercent = progress + "";

                    }


                    // writing data to file
                    output.write(data, 0, count);
                }

                // flushing output
                output.flush();

                // closing streams
                output.close();
                input.close();


            } catch (IOException e) {
                e.printStackTrace();
                sendBroadcast(stop);
            }

            return null;

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            sendBroadcast(new Intent(ACTION_COMPLETE_DOWNLOAD));
        }


    }


    private void stopBackgroundDownload(){
        if(!downloader.isCancelled())
            downloader.cancel(true);
    }

    //==============================================================================
    //==============================================================================


    //wake lock
    private void acquireWakeLock(){
        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "BOLLY:WakeLock");
        wakeLock.acquire(3*60*60*1000);
    }


    private void releaseWakeLock(){
        if(wakeLock != null)
            if(wakeLock.isHeld())
                wakeLock.release();
    }
}

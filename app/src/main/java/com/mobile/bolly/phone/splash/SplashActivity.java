package com.mobile.bolly.phone.splash;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;
import com.mobile.bolly.R;
import com.mobile.bolly.phone.detail.MovieDetailActivity;
import com.mobile.bolly.phone.home.MainActivity;
import com.mobile.bolly.util.Util;

import static com.mobile.bolly.util.Util.fullScreen;

public class SplashActivity extends AppCompatActivity {

    private static final String TAG = SplashActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        fullScreen(this);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                checkDeepLinking();
            }
        }, 800);


    }



    private void checkDeepLinking(){
        FirebaseDynamicLinks
                .getInstance()
                .getDynamicLink(getIntent())
                .addOnSuccessListener(new OnSuccessListener<PendingDynamicLinkData>() {
                    @Override
                    public void onSuccess(PendingDynamicLinkData pendingDynamicLinkData) {

                        Uri deepLink = null;
                        if(pendingDynamicLinkData != null) {
                            deepLink = pendingDynamicLinkData.getLink();
                            Log.d(TAG, "Deep link" + deepLink.toString());
                            String [] linkparts = deepLink.toString().split("=");
                            if(linkparts.length >= 2) {
                                String movieId = linkparts[linkparts.length - 1];
                                try {
                                    int tmdbId = Integer.parseInt(movieId);
                                    Log.d(TAG, "Movie Id "+ movieId);
                                    gotoDetailsActivity(tmdbId);
                                }catch (Exception e){
                                    e.printStackTrace();
                                    Util.showToast(SplashActivity.this, "Couldn't resolve request");
                                    gotoMainActivity();
                                }

                            }else{
                                gotoMainActivity();
                            }

                        }else{
                            gotoMainActivity();
                        }


                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Util.showToast(SplashActivity.this, "Couldn't resolve request");
                        gotoMainActivity();

                    }
                });
    }



    private void gotoMainActivity(){
        startActivity(new Intent(SplashActivity.this, MainActivity.class));
        finish();
    }



    private void gotoDetailsActivity(int tmdbId){
        Intent gotoDetails = new Intent(SplashActivity.this, MovieDetailActivity.class);
        gotoDetails.putExtra("tmdb_id", tmdbId);
        startActivity(gotoDetails);
        finish();
    }
}

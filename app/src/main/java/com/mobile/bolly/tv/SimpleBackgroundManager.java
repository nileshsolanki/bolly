package com.mobile.bolly.tv;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.util.DisplayMetrics;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.leanback.app.BackgroundManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.mobile.bolly.R;

public class SimpleBackgroundManager {

    Activity activity;
    BackgroundManager backgroundManager = null;
    DisplayMetrics displayMetrics;

    public SimpleBackgroundManager(Activity activity) {
        this.activity = activity;
        this.backgroundManager = BackgroundManager.getInstance(activity);
        backgroundManager.attach(activity.getWindow());
        displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
    }


    public void clearBackground(){

        if(backgroundManager != null)
            backgroundManager.clearDrawable();

    }


    public void setBackground(String url){

        if(backgroundManager == null)
            return;

        Glide.with(activity)
                .load(url)
                .apply(new RequestOptions().override(displayMetrics.widthPixels, displayMetrics.heightPixels).centerCrop())
                .into(new CustomTarget<Drawable>() {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                        ColorDrawable color = new ColorDrawable(activity.getResources().getColor(R.color.darken_transparent));
                        LayerDrawable layers = new LayerDrawable(new Drawable[]{resource, color});
                        backgroundManager.setDrawable(layers);
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {

                    }
                });

    }
}

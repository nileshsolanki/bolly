package com.mobile.bolly.television.common;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
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

    ColorDrawable color;

    public SimpleBackgroundManager(Activity activity) {
        this.activity = activity;
        this.backgroundManager = BackgroundManager.getInstance(activity);
        backgroundManager.attach(activity.getWindow());
        displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        color = new ColorDrawable(activity.getResources().getColor(R.color.darken_transparent));

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
                        LayerDrawable layers = new LayerDrawable(new Drawable[]{resource, color});


                        final int width = layers.getIntrinsicWidth();
                        final int height = layers.getIntrinsicHeight();

                        final Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                        layers.setBounds(0, 0, width, height);
                        layers.draw(new Canvas(bitmap));

                        backgroundManager.setBitmap(bitmap);
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {

                    }
                });

    }
}

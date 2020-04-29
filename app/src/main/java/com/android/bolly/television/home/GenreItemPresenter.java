package com.android.bolly.television.home;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.leanback.widget.ArrayObjectAdapter;
import androidx.leanback.widget.Presenter;

import com.android.bolly.R;
import com.android.bolly.constants.Tmdb;

import java.util.Arrays;

public class GenreItemPresenter extends Presenter {
    private final int CARD_HEIGHT = 150, CARD_WIDTH = 200;

    Context context;

    public GenreItemPresenter(Context context) {
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent) {

        TextView tvYear = new TextView(parent.getContext());
        tvYear.setLayoutParams(new ViewGroup.LayoutParams(CARD_WIDTH, CARD_HEIGHT));
        tvYear.setTextColor(context.getResources().getColor(android.R.color.white));
        tvYear.setFocusable(true);
        tvYear.setFocusableInTouchMode(true);
        tvYear.setGravity(Gravity.CENTER);
        return new ViewHolder(tvYear);

    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Object item) {

        int genreIndex = Arrays.asList(Tmdb.genres).indexOf((String) item);

        TextView tv = ((TextView) viewHolder.view);
        tv.setText((String)item);

        Drawable image = context.getResources().getDrawable(Tmdb.genreDrawables[genreIndex]);
        Drawable color = context.getResources().getDrawable(R.drawable.genre_foreground_tv);
        tv.setBackground(new LayerDrawable(new Drawable[]{image, color}));

    }

    @Override
    public void onUnbindViewHolder(ViewHolder viewHolder) {

    }



    public static ArrayObjectAdapter getGenreRowAdapter(Context context){

        GenreItemPresenter genreItemPresenter = new GenreItemPresenter(context);
        ArrayObjectAdapter genreRowAdapter = new ArrayObjectAdapter(genreItemPresenter);

        String [] genres = Tmdb.genres;
        for(String genre: genres){
            genreRowAdapter.add(genre);
        }

        return genreRowAdapter;

    }


}

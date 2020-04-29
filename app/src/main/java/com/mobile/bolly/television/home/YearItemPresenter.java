package com.mobile.bolly.television.home;

import android.content.Context;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.leanback.widget.ArrayObjectAdapter;
import androidx.leanback.widget.Presenter;

import com.mobile.bolly.R;

class YearItemPresenter extends Presenter {

    Context context;

    private YearItemPresenter(Context context) {
        this.context = context;
    }

    public static final int YEAR_CARD_WIDTH = 200, YEAR_CARD_HEIGHT = 100;

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent) {
        TextView view = new TextView(parent.getContext());
        view.setLayoutParams(new ViewGroup.LayoutParams(YEAR_CARD_WIDTH, YEAR_CARD_HEIGHT));
        view.setFocusable(true);
        view.setFocusableInTouchMode(true);
        view.setBackgroundColor(context.getResources().getColor(R.color.default_background_card));
        view.setTextColor(context.getResources().getColor(android.R.color.white));
        view.setGravity(Gravity.CENTER);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Object item) {
        ((TextView)viewHolder.view).setText((String)item);
    }

    @Override
    public void onUnbindViewHolder(ViewHolder viewHolder) {

    }



    public static ArrayObjectAdapter getYearRowAdapter (Context context){

        YearItemPresenter yearItemPresenter = new YearItemPresenter(context);
        ArrayObjectAdapter yearRowAdapter = new ArrayObjectAdapter(yearItemPresenter);
        for(int i = 2020; i >= 2011; i--){
            yearRowAdapter.add( (i + "") );
        }

        return yearRowAdapter;

    }
}
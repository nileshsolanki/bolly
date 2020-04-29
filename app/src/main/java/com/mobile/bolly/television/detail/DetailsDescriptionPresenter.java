package com.mobile.bolly.television.detail;

import androidx.leanback.widget.AbstractDetailsDescriptionPresenter;

import com.mobile.bolly.models.Result;

public class DetailsDescriptionPresenter extends AbstractDetailsDescriptionPresenter {
    @Override
    protected void onBindDescription(ViewHolder vh, Object item) {

        Result movie = (Result)item;

        if(movie != null){
            vh.getTitle().setText(movie.getTitle());
            vh.getSubtitle().setText(movie.getVoteAverage() + "");
            vh.getBody().setText(movie.getOverview());
        }

    }
}

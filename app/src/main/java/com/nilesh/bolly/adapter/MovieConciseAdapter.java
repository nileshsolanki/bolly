package com.nilesh.bolly.adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.nilesh.bolly.MovieDetailActivity;
import com.nilesh.bolly.R;
import com.nilesh.bolly.models.Result;
import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.util.List;

import static com.nilesh.bolly.constants.Tmdb.POSTER_DOMAIN_185;

public class MovieConciseAdapter extends RecyclerView.Adapter<MovieConciseAdapter.ConciseViewHolder> {


    private List<Result> movieDataset;
    Activity activity;

    public MovieConciseAdapter(List<Result> movieDataset, Activity activity){
        this.movieDataset = movieDataset;
        this.activity = activity;
    }


    public class ConciseViewHolder extends RecyclerView.ViewHolder {

        public TextView mName, mRating;
        public ImageView mPoster;

        public ConciseViewHolder(MaterialCardView movieCardLayout) {
            super(movieCardLayout);
            mName = movieCardLayout.findViewById(R.id.tv_name);
            mRating = movieCardLayout.findViewById(R.id.tv_rating);
            mPoster = movieCardLayout.findViewById(R.id.iv_poster);


            movieCardLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    Intent movieDetail = new Intent(activity, MovieDetailActivity.class);
                    movieDetail.putExtra("result", (Serializable) movieDataset.get(position));

                    ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                            activity,
                            mPoster,
                            ViewCompat.getTransitionName(mPoster));

                    activity.startActivity(movieDetail, options.toBundle());
                }
            });
        }
    }



    @NonNull
    @Override
    public ConciseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        MaterialCardView movieCardLayout = (MaterialCardView) LayoutInflater.from(parent.getContext())
                                        .inflate(R.layout.movie_card_consice, parent, false);

        ConciseViewHolder movieViewHolder = new ConciseViewHolder(movieCardLayout);


        return  movieViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ConciseViewHolder holder, int position) {
        holder.mName.setText(movieDataset.get(position).getTitle());
        holder.mRating.setText(movieDataset.get(position).getVoteAverage() + "");
        Picasso.get().load(POSTER_DOMAIN_185 + movieDataset.get(position).getPosterPath()).into(holder.mPoster);


        ViewCompat.setTransitionName(holder.mPoster, movieDataset.get(position).getId() + "");

    }

    @Override
    public int getItemCount() {
        return movieDataset.size();
    }





}

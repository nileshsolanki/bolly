package com.android.bolly.phone.home;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.bolly.phone.grid.MovieGridActivity;
import com.android.bolly.R;

import static com.android.bolly.constants.Tmdb.genreDrawables;
import static com.android.bolly.constants.Tmdb.genreIds;
import static com.android.bolly.constants.Tmdb.genres;

public class MovieGenreAdapter extends RecyclerView.Adapter<MovieGenreAdapter.GenreViewHolder> {
    Context context;



    public MovieGenreAdapter(Context ctx) {
        context = ctx;
    }

    @NonNull
    @Override
    public GenreViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        FrameLayout genreLayout = (FrameLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.genre_item, parent, false);
        genreLayout.setClipToOutline(true);
        return new GenreViewHolder(genreLayout);
    }

    @Override
    public void onBindViewHolder(@NonNull GenreViewHolder holder, int position) {

        holder.ivGenre.setImageDrawable(context.getDrawable(genreDrawables[position]));
        holder.tvGenre.setText(genres[position]);

    }

    @Override
    public int getItemCount() {
        return genreIds.length;
    }

    public class GenreViewHolder extends RecyclerView.ViewHolder {

        ImageView ivGenre;
        TextView tvGenre;

        public GenreViewHolder(@NonNull FrameLayout itemView) {
            super(itemView);
            ivGenre = itemView.findViewById(R.id.iv_genre);
            tvGenre = itemView.findViewById(R.id.tv_genre);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, MovieGridActivity.class);
                    intent.putExtra("genre", genres[getAdapterPosition()]);
                    intent.putExtra("genre_id", genreIds[getAdapterPosition()]);
                    intent.putExtra("category", 3);
                    context.startActivity(intent);
                }
            });


        }
    }
}

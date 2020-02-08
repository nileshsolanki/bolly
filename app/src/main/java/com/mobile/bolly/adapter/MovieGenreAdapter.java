package com.mobile.bolly.adapter;

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

import com.mobile.bolly.R;
import com.mobile.bolly.MovieGridActivity;

public class MovieGenreAdapter extends RecyclerView.Adapter<MovieGenreAdapter.GenreViewHolder> {
    Context context;

    public  static final String [] genres = new String[]{
      "ACTION",
      "ADVENTURE",
      "ANIMATION",
        "COMEDY",
        "CRIME",
        "DOCUMENTARY",
        "DRAMA",
        "FAMILY",
        "FANTACY",
        "HISTORY",
        "HORROR",
        "MUSICAL",
        "MYSTERY",
        "ROMANCE",
        "SCI-FI",
        "THRILLER",
        "WAR"
    };

    public static final Integer [] genreIds = new Integer[] { 28, 12, 16, 35, 80, 99, 18, 10751, 14, 36, 27, 10402, 9648, 10749, 878, 53, 10752};
    int [] genreDrawables = new int[] {R.drawable.action, R.drawable.adventure, R.drawable.animation, R.drawable.comedy, R.drawable.crime,
                    R.drawable.documentary, R.drawable.drama, R.drawable.family, R.drawable.fantasy, R.drawable.history, R.drawable.horror, R.drawable.musical,
                    R.drawable.mystery, R.drawable.romance, R.drawable.science_fiction, R.drawable.thriller,R.drawable.war};

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

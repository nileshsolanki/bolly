package com.nilesh.bolly.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.nilesh.bolly.MovieGridActivity;
import com.nilesh.bolly.R;

public class MovieYearAdapter extends RecyclerView.Adapter<MovieYearAdapter.YearViewHolder> {

    Context context;

    public MovieYearAdapter(Context ctx){
        context = ctx;
    }

    @NonNull
    @Override
    public YearViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        MaterialButton v = (MaterialButton) LayoutInflater.from(parent.getContext()).inflate(R.layout.year_item, parent, false);


        return new YearViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull YearViewHolder holder, int position) {
        holder.btnYear.setText(2020 - position + "");

    }

    @Override
    public int getItemCount() {
        return 21;
    }



    public class YearViewHolder extends RecyclerView.ViewHolder{

        public MaterialButton btnYear;

        public YearViewHolder(@NonNull MaterialButton yearCard) {
            super(yearCard);

            btnYear = yearCard.findViewById(R.id.tv_year);

            yearCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, MovieGridActivity.class);
                    intent.putExtra("category", 4);
                    intent.putExtra("year", 2020 - getAdapterPosition());
                    context.startActivity(intent);
                }
            });

        }
    }
}

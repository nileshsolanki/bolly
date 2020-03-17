package com.mobile.bolly.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mobile.bolly.MovieDetailActivity;
import com.mobile.bolly.R;
import com.mobile.bolly.models.Suggestion;

import java.util.ArrayList;
import java.util.List;

public class SuggestionAdapter extends RecyclerView.Adapter<SuggestionAdapter.SuggestionViewHolder> implements Filterable {

    List<Suggestion> suggestions = new ArrayList<>();
    List<Suggestion> filteredSuggestions = new ArrayList<>();
    Context context;


    public SuggestionAdapter(Context context) {
        this.context = context;
    }

    public void setSuggestionData(List<Suggestion> suggestions){
        this.suggestions.clear();
        this.suggestions.addAll(suggestions);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SuggestionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LinearLayout suggestionLayout = (LinearLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.suggestion_item, parent, false);
        SuggestionViewHolder suggestionViewHolder = new SuggestionViewHolder(suggestionLayout);
        return suggestionViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull SuggestionViewHolder holder, int position) {
        holder.movieYear.setText(suggestions.get(position).getYear());
        holder.movieTitle.setText(suggestions.get(position).getTitle());
    }

    @Override
    public int getItemCount() {
        return suggestions.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String text = charSequence.toString().toLowerCase();
                if(text.isEmpty()){
                    filteredSuggestions = suggestions;
                }
                else{
                    filteredSuggestions.clear();
                    for(Suggestion suggestion: suggestions){

                        if(suggestion.getTitle().toLowerCase().contains(text)){
                            filteredSuggestions.add(suggestion);
                        }

                    }

                }

                FilterResults filterResults = new FilterResults();
                filterResults.count = filteredSuggestions.size();
                filterResults.values = filteredSuggestions;

                return filterResults;

            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                suggestions.clear();
                suggestions.addAll((ArrayList<Suggestion>) filterResults.values);
                notifyDataSetChanged();
            }
        };
    }

    public class SuggestionViewHolder extends RecyclerView.ViewHolder {

        public TextView movieTitle, movieYear;

        public SuggestionViewHolder(@NonNull LinearLayout suggestionLayout) {
            super(suggestionLayout);
            movieTitle = suggestionLayout.findViewById(R.id.tv_movie_title);
            movieYear = suggestionLayout.findViewById(R.id.tv_movie_year);


            suggestionLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    int position = getAdapterPosition();
                    Intent detail = new Intent(context, MovieDetailActivity.class);
                    detail.putExtra("tmdb_id", suggestions.get(position).getTmdbId());
                    context.startActivity(detail);

                }
            });


        }
    }



}



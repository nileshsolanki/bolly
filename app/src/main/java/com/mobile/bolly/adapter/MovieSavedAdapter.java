package com.mobile.bolly.adapter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.mobile.bolly.R;


import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import com.mobile.bolly.util.Util;
import static com.mobile.bolly.util.Util.startMxPlayer;



public class MovieSavedAdapter extends RecyclerView.Adapter<MovieSavedAdapter.MovieSavedHolder> {

    Context context;
    List<String> savedMovies;
    String title;

    public MovieSavedAdapter(Context context, String title) {
        this.context = context;
        searchSavedMovies();
        this.title = title;
    }

    public void searchSavedMovies(){
        File file = new File(context.getExternalFilesDir(Environment.DIRECTORY_MOVIES) + "");
        String []files = file.list(new FilenameFilter() {
            @Override
            public boolean accept(File file, String s) {
                if(title != null && s.contains(title))
                    return false;
                return true;
            }
        });
        savedMovies = Arrays.asList(files);

    }

    public void updateTitle(String title){
        this.title = title;
        searchSavedMovies();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MovieSavedHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LinearLayout view = (LinearLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.downloaded_movie_item, parent, false);
        return new MovieSavedHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieSavedHolder holder, int position) {


        holder.tvMovieTitle.setText(savedMovies.get(position).substring(0, savedMovies.get(position).lastIndexOf(".")));

        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                File file = new File(context.getExternalFilesDir(Environment.DIRECTORY_MOVIES) + "/" + savedMovies.get(position));
                try {
                    file.getCanonicalFile().delete();
                } catch (IOException e) {
                    e.printStackTrace();
                    Util.showToast(context, "Error deleting file");
                }

                searchSavedMovies();
                notifyDataSetChanged();

            }
        });


    }

    @Override
    public int getItemCount() {
        return savedMovies.size();
    }

    public class MovieSavedHolder extends RecyclerView.ViewHolder{

        TextView tvMovieTitle;
        ImageButton btnDelete;

        public MovieSavedHolder(@NonNull LinearLayout movieLayout) {
            super(movieLayout);

            tvMovieTitle = movieLayout.findViewById(R.id.tv_title);
            btnDelete = movieLayout.findViewById(R.id.btn_delete);


            tvMovieTitle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    File file = new File(context.getExternalFilesDir(Environment.DIRECTORY_MOVIES) + "/" + savedMovies.get(getAdapterPosition()));
                    Uri fileUri;
                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                        fileUri = FileProvider.getUriForFile(context.getApplicationContext(), context.getOpPackageName() + ".provider", file);
                    }else{
                        fileUri = Uri.fromFile(file);
                    }


                    startMxPlayer(context, fileUri.toString(), tvMovieTitle.getRootView().getRootView());

                }
            });

        }
    }


}
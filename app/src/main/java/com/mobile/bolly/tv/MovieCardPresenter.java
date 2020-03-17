package com.mobile.bolly.tv;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.leanback.widget.ArrayObjectAdapter;
import androidx.leanback.widget.ImageCardView;
import androidx.leanback.widget.Presenter;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.mobile.bolly.R;
import com.mobile.bolly.constants.Tmdb;
import com.mobile.bolly.models.MovieDetails;
import com.mobile.bolly.models.MovieTopRated;
import com.mobile.bolly.models.Result;
import com.mobile.bolly.networking.BollyService;
import com.mobile.bolly.networking.RetrofitSingleton;
import com.mobile.bolly.networking.TmdbService;
import com.mobile.bolly.util.SharedPrefHelper;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static androidx.leanback.widget.BaseCardView.CARD_REGION_VISIBLE_ACTIVATED;
import static com.mobile.bolly.constants.Tmdb.APIKEY;

public class MovieCardPresenter extends Presenter {

    Context mContext;
    private static int CARD_WIDTH = 220;
    private static int CARD_HEIGHT = 320;


    private class ViewHolder extends Presenter.ViewHolder{

        ImageCardView mImageCardview;
        Drawable defaultImage;
        Result movie;

        public ViewHolder(View view) {
            super(view);

            mImageCardview = (ImageCardView) view;
            defaultImage = mContext.getDrawable(R.drawable.noconnection);
        }


        public void setMovie(Result movie) {
            this.movie = movie;
        }

        public ImageCardView getmImageCardview() {
            return mImageCardview;
        }

        public Drawable getDefaultImage() {
            return defaultImage;
        }

        public Result getMovie() {
            return movie;
        }
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent) {
        mContext = parent.getContext();
        ImageCardView movieImageCardView = new ImageCardView(mContext);
        movieImageCardView.setFocusable(true);
        movieImageCardView.setFocusableInTouchMode(true);
        movieImageCardView.setInfoVisibility(CARD_REGION_VISIBLE_ACTIVATED);
        movieImageCardView.setBackgroundColor(mContext.getResources().getColor(R.color.default_background_card));
        return new ViewHolder(movieImageCardView);
    }

    @Override
    public void onBindViewHolder(Presenter.ViewHolder viewHolder, Object item) {
        Result movie = (Result) item;
        ((ViewHolder) viewHolder).setMovie(movie);
        ((ViewHolder) viewHolder).mImageCardview.setTitleText(movie.getTitle());
        ((ViewHolder) viewHolder).setMovie(movie);
        ((ViewHolder) viewHolder).mImageCardview.setContentText(movie.getVoteAverage() + "");
        ((ViewHolder) viewHolder).mImageCardview.setMainImageDimensions(CARD_WIDTH, CARD_HEIGHT);
        //((ViewHolder) viewHolder).mImageCardview.setMainImage(((ViewHolder) viewHolder).getDefaultImage());
        Glide.with(mContext)
                .load(Tmdb.POSTER_DOMAIN_500 + movie.getPosterPath())
                .into(new CustomTarget<Drawable>() {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                        ((ViewHolder) viewHolder).mImageCardview.setMainImage(resource);
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {

                    }
                })
                .onLoadFailed(((ViewHolder) viewHolder).getDefaultImage());

    }

    @Override
    public void onUnbindViewHolder(Presenter.ViewHolder viewHolder) {

    }



    public static ArrayObjectAdapter getRecentMoviesRowAdapter(Context context){
        MovieCardPresenter movieCardPresenter = new MovieCardPresenter();
        ArrayObjectAdapter recentMovieRowAdapter = new ArrayObjectAdapter(movieCardPresenter);

        Result emptyResult = new Result();
        emptyResult.setTitle("Loading...");
        recentMovieRowAdapter.add(emptyResult);
        fetchRecentlyAdded(new SharedPrefHelper(context.getApplicationContext()), recentMovieRowAdapter);


        return recentMovieRowAdapter;
    }



    public static ArrayObjectAdapter getTopRatedMoviesRowAdapter(Context context){
        MovieCardPresenter movieCardPresenter = new MovieCardPresenter();
        ArrayObjectAdapter topRatedMovieRowAdapter = new ArrayObjectAdapter(movieCardPresenter);

        fetchTopRated(context, new SharedPrefHelper(context.getApplicationContext()), topRatedMovieRowAdapter);

        return topRatedMovieRowAdapter;

    }


    private static void fetchRecentlyAdded(SharedPrefHelper sph, ArrayObjectAdapter recentMovies) {
        BollyService service = RetrofitSingleton.getBollyService();
        service.getRecents().enqueue(new Callback<List<MovieDetails>>() {
            @Override
            public void onResponse(Call<List<MovieDetails>> call, Response<List<MovieDetails>> response) {
                if(response.body() != null){
                    recentMovies.clear();
                    for(MovieDetails details: response.body()){
                        Result movie = new Result();
                        movie.setVoteAverage(details.getVoteAverage());
                        movie.setTitle(details.getTitle());
                        movie.setReleaseDate(details.getReleaseDate());
                        movie.setOverview(details.getOverview());
                        movie.setOriginalLanguage(details.getOriginalLanguage());
                        movie.setPosterPath((String) details.getPosterPath());
                        movie.setAdult(details.getAdult());
                        movie.setBackdropPath(details.getBackdropPath());
                        movie.setId(details.getId());
                        movie.setPopularity(details.getPopularity());
                        movie.setVideo(details.getVideo());
                        movie.setVoteCount(details.getVoteCount());

                        recentMovies.add(movie);
                    }
                    //sph.putSearchResults(SharedPrefHelper.TYPE_RECENTS, recentMovies);
                    //sph.putFetchTimeStamp(System.currentTimeMillis());
                }
            }

            @Override
            public void onFailure(Call<List<MovieDetails>> call, Throwable t) {

            }
        });
    }




    private static void fetchTopRated(Context context, SharedPrefHelper sph, ArrayObjectAdapter topRatedMoviesRowAdapter) {
        TmdbService service = RetrofitSingleton.getTmdbService();
        service.topRated(APIKEY, "hi", "IN", 1).enqueue(new Callback<MovieTopRated>() {
            @Override
            public void onResponse(Call<MovieTopRated> call, Response<MovieTopRated> response) {
                topRatedMoviesRowAdapter.clear();
                for(Result result : response.body().getResults()){
                    if(Integer.parseInt(result.getReleaseDate().split("-")[0])>= 2011) {

                        topRatedMoviesRowAdapter.add(result);
                        //sph.putSearchResults(SharedPrefHelper.TYPE_TOPRATED, ratedMovies);
                        //sph.putFetchTimeStamp(System.currentTimeMillis());
                    }

                }


                if(topRatedMoviesRowAdapter.size() == 0){
                    Result emptyResult = new Result();
                    emptyResult.setTitle("Loading...");
                    topRatedMoviesRowAdapter.add(emptyResult);
                }

            }

            @Override
            public void onFailure(Call<MovieTopRated> call, Throwable t) {

            }
        });



    }
}

package com.mobile.bolly.television.grid;

import android.content.Intent;
import android.os.Bundle;

import androidx.leanback.app.VerticalGridSupportFragment;
import androidx.leanback.widget.ArrayObjectAdapter;
import androidx.leanback.widget.OnItemViewClickedListener;
import androidx.leanback.widget.Presenter;
import androidx.leanback.widget.Row;
import androidx.leanback.widget.RowPresenter;
import androidx.leanback.widget.VerticalGridPresenter;

import com.mobile.bolly.models.MovieDetails;
import com.mobile.bolly.models.Result;
import com.mobile.bolly.networking.BollyService;
import com.mobile.bolly.networking.RetrofitSingleton;
import com.mobile.bolly.television.detail.DetailsActivity;
import com.mobile.bolly.television.home.MovieCardPresenter;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MovieGridFragment extends VerticalGridSupportFragment {

    public static final int CATEGORY_RECENT = 1, CATEGORY_TOPRATED = 2, CATEGORY_GENRE = 3, CATEGORY_YEAR = 4;
    int category = 0;
    ArrayObjectAdapter movieGridAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        category = getActivity().getIntent().getIntExtra("category", 0);
        createGrid();

        fetchMoviesByCategory(category);
        setOnItemViewClickedListener(new ItemViewClickedListener());

    }



    private void createGrid() {

        VerticalGridPresenter verticalMoviePresenter = new VerticalGridPresenter();
        verticalMoviePresenter.setNumberOfColumns(5);
        setGridPresenter(verticalMoviePresenter);


        movieGridAdapter = new ArrayObjectAdapter(new MovieCardPresenter());
        Result emptyResult = new Result();
        emptyResult.setTitle("Loading...");
        movieGridAdapter.add(emptyResult);
        setAdapter(movieGridAdapter);

    }


    private void fetchMoviesByCategory(int category) {

        switch (category){

            case CATEGORY_RECENT:
                setTitle("Recent Additions");

                BollyService service = RetrofitSingleton.getBollyService();
                service.getRecents().enqueue(new Callback<List<MovieDetails>>() {
                    @Override
                    public void onResponse(Call<List<MovieDetails>> call, Response<List<MovieDetails>> response) {
                        if(response.body() != null){
                            movieGridAdapter.clear();

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

                                movieGridAdapter.add(movie);

                            }

                        }
                    }

                    @Override
                    public void onFailure(Call<List<MovieDetails>> call, Throwable t) { }
                });
                break;

            case CATEGORY_TOPRATED:
                setTitle("Top Rated");
                RetrofitSingleton.getBollyService().getTopRated().enqueue(new Callback<List<Result>>() {
                    @Override
                    public void onResponse(Call<List<Result>> call, Response<List<Result>> response) {
                        if(response.body() != null){
                            movieGridAdapter.clear();
                            for(Result result: response.body())
                                movieGridAdapter.add(result);
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Result>> call, Throwable t) {

                    }
                });

                break;

            case CATEGORY_GENRE:
                Intent intent = getActivity().getIntent();
                int genre = 0;
                if(intent != null) {
                    setTitle(intent.getStringExtra("genre"));
                    genre = intent.getIntExtra("genre_id", 0);
                }

                RetrofitSingleton.getBollyService().getForGenre(genre).enqueue(new Callback<List<Result>>() {
                    @Override
                    public void onResponse(Call<List<Result>> call, Response<List<Result>> response) {
                        if(response.body() != null){
                            movieGridAdapter.clear();
                            for(Result result: response.body())
                                movieGridAdapter.add(result);
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Result>> call, Throwable t) {

                    }
                });

                break;

            case CATEGORY_YEAR:
                String year = getActivity().getIntent().getStringExtra("year");
                setTitle(year);

                RetrofitSingleton.getBollyService().getForYear(Integer.parseInt(year)).enqueue(new Callback<List<Result>>() {
                    @Override
                    public void onResponse(Call<List<Result>> call, Response<List<Result>> response) {
                        if(response.body() != null){
                            movieGridAdapter.clear();
                            for(Result result: response.body())
                                movieGridAdapter.add(result);
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Result>> call, Throwable t) {

                    }
                });

                break;

            default:
                setTitle("Movies");
                break;


        }

    }


    private class ItemViewClickedListener implements OnItemViewClickedListener{

        @Override
        public void onItemClicked(Presenter.ViewHolder itemViewHolder, Object item, RowPresenter.ViewHolder rowViewHolder, Row row) {

            Intent details = new Intent(getActivity(), DetailsActivity.class);
            details.putExtra("result", (Result)item);
            startActivity(details);

        }
    }
}

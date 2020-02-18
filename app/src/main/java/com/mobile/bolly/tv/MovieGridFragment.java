package com.mobile.bolly.tv;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.leanback.app.VerticalGridSupportFragment;
import androidx.leanback.widget.ArrayObjectAdapter;
import androidx.leanback.widget.OnItemViewClickedListener;
import androidx.leanback.widget.Presenter;
import androidx.leanback.widget.Row;
import androidx.leanback.widget.RowPresenter;
import androidx.leanback.widget.VerticalGridPresenter;

import com.mobile.bolly.models.MovieDetails;
import com.mobile.bolly.models.MovieDiscover;
import com.mobile.bolly.models.MovieTopRated;
import com.mobile.bolly.models.Result;
import com.mobile.bolly.networking.BollyService;
import com.mobile.bolly.networking.RetrofitSingleton;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.mobile.bolly.constants.Tmdb.APIKEY;

public class MovieGridFragment extends VerticalGridSupportFragment {

    public static final int categoryRecent = 1, categoryToprated = 2, categoryGenre = 3, categoryYear = 4;
    int category = 0;
    int page = 1;
    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
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

            case categoryRecent:
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

            case categoryToprated:
                setTitle("Top Rated");
                RetrofitSingleton.getTmdbService().topRated(APIKEY, "hi", "IN", page)
                        .enqueue(new Callback<MovieTopRated>() {
                            @Override
                            public void onResponse(Call<MovieTopRated> call, Response<MovieTopRated> response) {
                                if(response.body().getResults() != null){
                                    if(page == 1) movieGridAdapter.clear();
                                    for(Result result: response.body().getResults()){
                                        if(Integer.parseInt(result.getReleaseDate().split("-")[0] ) >= 2000)
                                            movieGridAdapter.add(result);
                                    }

                                    Log.d("PageTotal", response.body().getTotalPages() + "");
                                    Log.d("Page", response.body().getPage() + "");



                                    if(response.body().getPage() < response.body().getTotalPages()){
                                        page += 1;
                                        fetchMoviesByCategory(category);
                                    }
                                }
                            }

                            @Override
                            public void onFailure(Call<MovieTopRated> call, Throwable t) {

                            }
                        });
                break;

            case categoryGenre:
                setTitle(getActivity().getIntent().getStringExtra("genre"));
                int genre = getActivity().getIntent().getIntExtra("genre_id", 0);

                RetrofitSingleton.getTmdbService().genre(APIKEY, "hi", "IN", page, true, genre + "", "release_date.desc", "IN", dateFormat.format(new Date()), "2001-01-01")
                        .enqueue(new Callback<MovieDiscover>() {
                            @Override
                            public void onResponse(Call<MovieDiscover> call, Response<MovieDiscover> response) {
                                if(response.body().getResults() != null){
                                    if(page == 1) movieGridAdapter.clear();

                                    for(Result movie: response.body().getResults()){
                                        movieGridAdapter.add(movie);
                                    }


                                    Log.d("PageTotal", response.body().getTotalPages() + "");
                                    Log.d("Page", response.body().getPage() + "");



                                    if(response.body().getPage() < response.body().getTotalPages()){
                                        page += 1;
                                        fetchMoviesByCategory(category);
                                    }
                                }
                            }

                            @Override
                            public void onFailure(Call<MovieDiscover> call, Throwable t) {

                            }
                        });

                break;

            case categoryYear:
                String year = getActivity().getIntent().getStringExtra("year");
                setTitle(year);

                RetrofitSingleton.getTmdbService()
                        .year(APIKEY, "hi", "IN", page, true, Integer.parseInt(year), "release_date.desc", "IN", dateFormat.format(new Date()))
                        .enqueue(new Callback<MovieDiscover>() {
                            @Override
                            public void onResponse(Call<MovieDiscover> call, Response<MovieDiscover> response) {
                                if(response.body().getResults() != null){
                                    if(page == 1) movieGridAdapter.clear();

                                    for(Result movie: response.body().getResults()){
                                        movieGridAdapter.add(movie);
                                    }


                                    Log.d("PageTotal", response.body().getTotalPages() + "");
                                    Log.d("Page", response.body().getPage() + "");



                                    if(response.body().getPage() < response.body().getTotalPages()){
                                        page += 1;
                                        fetchMoviesByCategory(category);
                                    }
                                }
                            }

                            @Override
                            public void onFailure(Call<MovieDiscover> call, Throwable t) {

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

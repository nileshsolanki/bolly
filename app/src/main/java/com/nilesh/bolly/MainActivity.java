package com.nilesh.bolly;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import com.nilesh.bolly.adapter.MovieConciseAdapter;
import com.nilesh.bolly.adapter.MovieYearAdapter;
import com.nilesh.bolly.fragments.HomeDefaultFragment;
import com.nilesh.bolly.fragments.HomeSearchFragment;
import com.nilesh.bolly.models.MovieDetails;
import com.nilesh.bolly.models.MovieNowPlaying;
import com.nilesh.bolly.models.MovieSearch;
import com.nilesh.bolly.models.MovieTopRated;
import com.nilesh.bolly.models.Result;
import com.nilesh.bolly.networking.RetrofitSingleton;
import com.nilesh.bolly.networking.TmdbService;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.nilesh.bolly.constants.Tmdb.APIKEY;
import static com.nilesh.bolly.util.Common.fullScreen;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private String RESPONSE = "response";

    FrameLayout flSwitch;
    SearchView searchView;
    private static final int HOME_DEFAULT_FRAGMENT = 0, HOME_SEARCH_FRAGMENT = 1;
    private static int currentFragment = HOME_DEFAULT_FRAGMENT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        fullScreen(this);

        HomeDefaultFragment defaultFragment = new HomeDefaultFragment();

        getSupportFragmentManager().beginTransaction().replace(R.id.fl_switch, defaultFragment, "SEARCH").commit();

        searchView = findViewById(R.id.search_view);
        flSwitch = findViewById(R.id.fl_switch);


        searchView.setOnClickListener(this);
        searchView.findViewById(R.id.search_close_btn).setOnClickListener(this);
        searchView.findViewById(R.id.search_src_text).setOnClickListener(this);


    }


    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }



    @Override
    public void onClick(View view) {


        switch (view.getId()){

            case R.id.search_view:
                if(currentFragment != HOME_SEARCH_FRAGMENT){
                    HomeSearchFragment searchFragment = new HomeSearchFragment();
                    getSupportFragmentManager().beginTransaction().replace(R.id.fl_switch, searchFragment, "SEARCH").commit();
                    currentFragment = HOME_SEARCH_FRAGMENT;

                    findViewById(R.id.search_button).performClick();
                }
                break;

            case R.id.search_src_text:
                if(currentFragment != HOME_SEARCH_FRAGMENT){
                    HomeSearchFragment searchFragment = new HomeSearchFragment();
                    getSupportFragmentManager().beginTransaction().replace(R.id.fl_switch, searchFragment, "SEARCH").commit();
                    currentFragment = HOME_SEARCH_FRAGMENT;
                }
                break;


            case R.id.search_close_btn:
                if(currentFragment != HOME_DEFAULT_FRAGMENT){
                    HomeDefaultFragment defaultFragment = new HomeDefaultFragment();
                    getSupportFragmentManager().beginTransaction().replace(R.id.fl_switch, defaultFragment, "DEFAULT").commit();
                    currentFragment = HOME_DEFAULT_FRAGMENT;

                    hideKeyboard(this);
                }

                ((EditText) findViewById(R.id.search_src_text)).setText("");
                break;

        }

    }
}

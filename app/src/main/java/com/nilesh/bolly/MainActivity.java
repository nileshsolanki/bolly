package com.nilesh.bolly;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.nilesh.bolly.adapter.MovieConciseAdapter;
import com.nilesh.bolly.adapter.MovieYearAdapter;
import com.nilesh.bolly.fragments.BookmarkFragment;
import com.nilesh.bolly.fragments.HomeDefaultFragment;
import com.nilesh.bolly.fragments.HomeFragment;
import com.nilesh.bolly.fragments.HomeSearchFragment;
import com.nilesh.bolly.fragments.InfoFragment;
import com.nilesh.bolly.models.MovieDetails;
import com.nilesh.bolly.models.MovieNowPlaying;
import com.nilesh.bolly.models.MovieSearch;
import com.nilesh.bolly.models.MovieTopRated;
import com.nilesh.bolly.models.Result;
import com.nilesh.bolly.networking.RetrofitSingleton;
import com.nilesh.bolly.networking.TmdbService;
import com.nilesh.bolly.util.ConnectivityChangeReceiver;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.nilesh.bolly.constants.Tmdb.APIKEY;
import static com.nilesh.bolly.util.Common.fullScreen;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    FrameLayout flSwitch;
    BottomNavigationView bottomNav;
    private static int selectedItem = 0;
    ConnectivityChangeReceiver connectivityReceiver;


    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(connectivityReceiver);
    }


    @Override
    protected void onStart() {
        super.onStart();
        connectivityReceiver = new ConnectivityChangeReceiver(getSupportFragmentManager());
        registerReceiver(connectivityReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        flSwitch = findViewById(R.id.fl_switch_main);
        bottomNav = findViewById(R.id.bottom_nav);

        setNavigationListener(bottomNav);
        bottomNav.setSelectedItemId(R.id.home);


    }

    private void setNavigationListener(BottomNavigationView bottomNav) {

        bottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                if(selectedItem == menuItem.getItemId())
                    return true;
                selectedItem  = menuItem.getItemId();

                switch (menuItem.getItemId()){

                    case R.id.home:
                        getSupportFragmentManager().beginTransaction()
                                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                                .replace(R.id.fl_switch_main, HomeFragment.getHomeFragment()).commit();

                        break;

                    case R.id.bookmarks:
                        getSupportFragmentManager().beginTransaction()
                                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                                .replace(R.id.fl_switch_main, BookmarkFragment.getBookmarkFragment()).commit();
                        break;


                    case R.id.info:
                        getSupportFragmentManager().beginTransaction()
                                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                                .replace(R.id.fl_switch_main, InfoFragment.getInfoFragment()).commit();
                        break;

                    default:
                        getSupportFragmentManager().beginTransaction()
                                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                                .replace(R.id.fl_switch_main, HomeFragment.getHomeFragment()).commit();
                        break;

                }

                return true;
            }
        });
    }





    @Override
    public void onClick(View view) {


    }
}

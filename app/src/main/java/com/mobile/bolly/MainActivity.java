package com.mobile.bolly;

import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.mobile.bolly.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.mobile.bolly.fragments.BookmarkFragment;
import com.mobile.bolly.fragments.HomeFragment;
import com.mobile.bolly.fragments.InfoFragment;
import com.mobile.bolly.fragments.UpdateFragment;
import com.mobile.bolly.models.UpdateLog;
import com.mobile.bolly.networking.RetrofitSingleton;
import com.mobile.bolly.util.ConnectivityChangeReceiver;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.mobile.bolly.util.Common.fullScreen;


public class MainActivity extends AppCompatActivity {
    FrameLayout flSwitch;
    BottomNavigationView bottomNav;
    private int selectedItem = 0;
    ConnectivityChangeReceiver connectivityReceiver;

    @Override
    protected void onPause() {
        super.onPause();
        if(connectivityReceiver != null)
            unregisterReceiver(connectivityReceiver);
    }


    @Override
    protected void onStart() {
        super.onStart();
        fullScreen(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
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

        checkUpdate();

    }

    private void checkUpdate() {

        RetrofitSingleton.getUpdateService().checkUpdates().enqueue(new Callback<UpdateLog>() {
            @Override
            public void onResponse(Call<UpdateLog> call, Response<UpdateLog> response) {
                if(response.body().getLatestVersion() != null){

                    if(BuildConfig.VERSION_CODE < response.body().getLatestVersionCode() ){

                        Bundle args = new Bundle();
                        args.putString("downloadUrl", response.body().getUrl());
                        args.putString("version", response.body().getLatestVersion());

                        UpdateFragment updateFragment = new UpdateFragment();
                        updateFragment.setArguments(args);

                        getSupportFragmentManager().beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                                .add(android.R.id.content, updateFragment, "update")
                                .addToBackStack("stack")
                                .commit();

                    }


                }
            }

            @Override
            public void onFailure(Call<UpdateLog> call, Throwable t) {

            }
        });


    }

    private void setNavigationListener(BottomNavigationView bottomNav) {

        bottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                if(selectedItem == menuItem.getItemId())
                    return true;
                selectedItem  = menuItem.getItemId();

                switch (menuItem.getItemId()){

                    case R.id.bookmarks:
                        getSupportFragmentManager().beginTransaction()
                                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                                .replace(R.id.fl_switch_main, new BookmarkFragment()).commit();
                        break;


                    case R.id.info:
                        getSupportFragmentManager().beginTransaction()
                                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                                .replace(R.id.fl_switch_main, new InfoFragment()).commit();
                        break;

                    default: //or r.id.home
                        getSupportFragmentManager().beginTransaction()
                                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                                .replace(R.id.fl_switch_main, new HomeFragment()).commit();
                }

                return true;
            }
        });
    }

}

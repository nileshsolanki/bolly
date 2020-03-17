package com.mobile.bolly;

import android.Manifest;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.messaging.FirebaseMessaging;
import com.mobile.bolly.fragments.BookmarkFragment;
import com.mobile.bolly.fragments.HomeFragment;
import com.mobile.bolly.fragments.InfoFragment;
import com.mobile.bolly.util.ConnectivityChangeReceiver;

import static com.mobile.bolly.util.Common.checkUpdate;
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

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
        }


        connectivityReceiver = new ConnectivityChangeReceiver(getSupportFragmentManager());
        registerReceiver(connectivityReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseMessaging.getInstance().subscribeToTopic("recent");

        flSwitch = findViewById(R.id.fl_switch_main);
        bottomNav = findViewById(R.id.bottom_nav);

        setNavigationListener(bottomNav);
        bottomNav.setSelectedItemId(R.id.home);

        checkUpdate(getSupportFragmentManager());

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

package com.nilesh.bolly.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;

import com.nilesh.bolly.R;

import static com.nilesh.bolly.util.Common.fullScreen;
import static com.nilesh.bolly.util.Common.hideKeyboard;


public class HomeFragment extends Fragment implements View.OnClickListener {
    View view;

    private String RESPONSE = "response";

    private  static HomeFragment homeFragment = null;

    FrameLayout flSwitch;
    SearchView searchView;
    private static final int HOME_DEFAULT_FRAGMENT = 0, HOME_SEARCH_FRAGMENT = 1;
    private static int currentFragment = HOME_DEFAULT_FRAGMENT;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_home, container, false);

        fullScreen(getActivity());

        HomeDefaultFragment defaultFragment = new HomeDefaultFragment();

        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fl_switch, defaultFragment, "SEARCH").commit();

        searchView = view.findViewById(R.id.search_view);
        flSwitch = view.findViewById(R.id.fl_switch);


        searchView.setOnClickListener(this);
        searchView.findViewById(R.id.search_close_btn).setOnClickListener(this);
        searchView.findViewById(R.id.search_src_text).setOnClickListener(this);




        return view;


    }



    public static HomeFragment getHomeFragment(){
        if(homeFragment == null)
            homeFragment = new HomeFragment();
        return homeFragment;
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){

            case R.id.search_view:
                if(currentFragment != HOME_SEARCH_FRAGMENT){
                    HomeSearchFragment searchFragment = new HomeSearchFragment();
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fl_switch, searchFragment, "SEARCH").commit();
                    currentFragment = HOME_SEARCH_FRAGMENT;

                    view.findViewById(R.id.search_button).performClick();
                }
                break;

            case R.id.search_src_text:
                if(currentFragment != HOME_SEARCH_FRAGMENT){
                    HomeSearchFragment searchFragment = new HomeSearchFragment();
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fl_switch, searchFragment, "SEARCH").commit();
                    currentFragment = HOME_SEARCH_FRAGMENT;
                }
                break;


            case R.id.search_close_btn:
                if(currentFragment != HOME_DEFAULT_FRAGMENT){
                    HomeDefaultFragment defaultFragment = new HomeDefaultFragment();
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fl_switch, defaultFragment, "DEFAULT").commit();
                    currentFragment = HOME_DEFAULT_FRAGMENT;

                    hideKeyboard(getActivity());
                }
                break;

        }

    }
}

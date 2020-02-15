package com.mobile.bolly.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.mobile.bolly.R;
import com.mobile.bolly.adapter.SuggestionAdapter;
import com.mobile.bolly.models.Suggestion;
import com.mobile.bolly.networking.RetrofitSingleton;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.mobile.bolly.util.Common.fullScreen;
import static com.mobile.bolly.util.Common.hideKeyboard;


public class HomeFragment extends Fragment implements View.OnClickListener {
    View view;

    private String RESPONSE = "response";
    long textChangeStamp = 0;


    FrameLayout flSwitch;
    SearchView searchView;
    String oldText = null;
    private static final int HOME_DEFAULT_FRAGMENT = 0, HOME_SEARCH_FRAGMENT = 1;
    private static int currentFragment = HOME_DEFAULT_FRAGMENT;
    SuggestionAdapter adapter;


    @Override
    public void onResume() {
        super.onResume();
        if(searchView != null)
            searchView.clearFocus();
    }

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

    private void setQueryListener(SearchView searchView) {

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {


                HomeSearchFragment homeSearchFragment = (HomeSearchFragment) getActivity().getSupportFragmentManager().findFragmentByTag("SEARCH");
                if(homeSearchFragment == null){
                    HomeSearchFragment searchFragment = new HomeSearchFragment();
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fl_switch, searchFragment, "SEARCH").commit();
                    currentFragment = HOME_SEARCH_FRAGMENT;
                }
                else if(homeSearchFragment != null) {
                    adapter = homeSearchFragment.adapter;
                    if (oldText != null && adapter.getItemCount() > 0 && newText.contains(oldText)) {
                        textChangeStamp = System.currentTimeMillis();
                        adapter.getFilter().filter(newText);

                    } else if (newText.length() >= 2 && System.currentTimeMillis() - textChangeStamp >= 800) {
                        textChangeStamp = System.currentTimeMillis();
                        fetchSuggestions(newText);
                    }else{
                        fetchSuggestions(newText);
                        textChangeStamp = System.currentTimeMillis();
                    }
                    oldText = newText;

                }
                return false;
            }
        });

    }


    private void fetchSuggestions(String query){
        RetrofitSingleton.getBollyService().getSuggestions(query).enqueue(new Callback<List<Suggestion>>() {
            @Override
            public void onResponse(Call<List<Suggestion>> call, Response<List<Suggestion>> response) {
                if(response.body() != null){
                    HomeSearchFragment homeSearchFragment = (HomeSearchFragment) getFragmentManager().findFragmentByTag("SEARCH");
                    if(homeSearchFragment != null){
                        homeSearchFragment.setData(response.body());
                    }
                }

            }

            @Override
            public void onFailure(Call<List<Suggestion>> call, Throwable t) {

            }
        });
    }



    @Override
    public void onClick(View view) {

        switch (view.getId()){

            case R.id.search_view:
                if(currentFragment != HOME_SEARCH_FRAGMENT){
                    HomeSearchFragment searchFragment = new HomeSearchFragment();
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fl_switch, searchFragment, "SEARCH").commit();
                    currentFragment = HOME_SEARCH_FRAGMENT;
                    setQueryListener(searchView);
                    view.findViewById(R.id.search_button).performClick();
                }
                break;


            case R.id.search_src_text:
                if(currentFragment != HOME_SEARCH_FRAGMENT){
                    HomeSearchFragment searchFragment = new HomeSearchFragment();
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fl_switch, searchFragment, "SEARCH").commit();
                    currentFragment = HOME_SEARCH_FRAGMENT;
                    setQueryListener(searchView);

                }
                break;


            case R.id.search_close_btn:
                if(currentFragment != HOME_DEFAULT_FRAGMENT){
                    HomeDefaultFragment defaultFragment = new HomeDefaultFragment();
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fl_switch, defaultFragment, "DEFAULT").commit();
                    currentFragment = HOME_DEFAULT_FRAGMENT;
                    searchView.setQuery("", false);
                    searchView.clearFocus();
                    hideKeyboard(getActivity());
                }
                break;

        }

    }
}

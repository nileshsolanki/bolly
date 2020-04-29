package com.mobile.bolly.phone.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mobile.bolly.R;
import com.mobile.bolly.phone.home.SuggestionAdapter;
import com.mobile.bolly.models.Suggestion;

import java.util.List;

public class HomeSearchFragment extends Fragment {

    View view;
    RecyclerView rvSuggestions;
    SuggestionAdapter adapter;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home_search, container, false);


        adapter = new SuggestionAdapter(getContext());
        rvSuggestions = view.findViewById(R.id.rv_suggestions);
        rvSuggestions.setLayoutManager(new LinearLayoutManager(getContext()));
        rvSuggestions.setAdapter(adapter);

        return view;
    }

    public void setData(List<Suggestion> data){
        adapter.setSuggestionData(data);
    }
}

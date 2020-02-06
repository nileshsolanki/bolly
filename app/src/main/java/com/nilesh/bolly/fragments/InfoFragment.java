package com.nilesh.bolly.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.nilesh.bolly.R;

public class InfoFragment extends Fragment {


    View v;
    private static InfoFragment infoFragment;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_info, container, false);

        return v;
    }

    public static InfoFragment getInfoFragment(){
        if(infoFragment == null)
            infoFragment = new InfoFragment();

        return infoFragment;
    }

}

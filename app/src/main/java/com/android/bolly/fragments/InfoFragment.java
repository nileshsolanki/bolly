package com.android.bolly.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.oss.licenses.OssLicensesMenuActivity;
import com.android.bolly.BuildConfig;
import com.android.bolly.R;

public class InfoFragment extends Fragment implements View.OnClickListener {


    View v;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_info, container, false);

        TextView tvVersionName  = v.findViewById(R.id.tv_version_name);
        tvVersionName.setText("v" + BuildConfig.VERSION_NAME);

        TextView tvShare = v.findViewById(R.id.tv_share);
        TextView tvOpenSourceLicenses = v.findViewById(R.id.tv_opensource_licenses);
        tvShare.setOnClickListener(this);
        tvOpenSourceLicenses.setOnClickListener(this);

        return v;
    }



    @Override
    public void onClick(View view) {

        switch (view.getId()){

            case R.id.tv_share:
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Share bolly");
                String shareMessage= "\nHey I am enjoying BOLLY! You can watch bollywood movies for FREE!!!\nYou should try it too\nYou can find it here\n\n";
                shareMessage = shareMessage + "https://bolly.page.link/home"  + "\n\n";
                shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
                startActivity(Intent.createChooser(shareIntent, "choose one"));
                break;

            case R.id.tv_opensource_licenses:
                startActivity(new Intent(getActivity(), OssLicensesMenuActivity.class));
                break;
        }


    }
}

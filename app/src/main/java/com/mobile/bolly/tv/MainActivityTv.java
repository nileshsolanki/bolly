package com.mobile.bolly.tv;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import android.app.Activity;
import android.os.Bundle;

import com.mobile.bolly.R;

import static com.mobile.bolly.util.Common.checkUpdate;
import static com.mobile.bolly.util.Common.checkUpdateTv;

public class MainActivityTv extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_tv);


        checkUpdateTv(MainActivityTv.this);
    }
}

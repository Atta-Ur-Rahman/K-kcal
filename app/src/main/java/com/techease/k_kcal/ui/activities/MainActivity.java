package com.techease.k_kcal.ui.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.techease.k_kcal.R;
import com.techease.k_kcal.ui.fragment.WelcomeFragment;
import com.techease.k_kcal.utilities.GeneralUtills;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        GeneralUtills.withOutBackStackConnectFragment(this,new WelcomeFragment());
    }

}

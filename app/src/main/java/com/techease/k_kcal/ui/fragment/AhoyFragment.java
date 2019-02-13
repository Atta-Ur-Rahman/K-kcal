package com.techease.k_kcal.ui.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.techease.k_kcal.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class AhoyFragment extends Fragment {


    public AhoyFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_ahoy, container, false);
    }

}

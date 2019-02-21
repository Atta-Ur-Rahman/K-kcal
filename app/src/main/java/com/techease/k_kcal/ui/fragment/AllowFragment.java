package com.techease.k_kcal.ui.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.techease.k_kcal.R;
import com.techease.k_kcal.utilities.GeneralUtills;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class AllowFragment extends Fragment {
    View  view;
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.btn_allow_access)
    Button btnAllowAccess;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_yaaay, container, false);
        initUI();
        return view;
    }

    private void initUI(){
        ButterKnife.bind(this,view);

        btnAllowAccess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            GeneralUtills.withOutBackStackConnectFragment(getActivity(),new AllitemFragment());
            }
        });

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GeneralUtills.withOutBackStackConnectFragment(getActivity(),new AboutUserFragment());
            }
        });


    }

}

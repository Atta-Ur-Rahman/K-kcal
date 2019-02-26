package com.techease.k_kcal.ui.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.techease.k_kcal.R;
import com.techease.k_kcal.models.itemDataModels.ItemDetailModel;
import com.techease.k_kcal.models.itemDataModels.ItemResturantDetailModel;

import java.util.List;

import butterknife.ButterKnife;


public class ResturantFragment extends Fragment {
    View view;
    public  static List<ItemResturantDetailModel> itemResturantDetailModelList;
    public static  ItemResturantDetailModel resturantDetailModel;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_resturant, container, false);
        initUI();
        return view;
    }

    private void initUI(){
        ButterKnife.bind(this,view);
        Toast.makeText(getActivity(), resturantDetailModel.getName(), Toast.LENGTH_SHORT).show();

    }
}

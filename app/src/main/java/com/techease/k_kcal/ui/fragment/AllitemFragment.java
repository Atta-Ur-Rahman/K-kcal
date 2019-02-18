package com.techease.k_kcal.ui.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.techease.k_kcal.R;
import com.techease.k_kcal.adapters.AllitemAdapters;
import com.techease.k_kcal.models.itemDataModels.ItemDetailModel;
import com.techease.k_kcal.models.itemDataModels.ItemResponseModel;
import com.techease.k_kcal.models.itemDataModels.ItemResturantDetailModel;
import com.techease.k_kcal.networking.ApiClient;
import com.techease.k_kcal.networking.ApiInterface;
import com.techease.k_kcal.utilities.AlertUtils;
import com.techease.k_kcal.utilities.GeneralUtills;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class AllitemFragment extends Fragment {
    android.support.v7.app.AlertDialog alertDialog;
    @BindView(R.id.rv_all_items)
    RecyclerView rvItems;
    @BindView(R.id.iv_profile)
    ImageView ivProfile;
    AllitemAdapters allitemAdapters;
    List<ItemDetailModel> itemDataModelList;
    View view;

    String token;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_item_found, container, false);
        initUI();
        return view;
    }

    private void initUI() {
        ButterKnife.bind(this, view);
        token = GeneralUtills.getApiToken(getActivity());
        RecyclerView.LayoutManager mLayoutManagerReviews = new LinearLayoutManager(getActivity());
        rvItems.setLayoutManager(mLayoutManagerReviews);
        itemDataModelList = new ArrayList<>();

        alertDialog = AlertUtils.createProgressDialog(getActivity());
        alertDialog.show();
        apiCallAllItems();

        ivProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GeneralUtills.connectFragment(getActivity(),new ProfileFragment());
            }
        });

    }

    private void apiCallAllItems() {
        ApiInterface services = ApiClient.getApiClient(token).create(ApiInterface.class);
        Call<ItemResponseModel> allUsers = services.getItems();
        allUsers.enqueue(new Callback<ItemResponseModel>() {
            @Override
            public void onResponse(Call<ItemResponseModel> call, Response<ItemResponseModel> response) {
                alertDialog.dismiss();
                if (response.body() == null) {
                    Toast.makeText(getActivity(), "no data", Toast.LENGTH_SHORT).show();
                } else if (response.body().getStatus()) {

                    itemDataModelList.addAll(response.body().getData());
                    allitemAdapters = new AllitemAdapters(getActivity(), itemDataModelList);
                    rvItems.setAdapter(allitemAdapters);
                    allitemAdapters.notifyDataSetChanged();

                } else {
                    Toast.makeText(getActivity(), "No Data Found", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<ItemResponseModel> call, Throwable t) {
                Log.d("fail", t.getMessage());
            }
        });
    }

}

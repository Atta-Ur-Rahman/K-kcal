package com.techease.k_kcal.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.techease.k_kcal.R;
import com.techease.k_kcal.adapters.ResturantsAdapters;
import com.techease.k_kcal.models.itemDataModels.ItemDetailModel;
import com.techease.k_kcal.models.itemDataModels.ItemResponseModel;
import com.techease.k_kcal.models.itemDataModels.ResturantDetailModel;
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


public class ResturantFragment extends Fragment {
    android.support.v7.app.AlertDialog alertDialog;
    View view;
    @BindView(R.id.rv_resturants)
    RecyclerView rvResturant;
    ResturantsAdapters resturantsAdapters;
    List<ResturantDetailModel> itemResturantDetailModels;

    String strToken;
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

        strToken = GeneralUtills.getApiToken(getActivity());
        RecyclerView.LayoutManager mLayoutManagerReviews = new LinearLayoutManager(getActivity());
        rvResturant.setLayoutManager(mLayoutManagerReviews);
        itemResturantDetailModels = new ArrayList<>();
        alertDialog = AlertUtils.createProgressDialog(getActivity());
        alertDialog.show();
        apiCallResturants();

    }

    private void apiCallResturants() {
        ApiInterface services = ApiClient.getApiClient(strToken).create(ApiInterface.class);
        Call<ItemResponseModel> allUsers = services.getResturants(GeneralUtills.getItemID(getActivity()));
        allUsers.enqueue(new Callback<ItemResponseModel>() {
            @Override
            public void onResponse(Call<ItemResponseModel> call, Response<ItemResponseModel> response) {
                alertDialog.dismiss();
                if (response.body() == null) {
                    Toast.makeText(getActivity(), "no data", Toast.LENGTH_SHORT).show();
                } else  {
                    itemResturantDetailModels.addAll(response.body().getData().get(0).getRestaurants());
                    resturantsAdapters = new ResturantsAdapters(getActivity(), itemResturantDetailModels);
                    rvResturant.setAdapter(resturantsAdapters);
                    resturantsAdapters.notifyDataSetChanged();

                }



            }

            @Override
            public void onFailure(Call<ItemResponseModel> call, Throwable t) {
                Log.d("fail", t.getMessage());
            }
        });
    }
}

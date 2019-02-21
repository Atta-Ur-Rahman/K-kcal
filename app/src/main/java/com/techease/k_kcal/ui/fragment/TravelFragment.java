package com.techease.k_kcal.ui.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.techease.k_kcal.R;
import com.techease.k_kcal.adapters.AllitemAdapters;
import com.techease.k_kcal.models.itemDataModels.ItemResponseModel;
import com.techease.k_kcal.models.travelDataModel.TravelReponseModel;
import com.techease.k_kcal.networking.ApiClient;
import com.techease.k_kcal.networking.ApiInterface;
import com.techease.k_kcal.utilities.AlertUtils;
import com.techease.k_kcal.utilities.GeneralUtills;

import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class TravelFragment extends Fragment {
    AlertDialog alertDialog;
    View view;
    @BindView(R.id.btn_continue_travel)
    Button btnContinue;
    @BindView(R.id.iv_car)
    ImageView ivCar;
    @BindView(R.id.iv_walk)
    ImageView ivWalk;
    @BindView(R.id.iv_car_check)
    ImageView ivCarCheck;
    @BindView(R.id.iv_walk_check)
    ImageView ivWalkCheck;

    String strWalk = "";
    private boolean valid = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_like_travel, container, false);
        initUI();
        return view;
    }

    private void initUI() {
        ButterKnife.bind(this, view);

        ivCar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ivCarCheck.setVisibility(View.VISIBLE);
                ivWalkCheck.setVisibility(View.GONE);
                strWalk = "car";
            }
        });

        ivWalk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ivCarCheck.setVisibility(View.GONE);
                ivWalkCheck.setVisibility(View.VISIBLE);
                strWalk = " walk";
            }
        });

        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Validate()) {
                    alertDialog = AlertUtils.createProgressDialog(getActivity());
                    alertDialog.show();
                    apiCallTravelInfo();
                }
            }
        });
    }


    private void apiCallTravelInfo() {
        ApiInterface services = ApiClient.getApiClient(GeneralUtills.getApiToken(getActivity())).create(ApiInterface.class);
        Call<TravelReponseModel> allUsers = services.travelInfo(strWalk);
        allUsers.enqueue(new Callback<TravelReponseModel>() {
            @Override
            public void onResponse(Call<TravelReponseModel> call, Response<TravelReponseModel> response) {
                alertDialog.dismiss();
                if (response.body() == null) {
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        Toast.makeText(getActivity(), jObjError.getString("message"), Toast.LENGTH_SHORT).show();

                    } catch (Exception e) {
                        Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                } else if (response.body().getStatus()) {
                    Toast.makeText(getActivity(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    GeneralUtills.connectFragment(getActivity(), new AllowFragment());
                }


            }

            @Override
            public void onFailure(Call<TravelReponseModel> call, Throwable t) {
                Log.d("fail", t.getMessage());
            }
        });
    }

    private boolean Validate() {
        valid = true;

        if (strWalk.isEmpty()) {
            Toast.makeText(getActivity(), "please select walk", Toast.LENGTH_SHORT).show();
            valid = false;
        } else {
            Log.d("error", "no error");
        }

        return valid;
    }
}

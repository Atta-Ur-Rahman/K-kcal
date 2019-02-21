package com.techease.k_kcal.ui.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.techease.k_kcal.R;
import com.techease.k_kcal.models.filterDataModels.ItemCategoriesModel;
import com.techease.k_kcal.models.logindatamodels.LoginResponseModel;
import com.techease.k_kcal.models.moreinfoDataModel.MoreInfoProfileModel;
import com.techease.k_kcal.networking.ApiClient;
import com.techease.k_kcal.networking.ApiInterface;
import com.techease.k_kcal.utilities.AlertUtils;
import com.techease.k_kcal.utilities.Config;
import com.techease.k_kcal.utilities.GeneralUtills;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class AboutUserFragment extends Fragment {
    AlertDialog alertDialog;
    View view;

    @BindView(R.id.iv_male)
    ImageView ivMale;
    @BindView(R.id.iv_female)
    ImageView ivFemale;
    @BindView(R.id.iv_male_check)
    ImageView ivMaleCheck;
    @BindView(R.id.iv_female_check)
    ImageView ivFemaleCheck;
    @BindView(R.id.et_age)
    EditText etAge;
    @BindView(R.id.et_height)
    EditText etHeight;
    @BindView(R.id.et_weight)
    EditText etWeight;
    @BindView(R.id.et_profession)
    EditText etProfession;
    @BindView(R.id.et_life_style)
    EditText etLifeStyle;
    @BindView(R.id.btn_continue)
    Button btnContinue;

    String strGender = "", strAge, strWeight, strHeight, strProfession, strLifeStyle, strAddress;
    private boolean valid = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragmen
        view = inflater.inflate(R.layout.fragment_about_you, container, false);
        initUI();
        return view;
    }

    private void initUI() {
        ButterKnife.bind(this, view);

        ivMale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ivMaleCheck.setVisibility(View.VISIBLE);
                ivFemaleCheck.setVisibility(View.GONE);
                strGender = "male";
                Toast.makeText(getActivity(), strGender, Toast.LENGTH_SHORT).show();
            }
        });

        ivFemale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ivMaleCheck.setVisibility(View.GONE);
                ivFemaleCheck.setVisibility(View.VISIBLE);
                strGender = "female";
                Toast.makeText(getActivity(), strGender, Toast.LENGTH_SHORT).show();
            }
        });

        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validate()) {
                    alertDialog = AlertUtils.createProgressDialog(getActivity());
                    alertDialog.show();
                    apiCallUserInfo();
                }

            }
        });
    }

    private void apiCallUserInfo() {
        ApiInterface services = ApiClient.getApiClient(GeneralUtills.getApiToken(getActivity())).create(ApiInterface.class);
        Call<MoreInfoProfileModel> userInfo = services.addMoreInfo(strGender, strAge, strHeight, strWeight, strProfession, strLifeStyle, strAddress);
        userInfo.enqueue(new Callback<MoreInfoProfileModel>() {
            @Override
            public void onResponse(Call<MoreInfoProfileModel> call, Response<MoreInfoProfileModel> response) {
                alertDialog.dismiss();
                if (response.body() == null) {
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        Toast.makeText(getActivity(), jObjError.getString("message"), Toast.LENGTH_SHORT).show();
                        if (jObjError.getString("message").contains("Successfully")){
                            GeneralUtills.connectFragment(getActivity(), new AllowFragment());
                        }else {
                            Toast.makeText(getActivity(), jObjError.getString("message"), Toast.LENGTH_SHORT).show();
                        }

                    } catch (Exception e) {
                        Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                } else if (response.body().getStatus()) {
                    Toast.makeText(getActivity(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    GeneralUtills.connectFragment(getActivity(), new AllowFragment());
                }
            }

            @Override
            public void onFailure(Call<MoreInfoProfileModel> call, Throwable t) {
                alertDialog.dismiss();
                Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private boolean validate() {
        valid = true;

        strAge = etAge.getText().toString().trim();
        strHeight = etHeight.getText().toString().trim();
        strWeight = etWeight.getText().toString().trim();
        strProfession = etProfession.getText().toString().trim();
        strLifeStyle = etLifeStyle.getText().toString().trim();
        strAddress = "peshawar";

        if (strGender.isEmpty()) {
            Toast.makeText(getActivity(), "please select Gender", Toast.LENGTH_SHORT).show();
            valid = false;
        } else {
            Log.d("error", "no error");
        }

        if (strAge.isEmpty()) {
            etAge.setError("enter a your age");
            valid = false;
        } else {
            etAge.setError(null);
        }
        if (strHeight.isEmpty()) {
            etHeight.setError("Please enter your height");
            valid = false;
        } else {
            etHeight.setError(null);
        }

        if (strWeight.isEmpty()) {
            etWeight.setError("Please enter your weight");
            valid = false;
        } else {
            etWeight.setError(null);
        }

        if (strProfession.isEmpty()) {
            etProfession.setError("Please select your profession");
            valid = false;
        } else {
            etProfession.setError(null);
        }

        if (strLifeStyle.isEmpty()) {
            etLifeStyle.setError("Please select your lifestyle");
            valid = false;
        } else {
            etLifeStyle.setError(null);
        }
        return valid;
    }

}

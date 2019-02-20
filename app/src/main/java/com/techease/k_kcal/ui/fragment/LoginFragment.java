package com.techease.k_kcal.ui.fragment;


import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.techease.k_kcal.R;
import com.techease.k_kcal.models.logindatamodels.LoginResponseModel;
import com.techease.k_kcal.networking.ApiClient;
import com.techease.k_kcal.networking.ApiInterface;
import com.techease.k_kcal.ui.fragment.forgotpassword.ForgotPasswordFragment;
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
public class LoginFragment extends Fragment {
    AlertDialog alertDialog;
    View view;
    @BindView(R.id.et_email_address)
    EditText etEmail;
    @BindView(R.id.et_password)
    EditText etPassword;
    @BindView(R.id.btn_login)
    TextView tvLogin;
    @BindView(R.id.tv_signup)
    TextView tvSignUp;
    @BindView(R.id.tv_forgot_password)
    TextView tvForgotPaswword;

    Boolean valid = false;
    String strEmail, strPassword;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_login, container, false);
        initUI();
        return view;
    }

    private void initUI() {
        ButterKnife.bind(this, view);

        Toast.makeText(getActivity(), GeneralUtills.getLat(getActivity()), Toast.LENGTH_SHORT).show();



        tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validate()) {
                    alertDialog = AlertUtils.createProgressDialog(getActivity());
                    alertDialog.show();
                    userLogin();
                }
            }
        });

        tvSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GeneralUtills.connectFragment(getActivity(), new SignUpFragment());
            }
        });

        tvForgotPaswword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GeneralUtills.connectFragment(getActivity(),new ForgotPasswordFragment());
            }
        });
    }

    private void userLogin() {
        ApiInterface services = ApiClient.getApiClient().create(ApiInterface.class);
        Call<LoginResponseModel> userLogin = services.userLogin(strEmail, strPassword, GeneralUtills.getLat(getActivity()),GeneralUtills.getLng(getActivity()));
        userLogin.enqueue(new Callback<LoginResponseModel>() {
            @Override
            public void onResponse(Call<LoginResponseModel> call, Response<LoginResponseModel> response) {
                alertDialog.dismiss();
                if (response.body() == null) {
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        Toast.makeText(getActivity(), jObjError.getString("message"), Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                } else if (response.body().getStatus()) {
                    String userID = response.body().getData().getUser().getId().toString();
                    GeneralUtills.putBooleanValueInEditor(getContext(),"isLogin",true);
                    GeneralUtills.putStringValueInEditor(getActivity(), "userID", userID);
                    GeneralUtills.putStringValueInEditor(getActivity(), "api_token", response.body().getData().getUser().getToken());
                    GeneralUtills.connectFragment(getActivity(), new AllitemFragment());
                }
            }

            @Override
            public void onFailure(Call<LoginResponseModel> call, Throwable t) {
                alertDialog.dismiss();
                Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private boolean validate() {
        valid = true;

        strEmail = etEmail.getText().toString().trim();
        strPassword = etPassword.getText().toString().trim();

        if (strEmail.isEmpty()) {
            etEmail.setError("enter a valid email address");
            valid = false;
        } else {
            etEmail.setError(null);
        }
        if (strPassword.isEmpty()) {
            etPassword.setError("Please enter a valid password");
            valid = false;
        } else {
            etPassword.setError(null);
        }
        return valid;
    }

}

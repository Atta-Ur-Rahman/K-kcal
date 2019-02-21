package com.techease.k_kcal.ui.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.techease.k_kcal.R;
import com.techease.k_kcal.models.logindatamodels.LoginResponseModel;
import com.techease.k_kcal.models.resendCodeDataModel.ResendCodeModel;
import com.techease.k_kcal.models.verifiyDataModels.VerifyResponseModel;
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
public class VerifyCodeFragment extends Fragment {
    AlertDialog alertDialog;
    View view;
    @BindView(R.id.et_code_num1)
    EditText et_num1;
    @BindView(R.id.et_code_num2)
    EditText et_num2;
    @BindView(R.id.et_code_num3)
    EditText et_num3;
    @BindView(R.id.et_code_num4)
    EditText et_num4;
    @BindView(R.id.btn_verify)
    Button btnVerify;
    @BindView(R.id.layout_resend_code)
    LinearLayout layoutResendCode;
    private String strVerifycode;

    String strToken;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_verify_code, container, false);
        ButterKnife.bind(this, view);
        strToken = GeneralUtills.getApiToken(getActivity());

        et_num1.addTextChangedListener(genraltextWatcher);
        et_num2.addTextChangedListener(genraltextWatcher);
        et_num3.addTextChangedListener(genraltextWatcher);
        et_num4.addTextChangedListener(genraltextWatcher);

        btnVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onDataInput();
            }
        });

        layoutResendCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog = AlertUtils.createProgressDialog(getActivity());
                alertDialog.show();
                apicallResendCode();
            }
        });
        return view;
    }

    private void onDataInput() {
        String num1 = et_num1.getText().toString();
        String num2 = et_num2.getText().toString();
        String num3 = et_num3.getText().toString();
        String num4 = et_num4.getText().toString();

        strVerifycode = num1 + num2 + num3 + num4;

        if (strVerifycode.equals("")) {
            Toast.makeText(getActivity(), "Please enter a valid code", Toast.LENGTH_SHORT).show();
        } else {
            alertDialog = AlertUtils.createProgressDialog(getActivity());
            alertDialog.show();
            apicallVerification();
        }
    }

    private void apicallVerification() {
        ApiInterface services = ApiClient.getApiClient(strToken).create(ApiInterface.class);
        Call<VerifyResponseModel> userVerify = services.userVerification(strVerifycode);
        userVerify.enqueue(new Callback<VerifyResponseModel>() {
            @Override
            public void onResponse(Call<VerifyResponseModel> call, Response<VerifyResponseModel> response) {
                alertDialog.dismiss();

                if (response.body() == null) {
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        Toast.makeText(getActivity(), jObjError.getString("message"), Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {

                    }
                } else if (response.body().getStatus()) {
                    Toast.makeText(getActivity(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    GeneralUtills.connectFragment(getActivity(), new AboutUserFragment());
                }
            }

            @Override
            public void onFailure(Call<VerifyResponseModel> call, Throwable t) {
                alertDialog.dismiss();
                Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void apicallResendCode() {
        ApiInterface services = ApiClient.getApiClient(strToken).create(ApiInterface.class);
        Call<ResendCodeModel> userVerify = services.resendCode();
        userVerify.enqueue(new Callback<ResendCodeModel>() {
            @Override
            public void onResponse(Call<ResendCodeModel> call, Response<ResendCodeModel> response) {
                alertDialog.dismiss();

                if (response.body() == null) {
                    try {
                        Toast.makeText(getActivity(), "Email already take", Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {

                    }
                } else if (response.body().getStatus()) {
                    Toast.makeText(getActivity(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    GeneralUtills.connectFragment(getActivity(), new LoginFragment());
                }
            }

            @Override
            public void onFailure(Call<ResendCodeModel> call, Throwable t) {
                alertDialog.dismiss();
                Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private TextWatcher genraltextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void afterTextChanged(Editable editable) {

            if (et_num1.length() == 1) {

                et_num2.requestFocus();

            }
            if (et_num2.length() == 1) {

                et_num3.requestFocus();

            }
            if (et_num3.length() == 1) {

                et_num4.requestFocus();

            }
        }

    };

}

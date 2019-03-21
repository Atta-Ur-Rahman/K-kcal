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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.techease.k_kcal.R;
import com.techease.k_kcal.models.logindatamodels.LoginResponseModel;
import com.techease.k_kcal.models.socialLoginModel.SocialResponseModel;
import com.techease.k_kcal.networking.ApiClient;
import com.techease.k_kcal.networking.ApiInterface;
import com.techease.k_kcal.ui.fragment.forgotpassword.ForgotPasswordFragment;
import com.techease.k_kcal.utilities.AlertUtils;
import com.techease.k_kcal.utilities.GeneralUtills;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends Fragment  {
    private String TAG = "LoginFragment";
    Unbinder unbinder;
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
    @BindView(R.id.btn_fb)
    ImageView btnFacebook;
    @BindView(R.id.sign_in_button)
    ImageView signInButton;

    Boolean valid = false;
    String strName, strEmail, strPassword;

    CallbackManager callbackManager;
    private LoginButton loginButton;
    private static final String EMAIL = "email";
    private GoogleSignInOptions gso;
    private GoogleSignInClient mGoogleSignInClient;
    private GoogleSignInAccount account;
    private static final int RC_SIGN_IN = 200;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_login, container, false);
        unbinder = ButterKnife.bind(this, view);
        loginButton = view.findViewById(R.id.login_button);
        callbackManager = CallbackManager.Factory.create();
        loginButton.setReadPermissions(Arrays.asList(EMAIL));
        loginButton.setFragment(this);
        initUI();

        btnFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginButton.performClick();

                // Callback registration for facebook login
                loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {

                        final GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                try {
                                    alertDialog = AlertUtils.createProgressDialog(getActivity());
                                    alertDialog.show();
                                    socialLoginApiCall(object.getString("email"));


                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    Log.d(TAG, e.getMessage().toString());
                                }
                            }
                        });
                        Bundle parameters = new Bundle();
                        parameters.putString("fields", "id, first_name, last_name, email,gender, birthday, location");
                        request.setParameters(parameters);
                        request.executeAsync();

                    }

                    @Override
                    public void onCancel() {
                        // App code
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        // App code
                    }
                });
                //end
            }
        });

        //google Sign in code

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestEmail()
                        .build();
                mGoogleSignInClient = GoogleSignIn.getClient(getActivity(), gso);
                account = GoogleSignIn.getLastSignedInAccount(getActivity());
                updateUI(account);
                signIn();
            }
        });
        return view;
    }

    private void initUI() {

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
                GeneralUtills.connectFragment(getActivity(), new ForgotPasswordFragment());
            }
        });
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }


    private void userLogin() {
        ApiInterface services = ApiClient.getApiClient().create(ApiInterface.class);
        Call<LoginResponseModel> userLogin = services.userLogin(strEmail, strPassword, GeneralUtills.getLat(getActivity()), GeneralUtills.getLng(getActivity()));
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
                    GeneralUtills.putBooleanValueInEditor(getContext(), "isLogin", true);
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

    //networking call for social login
    private void socialLoginApiCall(String email) {
        Toast.makeText(getActivity(), email, Toast.LENGTH_SHORT).show();
        ApiInterface services = ApiClient.getApiClient().create(ApiInterface.class);
        Call<SocialResponseModel> userLogin = services.socialLogin(email, GeneralUtills.getLat(getActivity()), GeneralUtills.getLng(getActivity()));
        userLogin.enqueue(new Callback<SocialResponseModel>() {
            @Override
            public void onResponse(Call<SocialResponseModel> call, Response<SocialResponseModel> response) {
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
                    GeneralUtills.putBooleanValueInEditor(getContext(), "isLogin", true);
                    GeneralUtills.putStringValueInEditor(getActivity(), "userID", userID);
                    GeneralUtills.putStringValueInEditor(getActivity(), "api_token", response.body().getData().getUser().getToken());
                    GeneralUtills.connectFragment(getActivity(), new AllitemFragment());
                }
            }

            @Override
            public void onFailure(Call<SocialResponseModel> call, Throwable t) {
                alertDialog.dismiss();
                Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            updateUI(account);
        } catch (ApiException e) {
            updateUI(null);
        }
    }

    private void updateUI(GoogleSignInAccount account) {

        if (account != null) {
            Log.d("zma",account.getEmail());
            alertDialog = AlertUtils.createProgressDialog(getActivity());
            alertDialog.show();
            strName = account.getDisplayName();
            socialLoginApiCall(account.getEmail());

        } else {
            Log.d("googleError", "you got some error");
        }
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

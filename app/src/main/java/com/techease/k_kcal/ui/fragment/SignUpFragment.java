package com.techease.k_kcal.ui.fragment;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.techease.k_kcal.R;
import com.techease.k_kcal.models.SocialSignUpResponseModel;
import com.techease.k_kcal.networking.ApiClient;
import com.techease.k_kcal.networking.ApiInterface;
import com.techease.k_kcal.utilities.AlertUtils;
import com.techease.k_kcal.utilities.GeneralUtills;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class SignUpFragment extends Fragment {
    private String TAG = "SignUpFragment";
    Unbinder unbinder;
    AlertDialog alertDialog;
    View view;
    @BindView(R.id.btn_signup_email)
    Button btnSignUpWithEmail;
    @BindView(R.id.tv_already_login)
    TextView tvAlreadyLogin;

    @BindView(R.id.btn_facebook)
    ImageView btnFacebook;
    @BindView(R.id.sign_in_button)
    ImageView signInButton;

    Boolean valid = false;
    String strImage;
    File sourceFile;

    CallbackManager callbackManager;
    private LoginButton loginButton;
    private static final String EMAIL = "email";
    private GoogleSignInOptions gso;
    private GoogleSignInClient mGoogleSignInClient;
    private GoogleSignInAccount account;
    private static final int RC_SIGN_IN = 100;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_sign_up_, container, false);
        unbinder = ButterKnife.bind(this, view);
        loginButton = view.findViewById(R.id.fb_login_button);
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
                                getFacebookData(object);

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
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void initUI() {
        ButterKnife.bind(this, view);
        stictModePolicy();

        btnSignUpWithEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GeneralUtills.connectFragment(getActivity(), new RegistrationFragment());
            }
        });

        tvAlreadyLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GeneralUtills.connectFragment(getContext(), new LoginFragment());
            }
        });
    }


    private void getFacebookData(JSONObject object) {

        try {
            String id = object.getString("id");
            try {
                URL profile_pic = new URL("https://graph.facebook.com/" + id + "/picture?width=200&height=150");
                try {
                    URL url = new URL(String.valueOf(profile_pic));
                    Bitmap bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                    saveImage(bitmap, "social");
                } catch (IOException e) {
                    System.out.println(e);
                }
                socialSignupApiCall(object.getString("name"), object.getString("email"), "facebook");

            } catch (MalformedURLException e) {
                e.printStackTrace();
                Log.d(TAG, String.valueOf(e.getCause()));
            }

        } catch (JSONException e) {
            Log.d(TAG, "Error parsing JSON");
        }
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
            Log.d("zma",account.getPhotoUrl().toString());
            alertDialog = AlertUtils.createProgressDialog(getActivity());
            alertDialog.show();
            try {
                URL url = new URL(account.getPhotoUrl().toString());
                Bitmap bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                saveImage(bitmap, "social");
            } catch (IOException e) {
                System.out.println(e);
            }
           // socialSignupApiCall(account.getDisplayName(), account.getEmail(), "google");

        } else {
            Log.d("googleError", "you got some error");
        }
    }

    private void socialSignupApiCall(String name, String email, String strSignupType) {
        Toast.makeText(getActivity(), name, Toast.LENGTH_SHORT).show();
        ApiInterface services = ApiClient.getApiClient().create(ApiInterface.class);
        final RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), sourceFile);
        final MultipartBody.Part profileImage = MultipartBody.Part.createFormData("profilePicture", sourceFile.getName(), requestFile);
        RequestBody Bodyname = RequestBody.create(MediaType.parse("text/plain"), "upload_test");
        RequestBody nameBody = RequestBody.create(MediaType.parse("multipart/form-data"), name);
        RequestBody emailBody = RequestBody.create(MediaType.parse("multipart/form-data"), email);
        RequestBody signupType = RequestBody.create(MediaType.parse("multipart/form-data"), strSignupType);
        RequestBody latBody = RequestBody.create(MediaType.parse("multipart/form-data"), GeneralUtills.getLat(getActivity()));
        RequestBody lonBody = RequestBody.create(MediaType.parse("multipart/form-data"), GeneralUtills.getLat(getActivity()));
        RequestBody countryBody = RequestBody.create(MediaType.parse("multipart/form-data"), "pakistan");
        RequestBody deviceTypeBody = RequestBody.create(MediaType.parse("multipart/form-data"), "android");

        final Call<SocialSignUpResponseModel> resgistration = services.socialSignUp(nameBody, emailBody, signupType, latBody, lonBody, countryBody, profileImage, deviceTypeBody, Bodyname);
        resgistration.enqueue(new Callback<SocialSignUpResponseModel>() {
            @Override
            public void onResponse(Call<SocialSignUpResponseModel> call, Response<SocialSignUpResponseModel> response) {
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
                    GeneralUtills.putStringValueInEditor(getActivity(), "api_token", response.body().getData().getToken());
                    GeneralUtills.withOutBackStackConnectFragment(getActivity(), new VerifyCodeFragment());
                }

            }

            @Override
            public void onFailure(Call<SocialSignUpResponseModel> call, Throwable t) {
                alertDialog.dismiss();
                Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.d("error", t.getMessage());


            }
        });
    }

    private void saveImage(Bitmap finalBitmap, String image_name) {

        File myDir = new File(Environment.getExternalStorageDirectory(), "kkcal");
        myDir.mkdirs();
        String fname = image_name + ".PNG";
        File file = new File(myDir, fname);
        if (file.exists()) file.delete();

        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.PNG, 80, out);
            out.flush();
            out.close();
            getOptionalFile();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getOptionalFile() {
        File path = new File(Environment.getExternalStorageDirectory(), "kkcal/social.PNG");
        sourceFile = path;
    }

    private void stictModePolicy() {
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }


}

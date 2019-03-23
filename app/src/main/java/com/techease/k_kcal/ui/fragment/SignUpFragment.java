package com.techease.k_kcal.ui.fragment;


import android.app.WallpaperManager;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
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

import com.bumptech.glide.Glide;
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
    @BindView(R.id.iv_fb_profile)
    ImageView ivFbProfile;
    @BindView(R.id.btn_signup_email)
    Button btnSignUpWithEmail;
    @BindView(R.id.tv_already_login)
    TextView tvAlreadyLogin;

    @BindView(R.id.btn_facebook)
    ImageView btnFacebook;
    @BindView(R.id.sign_in_button)
    ImageView signInButton;

    Boolean valid = false;
    String strImage, strName, strEmail;
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
        GeneralUtills.grantPermission(getActivity());
        strictModePolicy();
        loginButton = view.findViewById(R.id.fb_login_button);
        loginButton.setReadPermissions(Arrays.asList("email", "public_profile"));
        callbackManager = CallbackManager.Factory.create();
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
                                    strName = object.getString("first_name") + object.getString("last_name");
                                    strEmail = object.getString("email");
                                    String id = object.getString("id");
                                    URL profile_pic = null;
                                    try {
//                                        profile_pic = new URL("https://graph.facebook.com/" + id + "/picture?width=200&height=150");

                                        URL url = new URL("https://graph.facebook.com/" + id + "/picture?width=160&height=160");
                                        Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());

                                        Toast toast = new Toast(getActivity());
                                        ImageView view = new ImageView(getActivity());
                                        view.setImageBitmap(bmp);
                                        toast.setView(view);
                                        toast.show();

                                        strImage = String.valueOf(profile_pic);
                                        Glide.with(getActivity()).load(strImage).into(ivFbProfile);
                                    } catch (MalformedURLException e) {
                                        e.printStackTrace();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }


                                    if (validate()) {
                                        alertDialog = AlertUtils.createProgressDialog(getActivity());
                                        alertDialog.show();
                                        socialSignupApiCall("facebook");
                                    }


                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    Log.d("zmaFbE", e.getMessage().toString());
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

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestProfile()
                .requestId()
                .requestIdToken(getResources().getString(R.string.client_id))
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(getActivity(), gso);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
            updateUI(null);
        }
    }

    private void updateUI(GoogleSignInAccount account) {

        if (account != null) {
            strName = account.getDisplayName();
            if (null != account.getPhotoUrl()) {
                strImage = account.getPhotoUrl().toString();
            }
            strEmail = account.getEmail();
            if (validate()) {
                alertDialog = AlertUtils.createProgressDialog(getActivity());
                alertDialog.show();
                socialSignupApiCall("google");
            }

        } else {
            Log.d("googleError", "you got some error");
        }
    }

    private void socialSignupApiCall(String strSignupType) {
        if (sourceFile == null) {
            sourceFile = new File(Environment.getExternalStorageDirectory(), "Kcal/profile.PNG");
            sourceFile = new File(sourceFile.getAbsolutePath());
        }

        ApiInterface services = ApiClient.getApiClient().create(ApiInterface.class);
        final RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), sourceFile);
        final MultipartBody.Part profileImage = MultipartBody.Part.createFormData("profilePicture", sourceFile.getName(), requestFile);
        RequestBody Bodyname = RequestBody.create(MediaType.parse("text/plain"), "upload_test");
        RequestBody nameBody = RequestBody.create(MediaType.parse("multipart/form-data"), strName);
        RequestBody emailBody = RequestBody.create(MediaType.parse("multipart/form-data"), strEmail);
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

    private boolean validate() {
        valid = true;

        if (sourceFile == null) {
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.profile);
            saveImage(bitmap);
        } else {
            Log.d(TAG, "ok");
        }

        return valid;
    }

//    private void getProfileImage() {
//        try {
//            url = new URL(strImage);
//            bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());
//            saveImage(bitmap);
//        } catch (MalformedURLException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    private void saveImage(Bitmap finalBitmap) {

        File myDir = new File(Environment.getExternalStorageDirectory(), "Kcal");
        myDir.mkdirs();
        String image_name = "profile";
        String imageName = image_name + ".PNG";
        File file = new File(myDir, imageName);
        if (file.exists()) {
            file.delete();
        }

        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.PNG, 80, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void strictModePolicy() {
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }


}

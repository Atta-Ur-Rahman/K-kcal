package com.techease.k_kcal.ui.fragment;


import android.app.WallpaperManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
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
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.facebook.login.widget.ProfilePictureView;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.techease.k_kcal.R;
import com.techease.k_kcal.models.SocialSignUpResponseModel;
import com.techease.k_kcal.networking.ApiClient;
import com.techease.k_kcal.networking.ApiInterface;
import com.techease.k_kcal.utilities.AlertUtils;
import com.techease.k_kcal.utilities.FlushedInputStream;
import com.techease.k_kcal.utilities.GeneralUtills;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
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
    ImageView profilePictureView;
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
    Bitmap bitmap;
    File sourceFile;

    CallbackManager callbackManager;
    private LoginButton loginButton;
    private GoogleSignInClient mGoogleSignInClient;
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
        loginButton.setReadPermissions(Arrays.asList("email"));
        callbackManager = CallbackManager.Factory.create();
        loginButton.setFragment(this);
        initUI();

        btnFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginButton.performClick();

                loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(final LoginResult loginResult) {

                        final GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                try {
                                    strName = object.getString("first_name") + object.getString("last_name");
                                    strEmail = object.getString("email");
                                    String id = object.getString("id");
                                    strImage = "https://graph.facebook.com/" + id + "/picture?type=normal";
                                    new Async().execute(strImage);

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
            strEmail = account.getEmail();
            if (null != account.getPhotoUrl()) {
                new Async().execute(account.getPhotoUrl().toString());
            }

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
        sourceFile = new File(Environment.getExternalStorageDirectory(), "Kcal/profile.PNG");

        if (sourceFile == null) {
            valid = false;
            LoginManager.getInstance().logOut();
            Toast.makeText(getActivity(), "please try again", Toast.LENGTH_SHORT).show();
        } else {
            Log.d(TAG, "ok");
        }

        return valid;
    }


    private void strictModePolicy() {
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }

    private class Async extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... params) {
            try {
                URL url = new URL(params[0]);
                bitmap = BitmapFactory.decodeStream(new FlushedInputStream((InputStream) url.getContent()));
            } catch (IOException e) {
                Log.e(TAG, e.getMessage());
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            if (bitmap != null) {
                saveImage(bitmap);
            } else {
                bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.profile);
                saveImage(bitmap);
            }


        }
    }

    private void saveImage(Bitmap finalBitmap) {

        File myDir = new File(Environment.getExternalStorageDirectory(), "Kcal");
        myDir.mkdirs();
        File file = new File(myDir, "profile.PNG");
        if (file.exists()) {
            file.delete();
        }

        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (validate()) {
            alertDialog = AlertUtils.createProgressDialog(getActivity());
            alertDialog.show();
            socialSignupApiCall("facebook");

        }
    }

}

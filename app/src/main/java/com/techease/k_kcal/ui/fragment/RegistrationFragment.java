package com.techease.k_kcal.ui.fragment;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
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

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.techease.k_kcal.R;
import com.techease.k_kcal.models.logindatamodels.LoginResponseModel;
import com.techease.k_kcal.models.signupdatamodels.SignUpResponseModel;
import com.techease.k_kcal.networking.ApiClient;
import com.techease.k_kcal.networking.ApiInterface;
import com.techease.k_kcal.ui.activities.MainActivity;
import com.techease.k_kcal.utilities.AlertUtils;
import com.techease.k_kcal.utilities.GeneralUtills;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import id.zelory.compressor.Compressor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class RegistrationFragment extends Fragment {
    AlertDialog alertDialog;
    View view;
    @BindView(R.id.et_fullname)
    EditText etFullName;
    @BindView(R.id.et_email)
    EditText etEmail;
    @BindView(R.id.et_phone)
    EditText etPhone;
    @BindView(R.id.et_password)
    EditText etPassword;
    @BindView(R.id.et_confirm_password)
    EditText etConfirmPassword;
    @BindView(R.id.iv_profile)
    ImageView ivProfile;
    @BindView(R.id.btn_signup)
    Button btnSignUp;

    boolean valid = false;
    String strFullName,strEmail,strPhone,strPassword,strConfirmPassword,strLatitude,strLongitude,strDeviceType;
    File sourceFile;
    final int CAMERA_CAPTURE = 1;
    final int RESULT_LOAD_IMAGE = 2;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_ahoy, container, false);
        GeneralUtills.grantPermission(getActivity());
        initUI();
        return view;
    }

    private void initUI(){
        ButterKnife.bind(this,view);

        ivProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraBuilder();
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               if(validate()){
                   alertDialog = AlertUtils.createProgressDialog(getActivity());
                   alertDialog.show();
                   apiCallRegistration();
               }
            }
        });
    }

    //open camera view
    public void cameraBuilder() {
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(getActivity());
        pictureDialog.setTitle("Open");
        String[] pictureDialogItems = {
                "\tGallery",
                "\tCamera"};
        pictureDialog.setItems(pictureDialogItems,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                galleryIntent();

                                break;
                            case 1:
                                cameraIntent();
                                break;
                        }
                    }
                });
        pictureDialog.show();
    }

    public void cameraIntent() {
        Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(captureIntent, CAMERA_CAPTURE);
    }

    public void galleryIntent() {
        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, RESULT_LOAD_IMAGE);
    }
    private void apiCallRegistration(){
        ApiInterface services = ApiClient.getApiClient().create(ApiInterface.class);
        final RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), sourceFile);
        final MultipartBody.Part profileImage = MultipartBody.Part.createFormData("profilePicture", sourceFile.getName(), requestFile);
        RequestBody Bodyname = RequestBody.create(MediaType.parse("text/plain"), "upload_test");
        RequestBody nameBody = RequestBody.create(MediaType.parse("multipart/form-data"), strFullName);
        RequestBody emailBody = RequestBody.create(MediaType.parse("multipart/form-data"), strEmail);
        RequestBody passwordBody = RequestBody.create(MediaType.parse("multipart/form-data"), strPassword);
        RequestBody confirmPasswordBody = RequestBody.create(MediaType.parse("multipart/form-data"), strConfirmPassword);
        RequestBody latBody = RequestBody.create(MediaType.parse("multipart/form-data"), strPhone);
        RequestBody lonBody = RequestBody.create(MediaType.parse("multipart/form-data"), strLatitude);
        RequestBody phoneBody = RequestBody.create(MediaType.parse("multipart/form-data"), strLongitude);
        RequestBody deviceTypeBody = RequestBody.create(MediaType.parse("multipart/form-data"), strDeviceType);

        final Call<SignUpResponseModel> resgistration = services.userSignUp(nameBody,emailBody,passwordBody,passwordBody,phoneBody,deviceTypeBody,latBody,lonBody,profileImage,Bodyname);
        resgistration.enqueue(new Callback<SignUpResponseModel>() {
            @Override
            public void onResponse(Call<SignUpResponseModel> call, Response<SignUpResponseModel> response) {
                alertDialog.dismiss();
                if (response.body() == null) {
                    Toast.makeText(getActivity(), "Error", Toast.LENGTH_SHORT).show();
                } else if (response.body().getStatus()) {
                  GeneralUtills.putStringValueInEditor(getActivity(),"api_token",response.body().getData().getToken());
                  GeneralUtills.connectFragment(getActivity(),new VerifyCodeFragment());
                } else {
                    Toast.makeText(getActivity(), "error", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<SignUpResponseModel> call, Throwable t) {
                alertDialog.dismiss();
                Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.d("error", t.getMessage());


            }
        });
    }

    private boolean validate() {
        valid = true;

        strFullName = etFullName.getText().toString();
        strEmail = etEmail.getText().toString();
        strPhone = etPhone.getText().toString();
        strPassword = etPassword.getText().toString();
        strConfirmPassword = etConfirmPassword.getText().toString();
        strLatitude = "33.333";
        strLongitude = "44.87987";
        strDeviceType = "Android";

        if(sourceFile==null){
            Toast.makeText(getActivity(), "please select your profile", Toast.LENGTH_SHORT).show();
            valid = false;
        }
        else {

        }

        if (strFullName.isEmpty()) {
            etFullName.setError("enter your full name");
            valid = false;
        } else {
            etFullName.setError(null);
        }

        if (strEmail.isEmpty()) {
            etEmail.setError("enter a valid email address");
            valid = false;
        } else {
            etEmail.setError(null);
        }

        if (strPhone.isEmpty()) {
            etPhone.setError("enter a valid phone number");
            valid = false;
        } else {
            etPhone.setError(null);
        }

        if (strConfirmPassword.isEmpty()) {
            etConfirmPassword.setError("enter a valid password");
            valid = false;
        } else {
            etConfirmPassword.setError(null);
        }

        if (strPassword.isEmpty()) {
            etPassword.setError("Please enter a valid password");
            valid = false;
        } else {
            etPassword.setError(null);
        }
        return valid;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMAGE && null != data) {
            Uri selectedImageUri = data.getData();
            String imagepath = getPath(selectedImageUri);
            sourceFile = new File(imagepath);
            try {
                sourceFile = new Compressor(getActivity()).compressToFile(sourceFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else if (resultCode == RESULT_OK && requestCode == CAMERA_CAPTURE && data != null) {
            Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();

            thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
            sourceFile = new File(Environment.getExternalStorageDirectory(),
                    System.currentTimeMillis() + ".jpg");
            FileOutputStream fo;
            try {
                sourceFile.createNewFile();
                sourceFile = new Compressor(getActivity()).compressToFile(sourceFile);
                fo = new FileOutputStream(sourceFile);
                fo.write(bytes.toByteArray());
                fo.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            ivProfile.setImageBitmap(thumbnail);

        }
    }

    @SuppressLint("SetTextI18n")
    public String getPath(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = getActivity().getContentResolver().query(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        int columnIndex = cursor.getColumnIndex(projection[0]);
        String filePath = cursor.getString(columnIndex);
        ivProfile.setImageBitmap(BitmapFactory.decodeFile(filePath));
        return cursor.getString(column_index);

    }
}

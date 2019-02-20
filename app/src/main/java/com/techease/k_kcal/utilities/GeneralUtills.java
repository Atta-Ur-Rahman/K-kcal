package com.techease.k_kcal.utilities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.techease.k_kcal.R;

import java.util.List;

public class GeneralUtills {


    public static SharedPreferences sharedPreferences;
    public static SharedPreferences.Editor editor;
    public static ImageView imageView;
    public static LinearLayout linearLayout;
    public static TextView textView;


    public static Fragment connectFragment(Context context, Fragment fragment) {
        ((AppCompatActivity) context).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).addToBackStack("true").commit();
        return fragment;
    }

    public static Fragment withOutBackStackConnectFragment(Context context, Fragment fragment) {
        ((AppCompatActivity) context).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
        return fragment;
    }

    public static SharedPreferences.Editor putStringValueInEditor(Context context, String key, String value) {
        sharedPreferences = getSharedPreferences(context);
        editor = sharedPreferences.edit();
        editor.putString(key, value).commit();
        return editor;
    }

    public static SharedPreferences.Editor putIntegerValueInEditor(Context context, String key, int value) {
        sharedPreferences = getSharedPreferences(context);
        editor = sharedPreferences.edit();
        editor.putInt(key, value).commit();
        return editor;
    }

    public static SharedPreferences.Editor putBooleanValueInEditor(Context context, String key, boolean value) {
        sharedPreferences = getSharedPreferences(context);
        editor = sharedPreferences.edit();
        editor.putBoolean(key, value).commit();
        return editor;
    }



    public static SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences(Config.MY_PREF, 0);
    }

    public static String getApiToken(Context context){
        return getSharedPreferences(context).getString("api_token","");
    }

    public static String getForgotEmail(Context context){
        return getSharedPreferences(context).getString("forgot_email","");
    }

    public static String getLat(Context context){
        return getSharedPreferences(context).getString("latitude","");
    }

    public static String getLng(Context context){
        return getSharedPreferences(context).getString("longitude","");
    }

    public static String getName(Context context){
        return getSharedPreferences(context).getString("name","");
    }

    public static String getEmail(Context context){
        return getSharedPreferences(context).getString("email","");
    }

    public static boolean isLogin(Context context){
        return getSharedPreferences(context).getBoolean("isLogin",false);
    }

    public static void grantPermission(Activity  context){
        Dexter.withActivity(context)
                .withPermissions(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                ).withListener(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport report) {

            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {

            }

        }).check();
    }


}

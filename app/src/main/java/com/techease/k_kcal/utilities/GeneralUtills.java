package com.techease.k_kcal.utilities;

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

import com.techease.k_kcal.R;
public class GeneralUtills {


    public static SharedPreferences sharedPreferences;
    public static SharedPreferences.Editor editor;
    public static ImageView imageView;
    public static LinearLayout linearLayout;
    public static TextView textView;

    public static SharedPreferences.Editor putValueInEditor(Context context) {
        sharedPreferences = getSharedPreferences(context);
        editor = sharedPreferences.edit();
        return editor;
    }

    public static SharedPreferences getSharedPreferences(Context context) {
        //sharedPreferences = context.getSharedPreferences(Configuration.MY_PREF, 0);
        return context.getSharedPreferences(Configurations.MY_PREF, 0);
    }

    private static class Configurations {
        public static final String MY_PREF = null;
    }


    public static Fragment connectFragment(Context context, Fragment fragment) {
        ((AppCompatActivity) context).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).addToBackStack("true").commit();
        return fragment;
    }

    public static Fragment withOutBackStackConnectFragment(Context context, Fragment fragment) {
        ((AppCompatActivity) context).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
        return fragment;
    }


    public static ImageView imageViewDeclaration(int buttonID, View view) {
        imageView = view.findViewById(buttonID);
        return imageView;
    }

    public static TextView textViewDeclaration(int textViewID, View view) {
        textView = view.findViewById(textViewID);
        return textView;
    }

    public static LinearLayout linearLayoutDeclaration(int linearLayoutID, View view) {
        linearLayout = view.findViewById(linearLayoutID);
        return linearLayout;
    }

}

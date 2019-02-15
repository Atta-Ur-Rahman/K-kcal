package com.techease.k_kcal.models.logindatamodels;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LoginDataModel {
    @SerializedName("user")
    @Expose
    private LoginDetailModel user;

    public LoginDetailModel getUser() {
        return user;
    }

    public void setUser(LoginDetailModel user) {
        this.user = user;
    }
}

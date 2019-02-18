package com.techease.k_kcal.models.forgotDataModels;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class VerifyUserModel {
    @SerializedName("user")
    @Expose
    private VerifyUserDetailModel user;

    public VerifyUserDetailModel getUser() {
        return user;
    }

    public void setUser(VerifyUserDetailModel user) {
        this.user = user;
    }
}

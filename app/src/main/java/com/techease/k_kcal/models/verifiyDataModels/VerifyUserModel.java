package com.techease.k_kcal.models.verifiyDataModels;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class VerifyUserModel {
    @SerializedName("user")
    @Expose
    private VerifyDetailModel user;

    public VerifyDetailModel getUser() {
        return user;
    }

    public void setUser(VerifyDetailModel user) {
        this.user = user;
    }
}

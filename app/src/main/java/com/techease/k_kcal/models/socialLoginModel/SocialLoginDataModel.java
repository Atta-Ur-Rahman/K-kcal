package com.techease.k_kcal.models.socialLoginModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SocialLoginDataModel {
    @SerializedName("user")
    @Expose
    private SocialLoginUserModel user;

    public SocialLoginUserModel getUser() {
        return user;
    }

    public void setUser(SocialLoginUserModel user) {
        this.user = user;
    }
}

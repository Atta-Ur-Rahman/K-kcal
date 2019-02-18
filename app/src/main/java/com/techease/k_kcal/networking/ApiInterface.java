package com.techease.k_kcal.networking;

import com.techease.k_kcal.models.forgotDataModels.ChangePasswordModel;
import com.techease.k_kcal.models.forgotDataModels.ForgotPasswordModel;
import com.techease.k_kcal.models.forgotDataModels.VerifyCodeResponseModel;
import com.techease.k_kcal.models.itemDataModels.ItemResponseModel;
import com.techease.k_kcal.models.logindatamodels.LoginResponseModel;
import com.techease.k_kcal.models.resendCodeDataModel.ResendCodeModel;
import com.techease.k_kcal.models.signupdatamodels.SignUpResponseModel;
import com.techease.k_kcal.models.verifiyDataModels.VerifyResponseModel;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

/**
 * Created by eapple on 29/08/2018.
 */

public interface ApiInterface {

    @FormUrlEncoded
    @POST("login")
    Call<LoginResponseModel> userLogin(@Field("email") String email,
                                       @Field("password") String password,
                                       @Field("latitude") String latitude,
                                       @Field("longitude") String longitude);

    @Multipart
    @POST("register")
    Call<SignUpResponseModel> userSignUp(@Part("name") RequestBody name,
                                         @Part("email") RequestBody email,
                                         @Part("password") RequestBody password,
                                         @Part("confirmPassword") RequestBody passwordConfirmation,
                                         @Part("phoneNumber") RequestBody phoneNumber,
                                         @Part("deviceType") RequestBody deviceType,
                                         @Part("latitude") RequestBody lat,
                                         @Part("longitude") RequestBody lon,
                                         @Part MultipartBody.Part photo,
                                         @Part("profilePicture") RequestBody fileName);

    @FormUrlEncoded
    @POST("verifyCode")
    Call<VerifyResponseModel> userVerification(@Field("code") String code);

    @POST("sendCode")
    Call<ResendCodeModel> resendCode();

    @GET("getItems")
    Call<ItemResponseModel> getItems();


    @FormUrlEncoded
    @POST("resetPassword")
    Call<ForgotPasswordModel> resetPassword(@Field("email") String email);

    @FormUrlEncoded
    @POST("resetPasswordVerify")
    Call<VerifyCodeResponseModel> verifyPasswordCode(@Field("code") String code,
                                                     @Field("email") String email);

    @FormUrlEncoded
    @POST("changePassword")
    Call<ChangePasswordModel> changePassword(@Field("newPassword") String code,
                                             @Field("confirmPassword") String email);

}

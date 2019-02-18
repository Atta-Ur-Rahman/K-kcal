package com.techease.k_kcal.models.forgotDataModels;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class VerifyUserDetailModel {
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("profilePicture")
    @Expose
    private String profilePicture;
    @SerializedName("phoneNumber")
    @Expose
    private String phoneNumber;
    @SerializedName("gender")
    @Expose
    private Object gender;
    @SerializedName("age")
    @Expose
    private Object age;
    @SerializedName("height")
    @Expose
    private Object height;
    @SerializedName("weight")
    @Expose
    private Object weight;
    @SerializedName("profession")
    @Expose
    private Object profession;
    @SerializedName("lifeStyle")
    @Expose
    private Object lifeStyle;
    @SerializedName("travel")
    @Expose
    private Object travel;
    @SerializedName("deviceType")
    @Expose
    private String deviceType;
    @SerializedName("deviceToken")
    @Expose
    private Object deviceToken;
    @SerializedName("latitude")
    @Expose
    private String latitude;
    @SerializedName("longitude")
    @Expose
    private String longitude;
    @SerializedName("accountActivationStatus")
    @Expose
    private String accountActivationStatus;
    @SerializedName("activationCode")
    @Expose
    private String activationCode;
    @SerializedName("activationCodeUpdatedAt")
    @Expose
    private String activationCodeUpdatedAt;
    @SerializedName("accountStatus")
    @Expose
    private String accountStatus;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;
    @SerializedName("notificationStatus")
    @Expose
    private String notificationStatus;
    @SerializedName("token")
    @Expose
    private String token;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Object getGender() {
        return gender;
    }

    public void setGender(Object gender) {
        this.gender = gender;
    }

    public Object getAge() {
        return age;
    }

    public void setAge(Object age) {
        this.age = age;
    }

    public Object getHeight() {
        return height;
    }

    public void setHeight(Object height) {
        this.height = height;
    }

    public Object getWeight() {
        return weight;
    }

    public void setWeight(Object weight) {
        this.weight = weight;
    }

    public Object getProfession() {
        return profession;
    }

    public void setProfession(Object profession) {
        this.profession = profession;
    }

    public Object getLifeStyle() {
        return lifeStyle;
    }

    public void setLifeStyle(Object lifeStyle) {
        this.lifeStyle = lifeStyle;
    }

    public Object getTravel() {
        return travel;
    }

    public void setTravel(Object travel) {
        this.travel = travel;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public Object getDeviceToken() {
        return deviceToken;
    }

    public void setDeviceToken(Object deviceToken) {
        this.deviceToken = deviceToken;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getAccountActivationStatus() {
        return accountActivationStatus;
    }

    public void setAccountActivationStatus(String accountActivationStatus) {
        this.accountActivationStatus = accountActivationStatus;
    }

    public String getActivationCode() {
        return activationCode;
    }

    public void setActivationCode(String activationCode) {
        this.activationCode = activationCode;
    }

    public String getActivationCodeUpdatedAt() {
        return activationCodeUpdatedAt;
    }

    public void setActivationCodeUpdatedAt(String activationCodeUpdatedAt) {
        this.activationCodeUpdatedAt = activationCodeUpdatedAt;
    }

    public String getAccountStatus() {
        return accountStatus;
    }

    public void setAccountStatus(String accountStatus) {
        this.accountStatus = accountStatus;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getNotificationStatus() {
        return notificationStatus;
    }

    public void setNotificationStatus(String notificationStatus) {
        this.notificationStatus = notificationStatus;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}

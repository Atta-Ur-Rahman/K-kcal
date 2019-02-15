package com.techease.k_kcal.models.itemDataModels;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PivotModel {
    @SerializedName("item_id")
    @Expose
    private String itemId;
    @SerializedName("restaurant_id")
    @Expose
    private String restaurantId;

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(String restaurantId) {
        this.restaurantId = restaurantId;
    }
}

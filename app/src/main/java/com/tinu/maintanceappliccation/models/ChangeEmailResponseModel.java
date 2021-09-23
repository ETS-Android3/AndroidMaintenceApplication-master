package com.tinu.maintanceappliccation.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ChangeEmailResponseModel {

    @SerializedName("success")
    @Expose
    public boolean success;

    @SerializedName("data")
    @Expose
    public boolean data;

    @SerializedName("message")
    @Expose
    public String message;

    public boolean isSuccess() {
        return success;
    }

    public boolean isData() {
        return data;
    }

    public String getMessage() {
        return message;
    }
}

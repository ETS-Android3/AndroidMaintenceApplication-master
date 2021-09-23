package com.tinu.maintanceappliccation.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.security.SecureRandom;

public class UpdateTaskResponseModel {
    @SerializedName("success")
    @Expose
    String success;
    @SerializedName("data")
    @Expose
    String data;
    @SerializedName("message")
    @Expose
    String message;

    public String getSuccess() {
        return success;
    }

    public String getData() {
        return data;
    }

    public String getMessage() {
        return message;
    }
}

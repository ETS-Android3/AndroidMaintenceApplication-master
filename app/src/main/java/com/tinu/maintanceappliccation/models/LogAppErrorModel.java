package com.tinu.maintanceappliccation.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LogAppErrorModel {
    @SerializedName("success")
    @Expose
    public Boolean success;
    @SerializedName("data")
    @Expose
    public boolean  data;
    @SerializedName("message")
    @Expose
    public String message;

    public boolean isData() {
        return data;
    }

    public Boolean getSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }
}

package com.tinu.maintanceappliccation.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SampleScan {

    @SerializedName("success")
    @Expose
    private boolean success;

    @SerializedName("message")
    @Expose
    private String message;

    /* @SerializedName("data")
     @Expose
     private Data data;
 */


    public boolean isSuccess() {
        return success;
    }

  /*  public Data getData() {
        return data;
    }*/

    public String getMessage() {
        return message;
    }
}

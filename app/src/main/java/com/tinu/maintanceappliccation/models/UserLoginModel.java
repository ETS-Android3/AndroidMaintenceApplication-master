package com.tinu.maintanceappliccation.models;

import android.provider.ContactsContract;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserLoginModel {
    @SerializedName("success")
    @Expose
    boolean  success;

    @SerializedName("message")
    @Expose
    String  message;

    @SerializedName("data")
    @Expose
    DataUserModel data;

    public class DataUserModel
    {
        @SerializedName("token_type")
        @Expose
        String token_type;

        @SerializedName("expires_in")
        @Expose
        Integer expires_in;

        @SerializedName("access_token")
        @Expose
        String  access_token;

        @SerializedName("refresh_token")
        @Expose
        String refresh_token;

        public String getToken_type() {
            return token_type;
        }

        public Integer getExpires_in() {
            return expires_in;
        }

        public String getAccess_token() {
            return access_token;
        }

        public String getRefresh_token() {
            return refresh_token;
        }
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public DataUserModel getData() {
        return data;
    }
}

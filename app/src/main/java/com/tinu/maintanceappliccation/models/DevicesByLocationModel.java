package com.tinu.maintanceappliccation.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import androidx.annotation.StringRes;

public class DevicesByLocationModel {

    @SerializedName("success")
    @Expose
    public boolean success;
    @SerializedName("data")
    @Expose
    public Data data;
    public class  Data {

        @SerializedName("free_devices")
        @Expose
        public List<FreeDevices> free_devices;

        public List<FreeDevices> getFree_devices() {
            return free_devices;
        }
    }

    public boolean isSuccess() {
        return success;
    }

    public Data getData() {
        return data;
    }

    public class FreeDevices{

        @SerializedName("landmark")
        @Expose
        private String landmark;


        @SerializedName("device_name")
        @Expose
        public String device_name;

        @SerializedName("device_id")
        @Expose
        public String device_id;

        @SerializedName("latitude")
        @Expose
        public String latitude;

        @SerializedName("longitude")
        @Expose
        public String longitude;

        @SerializedName("location")
        @Expose
        public String location;

        @SerializedName("address_1")
        @Expose
        public String address_1;

        public String getAddress_1() {
            return address_1;
        }

        public String getDevice_name() {
            return device_name;
        }

        public String getDevice_id() {
            return device_id;
        }

        public String getLatitude() {
            return latitude;
        }

        public String getLongitude() {
            return longitude;
        }

        public String getLocation() {
            return location;
        }


        public String getLandmark() {
            return landmark;
        }

        public void setLandmark(String landmark) {
            this.landmark = landmark;
        }

    }

}

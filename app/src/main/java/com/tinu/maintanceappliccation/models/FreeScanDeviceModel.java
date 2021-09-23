package com.tinu.maintanceappliccation.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class FreeScanDeviceModel {
    @SerializedName("success")
    @Expose
    private boolean success;

    @SerializedName("message")
    @Expose
    private String message ="";

    @SerializedName("data")
    @Expose
    private Data data;
    public class Data{
        @SerializedName("id")
        @Expose
        private Integer id;

        @SerializedName("device_name")
        @Expose
        private String device_name;

        @SerializedName("device_id")
        @Expose
        private String device_id;

        @SerializedName("latitude")
        @Expose
        private String latitude;

        @SerializedName("longitude")
        @Expose
        private String longitude;

        @SerializedName("location")
        @Expose
        private String location;

        @SerializedName("address_1")
        @Expose
        private String address_1;
        @SerializedName("address_2")
        @Expose
        private String address_2;

        @SerializedName("landmark")
        @Expose
        private String landmark;

        @SerializedName("pincode")
        @Expose
        private String pincode;

        @SerializedName("status")
        @Expose
        private String status;

        @SerializedName("present_status")
        @Expose
        private String present_status;

        @SerializedName("contract_user")
        @Expose
        private String contract_user;
        @SerializedName("maintenance_company")
        @Expose
        private String maintenance_company;
        @SerializedName("maintenance_person")
        @Expose
        private String maintenance_person;
        @SerializedName("lock_process_status")
        @Expose
        private String lock_process_status;
        @SerializedName("initial_payment_status")
        @Expose
        private String initial_payment_status;
        @SerializedName("current_user")
        @Expose
        private int current_user;

        @SerializedName("close_hash")
        @Expose
        private String close_hash;

        @SerializedName("open_hash")
        @Expose
        private String open_hash;

        @SerializedName("user_email")
        @Expose
        private String user_email;

        @SerializedName("maintenance_type")
        @Expose
        private String  maintenance_type;


        @SerializedName("battery_level")
        @Expose
        public String battery_level;


        public String getClose_hash() {
            return close_hash;
        }

        public String getOpen_hash() {
            return open_hash;
        }

        public String getMaintenance_type() {
            return maintenance_type;
        }

        public Integer getId() {
            return id;
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

        public String getAddress_1() {
            return address_1;
        }

        public String getLandmark() {
            return landmark;
        }

        public String getPincode() {
            return pincode;
        }

        public String getStatus() {
            return status;
        }

        public String getPresent_status() {
            return present_status;
        }

        public String getContract_user() {
            return contract_user;
        }

        public String getMaintenance_company() {
            return maintenance_company;
        }

        public String getMaintenance_person() {
            return maintenance_person;
        }

        public String getLock_process_status() {
            return lock_process_status;
        }

        public String getInitial_payment_status() {
            return initial_payment_status;
        }

        public int getCurrent_user() {
            return current_user;
        }

        public String getAddress_2() {
            return address_2;
        }


        public String getUser_email() {
            return user_email;
        }

        public String getBattery_level() {
            return battery_level;
        }
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public Data getData() {
        return data;
    }
}

package com.tinu.maintanceappliccation.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class NarestLocationsModel {

    @SerializedName("success")
    @Expose
    private boolean success;
    @SerializedName("data")
    @Expose
    public List<Data> dataList;
    public class  Data {

        @SerializedName("location_id")
        @Expose
        private int location_id;


        @SerializedName("location")
        @Expose
        private String  location;

        @SerializedName("landmark")
        @Expose
        private String landmark;


        public String getLandmark() {
            return landmark;
        }

        public void setLandmark(String landmark) {
            this.landmark = landmark;
        }


        public int getLocation_id() {
            return location_id;
        }

        public String getLocation() {
            return location;
        }
    }

    public boolean isSuccess() {
        return success;
    }

    public List<Data> getDataList() {
        return dataList;
    }
}

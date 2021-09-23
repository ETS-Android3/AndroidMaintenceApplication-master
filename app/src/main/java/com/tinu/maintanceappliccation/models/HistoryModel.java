package com.tinu.maintanceappliccation.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class HistoryModel {

    @SerializedName("success")
    @Expose
    String success;
    @SerializedName("data")
    @Expose
    List<Data> data;
    @SerializedName("message")
    @Expose
    String message;

    public String getSuccess() {
        return success;
    }

    public List<Data> getData() {
        return data;
    }

    public String getMessage() {
        return message;
    }


    public class Data{
        @SerializedName("device_id")
        @Expose
        String device_id="";

        @SerializedName("type")
        @Expose
        String type="";


        @SerializedName("location_address")
        @Expose
        String location_address="";

        @SerializedName("status")
        @Expose
        String status="";

        @SerializedName("device_status")
        @Expose
        String device_status="";

        @SerializedName("battery_level")
        @Expose
        String battery_level="";

        @SerializedName("task_details")
        @Expose
        String task_details;

        @SerializedName("work_details")
        @Expose
        String work_details;

        @SerializedName("maintenance_person_name")
        @Expose
        String maintenance_person_name="";

        @SerializedName("updated_at")
        @Expose
        String  updated_at="";

        @SerializedName("device_name")
        @Expose
        String device_name;

        public String getDevice_id() {
            return device_id;
        }

        public String getType() {
            return type;
        }

        public String getLocation_address() {
            return location_address;
        }

        public String getStatus() {
            return status;
        }

        public String getDevice_status() {
            return device_status;
        }

        public String getBattery_level() {
            return battery_level;
        }

        public String getTask_details() {
            return task_details;
        }

        public String getWork_details() {
            return work_details;
        }

        public String getMaintenance_person_name() {
            return maintenance_person_name;
        }

        public String getUpdated_at() {
            return updated_at;
        }

        public String getDevice_name() {
            return device_name;
        }
    }
}

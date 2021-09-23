package com.tinu.maintanceappliccation.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TaskListModel {

    @SerializedName("success")
    @Expose
    private boolean success;

    @SerializedName("data")
    @Expose
    public List<Data> data;

    @SerializedName("message")
    @Expose
    public String message;


    public   class Data {

        @SerializedName("id")
        @Expose
        private int id;

        @SerializedName("code")
        @Expose
        private String  code;

        @SerializedName("device_id")
        @Expose
        private String  device_id;

        @SerializedName("maintenance_company")
        @Expose
        private int  maintenance_company;

        @SerializedName("maintenance_person")
        @Expose
        private int  maintenance_person;

        @SerializedName("location_id")
        @Expose
        private int  location_id;

        @SerializedName("type")
        @Expose
        private String   type;

        @SerializedName("status")
        @Expose
        private String status;


        @SerializedName("device_status")
        @Expose
        private String device_status;


        @SerializedName("task_details")
        @Expose
        private String task_details;


        @SerializedName("work_details")
        @Expose
        private String work_details;

        @SerializedName("start_time")
        @Expose
        private String start_time;

        @SerializedName("end_time")
        @Expose
        private String end_time;




        @SerializedName("initial_payment_status")
        @Expose
        private String initial_payment_status;





        @SerializedName("latitude")
        @Expose
        private String latitude;

        @SerializedName("longitude")
        @Expose
        private String longitude;


        @SerializedName("landmark")
        @Expose
        private String landmark;


        @SerializedName("created_at")
        @Expose
        private String created_at;

        @SerializedName("updated_at")
        @Expose
        private String updated_at;

        @SerializedName("device_name")
        @Expose
        private String device_name;

        @SerializedName("maintenance_person_name")
        @Expose
        private String maintenance_person_name;

        @SerializedName("location_address")
        @Expose
        private String location_address;


        public int getId() {
            return id;
        }

        public String getCode() {
            return code;
        }

        public String getDevice_id() {
            return device_id;
        }

        public int getMaintenance_company() {
            return maintenance_company;
        }

        public int getMaintenance_person() {
            return maintenance_person;
        }

        public int getLocation_id() {
            return location_id;
        }

        public String getType() {
            return type;
        }

        public String getStatus() {
            return status;
        }

        public String getDevice_status() {
            return device_status;
        }

        public String getTask_details() {
            return task_details;
        }

        public String getWork_details() {
            return work_details;
        }

        public String getStart_time() {
            return start_time;
        }

        public String getEnd_time() {
            return end_time;
        }

        public String getLatitude() {
            return latitude;
        }

        public String getLongitude() {
            return longitude;
        }

        public String getLandmark() {
            return landmark;
        }

        public String getCreated_at() {
            return created_at;
        }

        public String getUpdated_at() {
            return updated_at;
        }

        public String getDevice_name() {
            return device_name;
        }

        public String getMaintenance_person_name() {
            return maintenance_person_name;
        }

        public String getLocation_address() {
            return location_address;
        }

        public String getInitial_payment_status() {
            return initial_payment_status;
        }
    }

    public boolean isSuccess() {
        return success;
    }

    public List<Data> getData() {
        return data;
    }

    public String getMessage() {
        return message;
    }


}

package com.tinu.maintanceappliccation.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import androidx.annotation.StringRes;

public class scanDeviceModel {

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

        @SerializedName("maintenance_task")
        @Expose
        public MainteanceTask mainteanceTask;

        @SerializedName("battery_level")
        @Expose
        public String battery_level;

        @SerializedName("maintenance_documents")
        @Expose
        public List<maintenanceDocumentsImages>  maintenanceDocumentsImages;

        @SerializedName("initial_charges")
        @Expose
        public String initial_charges;



        public String getInitial_charges() {
            return initial_charges;
        }

        public void setInitial_charges(String initial_charges) {
            this.initial_charges = initial_charges;
        }



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

        public MainteanceTask getMainteanceTask() {
            return mainteanceTask;
        }

        public List<scanDeviceModel.maintenanceDocumentsImages> getMaintenanceDocumentsImages() {
            return maintenanceDocumentsImages;
        }

        public String getUser_email() {
            return user_email;
        }

        public String getBattery_level() {
            return battery_level;
        }
    }
    public  class  MainteanceTask
    {
        @SerializedName("code")
        @Expose
        public String code;

        @SerializedName("device_id")
        @Expose
        public String device_id;

        @SerializedName("location_address")
        @Expose
        public String location_address;
        @SerializedName("type")
        @Expose
        public String type;
        @SerializedName("status")
        @Expose
        public String status;
        @SerializedName("device_status")
        @Expose
        public String device_status;

        @SerializedName("battery_level")
        @Expose
        public String battery_level;

        @SerializedName("task_details")
        @Expose
        public String task_details;

        @SerializedName("work_details")
        @Expose
        public String work_details;

        @SerializedName("start_time")
        @Expose
        public String  start_time;

        @SerializedName("end_time")
        @Expose
        public String end_time;


        @SerializedName("latitude")
        @Expose
        public String latitude;

        @SerializedName("longitude")
        @Expose
        public String longitude;

        @SerializedName("landmark")
        @Expose
        public String landmark;

        @SerializedName("id")
        @Expose
        public int id;

        @SerializedName("maintenance_person_name")
        @Expose
        public String maintenance_person_name;


        @SerializedName("install")
        @Expose
        public String install;


        @SerializedName("regular_maintenance")
        @Expose
        public String regular_maintenance;


        @SerializedName("battery_replacement")
        @Expose
        public String battery_replacement;

        @SerializedName("device_replacement")
        @Expose
        public String device_replacement;

        @SerializedName("repair")
        @Expose
        public String repair ;

        public String getRepair() {
            return repair;
        }

        public void setRepair(String repair) {
            this.repair = repair;
        }

        public String getCode() {
            return code;
        }

        public String getDevice_id() {
            return device_id;
        }

        public String getLocation_address() {
            return location_address;
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

        public String getBattery_level() {
            return battery_level;
        }

        public String getTask_details() {
            return task_details;
        }

        public String getWork_details() {
            return work_details;
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

        public int getId() {
            return id;
        }

        public String getStart_time() {
            return start_time;
        }

        public String getEnd_time() {
            return end_time;
        }

        public String getMaintenance_person_name() {
            return maintenance_person_name;
        }

        public String getInstall() {
            return install;
        }

        public String getRegular_maintenance() {
            return regular_maintenance;
        }

        public String getBattery_replacement() {
            return battery_replacement;
        }

        public String getDevice_replacement() {
            return device_replacement;
        }
    }
    public class maintenanceDocumentsImages
    {
        @SerializedName("id")
        @Expose
        public int id;

        @SerializedName("task_id")
        @Expose
        public int task_id;

        @SerializedName("file_name")
        @Expose
        public String  file_name;

        @SerializedName("file")
        @Expose
        public String imagePath;

        @SerializedName("file_extension")
        @Expose
        public String file_extension;

        public int getId() {
            return id;
        }

        public int getTask_id() {
            return task_id;
        }

        public String getFile_name() {
            return file_name;
        }

        public String getImagePath() {
            return imagePath;
        }

        public String getFile_extension() {
            return file_extension;
        }
    }

    public boolean isSuccess() {
        return success;
    }

    public Data getData() {
        return data;
    }

    public String getMessage() {
        return message;
    }
}

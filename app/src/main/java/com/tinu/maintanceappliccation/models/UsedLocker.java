
package com.tinu.maintanceappliccation.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UsedLocker {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("device_id")
    @Expose
    private String deviceId;
    @SerializedName("latitude")
    @Expose
    private String latitude;
    @SerializedName("longitude")
    @Expose
    private String longitude;
    @SerializedName("location")
    @Expose
    private String location;
    @SerializedName("initial_charges")
    @Expose
    private Integer initialCharges;
    @SerializedName("usage_hour")
    @Expose
    private Integer usageHour;
    @SerializedName("rate_per_hour")
    @Expose
    private Integer ratePerHour;
    @SerializedName("present_status")
    @Expose
    private String presentStatus;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("qr_code")
    @Expose
    private String qrCode;
    @SerializedName("lock_code")
    @Expose
    private Object lockCode;
    @SerializedName("unlock_code")
    @Expose
    private Object unlockCode;
    @SerializedName("created_at")
    @Expose
    private Object createdAt;
    @SerializedName("updated_at")
    @Expose
    private Object updatedAt;

    //later added
    @SerializedName("device_name")
    @Expose
    private String deviceName;
    @SerializedName("characteristicUUID")
    @Expose
    private String characteristicUUID;
    @SerializedName("serviceUUID")
    @Expose
    private String serviceUUID;

    @SerializedName("start_time")
    @Expose
    private String start_time;

    @SerializedName("end_time")
    @Expose
    private String end_time;

    @SerializedName("initial_payment_status")
    @Expose
    private String initial_payment_status;

    @SerializedName("final_payment")
    @Expose
    private boolean final_payment;





    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Integer getInitialCharges() {
        return initialCharges;
    }

    public void setInitialCharges(Integer initialCharges) {
        this.initialCharges = initialCharges;
    }

    public Integer getUsageHour() {
        return usageHour;
    }

    public void setUsageHour(Integer usageHour) {
        this.usageHour = usageHour;
    }

    public Integer getRatePerHour() {
        return ratePerHour;
    }

    public void setRatePerHour(Integer ratePerHour) {
        this.ratePerHour = ratePerHour;
    }

    public String getPresentStatus() {
        return presentStatus;
    }

    public void setPresentStatus(String presentStatus) {
        this.presentStatus = presentStatus;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getQrCode() {
        return qrCode;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }

    public Object getLockCode() {
        return lockCode;
    }

    public void setLockCode(Object lockCode) {
        this.lockCode = lockCode;
    }

    public Object getUnlockCode() {
        return unlockCode;
    }

    public void setUnlockCode(Object unlockCode) {
        this.unlockCode = unlockCode;
    }

    public Object getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Object createdAt) {
        this.createdAt = createdAt;
    }

    public Object getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Object updatedAt) {
        this.updatedAt = updatedAt;
    }

    //later added
    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getCharacteristicUUID() {
        return characteristicUUID;
    }

    public void setCharacteristicUUID(String characteristicUUID) {
        this.characteristicUUID = characteristicUUID;
    }

    public String getServiceUUID() {
        return serviceUUID;
    }

    public void setServiceUUID(String serviceUUID) {
        this.serviceUUID = serviceUUID;
    }

    public String getStart_time() {
        return start_time;
    }

    public String getEnd_time() {
        return end_time;
    }

    public String getInitial_payment_status() {
        return initial_payment_status;
    }

    public boolean getFinal_payment() {
        return final_payment;
    }
}

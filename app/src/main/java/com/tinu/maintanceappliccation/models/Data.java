
package com.tinu.maintanceappliccation.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Data {

    @SerializedName("userId")
    @Expose
    private Integer userId;

    //for login
    @SerializedName("token_type")
    @Expose
    private String tokenType;
    @SerializedName("expires_in")
    @Expose
    private Integer expiresIn;
    @SerializedName("access_token")
    @Expose
    private String accessToken;
    @SerializedName("refresh_token")
    @Expose
    private String refreshToken;

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    //for lockerDetails
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("device_id")
    @Expose
    private String deviceId;

    @SerializedName("device_name")
    @Expose
    private String deviceName;

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
    //added later
    @SerializedName("characteristicUUID")
    @Expose
    private String characteristicUUID;
    @SerializedName("serviceUUID")
    @Expose
    private String serviceUUID;
    @SerializedName("lock_info")
    @Expose
    private String lockInfo;
    @SerializedName("success_info")
    @Expose
    private String successInfo;
    @SerializedName("unlock_info")
    @Expose
    private String unlockInfo;
    @SerializedName("payment_info")
    @Expose
    private String paymentInfo;
    @SerializedName("max_time")
    @Expose
    private String maxTime;
    //sec
    @SerializedName("open_hash")
    @Expose
    private String open_hash;
    @SerializedName("random_table")
    @Expose
    private String random_table;
    @SerializedName("close_hash")
    @Expose
    private String close_hash;

    @SerializedName("start_time")
    @Expose
    private String start_time;
    @SerializedName("end_time")
    @Expose
    private String end_time;

    //for user details
    @SerializedName("success")
    @Expose
    private Boolean success;
    @SerializedName("user")
    @Expose
    private User user;
    @SerializedName("message")
    @Expose
    private String message;


    //for payment
    @SerializedName("token")
    @Expose
    private String token;
    @SerializedName("partner_payment_id")
    @Expose
    private String partnerPaymentId;
    @SerializedName("amount")
    @Expose
    private Integer amount;
    @SerializedName("skus")
    @Expose
    private List<Sku> skus = null;
    @SerializedName("redirection_url")
    @Expose
    private String redirectionUrl;
    @SerializedName("callback_url")
    @Expose
    private String callbackUrl;


    //payment confirmation
    @SerializedName("payment")
    @Expose
    private Boolean payment;

    @SerializedName("payment_require")
    @Expose
    private Boolean payment_require;

    /*//for lock_img API
    @SerializedName("lock_code")
    @Expose
    private String lock_Code;
    @SerializedName("device_name")
    @Expose
    private String device_Name;
    @SerializedName("serviceUUID")
    @Expose
    private String serviceUUID;
    @SerializedName("characteristicUUID")
    @Expose
    private String characteristicUUID;*/

    /*//for unlock API
    @SerializedName("start_date")
    @Expose
    private String startDate;
    @SerializedName("end_date")
    @Expose
    private String endDate;
    @SerializedName("duration")
    @Expose
    private Integer duration;
    *//*@SerializedName("amount")
    @Expose
    private Integer amount;*/

    //for unlockRequest API
    @SerializedName("start_date")
    @Expose
    private String startDate;
    @SerializedName("end_date")
    @Expose
    private String endDate;
    @SerializedName("duration")
    @Expose
    private Integer duration;



    //success Api Response
    @SerializedName("first_name")
    @Expose
    private String firstName;
    @SerializedName("last_name")
    @Expose
    private Object lastName;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("mac_id")
    @Expose
    private String macId;
    @SerializedName("dob")
    @Expose
    private String dob;
    @SerializedName("mobile")
    @Expose
    private String mobile;
    @SerializedName("pin")
    @Expose
    private Integer pin;
    @SerializedName("password")
    @Expose
    private String password;
    @SerializedName("pin_verified")
    @Expose
    private String pinVerified;
    @SerializedName("user_type")
    @Expose
    private Integer userType;
    @SerializedName("phone_code")
    @Expose
    private Object phoneCode;
    @SerializedName("country")
    @Expose
    private String country;
    @SerializedName("email_verified_at")
    @Expose
    private Object emailVerifiedAt;
    @SerializedName("used_lockers")
    @Expose
    private List<UsedLocker> usedLockers = null; //
    @SerializedName("overtime")
    @Expose
    private Integer overtime;
    //for login
    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public Integer getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(Integer expiresIn) {
        this.expiresIn = expiresIn;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    //for lockerDetails


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

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    //later added

    public String getLockInfo() {
        return lockInfo;
    }

    public void setLockInfo(String lockInfo) {
        this.lockInfo = lockInfo;
    }

    public String getUnlockInfo() {
        return unlockInfo;
    }

    public void setUnlockInfo(String unlockInfo) {
        this.unlockInfo = unlockInfo;
    }

    public String getPaymentInfo() {
        return paymentInfo;
    }

    public void setPaymentInfo(String paymentInfo) {
        this.paymentInfo = paymentInfo;
    }

    public String getSuccessInfo() {
        return successInfo;
    }

    public void setSuccessInfo(String successInfo) {
        this.successInfo = successInfo;
    }

    public String getMaxTime() {
        return maxTime;
    }

    public void setMaxTime(String maxTime) {
        this.maxTime = maxTime;
    }

    //for userDetails


    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }


    //for payment

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getPartnerPaymentId() {
        return partnerPaymentId;
    }

    public void setPartnerPaymentId(String partnerPaymentId) {
        this.partnerPaymentId = partnerPaymentId;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public List<Sku> getSkus() {
        return skus;
    }

    public void setSkus(List<Sku> skus) {
        this.skus = skus;
    }

    public String getRedirectionUrl() {
        return redirectionUrl;
    }

    public void setRedirectionUrl(String redirectionUrl) {
        this.redirectionUrl = redirectionUrl;
    }

    public String getCallbackUrl() {
        return callbackUrl;
    }

    public void setCallbackUrl(String callbackUrl) {
        this.callbackUrl = callbackUrl;
    }

    //payment confirmation

    public Boolean getPayment() {
        return payment;
    }

    public void setPayment(Boolean payment) {
        this.payment = payment;
    }

    //for lock_img API

    /*public String getLock_Code() {
        return lock_Code;
    }

    public void setLock_Code(String lock_Code) {
        this.lock_Code = lock_Code;
    }

    public String getDevice_Name() {
        return device_Name;
    }

    public void setDevice_Name(String device_Name) {
        this.device_Name = device_Name;
    }

    public String getServiceUUID() {
        return serviceUUID;
    }

    public void setServiceUUID(String serviceUUID) {
        this.serviceUUID = serviceUUID;
    }

    public String getCharacteristicUUID() {
        return characteristicUUID;
    }

    public void setCharacteristicUUID(String characteristicUUID) {
        this.characteristicUUID = characteristicUUID;
    }*/

    //for unlockRequest API

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public Boolean getPayment_require() {
        return payment_require;
    }
//for success Api Response
    //----------


    public String getServiceUUID() {
        return serviceUUID;
    }

    public String getOpen_hash() {
        return open_hash;
    }

    public String getRandom_table() {
        return random_table;
    }

    public String getClose_hash() {
        return close_hash;
    }

    public String getMacId() {
        return macId;
    }

    public Integer getOvertime() {
        return overtime;
    }


    public String getStart_time() {
        return start_time;
    }

    public String getEnd_time() {
        return end_time;
    }
}

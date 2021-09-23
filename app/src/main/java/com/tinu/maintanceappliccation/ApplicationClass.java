package com.tinu.maintanceappliccation;

import android.app.Activity;
import android.content.Context;

import com.splunk.mint.Mint;
import com.tinu.maintanceappliccation.models.scanDeviceModel;

import java.util.ArrayList;
import java.util.List;

import androidx.multidex.MultiDexApplication;

public class ApplicationClass extends MultiDexApplication {

    private static Context mContext;
    private static Activity mActivity;
    public static String BLEAddress;
    public static String ScannedDeviceCode;
    public static SerialService service;
    public static String  deviceName;
    public static String RANDOM_APP_KEY;
    public static String closeHashValue;
    public static String deviceLocation;
    public static String deviceAddress1;
    public static String deviceAddress2;
    public static String devicepinCode;
    public static String devicelandMark;
    public static String devicelatitude;
    public static String devicelogitude;
    public static String currentLatitude;
    public static String currentLogitude;
    public static ArrayList<String> selectedImageList;
    public static String TaskListDeviceId;
    public static String TaskId;
    public static boolean isCodeScanFragment;
    public static String lock_process_status;
    public static String present_status;
    public static boolean isNormalCase;
    public static boolean isAwaitingForLock;
    public static boolean isSendMM;
    public static String unlockHashValue;
    public static boolean isStartTimeNeedToUpdate;
    private static String startTime;
    public static List<scanDeviceModel.maintenanceDocumentsImages> imagesList;
    public static String  personName;
    public static String Location;
    public static String status;
    public static String workDetails;
    public static String  task_details;
    public static String  battery_power;
    public static String  userEmail;
    public static String initial_payment_status;
    public static String install_id;
    public static String regular_maintenance_id;
    public static String battery_replacement_id;
    public static String device_replacement_id;
    public static String Device_name;
    public static String Device_version;
    public static String DeviceList;
    public static boolean isAutoLock;
    public static String repair;
    public static String initial_charges;
    public static String TypeName;





    public static Context getCurrentContext() {
        return mContext;
    }

    public static void setCurrentContext(Context mContext) {
        ApplicationClass.mContext = mContext;
        ApplicationClass.mActivity = (Activity) mContext;

    }
    public static Activity getCurrentActivity() {
        return mActivity;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
        Mint.initAndStartSession(ApplicationClass.getCurrentContext(), "f0c8d1e5");


    }
    public static String getTypeName() {
        return TypeName;
    }

    public static void setTypeName(String TypeName) {
        ApplicationClass.TypeName = TypeName;
    }

    public static String getInitialCharges() {
        return initial_charges;
    }

    public static void setInitialCharges(String initial_charges) {
        ApplicationClass.initial_charges = initial_charges;
    }



    public static String getBLEAddress() {
        return BLEAddress;
    }

    public static void setBLEAddress(String BLEAddress) {
        ApplicationClass.BLEAddress = BLEAddress;
    }

    public static String getScannedDeviceCode() {
        return ScannedDeviceCode;
    }

    public static void setScannedDeviceCode(String scannedDeviceCode) {
        ScannedDeviceCode = scannedDeviceCode;
    }

    public static SerialService getService() {
        return service;
    }

    public static void setService(SerialService service) {
        ApplicationClass.service = service;
    }
    public static String getDeviceName() {
        return deviceName;
    }

    public static void setDeviceName(String deviceName) {
        ApplicationClass.deviceName = deviceName;
    }

    public static String getRandomAppKey() {
        return RANDOM_APP_KEY;
    }

    public static void setRandomAppKey(String randomAppKey) {
        RANDOM_APP_KEY = randomAppKey;
    }



    public static String getDeviceLocation() {
        return deviceLocation;
    }

    public static String getDeviceAddress1() {
        return deviceAddress1;
    }

    public static String getDeviceAddress2() {
        return deviceAddress2;
    }

    public static String getDevicepinCode() {
        return devicepinCode;
    }

    public static String getDeviceLandMark() {
        return devicelandMark;
    }

    public static String getDevicelatitude() {
        return devicelatitude;
    }

    public static String getDevicelogitude() {
        return devicelogitude;
    }

    public static void setDeviceLocation(String deviceLocation) {
        ApplicationClass.deviceLocation = deviceLocation;
    }

    public static void setDeviceAddress1(String deviceAddress1) {
        ApplicationClass.deviceAddress1 = deviceAddress1;
    }

    public static void setDeviceAddress2(String deviceAddress2) {
        ApplicationClass.deviceAddress2 = deviceAddress2;
    }

    public static void setDevicepinCode(String devicepinCode) {
        ApplicationClass.devicepinCode = devicepinCode;
    }

    public static void setDevicelandMark(String devicelandMark) {
        ApplicationClass.devicelandMark = devicelandMark;
    }

    public static void setDevicelatitude(String devicelatitude) {
        ApplicationClass.devicelatitude = devicelatitude;
    }

    public static void setDevicelogitude(String devicelogitude) {
        ApplicationClass.devicelogitude = devicelogitude;
    }

    public static String getDevicelandMark() {
        return devicelandMark;
    }

    public static String getCurrentLatitude() {
        return currentLatitude;
    }

    public static void setCurrentLatitude(String currentLatitude) {
        ApplicationClass.currentLatitude = currentLatitude;
    }

    public static String getCurrentLogitude() {
        return currentLogitude;
    }

    public static void setCurrentLogitude(String currentLogitude) {
        ApplicationClass.currentLogitude = currentLogitude;
    }

    public static ArrayList<String> getSelectedImageList() {
        return selectedImageList;
    }

    public static void setSelectedImageList(ArrayList<String> selectedImageList) {
        ApplicationClass.selectedImageList = selectedImageList;
    }

    public static String getTaskListDeviceId() {
        return TaskListDeviceId;
    }

    public static void setTaskListDeviceId(String taskListDeviceId) {
        TaskListDeviceId = taskListDeviceId;
    }

    public static String getTaskId() {
        return TaskId;
    }

    public static void setTaskId(String taskId) {
        TaskId = taskId;
    }

    public static boolean isIsCodeScanFragment() {
        return isCodeScanFragment;
    }

    public static void setIsCodeScanFragment(boolean isCodeScanFragment) {
        ApplicationClass.isCodeScanFragment = isCodeScanFragment;
    }

    public static String getLock_process_status() {
        return lock_process_status;
    }

    public static void setLock_process_status(String lock_process_status) {
        ApplicationClass.lock_process_status = lock_process_status;
    }

    public static String getPresent_status() {
        return present_status;
    }

    public static void setPresent_status(String present_status) {
        ApplicationClass.present_status = present_status;
    }

    public static boolean isIsNormalCase() {
        return isNormalCase;
    }

    public static void setIsNormalCase(boolean isNormalCase) {
        ApplicationClass.isNormalCase = isNormalCase;
    }

    public static boolean isIsAwaitingForLock() {
        return isAwaitingForLock;
    }

    public static void setIsAwaitingForLock(boolean isAwaitingForLock) {
        ApplicationClass.isAwaitingForLock = isAwaitingForLock;
    }

    public static boolean isIsSendMM() {
        return isSendMM;
    }

    public static void setIsSendMM(boolean isSendMM) {
        ApplicationClass.isSendMM = isSendMM;
    }



    public static boolean isIsStartTimeNeedToUpdate() {
        return isStartTimeNeedToUpdate;
    }

    public static void setIsStartTimeNeedToUpdate(boolean isStartTimeNeedToUpdate) {
        ApplicationClass.isStartTimeNeedToUpdate = isStartTimeNeedToUpdate;
    }

    public static List<scanDeviceModel.maintenanceDocumentsImages> getImagesList() {
        return imagesList;
    }

    public static void setImagesList(List<scanDeviceModel.maintenanceDocumentsImages> imagesList) {
        ApplicationClass.imagesList = imagesList;
    }

    public static String getStartTime() {
        return startTime;
    }

    public static void setStartTime(String startTime) {
        ApplicationClass.startTime = startTime;
    }

    public static String getPersonName() {
        return personName;
    }

    public static void setPersonName(String personName) {
        ApplicationClass.personName = personName;
    }

    public static String getLocation() {
        return Location;
    }

    public static void setLocation(String location) {
        Location = location;
    }

    public static String getStatus() {
        return status;
    }

    public static void setStatus(String status) {
        ApplicationClass.status = status;
    }

    public static String getWorkDetails() {
        return workDetails;
    }

    public static void setWorkDetails(String workDetails) {
        ApplicationClass.workDetails = workDetails;
    }

    public static String getTask_details() {
        return task_details;
    }

    public static void setTask_details(String task_details) {
        ApplicationClass.task_details = task_details;
    }

    public static String getBattery_power() {
        return battery_power;
    }

    public static void setBattery_power(String battery_power) {
        ApplicationClass.battery_power = battery_power;
    }

    public static String getUserEmail() {
        return userEmail;
    }

    public static void setUserEmail(String userEmail) {
        ApplicationClass.userEmail = userEmail;
    }

    public static String getInitial_payment_status() {
        return initial_payment_status;
    }

    public static void setInitial_payment_status(String initial_payment_status) {
        ApplicationClass.initial_payment_status = initial_payment_status;
    }

    public static String getInstall_id() {
        return install_id;
    }

    public static String getRegular_maintenance_id() {
        return regular_maintenance_id;
    }

    public static String getBattery_replacement_id() {
        return battery_replacement_id;
    }

    public static String getDevice_replacement_id() {
        return device_replacement_id;
    }

    public static void setRegular_maintenance_id(String regular_maintenance_id) {
        ApplicationClass.regular_maintenance_id = regular_maintenance_id;
    }
    public static void setRepair_id(String repair_id) {
        ApplicationClass.repair = repair_id;
    }

    public static String getRepair_id() {
        return repair;
    }


    public static void setInstall_id(String install_id) {
        ApplicationClass.install_id = install_id;
    }

    public static void setBattery_replacement_id(String battery_replacement_id) {
        ApplicationClass.battery_replacement_id = battery_replacement_id;
    }

    public static void setDevice_replacement_id(String device_replacement_id) {
        ApplicationClass.device_replacement_id = device_replacement_id;
    }

    public static String getDevice_name() {
        return Device_name;
    }

    public static void setDevice_name(String device_name) {
        Device_name = device_name;
    }

    public static String getDevice_version() {
        return Device_version;
    }

    public static void setDevice_version(String device_version) {
        Device_version = device_version;
    }

    public static String getDeviceList() {
        return DeviceList;
    }

    public static void setDeviceList(String deviceList) {
        DeviceList = deviceList;
    }

    public static boolean isIsAutoLock() {
        return isAutoLock;
    }

    public static void setIsAutoLock(boolean isAutoLock) {
        ApplicationClass.isAutoLock = isAutoLock;
    }
}

package com.tinu.maintanceappliccation.utility;


import com.tinu.maintanceappliccation.BuildConfig;

import java.util.Locale;

public class ConstantProject {

    //sec
    public static final int NOTIFY_MANAGER_START_FOREGROUND_SERVICE = 1001;
    public ConstantProject() {}
    public static final String INTENT_ACTION_DISCONNECT = BuildConfig.APPLICATION_ID + ".Disconnect";
    public static final String NOTIFICATION_CHANNEL = BuildConfig.APPLICATION_ID + ".Channel";
    public static final String INTENT_CLASS_MAIN_ACTIVITY = BuildConfig.APPLICATION_ID + ".MainActivityDrawer";
    public static final String DefaultBLEAddress="80:1F:12:B2:2C:28";
    public static final String API_HEADER = "Bearer ";
    public static final String HEADER_AUTHORIZATION = "Authorization";
    public static boolean validate(String... strings) {
        for (String string : strings) {
            if (string == null) return false;
            if (string.length() < 1) return false;
        }
        return true;
    }

    public static String isEnglishLag= "en";
    public static String isJapaneaseLag="ja";
    public static String deviceAddress="Address";
    public static double balteryPower=4.5;
    public static final int TimoutThreeminitue=180000;
    public static final int TimeoutFiftySecond=15000;
    public static final int TimeoutFiveSecond=5000;

    public static  String ALPHA_NUMERIC_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    public static String DIGITS="0123456789";
    public static String ALPHACAPS="ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    public static String ALPHASMALL="abcdefghijklmnopqrstuvwxyz";


    //Security Stages

    public static String Sec_Status_Lock_IN="IN_send";
    public static String Sec_Status_Lock_MSG1="MSG1_sendAt_lock";
    public static String Sec_Status_Lock_C_HashValue="C:HashValue_lock";
    public static String Sec_Status_Lock_status_checking="isStatusChecking_lock";
    public static String Sec_Status_Lock_MM="isMM_send_lock";
    public static String Sec_Status_UNLock_I_hashValue="is_IHashValue_UNlock";
    public static String Sec_Status_UNLock_MSG1_send="is_MSG1_send_UNlock";
    public static String Sec_Status_UNLock_O_HashValue="is_O_HashValue_UNlock";
    public static String Sec_Status_UNLock_RR="is_RR_send_UNlock";

    //Task List
    public static String TaskTypeRepair="repair";
    public static String TaskTypeBatteryRelacement="battery replacement";

    public static String TaskListRecyclerViewAdapter="TaskListRecyclerViewAdapter";
    public static String Drawer="Drawer";
    public static String History="History";
    public static String isFrom="isFrom";
    public static String PENDING= "pendingOrRecheckBeforeBuildDeliver";
    public static String Periodic_inspection="Periodic Inspection";

    //Status
    public static String present_status_unlocked ="unlocked";
    public static String lock_process_status_free ="free";

    public static double batteryPower=4;

    public static String SummaryActivity="SummaryActivity";









}


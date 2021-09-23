package com.tinu.maintanceappliccation.utility;

import android.content.Context;
import android.content.SharedPreferences;


import com.tinu.maintanceappliccation.ApplicationClass;
import com.tinu.maintanceappliccation.R;

import static com.tinu.maintanceappliccation.ApplicationClass.getCurrentContext;

public class SharedPreferenceUtility {


    private static SharedPreferences sharedPreferences;


    public static SharedPreferences getSharedPreferenceInstance() {
        if (sharedPreferences == null)
            sharedPreferences = getCurrentContext().getSharedPreferences(
                    ApplicationClass.getCurrentContext().getString(R.string.USER_PREFERENCES),
                    Context.MODE_PRIVATE);

        return sharedPreferences;
    }



    public static void saveLastSyncTimeNotification(String lastSyncTimeNoti)
    {
        SharedPreferences.Editor sharedPreferencesEditor = getSharedPreferenceInstance().edit();

        sharedPreferencesEditor.putString("lastSyncTimeNoti", lastSyncTimeNoti);
        sharedPreferencesEditor.commit();
    }

    public static String getLastSyncTimeNoti() {

        return getSharedPreferenceInstance().getString("lastSyncTimeNoti", "");
    }


    public static void saveMoveCount(int movecount)
    {
        SharedPreferences.Editor sharedPreferencesEditor = getSharedPreferenceInstance().edit();

        sharedPreferencesEditor.putInt("movecount", movecount);
        sharedPreferencesEditor.commit();
    }
    public static void saveIsEnglish(boolean english)
    {
        SharedPreferences.Editor sharedPreferencesEditor = getSharedPreferenceInstance().edit();

        sharedPreferencesEditor.putBoolean("lang", english);
        sharedPreferencesEditor.commit();
    }

    public static void saveIsLoggedIn(boolean isLogin)
    {
        SharedPreferences.Editor sharedPreferencesEditor = getSharedPreferenceInstance().edit();

        sharedPreferencesEditor.putBoolean("isLogin", isLogin);
        sharedPreferencesEditor.commit();
    }

    public static void saveAccessToken(String  acess_token)
    {
        SharedPreferences.Editor sharedPreferencesEditor = getSharedPreferenceInstance().edit();

        sharedPreferencesEditor.putString("acess_token", acess_token);
        sharedPreferencesEditor.commit();
    }

    public static void saveUserId(String  userId)
    {
        SharedPreferences.Editor sharedPreferencesEditor = getSharedPreferenceInstance().edit();

        sharedPreferencesEditor.putString("userId", userId);
        sharedPreferencesEditor.commit();
    }

    public static void saveIsLock(boolean  isLock)
    {
        SharedPreferences.Editor sharedPreferencesEditor = getSharedPreferenceInstance().edit();

        sharedPreferencesEditor.putBoolean("isLock", isLock);
        sharedPreferencesEditor.commit();
    }
    public static boolean  isLogin() {

        return getSharedPreferenceInstance().getBoolean("isLogin", false);
    }
    public static boolean  isLock() {

        return getSharedPreferenceInstance().getBoolean("isLock", false);
    }

    public static boolean  getUserId() {

        return getSharedPreferenceInstance().getBoolean("userId", false);
    }
    public static String  getAccessToken() {

        return getSharedPreferenceInstance().getString("acess_token", "");
    }

    public static int getMoveCount() {

        return getSharedPreferenceInstance().getInt("movecount", 0);
    }
    public static boolean getIsEnglishLag() {

        return getSharedPreferenceInstance().getBoolean("lang", false);
    }

    public static void saveHashValueForUnlock(String hashvalueForUnlock)
    {
        SharedPreferences.Editor sharedPreferencesEditor = getSharedPreferenceInstance().edit();

        sharedPreferencesEditor.putString("hashvalueForUnlock", hashvalueForUnlock);
        sharedPreferencesEditor.commit();
    }

    public static String  getHashValueForUnlock() {

        return getSharedPreferenceInstance().getString("hashvalueForUnlock", "");
    }

    public static void saveIsUpholdHandle(Boolean isLockerIsStuck)
    {
        SharedPreferences.Editor sharedPreferencesEditor = getSharedPreferenceInstance().edit();

        sharedPreferencesEditor.putBoolean("isLockerIsStuck", isLockerIsStuck);
        sharedPreferencesEditor.commit();
    }

    public static Boolean  getIsUpholdHandle() {

        return getSharedPreferenceInstance().getBoolean("isLockerIsStuck", false);
    }

    public static void saveSelectedType(String type)
    {
        SharedPreferences.Editor sharedPreferencesEditor = getSharedPreferenceInstance().edit();

        sharedPreferencesEditor.putString("SelectedType", type);
        sharedPreferencesEditor.commit();
    }

    public static String  getSelectedType() {

        return getSharedPreferenceInstance().getString("SelectedType", "");
    }
}

package com.tinu.maintanceappliccation.restApiCall;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by sjn on 16/5/2019.
 */

    public class AppController extends Application {

        public static final String TAG = AppController.class
                .getSimpleName();

        private static AppController mInstance;


        static SharedPreferences.Editor editor;


        @Override
        public void onCreate() {
            super.onCreate();
            mInstance = this;
        }
        /*
       function to write string to preference file
        */
        public static void setString(Context context, String key, String value) {

            SharedPreferences sp = PreferenceManager
                    .getDefaultSharedPreferences(context);
            //apply commit
            sp.edit().putString(key, value).apply();
        }
        /*
        function to get the data written to preference file
         */
        public static String getString(Context context, String key) {

            SharedPreferences sp = PreferenceManager
                    .getDefaultSharedPreferences(context);
            return sp.getString(key, "");
        }


        public static void  clear(Context context) {

            SharedPreferences sp = PreferenceManager
                    .getDefaultSharedPreferences(context);
            editor = sp.edit();
            editor.clear();
            editor.commit();

        }



        public static synchronized AppController getInstance() {
            return mInstance;
        }



    }

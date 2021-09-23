package com.tinu.maintanceappliccation.activity;
import android.content.Intent;
import android.os.Bundle;

import com.tinu.maintanceappliccation.ApplicationClass;
import com.tinu.maintanceappliccation.MainActivityDrawer;
import com.tinu.maintanceappliccation.R;
import com.tinu.maintanceappliccation.utility.SharedPreferenceUtility;


import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    String isLogged = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ApplicationClass.setCurrentContext(this);

        Thread background = new Thread() {
            public void run() {

                try {
                    // Thread will sleep for 5

                    sleep(1*1000);

                    // After 5 seconds redirect to another intent
                    // After 5 seconds redirect to another intent
                    /*Intent i=new Intent(getBaseContext(),WelcomeActivity.class);
                    startActivity(i);*/


                    if (SharedPreferenceUtility.isLogin())
                    {
                        Intent i = new Intent(SplashActivity.this, LoginActivity.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                        startActivity(i);

                    }
                    else
                    {
                        Intent i = new Intent(SplashActivity.this, LanguageSelectionActivity.class);
                        startActivity(i);

                    }



                    //Remove activity
                    finish();

                } catch (Exception e) {

                }
            }
        };
        // start thread
        background.start();

    }



    @Override
    protected void onDestroy() {

        super.onDestroy();

    }

}


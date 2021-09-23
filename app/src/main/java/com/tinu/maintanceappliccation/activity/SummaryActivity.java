package com.tinu.maintanceappliccation.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.tinu.maintanceappliccation.ApplicationClass;
import com.tinu.maintanceappliccation.CodeScannerFragment;
import com.tinu.maintanceappliccation.MainActivity;
import com.tinu.maintanceappliccation.MainActivityDrawer;
import com.tinu.maintanceappliccation.R;
import com.tinu.maintanceappliccation.utility.ConstantProject;
import com.tinu.maintanceappliccation.utility.DialogManager;
import com.tinu.maintanceappliccation.utility.SharedPreferenceUtility;


import java.util.Locale;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

public class SummaryActivity extends BaseActivity {
    Button btn_test_device;
    TextView tv_personName, tv_deviceName, tv_startTime, tv_status, tv_location;
    String text;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String languageToLoad;
        if (SharedPreferenceUtility.getIsEnglishLag()) {
            languageToLoad = ConstantProject.isEnglishLag;
        } else {
            languageToLoad = ConstantProject.isJapaneaseLag;

        }

        Locale locale = new Locale(languageToLoad);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());

        setContentView(R.layout.activity_summary);
        ApplicationClass.setCurrentContext(this);

        init();

    }

    private void init() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        btn_test_device = (Button) findViewById(R.id.btn_test_device);
        tv_personName = (TextView) findViewById(R.id.tv_personName);
        tv_deviceName = (TextView) findViewById(R.id.tv_deviceName);
        tv_startTime = (TextView) findViewById(R.id.tv_startTime);
        tv_status = (TextView) findViewById(R.id.tv_status);
        tv_location = (TextView) findViewById(R.id.tv_location);

        btn_test_device.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferenceUtility.saveMoveCount(4);

                Intent intent = new Intent(SummaryActivity.this, MainActivityDrawer.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                intent.putExtra(ConstantProject.isFrom, ConstantProject.SummaryActivity);
                startActivity(intent);


            }
        });
        tv_deviceName.setText(ApplicationClass.getDeviceName());
        tv_personName.setText(ApplicationClass.getPersonName());
        tv_startTime.setText(ApplicationClass.getStartTime());


        if (SharedPreferenceUtility.getIsEnglishLag()) {

            if (ApplicationClass.getStatus().equalsIgnoreCase("on inspection")) {

                 text = "On Inspection";


            } else {

                 text = ApplicationClass.getStatus();

            }


        } else {
            if (ApplicationClass.getStatus().equalsIgnoreCase("on inspection")) {

                 text = "点検中";


            }


        }


        tv_status.setText(text);
        tv_location.setText(ApplicationClass.getLocation());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            DialogManager.showLogoutDialogue(new DialogManager.IMultiActionDialogOnClickListener() {
                @Override
                public void onPositiveClick() {
                    SharedPreferenceUtility.saveIsLoggedIn(false);
                    Intent intent = new Intent(ApplicationClass.getCurrentContext(), LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                    startActivity(intent);
                    finish();
                }

                @Override
                public void onNegativeClick() {

                }
            });


            // do something

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void applyOverrideConfiguration(Configuration overrideConfiguration) {
        if (overrideConfiguration != null) {
            int uiMode = overrideConfiguration.uiMode;
            overrideConfiguration.setTo(getBaseContext().getResources().getConfiguration());
            overrideConfiguration.uiMode = uiMode;
        }
        super.applyOverrideConfiguration(overrideConfiguration);
    }
}

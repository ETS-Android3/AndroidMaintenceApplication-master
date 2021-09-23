package com.tinu.maintanceappliccation.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Spinner;

import com.tinu.maintanceappliccation.ApplicationClass;
import com.tinu.maintanceappliccation.R;
import com.tinu.maintanceappliccation.adapter.DeviceSpinnerAdapter;
import com.tinu.maintanceappliccation.utility.ConstantProject;
import com.tinu.maintanceappliccation.utility.DialogManager;
import com.tinu.maintanceappliccation.utility.SharedPreferenceUtility;


import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;

public class DeviceDetailRepair extends BaseActivity {
    ImageView iv_next,iv_back;
    Spinner sp_types;
    DeviceSpinnerAdapter deviceSpinnerAdapter;
    List<String> types=new ArrayList<>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String languageToLoad;
        if (SharedPreferenceUtility.getIsEnglishLag())
        {
            languageToLoad= ConstantProject.isEnglishLag;
        }
        else {
            languageToLoad= ConstantProject.isJapaneaseLag;

        }

        Locale locale = new Locale(languageToLoad);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());

        setContentView(R.layout.activity_repair_details);
        ApplicationClass.setCurrentContext(this);

        init();

    }
    private void init(){
        iv_next =(ImageView) findViewById(R.id.iv_next);
        sp_types =(Spinner) findViewById(R.id.sp_types);
        iv_back = (ImageView)findViewById(R.id.iv_back);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        types =new ArrayList<>();
        types.add(getResources().getString(R.string.balttery));
        types.add(getResources().getString(R.string.pcb));
        types.add(getResources().getString(R.string.mechanickel));

        deviceSpinnerAdapter = new DeviceSpinnerAdapter(ApplicationClass.getCurrentContext(), types);
        sp_types.setAdapter(deviceSpinnerAdapter);


        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        iv_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(DeviceDetailRepair.this,ImageUploadActivity.class);
                startActivity(intent);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.action_settings){
            DialogManager.showLogoutDialogue(new DialogManager.IMultiActionDialogOnClickListener() {
                @Override
                public void onPositiveClick() {
                    SharedPreferenceUtility.saveIsLoggedIn(false);
                    Intent intent=new Intent(ApplicationClass.getCurrentContext(),LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                    startActivity(intent);
                    finish();
                }

                @Override
                public void onNegativeClick() {

                }
            });
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

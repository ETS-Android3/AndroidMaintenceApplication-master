package com.tinu.maintanceappliccation.activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.tinu.maintanceappliccation.ApplicationClass;
import com.tinu.maintanceappliccation.MainActivityDrawer;
import com.tinu.maintanceappliccation.R;
import com.tinu.maintanceappliccation.models.UpdateTaskResponseModel;
import com.tinu.maintanceappliccation.restApiCall.ApiClient;
import com.tinu.maintanceappliccation.restApiCall.ApiInterface;
import com.tinu.maintanceappliccation.utility.ConstantProject;
import com.tinu.maintanceappliccation.utility.DialogManager;
import com.tinu.maintanceappliccation.utility.SharedPreferenceUtility;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import androidx.appcompat.widget.Toolbar;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DeviceDetailsActivity  extends BaseActivity{
    ImageView iv_next,iv_back;
    TextView tv_device_name,tv_location,tv_address1,tv_address2,tv_pin,tv_repair_details;
    TextView et_landmark;
    ApiInterface apiService;
    LinearLayout ll_install,ll_repaire;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ApplicationClass.setCurrentContext(this);
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

        setContentView(R.layout.activity_device_details);


        init();
        setValues();

    }
    private void init(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        apiService = ApiClient.getClient().create(ApiInterface.class);

        iv_next = (ImageView) findViewById(R.id.iv_next);
        iv_back = (ImageView) findViewById(R.id.iv_back);
        tv_device_name = (TextView) findViewById(R.id.tv_device_name);
        tv_location = (TextView) findViewById(R.id.tv_location);
        tv_address1 = (TextView)findViewById(R.id.tv_address1);
        tv_address2 =(TextView) findViewById(R.id.tv_address2);
        tv_pin = (TextView) findViewById(R.id.tv_pin);
        et_landmark =(TextView) findViewById(R.id.et_landmark);
        ll_install =(LinearLayout) findViewById(R.id.ll_install);
        ll_repaire = (LinearLayout) findViewById(R.id.ll_repaire);
        tv_repair_details =(TextView) findViewById(R.id.tv_repair_details);

        if (!ApplicationClass.getTask_details().equals("null"))
        tv_repair_details.setText(""+ApplicationClass.getTask_details());

        if (SharedPreferenceUtility.getMoveCount()==1)
        {
            ll_install.setVisibility(View.VISIBLE);
           // ll_repaire.setVisibility(View.GONE);
        }
        else {
            ll_install.setVisibility(View.GONE);
           // ll_repaire.setVisibility(View.VISIBLE);


        }

        if(!ApplicationClass.getTypeName().equalsIgnoreCase("install")){
            ll_repaire.setVisibility(View.VISIBLE);
            DialogManager.showStatusDialogue(DeviceDetailsActivity.this, "", ApplicationClass.getTask_details(), ApplicationClass.getBattery_power(), ApplicationClass.getUserEmail(), ApplicationClass.getPresent_status(), ApplicationClass.getInitial_payment_status(),ApplicationClass.getInitialCharges(), new DialogManager.IUniActionDialogOnClickListener() {
                @Override
                public void onPositiveClick() {

                }
            });

        }else {

            ll_repaire.setVisibility(View.GONE);

        }



        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(DeviceDetailsActivity.this, MainActivityDrawer.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

               // intent.putExtra(ConstantProject.isFrom,ConstantProject.History);
                startActivity(intent);
            }
        });
        iv_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (SharedPreferenceUtility.getMoveCount()==1)
                {
                    if (et_landmark.getText().toString().isEmpty())
                    {
                        DialogManager.showUniActionDialog(DeviceDetailsActivity.this, getResources().getString(R.string.empty_landmark), "", new DialogManager.IUniActionDialogOnClickListener() {
                            @Override
                            public void onPositiveClick() {

                            }
                        });
                    }
                    else {
                        if (ApplicationClass.isIsStartTimeNeedToUpdate())
                        {
                            updateStartTime();
                        }
                        else {
                            Intent intent = new Intent(ApplicationClass.getCurrentContext(), ImageUploadActivity.class);
                            startActivity(intent);

                        }


                    }
                }

                else {
                    if (ApplicationClass.isIsStartTimeNeedToUpdate())
                    {
                        updateStartTime();
                    }
                    else {
                        Intent intent = new Intent(ApplicationClass.getCurrentContext(), ImageUploadActivity.class);
                        startActivity(intent);

                    }
                }
              //  ConstantProject.PENDING


            }
        });
    }

    public void setValues()
    {
        tv_device_name.setText(ApplicationClass.getDeviceName());
        tv_location.setText(ApplicationClass.getDeviceLocation());

        if(!ApplicationClass.getDeviceAddress1().equalsIgnoreCase("null")){
            tv_address1.setVisibility(View.VISIBLE);
            tv_address1.setText(ApplicationClass.getDeviceAddress1());


        }else {

            tv_address1.setVisibility(View.GONE);

            tv_address1.setText("");

        }



        if(!ApplicationClass.getDeviceAddress2().equalsIgnoreCase("null")){
            tv_address2.setVisibility(View.VISIBLE);

            tv_address2.setText(ApplicationClass.getDeviceAddress2());

        }else {
            tv_address2.setVisibility(View.GONE);

            tv_address2.setText("");

        }
        tv_pin.setText(ApplicationClass.getDevicepinCode());

        et_landmark.setText(ApplicationClass.getDeviceLandMark());
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
            // do something
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

    private void updateStartTime() {

        showBusyAnimation("");

      RequestBody taskID = null,start_time=null,work_details=null;
      String startTime=getCurrentTime();
      ApplicationClass.setStartTime(startTime);
        try {
            taskID = RequestBody.create(MediaType.parse("text/plain"), ApplicationClass.getTaskId());
            start_time = RequestBody.create(MediaType.parse("text/plain"), startTime);
            work_details = RequestBody.create(MediaType.parse("text/plain"), et_landmark.getText().toString());
        } catch (Exception e) {
            e.printStackTrace();
        }


        String acess_token = ConstantProject.API_HEADER + SharedPreferenceUtility.getAccessToken();


        apiService.updateTaskTimeAndLandMark(acess_token,taskID,start_time,work_details).enqueue(new Callback<UpdateTaskResponseModel>() {
            @Override
            public void onResponse(Call<UpdateTaskResponseModel> call, Response<UpdateTaskResponseModel> response) {

                if (response.code() == 200) {
                    Boolean status = true;
                    if (status == true && response.body().getData() != null) {

                        Intent intent =new Intent(DeviceDetailsActivity.this,ImageUploadActivity.class);
                        startActivity(intent);

                    } else if (status == false) {
                        String msg = response.body().getMessage();
                        new AlertDialog.Builder(ApplicationClass.getCurrentActivity())
                                .setTitle(R.string.alert)
                                .setMessage(msg)
                                .setCancelable(false)
                                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        Intent intent = new Intent(ApplicationClass.getCurrentContext(), MainActivityDrawer.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);

                                    }


                                }).show();

                    }
                } else if (response.code() == 404) {


                }
                hideBusyAnimation();
            }

            @Override
            public void onFailure(Call<UpdateTaskResponseModel> call, Throwable t) {
                hideBusyAnimation();
                if (!BaseActivity.isNetworkConnectionAvailable(ApplicationClass.getCurrentContext())) {
                    new AlertDialog.Builder(ApplicationClass.getCurrentContext())
                            .setTitle(R.string.alert)
                            .setMessage(R.string.OperfailedExtnd)
                            .setCancelable(false)
                            .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // Whatever..
                                }
                            }).show();

                } else {
                    Toast.makeText(ApplicationClass.getCurrentContext(), R.string.OperfailedExtnd, Toast.LENGTH_LONG).show();
                }
            }
        });
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent =new Intent(DeviceDetailsActivity.this, MainActivityDrawer.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        // intent.putExtra(ConstantProject.isFrom,ConstantProject.History);
        startActivity(intent);
    }
}

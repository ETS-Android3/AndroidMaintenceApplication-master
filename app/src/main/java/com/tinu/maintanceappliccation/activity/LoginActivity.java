package com.tinu.maintanceappliccation.activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


import com.tinu.maintanceappliccation.ApplicationClass;
import com.tinu.maintanceappliccation.MainActivityDrawer;
import com.tinu.maintanceappliccation.R;
import com.tinu.maintanceappliccation.models.UserLoginModel;
import com.tinu.maintanceappliccation.restApiCall.ApiClient;
import com.tinu.maintanceappliccation.restApiCall.ApiInterface;
import com.tinu.maintanceappliccation.utility.ConstantProject;
import com.tinu.maintanceappliccation.utility.DialogManager;
import com.tinu.maintanceappliccation.utility.SharedPreferenceUtility;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends BaseActivity {
private Button btn_login;
private String languageToLoad;
private EditText et_user_name,et_password;

    ApiInterface apiService;
    private static final int PERMISSION_REQUEST_CODE = 100;
    private static final int PERMISSION_REQUEST_CODE_WRITE = 101;
    CountDownTimer IhashValueSplit;
    int count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        setContentView(R.layout.activity_login_activity);
        apiService = ApiClient.getClient().create(ApiInterface.class);
        ApplicationClass.setCurrentContext(this);
        init();

    }
    public void init(){
        btn_login = (Button) findViewById(R.id.btn_login);
        et_user_name =(EditText)findViewById(R.id.et_user_name);
        et_password =(EditText)findViewById(R.id.et_password);
        et_password.setError(null);
        et_user_name.setError(null);

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (et_user_name.getText().toString().isEmpty())
                {
                    DialogManager.showAlertSingleActionDialog(ApplicationClass.getCurrentActivity().getResources().getString(R.string.empty_user_name), new DialogManager.IUniActionDialogOnClickListener() {
                        @Override
                        public void onPositiveClick() {

                        }
                    });
                }
               else if (et_password.getText().toString().isEmpty())
                {
                    DialogManager.showAlertSingleActionDialog(ApplicationClass.getCurrentActivity().getResources().getString(R.string.password_empty), new DialogManager.IUniActionDialogOnClickListener() {
                        @Override
                        public void onPositiveClick() {

                        }
                    });
                }
                else {
                    LoginAPICall();
                  //  sample();

                }

            }
        });


        if (Build.VERSION.SDK_INT >= 23) {
            if (checkPermission()) {
            } else {
                requestPermission(); // Code for permission
            }
        }
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkPermissionWrite()) {
            } else {
                requestPermissionWrite(); // Code for permission
            }
        }
    }

    public void sample()
    {
        String hashValue= "O:" + "faf100b3fd168ac5819f26841f13c723" + ";";//35
        List<String> list= new ArrayList<String>();

        int index = 0;
        while (index<hashValue.length()) {

            list.add(hashValue.substring(index, Math.min(index+8,hashValue.length())));
            index=index+8;
        }

        IhashValueSplit = new CountDownTimer(2000, 100) {

            public void onTick(long millisUntilFinished) {
                if (list.size()>count && count<8)
                {
                    Log.e("HashValue",list.get(count));
                }
                count++;



            }
            public void onFinish() {
                if (IhashValueSplit != null) {
                    IhashValueSplit.cancel();
                    IhashValueSplit = null;
                }



            }
        }.start();
    }

    public void LoginAPICall()
    {
        //mtuser@carrypark.com
        //password\
        String lan="jp";
        showBusyAnimation("");
        if (SharedPreferenceUtility.getIsEnglishLag())
        {
            lan="en";
        }
        else {
            lan="jp";
        }

        apiService.callUserLogin(et_user_name.getText().toString(),et_password.getText().toString(),"2","WlHVDPCB0ZvE4oswXii7wf7PIuFkUSb7yhaOFOrx",lan).enqueue(new Callback<UserLoginModel>() {
            @Override
            public void onResponse(Call<UserLoginModel> call, final Response<UserLoginModel> response) {

                SharedPreferenceUtility.saveLastSyncTimeNotification(getCurrentTime());
                if (response.code() == 200) {
                    hideBusyAnimation();
                    if (response.body().isSuccess())
                    {
                        SharedPreferenceUtility.saveIsLoggedIn(true);
                        SharedPreferenceUtility.saveAccessToken(response.body().getData().getAccess_token());
                        Intent intent=new Intent(LoginActivity.this, MainActivityDrawer.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                        startActivity(intent);
                    }
                    else {
                        DialogManager.showUniActionDialog(LoginActivity.this, response.body().getMessage(), "", new DialogManager.IUniActionDialogOnClickListener() {
                            @Override
                            public void onPositiveClick() {

                            }
                        });

                    }


                } else if (response.code() == 404) {


                }

            }

            @Override
            public void onFailure(Call<UserLoginModel> call, Throwable t) {
                hideBusyAnimation();
                if (!isNetworkConnectionAvailable(getApplicationContext())) {
                   // Toast.makeText(getApplicationContext(), getResources().getString(R.string.networkerror), Toast.LENGTH_LONG).show();

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

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(LoginActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }
    private void requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(LoginActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
            //Toast.makeText(MainActivity.this, "Write External Storage permission allows us to read files. Please allow this permission in App Settings.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(LoginActivity.this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
               // Log.e("value", "Permission Granted, Now you can use local drive .");
            } else {
              //  Log.e("value", "Permission Denied, You cannot use local drive .");
            }
            break;
            case PERMISSION_REQUEST_CODE_WRITE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.e("value", "Permission Granted, Now you can use local drive .");
            } else {
                Log.e("value", "Permission Denied, You cannot use local drive .");
            }
            break;
        }
    }
    private boolean checkPermissionWrite() {
        int result = ContextCompat.checkSelfPermission(LoginActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private void requestPermissionWrite() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(LoginActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            //Toast.makeText(MainActivity.this, "Write External Storage permission allows us to save files. Please allow this permission in App Settings.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(LoginActivity.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE_WRITE);
        }
    }

}

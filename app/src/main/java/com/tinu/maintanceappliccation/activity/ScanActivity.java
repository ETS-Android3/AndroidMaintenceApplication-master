package com.tinu.maintanceappliccation.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.zxing.Result;
import com.tinu.maintanceappliccation.ApplicationClass;
import com.tinu.maintanceappliccation.MainActivityDrawer;
import com.tinu.maintanceappliccation.R;
import com.tinu.maintanceappliccation.models.GetHashMsgModel;
import com.tinu.maintanceappliccation.models.scanDeviceModel;
import com.tinu.maintanceappliccation.restApiCall.ApiClient;
import com.tinu.maintanceappliccation.restApiCall.ApiInterface;
import com.tinu.maintanceappliccation.utility.ConstantProject;
import com.tinu.maintanceappliccation.utility.DialogManager;
import com.tinu.maintanceappliccation.utility.SharedPreferenceUtility;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ScanActivity extends BaseActivity {

    private static final int REQUEST_CAMERA_PERMISSION = 201;
    static final int MY_PERMISSIONS_REQUEST_CAMERA = 1242;
    ApiInterface apiService;
    ImageView iv_back;

    private CodeScanner mCodeScanner;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_code_scanner);
        ApplicationClass.setCurrentContext(this);
        init();


    }

    private void init() {
        apiService = ApiClient.getClient().create(ApiInterface.class);

        CodeScannerView scannerView = findViewById(R.id.scanner_view);
        iv_back = (ImageView)findViewById(R.id.iv_back);

        mCodeScanner = new CodeScanner(this, scannerView);
        mCodeScanner.setAutoFocusEnabled(true);
        mCodeScanner.setTouchFocusEnabled(false);
        mCodeScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull final Result result) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (!result.getText().equals("07152283"))
                        {

                            String device_code = result.getText().toString();
                            String device_id = "";
                            if(device_code.contains("=")) {
                                String[] id = device_code.split("=");
                                 device_id = id[1];

                            }else {

                                 device_id = result.getText().toString();
                            }


                                String msg = "";
                            if (!SharedPreferenceUtility.getIsEnglishLag()) {
                                msg = device_id + " " + ApplicationClass.getCurrentContext().getResources().getString(R.string.deviceConfirmation);
                            } else {
                                msg = ApplicationClass.getCurrentContext().getResources().getString(R.string.deviceConfirmation) + " " + device_id;


                            }

                            DialogManager.showConfirmDialogue(ApplicationClass.getCurrentActivity(), msg, new DialogManager.IMultiActionDialogOnClickListener() {

                                @Override
                                public void onPositiveClick() {
                                    String code = result.getText();

                                    if (SharedPreferenceUtility.getMoveCount()==8)
                                    {
                                        if(code.contains("http://159.65.150.119")||code.contains("http://167.172.39.84")){

                                            if(code.contains("=")){
                                                String[] id = code.split("=");
                                                String device_id = id[1];


                                                ApplicationClass.setScannedDeviceCode(device_id);
                                                Intent intent = new Intent(ScanActivity.this, HistoryActivity.class);
                                                startActivity(intent);

                                            }else {


                                                ApplicationClass.setScannedDeviceCode(result.getText());
                                                Intent intent = new Intent(ScanActivity.this, HistoryActivity.class);
                                                startActivity(intent);
                                            }


                                        }else {

                                            ApplicationClass.setScannedDeviceCode(result.getText());
                                            Intent intent = new Intent(ScanActivity.this, HistoryActivity.class);
                                            startActivity(intent);
                                        }

                                    }
                                    else {

                                        if(code.contains("http://159.65.150.119")||code.contains("http://167.172.39.84")) {

                                            if (code.contains("=")) {
                                                String[] id = code.split("=");
                                                String device_id = id[1];

                                                getLockerDetailsFromScanner(device_id);

                                            } else {

                                                getLockerDetailsFromScanner(result.getText());

                                            }

                                        }else {

                                            getLockerDetailsFromScanner(result.getText());


                                        }

                                    }
                                }

                                @Override
                                public void onNegativeClick() {
                                    ApplicationClass.setScannedDeviceCode(result.getText());
                                    Intent intent = new Intent(ScanActivity.this, MainActivityDrawer.class);
                                    startActivity(intent);
                                }


                            });




                        }
                    }
                });
            }
        });
        scannerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCodeScanner.startPreview();
            }
        });

        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(ScanActivity.this, MainActivityDrawer.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                startActivity(intent);
            }
        });

    }


    @Override
    public void onPause() {
        mCodeScanner.releaseResources();

        super.onPause();



    }

    @Override
    public void onStart() {
        super.onStart();

        //requesting permission
        int permissionCheck;
        permissionCheck = ContextCompat.checkSelfPermission(ApplicationClass.getCurrentActivity(), Manifest.permission.CAMERA);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(ApplicationClass.getCurrentActivity(), Manifest.permission.CAMERA)) {

            } else {
                ActivityCompat.requestPermissions(ApplicationClass.getCurrentActivity(), new String[]{Manifest.permission.CAMERA}, MY_PERMISSIONS_REQUEST_CAMERA);
            }
        } else {

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        // ignore requestCode as there is only one in this fragment
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(ApplicationClass.getCurrentActivity());
            builder.setTitle(getText(R.string.location_denied_title));
            builder.setMessage(getText(R.string.location_denied_message));
            builder.setPositiveButton(android.R.string.ok, null);
            builder.show();
        }
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CAMERA:
                if (ActivityCompat.checkSelfPermission(ApplicationClass.getCurrentActivity(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)
                    /*try {
                        mCameraManager.openCamera(mCameraIDsList[1], mCameraStateCB, new Handler());
                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                    }*/
                    break;
        }
    }


    private void getLockerDetailsFromScanner(String scannedDeviceCode) {

        showBusyAnimation("");
        Map<String, Object> candidateMap = new HashMap<>();

        if (SharedPreferenceUtility.getIsEnglishLag()) {
            candidateMap.put("lang_id", ConstantProject.isEnglishLag);
        } else {
            candidateMap.put("lang_id", ConstantProject.isJapaneaseLag);
        }

        candidateMap.put("device_id", scannedDeviceCode);
        String acess_token = ConstantProject.API_HEADER + SharedPreferenceUtility.getAccessToken();


        apiService.getScannedDeviceDetails(acess_token, candidateMap).enqueue(new Callback<scanDeviceModel>() {
            @Override
            public void onResponse(Call<scanDeviceModel> call, Response<scanDeviceModel> response) {

                if (response.code() == 200) {
                    Boolean status = response.body().isSuccess();
                    if (status == true && response.body().getData() != null) {
                        ApplicationClass.setInitialCharges("" + response.body().getData().getInitial_charges());
                        ApplicationClass.setTypeName(""+response.body().getData().getMainteanceTask().getType());


                        ApplicationClass.setDeviceName("" + response.body().getData().getDevice_name());
                        ApplicationClass.setScannedDeviceCode("" + response.body().getData().getDevice_id());
                        ApplicationClass.setDeviceAddress1("" + response.body().getData().getAddress_1());
                        ApplicationClass.setDeviceLocation("" + response.body().getData().getLocation());
                        ApplicationClass.setDeviceAddress2("" + response.body().getData().getAddress_2());
                        ApplicationClass.setDevicepinCode("" + response.body().getData().getPincode());
                        ApplicationClass.setDevicelandMark("" + response.body().getData().getLandmark());
                        ApplicationClass.setDevicelatitude("" + response.body().getData().getLatitude());
                        ApplicationClass.setDevicelogitude("" + response.body().getData().getLongitude());
                        ApplicationClass.setLocation("" + response.body().getData().getLocation());
                        ApplicationClass.setStatus("" + response.body().getData().getMainteanceTask().getStatus());
                        ApplicationClass.setPresent_status(""+response.body().getData().getPresent_status());

                        ApplicationClass.setUserEmail(""+response.body().getData().getUser_email());
                        ApplicationClass.setBattery_power(""+response.body().getData().getMainteanceTask().getBattery_level());
                        ApplicationClass.setWorkDetails("" + response.body().getData().getMainteanceTask().getWork_details());
                        ApplicationClass.setTask_details("" + response.body().getData().getMainteanceTask().getTask_details());
                        ApplicationClass.setInitial_payment_status(""+response.body().getData().getInitial_payment_status());

                        ApplicationClass.setRegular_maintenance_id(""+response.body().getData().getMainteanceTask().getRegular_maintenance());
                        ApplicationClass.setInstall_id(""+response.body().getData().getMainteanceTask().getInstall());
                        ApplicationClass.setBattery_replacement_id(""+response.body().getData().getMainteanceTask().getBattery_replacement());
                        ApplicationClass.setDevice_replacement_id(""+response.body().getData().getMainteanceTask().getDevice_replacement());
                        ApplicationClass.setRepair_id(""+response.body().getData().getMainteanceTask().getRepair());

                        ApplicationClass.setPersonName(response.body().getData().getMainteanceTask().getMaintenance_person_name());
                        if (response.body().getData() != null && response.body().getData().getMainteanceTask() != null) {
                            if (response.body().getData().getMainteanceTask().getLandmark() != null && !response.body().getData().getMainteanceTask().getLandmark().isEmpty())
                                ApplicationClass.setDevicelandMark(response.body().getData().getMainteanceTask().getLandmark());
                        }

                        if (response.body().getData() != null && response.body().getData().getMainteanceTask() != null) {
                            if (response.body().getData().getMainteanceTask().getLandmark() == null || response.body().getData().getMainteanceTask().getStart_time() == null) {
                                ApplicationClass.setIsStartTimeNeedToUpdate(true);
                            }
                            if (response.body().getData().getMainteanceTask().getLandmark() != null) {
                                if (response.body().getData().getMainteanceTask().getLandmark().isEmpty()) {
                                    ApplicationClass.setIsStartTimeNeedToUpdate(true);
                                } else if (response.body().getData().getMainteanceTask().getLandmark().length() < 0) {
                                    ApplicationClass.setIsStartTimeNeedToUpdate(true);

                                }

                                if (response.body().getData().getMainteanceTask().getStart_time()==null) {
                                    ApplicationClass.setIsStartTimeNeedToUpdate(true);
                                } else if (response.body().getData().getMainteanceTask().getStart_time().length() < 0) {
                                    ApplicationClass.setIsStartTimeNeedToUpdate(true);

                                }
                                ApplicationClass.setStartTime(response.body().getData().getMainteanceTask().getStart_time());

                            }

                            ApplicationClass.setTaskId("" + response.body().getData().getMainteanceTask().getId());
                        }
                        if (response.body().getData() != null) {
                            if (response.body().getData().getMaintenanceDocumentsImages() != null) {
                                ApplicationClass.setImagesList(response.body().getData().getMaintenanceDocumentsImages());
                            }
                        }

                        int moveCount;


                        if (response.body().getData().getMaintenance_type().equalsIgnoreCase(ConstantProject.TaskTypeRepair)|| response.body().getData().getMaintenance_type().equalsIgnoreCase(ConstantProject.TaskTypeBatteryRelacement)) {
                            moveCount = 2;
                        } else {
                            moveCount = 1;

                        }


                        if (SharedPreferenceUtility.getMoveCount() == moveCount) {
                            if (moveCount == 1) {
                                Intent intent = new Intent(ScanActivity.this, DeviceDetailsActivity.class);
                                startActivity(intent);
                            } else {
                                Intent intent = new Intent(ScanActivity.this, DeviceDetailsActivity.class);
                                startActivity(intent);
                            }
                        } else {

                            if (moveCount == 1) {
                                SharedPreferenceUtility.saveMoveCount(1);
                                Intent intent = new Intent(ScanActivity.this, DeviceDetailsActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                       /* DialogManager.showConfirmDialogue(ScanActivity.this, getResources().getString(R.string.confirm_install) + " " + ApplicationClass.getDeviceName(), new DialogManager.IMultiActionDialogOnClickListener() {
                                            @Override
                                            public void onPositiveClick() {

                                            }

                                            @Override
                                            public void onNegativeClick() {

                                            }
                                        });*/
                            } else {
                                SharedPreferenceUtility.saveMoveCount(2);
                                Intent intent = new Intent(ScanActivity.this, DeviceDetailsActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);

                                        /*DialogManager.showConfirmDialogue(ScanActivity.this, getResources().getString(R.string.confirm_repair) + " " + ApplicationClass.getDeviceName(), new DialogManager.IMultiActionDialogOnClickListener() {
                                            @Override
                                            public void onPositiveClick() {

                                            }

                                            @Override
                                            public void onNegativeClick() {

                                            }
                                        });*/
                            }
                        }


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

                    DialogManager.showUniActionDialog(ApplicationClass.getCurrentActivity(), ApplicationClass.getCurrentContext().getResources().getString(R.string.no_pending_task), "ok", new DialogManager.IUniActionDialogOnClickListener() {
                        @Override
                        public void onPositiveClick() {
                            finish();
                        }
                    });

                }
                hideBusyAnimation();
            }

            @Override
            public void onFailure(Call<scanDeviceModel> call, Throwable t) {
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
    public void onResume() {
        super.onResume();
        mCodeScanner.startPreview();

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

package com.tinu.maintanceappliccation.activity;
import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.tinu.maintanceappliccation.ApplicationClass;
import com.tinu.maintanceappliccation.MainActivityDrawer;
import com.tinu.maintanceappliccation.R;
import com.tinu.maintanceappliccation.adapter.WorkDetailsSampleRecyclerViewAdapter;
import com.tinu.maintanceappliccation.models.ImageModel;
import com.tinu.maintanceappliccation.models.UpdateTaskResponseModel;
import com.tinu.maintanceappliccation.models.WorkDetailsModel;
import com.tinu.maintanceappliccation.restApiCall.ApiClient;
import com.tinu.maintanceappliccation.restApiCall.ApiInterface;
import com.tinu.maintanceappliccation.utility.ConstantProject;
import com.tinu.maintanceappliccation.utility.DialogManager;
import com.tinu.maintanceappliccation.utility.SharedPreferenceUtility;


import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class WorkDetailsActivity extends BaseActivity {
    Button btn_worksummary;
    ImageView iv_back;
    EditText edtInput,ed_location;
    int PERMISSION_ID = 44;
    private static final int REQUEST_LOCATION = 1;
    private FusedLocationProviderClient mFusedLocationClient;
    ApiInterface apiService;
    String latitude,longitude;
    RecyclerView rv_check_box_list;

    RecyclerView mListView;
    String arrayData="",time="";
    WorkDetailsSampleRecyclerViewAdapter workDetailsSampleRecyclerViewAdapter;
    TextView SelcetTime;
    public static String EMAIL;
    List<WorkDetailsModel> workDetailsModelList =new ArrayList<>();
    String install="",inspection="",periodic_inspection="",regular_maintence="",battery_replacement="",device_replacement="";
    int len;

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
        apiService = ApiClient.getClient().create(ApiInterface.class);

        Locale locale = new Locale(languageToLoad);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());

        setContentView(R.layout.activity_work_details);
        ApplicationClass.setCurrentContext(this);

        init();


    }
    private void init(){
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        btn_worksummary =(Button)findViewById(R.id.btn_worksummary);
        iv_back =(ImageView)findViewById(R.id.iv_back);
        edtInput =(EditText)findViewById(R.id.edtInput);
        ed_location =(EditText) findViewById(R.id.ed_location);
        rv_check_box_list = (RecyclerView) findViewById(R.id.rv_check_box_list);

        rv_check_box_list.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ApplicationClass.getCurrentContext());
        rv_check_box_list.setLayoutManager(linearLayoutManager);



        if (ApplicationClass.getWorkDetails().equalsIgnoreCase("null"))
        {
           // edtInput.setText(ApplicationClass.getCurrentContext().getResources().getString(R.string.enter_work));
        }
        else
        edtInput.setText(ApplicationClass.getWorkDetails());



        edtInput.setImeOptions(EditorInfo.IME_ACTION_DONE);
        edtInput.setRawInputType(InputType.TYPE_CLASS_TEXT);
        /*if (SharedPreferenceUtility.getMoveCount()==1)
        {
         rv_check_box_list.setVisibility(View.GONE);
        }
        else {
            rv_check_box_list.setVisibility(View.VISIBLE);

        }*/
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        btn_worksummary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if ((edtInput.getText().toString().trim().length()==0) && (install.equals("0") && battery_replacement.equals("0")  && device_replacement.equals("0") && inspection.equals("0") && periodic_inspection.equals("0"))) {



                    showAlertSingleActionDialog(ApplicationClass.getCurrentActivity().getResources().getString(R.string.enterDetail));




                } else if (ed_location.getText().toString().isEmpty()) {
                        DialogManager.showAlertSingleActionDialog(ApplicationClass.getCurrentActivity().getResources().getString(R.string.empty_location), new DialogManager.IUniActionDialogOnClickListener() {
                            @Override
                            public void onPositiveClick() {

                            }
                        });

                    } else if (!validateLatitudeAndLongitude(ed_location.getText().toString())) {
                        DialogManager.showAlertSingleActionDialog(ApplicationClass.getCurrentActivity().getResources().getString(R.string.empty_location), new DialogManager.IUniActionDialogOnClickListener() {
                            @Override
                            public void onPositiveClick() {

                            }
                        });


                    } else {
                        updateTaskDetailsAndLatLongitude();
                    }




            }
        });

        if (ApplicationClass.getDevicelatitude()!=null && !ApplicationClass.getDevicelatitude().isEmpty()
        && ApplicationClass.getDevicelogitude()!=null && !ApplicationClass.getDevicelogitude().isEmpty())
        {
            ed_location.setText(ApplicationClass.getDevicelatitude()+","+ApplicationClass.getDevicelogitude());

        }
        else if (ApplicationClass.getCurrentLatitude()!=null && !ApplicationClass.getCurrentLatitude().isEmpty()
        && ApplicationClass.getCurrentLogitude()!=null && !ApplicationClass.getCurrentLogitude().isEmpty())
        {

            ed_location.setText(ApplicationClass.getCurrentLatitude()+","+ApplicationClass.getCurrentLogitude());

        }
        else {
            ed_location.setText(",");
        }


        // REMOVE

        WorkDetailsModel workDetailsInstall =new WorkDetailsModel();
        workDetailsInstall.setTag("install");
        install=ApplicationClass.getInstall_id();
        workDetailsInstall.setId(ApplicationClass.getInstall_id());
        workDetailsInstall.setName(ApplicationClass.getCurrentContext().getResources().getString(R.string.install));
        workDetailsModelList.add(workDetailsInstall);

       /* WorkDetailsModel workDetailsRegMaint =new WorkDetailsModel();
        workDetailsRegMaint.setTag("regular_maintenance");
        regular_maintence=ApplicationClass.getRegular_maintenance_id();
        workDetailsRegMaint.setId(ApplicationClass.getRegular_maintenance_id());
        workDetailsRegMaint.setName(ApplicationClass.getCurrentContext().getResources().getString(R.string.lblRegular));
        workDetailsModelList.add(workDetailsRegMaint);*/

        WorkDetailsModel workDetailsInspection =new WorkDetailsModel();
        workDetailsInspection.setTag("inspection");
        inspection=ApplicationClass.getRepair_id();
        workDetailsInspection.setId(ApplicationClass.getRepair_id());
        workDetailsInspection.setName(ApplicationClass.getCurrentContext().getResources().getString(R.string.repair));
        workDetailsModelList.add(workDetailsInspection);

        WorkDetailsModel workDetailPeriodicInspection =new WorkDetailsModel();
        workDetailPeriodicInspection.setTag("periodic_inspection");
        periodic_inspection=ApplicationClass.getRegular_maintenance_id();
         workDetailPeriodicInspection.setId(ApplicationClass.getRegular_maintenance_id());
        workDetailPeriodicInspection.setName(ApplicationClass.getCurrentContext().getResources().getString(R.string.periodic_inspection));
        workDetailsModelList.add(workDetailPeriodicInspection);

        WorkDetailsModel workDetailsBatteryRe =new WorkDetailsModel();
        workDetailsBatteryRe.setTag("battery_replacement");
        battery_replacement=ApplicationClass.getBattery_replacement_id();
        workDetailsBatteryRe.setId(ApplicationClass.getBattery_replacement_id());
        workDetailsBatteryRe.setName(ApplicationClass.getCurrentContext().getResources().getString(R.string.lblBtryReplce));
        workDetailsModelList.add(workDetailsBatteryRe);

        WorkDetailsModel workDetailsDeviceRe =new WorkDetailsModel();
        workDetailsDeviceRe.setTag("device_replacement");
        device_replacement = ApplicationClass.getDevice_replacement_id();
        workDetailsDeviceRe.setId(ApplicationClass.getDevice_replacement_id());
        workDetailsDeviceRe.setName(ApplicationClass.getCurrentContext().getResources().getString(R.string.lblDeviceReplce));
        workDetailsModelList.add(workDetailsDeviceRe);

        workDetailsSampleRecyclerViewAdapter = new WorkDetailsSampleRecyclerViewAdapter(this, workDetailsModelList, new WorkDetailsSampleRecyclerViewAdapter.CustomOnClickListener() {

            @Override
            public void updateClick(boolean isChecked, WorkDetailsModel workDetailsModel) {
                if (workDetailsModel.getTag().equals("install"))
                {
                    if (isChecked)
                    {
                        install ="1";
                    }
                    else {
                        install="0";
                    }
                }
                else if (workDetailsModel.getTag().equals("regular_maintenance"))
                {
                    if (isChecked)
                    {
                        regular_maintence ="1";
                    }
                    else {
                        regular_maintence="0";
                    }
                }
                else if (workDetailsModel.getTag().equals("battery_replacement"))
                {
                    if (isChecked)
                    {
                        battery_replacement ="1";
                    }
                    else {
                        battery_replacement="0";
                    }
                }
                else if (workDetailsModel.getTag().equals("device_replacement"))
                {
                    if (isChecked)
                    {
                        device_replacement ="1";
                    }
                    else {
                        device_replacement="0";
                    }
                }
                else if (workDetailsModel.getTag().equals("inspection"))
                {
                    if (isChecked)
                    {
                        inspection ="1";
                    }
                    else {
                        inspection="0";
                    }
                }
                else if (workDetailsModel.getTag().equals("periodic_inspection"))
                {
                    if (isChecked)
                    {
                        periodic_inspection ="1";
                    }
                    else {
                        periodic_inspection="0";
                    }
                }
            }


        });
        rv_check_box_list.setAdapter(workDetailsSampleRecyclerViewAdapter);


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

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PERMISSION_ID) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Granted. Start getting the location information


                ////
                if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    Activity#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for Activity#requestPermissions for more details.
                    return;
                }
                mFusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
                    if (location != null) {
                        ApplicationClass.setCurrentLatitude("" + location.getLatitude());
                        ApplicationClass.setCurrentLogitude("" + location.getLongitude());
                    }
                });
                /////
            } else if (requestCode == 1) {
            }
        }

    }

    @Override
    protected void onStart() {
        super.onStart();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(
                    ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(ApplicationClass.getCurrentActivity(), new String[]{ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
            }
        }

        // check permission

    }

    private boolean hasPermission(String permission) {
        if (canMakeSmores()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return (checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED);
            }
        }
        return true;
    }

    private boolean canMakeSmores() {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
    }


    @SuppressLint("MissingPermission")
    private void getLastLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                mFusedLocationClient.getLastLocation().addOnCompleteListener(
                        new OnCompleteListener<Location>() {
                            @Override
                            public void onComplete(@NonNull Task<Location> task) {
                                Location location = task.getResult();
                                if (location == null) {
                                    requestNewLocationData();
                                } else {
                                    ApplicationClass.setCurrentLatitude("" + location.getLatitude());
                                    ApplicationClass.setCurrentLogitude("" + location.getLongitude());


                                }
                            }
                        }
                );
            } else {
                startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), 1);


                getLocation();
            }
        } else {
            requestPermissions();
        }
    }

    @SuppressLint("MissingPermission")
    private void requestNewLocationData(){

        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(0);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mFusedLocationClient.requestLocationUpdates(
                mLocationRequest, mLocationCallback,
                Looper.myLooper()
        );

    }

    private LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();
            ApplicationClass.setCurrentLatitude("" + mLastLocation.getLatitude());
            ApplicationClass.setCurrentLogitude("" + mLastLocation.getLongitude());

        }
    };

    private boolean checkPermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                PERMISSION_ID
        );
    }

    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER
        );
    }

    public void getLocation() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    Activity#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for Activity#requestPermissions for more details.
                return;
            }
        }
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(ApplicationClass.getCurrentActivity(), new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            ApplicationClass.setCurrentLatitude("" + location.getLatitude());
                            ApplicationClass.setCurrentLogitude("" + location.getLongitude());


                        } else {
                        }
                    }
                });




    }

    @Override
    public void onResume() {
        super.onResume();
        getLastLocation();

    }
    private void updateTaskDetailsAndLatLongitude() {

        showBusyAnimation("");

        RequestBody taskID = null,lat=null,longi=null,work_details=null,installRB=null,inspectionRB=null,periodic_inspectionRB=null,regular_maintenanceRB=null,battery_replacementRB=null,device_replacementRB=null;
        try {
            taskID = RequestBody.create(MediaType.parse("text/plain"), ApplicationClass.getTaskId());
            work_details = RequestBody.create(MediaType.parse("text/plain"), edtInput.getText().toString());
            lat = RequestBody.create(MediaType.parse("text/plain"), latitude);
            longi = RequestBody.create(MediaType.parse("text/plain"), longitude);

            installRB =RequestBody.create(MediaType.parse("text/plain"), install);
            inspectionRB =RequestBody.create(MediaType.parse("text/plain"), inspection);
            periodic_inspectionRB =RequestBody.create(MediaType.parse("text/plain"), periodic_inspection);

          //  regular_maintenanceRB =RequestBody.create(MediaType.parse("text/plain"),regular_maintence );
            battery_replacementRB =RequestBody.create(MediaType.parse("text/plain"), battery_replacement);
            device_replacementRB =RequestBody.create(MediaType.parse("text/plain"),device_replacement );
        } catch (Exception e) {
            e.printStackTrace();
        }


        String acess_token = ConstantProject.API_HEADER + SharedPreferenceUtility.getAccessToken();


        apiService.updateWorkDetailsLatitudeAndLongitude(acess_token,taskID,work_details,lat,longi,installRB,inspectionRB,periodic_inspectionRB,battery_replacementRB,device_replacementRB).enqueue(new Callback<UpdateTaskResponseModel>() {
            @Override
            public void onResponse(Call<UpdateTaskResponseModel> call, Response<UpdateTaskResponseModel> response) {

                if (response.code() == 200) {
                    Boolean status = true;
                    if (status == true && response.body().getData() != null) {

                        Intent intent=new Intent(WorkDetailsActivity.this,SummaryActivity.class);

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


    public boolean validateLatitudeAndLongitude(String  latLong)
    {

        if (latLong==null)
        {
            return false;
        }
        else if (latLong.isEmpty())
        {
            return false;
        }
        else if (!latLong.contains(","))
        {
            return false;
        }
        else {

            String[] separated = latLong.split(",");
            // value of N

            latitude = separated[0];
            longitude = separated[1];
            return true;

        }


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

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }


    public void showAlertSingleActionDialog( String alertMessage) {

        View dialogView = (CardView) this.getLayoutInflater().inflate(R.layout.custom_alert_single_dialog, null, false);


        TextView tv_alert_message = (TextView) dialogView.findViewById(R.id.tv_alert_message);
        Button ok = (Button) dialogView.findViewById(R.id.bt_ok);


        tv_alert_message.setText(alertMessage);


        AlertDialog.Builder builder = new AlertDialog.Builder(this).setView(dialogView).setCancelable(false);
        final AlertDialog dialog = builder.create();

        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorAccent));




        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                dialog.dismiss();


            }
        });

    }




}

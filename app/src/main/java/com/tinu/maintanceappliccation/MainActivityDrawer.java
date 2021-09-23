package com.tinu.maintanceappliccation;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.tinu.maintanceappliccation.activity.BaseActivity;
import com.tinu.maintanceappliccation.activity.ScanActivity;
import com.tinu.maintanceappliccation.adapter.DeviceSpinnerAdapter;
import com.tinu.maintanceappliccation.adapter.TaskListRecyclerViewAdapter;
import com.tinu.maintanceappliccation.models.HashApiResponse;
import com.tinu.maintanceappliccation.models.NarestLocationsModel;
import com.tinu.maintanceappliccation.models.TaskListModel;
import com.tinu.maintanceappliccation.restApiCall.ApiClient;
import com.tinu.maintanceappliccation.restApiCall.ApiInterface;
import com.tinu.maintanceappliccation.utility.ConstantProject;
import com.tinu.maintanceappliccation.utility.DialogManager;
import com.tinu.maintanceappliccation.utility.SharedPreferenceUtility;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class MainActivityDrawer extends BaseActivity {
    TaskListRecyclerViewAdapter taskListRecyclerViewAdapter;
    List<TaskListModel.Data> taskListModelList;
    RecyclerView rvTaskList;
    private String languageToLoad;
    DeviceSpinnerAdapter deviceSpinnerAdapter;
    DeviceSpinnerAdapter TypeSpinnerAdapter;
    List<String> location = new ArrayList<>();
    List<String> typeList = new ArrayList<>();
    private static final int REQUEST_LOCATION = 1;
    private ArrayList permissionsToRequest;
    private ArrayList permissionsRejected = new ArrayList();
    private ArrayList permissions = new ArrayList();
    private final static int ALL_PERMISSIONS_RESULT = 101;
    String DeviceNameBLE = "";
    Spinner sp_location,sp_types;
    static final int MY_PERMISSIONS_REQUEST_CAMERA = 1242;
    int PERMISSION_ID = 44;
    private FusedLocationProviderClient mFusedLocationClient;
    ApiInterface apiService;
    LinearLayout ll_fl_main;
    int position_location=0,position_type=0;
    String type="";
    List<NarestLocationsModel.Data> nearestLocationList;
    boolean isLocationcalled;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

        setContentView(R.layout.activity_main_drawer);
        ApplicationClass.setCurrentContext(this);
        onCreateDrawer();
        init();
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            if (bundle.getString(ConstantProject.isFrom).equalsIgnoreCase(ConstantProject.SummaryActivity)) {

                SharedPreferenceUtility.saveMoveCount(3);
                LinearLayout ll_fl_main = findViewById(R.id.ll_fl_main);
                ll_fl_main.setVisibility(View.VISIBLE);

                LinearLayout ll_app_bar = findViewById(R.id.ll_app_bar);
                ll_app_bar.setVisibility(View.GONE);
                rvTaskList.setVisibility(View.GONE);
                Fragment fragment = new CodeScannerFragment();

                getSupportFragmentManager().beginTransaction()

                        .replace(R.id.fl_main, fragment,

                                fragment.getClass().getSimpleName()).commit();
            }

        }
        //  sentAppLog("Testing");


        //“P:X=15,Y=5,Z=5”.

        ///  getComposeHashmsg("CP001-00000002", "close", "2", "4", "29");

        ApplicationClass.setDevice_name(getDeviceName());
        ApplicationClass.setDevice_version(deviceVersion());
    }

    private void init() {
        LinearLayout ll_app_bar = findViewById(R.id.ll_app_bar);
        ll_app_bar.setVisibility(View.VISIBLE);
        apiService = ApiClient.getClient().create(ApiInterface.class);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setVisibility(View.VISIBLE);
        taskListModelList = new ArrayList<>();
        ll_fl_main = (LinearLayout) findViewById(R.id.ll_fl_main);
        nearestLocationList = new ArrayList<>();

        //rv
        rvTaskList = (RecyclerView) findViewById(R.id.rv_error_notification);
        rvTaskList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ApplicationClass.getCurrentContext());
        rvTaskList.setLayoutManager(linearLayoutManager);
        ll_fl_main.setVisibility(View.GONE);
        sp_location = (Spinner) findViewById(R.id.sp_location);
        sp_types = (Spinner) findViewById(R.id.sp_types);

        typeList=new ArrayList<>();
        typeList.add(ApplicationClass.getCurrentContext().getResources().getString(R.string.staticAll));

        typeList.add(ApplicationClass.getCurrentContext().getResources().getString(R.string.install));
        typeList.add(ApplicationClass.getCurrentContext().getResources().getString(R.string.periodic_inspection));
        typeList.add(ApplicationClass.getCurrentContext().getResources().getString(R.string.repair));
        typeList.add(ApplicationClass.getCurrentContext().getResources().getString(R.string.lblBtryReplce));

        typeList.add(ApplicationClass.getCurrentContext().getResources().getString(R.string.device_replacment));

        TypeSpinnerAdapter = new DeviceSpinnerAdapter(ApplicationClass.getCurrentContext(), typeList);
        sp_types.setAdapter(TypeSpinnerAdapter);


        sp_location.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // getDeviceByLocation(position);

                position_location=position;

                    getTaskList();


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        sp_types.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // getDeviceByLocation(position);

                position_type=position;
                if (position==1)
                {
                 type="install";
                }
                else if (position==2)
                {
                    type="periodic inspection";
                }
                else if (position==3)
                {

                    type="inspection work";

                }
                else if (position==4)
                {

                    type="btry_replacement";

                }
                else if (position==5)
                {

                    type="device replacement";

                }
                SharedPreferenceUtility.saveSelectedType(type);
                getTaskList();


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);


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

                        getNearestLocation();
                    }
                });
                /////
            } else if (requestCode == 1) {
            }
        }
        switch (requestCode) {

            case ALL_PERMISSIONS_RESULT:
                for (Object perms : permissionsToRequest) {
                    if (!hasPermission((String) perms)) {
                        permissionsRejected.add(perms);
                    }
                }

                if (permissionsRejected.size() > 0) {


                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (shouldShowRequestPermissionRationale(String.valueOf(permissionsRejected.get(0)))) {
                            showMessageOKCancel(ApplicationClass.getCurrentContext().getResources().getString(R.string.permision),
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                requestPermissions((String[]) permissionsRejected.toArray(new String[permissionsRejected.size()]), ALL_PERMISSIONS_RESULT);
                                            }
                                        }
                                    });
                            return;
                        }
                    }

                }

                break;
            case MY_PERMISSIONS_REQUEST_CAMERA:
                if (ActivityCompat.checkSelfPermission(ApplicationClass.getCurrentContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)
                   /* try {
                        mCameraManager.openCamera(mCameraIDsList[1], mCameraStateCB, new Handler());
                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                    }*/
                    break;
        }

    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(ApplicationClass.getCurrentContext())
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
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
                                    getNearestLocation();


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
    private void requestNewLocationData() {

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
            getNearestLocation();

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
                            getNearestLocation();


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

    private void getTaskList() {

        showBusyAnimation("");

        String acess_token = ConstantProject.API_HEADER + SharedPreferenceUtility.getAccessToken();
        Map<String, Object> candidateMap = new HashMap<>();
        candidateMap.put("latitude", ApplicationClass.getCurrentLatitude());
        candidateMap.put("longitude", ApplicationClass.getCurrentLogitude());
        if (position_location>0)
        {
            candidateMap.put("landmark",nearestLocationList.get(position_location-1).getLandmark());
        }

        if (position_type>0)
        {
            candidateMap.put("type",type);
        }

        apiService.getTaskList(acess_token, candidateMap).enqueue(new Callback<TaskListModel>() {
            @Override
            public void onResponse(Call<TaskListModel> call, Response<TaskListModel> response) {

                if (response.code() == 200) {
                    Boolean status = true;
                    if (status == true && response.body().getData() != null) {
                        taskListModelList.clear();
                        rvTaskList.setAdapter(null);
                        taskListModelList.addAll(response.body().getData());


                        taskListRecyclerViewAdapter = new TaskListRecyclerViewAdapter(ApplicationClass.getCurrentContext(), taskListModelList, new TaskListRecyclerViewAdapter.CustomOnClickListener() {

                            @Override
                            public void OrderDetails(TaskListModel.Data taskListModel) {
                                //  ConstantProject.PENDING
                                //  ApplicationClass.setTaskId(taskListModel.getTaskID)
                                ApplicationClass.setTaskListDeviceId(taskListModel.getDevice_id());
                                if (taskListModel.getType().equalsIgnoreCase(ConstantProject.TaskTypeRepair) ||taskListModel.getType().equalsIgnoreCase(ConstantProject.TaskTypeBatteryRelacement)) {
                                    SharedPreferenceUtility.saveMoveCount(2);
                                } else {
                                    SharedPreferenceUtility.saveMoveCount(1);
                                }
                                Intent intent = new Intent(MainActivityDrawer.this, ScanActivity.class);
                                intent.putExtra(ConstantProject.isFrom, ConstantProject.TaskListRecyclerViewAdapter);
                                startActivity(intent);

                            }


                        });
                        rvTaskList.setAdapter(taskListRecyclerViewAdapter);
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
            public void onFailure(Call<TaskListModel> call, Throwable t) {
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


    public boolean validateLatitudeAndLongitude(String latLong) {
        String latitude, longitude;
        if (latLong == null) {
            return false;
        } else if (latLong.isEmpty()) {
            return false;
        } else if (!latLong.contains(",")) {
            return false;
        } else {

            String[] separated = latLong.split(",");
            // value of N

            latitude = separated[0];
            longitude = separated[1];
            Log.e("LATi", latitude);
            Log.e("Logi", longitude);
            return true;

        }


    }



    public void getNearestLocation() {
        Map<String, Object> candidateMap = new HashMap<>();
        candidateMap.put("latitude", ApplicationClass.getCurrentLatitude());
        candidateMap.put("longitude", ApplicationClass.getCurrentLogitude());

        String acess_token = ConstantProject.API_HEADER + SharedPreferenceUtility.getAccessToken();

        apiService.getNearestLocations(acess_token, candidateMap).enqueue(new Callback<NarestLocationsModel>() {
            @Override
            public void onResponse(Call<NarestLocationsModel> call, Response<NarestLocationsModel> response) {
                if (response.code() == 200) {
                    nearestLocationList.addAll(response.body().getDataList());
                    location.clear();
                    location.add(ApplicationClass.getCurrentContext().getResources().getString(R.string.staticAll));
                    if (nearestLocationList != null && nearestLocationList.size() > 0) {
                        for (int i = 0; i < nearestLocationList.size(); i++) {
                            location.add(nearestLocationList.get(i).getLandmark());
                        }
                        deviceSpinnerAdapter = new DeviceSpinnerAdapter(ApplicationClass.getCurrentContext(), location);
                        sp_location.setAdapter(deviceSpinnerAdapter);


                        // getDeviceByLocation(0);
                        hideBusyAnimation();
                    }
                    else
                    {
                        hideBusyAnimation();

                        deviceSpinnerAdapter = new DeviceSpinnerAdapter(ApplicationClass.getCurrentContext(), location);
                        sp_location.setAdapter(deviceSpinnerAdapter);
                    }

                }
                else {
                    DialogManager.showAlertSingleActionDialog(ApplicationClass.getCurrentActivity().getResources().getString(R.string.OperfailedExtnd), new DialogManager.IUniActionDialogOnClickListener() {
                        @Override
                        public void onPositiveClick() {

                        }
                    });
                }


            }


            @Override
            public void onFailure(Call<NarestLocationsModel> call, Throwable t) {
                DialogManager.showAlertSingleActionDialog(t.getMessage(), new DialogManager.IUniActionDialogOnClickListener() {
                    @Override
                    public void onPositiveClick() {

                    }
                });
                if (!BaseActivity.isNetworkConnectionAvailable(ApplicationClass.getCurrentContext())) {
                    Toast.makeText(ApplicationClass.getCurrentContext(), ApplicationClass.getCurrentContext().getResources().getString(R.string.OperfailedExtnd), Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(ApplicationClass.getCurrentContext(), R.string.OperfailedExtnd, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public static String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return capitalize(model);
        }
        return capitalize(manufacturer) + " " + model;
    }

    public static String capitalize(String str) {
        if (TextUtils.isEmpty(str)) {
            return str;
        }
        char[] arr = str.toCharArray();
        boolean capitalizeNext = true;

        StringBuilder phrase = new StringBuilder();
        for (char c : arr) {
            if (capitalizeNext && Character.isLetter(c)) {
                phrase.append(Character.toUpperCase(c));
                capitalizeNext = false;
                continue;
            } else if (Character.isWhitespace(c)) {
                capitalizeNext = true;
            }
            phrase.append(c);
        }

        return phrase.toString();
    }

    public String deviceVersion()
    {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        int version = Build.VERSION.SDK_INT;
        String versionRelease = Build.VERSION.RELEASE;
        String Appversion="";
        try {
            PackageInfo pInfo = ApplicationClass.getCurrentContext().getPackageManager().getPackageInfo(getPackageName(), 0);
            Appversion = pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        String  data= "manufacturer :"+manufacturer+", "
                +" model :"+model+", "+
                " Android version :"+versionRelease
                +" Maintence App Version :"+ Appversion;
        return data;


    }

}

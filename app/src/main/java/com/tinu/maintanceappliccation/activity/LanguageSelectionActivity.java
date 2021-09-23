package com.tinu.maintanceappliccation.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;


import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.tinu.maintanceappliccation.ApplicationClass;
import com.tinu.maintanceappliccation.R;
import com.tinu.maintanceappliccation.utility.SharedPreferenceUtility;

import java.util.ArrayList;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

/*import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;*/

public class LanguageSelectionActivity extends BaseActivity {
    private Button button_get_started;
    private RadioButton radioJapanese,radioEnglish;
    TextView tv_welcome;
    private static final int REQUEST_CAMERA_PERMISSION = 201;
    static final int MY_PERMISSIONS_REQUEST_CAMERA = 1242;
    int PERMISSION_ID = 44;
    private FusedLocationProviderClient mFusedLocationClient;
    private final static int ALL_PERMISSIONS_RESULT = 101;
    private ArrayList permissionsRejected = new ArrayList();
    private static final int REQUEST_LOCATION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         setContentView(R.layout.activity_language_selection);
        ApplicationClass.setCurrentContext(this);
        init();
        clicks();
    }

    public void init() {
        button_get_started = (Button) findViewById(R.id.button_get_started);
        radioJapanese = (RadioButton)findViewById(R.id.radio_japanese);
        radioEnglish = (RadioButton)findViewById(R.id.radio_english);
        tv_welcome =(TextView)findViewById(R.id.tv_welcome);
        button_get_started =(Button)findViewById(R.id.button_get_started);
        radioEnglish.setChecked(true);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        //getLastLocation();
        getLocation();

    }

    public void clicks() {


        radioEnglish.setChecked(true);
        SharedPreferenceUtility.saveIsEnglish(true);

        radioEnglish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tv_welcome.setText("Welcome to Maintenance \nApplication");
                button_get_started.setText("Get Started");

                SharedPreferenceUtility.saveIsEnglish(true);

                radioJapanese.setChecked(false);
            }
        });
        radioJapanese.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tv_welcome.setText("ようこそ保守 \nアプリケーションへ");
                button_get_started.setText("開始");
                SharedPreferenceUtility.saveIsEnglish(false);
                radioEnglish.setChecked(false);
            }
        });
        button_get_started.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(LanguageSelectionActivity.this,LoginActivity.class);
                startActivity(intent);
            }
        });
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
    public void applyOverrideConfiguration(Configuration overrideConfiguration) {
        if (overrideConfiguration != null) {
            int uiMode = overrideConfiguration.uiMode;
            overrideConfiguration.setTo(getBaseContext().getResources().getConfiguration());
            overrideConfiguration.uiMode = uiMode;
        }
        super.applyOverrideConfiguration(overrideConfiguration);
    }
}




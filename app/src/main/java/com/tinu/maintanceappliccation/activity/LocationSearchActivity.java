package com.tinu.maintanceappliccation.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.tinu.maintanceappliccation.ApplicationClass;
import com.tinu.maintanceappliccation.R;
import com.tinu.maintanceappliccation.adapter.DeviceSpinnerAdapter;
import com.tinu.maintanceappliccation.models.DevicesByLocationModel;
import com.tinu.maintanceappliccation.models.NarestLocationsModel;
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
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static androidx.core.content.ContextCompat.checkSelfPermission;

public class LocationSearchActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    Spinner sp_device, sp_location;
    ImageView iv_back;
    // String[] device = { "CP001-2C28", "CP002", "CP003", "CP004", "CP005"};
    // String[] location = { "Tokyo", "Kyoto", "Osaka", "Nara", "Fuji san"};
    DeviceSpinnerAdapter deviceSpinnerAdapter;
    List<String> device = new ArrayList<>();
    List<String> location = new ArrayList<>();
    ApiInterface apiService;
    static final int MY_PERMISSIONS_REQUEST_CAMERA = 1242;
    int PERMISSION_ID = 44;
    private FusedLocationProviderClient mFusedLocationClient;
    private final static int ALL_PERMISSIONS_RESULT = 101;
    private ArrayList permissionsRejected = new ArrayList();
    private static final int REQUEST_LOCATION = 1;
    private ArrayList permissionsToRequest;
    List<DevicesByLocationModel.FreeDevices> freeDevicesList;
    List<NarestLocationsModel.Data> nearestLocationList;
    MapView mMapView;
    private ArrayList permissions = new ArrayList();

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

        setContentView(R.layout.activity_location_search);
        ApplicationClass.setCurrentContext(this);
        init();

    }

    private void init() {
        apiService = ApiClient.getClient().create(ApiInterface.class);

        freeDevicesList = new ArrayList<>();
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        nearestLocationList = new ArrayList<>();
        mMapView = (MapView) findViewById(R.id.mapView);

        sp_device = (Spinner) findViewById(R.id.sp_device);
        sp_location = (Spinner) findViewById(R.id.sp_location);
        iv_back = (ImageView) findViewById(R.id.iv_back);
        //Creating the ArrayAdapter instance having the country list
        ActivityCompat.requestPermissions(LocationSearchActivity.this,
                new String[]{ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        permissions.add(ACCESS_FINE_LOCATION);
        permissions.add(ACCESS_COARSE_LOCATION);
        permissionsToRequest = findUnAskedPermissions(permissions);


        sp_location.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                freeDevicesList =new ArrayList<>();
                getDeviceByLocation(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        sp_device.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                LatLng marker = new LatLng(Double.valueOf(freeDevicesList.get(position).getLatitude()), Double.valueOf(freeDevicesList.get(position).getLongitude()));
                mMap.addMarker(new MarkerOptions().position(marker).title(freeDevicesList.get(position).getLandmark()));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(marker));

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.

        sp_device.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {


            if (permissionsToRequest.size() > 0)
                requestPermissions((String[]) permissionsToRequest.toArray(new String[permissionsToRequest.size()]), ALL_PERMISSIONS_RESULT);
        }


        mMapView.onResume();
        try {
            MapsInitializer.initialize(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
        getNearestLocation();

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
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(ApplicationClass.getCurrentActivity(), new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            ApplicationClass.setCurrentLatitude("" + location.getLatitude());
                            ApplicationClass.setCurrentLogitude("" + location.getLongitude());
                        }
                    }
                });

        if (checkSelfPermission(
                 ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(
                ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(ApplicationClass.getCurrentActivity(), new String[]{ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        }


    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng marker = new LatLng(Double.valueOf(ApplicationClass.getCurrentLatitude()), Double.valueOf(ApplicationClass.getCurrentLogitude()));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(marker));
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
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

    public void getNearestLocation() {
        location=new ArrayList<>();

        Map<String, Object> candidateMap = new HashMap<>();
        candidateMap.put("latitude", ApplicationClass.getCurrentLatitude());
        candidateMap.put("longitude", ApplicationClass.getCurrentLogitude());

        String acess_token = ConstantProject.API_HEADER + SharedPreferenceUtility.getAccessToken();

        apiService.getNearestLocations(acess_token, candidateMap).enqueue(new Callback<NarestLocationsModel>() {
            @Override
            public void onResponse(Call<NarestLocationsModel> call, Response<NarestLocationsModel> response) {

                nearestLocationList.addAll(response.body().getDataList());
                location.clear();
                if (nearestLocationList != null && nearestLocationList.size() > 0) {
                    for (int i = 0; i < nearestLocationList.size(); i++) {
                        location.add(nearestLocationList.get(i).getLandmark());
                    }
                    deviceSpinnerAdapter = new DeviceSpinnerAdapter(ApplicationClass.getCurrentContext(), location);
                    sp_location.setAdapter(deviceSpinnerAdapter);


                    getDeviceByLocation(0);
                }


            }


            @Override
            public void onFailure(Call<NarestLocationsModel> call, Throwable t) {
                if (!BaseActivity.isNetworkConnectionAvailable(ApplicationClass.getCurrentContext())) {
                    Toast.makeText(ApplicationClass.getCurrentContext(), ApplicationClass.getCurrentContext().getResources().getString(R.string.OperfailedExtnd), Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(ApplicationClass.getCurrentContext(), R.string.OperfailedExtnd, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void getDeviceByLocation(int position) {
        if (nearestLocationList != null && nearestLocationList.size() > 0) {

            Map<String, Object> candidateMap = new HashMap<>();
            candidateMap.put("landmark", nearestLocationList.get(position).getLandmark());

            String acess_token = ConstantProject.API_HEADER + SharedPreferenceUtility.getAccessToken();

            apiService.getDeviceByLocation(acess_token, candidateMap).enqueue(new Callback<DevicesByLocationModel>() {
                @Override
                public void onResponse(Call<DevicesByLocationModel> call, Response<DevicesByLocationModel> response) {

                    device.clear();
                    if (response.body().getData() != null) {
                        if (response.body().getData().getFree_devices() != null) {
                            freeDevicesList.addAll(response.body().getData().getFree_devices());
                            device.clear();
                            for (int i = 0; i < freeDevicesList.size(); i++) {
                                device.add(freeDevicesList.get(i).getDevice_name());
                            }
                            deviceSpinnerAdapter = new DeviceSpinnerAdapter(ApplicationClass.getCurrentContext(), device);
                            sp_device.setAdapter(deviceSpinnerAdapter);

                            mMapView.getMapAsync(new OnMapReadyCallback() {
                                @Override
                                public void onMapReady(GoogleMap mMapp) {
                                    mMap = mMapp;
                                    if (checkSelfPermission(
                                            ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(
                                            ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                        ActivityCompat.requestPermissions(LocationSearchActivity.this, new String[]{ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
                                    }
                                    mMap.setMyLocationEnabled(true);


                                    if(mMap !=null) {


                                        for (int i = 0; i < freeDevicesList.size(); i++) {
                                            // Location location = locationDataList.get(i).getLocation();
                                            if (freeDevicesList.get(i).getLatitude()!=null && freeDevicesList.get(i).getLongitude()!=null)
                                            {
                                                LatLng position = new LatLng(Double.valueOf(freeDevicesList.get(i).getLatitude()), Double.valueOf(freeDevicesList.get(i).getLongitude()));
                                                mMap.addMarker(new MarkerOptions().position(position).title(freeDevicesList.get(i).getLandmark()).snippet(""));
                                                if (i == 0) {
                                                    CameraPosition camPos = new CameraPosition(position, 12, 0, 0);
                                                    mMap.moveCamera(CameraUpdateFactory.newCameraPosition(camPos));
                                                    // fillTextViews(location);
                                                }
                                            }

                                        }





                                        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                                            @Override
                                            public View getInfoWindow(Marker marker) {
                                                return null;
                                            }

                                            @Override
                                            public View getInfoContents(Marker marker) {

                                                View v = getLayoutInflater().inflate(R.layout.info_window,null);
                                                TextView showTitle = (TextView)  v.findViewById(R.id.showTitle);
                                                TextView showSnappest = (TextView)v.findViewById(R.id.showSnappest);


                                                LatLng latLng = marker.getPosition();
                                                showTitle.setText(marker.getTitle());
                                                showSnappest.setText(marker.getSnippet());
                                                // showLat.setText("Latitude:"+latLng.latitude);
                                                // showLong.setText("Longitude:"+latLng.longitude);


                                                return v;
                                            }
                                        });



                                    }
                                }
                            });

                        }
                    }

                }


                @Override
                public void onFailure(Call<DevicesByLocationModel> call, Throwable t) {
                    if (!BaseActivity.isNetworkConnectionAvailable(ApplicationClass.getCurrentContext())) {
                        Toast.makeText(ApplicationClass.getCurrentContext(), ApplicationClass.getCurrentContext().getResources().getString(R.string.OperfailedExtnd), Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(ApplicationClass.getCurrentContext(), R.string.OperfailedExtnd, Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getLastLocation();

    }

    ///Location

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

    private ArrayList findUnAskedPermissions(ArrayList wanted) {
        ArrayList result = new ArrayList();

        for (Object perm : wanted) {
            if (!hasPermission((String) perm)) {
                result.add(perm);
            }
        }

        return result;
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

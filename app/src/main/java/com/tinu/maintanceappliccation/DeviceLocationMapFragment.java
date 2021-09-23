package com.tinu.maintanceappliccation;

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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.tinu.maintanceappliccation.activity.BaseActivity;
import com.tinu.maintanceappliccation.activity.LocationSearchActivity;
import com.tinu.maintanceappliccation.adapter.DeviceSpinnerAdapter;
import com.tinu.maintanceappliccation.models.DevicesByLocationModel;
import com.tinu.maintanceappliccation.models.NarestLocationsModel;
import com.tinu.maintanceappliccation.restApiCall.ApiClient;
import com.tinu.maintanceappliccation.restApiCall.ApiInterface;
import com.tinu.maintanceappliccation.restApiCall.AppController;
import com.tinu.maintanceappliccation.utility.ConstantProject;
import com.tinu.maintanceappliccation.utility.SharedPreferenceUtility;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static androidx.core.content.ContextCompat.checkSelfPermission;


public class DeviceLocationMapFragment extends Fragment implements OnMapReadyCallback {
    int PERMISSION_ID = 44;
    GoogleMap googleMap;
    MapView mMapView;
    ApiInterface apiService;



    Spinner sp_device, sp_location;
    ImageView iv_back;
    // String[] device = { "CP001-2C28", "CP002", "CP003", "CP004", "CP005"};
    // String[] location = { "Tokyo", "Kyoto", "Osaka", "Nara", "Fuji san"};
    DeviceSpinnerAdapter deviceSpinnerAdapter;
    List<String> device = new ArrayList<>();
    List<String> location = new ArrayList<>();
    List<DevicesByLocationModel.FreeDevices> freeDevicesList;
    List<NarestLocationsModel.Data> nearestLocationList;


    LocationManager locationManager;
    String latitude_, longitude_;
    private static final int REQUEST_LOCATION = 1;
    FusedLocationProviderClient mFusedLocationClient;


// location

    private ArrayList permissionsToRequest;
    private ArrayList permissionsRejected = new ArrayList();
    private ArrayList permissions = new ArrayList();
    private final static int ALL_PERMISSIONS_RESULT = 101;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

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

        return inflater.inflate(R.layout.activity_location_search, null);


    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        apiService = ApiClient.getClient().create(ApiInterface.class);
        mMapView = (MapView) view.findViewById(R.id.mapView);
        if (mMapView != null)
            mMapView.onCreate(savedInstanceState);
        ActivityCompat.requestPermissions(getActivity(),
                new String[]{ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        permissions.add(ACCESS_FINE_LOCATION);
        permissions.add(ACCESS_COARSE_LOCATION);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());

        permissionsToRequest = findUnAskedPermissions(permissions);
        //get the permissions we have asked for before but are not granted..
        //we will store this in a global list to access later.

        freeDevicesList = new ArrayList<>();
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        nearestLocationList = new ArrayList<>();
        mMapView = (MapView) view.findViewById(R.id.mapView);

        sp_device = (Spinner) view.findViewById(R.id.sp_device);
        sp_location = (Spinner) view.findViewById(R.id.sp_location);
        iv_back = (ImageView) view.findViewById(R.id.iv_back);

        sp_location.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                getDeviceByLocation(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        sp_device.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

               /* LatLng marker = new LatLng(Double.valueOf(freeDevicesList.get(position).getLatitude()), Double.valueOf(freeDevicesList.get(position).getLongitude()));
                googleMap.addMarker(new MarkerOptions().position(marker).title(freeDevicesList.get(position).getLocation()));
                googleMap.moveCamera(CameraUpdateFactory.newLatLng(marker));*/

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);


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
                Intent intent = new Intent(ApplicationClass.getCurrentContext(), MainActivityDrawer.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                startActivity(intent);
            }
        });
        if (checkSelfPermission(getContext(),Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(getContext(),Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {


            if (permissionsToRequest.size() > 0)
                requestPermissions((String[]) permissionsToRequest.toArray(new String[permissionsToRequest.size()]), ALL_PERMISSIONS_RESULT);
        }


        mMapView.onResume();

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                googleMap = mMap;
                if (checkSelfPermission(
                        getContext(), ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(
                        getContext(), ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
                }
                googleMap.setMyLocationEnabled(true);


                if (googleMap != null) {

                         for (int i = 0; i < freeDevicesList.size(); i++) {
                            // Location location = locationDataList.get(i).getLocation();
                            if (freeDevicesList.get(i).getLatitude() != null && freeDevicesList.get(i).getLongitude() != null) {
                                LatLng position = new LatLng(Double.valueOf(freeDevicesList.get(i).getLatitude()), Double.valueOf(freeDevicesList.get(i).getLongitude()));
                                googleMap.addMarker(new MarkerOptions().position(position).title(freeDevicesList.get(i).getLandmark()).snippet(""));
                                if (i == 0) {
                                    CameraPosition camPos = new CameraPosition(position, 12, 0, 0);
                                    googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(camPos));
                                    // fillTextViews(location);
                                }
                            }

                        }



                    googleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                        @Override
                        public View getInfoWindow(Marker marker) {
                            return null;
                        }

                        @Override
                        public View getInfoContents(Marker marker) {

                            View v = getLayoutInflater().inflate(R.layout.info_window, null);
                            TextView showTitle = (TextView) v.findViewById(R.id.showTitle);
                            TextView showSnappest = (TextView) v.findViewById(R.id.showSnappest);


                            LatLng latLng = marker.getPosition();
                            showTitle.setText(marker.getTitle());
                            showSnappest.setText(marker.getSnippet());
                            // showLat.setText("Latitude:"+latLng.latitude);
                            // showLong.setText("Longitude:"+latLng.longitude);

                                /*if (marker.getTitle() != null && !marker.getTitle().isEmpty()) {
                                    for (int i = 0; i < locationDataList.size(); i++) {
                                        if (locationDataList.get(i).getLocation().equals(marker.getTitle())) {
                                            tvPlace.setText("\t" + locationDataList.get(i).getLocation());

                                            getDevicesByLocation(locationDataList.get(i).getLocation_id());
                                            CarryParkApplication.setPlace(locationDataList.get(i).getLocation());
                                            CarryParkApplication.setDepositeTime(locationDataList.get(i).getStart_time());
                                        }
                                    }
                                }*/


                            return v;
                        }
                    });


                }
            }
        });

        if (checkSelfPermission(
                getContext(), ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(
                getContext(), ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        }


        getLastLocation();
        getLocation();
        getNearestLocation();
        //tinu
        // getLocationDetails("9.672370","76.568670");

    }

    public void getLocation() {


        if (checkSelfPermission(getContext(),Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(getContext(),Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
                            //  getLocationDetails(CarryParkApplication.getLatitude(),CarryParkApplication.getLongitude());


                        }
                    }
                });



    }
    @SuppressLint("MissingPermission")
    private void getLastLocation(){
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
                                    ApplicationClass.setCurrentLatitude(""+location.getLatitude());
                                    ApplicationClass.setCurrentLogitude(""+location.getLongitude());


                                }
                            }
                        }
                );
            } else {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
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

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        mFusedLocationClient.requestLocationUpdates(
                mLocationRequest, mLocationCallback,
                Looper.myLooper()
        );

    }

    private LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();
            ApplicationClass.setCurrentLatitude(""+mLastLocation.getLatitude());
            ApplicationClass.setCurrentLogitude(""+mLastLocation.getLongitude());

        }
    };
    private boolean checkPermissions(){
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            return true;
        }
        return false;
    }
    private void requestPermissions(){
        ActivityCompat.requestPermissions(
                getActivity(),
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                PERMISSION_ID
        );
    }
    private boolean isLocationEnabled(){
        LocationManager locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER
        );
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
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

    private boolean hasPermission(String permission) {
        if (canMakeSmores()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return (checkSelfPermission(getContext(),permission) == PackageManager.PERMISSION_GRANTED);
            }
        }
        return true;
    }

    private boolean canMakeSmores() {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
    }


    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

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
                            showMessageOKCancel("These permissions are mandatory for the application. Please allow access.",
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
        }

    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(getContext())
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {

        if(googleMap !=null) {
            googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    if (marker.getTitle() != null && marker.getTitle().isEmpty()) {
                            for (int i = 0; i < freeDevicesList.size(); i++) {
                                // Location location = locationDataList.get(i).getLocation();
                                if (freeDevicesList.get(i).getLatitude()!=null && freeDevicesList.get(i).getLongitude()!=null)
                                {
                                    LatLng position = new LatLng(Double.valueOf(freeDevicesList.get(i).getLatitude()), Double.valueOf(freeDevicesList.get(i).getLongitude()));
                                    googleMap.addMarker(new MarkerOptions().position(position).title(freeDevicesList.get(i).getLandmark()).snippet(""));
                                    if (i == 0) {
                                        CameraPosition camPos = new CameraPosition(position, 12, 0, 0);
                                        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(camPos));
                                        // fillTextViews(location);
                                    }
                                }

                            }



                    }

                    return true;
                }
            });
        }

    }



    /// API calles

    public void getNearestLocation() {

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


                   // getDeviceByLocation(0);
                    ((BaseActivity) getActivity()).hideBusyAnimation();
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
        ((BaseActivity) getActivity()).showBusyAnimation("");
        freeDevicesList.clear();
        if (nearestLocationList != null && nearestLocationList.size() > 0) {

            Map<String, Object> candidateMap = new HashMap<>();
            candidateMap.put("landmark", nearestLocationList.get(position).getLandmark());

            String acess_token = ConstantProject.API_HEADER + SharedPreferenceUtility.getAccessToken();

            apiService.getDeviceByLocation(acess_token, candidateMap).enqueue(new Callback<DevicesByLocationModel>() {
                @Override
                public void onResponse(Call<DevicesByLocationModel> call, Response<DevicesByLocationModel> response) {
                    ((BaseActivity) getActivity()).hideBusyAnimation();
                    ((BaseActivity) getActivity()).hideBusyAnimation();
                    ((BaseActivity) getActivity()).hideBusyAnimation();
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
                                    googleMap = mMapp;
                                    if (checkSelfPermission(getActivity(),ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(
                                           getContext(), ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                        ActivityCompat.requestPermissions(ApplicationClass.getCurrentActivity(), new String[]{ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
                                    }
                                    googleMap.setMyLocationEnabled(true);


                                    if(googleMap !=null) {


                                        for (int i = 0; i < freeDevicesList.size(); i++) {
                                            // Location location = locationDataList.get(i).getLocation();
                                            if (freeDevicesList.get(i).getLatitude()!=null && freeDevicesList.get(i).getLongitude()!=null)
                                            {
                                                LatLng position = new LatLng(Double.valueOf(freeDevicesList.get(i).getLatitude()), Double.valueOf(freeDevicesList.get(i).getLongitude()));
                                                googleMap.addMarker(new MarkerOptions().position(position).title(freeDevicesList.get(i).getLandmark()).snippet(freeDevicesList.get(i).getAddress_1()));
                                                if (i == 0) {
                                                    CameraPosition camPos = new CameraPosition(position, 12, 0, 0);
                                                    googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(camPos));
                                                    // fillTextViews(location);
                                                }
                                            }

                                        }




                                        googleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
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


                                        googleMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
                                            @Override
                                            public void onCameraIdle() {
                                                //get latlng at the center by calling
                                                LatLng midLatLng = googleMap.getCameraPosition().target;
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
                    ((BaseActivity) getActivity()).hideBusyAnimation();
                    ((BaseActivity) getActivity()).hideBusyAnimation();
                    ((BaseActivity) getActivity()).hideBusyAnimation();
                    if (!BaseActivity.isNetworkConnectionAvailable(ApplicationClass.getCurrentContext())) {
                        Toast.makeText(ApplicationClass.getCurrentContext(), ApplicationClass.getCurrentContext().getResources().getString(R.string.OperfailedExtnd), Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(ApplicationClass.getCurrentContext(), R.string.OperfailedExtnd, Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }
}

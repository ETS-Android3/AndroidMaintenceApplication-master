package com.tinu.maintanceappliccation;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.zxing.Result;
import com.tinu.maintanceappliccation.activity.BaseActivity;
import com.tinu.maintanceappliccation.activity.DeviceDetailsActivity;
import com.tinu.maintanceappliccation.activity.HistoryActivity;
import com.tinu.maintanceappliccation.activity.ScanActivity;
import com.tinu.maintanceappliccation.models.HashApiResponse;
import com.tinu.maintanceappliccation.models.scanDeviceModel;
import com.tinu.maintanceappliccation.restApiCall.ApiClient;
import com.tinu.maintanceappliccation.restApiCall.ApiInterface;
import com.tinu.maintanceappliccation.utility.ConstantProject;
import com.tinu.maintanceappliccation.utility.DialogManager;
import com.tinu.maintanceappliccation.utility.SharedPreferenceUtility;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;
import static android.telephony.MbmsDownloadSession.RESULT_CANCELLED;
import static android.widget.Toast.LENGTH_SHORT;
import static com.tinu.maintanceappliccation.activity.BaseActivity.isNetworkConnectionAvailable;


public class CodeScannerFragment extends Fragment {

    private String languageToLoad;

    private CodeScanner mCodeScanner;

    private static final int REQUEST_CAMERA_PERMISSION = 201;
    CountDownTimer waitForConnecttoDevice;
    private String deviceList;




    // private CodeScanner mCodeScanner;
    ApiInterface apiService;


    //Camera
    static final String TAG = "CamTest";
    static final int MY_PERMISSIONS_REQUEST_CAMERA = 1242;
    private static final int MSG_CAMERA_OPENED = 1;
    private static final int MSG_SURFACE_READY = 2;

    // security
    BluetoothManager btManager;
    BluetoothAdapter btAdapter;
    BluetoothLeScanner btScanner;
    private final static int REQUEST_ENABLE_BT = 1;
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;




    private enum ScanState {NONE, LESCAN, DISCOVERY, DISCOVERY_FINISHED}

    private ScanState scanState = ScanState.NONE;
    private static final long LESCAN_PERIOD = 30000; // similar to bluetoothAdapter.startDiscovery
    private Handler leScanStopHandler = new Handler();
    private BluetoothAdapter.LeScanCallback leScanCallback;
    private BroadcastReceiver discoveryBroadcastReceiver;
    private IntentFilter discoveryIntentFilter;

    private Menu menu;
    private BluetoothAdapter bluetoothAdapter;
    private ArrayList<BluetoothDevice> listItems = new ArrayList<>();
    private ArrayAdapter<BluetoothDevice> listAdapter;
    String Address = "";
    ImageView iv_back;


    BluetoothAdapter mBluetoothAdapter;

    // connection
    private enum Connected {
        False, Pending, True
    }


    String isFrom = null;
    private boolean isPaymentCancell;

    private SerialService service;
    public CodeScannerFragment() {
        leScanCallback = (device, rssi, scanRecord) -> {
            if (device != null && getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    updateScan(device);
                });
            }
        };
        discoveryBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(BluetoothDevice.ACTION_FOUND)) {
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    if (device.getType() != BluetoothDevice.DEVICE_TYPE_CLASSIC && getActivity() != null) {
                        getActivity().runOnUiThread(() -> updateScan(device));
                    }
                }
                if (intent.getAction().equals((BluetoothAdapter.ACTION_DISCOVERY_FINISHED))) {
                    scanState = ScanState.DISCOVERY_FINISHED; // don't cancel again
                    stopScan();
                }
            }
        };
        discoveryIntentFilter = new IntentFilter();
        discoveryIntentFilter.addAction(BluetoothDevice.ACTION_FOUND);
        discoveryIntentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (getActivity().getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH))
            bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        listAdapter = new ArrayAdapter<BluetoothDevice>(getActivity(), 0, listItems) {
            @Override
            public View getView(int position, View view, ViewGroup parent) {
                BluetoothDevice device = listItems.get(position);
                if (view == null)
                    view = getActivity().getLayoutInflater().inflate(R.layout.device_list_item, parent, false);
                TextView text1 = view.findViewById(R.id.text1);
                TextView text2 = view.findViewById(R.id.text2);
                if (device.getName() == null || device.getName().isEmpty())
                    text1.setText("<unnamed>");
                else
                    text1.setText(device.getName());
                text2.setText(device.getAddress());
                return view;
            }
        };


    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
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
        getActivity().getBaseContext().getResources().updateConfiguration(config,
                getActivity().getBaseContext().getResources().getDisplayMetrics());


        return inflater.inflate(R.layout.fragment_scan, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        apiService =
                ApiClient.getClient().create(ApiInterface.class);


        ApplicationClass.setScannedDeviceCode("");

        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN); //show the activity in full screen


        CodeScannerView scannerView = view.findViewById(R.id.scanner_view);
        mCodeScanner = new CodeScanner(ApplicationClass.getCurrentActivity(), scannerView);
        mCodeScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull final Result result) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (!result.getText().equals("07152283"))
                        {
                            String msg = "";
                            if (!SharedPreferenceUtility.getIsEnglishLag()) {
                                msg = result.getText().toString()  + ApplicationClass.getCurrentContext().getResources().getString(R.string.deviceConfirmation);
                            } else {
                                msg = ApplicationClass.getCurrentContext().getResources().getString(R.string.deviceConfirmation)  + result.getText().toString();


                            }
                            DialogManager.showConfirmDialogue(ApplicationClass.getCurrentActivity(), msg, new DialogManager.IMultiActionDialogOnClickListener() {

                                @Override
                                public void onPositiveClick() {

                                    moveToInitialPaymentFragment(result.getText());
                                }

                                @Override
                                public void onNegativeClick() {
                                    ApplicationClass.setScannedDeviceCode(result.getText());
                                    Intent intent = new Intent(ApplicationClass.getCurrentContext(), MainActivityDrawer.class);
                                    startActivity(intent);
                                }


                            });



                        }

                        else {
                            DialogManager.showAlertSingleActionDialog(ApplicationClass.getCurrentActivity().getResources().getString(R.string.OperfailedExtnd), new DialogManager.IUniActionDialogOnClickListener() {
                                @Override
                                public void onPositiveClick() {

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

        mCodeScanner.setTouchFocusEnabled(false);
        mCodeScanner.setAutoFocusEnabled(true);
        initializeBluetooth();

        btManager = (BluetoothManager) getActivity().getSystemService(Context.BLUETOOTH_SERVICE);
        btAdapter = btManager.getAdapter();
        if (btAdapter!=null) {
            btScanner = btAdapter.getBluetoothLeScanner();

            btAdapter.enable();
        }

        if (btAdapter != null && !btAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        }
        iv_back =(ImageView) view.findViewById(R.id.iv_back);

        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(ApplicationClass.getCurrentContext(), MainActivityDrawer.class);

                intent.putExtra(ConstantProject.isFrom,ConstantProject.History);
                startActivity(intent);
            }
        });

        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            ((BaseActivity) getActivity()).hideBusyAnimation();

            DialogManager.showUniActionDialog(ApplicationClass.getCurrentActivity(), ApplicationClass.getCurrentContext().getResources().getString(R.string.bleNotavbl), "ok", new DialogManager.IUniActionDialogOnClickListener() {
                @Override
                public void onPositiveClick() {
                    Intent intent = new Intent(ApplicationClass.getCurrentContext(), MainActivityDrawer.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);

                }
            });
        } else if (!mBluetoothAdapter.isEnabled()) {
            ((BaseActivity) getActivity()).hideBusyAnimation();

            DialogManager.showUniActionDialog(ApplicationClass.getCurrentActivity(), ApplicationClass.getCurrentContext().getResources().getString(R.string.bleNotavbldesc), "ok", new DialogManager.IUniActionDialogOnClickListener() {
                @Override
                public void onPositiveClick() {
                    Intent intent = new Intent(ApplicationClass.getCurrentContext(), MainActivityDrawer.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
            });
        }





        /////////////////////////////////////////  security implementation


/// secu  ////
        // Make sure we have access coarse location enabled, if not, prompt the user to enable it
        if (getActivity().checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle(getResources().getString(R.string.location_denied_title));
            builder.setMessage(R.string.location_denied_message);
            builder.setPositiveButton(android.R.string.ok, null);
            builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_COARSE_LOCATION);
                }
            });
            builder.show();
        }

        startScan();
        ((BaseActivity) getActivity()).callBackgroundScanning();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            if (resultCode == getActivity().RESULT_OK) {
                String contents = data.getStringExtra("SCAN_RESULT");
                Log.e("code",contents);
            }
            if(resultCode == RESULT_CANCELLED){
                //handle cancel
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().registerReceiver(discoveryBroadcastReceiver, discoveryIntentFilter);
        mCodeScanner.startPreview();

    }

    @Override
    public void onPause() {
        mCodeScanner.releaseResources();
        super.onPause();
        stopScan();
        getActivity().unregisterReceiver(discoveryBroadcastReceiver);


    }
    public void moveToInitialPaymentFragment(String scanned_device_code) {
        ApplicationClass.setScannedDeviceCode(scanned_device_code);
        getLockerDetailsFromScanner(scanned_device_code);
       // surfaceView.setVisibility(View.GONE);




    }





    ///Camera

    @Override
    public void onStart() {
        super.onStart();

        //requesting permission
        int permissionCheck;
        permissionCheck = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.CAMERA)) {

            } else {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA}, MY_PERMISSIONS_REQUEST_CAMERA);
                //  Toast.makeText(getApplicationContext(), "request permission", Toast.LENGTH_SHORT).show();
            }
        } else {
            //Toast.makeText(getApplicationContext(), "PERMISSION_ALREADY_GRANTED", Toast.LENGTH_SHORT).show();
            /*try {
                mCameraManager.openCamera(mCameraIDsList[0], mCameraStateCB, new Handler());
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }*/
        }
    }

    @Override
    public void onStop() {
        super.onStop();

    }


    //////////////////////////// Security Implementation


    private void initializeBluetooth() {


        final BluetoothManager bluetoothManager =
                (BluetoothManager) getActivity().getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        // Checks if Bluetooth is supported on the device.
        if (mBluetoothAdapter == null) {
            Toast.makeText(getContext(), "No Bluetooth", LENGTH_SHORT).show();
            return;
        } else {
            if (!mBluetoothAdapter.isEnabled()) {
                mBluetoothAdapter.enable();
            }
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        menu = null;



    }


    @SuppressLint("StaticFieldLeak") // AsyncTask needs reference to this fragment
    private void startScan() {
        if (scanState != ScanState.NONE)
            return;
        scanState = ScanState.LESCAN;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (getActivity().checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                scanState = ScanState.NONE;
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle(R.string.location_permission_title);
                builder.setMessage(R.string.location_permission_message);
                builder.setPositiveButton(android.R.string.ok,
                        (dialog, which) -> requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 0));
                builder.show();
                return;
            }
            LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
            boolean locationEnabled = false;
            try {
                locationEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            } catch (Exception ignored) {
            }
            try {
                locationEnabled |= locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            } catch (Exception ignored) {
            }
            if (!locationEnabled)
                scanState = ScanState.DISCOVERY;
            // Starting with Android 6.0 a bluetooth scan requires ACCESS_COARSE_LOCATION permission, but that's not all!
            // LESCAN also needs enabled 'location services', whereas DISCOVERY works without.
            // Most users think of GPS as 'location service', but it includes more, as we see here.
            // Instead of asking the user to enable something they consider unrelated,
            // we fall back to the older API that scans for bluetooth classic _and_ LE
            // sometimes the older API returns less results or slower
        }
        listItems.clear();
        listAdapter.notifyDataSetChanged();
        //etEmptyText("<scanning...>");

        if (scanState == ScanState.LESCAN) {
            leScanStopHandler.postDelayed(this::stopScan, LESCAN_PERIOD);
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void[] params) {
                    bluetoothAdapter.startLeScan(null, leScanCallback);
                    return null;
                }
            }.execute(); // start async to prevent blocking UI, because startLeScan sometimes take some seconds
        } else {
            bluetoothAdapter.startDiscovery();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        // ignore requestCode as there is only one in this fragment
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            new Handler(Looper.getMainLooper()).postDelayed(this::startScan, 1); // run after onResume to avoid wrong empty-text
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(getText(R.string.location_denied_title));
            builder.setMessage(getText(R.string.location_denied_message));
            builder.setPositiveButton(android.R.string.ok, null);
            builder.show();
        }
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CAMERA:
                if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)
                    /*try {
                        mCameraManager.openCamera(mCameraIDsList[1], mCameraStateCB, new Handler());
                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                    }*/
                break;
        }
    }

    private void updateScan(BluetoothDevice device) {
        if (scanState == ScanState.NONE)
            return;
        if (listItems.indexOf(device) < 0) {
            listItems.add(device);
            Collections.sort(listItems, CodeScannerFragment::compareTo);
            listAdapter.notifyDataSetChanged();

        }
    }

    static int compareTo(BluetoothDevice a, BluetoothDevice b) {
        boolean aValid = a.getName() != null && !a.getName().isEmpty();
        boolean bValid = b.getName() != null && !b.getName().isEmpty();
        if (aValid && bValid) {
            int ret = a.getName().compareTo(b.getName());
            if (ret != 0) return ret;
            return a.getAddress().compareTo(b.getAddress());
        }
        if (aValid) return -1;
        if (bValid) return +1;
        return a.getAddress().compareTo(b.getAddress());
    }


    private void stopScan() {
        if (scanState == CodeScannerFragment.ScanState.NONE)
            return;
        if (menu != null) {

        }
        switch (scanState) {
            case LESCAN:
                leScanStopHandler.removeCallbacks(this::stopScan);
                bluetoothAdapter.stopLeScan(leScanCallback);
                break;
            case DISCOVERY:
                bluetoothAdapter.cancelDiscovery();
                break;
            default:
                // already canceled
        }
        scanState = CodeScannerFragment.ScanState.NONE;

    }

    public void connectToDevice() {
        //REMOVE AT PRODUCTION


        // DeviceNameBLE ="DESKTOP-DVQ63AJ";

        for (int i = 0; i < listItems.size(); i++) {
            if (listItems.get(i).getName() != null && !listItems.get(i).getName().isEmpty()) {

                if (listItems.get(i).getName().equalsIgnoreCase(ApplicationClass.getDeviceName())) {
                    Address = listAdapter.getItem(i).getAddress();

                    ApplicationClass.setBLEAddress(Address);
                    deviceList =deviceList+listItems.get(i).getName()+"("+listItems.get(i).getAddress()+")"+",";
                }
                else if (listItems.get(i).getName().equals("RN4678-2C40"))
                {
                    Toast.makeText(ApplicationClass.getCurrentContext(), "Deprecated device found"+"RN4678-2C40", LENGTH_SHORT).show();
                }
            }

        }
        //REMOVE AT PRODUCTIO
        //TINU
         // Address ="80:1F:12:B2:2C:28";
        // Address ="2B:B2:BD:C1:35:17";
        // Address ="28:B2:BD:C1:35:17";
        ApplicationClass.setBLEAddress(Address);
        if (Address != null && !Address.isEmpty()) {
            //((BaseActivity) getActivity()).hideBusyAnimation();
            if (waitForConnecttoDevice != null) {
                waitForConnecttoDevice.cancel();
                waitForConnecttoDevice = null;
            }
            getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    ApplicationClass.setDeviceList(deviceList);
                    ApplicationClass.setDeviceList(deviceList);
                    Bundle args = new Bundle();
                    args.putString("device", Address);
                    Fragment fragment = new TerminalFragment();
                    fragment.setArguments(args);
                    getFragmentManager().beginTransaction().replace(R.id.fl_main, fragment, "terminal").addToBackStack(null).commit();
                }
            });


        }
        else {

            for (int i = 0; i < BaseActivity.finalArrayOfFoundBTDevices.size(); i++) {

                if (BaseActivity.finalArrayOfFoundBTDevices.get(i).getName() != null && !BaseActivity.finalArrayOfFoundBTDevices.get(i).getName().isEmpty()) {

                    if (BaseActivity.finalArrayOfFoundBTDevices.get(i).getName().equalsIgnoreCase(ApplicationClass.getDeviceName())) {
                        Address = BaseActivity.finalArrayOfFoundBTDevices.get(i).getAddress();
                        ApplicationClass.setBLEAddress(Address);
                        if (waitForConnecttoDevice != null) {
                            waitForConnecttoDevice.cancel();
                            waitForConnecttoDevice = null;
                        }



                        break;
                    }
                }

            }

            if (Address != null && !Address.isEmpty()) {
                //((BaseActivity) getActivity()).hideBusyAnimation();
                if (waitForConnecttoDevice != null) {
                    waitForConnecttoDevice.cancel();
                    waitForConnecttoDevice = null;
                }
                getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        ApplicationClass.setDeviceList(deviceList);
                        Log.e("conten device list",deviceList);

                        Bundle args = new Bundle();
                        args.putString("device", Address);
                        Fragment fragment = new TerminalFragment();
                        fragment.setArguments(args);
                        getFragmentManager().beginTransaction().replace(R.id.fl_main, fragment, "terminal").addToBackStack(null).commit();
                    }
                });


            }
        }



    }





    private void checkStatusEveryIntervels() {
        waitForConnecttoDevice = new CountDownTimer(20000, 2000) {
            //check each 2 seconds
            public void onTick(long millisUntilFinished) {
                connectToDevice();


            }

            public void onFinish() {
                if (waitForConnecttoDevice != null) {
                    waitForConnecttoDevice.cancel();
                    waitForConnecttoDevice = null;
                }

                ((BaseActivity) getActivity()).hideBusyAnimation();

                getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        String msg="";
                        if (true)
                        {
                            msg=  ApplicationClass.getCurrentContext().getResources().getString(R.string.locker_not_range_lock) ;
                        }
                        else {
                            msg= ApplicationClass.getCurrentContext().getResources().getString(R.string.bltNotDiscoverChkOut) ;

                        }
                        String finalMsg = msg;
                        DialogManager.showAlertSingleActionDialog(finalMsg, new DialogManager.IUniActionDialogOnClickListener() {
                            @Override
                            public void onPositiveClick() {
                                ApplicationClass.setDeviceList(deviceList);
                                postErrorLog("105","Device not found","",0,"","No response from device","デバイスからの応答なし。");


                            }
                        });



                    }
                });




            }
        }.start();
    }


    private void getLockerDetailsFromScanner(String scannedDeviceCode) {

        ((BaseActivity) getActivity()).showBusyAnimation("");
        Map<String, Object> candidateMap = new HashMap<>();

        if (SharedPreferenceUtility.getIsEnglishLag()) {
            candidateMap.put("lang_id", ConstantProject.isEnglishLag);
        } else {
            candidateMap.put("lang_id", ConstantProject.isJapaneaseLag);
        }
        candidateMap.put("free_scan", "1");
        candidateMap.put("device_id", scannedDeviceCode);
        String acess_token = ConstantProject.API_HEADER + SharedPreferenceUtility.getAccessToken();


        apiService.getScannedDeviceDetails(acess_token, candidateMap).enqueue(new Callback<scanDeviceModel>() {
            @Override
            public void onResponse(Call<scanDeviceModel> call, Response<scanDeviceModel> response) {

                if (response.code() == 200) {
                    Boolean status = response.body().isSuccess();
                    if (status == true && response.body().getData() != null ) {

                       /* if (response.body().getData().getPresent_status().equalsIgnoreCase("unlocked") || response.body().getData().getStatus().equals("inactive"))
                        {*/
                        if (response.body().getData() != null && response.body().getData().getMainteanceTask() != null)
                            ApplicationClass.setTaskId("" + response.body().getData().getMainteanceTask().getId());

                        ApplicationClass.setDeviceName(response.body().getData().getDevice_name());
                        ApplicationClass.setScannedDeviceCode(response.body().getData().getDevice_id());
                        ApplicationClass.setDeviceAddress1(response.body().getData().getAddress_1());
                        ApplicationClass.setDeviceLocation(response.body().getData().getLocation());
                        ApplicationClass.setDeviceAddress2(response.body().getData().getAddress_2());
                        ApplicationClass.setDevicepinCode(response.body().getData().getPincode());
                        ApplicationClass.setDevicelandMark(response.body().getData().getLandmark());
                        ApplicationClass.setDevicelatitude(response.body().getData().getLatitude());
                        ApplicationClass.setDevicelogitude(response.body().getData().getLongitude());
                        ApplicationClass.setLock_process_status(response.body().getData().getLock_process_status());
                        ApplicationClass.setPresent_status(response.body().getData().getPresent_status());
                        SharedPreferenceUtility.saveHashValueForUnlock(response.body().getData().getClose_hash());
                        ((BaseActivity) getActivity()).hideBusyAnimation();
                        ((BaseActivity) getActivity()).showBusyAnimation("");
                        checkStatusEveryIntervels();

                    /*}*/
                       /* else {
                            DialogManager.showAlertSingleActionDialog(ApplicationClass.getCurrentActivity().getResources().getString(R.string.deviceInactiee), new DialogManager.IUniActionDialogOnClickListener() {
                                @Override
                                public void onPositiveClick() {
                                    Intent intent = new Intent(ApplicationClass.getCurrentContext(), MainActivityDrawer.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                }
                            });
                        }*/



                    } else if (status == false) {
                        String msg = response.body().getMessage();
                        DialogManager.showAlertSingleActionDialog(msg, new DialogManager.IUniActionDialogOnClickListener() {
                            @Override
                            public void onPositiveClick() {

                                Intent intent = new Intent(ApplicationClass.getCurrentContext(), MainActivityDrawer.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);

                            }
                        });


                    }
                } else if (response.code() == 404) {
                    ((BaseActivity) getActivity()).hideBusyAnimation();
                    DialogManager.showAlertSingleActionDialog(ApplicationClass.getCurrentContext().getString(R.string.no_pending_task), new DialogManager.IUniActionDialogOnClickListener() {
                        @Override
                        public void onPositiveClick() {

                            Intent intent = new Intent(ApplicationClass.getCurrentContext(), MainActivityDrawer.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);

                        }
                    });

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
            public void onFailure(Call<scanDeviceModel> call, Throwable t) {
                ((BaseActivity) getActivity()).hideBusyAnimation();
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
    public void postErrorLog(final String ErrorCode, final String message, String givenInputs, int ng, String battery, String eng, String jap) {

        Map<String, Object> candidateMap = new HashMap<>();
        candidateMap.put("device_id", ApplicationClass.getScannedDeviceCode());
        candidateMap.put("app", "Android");
        candidateMap.put("device_status",jap);

        candidateMap.put("device_model",ApplicationClass.getDevice_name());
        candidateMap.put("device_version", ApplicationClass.getDevice_version());
        candidateMap.put("scanned_qr_code", ApplicationClass.getScannedDeviceCode());
        candidateMap.put("available_blt_devices", deviceList);
        candidateMap.put("command_sent", "no");
        candidateMap.put("command_received", "no");
        candidateMap.put("error_message_en", eng);
        candidateMap.put("error_message_jp", jap);
        candidateMap.put("comments", message );//commentsForAppLog

        String acess_token = ConstantProject.API_HEADER + SharedPreferenceUtility.getAccessToken();

        apiService =
                ApiClient.getClient().create(ApiInterface.class);
        apiService.postErrorLog(acess_token, candidateMap).enqueue(new Callback<HashApiResponse>() {
            @Override
            public void onResponse(Call<HashApiResponse> call, Response<HashApiResponse> response) {
                if (response.code() == 200) {
                    Intent intent = new Intent(ApplicationClass.getCurrentContext(), MainActivityDrawer.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }


            }


            @Override
            public void onFailure(Call<HashApiResponse> call, Throwable t) {
                if (!isNetworkConnectionAvailable(ApplicationClass.getCurrentContext())) {



                } else {

                }
            }
        });
    }

}

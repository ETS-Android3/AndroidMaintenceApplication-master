package com.tinu.maintanceappliccation;

import android.Manifest;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.tinu.maintanceappliccation.activity.BaseActivity;
import com.tinu.maintanceappliccation.activity.LogoutActivity;
import com.tinu.maintanceappliccation.models.GetHashMsgModel;
import com.tinu.maintanceappliccation.models.HashApiResponse;
import com.tinu.maintanceappliccation.models.LogAppErrorModel;
import com.tinu.maintanceappliccation.models.UpdateTaskResponseModel;
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

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;
import static android.widget.Toast.LENGTH_SHORT;
import static com.tinu.maintanceappliccation.activity.BaseActivity.isNetworkConnectionAvailable;
import static com.tinu.maintanceappliccation.utility.ConstantProject.ALPHACAPS;
import static com.tinu.maintanceappliccation.utility.ConstantProject.ALPHASMALL;
import static com.tinu.maintanceappliccation.utility.ConstantProject.ALPHA_NUMERIC_STRING;
import static com.tinu.maintanceappliccation.utility.ConstantProject.DIGITS;
import static com.tinu.maintanceappliccation.utility.ConstantProject.balteryPower;


public class TestUnlockFragment extends Fragment implements View.OnClickListener, ServiceConnection, SerialListener {
    @Override
    public void onClick(View v) {

    }

    ///sec
    String Secstatus = "";
    private boolean initialStart = true;

    private enum Connected {False, Pending, True}

    private Connected connected = Connected.True;
    private TextView receiveText, tv_device_name;
    private SerialService service;
    String CurrentResponse = "", PreviousResponse;
    private String languageToLoad;

    // Security KEY
    private String RANDOM_APP_KEY = "";
    private String HASH_VALUE = "", IHashValue = "";
    private boolean allow=true;
    private boolean isMSG1UnlockSent = false, isMSG1lockSent = false, isHashValueResponseLock = false, isHashValueResponseUnlock = false;

    ///end: Sec
    String MSG1 = "";
    private SharedPreferences sharedPreferences;
    TextView tvUser, tv_comment;

    String Inputs;
    Fragment targetFragment = null;
    String RESPONSE_MSG = "";
    // private long currentTimeOfTimer = 300000;
    // BottomNavigationView navigation;

    private ApiInterface apiService;
    int trailCount = 0;
    boolean isShowingBusyAniReconnect = false, checktheResponseisReceived = false, isIHashSend = false,checktheResponseisReceivedOHash =false;
    CountDownTimer manageResponse,OHasValueSplit,manageResponseOHash;
    int   OhashCount=0;

    String LastSend, LastInput;

    public TestUnlockFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (SharedPreferenceUtility.getIsEnglishLag()) {
            languageToLoad = ConstantProject.isEnglishLag;
        } else {
            languageToLoad = ConstantProject.isJapaneaseLag;

        }

        Locale locale = new Locale(languageToLoad);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getActivity().getBaseContext().getResources().updateConfiguration(config,
                getActivity().getBaseContext().getResources().getDisplayMetrics());

        return inflater.inflate(R.layout.activity_test_unlock, container, false);
    }

    //sjn ble
    //sjn addded
    //--
    //sjn test
    BluetoothManager btManager;
    BluetoothAdapter btAdapter;
    BluetoothLeScanner btScanner;

    private final static int REQUEST_ENABLE_BT = 1;
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;

    Button btn_unlock;
    Boolean isUnlockPress = false;

    private boolean successApiUnlockisCalled;


    BluetoothAdapter mBluetoothAdapter;

    String lockOrUnlock;
    CountDownTimer waitTimer, waitingForThreeMinitue, ThreeMinitueTimingForLock,IhashValueSplit;
    int count =0;


    // Stops scanning after 5 seconds.
    private Handler mHandler = new Handler();
    private static final long SCAN_PERIOD = 5000;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sharedPreferences = getActivity().getSharedPreferences("carry_park", MODE_PRIVATE);
        if (service == null) {
            service = ApplicationClass.getService();
        }
        apiService =
                ApiClient.getClient().create(ApiInterface.class);

        tv_device_name = (TextView) view.findViewById(R.id.tv_device_name);
        tv_comment = (TextView) view.findViewById(R.id.tv_comment);
        tv_comment.setText("Start");
        btn_unlock = (Button) view.findViewById(R.id.btn_unlock);
        receiveText = (TextView) view.findViewById(R.id.tv_recive);
        String user = sharedPreferences.getString("user_name", null);
        receiveText.setVisibility(View.GONE);
        tv_comment.setVisibility(View.GONE);



        btn_unlock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!isUnlockPress) {

                    isUnlockPress = true;
                    ((BaseActivity) getActivity()).showBusyAnimation("");
                                        manageResponse = new CountDownTimer(ConstantProject.TimeoutFiftySecond, 3000) {
                        //check each 2 seconds
                        public void onTick(long millisUntilFinished) {

                            if (!checktheResponseisReceived) {
                                checktheResponseisReceived = false;

                                    IHashValue = SharedPreferenceUtility.getHashValueForUnlock();

                                getActivity().runOnUiThread(this::SendIHashValue);


                            } else {
                                ((BaseActivity) getActivity()).hideBusyAnimation();
                                if (manageResponse != null) {
                                    manageResponse.cancel();
                                    manageResponse = null;
                                }
                                if (IhashValueSplit != null) {
                                    IhashValueSplit.cancel();
                                    IhashValueSplit = null;
                                }

                            }


                        }

                        private void SendIHashValue() {
                            isIHashSend = true;
                            Secstatus = ConstantProject.Sec_Status_UNLock_I_hashValue;

                           // send("I:" + IHashValue + ";");
                            count =0;
                            String hashValue= "I:" + IHashValue + ";";//35
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
                                        send(list.get(count));
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


                        public void onFinish() {
                            if (manageResponse != null) {
                                manageResponse.cancel();
                                manageResponse = null;
                            }
                            if (IhashValueSplit != null) {
                                IhashValueSplit.cancel();
                                IhashValueSplit = null;
                            }

                        }
                    }.start();

                }


                /////


            }
        });


        tv_device_name.setText(ApplicationClass.getDeviceName());
        initializeBluetooth();

        btManager = (BluetoothManager) getActivity().getSystemService(Context.BLUETOOTH_SERVICE);
        btAdapter = btManager.getAdapter();
        btScanner = btAdapter.getBluetoothLeScanner();

        if (btAdapter != null && !btAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        }

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


    }


    private void initializeBluetooth() {
        mHandler = new Handler();

        // Use this check to determine whether BLE is supported on the device.  Then you can
        // selectively disable BLE-related features.
        // Initializes a Bluetooth adapter.  For API level 18 and above, get a reference to
        // BluetoothAdapter through BluetoothManager.
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getActivity().getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        // Checks if Bluetooth is supported on the device.
        if (mBluetoothAdapter == null) {
            Toast.makeText(getContext(), "No Bluetooth", LENGTH_SHORT).show();
            return;
        }
        //sjn
        else {
            if (!mBluetoothAdapter.isEnabled()) {
                mBluetoothAdapter.enable();
            }
        }
    }


    public void status() {

        send("st;");
    }




    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_COARSE_LOCATION: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    System.out.println("coarse location permission granted");
                } else {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle("Functionality limited");
                    builder.setMessage("Since location access has not been granted, this app will not be able to discover beacons when in the background.");
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {

                        @Override
                        public void onDismiss(DialogInterface dialog) {
                        }

                    });
                    builder.show();
                }
                return;
            }
        }
    }


    @Override
    public void onStart() {
        super.onStart();

        if (service != null)
            service.attach(this);
        else
            getActivity().startService(new Intent(getActivity(), SerialService.class)); // prevents service destroy on unbind from recreated activity caused by orientation change

    }

    @Override
    public void onStop() {
        super.onStop();


    }

    //call Lock API


    // stsrt: security IMPlementation


    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        service = ApplicationClass.getService();
        if (service != null)
            service.attach(this);
        else
            getActivity().startService(new Intent(getActivity(), SerialService.class)); // prevents service destroy on unbind from recreated activity caused by orientation change


    }

    public void callSuccessApiLock() {
        /*ApplicationClass.setIsSendMM(false);
        ApplicationClass.setIsStatusChecking(false);
        ApplicationClass.setIsAwaitingForPutLockDown(false);
*/
        if (waitTimer != null) {
            waitTimer.cancel();
            waitTimer = null;
        }
        if (ThreeMinitueTimingForLock != null) {
            ThreeMinitueTimingForLock.cancel();
            ThreeMinitueTimingForLock = null;
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
        service = null;
    }

    public void onSerialConnect() {

        connected = Connected.True;
        ((BaseActivity) ApplicationClass.getCurrentActivity()).hideBusyAnimation();


    }


    public void SentST() {
        send("st;");
    }


    @Override
    public void onSerialConnectError(Exception e) {
        disconnect();
        if (!isShowingBusyAniReconnect) {
            ((BaseActivity) getActivity()).showBusyAnimation("");
            isShowingBusyAniReconnect = true;
        }
        if (trailCount < 10) {
            trailCount++;
            connect();
        } else {

            disconnect();
            ((BaseActivity) getActivity()).hideBusyAnimation();
            DialogManager.showUniActionDialog(ApplicationClass.getCurrentActivity(), ApplicationClass.getCurrentContext().getResources().getString(R.string.smtngWntWrng), "ok", new DialogManager.IUniActionDialogOnClickListener() {
                @Override
                public void onPositiveClick() {
                    Intent intent = new Intent(ApplicationClass.getCurrentContext(), MainActivityDrawer.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);


                }
            });

        }


    }

    @Override
    public void onSerialRead(byte[] data) {
        receive(data);
    }

    @Override
    public void onSerialIoError(Exception e) {


        disconnect();
        if (!isShowingBusyAniReconnect) {
            isShowingBusyAniReconnect = true;
            ((BaseActivity) getActivity()).showBusyAnimation("");
        }
        if (trailCount < 10) {
            trailCount++;
            connect();

        } else {

            disconnect();
            ((BaseActivity) getActivity()).hideBusyAnimation();
            DialogManager.showUniActionDialog(ApplicationClass.getCurrentActivity(), ApplicationClass.getCurrentContext().getResources().getString(R.string.smtngWntWrng), "ok", new DialogManager.IUniActionDialogOnClickListener() {
                @Override
                public void onPositiveClick() {
                    Intent intent = new Intent(ApplicationClass.getCurrentContext(), MainActivityDrawer.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);


                }
            });

        }


    }


    private void connect() {
        try {
            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            BluetoothDevice device = bluetoothAdapter.getRemoteDevice(ApplicationClass.getBLEAddress());
            connected = Connected.Pending;
            SerialSocket socket = new SerialSocket(getActivity().getApplicationContext(), device);
            service.connect(socket);
        } catch (Exception e) {
            onSerialConnectError(e);
        }


    }

    private void send(String str) {
        Log.e("contents send:un", str);

        LastSend = str;
       // sentAppLog("Testing send at unlock" + LastSend);
        tv_comment.setText(tv_comment.getText().toString() + "\n" + "Send: " + str);

        Inputs = str;
        if (connected != Connected.True) {
            return;
        }
        try {
            byte[] data = (str).getBytes();
            service.write(data);
        } catch (Exception e) {
            onSerialIoError(e);
        }
    }

    public void receive(byte[] data) {


        receiveText.setText("");
        receiveText.append(new String(data));
        // Toast.makeText(ApplicationClass.getCurrentContext(),receiveText.getText().toString(), Toast.LENGTH_SHORT).show();
        RESPONSE_MSG = "";
        RESPONSE_MSG = receiveText.getText().toString();
        Log.e("contents re",RESPONSE_MSG);
        LastInput = RESPONSE_MSG;
      //  sentAppLog("Testing" + " data receive " + RESPONSE_MSG);

        if (RESPONSE_MSG.contains("\r")) {
            RESPONSE_MSG = RESPONSE_MSG.replaceAll("\r", "");

        }
        CurrentResponse = RESPONSE_MSG;
        if (PreviousResponse==null)
        {
            allow =true;
        }
        else if (CurrentResponse.equalsIgnoreCase(PreviousResponse))
        {
            if (SharedPreferenceUtility.getIsUpholdHandle())
            {
                allow =true;
            }
            else {
                allow =false;
            }
        }
        else {
            allow =true;
        }


        if (allow) {
           // sentAppLog("Testing" + " data receive Inside" + RESPONSE_MSG);
            tv_comment.setText(tv_comment.getText().toString() + "\n" + "Receive: " + CurrentResponse);

            PreviousResponse = RESPONSE_MSG;


            if (Secstatus.equalsIgnoreCase(ConstantProject.Sec_Status_UNLock_MSG1_send)) {

                String X, Y, Z, messageResponse = RESPONSE_MSG;
                if (messageResponse != null && messageResponse.length() > 0 && messageResponse.contains("P") && messageResponse.contains("X")
                        && messageResponse.contains("Y") && messageResponse.contains("Z")) {
                    // message is response of the MSG1;
                    //“P:X=15,Y=5,Z=5”.
                    if (messageResponse.contains("\r")) {
                        messageResponse = messageResponse.replaceAll("\r", "");

                    }
                    String[] separated = messageResponse.split(",");
                    // value of N

                    X = separated[0].substring(separated[0].indexOf("=") + 1, separated[0].length());
                    Log.e("Receive", X);
                    Y = separated[1].substring(separated[1].indexOf("=") + 1, separated[1].length());
                    Log.e("Receive", Y);
                    Z = separated[2].substring(separated[2].indexOf("=") + 1, separated[2].length());
                    Log.e("Receive", Z);

/// Call Hash Value API

                    getHashValuesAtUnLock(ApplicationClass.getScannedDeviceCode(), "open", X, Y, Z);


                }


            }

            if (Secstatus.equalsIgnoreCase(ConstantProject.Sec_Status_UNLock_O_HashValue)) {
                checktheResponseisReceivedOHash =true;
                ((BaseActivity) getActivity()).hideBusyAnimation();
                if (RESPONSE_MSG.contains("O:R=OK")) {
                    // postErrorLog(receiveText.getText().toString(), CarryParkApplication.getCurrentContext().getResources().getString(R.string.device_auth_failed), Inputs);

                    //  SendRR();

                }
                else  if (RESPONSE_MSG.contains("E=-203"))
                {

                    DialogManager.showAlertSingleActionDialog(RESPONSE_MSG, new DialogManager.IUniActionDialogOnClickListener() {
                        @Override
                        public void onPositiveClick() {
                            postErrorLog(RESPONSE_MSG);
                            disconnect();
                            ApplicationClass.setIsAutoLock(true);
                            disconnect();
                            incompleteTask();

                            Bundle args = new Bundle();
                            args.putString("device",
                                    ApplicationClass.getBLEAddress());
                            Fragment fragment = new TerminalFragmentAutoLock();
                            fragment.setArguments(args);
                            getFragmentManager().beginTransaction().replace(R.id.fl_main, fragment, "terminal").addToBackStack(null).commit();

                        }
                    });
               /*     manageResponse = new CountDownTimer(ConstantProject.TimeoutFiftySecond, 3000) {
                        //check each 2 seconds
                        public void onTick(long millisUntilFinished) {

                            if (!checktheResponseisReceived) {
                                checktheResponseisReceived = false;

                                IHashValue = SharedPreferenceUtility.getHashValueForUnlock();

                                getActivity().runOnUiThread(this::SendIHashValue);


                            } else {

                                if (manageResponse != null) {
                                    manageResponse.cancel();
                                    manageResponse = null;
                                }
                                if (IhashValueSplit != null) {
                                    IhashValueSplit.cancel();
                                    IhashValueSplit = null;
                                }

                            }


                        }

                        private void SendIHashValue() {
                            isIHashSend = true;
                            Secstatus = ConstantProject.Sec_Status_UNLock_I_hashValue;

                            // send("I:" + IHashValue + ";");
                            count =0;
                            String hashValue= "I:" + IHashValue + ";";//35
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
                                        send(list.get(count));
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


                        public void onFinish() {
                            if (manageResponse != null) {
                                manageResponse.cancel();
                                manageResponse = null;
                            }
                            if (IhashValueSplit != null) {
                                IhashValueSplit.cancel();
                                IhashValueSplit = null;
                            }

                        }
                    }.start();*/

                }
                else if (RESPONSE_MSG.contains("NG") || RESPONSE_MSG.contains("ER")) {

                    DialogManager.showUniActionDialog(ApplicationClass.getCurrentActivity(), ApplicationClass.getCurrentContext().getResources().getString(R.string.device_auth_failed), "ok", new DialogManager.IUniActionDialogOnClickListener() {
                        @Override
                        public void onPositiveClick() {
                            ApplicationClass.setIsAutoLock(true);
                            disconnect();
                            Bundle args = new Bundle();
                            args.putString("device",
                                    ApplicationClass.getBLEAddress());
                            Fragment fragment = new TerminalFragmentAutoLock();
                            fragment.setArguments(args);
                            getFragmentManager().beginTransaction().replace(R.id.fl_main, fragment, "terminal").addToBackStack(null).commit();

                            //REMOVE IN PRO
                         /*   disconnect();
                            Intent intent = new Intent(ApplicationClass.getCurrentContext(), MainActivityDrawer.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
*/


                            // completeTask();




                        }

                    });
                }

                String response = receiveText.getText().toString();
                if (response.contains("S") && response.contains("L")) {

                    String N, S, L, P, B;

                    String[] separated = response.split(",");

                    if (response != null && response.contains("N") && response.contains("B")) {
                        N = separated[0].substring(separated[0].length() - 1);
                        S = separated[1].substring(separated[1].length() - 1);
                        L = separated[2].substring(separated[2].length() - 1);
                        P = separated[3].substring(separated[3].length() - 1);
                        B = separated[4].substring(separated[4].length() - 5);


                        if (L.equalsIgnoreCase("0") && S.equals("0")) {

                            SendRR();


                            disconnect();
                            if (ApplicationClass.getTaskId()==null ||ApplicationClass.getTaskId().isEmpty())
                            {

                                Intent intent=new Intent(ApplicationClass.getCurrentActivity(),MainActivityDrawer.class);
                                startActivity(intent);
                            }
                            else {
                                completeTask();
                            }


                        }
                        else  if (L.equalsIgnoreCase("1") && S.equals("0")) {
                            DialogManager.showUniActionDialog(ApplicationClass.getCurrentActivity(), ApplicationClass.getCurrentContext().getResources().getString(R.string.smtngWntWrng), "ok", new DialogManager.IUniActionDialogOnClickListener() {
                                @Override
                                public void onPositiveClick() {

                                    disconnect();
                                    ApplicationClass.setIsAutoLock(true);

                                    Bundle args = new Bundle();
                                    args.putString("device",
                                            ApplicationClass.getBLEAddress());
                                    Fragment fragment = new TerminalFragmentAutoLock();
                                    fragment.setArguments(args);
                                    getFragmentManager().beginTransaction().replace(R.id.fl_main, fragment, "terminal").addToBackStack(null).commit();

                                }
                            });

                        }


                    }

                }


            }


        }
        if (Secstatus.equalsIgnoreCase(ConstantProject.Sec_Status_UNLock_I_hashValue)) {
            if (isIHashSend) {
                checktheResponseisReceived = true;

                LockSecurityReadIHashValueUnLockAlart(RESPONSE_MSG);
                if (manageResponse != null) {
                    manageResponse.cancel();
                    manageResponse = null;
                }
            }


        }


    }


    //call HashValueAPI

    public void getHashValuesAtUnLock(final String deviceId, final String lckOrUnlck, String X_value, String Y_value, String Z_value) {
        ((BaseActivity) getActivity()).showBusyAnimation("");
        Map<String, Object> candidateMap = new HashMap<>();
        candidateMap.put("device_id", ApplicationClass.getScannedDeviceCode());
        candidateMap.put("command_text", lckOrUnlck);
        candidateMap.put("random_app_key", ApplicationClass.getRandomAppKey());
        candidateMap.put("starting_position", X_value);
        candidateMap.put("key_string_length", Y_value);
        candidateMap.put("skip_chars", Z_value);

        Log.d("callAPILock==", "map==" + candidateMap);
        String acess_token = ConstantProject.API_HEADER + SharedPreferenceUtility.getAccessToken();
        Log.e("access_token", acess_token);
        apiService =
                ApiClient.getClient().create(ApiInterface.class);
        apiService.getHashValues(acess_token, candidateMap).enqueue(new Callback<HashApiResponse>() {
            @Override
            public void onResponse(Call<HashApiResponse> call, Response<HashApiResponse> response) {
                ((BaseActivity) getActivity()).hideBusyAnimation();


                // if (response.code() == 200 && response.body().getSuccess() == true) {
/// sending hash value to Device


                if (response.body().getData() != null && !response.body().getData().isEmpty()) {
                    // isMSG1UnlockSent=false;
                    tv_comment.setText(tv_comment.getText().toString()+"HashValue at unlock:"+response.body().getData()+"with"+ApplicationClass.getScannedDeviceCode()+","+lckOrUnlck+","+ApplicationClass.getRandomAppKey());

                    isMSG1UnlockSent = false;
                    isHashValueResponseLock = false;
                    isHashValueResponseUnlock = true;
                    HASH_VALUE = response.body().getData();
                    Secstatus = ConstantProject.Sec_Status_UNLock_O_HashValue;

                    manageResponseOHash = new CountDownTimer(ConstantProject.TimeoutFiftySecond, 2000) {
                        //check each 2 seconds
                        public void onTick(long millisUntilFinished) {

                            if (!checktheResponseisReceivedOHash) {
                                checktheResponseisReceivedOHash = false;
                                ApplicationClass.getCurrentActivity().runOnUiThread(this::SendHashValue);



                            } else {
                                if (manageResponseOHash != null) {
                                    manageResponseOHash.cancel();
                                    manageResponseOHash = null;
                                }


                            }


                        }

                        private void SendHashValue() {


                            OhashCount=0;

                            String hashValue= "O:" + HASH_VALUE + ";";//35
                            Log.e("HashSplit",hashValue);
                            List<String> OhashList= new ArrayList<String>();

                            int OHashindex = 0;
                            while (OHashindex<hashValue.length()) {

                                OhashList.add(hashValue.substring(OHashindex, Math.min(OHashindex+8,hashValue.length())));
                                OHashindex=OHashindex+8;
                            }

                            OHasValueSplit = new CountDownTimer(2000, 100) {

                                public void onTick(long millisUntilFinished) {
                                    if (OhashList.size()>OhashCount && OhashCount<6)
                                    {
                                        send(OhashList.get(OhashCount));
                                        Log.e("HashSplit",OhashList.get(OhashCount));

                                    }

                                    OhashCount =OhashCount+1;



                                }
                                public void onFinish() {
                                    if (OHasValueSplit != null) {
                                        OHasValueSplit.cancel();
                                        OHasValueSplit = null;
                                    }



                                }
                            }.start();



                        }


                        public void onFinish() {
                            if (manageResponseOHash != null) {
                                manageResponseOHash.cancel();
                                manageResponseOHash = null;
                            }
                            if (IhashValueSplit != null) {
                                IhashValueSplit.cancel();
                                IhashValueSplit = null;
                            }
                            ((BaseActivity) getActivity()).hideBusyAnimation();
                            ApplicationClass.setIsAutoLock(true);
                            disconnect();
                            Bundle args = new Bundle();
                            args.putString("device",
                                    ApplicationClass.getBLEAddress());
                            Fragment fragment = new TerminalFragmentAutoLock();
                            fragment.setArguments(args);
                            getFragmentManager().beginTransaction().replace(R.id.fl_main, fragment, "terminal").addToBackStack(null).commit();

                        }
                    }.start();





                }


            }


            @Override
            public void onFailure(Call<HashApiResponse> call, Throwable t) {
                ((BaseActivity) getActivity()).hideBusyAnimation();

                if (!BaseActivity.isNetworkConnectionAvailable(ApplicationClass.getCurrentContext())) {
                    Toast.makeText(ApplicationClass.getCurrentContext(), ApplicationClass.getCurrentContext().getResources().getString(R.string.OperfailedExtnd), Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(ApplicationClass.getCurrentContext(), R.string.OperfailedExtnd, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void SendRR() {
        isHashValueResponseUnlock = false;

        ApplicationClass.getCurrentActivity().runOnUiThread(this::sendrr);


    }

    private void sendrr() {
        send("RR;");
    }

    public void SendMM() {
        ApplicationClass.getCurrentActivity().runOnUiThread(this::sendmm);

    }


    public void sendmm() {
        send("MM;");
    }


    @Override
    public void onDestroy() {
        if (connected != Connected.False)
            disconnect();
        getActivity().stopService(new Intent(getActivity(), SerialService.class));
        super.onDestroy();
    }

    private void disconnect() {
        connected = Connected.False;
        service.disconnect();

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        disconnect();
        if (waitTimer != null) {
            waitTimer.cancel();
            waitTimer = null;
        }

        if (waitingForThreeMinitue != null) {
            waitingForThreeMinitue.cancel();
            waitingForThreeMinitue = null;
        }
        if (ThreeMinitueTimingForLock != null) {
            ThreeMinitueTimingForLock.cancel();
            ThreeMinitueTimingForLock = null;
        }


    }


    public String randomAlphaNumeric(int count) {
        StringBuilder builder = new StringBuilder();
        while (count-- != 0) {
            int character = (int) (Math.random() * ALPHA_NUMERIC_STRING.length());
            builder.append(ALPHA_NUMERIC_STRING.charAt(character));
        }
        return builder.toString();

    }

    public String randomDigit(int count) {
        StringBuilder builder = new StringBuilder();
        while (count-- != 0) {
            int character = (int) (Math.random() * DIGITS.length());
            builder.append(DIGITS.charAt(character));
        }
        return builder.toString();

    }

    public String randomAlphaCaps(int count) {
        StringBuilder builder = new StringBuilder();
        while (count-- != 0) {
            int character = (int) (Math.random() * ALPHACAPS.length());
            builder.append(ALPHACAPS.charAt(character));
        }
        return builder.toString();

    }

    public String randomAlphaSmall(int count) {
        StringBuilder builder = new StringBuilder();
        while (count-- != 0) {
            int character = (int) (Math.random() * ALPHASMALL.length());
            builder.append(ALPHASMALL.charAt(character));
        }
        return builder.toString();

    }

    public void LockSecurityReadIHashValueUnLockAlart(String messageResponse) {
        {
           /* String RANDOM_APP_KEY_all = randomAlphaNumeric(1);
            String RANDOM_Digit = randomDigit(1);
            String RANDOM_ALPHACAPS = randomAlphaCaps(1);
            String RANDOM_ALPHASMALL = randomAlphaSmall(1);
            RANDOM_APP_KEY = RANDOM_APP_KEY_all + RANDOM_Digit + RANDOM_ALPHACAPS + RANDOM_ALPHASMALL;
      */

            if (ApplicationClass.isIsAutoLock())
            {
                RANDOM_APP_KEY="ABCD";
                ApplicationClass.setRandomAppKey(RANDOM_APP_KEY);
                MSG1 = "M:" + RANDOM_APP_KEY + ";";
                Secstatus = ConstantProject.Sec_Status_UNLock_MSG1_send;

                ApplicationClass.getCurrentActivity().runOnUiThread(this::SendMSG1);
            }
           else if (messageResponse.contains("NG") || messageResponse.contains("ER"))
            {
                ((BaseActivity) getActivity()).hideBusyAnimation();
                ApplicationClass.setIsAutoLock(true);
                disconnect();
                Bundle args = new Bundle();
                args.putString("device",
                        ApplicationClass.getBLEAddress());
                Fragment fragment = new TerminalFragmentAutoLock();
                fragment.setArguments(args);
                getFragmentManager().beginTransaction().replace(R.id.fl_main, fragment, "terminal").addToBackStack(null).commit();

            }
           else {
               RANDOM_APP_KEY="ABCD";
               ApplicationClass.setRandomAppKey(RANDOM_APP_KEY);
               MSG1 = "M:" + RANDOM_APP_KEY + ";";
               Secstatus = ConstantProject.Sec_Status_UNLock_MSG1_send;

               ApplicationClass.getCurrentActivity().runOnUiThread(this::SendMSG1);
           }





        }


    }


    public double checkBatteryPower(String input) {

        int decimal = Integer.parseInt(input, 16);

        double power = new Double(decimal) / new Double(65535);
        double baltteryPower = 3.3 * power;
        baltteryPower = baltteryPower * 4.3;
        return baltteryPower;

    }

    public void SendMSG1() {
        send(MSG1);
    }

    public void getOHashMsgAtUnlock() {

        Map<String, Object> candidateMap = new HashMap<>();
        candidateMap.put("device_id", ApplicationClass.getScannedDeviceCode());
        candidateMap.put("type", "open");

        String acess_token = ConstantProject.API_HEADER + SharedPreferenceUtility.getAccessToken();

        apiService.getOHashMsgAtUnlock(acess_token, candidateMap).enqueue(new Callback<GetHashMsgModel>() {
            @Override
            public void onResponse(Call<GetHashMsgModel> call, Response<GetHashMsgModel> response) {


                IHashValue = response.body().getData();
               // SendIHashValue();
            }


            @Override
            public void onFailure(Call<GetHashMsgModel> call, Throwable t) {
                if (!BaseActivity.isNetworkConnectionAvailable(ApplicationClass.getCurrentContext())) {
                    Toast.makeText(ApplicationClass.getCurrentContext(), ApplicationClass.getCurrentContext().getResources().getString(R.string.OperfailedExtnd), Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(ApplicationClass.getCurrentContext(), R.string.OperfailedExtnd, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void completeTask() {

        RequestBody status = null, end_time = null, taskId = null;
        try {
            taskId = RequestBody.create(MediaType.parse("text/plain"), ApplicationClass.getTaskId());

            status = RequestBody.create(MediaType.parse("text/plain"), "inspection completed");
            end_time = RequestBody.create(MediaType.get("text/plain"), BaseActivity.getCurrentTimeStampWithZone());

        } catch (Exception e) {
            e.printStackTrace();
        }


        String acess_token = ConstantProject.API_HEADER + SharedPreferenceUtility.getAccessToken();


        apiService.CompleteTask(acess_token, taskId, status, end_time).enqueue(new Callback<UpdateTaskResponseModel>() {
            @Override
            public void onResponse(Call<UpdateTaskResponseModel> call, Response<UpdateTaskResponseModel> response) {

                if (response.code() == 200) {
                    Boolean status = true;
                    if (status == true && response.body().getData() != null) {

                        DialogManager.showUniActionDialog(ApplicationClass.getCurrentActivity(), response.body().getMessage(), "ok", new DialogManager.IUniActionDialogOnClickListener() {
                            @Override
                            public void onPositiveClick() {
                                Intent intent = new Intent(ApplicationClass.getCurrentContext(), LogoutActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                                startActivity(intent);
                            }
                        });


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

            }

            @Override
            public void onFailure(Call<UpdateTaskResponseModel> call, Throwable t) {
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

    public void sentAppLog(String comments) {


        String acess_token = "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiIsImp0aSI6IjNiZGE4OWU5YzE5Y2JmZGVmMTRlYzMwN2MxY2RkYzczMGFlODU3YWJhM2NiYWIwNWYzMDY5OTcyODZkZmJjMmY0ZWQ0ZTdlNDFhYTNiZDUwIn0.eyJhdWQiOiIxIiwianRpIjoiM2JkYTg5ZTljMTljYmZkZWYxNGVjMzA3YzFjZGRjNzMwYWU4NTdhYmEzY2JhYjA1ZjMwNjk5NzI4NmRmYmMyZjRlZDRlN2U0MWFhM2JkNTAiLCJpYXQiOjE2MDA0Mjc2OTUsIm5iZiI6MTYwMDQyNzY5NSwiZXhwIjoxNjMxOTYzNjk1LCJzdWIiOiIzMDMiLCJzY29wZXMiOlsiKiJdfQ.Q7Rt0KSB14OTBRvYdIyKVU8z11oqQi3tRNP1L0rrka2VilCzEL8iPE0GjSu1mzkw8nUxROd7_-cdn9id1mla9mNHfcFisYu3Sg2glNBpD6-eS0EQG-ZncrtyYyl0-z8mgiE0Io2Rw_fvY0GgIB8YSyoQQs_kYDOQqD31et7pMx2QoNAR0p0A0gwGWFWcbV24Hc6eyyhuJuj4XNXjItMNDTiykPABRJfELkdvU_2_XmNvSPiSA6awcKdgVcphGDvGZPqtn2nqT5Y1EGvZ6xIPYHCXTp8hCKG80wCmrmssp7fD8hrYwoOv75t8LfnEWa61IudjWVd3IaDIhDlHlHDs_HfmgT-4-wBApuJm7sDU2R1rMqbxf5xN5f4sBEokJ5IZLKP7FB6qryg2JCiNDnqOJHOMF5ekd5eU0NtfUEhrnbvPC8VVfah8KAprs2sR7V_nCW2jxtFKaVq5Kn29PKctBnTLatOwsrD-sA7NxutpEm0NNF61P_WQRR8y4ht1JFf9a2JYyL5lOnJciunmjdNnVtZzSY1MQ-mnZsjKe7N4YA-xjfel1Q8wSepgd2Ye1HPz-jG7fz3W_nKXSbtliJM9LeFGNWklg-tydlfI6AMYWoKf9Of42uNkKYAJq2D7iRr506CW-b_5pIBgXS_GISjz1-s-3AMNd-rj2HCgpgDO3pY";
        Map<String, Object> candidateMap = new HashMap<>();
        candidateMap.put("device_id", "CP001-2C28");
        candidateMap.put("app", "Android");
        candidateMap.put("device_status", tv_comment.getText().toString());
        candidateMap.put("payment_require", "");
        candidateMap.put("device_model", "");
        candidateMap.put("device_version", "");
        candidateMap.put("scanned_qr_code", "");
        candidateMap.put("command_sent", LastSend);
        candidateMap.put("command_received", LastInput);
        candidateMap.put("comments", comments);


        apiService.LogAppErrors(acess_token, candidateMap).enqueue(new Callback<LogAppErrorModel>() {
            @Override
            public void onResponse(Call<LogAppErrorModel> call, Response<LogAppErrorModel> response) {


            }


            @Override
            public void onFailure(Call<LogAppErrorModel> call, Throwable t) {


            }
        });


    }

    public void postErrorLog(String comments) {

        Map<String, Object> candidateMap = new HashMap<>();
        candidateMap.put("device_id", ApplicationClass.getScannedDeviceCode());
        candidateMap.put("app", "Android");
        candidateMap.put("device_status", "Scanning completed");

        candidateMap.put("device_model",ApplicationClass.getDevice_name());
        candidateMap.put("device_version", ApplicationClass.getDevice_version());
        candidateMap.put("scanned_qr_code", ApplicationClass.getScannedDeviceCode());
        candidateMap.put("available_blt_devices", ApplicationClass.getDeviceList());
        candidateMap.put("command_sent", LastSend);
        candidateMap.put("command_received", LastSend);
        candidateMap.put("btryPower",ApplicationClass.getBattery_power() );
        candidateMap.put("error_message_en", "Debug Log");
        candidateMap.put("error_message_jp", "デバッグログ");
        candidateMap.put("comments",comments);//commentsForAppLog

        String acess_token = ConstantProject.API_HEADER + SharedPreferenceUtility.getAccessToken();

        apiService =
                ApiClient.getClient().create(ApiInterface.class);
        apiService.postErrorLog(acess_token, candidateMap).enqueue(new Callback<HashApiResponse>() {
            @Override
            public void onResponse(Call<HashApiResponse> call, Response<HashApiResponse> response) {


            }


            @Override
            public void onFailure(Call<HashApiResponse> call, Throwable t) {
                if (!isNetworkConnectionAvailable(ApplicationClass.getCurrentContext())) {



                } else {

                }
            }
        });
    }
    private void incompleteTask() {


        RequestBody status = null, end_time = null, taskId = null;
        try {
            taskId = RequestBody.create(MediaType.parse("text/plain"), ApplicationClass.getTaskId());

            status = RequestBody.create(MediaType.parse("text/plain"), "inspection incompleted");
            end_time = RequestBody.create(MediaType.get("text/plain"), BaseActivity.getCurrentTimeStampWithZone());

        } catch (Exception e) {
            e.printStackTrace();
        }


        String acess_token = ConstantProject.API_HEADER + SharedPreferenceUtility.getAccessToken();


        apiService.CompleteTask(acess_token, taskId, status, end_time).enqueue(new Callback<UpdateTaskResponseModel>() {
            @Override
            public void onResponse(Call<UpdateTaskResponseModel> call, Response<UpdateTaskResponseModel> response) {

                if (response.code() == 200) {
                    Boolean status = true;
                    if (status == true && response.body().getData() != null) {
                        disconnect();

                        DialogManager.showUniActionDialog(ApplicationClass.getCurrentActivity(), response.body().getMessage(), "ok", new DialogManager.IUniActionDialogOnClickListener() {
                            @Override
                            public void onPositiveClick() {
                                Intent intent = new Intent(ApplicationClass.getCurrentContext(), LogoutActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                                startActivity(intent);
                            }
                        });


                    } else if (status == false) {
                        String msg = response.body().getMessage();
                        disconnect();
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
                disconnect();
                Intent intent = new Intent(ApplicationClass.getCurrentContext(), MainActivityDrawer.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                startActivity(intent);

            }

            @Override
            public void onFailure(Call<UpdateTaskResponseModel> call, Throwable t) {
                disconnect();
                Intent intent = new Intent(ApplicationClass.getCurrentContext(), MainActivityDrawer.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
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
                    //Toast.makeText(ApplicationClass.getCurrentContext(), R.string.OperfailedExtnd, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

}



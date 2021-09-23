package com.tinu.maintanceappliccation;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ApplicationInfo;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.method.ScrollingMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.tinu.maintanceappliccation.activity.BaseActivity;
import com.tinu.maintanceappliccation.activity.LogoutActivity;
import com.tinu.maintanceappliccation.models.HashApiResponse;
import com.tinu.maintanceappliccation.models.UpdateTaskResponseModel;
import com.tinu.maintanceappliccation.restApiCall.ApiClient;
import com.tinu.maintanceappliccation.restApiCall.ApiInterface;
import com.tinu.maintanceappliccation.utility.ConstantProject;
import com.tinu.maintanceappliccation.utility.DialogManager;
import com.tinu.maintanceappliccation.utility.SharedPreferenceUtility;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.BLUETOOTH_SERVICE;
import static com.tinu.maintanceappliccation.ApplicationClass.RANDOM_APP_KEY;
import static com.tinu.maintanceappliccation.activity.BaseActivity.isNetworkConnectionAvailable;

public class TerminalFragmentAutoLock extends Fragment implements ServiceConnection, SerialListener {

    private enum Connected {False, Pending, True}

    private String deviceAddress;
    private String newline = "\r\n";

    private TextView receiveText;
    CountDownTimer manageResponse, splitresponse,IhashValueSplit,manageResponseOHash;
    String hashValue;
    private SerialService service;
    private boolean initialStart = true;
    private TerminalFragmentAutoLock.Connected connected = TerminalFragmentAutoLock.Connected.False;
    ImageView iv_back;
    int connect = 0, lostConnection = 0;
    private boolean isTestLockCalled, isTestUnLockeCalled, isAwaitingForLock;
    private int count = 0;
    boolean checktheResponseisReceived;
    private ApiInterface apiService;
    double batteryPower = 0.0;
    private String commentSend = "", commandReceive = "", device_status = "",OHasValueSplit;
    private int statusCount = 0,OhashCount=0;;
    private String MSG1 = "";
    boolean isShowingBusyAniReconnect = false,  isIHashSend = false,checktheResponseisReceivedOHash =false;

    /*
     * Lifecycle
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        setRetainInstance(true);
        deviceAddress = getArguments().getString("device");
        apiService =
                ApiClient.getClient().create(ApiInterface.class);

    }

   /* @Override
    public void onDestroy() {
        if (connected != Connected.False)
            disconnect();
        getActivity().stopService(new Intent(getActivity(), SerialService.class));
        super.onDestroy();
    }*/

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
        if (service != null && !getActivity().isChangingConfigurations())
            service.detach();
        super.onStop();
    }

    @SuppressWarnings("deprecation")
    // onAttach(context) was added with API 23. onAttach(activity) works for all API versions
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        getActivity().bindService(new Intent(getActivity(), SerialService.class), this, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onDetach() {
        try {
            getActivity().unbindService(this);
        } catch (Exception ignored) {
        }
        super.onDetach();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (initialStart && service != null) {
            initialStart = false;
            getActivity().runOnUiThread(this::connect);
        }
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder binder) {
        service = ((SerialService.SerialBinder) binder).getService();
        service.attach(this);
        ApplicationClass.setService(service);
        if (initialStart && isResumed()) {
            initialStart = false;

            getActivity().runOnUiThread(this::connect);
        }
    }


    @Override
    public void onServiceDisconnected(ComponentName name) {
        service = null;
    }

    /*
     * UI
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_scan_terminal, container, false);
        receiveText = view.findViewById(R.id.receive_text);                          // TextView performance decreases with number of spans
        receiveText.setTextColor(getResources().getColor(R.color.colorBlack)); // set as default color to reduce number of spans
        receiveText.setMovementMethod(ScrollingMovementMethod.getInstance());
        iv_back = (ImageView) view.findViewById(R.id.iv_back);

        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                disconnect();
                Intent intent = new Intent(ApplicationClass.getCurrentContext(), MainActivityDrawer.class);

                intent.putExtra(ConstantProject.isFrom, ConstantProject.History);
                startActivity(intent);
            }
        });

        BluetoothAdapter bleAdapter = ((BluetoothManager) getActivity().getSystemService(BLUETOOTH_SERVICE)).getAdapter();
        Set<BluetoothDevice> pairedDevices = bleAdapter.getBondedDevices();
        for (BluetoothDevice d : pairedDevices) {
            if (d.getAddress().equals("Your Device MAC")) {
                d.connectGatt(getContext(), true, new BluetoothGattCallback() {
                    @Override
                    public void onConnectionStateChange(BluetoothGatt
                                                                gatt, int status, int newState) {
                        super.onConnectionStateChange(gatt, status, newState);
                        switch (newState) {
                            case BluetoothProfile.STATE_CONNECTED:
                                Log.i("GattCallback", "connected");
                                gatt.getServices();
                                break;
                            case BluetoothProfile.STATE_DISCONNECTED:
                                Log.i("GattCallback", "Disconnected");
                                break;
                        }
                    }
                });
            }
        }

        return view;
    }

    public void sentST() {
        ApplicationClass.getCurrentActivity().runOnUiThread(this::SentST);

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_terminal, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.clear) {
            receiveText.setText("");
            return true;
        } else if (id == R.id.newline) {

            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    /*
     * Serial + UI
     */
    private void connect() {
        try {
            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            // BluetoothDevice device = bluetoothAdapter.getRemoteDevice("28:B2:BD:C1:35:17");

            BluetoothDevice device = bluetoothAdapter.getRemoteDevice(deviceAddress);
            status("connecting...");
          //  Log.e("contents","TFA");
            connected = TerminalFragmentAutoLock.Connected.Pending;
            SerialSocket socket = new SerialSocket(getActivity().getApplicationContext(), device);
            service.connect(socket);
        } catch (Exception e) {
            onSerialConnectError(e);
        }
    }

    public void SentST() {
        send("st;");
    }

    private void disconnect() {
        connected = TerminalFragmentAutoLock.Connected.False;
        service.disconnect();
    }

    private void send(String str) {
        Log.e("contents ter_send", str);
        if (connected != TerminalFragmentAutoLock.Connected.True) {
            Toast.makeText(getActivity(), "not connected", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            commentSend = commentSend + str + ",";
            SpannableStringBuilder spn = new SpannableStringBuilder(str);
            spn.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorSendText)), 0, spn.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            receiveText.append(spn);
            byte[] data = (str).getBytes();
            service.write(data);
        } catch (Exception e) {
            onSerialIoError(e);
        }
    }

    private void receive(byte[] data) {
        receiveText.setText("");
        receiveText.append(new String(data));
        checktheResponseisReceived = true;
        statusCount = statusCount + 1;

        ApplicationClass.setIsAwaitingForLock(false);
        ApplicationClass.setIsNormalCase(false);
        ApplicationClass.setIsSendMM(false);
        ApplicationClass.setIsAutoLock(false);


        String N, S, L, P, B;

        String response = receiveText.getText().toString();
        if (response.contains("\r")) {
            response = response.replaceAll("\r", "");
            commandReceive = commandReceive + "," + response;
            Log.e("contents ter_rec",response);
        }
        /*Log.e("contents ter", response);
        String[] separated = response.split(",");
        if (response != null && response.contains("N") && response.contains("B")) {
            N = separated[0].substring(separated[0].length() - 1);
            Log.e("Receive", N);
            S = separated[1].substring(separated[1].length() - 1);
            Log.e("Receive", S);
            L = separated[2].substring(separated[2].length() - 1);
            Log.e("Receive", L);
            P = separated[3].substring(separated[3].length() - 1);
            Log.e("Receive", P);
            B = separated[4].substring(separated[4].length() - 5);
            Log.e("Receive", B);

            String textWithoutPrefix = B.substring(1);
            // short value = Short.parseShort(textWithoutPrefix, 16);
            if (manageResponse != null) {
                manageResponse.cancel();
                manageResponse = null;
            }
            // double balteryPower = 3.3 * (Double.valueOf(value) / Double.valueOf(65535)) * (43 / 10);
            batteryPower = checkBatteryPower(textWithoutPrefix);

            if (!isNetworkConnectionAvailable(ApplicationClass.getCurrentContext())) {
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
                //Toast.makeText(getContext(), "No internet connection", Toast.LENGTH_LONG).show();
            } else {
                //tinu
                //case1: s=0,l=0 not initial payment done ->normal case
                //case2: s=0,l=0 initial payment done -> logout after initial payment done
                // case3: s=1,l=0 initial payment done ->logout at manual lock_img done
                // case:4: s=1,l-1 initial payment done ->Not called mm
                if (batteryPower <= ConstantProject.batteryPower) {
                    DialogManager.showUniActionDialog(ApplicationClass.getCurrentActivity(), ApplicationClass.getCurrentContext().getResources().getString(R.string.btryPower) + " :" + batteryPower + "V", "ok", new DialogManager.IUniActionDialogOnClickListener() {
                        @Override
                        public void onPositiveClick() {
                            postErrorLog("s=0,L=0" + ApplicationClass.getCurrentContext().getResources().getString(R.string.btryPower));
                            device_status = ApplicationClass.getCurrentContext().getResources().getString(R.string.btryPower);
                            disconnect();
                            Intent intent = new Intent(ApplicationClass.getCurrentContext(), MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);

                        }
                    });

                }
                if (S.equalsIgnoreCase("0") && L.equalsIgnoreCase("0")) {

                    SharedPreferenceUtility.saveIsUpholdHandle(false);
                    if (!isTestLockCalled) {
                        postErrorLog("s=0,L=0" + " Unlocked");
                        device_status = "Unlocked";
                        isTestLockCalled = true;
                        Fragment fragment = new TestLockFragment();
                        getFragmentManager().beginTransaction().replace(R.id.fl_main, fragment, "terminal").addToBackStack(null).commit();

                    }


                } else if (S.equalsIgnoreCase("0") && L.equalsIgnoreCase("1")) {
                    if (!isTestLockCalled) {
                        postErrorLog("s=0,L=1");
                        device_status = "s=0,L=1";
                        SharedPreferenceUtility.saveIsUpholdHandle(true);
                        isTestLockCalled = true;
                        Fragment fragment = new TestLockFragment();
                        getFragmentManager().beginTransaction().replace(R.id.fl_main, fragment, "terminal").addToBackStack(null).commit();

                    }
                    *//*DialogManager.showUniActionDialog(ApplicationClass.getCurrentActivity(), ApplicationClass.getCurrentContext().getResources().getString(R.string.uphold_handle), "ok", new DialogManager.IUniActionDialogOnClickListener() {
                        @Override
                        public void onPositiveClick() {
                            ((BaseActivity) getActivity()).hideBusyAnimation();
                        }
                    });*//*

                } else if (S.equalsIgnoreCase("1") && L.equalsIgnoreCase("0")) {
                    SharedPreferenceUtility.saveIsUpholdHandle(false);
                    if (!isAwaitingForLock) {
                        device_status = "Hash Value saved";
                        postErrorLog("s=1,L=0");
                        isAwaitingForLock = true;
                        Fragment fragment = new AwaitingForLockFragment();
                        getFragmentManager().beginTransaction().replace(R.id.fl_main, fragment, "terminal").addToBackStack(null).commit();

                    }


                } else if (S.equalsIgnoreCase("1") && L.equalsIgnoreCase("1")) {
//send mm
                    SharedPreferenceUtility.saveIsUpholdHandle(true);

                    if (!isTestUnLockeCalled) {
                        device_status = "Locked";
                        postErrorLog("s=1,L=1" + " locked");
                        isTestUnLockeCalled = true;
                        Fragment fragment = new TestUnlockFragment();
                        getFragmentManager().beginTransaction().replace(R.id.fl_main, fragment, "terminal").addToBackStack(null).commit();

                    }


                }


            }


        }*/
        Log.e("contents statusCount", ""+statusCount);
        if (statusCount == 1) {
            ApplicationClass.getCurrentActivity().runOnUiThread(this::SendIN);
        } else if (statusCount == 2) {
            RANDOM_APP_KEY = "ABCD";
            ApplicationClass.setRandomAppKey(RANDOM_APP_KEY);
            MSG1 = "M:" + RANDOM_APP_KEY + ";";

            getActivity().runOnUiThread(this::SendMSG1);

        } else if (statusCount == 3) {
            String messageResponse = response;
            if (messageResponse != null && messageResponse.length() > 0 && messageResponse.contains("P") && messageResponse.contains("X")
                    && messageResponse.contains("Y") && messageResponse.contains("Z")) {
                // message is response of the MSG1;
                //“P:X=15,Y=5,Z=5”.
                if (messageResponse.contains("\r")) {
                    messageResponse = messageResponse.replaceAll("\r", "");

                }
                String[] separated = messageResponse.split(",");
                // value of N

                String X = separated[0].substring(separated[0].indexOf("=") + 1, separated[0].length());
                String Y = separated[1].substring(separated[1].indexOf("=") + 1, separated[1].length());
                String Z = separated[2].substring(separated[2].indexOf("=") + 1, separated[2].length());

/// Call Hash Value API


                getComposeHashmsg(ApplicationClass.getScannedDeviceCode(), "close", X, Y, Z);
            }

        }
        else if (statusCount==4)
        {
            if (splitresponse != null) {
                splitresponse.cancel();
                splitresponse = null;
            }
            SendMM();

        }
        else if (statusCount==6)
        {
            getActivity().runOnUiThread(this::SendIHashValue);
        }
        else if (statusCount==7)
        {
            RANDOM_APP_KEY="ABCD";
            ApplicationClass.setRandomAppKey(RANDOM_APP_KEY);
            MSG1 = "M:" + RANDOM_APP_KEY + ";";


            ApplicationClass.getCurrentActivity().runOnUiThread(this::SendMSG1);
        }
        else if (statusCount==8) {
            String X, Y, Z, messageResponse = response;
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
             //   Log.e("Receive", X);
                Y = separated[1].substring(separated[1].indexOf("=") + 1, separated[1].length());
              //  Log.e("Receive", Y);
                Z = separated[2].substring(separated[2].indexOf("=") + 1, separated[2].length());
             //   Log.e("Receive", Z);

/// Call Hash Value API

                getHashValuesAtUnLock(ApplicationClass.getScannedDeviceCode(), "open", X, Y, Z);

            }
        }
        else if (statusCount==9) {
            ((BaseActivity) getActivity()).hideBusyAnimation();
            ((BaseActivity) getActivity()).hideBusyAnimation();
            ApplicationClass.getCurrentActivity().runOnUiThread(this::sendrr);
            completeTask();
        }
    }


    private void SendIN() {

        send("IN;");

    }

    private void SendMSG1() {

        send(MSG1);
    }

    private void status(String str) {
        SpannableStringBuilder spn = new SpannableStringBuilder(str + '\n');
        spn.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorStatusText)), 0, spn.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        receiveText.append(spn);
    }

    /*
     * SerialListener
     */
    @Override
    public void onSerialConnect() {
        status("connected");
        connected = TerminalFragmentAutoLock.Connected.True;

        ((BaseActivity) getActivity()).showBusyAnimation("");

        manageResponse = new CountDownTimer(ConstantProject.TimeoutFiftySecond, 1000) {
            //check each 2 seconds
            public void onTick(long millisUntilFinished) {

                if (!checktheResponseisReceived) {
                    sentST();
                } else {
                    if (manageResponse != null) {
                        manageResponse.cancel();
                        manageResponse = null;
                    }

                }


            }


            public void onFinish() {


            }
        }.start();


    }

    @Override
    public void onSerialConnectError(Exception e) {
        status("connection failed: " + e.getMessage());
       // Log.e("contents", e.toString());
        disconnect();
    }

    @Override
    public void onSerialRead(byte[] data) {
        receive(data);
    }


    @Override
    public void onSerialIoError(Exception e) {
        status("connection lost: " + e.getMessage());
     //   Log.e("contents", e.toString());
        disconnect();
    }

    public double checkBatteryPower(String input) {

        int decimal = Integer.parseInt(input, 16);

        double power = new Double(decimal) / new Double(65535);
        double baltteryPower = 3.3 * power;
        baltteryPower = baltteryPower * 4.3;
        return baltteryPower;

    }

    public void postErrorLog(String comments) {

        Map<String, Object> candidateMap = new HashMap<>();
        candidateMap.put("device_id", ApplicationClass.getScannedDeviceCode());
        candidateMap.put("app", "Android");
        candidateMap.put("device_status", device_status);

        candidateMap.put("device_model", ApplicationClass.getDevice_name());
        candidateMap.put("device_version", ApplicationClass.getDevice_version());
        candidateMap.put("scanned_qr_code", ApplicationClass.getScannedDeviceCode());
        candidateMap.put("available_blt_devices", ApplicationClass.getDeviceList());
        candidateMap.put("command_sent", commentSend);
        candidateMap.put("command_received", commandReceive);
        candidateMap.put("btryPower", batteryPower);
        candidateMap.put("error_message_en", "Debug Log");
        candidateMap.put("error_message_jp", "デバッグログ");
        candidateMap.put("comments", comments);//commentsForAppLog

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

    public void getComposeHashmsg(final String deviceId, final String lckOrUnlck, String
            X_value, String Y_value, String Z_value) {
     //   Log.e("content", "x" + X_value + "y=" + Y_value + "Z=" + Z_value);

        Map<String, Object> candidateMap = new HashMap<>();
        candidateMap.put("device_id", ApplicationClass.getScannedDeviceCode());
        candidateMap.put("command_text", lckOrUnlck);
        candidateMap.put("random_app_key", ApplicationClass.getRandomAppKey());
        candidateMap.put("starting_position", X_value);
        candidateMap.put("key_string_length", Y_value);
        candidateMap.put("skip_chars", Z_value);

        String acess_token = ConstantProject.API_HEADER + SharedPreferenceUtility.getAccessToken();

        apiService.getHashValues(acess_token, candidateMap).enqueue(new Callback<HashApiResponse>() {
            @Override
            public void onResponse(Call<HashApiResponse> call, Response<HashApiResponse> response) {

             //   ((BaseActivity) getActivity()).hideBusyAnimation();
                if (response != null && response.body().getData() != null) {


                    String HASH_VALUE = response.body().getData();

                    SharedPreferenceUtility.saveHashValueForUnlock(HASH_VALUE);


                    SendHashValue(HASH_VALUE);


                }


            }


            @Override
            public void onFailure(Call<HashApiResponse> call, Throwable t) {

                if (!BaseActivity.isNetworkConnectionAvailable(ApplicationClass.getCurrentContext())) {
                    Toast.makeText(ApplicationClass.getCurrentContext(), ApplicationClass.getCurrentContext().getResources().getString(R.string.OperfailedExtnd), Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(ApplicationClass.getCurrentContext(), R.string.OperfailedExtnd, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void SendHashValue(String HASH_VALUE) {

        // send("C:" + HASH_VALUE + ";");
        String hashValue = "C:" + HASH_VALUE + ";";//35

        List<String> list = new ArrayList<String>();

        int index = 0;
        while (index < hashValue.length()) {

            list.add(hashValue.substring(index, Math.min(index + 8, hashValue.length())));
            index = index + 8;
        }

        splitresponse = new CountDownTimer(2000, 100) {

            public void onTick(long millisUntilFinished) {
                if (list.size() > count && count < 8) {
                    send(list.get(count));
                }
                count++;


            }

            public void onFinish() {
                if (splitresponse != null) {
                    splitresponse.cancel();
                    splitresponse = null;
                }


            }
        }.start();

    }

    private void SendMM() {
        ApplicationClass.getCurrentActivity().runOnUiThread(this::sendmm);

    }

    public void sendmm() {
        send("MM;");
    }

    private void SendIHashValue() {
        String IHashValue = SharedPreferenceUtility.getHashValueForUnlock();

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


                // if (response.code() == 200 && response.body().getSuccess() == true) {
/// sending hash value to Device


                if (response.body().getData() != null && !response.body().getData().isEmpty()) {
                    // isMSG1UnlockSent=false;

                    String   HASH_VALUE = response.body().getData();
                     hashValue= "O:" + HASH_VALUE + ";";//35

                 //   Log.e("HashSplit",hashValue);

                    ApplicationClass.getCurrentActivity().runOnUiThread(this::hashValue);

                }


            }

            private void hashValue() {
                send(hashValue);
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
    private void sendrr() {
        send("RR;");
    }
    private void completeTask() {
        ApplicationClass.setIsAutoLock(false);

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


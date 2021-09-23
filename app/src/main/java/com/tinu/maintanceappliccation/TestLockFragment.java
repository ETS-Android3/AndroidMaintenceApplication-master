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
import android.content.res.Configuration;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.tinu.maintanceappliccation.activity.BaseActivity;
import com.tinu.maintanceappliccation.models.HashApiResponse;
import com.tinu.maintanceappliccation.models.LogAppErrorModel;
import com.tinu.maintanceappliccation.restApiCall.ApiClient;
import com.tinu.maintanceappliccation.restApiCall.ApiInterface;
import com.tinu.maintanceappliccation.utility.ConstantProject;
import com.tinu.maintanceappliccation.utility.DialogManager;
import com.tinu.maintanceappliccation.utility.SharedPreferenceUtility;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.BLUETOOTH_SERVICE;
import static com.tinu.maintanceappliccation.ApplicationClass.RANDOM_APP_KEY;
import static com.tinu.maintanceappliccation.utility.ConstantProject.ALPHACAPS;
import static com.tinu.maintanceappliccation.utility.ConstantProject.ALPHASMALL;
import static com.tinu.maintanceappliccation.utility.ConstantProject.ALPHA_NUMERIC_STRING;
import static com.tinu.maintanceappliccation.utility.ConstantProject.DIGITS;


public class TestLockFragment extends Fragment implements ServiceConnection, SerialListener {
    private enum Connected {False, Pending, True}


    private TextView receiveText;
    private String languageToLoad;
    private SerialService service;
    private boolean initialStart = true;
    private Connected connected = Connected.True;
    int connect = 0, lostConnection = 0;
    String SecurityCurrentStatus = "";
    Button btn_lock;
    String RESPONSE_MSG;
    String MSG1, HASH_VALUE;
    ApiInterface apiService;
    TextView tv_show,tv_devcieName;
    boolean isHashValueSend;
    ImageView iv_back;
    String previousResponse = "", currentResponse = "";
    boolean checktheResponseisReceived;
    CountDownTimer manageResponse,splitresponse;
    int count=0;
    String LastSend,LastInput;
    private String sendCommands;
    private String receiveCommands;


    /*
     * Lifecycle
     */
    public TestLockFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //deviceAddress = getArguments().getString("device");
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
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (service == null) {
            service = ApplicationClass.getService();
        }
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder binder) {
        service = ApplicationClass.getService();
        if (service != null)
            service.attach(this);
        else
            getActivity().startService(new Intent(getActivity(), SerialService.class)); // prevents service destroy on unbind from recreated activity caused by orientation change


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

        View view = inflater.inflate(R.layout.activity_test_lock, container, false);
        receiveText = view.findViewById(R.id.receiveText);                          // TextView performance decreases with number of spans
        receiveText.setTextColor(getResources().getColor(R.color.colorBlack)); // set as default color to reduce number of spans
        receiveText.setMovementMethod(ScrollingMovementMethod.getInstance());
        receiveText.setVisibility(View.GONE);
        btn_lock = view.findViewById(R.id.btn_lock);
        apiService = ApiClient.getClient().create(ApiInterface.class);
        tv_show = view.findViewById(R.id.tv_show);
       tv_show.setVisibility(View.GONE);
        tv_devcieName =view.findViewById(R.id.tv_devcieName);
        iv_back =view.findViewById(R.id.iv_back);

        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                disconnect();
                Intent intent = new Intent(ApplicationClass.getCurrentContext(), MainActivityDrawer.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                startActivity(intent);
            }
        });

        /*BluetoothAdapter bleAdapter = ((BluetoothManager) getActivity().getSystemService(BLUETOOTH_SERVICE)).getAdapter();
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
*/

        if (ApplicationClass.isIsAutoLock())
        {

           // ApplicationClass.getCurrentActivity().runOnUiThread(this::autoLock);
        }
        btn_lock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((BaseActivity) getActivity()).showBusyAnimation("");


                manageResponse = new CountDownTimer(ConstantProject.TimeoutFiveSecond, 1000) {
                    //check each 2 seconds
                    public void onTick(long millisUntilFinished) {

                        if (!checktheResponseisReceived)
                        {
                            checktheResponseisReceived=false;

                                SendIN();



                        }
                        else {

                            if (manageResponse != null) {
                                manageResponse.cancel();
                                manageResponse = null;
                            }



                        }



                    }


                    public void onFinish() {


                        disconnect();
                        ApplicationClass.setIsAutoLock(true);

                        Bundle args = new Bundle();
                        args.putString("device",
                                ApplicationClass.getBLEAddress());
                        Fragment fragment = new TerminalFragmentAutoLock();
                        fragment.setArguments(args);
                        getFragmentManager().beginTransaction().replace(R.id.fl_main, fragment, "terminal").addToBackStack(null).commit();

                        if (manageResponse != null) {
                            manageResponse.cancel();
                            manageResponse = null;
                        }

                    }
                }.start();


            }
        });

        tv_devcieName.setText(ApplicationClass.getDeviceName());
        return view;


    }

    public void SendMM() {
        ApplicationClass.getCurrentActivity().runOnUiThread(this::sendmm);

    }


    public void sendmm() {
        send("MM;");
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

            BluetoothDevice device = bluetoothAdapter.getRemoteDevice(ApplicationClass.getBLEAddress());
            status("connecting...");
            connected = Connected.Pending;
            SerialSocket socket = new SerialSocket(getActivity().getApplicationContext(), device);
            service.connect(socket);
        } catch (Exception e) {
            onSerialConnectError(e);
        }
    }

    private void disconnect() {
        connected = Connected.False;
        service.disconnect();
    }

    private void send(String str) {
        LastSend=str;
        Log.e("contents Send:Lock", str);
        tv_show.setText(tv_show.getText().toString()+"\n" + "receive:" + str);
      //  sentAppLog("Test:Send "+str);
        sendCommands = sendCommands+str;
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

    private void receive(byte[] data) {
        StringWriter sw = new StringWriter();
        checktheResponseisReceived=true;
        sw.append(new String(data));
        tv_show.setText(tv_show.getText().toString()+"\n" + "receive:" + sw.toString());
        String response = "";
        receiveText.setText("");
        receiveText.append(new String(data));
        String N, S, L, P, B;
        String X, Y, Z;


        // Toast.makeText(CarryParkApplication.getCurrentContext(), "" + receiveText.getText().toString(), Toast.LENGTH_SHORT).show();
        RESPONSE_MSG = receiveText.getText().toString();
        LastInput=RESPONSE_MSG;
        if (manageResponse != null) {
            manageResponse.cancel();
            manageResponse = null;
        }

        currentResponse = RESPONSE_MSG;
        if (!currentResponse.equalsIgnoreCase(previousResponse)) {
          //  sentAppLog("Test:I:R");
            receiveCommands =receiveCommands+currentResponse;
            Log.e("contents Re", RESPONSE_MSG);
            previousResponse=RESPONSE_MSG;
            if (RESPONSE_MSG.contains("\r")) {
                RESPONSE_MSG = RESPONSE_MSG.replaceAll("\r", "");

            }

            if (SecurityCurrentStatus.equalsIgnoreCase(ConstantProject.Sec_Status_Lock_IN)) {

                LockSecurityReadinAlearts(RESPONSE_MSG);
                if (manageResponse != null) {
                    manageResponse.cancel();
                    manageResponse = null;
                }
            } else if (SecurityCurrentStatus.equalsIgnoreCase(ConstantProject.Sec_Status_Lock_MSG1)) {
                ////


                String messageResponse = RESPONSE_MSG;
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
                    Y = separated[1].substring(separated[1].indexOf("=") + 1, separated[1].length());
                    Z = separated[2].substring(separated[2].indexOf("=") + 1, separated[2].length());

/// Call Hash Value API


                    getComposeHashmsg(ApplicationClass.getScannedDeviceCode(), "close", X, Y, Z);
                } else {

                }


                ////
            } else if (SecurityCurrentStatus.equalsIgnoreCase(ConstantProject.Sec_Status_Lock_C_HashValue)) {

                if (RESPONSE_MSG.contains("C:R=NG") || RESPONSE_MSG.contains("ER")) {
                    ((BaseActivity) getActivity()).hideBusyAnimation();

                    DialogManager.showUniActionDialog(ApplicationClass.getCurrentActivity(), ApplicationClass.getCurrentContext().getResources().getString(R.string.device_auth_failed), "ok", new DialogManager.IUniActionDialogOnClickListener() {
                        @Override
                        public void onPositiveClick() {

                            //REMOVE IN PRODUCTION
                       Fragment fragment = new AwaitingForLockFragment();

                        getFragmentManager().beginTransaction().replace(R.id.fl_main, fragment, "").addToBackStack(null).commit();



                       /* disconnect();
                        Intent intent = new Intent(ApplicationClass.getCurrentContext(), MainActivityDrawer.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
*/

                        }


                    });
                }

                response = RESPONSE_MSG;

                if (response.contains("\r"))
                    response = response.replaceAll("\r", "");


                if (response.contains("S") && response.contains("N") && response.contains("L") && response.contains("P") && response.contains("B")) {
                    String[] separated = response.split(",");

                    N = separated[0].substring(separated[0].length() - 1);
                    Log.e("Receive", N);
                    S = separated[1].substring(separated[1].length() - 1);
                    Log.e("Receive", S);
                    L = separated[2].substring(separated[2].length() - 1);
                    Log.e("Receive", L);
                    B = separated[4].substring(separated[4].length() - 5);
                    Log.e("Receive", B);


                    if (S.equalsIgnoreCase("1") && L.equalsIgnoreCase("0")) {

                        Fragment fragment = new AwaitingForLockFragment();

                        getFragmentManager().beginTransaction().replace(R.id.fl_main, fragment, "").addToBackStack(null).commit();


                    }
                    if (S.equalsIgnoreCase("1") && L.equalsIgnoreCase("1")) {



                        Fragment fragment = new TestUnlockFragment();

                        getFragmentManager().beginTransaction().replace(R.id.fl_main, fragment, "").addToBackStack(null).commit();


                    }
                    //REMOVE IN PRODUCTION
            /* Fragment fragment = new AwaitingForLockFragment();

                getFragmentManager().beginTransaction().replace(R.id.fl_main, fragment, "").addToBackStack(null).commit();
*/

                }

            }



        }


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
        connected = Connected.True;

        ((BaseActivity) getActivity()).hideBusyAnimation();


    }

    @Override
    public void onSerialConnectError(Exception e) {
        status("connection failed: " + e.getMessage());
        disconnect();
    }

    @Override
    public void onSerialRead(byte[] data) {
        receive(data);
    }


    @Override
    public void onSerialIoError(Exception e) {
        status("connection lost: " + e.getMessage());
        disconnect();
    }


    /// sending

    private void SendIN() {
        SecurityCurrentStatus = ConstantProject.Sec_Status_Lock_IN;
        send("IN;");

    }
    private void SendST() {
        SecurityCurrentStatus = ConstantProject.Sec_Status_Lock_IN;
        send("st;");

    }

    private void SendMSG1() {
        SecurityCurrentStatus = ConstantProject.Sec_Status_Lock_MSG1;
        send(MSG1);
    }


    public void LockSecurityReadinAlearts(String messageResponse) {
        {//S:N=0,S=0,L=0,P=0,B=5b40

            // messageResponse ="S: N=1, S=0, L=0, P=0, B=5b40";
            String response = messageResponse.replaceAll("\r", "");


            String N, S, L, P, B;


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

                double batteryPower = checkBatteryPower(textWithoutPrefix);

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
                    //Toast.makeText(getContext(), "No internet connection", Toast.LENGTH_LONG).show();
                } else {
                    //tinu
                    //case1: s=0,l=0 not initial payment done ->normal case
                    //case2: s=0,l=0 initial payment done -> logout after initial payment done
                    // case3: s=1,l=0 initial payment done ->logout at manual lock_img done
                    // case:4: s=1,l-1 initial payment done ->Not called mm
                    if (batteryPower <= ConstantProject.balteryPower) {
                        DialogManager.showUniActionDialog(ApplicationClass.getCurrentActivity(), ApplicationClass.getCurrentContext().getResources().getString(R.string.btryPower) + " :" + batteryPower, "ok", new DialogManager.IUniActionDialogOnClickListener() {
                            @Override
                            public void onPositiveClick() {
//REMOVE AT PRODUCTION
                               /* String RANDOM_APP_KEY_all = randomAlphaNumeric(1);
                                String RANDOM_Digit = randomDigit(1);
                                String RANDOM_ALPHACAPS = randomAlphaCaps(1);
                                String RANDOM_ALPHASMALL = randomAlphaSmall(1);
                                RANDOM_APP_KEY = RANDOM_APP_KEY_all + RANDOM_Digit + RANDOM_ALPHACAPS + RANDOM_ALPHASMALL;
                              */
                                RANDOM_APP_KEY="ABCD";
                               ApplicationClass.setRandomAppKey(RANDOM_APP_KEY);
                                MSG1 = "M:" + RANDOM_APP_KEY + ";";

                                getActivity().runOnUiThread(this::SendMSG1);
                            }
                            private void SendMSG1() {
                                SecurityCurrentStatus = ConstantProject.Sec_Status_Lock_MSG1;
                                send(MSG1);
                            }
                        });

                    } else {

                        String RANDOM_APP_KEY_all = randomAlphaNumeric(1);
                        String RANDOM_Digit = randomDigit(1);
                        String RANDOM_ALPHACAPS = randomAlphaCaps(1);
                        String RANDOM_ALPHASMALL = randomAlphaSmall(1);
                        RANDOM_APP_KEY ="ABCD";
                        ApplicationClass.setRandomAppKey(RANDOM_APP_KEY);
                        MSG1 = "M:" + RANDOM_APP_KEY + ";";

                        getActivity().runOnUiThread(this::SendMSG1);

                    }

                }


            }

/// end: 1. Lock response of IN;

/// st: 1. Lock response of IN;


        }

    }

    public double checkBatteryPower(String input) {

        int decimal = Integer.parseInt(input, 16);

        double power = new Double(decimal) / new Double(65535);
        double baltteryPower = 3.3 * power;
        baltteryPower = baltteryPower * 4.3;
        return baltteryPower;

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

    public void getComposeHashmsg(final String deviceId, final String lckOrUnlck, String X_value, String Y_value, String Z_value) {


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

                ((BaseActivity) getActivity()).hideBusyAnimation();
                if (response != null && response.body().getData() != null) {

                    isHashValueSend = true;
                    HASH_VALUE = response.body().getData();
                    tv_show.setText(tv_show.getText().toString()+":: "+"HashValue ApiResponse"+HASH_VALUE+ "with"+ApplicationClass.getScannedDeviceCode()+","+lckOrUnlck+","+ApplicationClass.getRandomAppKey());

                    SharedPreferenceUtility.saveHashValueForUnlock(HASH_VALUE);
                    SecurityCurrentStatus = ConstantProject.Sec_Status_Lock_C_HashValue;
                    ApplicationClass.getCurrentActivity().runOnUiThread(this::SendHashValue);




                }


            }

            public void SendHashValue() {
                SecurityCurrentStatus = ConstantProject.Sec_Status_Lock_C_HashValue;
                // send("C:" + HASH_VALUE + ";");
                String hashValue= "C:" + HASH_VALUE + ";";//35
                tv_show.setText(tv_show.getText().toString()+"HashValue sent"+hashValue);
                List<String> list= new ArrayList<String>();

                int index = 0;
                while (index<hashValue.length()) {

                    list.add(hashValue.substring(index, Math.min(index+8,hashValue.length())));
                    index=index+8;
                }

                splitresponse = new CountDownTimer(2000, 100) {

                    public void onTick(long millisUntilFinished) {
                        if (list.size()>count && count<8)
                        {
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


    public void sentAppLog(String comments) {


        String acess_token = "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiIsImp0aSI6IjNiZGE4OWU5YzE5Y2JmZGVmMTRlYzMwN2MxY2RkYzczMGFlODU3YWJhM2NiYWIwNWYzMDY5OTcyODZkZmJjMmY0ZWQ0ZTdlNDFhYTNiZDUwIn0.eyJhdWQiOiIxIiwianRpIjoiM2JkYTg5ZTljMTljYmZkZWYxNGVjMzA3YzFjZGRjNzMwYWU4NTdhYmEzY2JhYjA1ZjMwNjk5NzI4NmRmYmMyZjRlZDRlN2U0MWFhM2JkNTAiLCJpYXQiOjE2MDA0Mjc2OTUsIm5iZiI6MTYwMDQyNzY5NSwiZXhwIjoxNjMxOTYzNjk1LCJzdWIiOiIzMDMiLCJzY29wZXMiOlsiKiJdfQ.Q7Rt0KSB14OTBRvYdIyKVU8z11oqQi3tRNP1L0rrka2VilCzEL8iPE0GjSu1mzkw8nUxROd7_-cdn9id1mla9mNHfcFisYu3Sg2glNBpD6-eS0EQG-ZncrtyYyl0-z8mgiE0Io2Rw_fvY0GgIB8YSyoQQs_kYDOQqD31et7pMx2QoNAR0p0A0gwGWFWcbV24Hc6eyyhuJuj4XNXjItMNDTiykPABRJfELkdvU_2_XmNvSPiSA6awcKdgVcphGDvGZPqtn2nqT5Y1EGvZ6xIPYHCXTp8hCKG80wCmrmssp7fD8hrYwoOv75t8LfnEWa61IudjWVd3IaDIhDlHlHDs_HfmgT-4-wBApuJm7sDU2R1rMqbxf5xN5f4sBEokJ5IZLKP7FB6qryg2JCiNDnqOJHOMF5ekd5eU0NtfUEhrnbvPC8VVfah8KAprs2sR7V_nCW2jxtFKaVq5Kn29PKctBnTLatOwsrD-sA7NxutpEm0NNF61P_WQRR8y4ht1JFf9a2JYyL5lOnJciunmjdNnVtZzSY1MQ-mnZsjKe7N4YA-xjfel1Q8wSepgd2Ye1HPz-jG7fz3W_nKXSbtliJM9LeFGNWklg-tydlfI6AMYWoKf9Of42uNkKYAJq2D7iRr506CW-b_5pIBgXS_GISjz1-s-3AMNd-rj2HCgpgDO3pY";
        Map<String, Object> candidateMap = new HashMap<>();
        candidateMap.put("device_id", "CP001-2C28");
        candidateMap.put("app", "Android");
        candidateMap.put("device_status", tv_show.getText().toString());
        candidateMap.put("payment_require", "");
        candidateMap.put("device_model", "");
        candidateMap.put("device_version", "test version");
        candidateMap.put("scanned_qr_code","");
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


    public void autoLock()
    {
        ((BaseActivity) getActivity()).showBusyAnimation("");


        manageResponse = new CountDownTimer(ConstantProject.TimeoutFiftySecond, 1000) {
            //check each 2 seconds
            public void onTick(long millisUntilFinished) {

                if (!checktheResponseisReceived)
                {
                    checktheResponseisReceived=false;

                    SendIN();



                }
                else {
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
}


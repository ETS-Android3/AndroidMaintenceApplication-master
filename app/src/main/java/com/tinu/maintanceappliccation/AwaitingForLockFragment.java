package com.tinu.maintanceappliccation;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.media.MediaPlayer;
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
import com.tinu.maintanceappliccation.models.LogAppErrorModel;
import com.tinu.maintanceappliccation.models.SuccessApiResponse;
import com.tinu.maintanceappliccation.restApiCall.ApiClient;
import com.tinu.maintanceappliccation.restApiCall.ApiInterface;
import com.tinu.maintanceappliccation.utility.ConstantProject;
import com.tinu.maintanceappliccation.utility.DialogManager;
import com.tinu.maintanceappliccation.utility.SharedPreferenceUtility;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AwaitingForLockFragment extends Fragment implements ServiceConnection, SerialListener {
    Fragment targetFragment = null;
    Button button_login;
    private boolean initialStart = true;

    private enum Connected {False, Pending, True}

    private Connected connected = Connected.True;
    private TextView receiveText;
    CountDownTimer waitTimer;
    ApiInterface apiService;
    private SerialService service;
    String SecStatus="";
    TextView tv_device_name;
    int count=0;
    private String languageToLoad,LastSen="",LastInput="";

    public AwaitingForLockFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
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


        return inflater.inflate(R.layout.activity_manual_lock, container, false);

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (service == null) {
            service = ApplicationClass.getService();
        }
        apiService =
                ApiClient.getClient().create(ApiInterface.class);
        receiveText =view.findViewById(R.id.receiveText);
        tv_device_name =view.findViewById(R.id.tv_device_name);

        tv_device_name.setText(ApplicationClass.getDeviceName());

        SecStatus= ConstantProject.Sec_Status_Lock_status_checking;
        if (SharedPreferenceUtility.getIsUpholdHandle())
        {
            SendMM();

            Fragment fragment = new TestUnlockFragment();

            getFragmentManager().beginTransaction().replace(R.id.fl_main, fragment, "").addToBackStack(null).commit();

        }
        else {
            checkStatusEveryTenSecond();
        }



    }


    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        service = ApplicationClass.getService();
        if (service != null)
            service.attach(this);
        else
            getActivity().startService(new Intent(getActivity(), SerialService.class)); // prevents service destroy on unbind from recreated activity caused by orientation change


    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
        service = null;
    }

    public void onSerialConnect() {


        connected = Connected.True;

    }

    @Override
    public void onSerialConnectError(Exception e) {
        DialogManager.showUniActionDialog(ApplicationClass.getCurrentActivity(), ApplicationClass.getCurrentContext().getResources().getString(R.string.Failed_connected) + " " + ApplicationClass.getDeviceName(), "ok", new DialogManager.IUniActionDialogOnClickListener() {
            @Override
            public void onPositiveClick() {
                connect();

            }
        });
    }

    @Override
    public void onSerialRead(byte[] data) {
        receive(data);
    }

    @Override
    public void onSerialIoError(Exception e) {


        DialogManager.showUniActionDialog(ApplicationClass.getCurrentActivity(), ApplicationClass.getCurrentContext().getResources().getString(R.string.Failed_connected) + " " + ApplicationClass.getDeviceName(), "ok", new DialogManager.IUniActionDialogOnClickListener() {
            @Override
            public void onPositiveClick() {
                connect();

            }
        });


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
        Log.e("contents Send:Lock", str);
        LastSen=str;
      //  sentAppLog(str);
        if (connected != Connected.True) {
            Toast.makeText(getActivity(), "not connected", Toast.LENGTH_SHORT).show();
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
        receiveText.setText("");
        receiveText.append(new String(data));
        LastInput=receiveText.getText().toString();
        Log.e("contents Re", receiveText.getText().toString());
      //  sentAppLog(LastInput);
        String message = receiveText.getText().toString();
           message = message.replaceAll("\r", "");
            if (message.equalsIgnoreCase("C:R=OK")) {


            }
            String N, S, L, B, response = null;
            response = message.replaceAll("\r", "");
            if ( response.contains("S") && response.contains("L") ) {

                String[] separated = response.split(",");

                N = separated[0].substring(separated[0].length() - 1);
                Log.e("Receive", N);
                S = separated[1].substring(separated[1].length() - 1);
                Log.e("Receive", S);
                L = separated[2].substring(separated[2].length() - 1);
                Log.e("Receive", L);
                B = separated[4].substring(separated[4].length() - 5);
                Log.e("Receive", B);


                if (S.equalsIgnoreCase("1") && L.equalsIgnoreCase("1")) {
                    ((BaseActivity) getActivity()).hideBusyAnimation();

                    if (waitTimer != null) {
                        waitTimer.cancel();
                        waitTimer = null;
                    }
                    SendMM();

                    Fragment fragment = new TestUnlockFragment();

                    getFragmentManager().beginTransaction().replace(R.id.fl_main, fragment, "").addToBackStack(null).commit();


                    // CarryParkApplication.setIsStatusChecking(false);

                } else {
                    if (SharedPreferenceUtility.getIsUpholdHandle())
                    {
                        if (waitTimer != null) {
                            waitTimer.cancel();
                            waitTimer = null;
                        }
                        SendMM();

                        Fragment fragment = new TestUnlockFragment();

                        getFragmentManager().beginTransaction().replace(R.id.fl_main, fragment, "").addToBackStack(null).commit();


                    }
                   /*
*/
                    //REMOVE IN PRODUCTION
                  /*count++;

                    if (count==5)
                    {
                        if (waitTimer != null) {
                            waitTimer.cancel();
                            waitTimer = null;
                        }
                        Fragment fragment = new TestUnlockFragment();

                        getFragmentManager().beginTransaction().replace(R.id.fl_main, fragment, "").addToBackStack(null).commit();


                    }*/
                }


            }





    }

    private void SendMM() {
        ApplicationClass.getCurrentActivity().runOnUiThread(this::sendmm);

    }

    public void sendmm() {
        send("MM;");
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        getActivity().bindService(new Intent(getActivity(), SerialService.class), this, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (service != null)
            service.attach(this);
        else
            getActivity().startService(new Intent(getActivity(), SerialService.class)); // prevents service destroy on unbind from recreated activity caused by orientation change
    }


    private void checkStatusEveryTenSecond() {
        waitTimer = new CountDownTimer(ConstantProject.TimoutThreeminitue, 3000) {

            public void onTick(long millisUntilFinished) {
                //called every 10 second, which could be used to
                //send messages or some other action

                send("st;");
            }

            public void onFinish() {
                if (waitTimer != null) {
                    waitTimer.cancel();
                    waitTimer = null;
                }
                //After 60000 milliseconds (60 sec) finish current
                //if you would like to execute something when time finishes
            }
        }.start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (waitTimer != null) {
            waitTimer.cancel();
            waitTimer = null;
        }
    }

    public void sentAppLog(String comments) {


        String acess_token = "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiIsImp0aSI6IjNiZGE4OWU5YzE5Y2JmZGVmMTRlYzMwN2MxY2RkYzczMGFlODU3YWJhM2NiYWIwNWYzMDY5OTcyODZkZmJjMmY0ZWQ0ZTdlNDFhYTNiZDUwIn0.eyJhdWQiOiIxIiwianRpIjoiM2JkYTg5ZTljMTljYmZkZWYxNGVjMzA3YzFjZGRjNzMwYWU4NTdhYmEzY2JhYjA1ZjMwNjk5NzI4NmRmYmMyZjRlZDRlN2U0MWFhM2JkNTAiLCJpYXQiOjE2MDA0Mjc2OTUsIm5iZiI6MTYwMDQyNzY5NSwiZXhwIjoxNjMxOTYzNjk1LCJzdWIiOiIzMDMiLCJzY29wZXMiOlsiKiJdfQ.Q7Rt0KSB14OTBRvYdIyKVU8z11oqQi3tRNP1L0rrka2VilCzEL8iPE0GjSu1mzkw8nUxROd7_-cdn9id1mla9mNHfcFisYu3Sg2glNBpD6-eS0EQG-ZncrtyYyl0-z8mgiE0Io2Rw_fvY0GgIB8YSyoQQs_kYDOQqD31et7pMx2QoNAR0p0A0gwGWFWcbV24Hc6eyyhuJuj4XNXjItMNDTiykPABRJfELkdvU_2_XmNvSPiSA6awcKdgVcphGDvGZPqtn2nqT5Y1EGvZ6xIPYHCXTp8hCKG80wCmrmssp7fD8hrYwoOv75t8LfnEWa61IudjWVd3IaDIhDlHlHDs_HfmgT-4-wBApuJm7sDU2R1rMqbxf5xN5f4sBEokJ5IZLKP7FB6qryg2JCiNDnqOJHOMF5ekd5eU0NtfUEhrnbvPC8VVfah8KAprs2sR7V_nCW2jxtFKaVq5Kn29PKctBnTLatOwsrD-sA7NxutpEm0NNF61P_WQRR8y4ht1JFf9a2JYyL5lOnJciunmjdNnVtZzSY1MQ-mnZsjKe7N4YA-xjfel1Q8wSepgd2Ye1HPz-jG7fz3W_nKXSbtliJM9LeFGNWklg-tydlfI6AMYWoKf9Of42uNkKYAJq2D7iRr506CW-b_5pIBgXS_GISjz1-s-3AMNd-rj2HCgpgDO3pY";
        Map<String, Object> candidateMap = new HashMap<>();
        candidateMap.put("device_id", "CP001-2C28");
        candidateMap.put("app", "Android");
        candidateMap.put("device_status","Test data"+ comments);
        candidateMap.put("payment_require", "");
        candidateMap.put("device_model", "");
        candidateMap.put("device_version", "test version");
        candidateMap.put("scanned_qr_code","");
        candidateMap.put("command_sent", LastSen);
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

}

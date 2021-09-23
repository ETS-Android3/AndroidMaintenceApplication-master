package com.tinu.maintanceappliccation.activity;

import android.app.Activity;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.tinu.maintanceappliccation.ApplicationClass;
import com.tinu.maintanceappliccation.CodeScannerFragment;
import com.tinu.maintanceappliccation.DeviceLocationMapFragment;
import com.tinu.maintanceappliccation.DevicesFragment;
import com.tinu.maintanceappliccation.R;
import com.tinu.maintanceappliccation.models.BluetoothObject;
import com.tinu.maintanceappliccation.utility.ConstantProject;
import com.tinu.maintanceappliccation.utility.DialogManager;
import com.tinu.maintanceappliccation.utility.SharedPreferenceUtility;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;
import static com.tinu.maintanceappliccation.ApplicationClass.getCurrentActivity;



public class BaseActivity extends AppCompatActivity {

    private Dialog progressDialog;
    Fragment targetFragment = null;
    private Toolbar toolbar;
    public DrawerLayout mDrawerLayout;
    private LinearLayout navigationDrawer;
    ImageView ivMenu;//fragment
    RecyclerView rv_error_notification;
    public  static ArrayList<BluetoothObject> finalArrayOfFoundBTDevices;
    private BluetoothAdapter  bluetoothAdapter;

    TextView tv_install,tv_periodic_inspection,tv_repair,tv_location,tv_test_device,tv_history,
            tv_battery_replacement,tv_device_replacement;


    public void showBusyAnimation(final String message) {

        getCurrentActivity().runOnUiThread(new Runnable() {

            @Override
            public void run() {

                LayoutInflater inflater = (LayoutInflater) ApplicationClass.getCurrentContext().getSystemService(LAYOUT_INFLATER_SERVICE);
                View progressLayout = inflater.inflate(R.layout.custom_progress_dialog_view, null);

                TextView loadingText = (TextView) progressLayout.findViewById(R.id.tv_loadingmsg);

                if (message.equalsIgnoreCase("")) {

                    loadingText.setVisibility(View.GONE);
                } else {
                    loadingText.setVisibility(View.VISIBLE);
                    loadingText.setText(message);
                }

                progressDialog = new Dialog(ApplicationClass.getCurrentContext());
                progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                progressDialog.setContentView(progressLayout);
                progressDialog.setCancelable(true);
                progressDialog.setCanceledOnTouchOutside(false);
                if (!progressDialog.isShowing())

                    if(!((Activity) ApplicationClass.getCurrentActivity()).isFinishing())
                    {
                        progressDialog.show();
                    }

            }
        });

    }


    public void hideBusyAnimation() {

        getCurrentActivity().runOnUiThread(new Runnable() {
            public void run() {

                try {
                    if ((progressDialog != null) && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                } catch (final Exception e) {
                    e.printStackTrace();
                } finally {
                    progressDialog = null;
                }
            }
        });

    }


    public String  getCurrentTime()
    {
        Date today = new Date();
        //SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss a");

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

        SimpleDateFormat day = new SimpleDateFormat("d");
        SimpleDateFormat month = new SimpleDateFormat("M");
        SimpleDateFormat year = new SimpleDateFormat("y");
        SimpleDateFormat amOrpm = new SimpleDateFormat("a");
        SimpleDateFormat hh = new SimpleDateFormat("hh");
        SimpleDateFormat mm = new SimpleDateFormat("mm");

        String dateToStr = format.format(today);
        return dateToStr;
    }

    public String  ConvertToTimeFormat(Date today)
    {
        //SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss a");

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

        SimpleDateFormat day = new SimpleDateFormat("d");
        SimpleDateFormat month = new SimpleDateFormat("M");
        SimpleDateFormat year = new SimpleDateFormat("y");
        SimpleDateFormat amOrpm = new SimpleDateFormat("a");
        SimpleDateFormat hh = new SimpleDateFormat("hh");
        SimpleDateFormat mm = new SimpleDateFormat("mm");

        String dateToStr = format.format(today);
        return dateToStr;
    }

    public static String timeFormatInEnglish(String amOrPm,String hour,String minitus)
    {
        String date;

        if (amOrPm.equalsIgnoreCase("AM"))
        {
            date=hour+":"+minitus+"AM";

        }
        else {
            date=hour+":"+minitus+"PM";
        }
        return date;
    }

    public static boolean isNetworkConnectionAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) ApplicationClass.getCurrentContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        if (info == null) return false;
        NetworkInfo.State network = info.getState();
        return (network == NetworkInfo.State.CONNECTED || network == NetworkInfo.State.CONNECTING);
    }
    public static String getCurrentTimeStampWithZone() {

        Calendar calendarNow = Calendar.getInstance(Locale.getDefault());
        return correctedTimeStampwithZone(calendarNow.getTime());

    }
    public static String correctedTimeStampwithZone(Date dateTime) {

        SimpleDateFormat apiDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss ZZZZZ", Locale.getDefault());

        String timeStamp = apiDateFormat.format(dateTime);

        String[] timeStampArray = timeStamp.split(" ");

        if (timeStampArray[2].contains(":")) {

            return timeStamp;

        } else {

            String firstHalfofZone = timeStampArray[2].substring(0, timeStampArray[2].length() - 2);
            String lastHalfofZone = timeStampArray[2].substring(timeStampArray[2].length() - 2, timeStampArray[2].length());

            String correctedZone = firstHalfofZone + ":" + lastHalfofZone;

            return timeStampArray[0] + " " + timeStampArray[1] + " " + correctedZone;
        }
    }

    public void onCreateDrawer() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationDrawer = (LinearLayout) findViewById(R.id.ll_navigation_drawer);
        ivMenu = (ImageView) findViewById(R.id.iv_menuicon);

        tv_install =(TextView) findViewById(R.id.tv_install);
        tv_repair =(TextView) findViewById(R.id.tv_repair);
        tv_location =(TextView) findViewById(R.id.tv_location);
        tv_test_device = (TextView) findViewById(R.id.tv_test_device);
        tv_history = (TextView)findViewById(R.id.tv_history);
        tv_periodic_inspection = (TextView)findViewById(R.id.tv_periodic_inspection);
        tv_battery_replacement = (TextView)findViewById(R.id.tv_battery_replacement);
        tv_device_replacement = (TextView)findViewById(R.id.tv_device_replacement);
        mDrawerLayout.setScrimColor(getResources().getColor(android.R.color.transparent));


        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar,
                R.string.app_name, R.string.app_name);


        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();
        mDrawerToggle.setDrawerIndicatorEnabled(true);
        toolbar.setNavigationIcon(null);


        String activityName = (getCurrentActivity().getClass().getSimpleName());

        if (activityName.equals("ScanActivity")) {

            Intent intent =new Intent(BaseActivity.this,ScanActivity.class);
            intent.putExtra(ConstantProject.isFrom,ConstantProject.Drawer);

            startActivity(intent);
        }



        //to dooooo remaining


        mDrawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
            }

            @Override
            public void onDrawerOpened(View drawerView) {


            }

            @Override
            public void onDrawerStateChanged(int newState) {
            }

            @Override
            public void onDrawerClosed(View drawerView) {

            }
        });

        closeNavDrawer();

        if (ivMenu != null) {
            ivMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mDrawerLayout.isDrawerOpen(Gravity.LEFT)) {
                        mDrawerLayout.closeDrawer(Gravity.LEFT);


                    } else {
                        mDrawerLayout.openDrawer(Gravity.LEFT);
                    }
                }
            });
        }
        if (tv_install!=null)
        {
            tv_install.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    closeNavDrawer();
                    SharedPreferenceUtility.saveSelectedType("install");

                    SharedPreferenceUtility.saveMoveCount(1);
                    Intent intent =new Intent(BaseActivity.this,InstallActivity.class);
                    intent.putExtra(ConstantProject.isFrom,ConstantProject.Drawer);

                    startActivity(intent);
                }
            });
        }

        if (tv_periodic_inspection!=null)
        {
            tv_periodic_inspection.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    closeNavDrawer();
                    SharedPreferenceUtility.saveSelectedType("");

                    SharedPreferenceUtility.saveMoveCount(2);
                    Intent intent =new Intent(BaseActivity.this,PeriodicInspectionActivity.class);
                    intent.putExtra(ConstantProject.isFrom,ConstantProject.Drawer);

                    startActivity(intent);
                }
            });
        }
        if (tv_location!=null)
        {
            tv_location.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    closeNavDrawer();
                    SharedPreferenceUtility.saveSelectedType("");

                    rv_error_notification =findViewById(R.id.rv_error_notification);

                    SharedPreferenceUtility.saveMoveCount(6);
                    LinearLayout  ll_fl_main = findViewById(R.id.ll_fl_main);
                    ll_fl_main.setVisibility(View.VISIBLE);

                    LinearLayout ll_app_bar =findViewById(R.id.ll_app_bar);
                    ll_app_bar.setVisibility(View.GONE);
                    rv_error_notification.setVisibility(View.GONE);
                    Fragment fragment = new DeviceLocationMapFragment();

                    getSupportFragmentManager().beginTransaction()

                            .replace(R.id.fl_main, fragment,

                                    fragment.getClass().getSimpleName()).commit();


                }
            });
        }

        if (tv_repair!=null)
        {
            tv_repair.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    closeNavDrawer();
                    SharedPreferenceUtility.saveSelectedType("");

                    SharedPreferenceUtility.saveMoveCount(3);

                    Intent intent =new Intent(BaseActivity.this,InspectionActivity.class);
                    intent.putExtra(ConstantProject.isFrom,ConstantProject.Drawer);
                    startActivity(intent);

                }
            });
        }

        if (tv_test_device!=null)
        {
            tv_test_device.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    rv_error_notification =findViewById(R.id.rv_error_notification);
                    closeNavDrawer();
                    SharedPreferenceUtility.saveSelectedType("");

                    LinearLayout  ll_fl_main = findViewById(R.id.ll_fl_main);
                    ll_fl_main.setVisibility(View.VISIBLE);

                   LinearLayout ll_app_bar =findViewById(R.id.ll_app_bar);
                   ll_app_bar.setVisibility(View.GONE);
                    rv_error_notification.setVisibility(View.GONE);
                    Fragment fragment = new CodeScannerFragment();

                    getSupportFragmentManager().beginTransaction()

                            .replace(R.id.fl_main, fragment,

                                    fragment.getClass().getSimpleName()).commit();

                }
            });
        }

        if (tv_history!=null)
        {
            tv_history.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SharedPreferenceUtility.saveSelectedType("");

                    SharedPreferenceUtility.saveMoveCount(8);
                    Intent intent =new Intent(BaseActivity.this,ScanActivity.class);
                    intent.putExtra(ConstantProject.isFrom,ConstantProject.History);

                    startActivity(intent);

                }
            });


        }
        if (tv_battery_replacement!=null)
        {
            tv_battery_replacement.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SharedPreferenceUtility.saveSelectedType("");

                    SharedPreferenceUtility.saveMoveCount(4);
                    Intent intent =new Intent(BaseActivity.this,BatteryReplacementActivity.class);
                    intent.putExtra(ConstantProject.isFrom,ConstantProject.Drawer);

                    startActivity(intent);

                }
            });


        }
        if (tv_device_replacement!=null)
        {
            tv_device_replacement.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SharedPreferenceUtility.saveMoveCount(5);
                    SharedPreferenceUtility.saveSelectedType("");


                    Intent intent =new Intent(BaseActivity.this,DeviceReplacementActivity.class);
                    intent.putExtra(ConstantProject.isFrom,ConstantProject.Drawer);

                    startActivity(intent);

                }
            });


        }

    }
    public void closeNavDrawer() {

        mDrawerLayout.closeDrawer(Gravity.LEFT);

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


    @Override
    public void applyOverrideConfiguration(Configuration overrideConfiguration) {
        if (overrideConfiguration != null) {
            int uiMode = overrideConfiguration.uiMode;
            overrideConfiguration.setTo(getBaseContext().getResources().getConfiguration());
            overrideConfiguration.uiMode = uiMode;
        }
        super.applyOverrideConfiguration(overrideConfiguration);
    }


    public void callBackgroundScanning()
    {
        new Thread(new FilterDeviceMethod2()).start();
    }
    class FilterDeviceMethod2 implements Runnable {
        @Override
        public void run() {
            int BLUETOOTH_REQUEST = 1;
            bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

            if (bluetoothAdapter == null) {

                // Show proper message here
                finish();
            } else {


                if (!bluetoothAdapter.isEnabled()) {
                    Intent enableBluetoothIntent = new Intent(
                            BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBluetoothIntent, BLUETOOTH_REQUEST);
                } else {
                    // setButtonsEnabled(true);
                }
            }
            bluetoothAdapter.enable();
            displayListOfFoundDevices();

        }
    }






    private void displayListOfFoundDevices()
    {
        ArrayList<BluetoothObject> arrayOfFoundBTDevices = null;

        arrayOfFoundBTDevices = new ArrayList<BluetoothObject>();
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // start looking for bluetooth devices
        bluetoothAdapter.startDiscovery();

        // Discover new devices
        // Create a BroadcastReceiver for ACTION_FOUND
        finalArrayOfFoundBTDevices = arrayOfFoundBTDevices;
        final BroadcastReceiver mReceiver = new BroadcastReceiver()
        {
            @Override
            public void onReceive(Context context, Intent intent)
            {
                String action = intent.getAction();
                // When discovery finds a device
                if (BluetoothDevice.ACTION_FOUND.equals(action))
                {
                    // Get the bluetoothDevice object from the Intent
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                    // Get the "RSSI" to get the signal strength as integer,
                    // but should be displayed in "dBm" units
                    int rssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI,Short.MIN_VALUE);

                    // Create the device object and add it to the arrayList of devices
                    BluetoothObject bluetoothObject = new BluetoothObject();
                    bluetoothObject.setName(device.getName());
                    bluetoothObject.setAddress(device.getAddress());
                    bluetoothObject.setUuid(""+device.getUuids());


                    // Log.e("BlueTooth",device.getName()+device.getAddress());
                   /* bluetoothObject.setBluetooth_state(device.getBondState());
                    bluetoothObject.setBluetooth_type(device.getType());    // requires API 18 or higher
                    bluetoothObject.setBluetooth_rssi(rssi);
*/
                    finalArrayOfFoundBTDevices.add(bluetoothObject);


                }
            }
        };
        // Register the BroadcastReceiver
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mReceiver, filter);
    }
}

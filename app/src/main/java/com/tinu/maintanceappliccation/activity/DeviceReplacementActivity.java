package com.tinu.maintanceappliccation.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tinu.maintanceappliccation.ApplicationClass;
import com.tinu.maintanceappliccation.MainActivityDrawer;
import com.tinu.maintanceappliccation.R;
import com.tinu.maintanceappliccation.adapter.TaskListRecyclerViewAdapter;
import com.tinu.maintanceappliccation.models.TaskListModel;
import com.tinu.maintanceappliccation.restApiCall.ApiClient;
import com.tinu.maintanceappliccation.restApiCall.ApiInterface;
import com.tinu.maintanceappliccation.utility.ConstantProject;
import com.tinu.maintanceappliccation.utility.SharedPreferenceUtility;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DeviceReplacementActivity extends BaseActivity {
    ImageView iv_back;
    ApiInterface apiService;
    RecyclerView rvTaskList;
    List<TaskListModel.Data> taskListModelList;
    TaskListRecyclerViewAdapter taskListRecyclerViewAdapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String languageToLoad;
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
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());

        setContentView(R.layout.activity_device_replacement);
        ApplicationClass.setCurrentContext(this);

        init();

    }
    private void init(){
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setVisibility(View.VISIBLE);
        iv_back =(ImageView)findViewById(R.id.iv_back);
        apiService = ApiClient.getClient().create(ApiInterface.class);
        rvTaskList = (RecyclerView)findViewById(R.id.rv_device);
        rvTaskList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ApplicationClass.getCurrentContext());
        rvTaskList.setLayoutManager(linearLayoutManager);
        taskListModelList = new ArrayList<>();

        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(DeviceReplacementActivity.this, MainActivityDrawer.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                intent.putExtra(ConstantProject.isFrom,ConstantProject.Periodic_inspection);
                startActivity(intent);
            }
        });

        getTaskList("device replacement");

    }

    private void getTaskList(String device_replacement) {

        showBusyAnimation("");

        String acess_token = ConstantProject.API_HEADER + SharedPreferenceUtility.getAccessToken();
        Map<String, Object> candidateMap = new HashMap<>();


            candidateMap.put("landmark","");



            candidateMap.put("type",device_replacement);


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
                                Intent intent = new Intent(DeviceReplacementActivity.this, ScanActivity.class);
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

}

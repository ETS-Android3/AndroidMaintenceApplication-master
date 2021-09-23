package com.tinu.maintanceappliccation.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.tinu.maintanceappliccation.ApplicationClass;
import com.tinu.maintanceappliccation.MainActivityDrawer;
import com.tinu.maintanceappliccation.R;
import com.tinu.maintanceappliccation.adapter.DeviceSpinnerAdapter;
import com.tinu.maintanceappliccation.adapter.HistoryRecyclerViewAdapter;
import com.tinu.maintanceappliccation.adapter.ImageRecyclerViewAdapter;
import com.tinu.maintanceappliccation.adapter.TaskListRecyclerViewAdapter;
import com.tinu.maintanceappliccation.models.HistoryModel;
import com.tinu.maintanceappliccation.models.TaskListModel;
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

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HistoryActivity extends BaseActivity {
    ImageView iv_back;
    ApiInterface apiService;
    RecyclerView rv_history;
    TextView tv_history;
    HistoryRecyclerViewAdapter historyRecyclerViewAdapter;
    List<HistoryModel.Data> historyList;



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

        setContentView(R.layout.activity_history);
        ApplicationClass.setCurrentContext(this);

        init();

    }
    private void init(){
        historyList =new ArrayList<>();
        iv_back =(ImageView)findViewById(R.id.iv_back);
        apiService = ApiClient.getClient().create(ApiInterface.class);
        rv_history = (RecyclerView)findViewById(R.id.rv_history);
        tv_history =(TextView)findViewById(R.id.tv_history);
        rv_history.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ApplicationClass.getCurrentContext());
        rv_history.setLayoutManager(linearLayoutManager);

        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(HistoryActivity.this, MainActivityDrawer.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                intent.putExtra(ConstantProject.isFrom,ConstantProject.History);
                startActivity(intent);
            }
        });


        CallHistory(ApplicationClass.getScannedDeviceCode());

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

    private void CallHistory(String deviceId) {

        showBusyAnimation("");
        Map<String, Object> candidateMap = new HashMap<>();

        if (SharedPreferenceUtility.getIsEnglishLag()) {
            candidateMap.put("lang_id", ConstantProject.isEnglishLag);
        } else {
            candidateMap.put("lang_id", ConstantProject.isJapaneaseLag);
        }

        candidateMap.put("device_id", deviceId);
        String acess_token = ConstantProject.API_HEADER + SharedPreferenceUtility.getAccessToken();


        apiService.maintenanceHistory(acess_token, candidateMap).enqueue(new Callback<HistoryModel>() {
            @Override
            public void onResponse(Call<HistoryModel> call, Response<HistoryModel> response) {
                hideBusyAnimation();
                if (response.code() == 200) {
                    if (response.body()!=null)
                    {
                        if (response.body().getData()!=null)
                        {
                            tv_history.setText(response.body().getData().get(0).getDevice_name());
                            historyList.addAll(response.body().getData());
                            historyRecyclerViewAdapter = new HistoryRecyclerViewAdapter(ApplicationClass.getCurrentContext(), historyList, new HistoryRecyclerViewAdapter.CustomOnClickListener() {
                                @Override
                                public void OrderDetails(TaskListModel.Data taskListModel) {

                                }
                            });

                            rv_history.setAdapter(historyRecyclerViewAdapter);

                        }
                    }




                }

            }

            @Override
            public void onFailure(Call<HistoryModel> call, Throwable t) {
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

package com.tinu.maintanceappliccation.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.tinu.maintanceappliccation.ApplicationClass;
import com.tinu.maintanceappliccation.MainActivityDrawer;
import com.tinu.maintanceappliccation.R;
import com.tinu.maintanceappliccation.adapter.ImageRecyclerViewAdapter;
import com.tinu.maintanceappliccation.models.ImageModel;
import com.tinu.maintanceappliccation.models.UpdateTaskResponseModel;
import com.tinu.maintanceappliccation.restApiCall.ApiClient;
import com.tinu.maintanceappliccation.restApiCall.ApiInterface;
import com.tinu.maintanceappliccation.utility.ConstantProject;
import com.tinu.maintanceappliccation.utility.DialogManager;
import com.tinu.maintanceappliccation.utility.SharedPreferenceUtility;
import com.zfdang.multiple_images_selector.ImagesSelectorActivity;
import com.zfdang.multiple_images_selector.SelectorSettings;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ImageUploadActivity extends BaseActivity {
    ImageView iv_add, iv_back;
    int REQUEST_IMAGE = 10, REQUEST_PERMISSION = 100;
    RecyclerView rv_images;
    ArrayList<String> imageListFromGallery;
    ArrayList<ImageModel> imageListFromServer;
    ArrayList<ImageModel> imageListForRecyclerView;

    ImageRecyclerViewAdapter imageRecyclerViewAdapter;
    ProgressDialog progressBar;
    private int progressBarStatus = 0;
    private Handler progressBarHandler = new Handler();
    private long fileSize = 0;
    int SELECT_IMAGE = 11;
    Button btn_upload;
    ApiInterface apiService;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ApplicationClass.setCurrentContext(this);
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

        setContentView(R.layout.activity_image_upload);



        init();


    }

    public void init() {
        /**/
        imageListFromGallery = new ArrayList<>();
        imageListFromServer = new ArrayList<>();
        imageListForRecyclerView = new ArrayList<>();
        apiService = ApiClient.getClient().create(ApiInterface.class);
        if (ApplicationClass.getImagesList() != null) {
            for (int i = 0; i < ApplicationClass.getImagesList().size(); i++) {
                ImageModel imageModel = new ImageModel();
                imageModel.setId(ApplicationClass.getImagesList().get(i).getId());
                imageModel.setPath(ApplicationClass.getImagesList().get(i).getImagePath());
                imageModel.setServer(true);

                imageListFromServer.add(imageModel);
            }
        }


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        rv_images = (RecyclerView) findViewById(R.id.rv_images);
        btn_upload = (Button) findViewById(R.id.btn_upload);

        iv_add = (ImageView) findViewById(R.id.iv_add);
        iv_back = (ImageView) findViewById(R.id.iv_back);
        rv_images.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new GridLayoutManager(ApplicationClass.getCurrentContext(), 3);
        rv_images.setLayoutManager(linearLayoutManager);

        iv_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //MultiImageSelector.create(ApplicationClass.getCurrentContext());

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                        && ContextCompat.checkSelfPermission(ApplicationClass.getCurrentContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(ApplicationClass.getCurrentActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            REQUEST_PERMISSION);
                    openImagePicker();
                    return;
                } else {
                    openImagePicker();
                }


            }

        });

        imageListForRecyclerView.clear();
        imageListForRecyclerView.addAll(imageListFromServer);
        imageRecyclerViewAdapter = new ImageRecyclerViewAdapter(ApplicationClass.getCurrentContext(), imageListForRecyclerView, new ImageRecyclerViewAdapter.CustomOnClickListener() {


            @Override
            public void OrderDetails(int imageid, ImageModel imageModel) {

                if (imageModel.isServer()) {
                    DeleteImage(imageid);
                } else {

                    for (int i = 0; i < imageListForRecyclerView.size(); i++) {
                        if (!imageListForRecyclerView.get(i).isServer()) {
                            if (imageListForRecyclerView.get(i).getId() == imageModel.getId()) {
                                for (int k = 0; k < imageListFromGallery.size(); k++) {
                                    if (imageListFromGallery.get(k).equals(imageListForRecyclerView.get(i).getPath())) {
                                        imageListFromGallery.remove(k);
                                    }
                                }
                            }
                        }
                    }


                }


            }


        });
        rv_images.setAdapter(imageRecyclerViewAdapter);

        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        btn_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (imageListFromGallery != null) {
                    if (imageListFromGallery.size() > 0) {
                        ApplicationClass.setSelectedImageList(imageListFromGallery);

                       /* Intent intent = new Intent(ImageUploadActivity.this, WorkDetailsActivity.class);
                        startActivity(intent);*/
                        UploadImages();

                    } else {
                        Intent intent = new Intent(ImageUploadActivity.this, WorkDetailsActivity.class);
                        startActivity(intent);
                        /*DialogManager.showConfirmDialogue(ApplicationClass.getCurrentActivity(), ApplicationClass.getCurrentActivity().getResources().getString(R.string.empty_images), new DialogManager.IMultiActionDialogOnClickListener() {
                            @Override
                            public void onPositiveClick() {

                            }

                            @Override
                            public void onNegativeClick() {

                            }
                        });*/
                    }
                } else {
                   /* DialogManager.showUniActionDialog(ApplicationClass.getCurrentActivity(), ApplicationClass.getCurrentActivity().getResources().getString(R.string.empty_images), "ok", new DialogManager.IUniActionDialogOnClickListener() {
                        @Override
                        public void onPositiveClick() {

                        }
                    });*/
                }

            }
        });
    }

    @Override
    public void onRequestPermissionsResult(final int requestCode, @NonNull final String[] permissions, @NonNull final int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openImagePicker();
            } else {
                // User refused to grant permission.
            }
        }
    }

    public void openImagePicker() {
        Intent intent = new Intent(ImageUploadActivity.this, ImagesSelectorActivity.class);
// whether show camera
        intent.putExtra(SelectorSettings.SELECTOR_SHOW_CAMERA, true);
// max select image amount
        intent.putExtra(SelectorSettings.SELECTOR_MAX_IMAGE_NUMBER, 100);
        intent.putExtra(SelectorSettings.SELECTOR_MIN_IMAGE_SIZE, 100000);

        intent.putExtra(SelectorSettings.SELECTOR_SHOW_CAMERA, true);

// select mode (MultiImageSelectorActivity.MODE_SINGLE OR MultiImageSelectorActivity.MODE_MULTI)
// default select images (support array list)
        intent.putStringArrayListExtra(SelectorSettings.SELECTOR_INITIAL_SELECTED_LIST, imageListFromGallery);

        startActivityForResult(intent, REQUEST_IMAGE);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE) {
            if (resultCode == RESULT_OK) {

                if (requestCode == SELECT_IMAGE) {
                    if (resultCode == Activity.RESULT_OK) {
                        if (data != null) {

                        }
                    } else if (resultCode == Activity.RESULT_CANCELED) {
                    }
                } else {
                    imageListFromGallery.addAll(data.getStringArrayListExtra(SelectorSettings.SELECTOR_RESULTS));

                    imageListForRecyclerView.clear();
                    for (int i = 0; i < imageListFromGallery.size(); i++) {
                        ImageModel imageModel = new ImageModel();
                        imageModel.setPath(imageListFromGallery.get(i));
                        imageModel.setId(i);
                        imageModel.setServer(false);
                        imageListFromServer.add(imageModel);

                    }
                    //imageListForRecyclerView.addAll(imageListFromGallery);
                    imageListForRecyclerView.addAll(imageListFromServer);
                    imageRecyclerViewAdapter = new ImageRecyclerViewAdapter(ApplicationClass.getCurrentContext(), imageListForRecyclerView, new ImageRecyclerViewAdapter.CustomOnClickListener() {


                        @Override
                        public void OrderDetails(int imageid, ImageModel imageModel) {

                            if (imageModel.isServer()) {
                                DeleteImage(imageid);
                            } else {

                                for (int i = 0; i < imageListForRecyclerView.size(); i++) {
                                    if (!imageListForRecyclerView.get(i).isServer()) {
                                        if (imageListForRecyclerView.get(i).getId() == imageModel.getId()) {
                                            for (int k = 0; k < imageListFromGallery.size(); k++) {
                                                if (imageListFromGallery.get(k).equals(imageListForRecyclerView.get(i).getPath())) {
                                                    imageListFromGallery.remove(k);
                                                }
                                            }
                                        }
                                    }
                                }


                            }


                        }


                    });
                    rv_images.setAdapter(imageRecyclerViewAdapter);

                  //  iv_add.setVisibility(View.GONE);
                }
                // Get the result list of select image paths


            }

        }
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


    public void UploadImages() {
        ((BaseActivity) ApplicationClass.getCurrentActivity()).showBusyAnimation("");

// ConstantProject.PENDING
        RequestBody taskID = null;
        try {
            taskID = RequestBody.create(MediaType.parse("text/plain"), ApplicationClass.getTaskId());
        } catch (Exception e) {
            e.printStackTrace();
        }
        String acess_token = ConstantProject.API_HEADER + SharedPreferenceUtility.getAccessToken();

/////////////////

        MultipartBody.Part[] surveyImagesParts = new MultipartBody.Part[imageListFromGallery.size()];

        for (int index = 0; index <
                imageListFromGallery
                        .size(); index++) {


            File file = new File(imageListFromGallery
                    .get(index));
            RequestBody surveyBody = RequestBody.create(MediaType.parse("image/*"),
                    file);
            surveyImagesParts[index] = MultipartBody.Part.createFormData("maintenanceImage[]",
                    file.getName(),
                    surveyBody);
        }

        apiService.uploadImages(acess_token, taskID, surveyImagesParts).enqueue(new Callback<UpdateTaskResponseModel>() {
            @Override
            public void onResponse(Call<UpdateTaskResponseModel> call, Response<UpdateTaskResponseModel> response) {

                if (response.code() == 200) {
                    Boolean status = true;
                    if (status == true && response.body().getData() != null) {

                        ((BaseActivity) ApplicationClass.getCurrentActivity()).hideBusyAnimation();
                        Intent intent = new Intent(ApplicationClass.getCurrentActivity(), WorkDetailsActivity.class);
                        ApplicationClass.getCurrentActivity().startActivity(intent);
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
                                        ApplicationClass.getCurrentActivity().startActivity(intent);

                                    }


                                }).show();

                    }
                } else if (response.code() == 404) {


                }
                ((BaseActivity) ApplicationClass.getCurrentActivity()).hideBusyAnimation();
            }

            @Override
            public void onFailure(Call<UpdateTaskResponseModel> call, Throwable t) {
                ((BaseActivity) ApplicationClass.getCurrentActivity()).hideBusyAnimation();
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

    private void DeleteImage(int document_id) {

        ((BaseActivity) ApplicationClass.getCurrentActivity()).showBusyAnimation("");
        Map<String, Object> candidateMap = new HashMap<>();

        if (SharedPreferenceUtility.getIsEnglishLag()) {
            candidateMap.put("lang_id", ConstantProject.isEnglishLag);
        } else {
            candidateMap.put("lang_id", ConstantProject.isJapaneaseLag);
        }

        candidateMap.put("document_id", document_id);
        String acess_token = ConstantProject.API_HEADER + SharedPreferenceUtility.getAccessToken();


        apiService.deleteImage(acess_token, candidateMap).enqueue(new Callback<UpdateTaskResponseModel>() {
            @Override
            public void onResponse(Call<UpdateTaskResponseModel> call, Response<UpdateTaskResponseModel> response) {
                ((BaseActivity) ApplicationClass.getCurrentActivity()).hideBusyAnimation();
                if (response.code() == 200) {


                }

            }

            @Override
            public void onFailure(Call<UpdateTaskResponseModel> call, Throwable t) {
                ((BaseActivity) ApplicationClass.getCurrentActivity()).hideBusyAnimation();

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

   /* @Override
    public void applyOverrideConfiguration(Configuration overrideConfiguration) {
        if (overrideConfiguration != null) {
            int uiMode = overrideConfiguration.uiMode;
            overrideConfiguration.setTo(getBaseContext().getResources().getConfiguration());
            overrideConfiguration.uiMode = uiMode;
        }
        super.applyOverrideConfiguration(overrideConfiguration);
    }
*/

}

package com.tinu.maintanceappliccation.restApiCall;


import com.tinu.maintanceappliccation.models.ChangeEmailResponseModel;
import com.tinu.maintanceappliccation.models.DevicesByLocationModel;
import com.tinu.maintanceappliccation.models.FreeScanDeviceModel;
import com.tinu.maintanceappliccation.models.GetHashMsgModel;
import com.tinu.maintanceappliccation.models.HashApiResponse;
import com.tinu.maintanceappliccation.models.HistoryModel;
import com.tinu.maintanceappliccation.models.LockApiResponse;
import com.tinu.maintanceappliccation.models.LockerDetailsResponse;
import com.tinu.maintanceappliccation.models.LogAppErrorModel;
import com.tinu.maintanceappliccation.models.NarestLocationsModel;
import com.tinu.maintanceappliccation.models.SuccessApiResponse;
import com.tinu.maintanceappliccation.models.TaskListModel;
import com.tinu.maintanceappliccation.models.UnLockApiResponse;
import com.tinu.maintanceappliccation.models.UnLockRequestApiResponse;
import com.tinu.maintanceappliccation.models.UpdateTaskResponseModel;
import com.tinu.maintanceappliccation.models.UserLoginModel;
import com.tinu.maintanceappliccation.models.scanDeviceModel;
import com.tinu.maintanceappliccation.utility.ConstantProject;

import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ApiInterface {
   // String BaseUrl = "http://3.7.230.192:8080/oo-admin-wsinstance1-1.0/";
   // String BaseUrlPHP ="http://3.7.230.192/oo-pos-ws/";
/*

// java API
    @POST("shop/login")
    Call<ShopModel> shopLogin(@Body Map<String, Object> request);

    @POST("app-orders/get-orders")
    Call<AllOrderModel> getAllOrders(@Body Map<String, Object> request);
    @GET("orderhistory/get-headers")
    Call<OrderDetailsAllOrder> getDetailsAllOrder(@Query("orderId") String orderId);

// Php API
    @FormUrlEncoded
    @POST("get_online_orders.php")
    Call<OrderDetailsNewModel>getNewOrders(@Field("shop_code") String shop_code, @Field(value = "updated_at", encoded = false) String updated_at);

    @FormUrlEncoded
    @POST("oo_status_update.php")
    Call<UpdateStatusModel>updateStatus(@Field("shop_id") String shop_id, @Field("order_id") String order_id, @Field("status") String status, @Field("updated_at") String updated_at);


*/

    @FormUrlEncoded
    @POST("maintenance/userLogin")
    Call<UserLoginModel> callUserLogin(@Field("username") String username, @Field("password") String password, @Field("client_id") String client_id,
                                       @Field("client_secret") String client_secret, @Field("lang_id") String lang_id);


    @POST("maintenance/composeHashMsg")
    Call<HashApiResponse> getHashValues(@Header(ConstantProject.HEADER_AUTHORIZATION) String acces_token, @Body Map<String, Object> request);

    @POST("maintenance/scanDevice")
    Call<scanDeviceModel>getScannedDeviceDetails(@Header(ConstantProject.HEADER_AUTHORIZATION)String acces_token, @Body Map<String, Object> request);


    @Multipart
    @POST("maintenance/updateTask")
    Call<UpdateTaskResponseModel>updateTaskTimeAndLandMark(@Header(ConstantProject.HEADER_AUTHORIZATION)String acces_token,@Part("task_id") RequestBody task_id,@Part("start_time") RequestBody start_time,@Part("landmark") RequestBody landmark);

    @Multipart
    @POST("maintenance/updateTask")
    Call<UpdateTaskResponseModel>updateWorkDetailsLatitudeAndLongitude(@Header(ConstantProject.HEADER_AUTHORIZATION)String acces_token,@Part("task_id") RequestBody task_id,@Part("work_details") RequestBody work_details,@Part("latitude") RequestBody latitude,@Part("longitude") RequestBody longitude,
                                                                       @Part("install") RequestBody install,@Part("repair") RequestBody inspection,@Part("regular_maintenance") RequestBody periodic_inspection,@Part("battery_replacement") RequestBody battery_replacement,@Part("device_replacement") RequestBody device_replacement);

    @Multipart
    @POST("maintenance/updateTask")
    Call<UpdateTaskResponseModel>uploadImages(@Header(ConstantProject.HEADER_AUTHORIZATION)String acces_token,@Part("task_id") RequestBody task_id,@Part MultipartBody.Part[]  images);


    @POST("maintenance/taskList")
    Call<TaskListModel>getTaskList(@Header(ConstantProject.HEADER_AUTHORIZATION)String acces_token,@Body Map<String, Object> request);


    @POST("maintenance/nearestLocations")
    Call<NarestLocationsModel>getNearestLocations(@Header(ConstantProject.HEADER_AUTHORIZATION)String acces_token, @Body Map<String, Object> request);

    @POST("maintenance/getDevicesByLocation")
    Call<DevicesByLocationModel>getDeviceByLocation(@Header(ConstantProject.HEADER_AUTHORIZATION)String acces_token, @Body Map<String, Object> request);

    @POST("maintenance/getHashMsg")
    Call<GetHashMsgModel> getOHashMsgAtUnlock(@Header(ConstantProject.HEADER_AUTHORIZATION) String acces_token, @Body Map<String, Object> request);

    @POST("maintenance/deleteDocument")
    Call<UpdateTaskResponseModel> deleteImage(@Header(ConstantProject.HEADER_AUTHORIZATION) String acces_token, @Body Map<String, Object> request);


    @POST("maintenance/maintenanceHistory")
    Call<HistoryModel> maintenanceHistory(@Header(ConstantProject.HEADER_AUTHORIZATION) String acces_token, @Body Map<String, Object> request);

    @Multipart
    @POST("maintenance/updateTask")
    Call<UpdateTaskResponseModel>CompleteTask(@Header(ConstantProject.HEADER_AUTHORIZATION)String acces_token,@Part("task_id") RequestBody task_id,@Part("status") RequestBody status,@Part("end_time") RequestBody end_time);

    @POST("logAppError")
    Call<LogAppErrorModel> LogAppErrors(@Header(GloablMethods.HEADER_AUTHORIZATION) String access_token, @Body Map<String, Object> request);

    @POST("maintenance/scanDevice")
    Call<FreeScanDeviceModel>getScannedDeviceDetailsFreeScan(@Header(ConstantProject.HEADER_AUTHORIZATION)String acces_token, @Body Map<String, Object> request);


    @POST("MaintenanceAppError")
    Call<HashApiResponse> postErrorLog(@Header(GloablMethods.HEADER_AUTHORIZATION) String acces_token, @Body Map<String, Object> request);


}

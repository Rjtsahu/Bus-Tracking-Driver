package com.sahurjt.bstdriver.Interfaces;

import com.sahurjt.bstdriver.Models.AttendanceRequest;
import com.sahurjt.bstdriver.Models.DriverLoginModel;
import com.sahurjt.bstdriver.Models.GpsRequest;
import com.sahurjt.bstdriver.Models.KidsResponse;
import com.sahurjt.bstdriver.Models.LoginResponse;
import com.sahurjt.bstdriver.Models.Response;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Body;
import retrofit2.http.Query;


public interface ApiInterface {
    public static String QRY_TOKEN = "token";
    public static String QRY_PICKED = "present";
    public static String QRY_DROPPED = "dropped";
    public static String STATUS_OK = "ok";
    public static String STATUS_FAIL = "error";

    // method to login driver and get access token
    @POST("access_token")
    Call<LoginResponse> loginAndGetToken(@Body DriverLoginModel login);

    @POST("add_gps")
    Call<Response> addGpsForThisBus(@Body GpsRequest gpsRequest, @Query(QRY_TOKEN) String token);

    // logout from this journey session
    @GET("logout")
    Call<Response> logout(@Query(QRY_TOKEN) String token);

    // get list of kids ; PICKUP time
    public static String QRY_KID_PRESENT = "1";
    public static String QRY_KID_NOT_YET_PRESENT = "0";

    @GET("get_kids")
    Call<KidsResponse> getKids(@Query(QRY_TOKEN) String token, @Query(QRY_PICKED) String present);

    //get list of kids ;DROP time
    public static String QRY_KID_DROP_PRESENT = "1";
    public static String QRY_KID_DROP_NOT_YET_PRESENT = "0";

    @GET("get_kids_dropped")
    Call<KidsResponse> getKidsDropped(@Query(QRY_TOKEN) String token, @Query(QRY_DROPPED) String dropped);

    // pick add attendance request
    @POST("add_pick_attendance")
    Call<Response> addPickAttends(@Query(QRY_TOKEN) String token, @Body AttendanceRequest kid_ids);

    // drop add attendance request
    @POST("add_drop_attendance")
    Call<Response> addDropAttends(@Query(QRY_TOKEN) String token,@Body AttendanceRequest kid_ids);
}

package com.sahurjt.bstdriver.Network;

import android.content.Context;

import com.sahurjt.bstdriver.Utils.SharedPrefHelper;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
     //public static final String API_BASE_KID_IMAGE_URL="http://192.168.43.241:5000/api/v1/driver/photo_kid?";
    public static final String API_BASE_KID_IMAGE_URL = "http://sahurjt.pythonanywhere.com/api/v1/driver/photo_kid?";
    // private static final String API_BASE_URL = "http://192.168.43.241:5000/api/v1/driver/";
    private static final String API_BASE_URL = "http://sahurjt.pythonanywhere.com/api/v1/driver/";
    private static Retrofit retrofit = null;

    public static Retrofit getClient() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder().baseUrl(API_BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
        }
        return retrofit;
    }

}

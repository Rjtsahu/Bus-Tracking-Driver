package com.sahurjt.bstdriver.Utils;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.camera2.params.StreamConfigurationMap;

public class SharedPrefHelper {
    private static final String SP_FILE = "pref";
    private static final String DEFAULT_STRING = "";
    private static final int DEFAULT_INT=0;
    private static SharedPrefHelper this_context;
    private static SharedPreferences sharedPreferences;
    private static SharedPreferences.Editor spEditor;

    // prevent constructor construction
    private SharedPrefHelper() {

    }

    public static SharedPrefHelper getInstance(Context ctx) {

        if (this_context == null) {
            this_context = new SharedPrefHelper();
            sharedPreferences = ctx.getSharedPreferences(SP_FILE, Context.MODE_PRIVATE);
            spEditor = sharedPreferences.edit();
        }
        return this_context;
    }

    public void addString(String key, String value) {
        spEditor.putString(key, value);
        commit();
    }

    public void addBoolean(String key, Boolean value) {
        spEditor.putBoolean(key, value);
        commit();
    }

    public void addInteger(String key, Integer value) {
        spEditor.putInt(key, value);
        commit();
    }

    public String getString(String key) {
        return sharedPreferences.getString(key, DEFAULT_STRING);
    }

    public int getInteger(String key){ return sharedPreferences.getInt(key,DEFAULT_INT);}

    private void commit() {
        if (spEditor != null) spEditor.commit();
    }

    // constant related to key
    public static final String LOGIN_USERNAME = "username";
    public static final String LOGIN_PASSWORD = "password";
    public static final String LOGIN_TOKEN = "token";
    public static final String LOGIN_TOKEN_VALIDITY = "valid_till";
    public static final String LOGIN_NAME = "name";
    public static final String LOGIN_BUS_NO = "bus_no";
    public static final String LOGIN_TRIP_TYPE = "trip_type";
    public static final String SERVER_TYPE="server_type";
}

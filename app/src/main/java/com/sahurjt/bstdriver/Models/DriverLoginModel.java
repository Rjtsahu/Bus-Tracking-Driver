package com.sahurjt.bstdriver.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class DriverLoginModel {

    @SerializedName("username")
    @Expose
    private String username;

    @SerializedName("password")
    @Expose
    private String password;

    @SerializedName("journey_type")
    @Expose
    private int journey_type;

    // also journey type is required
    public static final int JOURNEY_FROM_SCHOOL_TO_HOME = 1;
    public static final int JOURNEY_FROM_HOME_TO_SCHOOL = 0;

    public DriverLoginModel(String username, String password,int journey_type) {
        this.username = username;
        this.password = password;
        this.journey_type = journey_type;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getJourneyType() {
        return this.journey_type;
    }

    ;

    public void setJourneyType(int journey_type) {
        this.journey_type = journey_type;
    }
}

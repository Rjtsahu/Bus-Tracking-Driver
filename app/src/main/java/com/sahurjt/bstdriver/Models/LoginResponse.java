package com.sahurjt.bstdriver.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Rajat_Sahu on 20-03-2017.
 */

public class LoginResponse extends Response {

    @SerializedName("valid_till")
    @Expose
    private String validTill;

    @SerializedName("token")
    @Expose
    private String token;

    @SerializedName("bus")
    @Expose
    private String bus;

    @SerializedName("name")
    @Expose
    private String name;


    public String getValidTill() {

        return validTill;
    }


    public String getToken() {
        return token;
    }

    public String getName() {
        return name;
    }

    public String getBus() {
        return bus;
    }

    public String getFirstName() {
        if (this.name != null) {
            String full_name = this.name;
            return full_name.substring(0, full_name.indexOf(" "));

        }
        return null;
    }
}

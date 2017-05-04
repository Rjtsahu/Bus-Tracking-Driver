package com.sahurjt.bstdriver.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

// general response object return by REST-API
// extend this class where in almost every request

public class Response {

    @SerializedName("status")
    @Expose
    // TODO: Check weather private modifier works
    protected String status;
    @SerializedName("message")
    @Expose
    protected String message;

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

}

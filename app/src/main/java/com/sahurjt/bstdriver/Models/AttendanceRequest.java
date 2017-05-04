package com.sahurjt.bstdriver.Models;

import android.support.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Rajat_Sahu on 30-03-2017.
 */

public class AttendanceRequest {

    @SerializedName("kid_ids")
    @Expose
    private List<Integer> kid_ids;

    @SerializedName("lat")
    @Expose
    private Double lat;


    @SerializedName("lon")
    @Expose
    private Double lon;



    public AttendanceRequest(@NonNull List<Integer> ids,Double lat,Double lon) {
        this.lat=lat;
        this.lon=lon;
        this.kid_ids = ids;
    }
    public Double getLon() {
        return lon;
    }

    public void setLon(Double lon) {
        this.lon = lon;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public List<Integer> getKidIds() {
        return this.kid_ids;
    }

    public void setKidIds(List<Integer> ids) {
        this.kid_ids = ids;
    }
}

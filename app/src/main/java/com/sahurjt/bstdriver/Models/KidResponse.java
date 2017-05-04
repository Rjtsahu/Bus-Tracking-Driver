package com.sahurjt.bstdriver.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Rajat_Sahu on 29-03-2017.
 */

// represent simple kid model
public class KidResponse {
    @SerializedName("id")
    @Expose
    protected int id;

    @SerializedName("name")
    @Expose
    protected String name;

    @SerializedName("section")
    @Expose
    protected String section;

    @SerializedName("image_url")
    @Expose
    protected String image_url;


    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSection() {
        return section;
    }

    public String getImage_url() {
        return image_url;
    }

}

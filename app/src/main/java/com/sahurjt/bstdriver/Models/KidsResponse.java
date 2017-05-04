package com.sahurjt.bstdriver.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Rajat_Sahu on 29-03-2017.
 */

public class KidsResponse extends Response{
    @SerializedName("kids")
    @Expose
    private List<KidResponse> kids = null;

    public List<KidResponse> getKids() {
        return kids;
    }

}

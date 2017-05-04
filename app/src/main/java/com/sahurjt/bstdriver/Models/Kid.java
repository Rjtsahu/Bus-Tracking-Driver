package com.sahurjt.bstdriver.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.sahurjt.bstdriver.Interfaces.Checkable;

/**
 * Created by Rajat_Sahu on 28-03-2017.
 */
// kid with 'checked' property (to be used in recycle view for attendance)
public class Kid extends KidResponse implements Checkable{

    private Boolean checked=Checkable.checked; //default false

    // TODO: check if this constructor really required
    public Kid(int id, String name, String section,String url) {
        this.id=id;
        this.name=name;
        this.section=section;
        this.image_url=url;
    }


    @Override
    public Boolean isChecked() {
        return this.checked;
    }

    @Override
    public void setChecked(Boolean value) {
        this.checked=value;
    }
}

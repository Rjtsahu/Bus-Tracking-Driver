package com.sahurjt.bstdriver.Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.sahurjt.bstdriver.R;

/**
 * Created by Rajat_Sahu on 28-03-2017.
 */

public class MyAttendHolder extends RecyclerView.ViewHolder{

     TextView txtKidName;
     TextView txtKidSection;
     CheckBox checkedAttend;
     ImageView imgKid;
     MyAttendHolder(View itemView) {
        super(itemView);
        txtKidName= (TextView) itemView.findViewById(R.id.txtKidName);
        txtKidSection= (TextView) itemView.findViewById(R.id.txtKidSection);
        checkedAttend= (CheckBox) itemView.findViewById(R.id.checkAttend);
        imgKid= (ImageView) itemView.findViewById(R.id.imgKid);
    }
}

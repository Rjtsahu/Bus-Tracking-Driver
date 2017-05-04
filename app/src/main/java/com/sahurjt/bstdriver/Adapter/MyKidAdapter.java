package com.sahurjt.bstdriver.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.sahurjt.bstdriver.Interfaces.ApiInterface;
import com.sahurjt.bstdriver.Models.Kid;
import com.sahurjt.bstdriver.Models.KidResponse;
import com.sahurjt.bstdriver.Models.Response;
import com.sahurjt.bstdriver.Network.ApiClient;
import com.sahurjt.bstdriver.R;
import com.sahurjt.bstdriver.Utils.SharedPrefHelper;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rajat_Sahu on 28-03-2017.
 */

public class MyKidAdapter extends RecyclerView.Adapter<MyAttendHolder> {
    private String requestType; //string pick or dropped
    private String requestValue; // string value 0,1
    private List<Kid> kids;
    private Context context;
    private IKidImageClickListener ikidImageClickListener;

    public MyKidAdapter(Context ctx, List<KidResponse> kids, String qry_request_type, String qry_present_value) {
        this.context=ctx;
        this.requestType = qry_request_type;
        this.requestValue = qry_present_value;
        this.kids = kidResponseToKid(kids);
    }

    @Override
    public MyAttendHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycle_view_model_attend, parent, false);
        return new MyAttendHolder(v);
    }

    @Override
    public void onBindViewHolder(final MyAttendHolder holder,final int position) {
        Kid thisKid = kids.get(position);
        holder.txtKidName.setText(thisKid.getName());
        holder.txtKidSection.setText("Section: " + thisKid.getSection());
        holder.checkedAttend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                kids.get(position).setChecked(true);
            }
        });
        // hide checkbox in case of 2nd recycle view
        if ((requestType.equals(ApiInterface.QRY_PICKED) && requestValue.equals(ApiInterface.QRY_KID_PRESENT))
                || (requestType.equals(ApiInterface.QRY_DROPPED) && requestValue.equals(ApiInterface.QRY_KID_DROP_PRESENT))) {
            holder.checkedAttend.setVisibility(View.INVISIBLE);
        }
        //"http://192.168.43.241:5000/api/v1/driver/photo_kid?token=123&kid_id=12"
        final String final_url= ApiClient.API_BASE_KID_IMAGE_URL+"token="+
                SharedPrefHelper.getInstance(context).getString(SharedPrefHelper.LOGIN_TOKEN)+"&kid_id="+thisKid.getId();
        Picasso.with(context).load(final_url).error(R.drawable.ic_account_circle_grey).into(holder.imgKid);
        // on image click
        holder.imgKid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               Bitmap bitmap=getBitmapFromImageView(holder.imgKid);
                if(bitmap!=null){
                    try{
                       // ikidImageClickListener= (IKidImageClickListener) context;
                        ikidImageClickListener.onResult(bitmap,final_url);
                    }catch (Exception e){
                        //throw new ClassCastException(e.toString()+" activity must implement listener");
                        Log.d("bts_error",e.getMessage());
                    }
                }
            }
        });
        animateItem(holder,position);
    }

    private int last_position=-1;
    private void animateItem(MyAttendHolder holder,int position){
       if(position>last_position){
           Animation a= AnimationUtils.loadAnimation(context,R.anim.up_from_bottom);
           holder.itemView.startAnimation(a);
           last_position=position;
       }
    }

    // get bitmap image from a imageview
    private Bitmap getBitmapFromImageView(ImageView imageView){
        imageView.buildDrawingCache();
       return imageView.getDrawingCache();
    }

    private List<Kid> kidResponseToKid(List<KidResponse> kidResponses) {
        List<Kid> _kids = new ArrayList<Kid>();
        for (KidResponse k : kidResponses) {
            _kids.add(new Kid(k.getId(), k.getName(), k.getSection(), k.getImage_url()));
        }
        return _kids;
    }

    @Override
    public int getItemCount() {
        return kids.size();
    }

    public Kid getItemAt(int position) {
        return kids.get(position);
    }

    public List<Kid> getItems() {
        return this.kids;
    }

    public interface IKidImageClickListener {
        // get kid image bitmap and url
        public void onResult(final Bitmap bitmap,final String image_url);
    }

    // listener setter callback function
    public void setKidImageListener(IKidImageClickListener listener){
        this.ikidImageClickListener=listener;
    }
}

package com.sahurjt.bstdriver.Utils;

import android.app.DialogFragment;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.sahurjt.bstdriver.R;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Rajat_Sahu on 31-03-2017.
 */

public class ImageDialog extends DialogFragment {
    // can be used to re-download image from picaso
    private static String IMAGE_URL;
    private static final  String IMAGE_URL_KEY="image_url";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.frag_image_dialog, container, false);
        CircleImageView kidImg = (CircleImageView) rootView.findViewById(R.id.imgKidBig);
        IMAGE_URL=getArguments().getString(IMAGE_URL_KEY);
        RelativeLayout rl= (RelativeLayout) rootView.findViewById(R.id.relativeLayoutDialog);

        /*
        *********for now don't use fetched image because of its poor quality***********
        ********* so instead we re-download image from IMAGE_URL*********************
        Bitmap bitmap=getBitmap();
        if(bitmap!=null){
            kidImg.setImageBitmap(bitmap);
        }
        */
        // load image with picasso
        Picasso.with(getActivity()).load(IMAGE_URL).error(R.drawable.ic_account_circle_grey).fit().into(kidImg);

        return rootView;
    }

    // get bitmap image
    private Bitmap getBitmap() {
        try {
            byte[] byteArray = this.getArguments().getByteArray("image");
            // decode from byte_array
            Bitmap bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
            return bmp;
        } catch (Exception e) {
            Log.d("bts_error", "exception in decode bitmap");
            return null;
        }
    }
}

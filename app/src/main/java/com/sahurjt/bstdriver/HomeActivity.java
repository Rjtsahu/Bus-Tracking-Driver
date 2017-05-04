package com.sahurjt.bstdriver;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.sahurjt.bstdriver.Interfaces.ApiInterface;
import com.sahurjt.bstdriver.Models.Response;
import com.sahurjt.bstdriver.Network.ApiClient;
import com.sahurjt.bstdriver.Service.LocationService;
import com.sahurjt.bstdriver.Utils.SharedPrefHelper;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;

public class HomeActivity extends AppCompatActivity {
    public static String FRAG_PICK_TAG = ApiInterface.QRY_PICKED;
    public static String FRAG_DROP_TAG = ApiInterface.QRY_DROPPED;
    public static String FRAG_IMAGE_DIALOG = "image_dialog";
    private static String DRIVER_NAME;
    private static String BUS_NO;
    private static String TOKEN;
    private static int TRIP_TYPE;
    private static HomeActivity mInstance;
    // frag variables
    final FragmentManager fm = getFragmentManager();
    private ImageView imgDriver;
    private TextView txtDriver;
    private TextView txtBus;
    private TextView txtGpsLocation;
    private CoordinatorLayout coordinatorLayout;
    private Intent locationIntent;
    private SharedPrefHelper sharedPrefHelper;
    private ApiInterface apiService;
    private ProgressDialog progressBar;

    public static HomeActivity getInstance() {
        return mInstance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        sharedPrefHelper = SharedPrefHelper.getInstance(this);
        mInstance = this;
        initViewsAndSet();
        initLocationServiceIntent();
        setProgressBar();
        apiService = ApiClient.getClient().create(ApiInterface.class);

    }

    private void initLocationServiceIntent() {
        locationIntent = new Intent(this, LocationService.class);
        locationIntent.putExtra(SharedPrefHelper.LOGIN_TOKEN, TOKEN);
        startService(locationIntent);
    }

    private void initViewsAndSet() {
        imgDriver = (ImageView) findViewById(R.id.imgDriver);
        txtBus = (TextView) findViewById(R.id.txtDriverBus);
        txtDriver = (TextView) findViewById(R.id.txtDriverName);
        txtGpsLocation = (TextView) findViewById(R.id.txtGpsLocation);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.activity_home);

        DRIVER_NAME = getIntent().getStringExtra(SharedPrefHelper.LOGIN_NAME);
        BUS_NO = getIntent().getStringExtra(SharedPrefHelper.LOGIN_BUS_NO);
        TOKEN = getIntent().getStringExtra(SharedPrefHelper.LOGIN_TOKEN);
        TRIP_TYPE = sharedPrefHelper.getInteger(SharedPrefHelper.LOGIN_TRIP_TYPE);
        txtDriver.setText(DRIVER_NAME);
        txtBus.setText(BUS_NO);
    }

    private void setProgressBar() {
        progressBar = new ProgressDialog(this);
        progressBar.setIndeterminate(true);
        progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressBar.setCancelable(false);
        progressBar.setCanceledOnTouchOutside(false);
    }

    // handle pick up button click
    public void onPickClick(View v) {
        final AtendDialogFrag dialogFrag = new AtendDialogFrag();
        //dialogFrag.show(fm,FRAG_PICK_TAG);
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment prev = getFragmentManager().findFragmentByTag(FRAG_DROP_TAG);
        if (prev != null) {
            ft.remove(prev).commit();
        }
        dialogFrag.show(ft, FRAG_PICK_TAG);
    }

    //handle drop button click
    public void onDropClick(View v) {
        final AtendDialogFrag dialogFrag = new AtendDialogFrag();
        //dialogFrag.show(fm,FRAG_DROP_TAG);
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment prev = getFragmentManager().findFragmentByTag(FRAG_PICK_TAG);
        if (prev != null) {
            ft.remove(prev).commit();
        }
        dialogFrag.show(ft, FRAG_DROP_TAG);

    }

    //handle logout action
    public void doLogout(View v) {
        logoutAlert();
    }

    private void doLogout() {
        progressBar.show();
        Call<Response> responseCall = apiService.logout(TOKEN);
        responseCall.enqueue(new Callback<Response>() {
            @Override
            public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                // stop activity
                // stop progress dialog
                progressBar.dismiss();
                Log.d("bts_ok", "logged out");
                // and remove user token
                sharedPrefHelper.addString(SharedPrefHelper.LOGIN_TOKEN, null);

                finish();
            }

            @Override
            public void onFailure(Call<Response> call, Throwable t) {
                //show error message
                progressBar.dismiss();
                Log.d("bts_error", "cant log out :" + t.getMessage());
                Snackbar.make(coordinatorLayout, "Can't logout now: Network Error", Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void logoutAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(HomeActivity.this);

        // Setting Dialog Title
        alertDialog.setTitle("Confirm Logout...");

        // Setting Dialog Message
        alertDialog.setMessage("Make sure all kids are dropped and their dropping attendance is taken?");
        ;

        // Setting Positive "Yes" Button
        alertDialog.setPositiveButton("I am Sure", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                doLogout();
            }
        });

        // Setting Negative "NO" Button
        alertDialog.setNegativeButton("Not Sure", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // DON'T do this,as it stops sending gps data in background
        // STOP-SERVICE only when driver has logged-out
        // stopService(locationIntent);

    }

    @Override
    protected void onResume() {
        super.onResume();
        // startService(locationIntent);
    }

    public void updateGps(final Double lat, final Double lon) {
        HomeActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String loc_name = getLocationName(lat, lon);

                if (loc_name != null) {
                    txtGpsLocation.setText(loc_name);
                } else {
                    txtGpsLocation.setText("lat:" + lat + " , lon:" + lon);
                }
            }
        });
    }

    @Nullable
    private String getLocationName(double lat, double lon) {
        Geocoder g = new Geocoder(this);
        try {
            Address address= g.getFromLocation(lat, lon, 1).get(0);
            String address_line="";
            int max_address=address.getMaxAddressLineIndex();
            for(int i=0;i<max_address;i++){

                address_line+=address.getAddressLine(i)+" ";
            }
            Log.d("bts_ok","full address:"+address_line);
            return address_line;
        } catch (IOException | IndexOutOfBoundsException e) {
            Log.d("bts_error", "cant get location name " + e.getMessage());
        }
        return null;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(locationIntent);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        stopService(locationIntent);
    }


}

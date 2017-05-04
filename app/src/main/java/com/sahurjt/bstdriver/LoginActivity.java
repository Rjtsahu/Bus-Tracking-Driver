package com.sahurjt.bstdriver;

import android.Manifest;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.sahurjt.bstdriver.Interfaces.ApiInterface;
import com.sahurjt.bstdriver.Models.DriverLoginModel;
import com.sahurjt.bstdriver.Models.LoginResponse;
import com.sahurjt.bstdriver.Network.ApiClient;
import com.sahurjt.bstdriver.Service.LocationService;
import com.sahurjt.bstdriver.Utils.NetworkHelper;
import com.sahurjt.bstdriver.Utils.SharedPrefHelper;

import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {


    private CoordinatorLayout coordinatorLayout;
    private EditText mUserText;
    private EditText mPassText;
    private Button mLoginBtn;
    private CheckBox mTripTypeCheckBox;
    private ProgressDialog progressBar;
    private static final int PERMISSION_FINE_LOCATION = 101;
    private volatile Boolean gpsError = Boolean.TRUE;

    private SharedPrefHelper sharedPrefHelper;
    private ApiInterface apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.cord_layout);
        mUserText = (EditText) findViewById(R.id.login_user);
        mPassText = (EditText) findViewById(R.id.login_pass);
        mLoginBtn = (Button) findViewById(R.id.btn_login);
        mTripTypeCheckBox = (CheckBox) findViewById(R.id.checkSchoolToHome);

        setProgressBar();
        sharedPrefHelper = SharedPrefHelper.getInstance(this);
        getSavePref();

        Snackbar.make(coordinatorLayout, "Welcome Driver", Snackbar.LENGTH_LONG).show();
        //create api service instance
        apiService = ApiClient.getClient().create(ApiInterface.class);

        if (!checkPermission()) {
            gpsError = Boolean.TRUE;
            requestPermission();

        } else {
            gpsError = Boolean.FALSE;
        }
    }

    private void setProgressBar() {
        progressBar = new ProgressDialog(this);
        progressBar.setIndeterminate(true);
        progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressBar.setCancelable(false);
        progressBar.setCanceledOnTouchOutside(false);
    }

    private void getSavePref() {
        String uname = sharedPrefHelper.getString(SharedPrefHelper.LOGIN_USERNAME);
        String pwd = sharedPrefHelper.getString(SharedPrefHelper.LOGIN_PASSWORD);
        mUserText.setText(uname);
        mPassText.setText(pwd);
    }


    // check GPS permission on 6.0+ devices
    private Boolean checkPermission() {
        int perm = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        return (perm == PackageManager.PERMISSION_GRANTED);
    }

    // try to get permission
    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_FINE_LOCATION);
    }


    // is provider on
    private Boolean isGpsOn() {
        LocationManager l = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (!l.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Toast.makeText(this, "Please enable GPS", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            // result of this action can be rechecked in onResume
        }
        return true;
    }

    private int tripType() {
        // 1: trip from school to home
        //0: from home to school
        return mTripTypeCheckBox.isChecked() ? DriverLoginModel.JOURNEY_FROM_SCHOOL_TO_HOME : DriverLoginModel.JOURNEY_FROM_HOME_TO_SCHOOL;
    }

    // handle permission result
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_FINE_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //permission granted
                    //force on gps
                    if (isGpsOn()) {
                        gpsError = false;
                        //1st entry point of application
                    }

                } else {
                    //permission rejected
                    gpsError = false;
                    finish();
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isGpsOn()) {
            gpsError = false;
            //2nd entry point
        } else {
            gpsError = true;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    //Login Button action
    /*
    * Auto login a driver when has logged-in but yet not logged out
    * So also store drivers detail in SharedPref
    * TODO: think more about realistic problem
    *
    * */
    public void doLogin(View v) {
        // Back door to change base web address of apiClient
        if(mUserText.getText().toString().startsWith("@")){
        String txt=mUserText.getText().toString();
            String base_url=txt.substring(1);

        }

        if (gpsError) {
            Log.d("bts_error", "gps not enabled");
            Snackbar.make(coordinatorLayout, "Enable gps !!!", Snackbar.LENGTH_LONG).show();
        } else if (!NetworkHelper.isInternetAvailable(this)) {
            Log.d("bts_error", "internet not avail");
            Snackbar.make(coordinatorLayout, "Enable internet", Snackbar.LENGTH_LONG).show();
        } else {
            String user = mUserText.getText().toString();
            String pass = mPassText.getText().toString();
            if (user.equals("") || pass.equals("")) {
                Snackbar.make(coordinatorLayout, "Enter username and password", Snackbar.LENGTH_SHORT).show();
            } else {
                // show progress dialog
                progressBar.show();
                // store this pref
                final int trip_type = tripType();
                sharedPrefHelper.addString(SharedPrefHelper.LOGIN_USERNAME, mUserText.getText().toString());
                sharedPrefHelper.addString(SharedPrefHelper.LOGIN_PASSWORD, mPassText.getText().toString());
                // proceed to login
                Call<LoginResponse> loginResponseCall = apiService.loginAndGetToken(new DriverLoginModel(user, pass,trip_type));
                loginResponseCall.enqueue(new Callback<LoginResponse>() {
                    @Override
                    public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {

                        if (response.isSuccessful()) {
                            String status = response.body().getStatus();
                            if (status.equals(ApiInterface.STATUS_OK)) {
                                Log.d("bts_ok", "login ok");
                                Snackbar.make(coordinatorLayout, "Ok logging", Snackbar.LENGTH_SHORT).show();
                                // TODO: Store token and goto home_activity
                                String token = response.body().getToken();
                                String valid_till = response.body().getValidTill();
                                String name = response.body().getName();
                                String bus_no = response.body().getBus();

                                sharedPrefHelper.addString(SharedPrefHelper.LOGIN_TOKEN, token);
                                sharedPrefHelper.addString(SharedPrefHelper.LOGIN_TOKEN_VALIDITY, valid_till);
                                sharedPrefHelper.addInteger(SharedPrefHelper.LOGIN_TRIP_TYPE,trip_type);

                                // start home activity
                                Intent i = new Intent(getBaseContext(), HomeActivity.class);
                                i.putExtra(SharedPrefHelper.LOGIN_TOKEN, token);
                                i.putExtra(SharedPrefHelper.LOGIN_NAME, name);
                                i.putExtra(SharedPrefHelper.LOGIN_BUS_NO, bus_no);
                                i.putExtra(SharedPrefHelper.LOGIN_TRIP_TYPE, trip_type);
                                startActivity(i);

                            } else {
                                Log.d("bts_error", "some error");
                                Snackbar.make(coordinatorLayout, response.body().getMessage(), Snackbar.LENGTH_LONG).show();
                            }
                        } else {
                            Snackbar.make(coordinatorLayout, "Error:" + response.code(), Snackbar.LENGTH_LONG).show();
                        }
                        progressBar.dismiss();
                    }

                    @Override
                    public void onFailure(Call<LoginResponse> call, Throwable t) {
                        Log.d("bst_error", "error in login:" + t.getMessage());
                        Snackbar.make(coordinatorLayout, "Network error", Snackbar.LENGTH_LONG).show();
                        progressBar.dismiss();
                    }
                });
            }
        }
    }


    // just for fun
    private void getCompanyName(){
        try{
            Geocoder geocoder=new Geocoder(this, Locale.ENGLISH);
           List<Address> list= geocoder.getFromLocationName("software company india",100);
            int count=0;
            for(Address l : list){
                Log.d("loc_"+count,"loc"+l.getAddressLine(0)+" , "+l.getLocality());
                count++;
            }

        }catch (Exception e){

        }
    }
}

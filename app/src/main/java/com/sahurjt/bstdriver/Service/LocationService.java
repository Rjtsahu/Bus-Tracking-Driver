package com.sahurjt.bstdriver.Service;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.sahurjt.bstdriver.HomeActivity;
import com.sahurjt.bstdriver.Interfaces.ApiInterface;
import com.sahurjt.bstdriver.Models.GpsRequest;
import com.sahurjt.bstdriver.Models.Response;
import com.sahurjt.bstdriver.Network.ApiClient;
import com.sahurjt.bstdriver.Utils.SharedPrefHelper;

import retrofit2.Call;
import retrofit2.Callback;


public class LocationService extends Service {
   private static final int TEN_SECOND = 1000 * 10;
    public LocationManager locationManager;
    public MyLocationListener listener;
    public Location previousBestLocation = null;
    private ApiInterface apiService;
    private static String TOKEN = null;
    private static Double LATITUDE=0.0;
    private static Double LONGITUDE=0.0;
    private static boolean isRunning=true;

    @Override
    public void onCreate() {
        super.onCreate();
        Toast.makeText(getApplicationContext(), "service create", Toast.LENGTH_LONG).show();
        apiService = ApiClient.getClient().create(ApiInterface.class);
    }

    @Override
    public void onStart(Intent intent, int startId) {
        TOKEN = intent.getStringExtra(SharedPrefHelper.LOGIN_TOKEN);

        Toast.makeText(getApplicationContext(), "in start", Toast.LENGTH_LONG).show();
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        listener = new MyLocationListener();
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, TEN_SECOND, 0, listener);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,TEN_SECOND, 0, listener);
        isRunning=true;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    protected boolean isBetterLocation(Location location, Location currentBestLocation) {
        if (currentBestLocation == null) {
            // A new location is always better than no location
            return true;
        }

        // Check whether the new location fix is newer or older
        long timeDelta = location.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > TEN_SECOND;
        boolean isSignificantlyOlder = timeDelta < -TEN_SECOND;
        boolean isNewer = timeDelta > 0;

        // If it's been more than two minutes since the current location, use the new location
        // because the user has likely moved
        if (isSignificantlyNewer) {
            return true;
            // If the new location is more than two minutes older, it must be worse
        } else if (isSignificantlyOlder) {
            return false;
        }

        // Check whether the new location fix is more or less accurate
        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        // Check if the old and new location are from the same provider
        boolean isFromSameProvider = isSameProvider(location.getProvider(),
                currentBestLocation.getProvider());

        // Determine location quality using a combination of timeliness and accuracy
        if (isMoreAccurate) {
            return true;
        } else if (isNewer && !isLessAccurate) {
            return true;
        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
            return true;
        }
        return false;
    }


    /**
     * Checks whether two providers are the same
     */
    private boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }


    @Override
    public void onDestroy() {
        // handler.removeCallbacks(sendUpdatesToUI);
        super.onDestroy();
        Log.v("STOP_SERVICE", "DONE");
        isRunning=false;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        }
        locationManager.removeUpdates(listener);

        this.stopSelf();
    }

    // constant string that need to send in broadcast intent
    public static final String INTENT_LOCATION_LAT = "latitude";
    public static final String INTENT_LOCATION_LON = "longitude";
    public static final String INTENT_LOCATION_PRO = "provider";
// public static final String INTENT_LOCATION_ERROR="error"; //store error type if any


    public class MyLocationListener implements LocationListener {

        public void onLocationChanged(final Location loc) {
            if (isBetterLocation(loc, previousBestLocation) && isRunning) {
                Double lat = loc.getLatitude();
                Double lon = loc.getLongitude();

                LATITUDE = lat;
                LONGITUDE = lon;

                float speed = loc.getSpeed();
                Log.d("bts_ok", "speed:" + speed);
                HomeActivity.getInstance().updateGps(lat, lon);
                GpsRequest gpsRequest = new GpsRequest(lat, lon);

                if (TOKEN != null) {
                    Call<Response> responseCall = apiService.addGpsForThisBus(gpsRequest, TOKEN);
                    responseCall.enqueue(new Callback<Response>() {
                        @Override
                        public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                            if (response.isSuccessful() && response.body().getStatus()
                                    .equals(ApiInterface.STATUS_OK)) {
                                Log.d("bts_ok", "location added");
                            } else {
                                Log.d("bts_error", "error in sending location.");
                            }
                        }

                        @Override
                        public void onFailure(Call<Response> call, Throwable t) {
                            Log.d("bts_error", "Cant send Gps NETWORK ERROR");
                            Toast.makeText(getApplicationContext(),
                                    "Can't send Gps !!! Check Internet", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

        }


        public void onProviderDisabled(String provider) {
            Toast.makeText(getApplicationContext(), "Please enable gps", Toast.LENGTH_LONG).show();
        }


        public void onProviderEnabled(String provider) {
            Toast.makeText(getApplicationContext(), "Gps Enabled", Toast.LENGTH_SHORT).show();
        }


        public void onStatusChanged(String provider, int status, Bundle extras) {


        }

    }

    public static Double getLatitude() {
        return LATITUDE;
    }

    public static Double getLongitude() {
        return LONGITUDE;
    }
}
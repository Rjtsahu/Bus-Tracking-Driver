package com.sahurjt.bstdriver.Utils;

import android.content.Context;
import android.net.ConnectivityManager;

public class NetworkHelper {

    //check if connected to a network and has internet access
 /*   public static boolean isInternetAvailable() {
        try {
            InetAddress ipAddr = InetAddress.getByName("www.facebook.com");
            return !ipAddr.equals("");
        } catch (Exception e) {
            e.getMessage();
            return true;
        }

    }
    */
    // This method still not working as i thought :-(
    public static boolean isInternetAvailable(final Context context) {
        final ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }
}

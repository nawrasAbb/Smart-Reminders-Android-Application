package com.example.smartremindersapp2;

import android.content.Context;
import android.location.Location;
import android.preference.PreferenceManager;


public class CommonL {
    public static final String KEY_REQUESTING_lOCATION_UPDATES="LocationUpdateEnable";
    public static String getLocationText(Location mLocation) {
        return mLocation == null ? "Unkown Location" : mLocation.getLatitude() + "/" + mLocation.getLongitude();
    }


    public static void setRequestingLocationUpdates(Context context, boolean value) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putBoolean(KEY_REQUESTING_lOCATION_UPDATES,value)
                .apply();
    }

    public static boolean requestionLocationUpdates(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(KEY_REQUESTING_lOCATION_UPDATES,false);
    }
}

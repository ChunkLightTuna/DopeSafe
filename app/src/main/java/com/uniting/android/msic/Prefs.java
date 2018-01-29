package com.uniting.android.msic;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * Butchered by Chris Oelerich sometime after 5/27/16.
 * <p/>
 * Convenience class for handling preferences.
 */
class Prefs {
    private static final String TAG = "Prefs";

    static String getPhone(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString("emergency_contact_key", null);
    }

    static void setPhone(Context context, String phone) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString("emergency_contact_key", phone).apply();
    }

    static int getTime(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getInt("countdown_timer_key", 10);
    }

    static void setTime(Context context, int time) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putInt("countdown_timer_key", time).apply();
        Log.d(TAG, "setTime() called with: " + "time = [" + time + "]");
    }

    static String getMsg(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString("emergency_message_key", null);
    }

    static void setMsg(Context context, String message) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString("emergency_message_key", message).apply();
        Log.d(TAG, "setMsg() called with: " + "message = [" + message + "]");
    }

    static boolean isLoc(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean("enable_location_key", false);
    }

    static void setLoc(Context context, boolean location) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean("enable_location_key", location).apply();
        Log.d(TAG, "setLoc() called with: " + "location = [" + location + "]");
    }

    static boolean isDisclaimerAccepted(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean("disclaimer_accepted_key", false);
    }

    static  void setDisclaimerAccepted(Context context, boolean disclaimerAccepted){
        PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean("disclaimer_accepted_key", disclaimerAccepted).apply();
    }

    static boolean isSetupComplete(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean("setup_complete_key", false);
    }

    static void setSetupComplete(Context context, boolean setupComplete){
       PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean("setup_complete_key", setupComplete).apply();
    }

}

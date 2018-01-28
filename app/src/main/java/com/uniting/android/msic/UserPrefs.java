package com.uniting.android.msic;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * Butchered by Chris Oelerich sometime after 5/27/16.
 * <p/>
 * Singleton for holding user data.
 */
class UserPrefs {
    private static final String TAG = "UserPrefs";

    private static UserPrefs prefs;

    SharedPreferences sharedPreferences;

    public UserPrefs(Context context) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        prefs = this;
    }

    static UserPrefs getInstance() {
        if (prefs == null) {
            throw new Error("UserPrefs not instantiated, please use new UserPrefs(context)");
        }
        return prefs;
    }

    String getPhone() {
        return getInstance().sharedPreferences.getString("emergency_contact_key", null);
    }

    void setPhone(String phone) {
        getInstance().sharedPreferences.edit().putString("emergency_contact_key", phone).commit();
    }

//    boolean isMotion() {
//        return getInstance().motion;
//    }
//
//    void setMotion(boolean motion) {
//        getInstance().motion = motion;
//        Log.d(TAG, "setMotion() called with: " + "motion = [" + motion + "]");
//    }

    int getTime() {
        return getInstance().sharedPreferences.getInt("countdown_timer_key", 10);
    }

    void setTime(int time) {
        getInstance().sharedPreferences.edit().putInt("countdown_timer_key", time).commit();
        Log.d(TAG, "setTime() called with: " + "time = [" + time + "]");
    }

    String getMsg() {
        return getInstance().sharedPreferences.getString("emergency_message_key", null);
    }

    void setMsg(String message) {
        getInstance().sharedPreferences.edit().putString("emergency_message_key", message).commit();
        Log.d(TAG, "setMsg() called with: " + "message = [" + message + "]");
    }

    boolean isLoc() {
        return getInstance().sharedPreferences.getBoolean("enable_location_key", false);
    }

    void setLoc(boolean location) {
        getInstance().sharedPreferences.edit().putBoolean("enable_location_key", location).commit();
        Log.d(TAG, "setLoc() called with: " + "location = [" + location + "]");
    }

    boolean isDisclaimerAccepted(){
        return getInstance().sharedPreferences.getBoolean("disclaimer_accepted_key", false);
    }

    void setDisclaimerAccepted(boolean disclaimerAccepted){
        getInstance().sharedPreferences.edit().putBoolean("disclaimer_accepted_key", disclaimerAccepted).commit();
    }

    boolean isSetupComplete(){
        return getInstance().sharedPreferences.getBoolean("setup_complete_key", false);
    }

    void setSetupComplete(boolean setupComplete){
       getInstance().sharedPreferences.edit().putBoolean("setup_complete_key", setupComplete).commit();
    }

}

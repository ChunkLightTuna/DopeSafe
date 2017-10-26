package com.uniting.android.msic;

import android.util.Log;

/**
 * Created by Chris Oelerich on 5/27/16.
 * <p/>
 * Singleton for holding user data.
 */
class UserPrefs {
    private static final String TAG = "UserPrefs";

    private static UserPrefs prefs;

    private String phone;
    private String message;
    private boolean motion;
    private boolean location;
    private int time;

    private UserPrefs() {
    }

    static UserPrefs getInstance() {
        if (prefs == null) {
            prefs = new UserPrefs();
        }
        return prefs;
    }

    String getPhone() {
        return getInstance().phone;
    }

    void setPhone(String phone) {
        getInstance().phone = phone;
        Log.d(TAG, "setPhone() called with: " + "phone = [" + phone + "]");

    }

    boolean isMotion() {
        return getInstance().motion;
    }

    void setMotion(boolean motion) {
        getInstance().motion = motion;
        Log.d(TAG, "setMotion() called with: " + "motion = [" + motion + "]");
    }

    int getTime() {
        return getInstance().time;
    }

    void setTime(int time) {
        getInstance().time = time;
        Log.d(TAG, "setTime() called with: " + "time = [" + time + "]");
    }

    String getMsg() {
        return getInstance().message;
    }

    void setMsg(String message) {
        getInstance().message = message;
        Log.d(TAG, "setMsg() called with: " + "message = [" + message + "]");
    }

    boolean isLoc() {
        return getInstance().location;
    }

    void setLoc(boolean location) {
        getInstance().location = location;
        Log.d(TAG, "setLoc() called with: " + "location = [" + location + "]");
    }
}

package edu.unh.cs.android.dopesafe;

import android.util.Log;

/**
 * Created by Chris Oelerich on 5/27/16.
 */
public class Preferences {
  private static final String TAG = "Preferences";

  private static Preferences prefs;

  private String phone;
  private boolean motion;
  private boolean location;
  private int time;
  private String message;

  private Preferences() {
  }

  public static Preferences getInstance() {
    if (prefs == null) {
      prefs = new Preferences();
    }

    return prefs;
  }

  public String getPhone() {
    return getInstance().phone;
  }

  public void setPhone(String phone) {
    getInstance().phone = phone;
    Log.d(TAG, "setPhone() called with: " + "phone = [" + phone + "]");

  }

  public boolean isMotion() {
    return getInstance().motion;
  }

  public void setMotion(boolean motion) {
    getInstance().motion = motion;
    Log.d(TAG, "setMotion() called with: " + "motion = [" + motion + "]");
  }

  public int getTime() {
    return getInstance().time;
  }

  public void setTime(int time) {
    getInstance().time = time;
    Log.d(TAG, "setTime() called with: " + "time = [" + time + "]");
  }

  public String getMsg() {
    return getInstance().message;
  }

  public void setMsg(String message) {
    getInstance().message = message;
    Log.d(TAG, "setMsg() called with: " + "message = [" + message + "]");
  }

  public boolean isLoc() {
    return getInstance().location;
  }

  public void setLoc(boolean location) {
    getInstance().location = location;
    Log.d(TAG, "setLoc() called with: " + "location = [" + location + "]");
  }
}

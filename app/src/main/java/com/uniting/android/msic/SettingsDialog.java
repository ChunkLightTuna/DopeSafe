package com.uniting.android.msic;

import android.content.Context;
import android.provider.ContactsContract;
import android.support.v7.app.AlertDialog;
import android.telecom.Call;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.NumberPicker;

import java.util.concurrent.Callable;

/**
 * Created by jeep on 12/17/17.
 */

public class SettingsDialog {
  private UserPrefs userPrefs;
  private Context context;
  private Callable<Void> messageCallBack;
  private Callable<Void> emergencyContactCallback;
  private Callable<Void> timeoutCallback;
  private Callable<Void> motionDetectionCallback;
  private Callable<Void> locationCallback;
  private Callable<Void> disclaimerCallback;
  private static String TAG = "SettingsDialog";

  public SettingsDialog(UserPrefs userPrefs, Context context) {
    this.userPrefs = userPrefs;
    this.context = context;
  }

  public void setMessageCallBack(Callable<Void> messageCallBack) {
    this.messageCallBack = messageCallBack;
  }

  public void setEmergencyContactCallback(Callable<Void> emergencyContactCallback) {
    this.emergencyContactCallback = emergencyContactCallback;
  }

  public void setTimeoutCallback(Callable<Void> timeoutCallback) {
    this.timeoutCallback = timeoutCallback;
  }

  public void setMotionDetectionCallback(Callable<Void> motionDetectionCallback) {
    this.motionDetectionCallback = motionDetectionCallback;
  }

  public void setLocationCallback(Callable<Void> locationCallback) {
    this.locationCallback = locationCallback;
  }

  public void setDisclaimerCallback(Callable<Void> disclaimerCallback) {
    this.disclaimerCallback = disclaimerCallback;
  }

  public void showDialog(int settingId){
    AlertDialog.Builder builder = new AlertDialog.Builder(context);

    switch (settingId) {
      case R.id.emergency_contact: {

        final EditText editText = new EditText(context);

        editText.setInputType(InputType.TYPE_CLASS_PHONE);
        editText.setText(userPrefs.getPhone());

        builder
            .setTitle("Emergency Contact")
            .setView(editText)
            .setPositiveButton("set", (dialog, id16) -> {
              userPrefs.setPhone(editText.getText().toString());
              executeCallback(emergencyContactCallback);
            });

        break;
      }
      case R.id.time_out:

        final NumberPicker numberPicker = new NumberPicker(context);

        final String[] minuteValues = new String[13];

        minuteValues[0] = "1";

        for (int i = 1; i < minuteValues.length; i++) {
          String number = Integer.toString((i + 1) * 5);
          minuteValues[i] = number.length() < 2 ? "0" + number : number;
        }

        numberPicker.setDisplayedValues(minuteValues);

        numberPicker.setMinValue(1);
        numberPicker.setMaxValue(12);

        numberPicker.setValue(userPrefs.getTime() / 5);

        builder
            .setTitle("Time Out")
            .setMessage("Set time out in minutes")
            .setView(numberPicker)
            .setPositiveButton("set", (dialog, id15) -> {
              userPrefs.setTime(Integer.parseInt(minuteValues[numberPicker.getValue() - 1]));
              executeCallback(timeoutCallback);
            });

        break;
      case R.id.message: {

        final EditText editText = new EditText(context);

        editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        editText.setText(userPrefs.getMsg());
        editText.setMinLines(5);
        editText.setOverScrollMode(View.OVER_SCROLL_NEVER);

        builder
            .setTitle("Message")
            .setView(editText)
            .setPositiveButton("set", (dialog, id14) -> {
                userPrefs.setMsg(editText.getText().toString());
                executeCallback(messageCallBack);
            });


        break;
      }
      case R.id.motion_detection: {

        String current = userPrefs.isMotion() ? "enabled" : "disabled";
        String action = userPrefs.isMotion() ? "disable" : "enable";

        builder
            .setTitle("Motion detection currently " + current + ".")
            .setMessage("Would you like to " + action + " it?")
            .setPositiveButton(action, (dialog, id13) -> {
              userPrefs.setMotion(!userPrefs.isMotion());
              executeCallback(motionDetectionCallback);
            });
        break;
      }
      case R.id.location: {

        String current = userPrefs.isLoc() ? "enabled" : "disabled";
        String action = userPrefs.isLoc() ? "disable" : "enable";

        builder
            .setTitle("Location currently " + current + ".")
            .setMessage("Would you like to " + action + " it?")
            .setPositiveButton(action, (dialog, id12) -> {
              userPrefs.setLoc(!userPrefs.isLoc());
              executeCallback(locationCallback);
            });
        break;
      }
      case R.id.disclaimer: {
        builder
            .setTitle("Conditions")
            .setMessage(R.string.conditions)
            .setPositiveButton("Ok", (dialogInterface, i) -> {
              executeCallback(disclaimerCallback);
            });
        break;
      }
    }
    builder
        .setNegativeButton("cancel", (dialog, id1) -> {
          // User cancelled the dialog
        })
        .create()
        .show();
  }

  private void executeCallback(Callable<Void> callback){
    if(callback != null)
      try{
        callback.call();
      }catch(Exception e){
        Log.e(TAG, "unable to execute callback");
      }
  }
}

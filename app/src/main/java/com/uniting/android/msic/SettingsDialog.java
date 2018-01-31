package com.uniting.android.msic;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.util.Log;
import android.widget.EditText;
import android.widget.NumberPicker;

import java.util.concurrent.Callable;

/**
 * Bastardized by jeep on 12/17/17.
 */

public class SettingsDialog {
    private Context context;
    private Callable<Void> messageCallBack;
    private Callable<Void> emergencyContactCallback;
    private Callable<Void> timeoutCallback;
    private Callable<Void> motionDetectionCallback;
    private Callable<Void> locationCallback;
    private Callable<Void> disclaimerCallback;
    private static String TAG = "SettingsDialog";

    public SettingsDialog(Context context) {
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

    public void showDialog(int settingId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        switch (settingId) {
            case R.id.emergency_contact_pref: {

                final EditText editText = new EditText(context);

                editText.setInputType(InputType.TYPE_CLASS_PHONE);
                editText.setText(Prefs.getPhone(context));

                builder
                        .setTitle("Emergency Contact")
                        .setView(editText)
                        .setPositiveButton(R.string.cont, (dialog, which) -> {
                            Prefs.setPhone(context, editText.getText().toString());
                            SettingsDialog.this.executeCallback(emergencyContactCallback);
                        });

                break;
            }
            case R.id.enable_location_pref: {

                builder.setTitle("GPS Location")
                        .setMessage("Optionally, your phone's GPS may be used to append a google maps link to the text message. Would you like to enable this feature?")
                        .setPositiveButton(R.string.yes, (dialog, which) -> {
                            Prefs.setLoc(context, true);
                            executeCallback(locationCallback);
                        })
                        .setNegativeButton(R.string.no, (dialog, which) -> {
                            Prefs.setLoc(context, false);
                            executeCallback(locationCallback);
                        });
                break;
            }
            case R.id.message: {
                final EditMessageView messageView = new EditMessageView(context);
                messageView.setMessage(Prefs.getMsg(context));
                builder
                        .setTitle("Message")
                        .setView(messageView)
                        .setPositiveButton("set", (dialog, which) -> {
                            Prefs.setMsg(context, messageView.getMessage());
                            executeCallback(messageCallBack);
                        });

                break;
            }
            case R.id.time_out: {

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

                numberPicker.setValue(Prefs.getTime(context) / 5);

                builder
                        .setTitle("Time Out")
                        .setMessage("Set time out in minutes")
                        .setView(numberPicker)
                        .setPositiveButton("set", (dialog, which) -> {
                            Prefs.setTime(context, Integer.parseInt(minuteValues[numberPicker.getValue() - 1]));
                            executeCallback(timeoutCallback);
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
                .setCancelable(false)
                .show();
    }

    private void executeCallback(Callable<Void> callback) {
        if (callback != null)
            try {
                callback.call();
            } catch (Exception e) {
                Log.e(TAG, "unable to execute callback");
            }
    }
}

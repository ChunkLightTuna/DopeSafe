package com.uniting.android.msic;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.SEND_SMS;
import static android.Manifest.permission.READ_PHONE_STATE;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;

/**
 * Static permission logix.
 */
class Permissions {
    private static final int REQUEST_LOCATION = 1;
    private static final int REQUEST_SMS = 2;
    private static final String TAG = "Permissions";

    static void requestLocation(Activity activity) {
        Log.d(TAG, "Prefs.isLoc: " + Prefs.isLoc(activity) + " locationGranted: " + locationGranted(activity));

        if (Prefs.isLoc(activity) && !locationGranted(activity)) {
            ActivityCompat.requestPermissions(activity, new String[]{ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        }
    }

    static void requestSMS(Activity activity) {
        if (!smsGranted(activity)) {
            ActivityCompat.requestPermissions(activity, new String[]{SEND_SMS, READ_PHONE_STATE}, REQUEST_SMS);
        }
    }

    static boolean locationGranted(Context context) {
        return granted(context, new String[]{ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION});
    }

    static boolean smsGranted(Context context) {
        return granted(context, new String[]{SEND_SMS, READ_PHONE_STATE});
    }

    static AlertDialog handlePermissionResult(Activity activity, int requestCode, @NonNull int[] grantResults) {

        switch (requestCode) {
//            case REQUEST_LOCATION:
//                if (!permissionsGranted(grantResults)) {
//                    if (!activity.shouldShowRequestPermissionRationale(ACCESS_COARSE_LOCATION) || !activity.shouldShowRequestPermissionRationale(ACCESS_FINE_LOCATION)) {
//                        new AlertDialog.Builder(activity)
//                                .setMessage(R.string.location_permissions_dialog_message)
//                                .setPositiveButton(R.string.settings, (dialog, which) -> {
//                                    Intent intent = new Intent();
//                                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
//                                    Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
//                                    intent.setData(uri);
//                                    activity.startActivity(intent);
//                                })
//                                .setNegativeButton(R.string.cancel, (dialog, which) -> {
//                                })
//                                .show();
//                    }
//                }
//                break;

            case REQUEST_SMS:
                if (!permissionsGranted(grantResults)) {
                    AlertDialog.Builder smsDialog = new AlertDialog.Builder(activity);
                    smsDialog.setMessage(R.string.sms_permissions_dialog_message)
                            .setCancelable(false);
                    if (activity.shouldShowRequestPermissionRationale(SEND_SMS) && activity.shouldShowRequestPermissionRationale(READ_PHONE_STATE)) {
                        smsDialog.setPositiveButton(R.string.ok, (dialog, which) -> requestSMS(activity));
                    } else {
                        smsDialog.setPositiveButton(R.string.settings, (dialog, which) -> {
                            Intent intent = new Intent();
                            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
                            intent.setData(uri);
                            activity.startActivity(intent);
                        })
                                .setOnDismissListener(dialog -> requestSMS(activity));
                    }
                    return smsDialog.show();
                }

        }
        return null;
    }

    private static boolean permissionsGranted(int[] grantResults) {
        if (grantResults.length == 0)
            return false;
        else
            for (int result : grantResults)
                if (result != PERMISSION_GRANTED)
                    return false;

        return true;
    }

    private static boolean granted(Context context, String[] permissions) {
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(context, permission) != PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }
}

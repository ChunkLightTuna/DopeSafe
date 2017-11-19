package com.uniting.android.msic;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;

/**
 * Static permission logix.
 */

class Permissions {
    private static final int REQUEST_LOCATION = 1;
    private static final int REQUEST_SMS = 2;
    private static final int GRANTED = PackageManager.PERMISSION_GRANTED;
    private static final String COARSE = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final String FINE = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String SMS = Manifest.permission.SEND_SMS;

    static void requestLocation(Activity activity) {
        ActivityCompat.requestPermissions(activity, new String[]{COARSE, FINE}, REQUEST_LOCATION);
    }

    static void requestSMS(Activity activity) {
        ActivityCompat.requestPermissions(activity, new String[]{SMS}, REQUEST_SMS);
    }

    static boolean locationGranted(Context context) {
        return granted(context, new String[]{COARSE, FINE});
    }

    static boolean smsGranted(Context context) {
        return granted(context, new String[]{SMS});
    }


//    static void noRealyDealWithIt(Activity activity, a)
    static AlertDialog dealWithIt(Activity activity, int requestCode, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_LOCATION:
                if (!permissionsGranted(grantResults)) {
                    AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(activity);
                    dialogBuilder.setTitle(R.string.permissions_dialog_title)
                            .setMessage(R.string.location_permissions_dialog_message)
                            .setPositiveButton(R.string.ok, (dialog, which) -> {
                                //
                            })
                            .setNegativeButton(R.string.settings, (dialog, which) -> {
                                Intent intent = new Intent();
                                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
                                intent.setData(uri);
                                activity.startActivity(intent);
                            });

                    return dialogBuilder.create();
                }
                break;

            case REQUEST_SMS:
                if (!permissionsGranted(grantResults)) {
                    AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(activity);
                    dialogBuilder
                            .setMessage(R.string.sms_permissions_dialog_message)
                            .setCancelable(false);
                    if (activity.shouldShowRequestPermissionRationale(SMS)) {
                        dialogBuilder
                                .setPositiveButton(R.string.ok, (dialog, which) -> requestSMS(activity));
                    } else {
                        dialogBuilder
                                .setPositiveButton(R.string.settings, (dialog, which) -> {
                                    Intent intent = new Intent();
                                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                    Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
                                    intent.setData(uri);
                                    activity.startActivity(intent);
                                })
                                .setOnDismissListener(dialog -> requestSMS(activity));
                    }

                    return dialogBuilder.create();
                }
                break;
        }
        return null;
    }

    private static boolean permissionsGranted(int[] grantResults) {
        if (grantResults.length == 0)
            return false;
        else
            for (int result : grantResults)
                if (result != GRANTED)
                    return false;

        return true;
    }

    private static boolean granted(Context context, String[] permissions) {
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(context, permission) != GRANTED) {
                return false;
            }
        }
        return true;
    }
}

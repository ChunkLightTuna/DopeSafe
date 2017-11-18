package com.uniting.android.msic;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;

/**
 * Static permission logix.
 */

class Permissions {
    static final int REQUEST_ALL_NECESSARY = 1;
    static final int REQUEST_LOCATION = 2;
    static final int REQUEST_SMS = 3;
    private static final int GRANTED = PackageManager.PERMISSION_GRANTED;
    private static final String COARSE = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final String FINE = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String SMS = Manifest.permission.SEND_SMS;


    static void getAll(Activity activity) {
        String[] permissions = new String[]{SMS, COARSE, FINE};

        if (!allPermissionsAreGranted(activity))
            ActivityCompat.requestPermissions(activity, permissions, REQUEST_ALL_NECESSARY);
    }

    static void getLocation(Activity activity) {
        String[] permissions = new String[]{COARSE, FINE};

        if (!locationPermissionsGranted(activity)) {
            ActivityCompat.requestPermissions(activity, permissions, REQUEST_LOCATION);
        }
    }

    private static boolean locationPermissionsGranted(Context context) {
        return ContextCompat.checkSelfPermission(context, COARSE) == GRANTED &&
                ContextCompat.checkSelfPermission(context, FINE) == GRANTED;
    }

    private static boolean allPermissionsAreGranted(Context context) {
        return ContextCompat.checkSelfPermission(context, SMS) == GRANTED &&
                ContextCompat.checkSelfPermission(context, COARSE) == GRANTED &&
                ContextCompat.checkSelfPermission(context, FINE) == GRANTED;
    }

    static AlertDialog.Builder buildDialog(Activity activity) {
        return new AlertDialog.Builder(activity).setMessage(R.string.permissions_dialog_message)
                .setTitle(R.string.permissions_dialog_title)
                .setPositiveButton(R.string.ok, (dialog, which) -> {
                    //
                })
                .setNegativeButton(R.string.settings, ((dialog, which) -> {
                    Intent intent = new Intent();
                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
                    intent.setData(uri);
                    activity.startActivity(intent);
                }))
                .setOnDismissListener(dialog -> Permissions.getAll(activity));
    }

    static boolean permissionsGranted(int[] grantResults) {
        if (grantResults.length == 0)
            return false;
        else
            for (int result : grantResults)
                if (result != GRANTED)
                    return false;

        return true;
    }


}

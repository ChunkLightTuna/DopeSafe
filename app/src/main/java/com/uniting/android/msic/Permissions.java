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
    static final int PERMISSIONS_REQUEST_ALL_NECESSARY = 15423;

    static void getPermissions(Activity activity) {
        String[] permissions = new String[]{Manifest.permission.SEND_SMS,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION};

        if (!allPermissionsAreGranted(activity))
            ActivityCompat.requestPermissions(activity, permissions, PERMISSIONS_REQUEST_ALL_NECESSARY);
    }

    private static boolean allPermissionsAreGranted(Context context) {
        return !(ContextCompat.checkSelfPermission(context,
                Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(context,
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(context,
                        Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED);
    }

    static AlertDialog buildDialog(Activity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        return builder.setMessage(R.string.permissions_dialog_message)
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
                .setOnDismissListener(dialog -> Permissions.getPermissions(activity))
                .create();
    }

    static boolean permissionsGranted(int[] grantResults) {
        if (grantResults.length == 0)
            return false;
        else
            for (int result : grantResults)
                if (result != PackageManager.PERMISSION_GRANTED)
                    return false;

        return true;
    }


}

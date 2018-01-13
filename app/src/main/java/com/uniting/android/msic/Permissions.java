package com.uniting.android.msic;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.SEND_SMS;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;

/**
 * Static permission logix.
 */
class Permissions {
    private static final int REQUEST_LOCATION = 1;
    private static final int REQUEST_SMS = 2;

    static void requestLocation(Activity activity) {
        ActivityCompat.requestPermissions(activity, new String[]{ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
    }

    static void requestSMS(Activity activity) {
        ActivityCompat.requestPermissions(activity, new String[]{SEND_SMS}, REQUEST_SMS);
    }

    static void requestNotificationPolicy(Context context) {
        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && nm != null && !nm.isNotificationPolicyAccessGranted()) {
            new AlertDialog.Builder(context)
                    .setTitle(R.string.DND_alert_title)
                    .setMessage(R.string.DND_alert_message)
                    .setPositiveButton(R.string.ok, (dialog, which) -> context.startActivity(new Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS)))
                    .setNegativeButton(R.string.no, (dialog, which) -> {
                    })
                    .show();
        }
    }

    static boolean locationGranted(Context context) {
        return granted(context, new String[]{ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION});
    }

    static boolean smsGranted(Context context) {
        return granted(context, new String[]{SEND_SMS});
    }

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

                    return dialogBuilder.show();
                }
                break;

            case REQUEST_SMS:
                if (!permissionsGranted(grantResults)) {
                    AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(activity);
                    dialogBuilder
                            .setMessage(R.string.sms_permissions_dialog_message)
                            .setCancelable(false);
                    if (activity.shouldShowRequestPermissionRationale(SEND_SMS)) {
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
                    return dialogBuilder.show();
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

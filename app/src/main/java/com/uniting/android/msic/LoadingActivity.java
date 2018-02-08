package com.uniting.android.msic;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;

import com.uniting.android.msic.stepper.SetupActivity;


public class LoadingActivity extends AppCompatActivity {

    private static final String TAG = "LoadingActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        if (!Prefs.isDisclaimerAccepted(this))
            showDisclaimerDialog();
        else if (!Prefs.isSetupComplete(this))
            launchActivity(SettingsActivity.class);
        else
            showConfirmationDialog();
    }


    private void showDisclaimerDialog() {
        String header = getString(R.string.disclaimer_header)
                .replace("\n", "<br />");
        String conditions = getString(R.string.conditions)
                .replace("\n", "<br />");
        Spanned boldHeader = Html.fromHtml("<b>" + header + "</b>" + conditions);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(boldHeader)
                .setTitle(R.string.disclaimer_dialog_title)
                .setPositiveButton(R.string.disclaimer_dialog_pos_button_text,
                        (dialogInterface, i) -> LoadingActivity.this.handleDisclaimerAccepted())
                .setNegativeButton(R.string.disclaimer_dialog_neg_button_text,
                        (dialogInterface, i) -> LoadingActivity.this.handleDisclaimerDenied())
                .setCancelable(false)
                .create()
                .show();
    }

    private void showConfirmationDialog() {
        ConfirmationDialog confirmationDialog = new ConfirmationDialog(this);
        confirmationDialog
                .setContinueCallback(() -> {
                    launchActivity(MainActivity.class);
                    return null;
                })
                .setEditCallback(() -> {
                    launchActivity(SettingsActivity.class);
                    return null;
                })
                .showDialog();
    }


    private void handleDisclaimerAccepted() {
        Log.d(TAG, "disclaimer accepted");
        Prefs.setDisclaimerAccepted(this, true);
        if (!Prefs.isSetupComplete(this))
            launchActivity(SetupActivity.class);
        else
            showConfirmationDialog();
        //getNextStep();
    }

    private void handleDisclaimerDenied() {
        Log.d(TAG, "disclaimer denied");
        Prefs.setDisclaimerAccepted(this, false);
        finishAndRemoveTask();
    }

    private void launchActivity(Class<? extends Activity> activityClass) {
        Log.d(TAG, "Starting " + activityClass.getCanonicalName());
        Intent intent = new Intent(this, activityClass);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}

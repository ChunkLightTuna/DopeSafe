package com.uniting.android.msic;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;

import java.util.concurrent.Callable;


public class LoadingActivity extends AppCompatActivity {

    private static final String TAG = "LoadingActivity";

    private SharedPreferences prefs;

    private UserPrefs userPrefs;

    private SettingsDialog settingsDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//    setContentView(R.layout.activity_loading);

        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
        prefs = getPreferences(Context.MODE_PRIVATE);
        userPrefs = UserPrefs.getInstance();
        settingsDialog = new SettingsDialog(userPrefs, this);
        checkDisclaimerAcceptance();
    }


    private void checkDisclaimerAcceptance() {
        if (isDisclaimerAccepted())
            startMainActivity();
        else
            showDisclaimerDialog();
    }

    private boolean isDisclaimerAccepted() {
        return prefs.getBoolean("disclaimer_accepted", false);
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
                .create()
                .show();
    }

    private void showSetupMessageDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.setup_dialog_message)
                .setPositiveButton(R.string.cont, (dialogInterface, i) -> getNextStep())
                .setNegativeButton(R.string.exit, (dialogInterface, i) -> handleSetUpDenied())
                .create()
                .show();
    }

    private void getNextStep(){
        if(userPrefs.getPhone() == null) {
            settingsDialog.setEmergencyContactCallback(() -> {
                getNextStep();
                return null;
            });
            settingsDialog.showDialog(R.id.emergency_contact);
        }else if(userPrefs.getTime() == 0){
            settingsDialog.setTimeoutCallback(() -> {
                getNextStep();
                return null;
            });
            settingsDialog.showDialog(R.id.time_out);
        }else if(userPrefs.getMsg() == null) {
            settingsDialog.setMessageCallBack(new Callable<Void>() {
                @Override
                public Void call() throws Exception {
                    getNextStep();
                    return null;
                }
            });
            settingsDialog.showDialog(R.id.message);
        }
        else
            showSetupCompleteDialog();
    }

    private void showSetupCompleteDialog(){
        Log.d(TAG, "showing setup complete dialog");
        handleSetUpCompleted();
    }

    private void handleDisclaimerAccepted() {
        Log.d(TAG, "disclaimer accepted");
        setDisclaimerAccepted(true);
        showSetupMessageDialog();
    }

    private void handleDisclaimerDenied() {
        Log.d(TAG, "disclaimer denied");
        setDisclaimerAccepted(false);
        exitApplication();
    }

    private void handleSetUpDenied(){
        setSetupCompleted(false);
        exitApplication();
    }

    private void handleSetUpCompleted(){
        setSetupCompleted(true);
        startMainActivity();
    }
    private void setDisclaimerAccepted(boolean accepted) {
        prefs.edit().putBoolean("disclaimer_accepted", accepted).apply();
    }

    private void setSetupCompleted(boolean complete){
        prefs.edit().putBoolean("setup_completed", complete).apply();
    }

    private void exitApplication() {
        finishAndRemoveTask();
    }

    private void startMainActivity() {
        Log.d(TAG, "starting main activity");
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

}

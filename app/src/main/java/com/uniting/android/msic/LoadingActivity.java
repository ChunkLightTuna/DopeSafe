package com.uniting.android.msic;

import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;


public class LoadingActivity extends AppCompatActivity {

    private static final String TAG = "LoadingActivity";

    private SettingsDialog settingsDialog;
    private boolean phoneSet;
    private boolean messageSet;
    private boolean timeSet;
    private boolean locationSet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);
        phoneSet = false;
        messageSet = false;
        timeSet = false;
        locationSet = false;
        settingsDialog = new SettingsDialog(this);
        if(!Prefs.isDisclaimerAccepted(this) && !Prefs.isSetupComplete(this))
            checkDisclaimerAcceptance();
        else
            showConfirmationDialog();
    }


    private void checkDisclaimerAcceptance() {
        if(Prefs.isDisclaimerAccepted(this))
            Log.d(TAG, "disclaimer accepted");
        else
            showDisclaimerDialog();
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

    private void showConfirmationDialog(){
       ConfirmationDialog confirmationDialog = new ConfirmationDialog(this);
       confirmationDialog
               .setContinueCallback(() -> {
                   startMainActivity();
                   return null;
               })
               .setEditCallback(() -> {
                   getNextStep();
                   return null;
               })
               .showDialog();
    }

//    private void showSetupMessageDialog(){
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//            builder.setMessage(R.string.setup_dialog_message)
//                .setPositiveButton(R.string.cont, (dialogInterface, i) -> getNextStep())
//                .setNegativeButton(R.string.exit, (dialogInterface, i) -> handleSetUpDenied())
//                .create()
//                .show();
//    }

    private void getNextStep(){
        if(!phoneSet) {
            settingsDialog.setEmergencyContactCallback(() -> {
                phoneSet = true;
                getNextStep();
                return null;
            });
            settingsDialog.showDialog(R.id.emergency_contact_pref);
        }else if(!messageSet) {
            settingsDialog.setMessageCallBack(() -> {
                messageSet = true;
                getNextStep();
                return null;
            });
            settingsDialog.showDialog(R.id.message);
        }else if(!timeSet){
            settingsDialog.setTimeoutCallback(() -> {
                timeSet = true;
                getNextStep();
                return null;
            });
            settingsDialog.showDialog(R.id.time_out);
        }else if(!locationSet){
            settingsDialog.setLocationCallback(() -> {
                locationSet = true;
                getNextStep();
                return null;
            });
            settingsDialog.showDialog(R.id.enable_location_pref);
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
        Prefs.setDisclaimerAccepted(this, true);
        getNextStep();
    }

    private void handleDisclaimerDenied() {
        Log.d(TAG, "disclaimer denied");
        Prefs.setDisclaimerAccepted(this, false);
        exitApplication();
    }

    private void handleSetUpDenied(){
        Prefs.setSetupComplete(this, false);
        exitApplication();
    }

    private void handleSetUpCompleted(){
        Prefs.setSetupComplete(this, true);
        startMainActivity();
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

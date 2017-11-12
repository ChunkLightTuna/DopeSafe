package com.uniting.android.msic;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;



public class LoadingActivity extends AppCompatActivity {

  private static final String TAG = "LoadingActivity";

  private SharedPreferences prefs;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_loading);

    // Upon interacting with UI controls, delay any scheduled hide()
    // operations to prevent the jarring behavior of controls going away
    // while interacting with the UI.
    prefs = getPreferences(Context.MODE_PRIVATE);
    checkDisclaimerAcceptance();
  }


  private void checkDisclaimerAcceptance(){
    if(isDisclaimerAccepted())
      startMainActivity();
    else
      showDisclaimerDialog();
  }

  private boolean isDisclaimerAccepted(){
    return prefs.getBoolean("disclaimer_accepted", false);
  }

  private void showDisclaimerDialog(){
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

  private void handleDisclaimerAccepted(){
    Log.d(TAG, "disclaimer accepted");
    setDisclaimerAccepted(true);
    startMainActivity();
  }

  private void handleDisclaimerDenied(){
    Log.d(TAG, "disclaimer denied");
    setDisclaimerAccepted(false);
    exitApplication();
  }

  private void setDisclaimerAccepted(boolean accepted){
    prefs.edit().putBoolean("disclaimer_accepted", accepted).apply();
  }

  private void exitApplication(){
    finishAndRemoveTask();
  }

  private void startMainActivity(){
    Log.d(TAG, "starting main activity");
    Intent intent = new Intent(this, MainActivity.class);
    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
    startActivity(intent);
  }

}

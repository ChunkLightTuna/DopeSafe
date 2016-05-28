package edu.unh.cs.android.dopesafe;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.NumberPicker;

/**
 * Created by Chris Oelerich on 5/21/16.
 * <p/>
 * Attaches alert dialogs to settings menu
 */
public class SettingsMenu
    implements NavigationView.OnNavigationItemSelectedListener {

  private static final String TAG = "SettingsMenu";
  private MainActivity activity;

  private UserPrefs prefs;

  public SettingsMenu(MainActivity activity, UserPrefs prefs) {
    this.activity = activity;
    this.prefs = prefs;
  }

  @SuppressWarnings("StatementWithEmptyBody")
  @Override
  public boolean onNavigationItemSelected(MenuItem item) {
    Log.d(TAG, "onNavigationItemSelected() called with: " + "item = [" + item + "]");

    // Handle navigation view item clicks here.
    int id = item.getItemId();

    if (id == R.id.get_help) {
      Intent intent = new Intent(Intent.ACTION_VIEW);
      intent.setData(Uri.parse(activity.getResources().getString(R.string.help_website)));
      activity.startActivity(intent);
    } else {

      AlertDialog.Builder builder = new AlertDialog.Builder(activity);

      if (id == R.id.emergency_contact) {

        final EditText editText = new EditText(activity);

        editText.setInputType(InputType.TYPE_CLASS_PHONE);
        editText.setText(prefs.getPhone());

        builder
            .setTitle("Emergency Contact")
            .setView(editText)
            .setPositiveButton("set", new DialogInterface.OnClickListener() {
              public void onClick(DialogInterface dialog, int id) {
                prefs.setPhone(editText.getText().toString());
              }
            });

      } else if (id == R.id.time_out) {

        final NumberPicker numberPicker = new NumberPicker(activity);

        final String[] minuteValues = new String[13];

        minuteValues[0] = "1";

        for (int i = 1; i < minuteValues.length; i++) {
          String number = Integer.toString((i + 1) * 5);
          minuteValues[i] = number.length() < 2 ? "0" + number : number;
        }

        numberPicker.setDisplayedValues(minuteValues);

        numberPicker.setMinValue(1);
        numberPicker.setMaxValue(12);

        numberPicker.setValue(prefs.getTime() / 5);

        builder
            .setTitle("Time Out")
            .setMessage("Set time out in minutes")
            .setView(numberPicker)
            .setPositiveButton("set", new DialogInterface.OnClickListener() {
              public void onClick(DialogInterface dialog, int id) {
                prefs.setTime(Integer.parseInt(minuteValues[numberPicker.getValue() - 1]));
                activity.updateTime();
              }
            });

      } else if (id == R.id.message) {

        final EditText editText = new EditText(activity);

        editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        editText.setText(prefs.getMsg());
        editText.setMinLines(5);
        editText.setOverScrollMode(View.OVER_SCROLL_NEVER);

        builder
            .setTitle("Emergency Contact")
            .setView(editText)
            .setPositiveButton("set", new DialogInterface.OnClickListener() {
              public void onClick(DialogInterface dialog, int id) {
                prefs.setMsg(editText.getText().toString());
              }
            });


      } else if (id == R.id.motion_detection) {

        String current = prefs.isMotion() ? "enabled" : "disabled";
        String action = prefs.isMotion() ? "disable" : "enable";

        builder
            .setTitle("Motion detection currently " + current + ".")
            .setMessage("Would you like to " + action + " it?")
            .setPositiveButton(action, new DialogInterface.OnClickListener() {
              public void onClick(DialogInterface dialog, int id) {
                prefs.setMotion(!prefs.isMotion());
              }
            });
      } else if (id == R.id.location) {

        String current = prefs.isLoc() ? "enabled" : "disabled";
        String action = prefs.isLoc() ? "disable" : "enable";

        builder
            .setTitle("Location currently " + current + ".")
            .setMessage("Would you like to " + action + " it?")
            .setPositiveButton(action, new DialogInterface.OnClickListener() {
              public void onClick(DialogInterface dialog, int id) {
                prefs.setLoc(!prefs.isLoc());
              }
            });
      }
      builder
          .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
              // User cancelled the dialog
            }
          })
          .create()
          .show();
    }

    DrawerLayout drawer = (DrawerLayout) activity.getWindow().getDecorView().findViewById(R.id.drawer_layout);
    drawer.closeDrawer(GravityCompat.START);
    return true;
  }
}

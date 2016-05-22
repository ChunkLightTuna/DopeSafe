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
 */
public class Settings
    implements NavigationView.OnNavigationItemSelectedListener {

  private static final String TAG = "Settings";
  private MainActivity activity;

  //TODO should be pulled out and saved b/w sessions
  private String contact_phone;
  private boolean motion;
  private boolean location;
  private int timeMax;
  private String message;

  public Settings(MainActivity activity) {
    this.activity = activity;
  }


  @SuppressWarnings("StatementWithEmptyBody")
  @Override
  public boolean onNavigationItemSelected(MenuItem item) {
    Log.d(TAG, "onNavigationItemSelected() called with: " + "item = [" + item + "]");

    // Handle navigation view item clicks here.
    int id = item.getItemId();

    if (id == R.id.get_help) {
      String url = "http://www.hopefornhrecovery.org/";
      Intent intent = new Intent(Intent.ACTION_VIEW);
      intent.setData(Uri.parse(url));
      activity.startActivity(intent);

    } else {

      AlertDialog.Builder builder = new AlertDialog.Builder(activity);

      if (id == R.id.emergency_contact) {

        final EditText editText = new EditText(activity);

        editText.setInputType(InputType.TYPE_CLASS_PHONE);
        editText.setText(contact_phone);

        builder
            .setTitle("Emergency Contact")
            .setView(editText)
            .setPositiveButton("set", new DialogInterface.OnClickListener() {
              public void onClick(DialogInterface dialog, int id) {
                contact_phone = editText.getText().toString();
              }
            });

      } else if (id == R.id.time_out) {

        final NumberPicker numberPicker = new NumberPicker(activity);

        final String[] minuteValues = new String[13];

        minuteValues[0] = "1";

        for (int i = 1; i < minuteValues.length; i++) {
          String number = Integer.toString((i+1)*5);
          minuteValues[i] = number.length() < 2 ? "0" + number : number;
        }

        numberPicker.setDisplayedValues(minuteValues);

        numberPicker.setMinValue(1);
        numberPicker.setMaxValue(12);

        numberPicker.setValue(timeMax);

        builder
            .setTitle("Time Out")
            .setMessage("Set time out in minutes")
            .setView(numberPicker)
            .setPositiveButton("set", new DialogInterface.OnClickListener() {
              public void onClick(DialogInterface dialog, int id) {
                timeMax = Integer.parseInt(minuteValues[numberPicker.getValue() - 1]);
                activity.updateTime();
              }
            });

      } else if (id == R.id.message) {

        final EditText editText = new EditText(activity);

        editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        editText.setText(message);
        editText.setMinLines(5);
        editText.setOverScrollMode(View.OVER_SCROLL_NEVER);

        builder
            .setTitle("Emergency Contact")
            .setView(editText)
            .setPositiveButton("set", new DialogInterface.OnClickListener() {
              public void onClick(DialogInterface dialog, int id) {
                contact_phone = editText.getText().toString();
              }
            });


      } else if (id == R.id.motion_detection) {

        String current = motion ? "disabled" : "enabled";
        String action = motion ? "enable" : "disable";

        builder
            .setTitle("Motion detection currently " + current + ".")
            .setMessage("Would you like to " + action + " it?")
            .setPositiveButton(action, new DialogInterface.OnClickListener() {
              public void onClick(DialogInterface dialog, int id) {
                motion = !motion;
                Log.d(TAG, "motion is set  to " + motion);
              }
            });
      } else if (id == R.id.location) {

        String current = location ? "enabled" : "disabled";
        String action = location ? "disable" : "enable";

        builder
            .setTitle("Location currently " + current + ".")
            .setMessage("Would you like to " + action + " it?")
            .setPositiveButton(action, new DialogInterface.OnClickListener() {
              public void onClick(DialogInterface dialog, int id) {
                location = !location;
                Log.d(TAG, "motion is set  to " + location);
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


  public String getPhone() {
    return contact_phone;
  }

  public void setContact_phone(String contact_phone) {
    this.contact_phone = contact_phone;
  }

  public boolean isMotion() {
    return motion;
  }

  public void setMotion(boolean motion) {
    this.motion = motion;
  }

  public int getTimeMax() {
    return timeMax;
  }

  public void setTimeMax(int timeMax) {
    this.timeMax = timeMax;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public boolean isLocation() {
    return location;
  }

  public void setLocation(boolean location) {
    this.location = location;
  }
}

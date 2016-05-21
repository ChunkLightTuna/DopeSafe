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
  private int timeout;
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

        final EditText input = new EditText(activity);

        input.setInputType(InputType.TYPE_CLASS_PHONE);
        input.setText(contact_phone);

        builder
            .setTitle("Emergency Contact")
            .setView(input)
            .setPositiveButton("set", new DialogInterface.OnClickListener() {
              public void onClick(DialogInterface dialog, int id) {
                contact_phone = input.getText().toString();
              }
            });

      } else if (id == R.id.time_out) {

        final NumberPicker input = new NumberPicker(activity);
        input.setMinValue(1);
        input.setMaxValue(60);
        input.setValue(timeout);

        builder
            .setTitle("Time Out")
            .setMessage("Set time out in minutes")
            .setView(input)
            .setPositiveButton("set", new DialogInterface.OnClickListener() {
              public void onClick(DialogInterface dialog, int id) {
                timeout = input.getValue();
                activity.updateTime();
              }
            });

      } else if (id == R.id.message) {

        final EditText input = new EditText(activity);

        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        input.setText(message);
        input.setMinLines(5);
        input.setOverScrollMode(View.OVER_SCROLL_NEVER);

        builder
            .setTitle("Emergency Contact")
            .setView(input)
            .setPositiveButton("set", new DialogInterface.OnClickListener() {
              public void onClick(DialogInterface dialog, int id) {
                contact_phone = input.getText().toString();
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
            })

            .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
              public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
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


  public String getContact_phone() {
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

  public int getTimeout() {
    return timeout;
  }

  public void setTimeout(int timeout) {
    this.timeout = timeout;
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
    this.location= location;
  }
}

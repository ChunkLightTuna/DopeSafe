package edu.unh.cs.android.dopesafe;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.MenuItem;

/**
 * Created by Chris Oelerich on 5/21/16.
 */
public class Settings
    implements NavigationView.OnNavigationItemSelectedListener {

  private static final String TAG = "Settings";
  private Activity activity;

  //TODO should be pulled out and saved b/w sessions
  private String contact_name;
  private String contact_phone;
  private boolean motion;
  private int timeout;

  public Settings(Activity activity) {
    this.activity = activity;


    //defaults for now
    contact_name = "Ali";
    contact_phone = activity.getResources().getString(R.string.ali);
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

        builder
            .setMessage("A")
            .setTitle("B")
            .setPositiveButton("enable", new DialogInterface.OnClickListener() {
              public void onClick(DialogInterface dialog, int id) {
                //toggle
              }
            })
            .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
              public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
              }
            });

      } else if (id == R.id.time_out) {
        builder
            .setTitle("Time out length")
            .setPositiveButton("Set", new DialogInterface.OnClickListener() {
              public void onClick(DialogInterface dialog, int id) {
                Log.d(TAG, "motion is set  to " + motion);
              }
            });
      } else if (id == R.id.message) {


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
}

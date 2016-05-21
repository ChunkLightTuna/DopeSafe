package edu.unh.cs.android.dopesafe;

import android.app.Activity;
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

  public Settings(Activity activity) {
    this.activity = activity;
  }



  @SuppressWarnings("StatementWithEmptyBody")
  @Override
  public boolean onNavigationItemSelected(MenuItem item) {
    Log.d(TAG, "onNavigationItemSelected() called with: " + "item = [" + item + "]");

    // Handle navigation view item clicks here.
    int id = item.getItemId();

    if (id == R.id.emergency_contact) {
      Log.d(TAG, "onNavigationItemSelected() called with: " + "id = [emergency contact] ");

      AlertDialog.Builder builder = new AlertDialog.Builder(activity);

      builder.setMessage("A")
          .setTitle("B")
          .create();

    } else if (id == R.id.time_out) {
      Log.d(TAG, "onNavigationItemSelected() called with: " + "id = [time out] ");
    } else if (id == R.id.message) {
      Log.d(TAG, "onNavigationItemSelected() called with: " + "id = [message] ");
    } else if (id == R.id.get_help) {
      Log.d(TAG, "onNavigationItemSelected() called with: " + "id = [get help] ");
    } else if (id == R.id.motion_detection) {
      Log.d(TAG, "onNavigationItemSelected() called with: " + "id = [motion detection] ");
//    } else if (id == R.id.nav_send) {
    }

    DrawerLayout drawer = (DrawerLayout) activity.getWindow().getDecorView().findViewById(R.id.drawer_layout);
    drawer.closeDrawer(GravityCompat.START);
    return true;
  }
}

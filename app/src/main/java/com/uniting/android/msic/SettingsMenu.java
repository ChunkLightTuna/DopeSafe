package com.uniting.android.msic;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
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

import java.util.concurrent.Callable;

/**
 * Created by Chris Oelerich on 5/21/16.
 * <p/>
 * Attaches alert dialogs to settings menu
 */
class SettingsMenu implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "SettingsMenu";
    private MainActivity activity;
    private UserPrefs prefs;
    private SettingsDialog settingsDialog;

    SettingsMenu(MainActivity activity, UserPrefs prefs) {
        this.activity = activity;
        this.prefs = prefs;
        this.settingsDialog = new SettingsDialog(prefs, activity);
        settingsDialog.setTimeoutCallback(() -> {
            activity.updateTime();
            return null;
        });
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Log.d(TAG, "onNavigationItemSelected() called with: " + "item = [" + item + "]");

        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.get_help) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(activity.getResources().getString(R.string.help_website)));
            activity.startActivity(intent);
        } else if (id == R.id.settings) {
            Intent intent = new Intent(activity, SettingsActivity.class);
            activity.startActivity(intent);
        } else {
            settingsDialog.showDialog(id);
        }

        DrawerLayout drawer = activity.getWindow().getDecorView().findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}

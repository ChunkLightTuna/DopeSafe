package com.uniting.android.msic;

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

/**
 * Created by Chris Oelerich on 5/21/16.
 * <p/>
 * Attaches alert dialogs to settings menu
 */
class SettingsMenu implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "SettingsMenu";
    private MainActivity activity;

    private UserPrefs prefs;

    SettingsMenu(MainActivity activity, UserPrefs prefs) {
        this.activity = activity;
        this.prefs = prefs;
    }

    //    @SuppressWarnings("StatementWithEmptyBody")
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

            AlertDialog.Builder builder = new AlertDialog.Builder(activity);

            switch (id) {
                case R.id.emergency_contact: {

                    final EditText editText = new EditText(activity);

                    editText.setInputType(InputType.TYPE_CLASS_PHONE);
                    editText.setText(prefs.getPhone());

                    builder
                            .setTitle("Emergency Contact")
                            .setView(editText)
                            .setPositiveButton("set", (dialog, id16) -> prefs.setPhone(editText.getText().toString()));

                    break;
                }
                case R.id.time_out:

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
                            .setPositiveButton("set", (dialog, id15) -> {
                                prefs.setTime(Integer.parseInt(minuteValues[numberPicker.getValue() - 1]));
                                activity.updateTime();
                            });

                    break;
                case R.id.message: {

                    final EditText editText = new EditText(activity);

                    editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
                    editText.setText(prefs.getMsg());
                    editText.setMinLines(5);
                    editText.setOverScrollMode(View.OVER_SCROLL_NEVER);

                    builder
                            .setTitle("Emergency Contact")
                            .setView(editText)
                            .setPositiveButton("set", (dialog, id14) -> prefs.setMsg(editText.getText().toString()));


                    break;
                }
                case R.id.motion_detection: {

                    String current = prefs.isMotion() ? "enabled" : "disabled";
                    String action = prefs.isMotion() ? "disable" : "enable";

                    builder
                            .setTitle("Motion detection currently " + current + ".")
                            .setMessage("Would you like to " + action + " it?")
                            .setPositiveButton(action, (dialog, id13) -> prefs.setMotion(!prefs.isMotion()));
                    break;
                }
                case R.id.location: {

                    String current = prefs.isLoc() ? "enabled" : "disabled";
                    String action = prefs.isLoc() ? "disable" : "enable";

                    builder
                            .setTitle("Location currently " + current + ".")
                            .setMessage("Would you like to " + action + " it?")
                            .setPositiveButton(action, (dialog, id12) -> prefs.setLoc(!prefs.isLoc()));
                    break;
                }
            }
            builder
                    .setNegativeButton("cancel", (dialog, id1) -> {
                        // User cancelled the dialog
                    })
                    .create()
                    .show();
        }

        DrawerLayout drawer = activity.getWindow().getDecorView().findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}

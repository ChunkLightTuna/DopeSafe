package com.uniting.android.msic;

import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;

public class SettingsActivity extends AppCompatActivity {
    private static final String TAG = "SettingsActivity";

    /**
     * A preference value change listener that updates the preference's summary
     * to reflect its new value.
     */
    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = (preference, value) -> {
        String stringValue = value.toString();


        if (preference instanceof ListPreference) {
            // For list preferences, look up the correct display value in
            // the preference's 'entries' list.
            ListPreference listPreference = (ListPreference) preference;
            int index = listPreference.findIndexOfValue(stringValue);

            // Set the summary to reflect the new value.
            preference.setSummary(
                    index >= 0
                            ? listPreference.getEntries()[index]
                            : null);

        } else {
            // For all other preferences, set the summary to the value's
            // simple string representation.
            preference.setSummary(stringValue);
        }
        return true;
    };


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Binds a preference's summary to its value. More specifically, when the
     * preference's value is changed, its summary (line of text below the
     * preference title) is updated to reflect the value. The summary is also
     * immediately updated upon calling this method. The exact display format is
     * dependent on the type of preference.
     *
     * @see #sBindPreferenceSummaryToValueListener
     */
    private static void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        // Trigger the listener immediately with the preference's
        // current value.
        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }


        getFragmentManager().beginTransaction().replace(android.R.id.content, new PrefsFrag()).commit();
    }

    @Override
    protected void onResume() {
        Log.w(TAG, "onResume");

        super.onResume();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        Permissions.handlePermissionResult(this, requestCode, grantResults);
    }


    public static class PrefsFrag extends PreferenceFragment {
        PreferenceScreen preferenceScreen;
        SwitchPreference locationPref;
        boolean brb;

        @Override
        public void onResume() {
            super.onResume();

            if (brb) {
                if (Permissions.locationGranted(getContext())) {
                    locationPref.setChecked(Prefs.isLoc(getContext()));
                } else {
                    Prefs.setLoc(getContext(), false);
                    locationPref.setChecked(false);
                }
                brb = false;
            }
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            addPreferencesFromResource(com.uniting.android.msic.R.xml.prefs);
            preferenceScreen = getPreferenceScreen();
            brb = false;

            locationPref = ((SwitchPreference) preferenceScreen.findPreference(getContext().getString(R.string.enable_location_key)));

            if (!Permissions.locationGranted(getContext()) && (locationPref.isChecked() || Prefs.isLoc(getContext()))) {
                Prefs.setLoc(getContext(), false);
                locationPref.setChecked(false);
            }

            locationPref.setOnPreferenceChangeListener((preference, newValue) -> {
                if ((boolean) newValue && !Permissions.locationGranted(getContext())) {
                    brb = true;
                    Prefs.setLoc(getContext(), true);
                    Permissions.requestLocation(getActivity());

                }
                Log.d(TAG, "consistent? " + (Prefs.isLoc(getContext()) == Permissions.locationGranted(getContext())));
                return true;
            });

            preferenceScreen.addPreference(SmsMessagePreference.newInstance(
                    getContext(),
                    getString(R.string.pref_title_emergency_message),
                    getString(R.string.pref_default_emergency_message),
                    getString(R.string.emergency_message_key))
            );

            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
            bindPreferenceSummaryToValue(findPreference(getString(R.string.emergency_message_key)));
            bindPreferenceSummaryToValue(findPreference(getString(R.string.emergency_contact_key)));
            bindPreferenceSummaryToValue(findPreference(getString(R.string.emergency_contact_aux_key)));
        }
    }
}

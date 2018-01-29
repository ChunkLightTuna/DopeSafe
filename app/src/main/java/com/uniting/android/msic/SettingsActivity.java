package com.uniting.android.msic;

import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceScreen;
import android.support.v7.app.ActionBar;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

public class SettingsActivity extends AppCompatActivity {
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

        getFragmentManager().beginTransaction().replace(android.R.id.content,
                new PrefsFrag()).commit();
    }

    public static class PrefsFrag extends PreferenceFragment {
        private static final String TAG = "PrefsFrag";

        PreferenceScreen preferenceScreen;
        Preference notificationPref;

        @Override
        public void onResume() {
            super.onResume();

            //remove option to enable notification permission on return from notification permission menu
            NotificationManager nm = (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N &&
                    nm != null &&
                    nm.isNotificationPolicyAccessGranted() &&
                    preferenceScreen != null &&
                    notificationPref != null
                    )
                preferenceScreen.removePreference(notificationPref);
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            addPreferencesFromResource(com.uniting.android.msic.R.xml.prefs);
            preferenceScreen = getPreferenceScreen();

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
//            bindPreferenceSummaryToValue(findPreference("enable_location"));

            /*
                        if (id == R.id.enable_location_pref) {

                if(!Permissions.locationGranted(getContext())) {
                    Permissions.requestLocation(getActivity());
                    SharedPreferences preferences = getActivity().getPreferences(Context.MODE_PRIVATE);
                }

                return true;
            }
            */

        }
    }
}

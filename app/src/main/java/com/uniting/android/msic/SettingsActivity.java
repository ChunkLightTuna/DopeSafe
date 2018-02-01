package com.uniting.android.msic;

import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.telephony.PhoneNumberUtils;
import android.util.Log;
import android.view.MenuItem;

public class SettingsActivity extends AppCompatActivity {
    private static final String TAG = "SettingsActivity";


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
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

            locationPref = ((SwitchPreference) findPreference(getString(R.string.enable_location_key)));
            Preference timePref = findPreference(getString(R.string.countdown_time_key));
            Preference phoneOnePref = findPreference(getString(R.string.emergency_contact_key));
            Preference phoneTwoPref = findPreference(getString(R.string.emergency_contact_aux_key));
            SmsMessagePreference msgPref = SmsMessagePreference.newInstance(
                    getContext(),
                    getString(R.string.pref_title_emergency_message),
                    getString(R.string.pref_default_emergency_message),
                    getString(R.string.emergency_message_key));

            preferenceScreen.addPreference(msgPref);

            if (!Permissions.locationGranted(getContext()) && (locationPref.isChecked() || Prefs.isLoc(getContext()))) {
                Prefs.setLoc(getContext(), false);
                locationPref.setChecked(false);
            }

            locationPref.setOnPreferenceChangeListener((p, v) -> {
                if ((boolean) v && !Permissions.locationGranted(getContext())) {
                    brb = true;
                    Prefs.setLoc(getContext(), true);
                    Permissions.requestLocation(getActivity());
                }
                return true;
            });

            Preference.OnPreferenceChangeListener phoneNumberListener = (p, number) -> {
                boolean globalPhoneNumber = PhoneNumberUtils.isGlobalPhoneNumber((String) number);
                if (globalPhoneNumber) p.setSummary(number.toString());
                return globalPhoneNumber;
            };

            Preference.OnPreferenceChangeListener timeListener = (p, v) -> {
                ListPreference listPreference = (ListPreference) p;

                int index = listPreference.findIndexOfValue(v.toString());

                p.setSummary(index >= 0 ? listPreference.getEntries()[index] : null);
                return true;
            };

            Preference.OnPreferenceChangeListener msgListener = (p, message) -> {
                boolean b = !((String) message).isEmpty();
                if (b) p.setSummary(message.toString());
                return b;
            };

            phoneOnePref.setOnPreferenceChangeListener(phoneNumberListener);
            phoneTwoPref.setOnPreferenceChangeListener(phoneNumberListener);
            timePref.setOnPreferenceChangeListener(timeListener);
            msgPref.setOnPreferenceChangeListener(msgListener);

            phoneNumberListener.onPreferenceChange(phoneOnePref, Prefs.getPhone(getContext()));
            phoneNumberListener.onPreferenceChange(phoneTwoPref, Prefs.getPhoneAux(getContext()));
            timeListener.onPreferenceChange(timePref, Prefs.getTime(getContext()));
            msgListener.onPreferenceChange(msgPref, Prefs.getMsg(getContext()));
        }
    }
}
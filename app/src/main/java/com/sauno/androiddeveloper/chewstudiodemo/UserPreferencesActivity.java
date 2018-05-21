package com.sauno.androiddeveloper.chewstudiodemo;

import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuItem;

public class UserPreferencesActivity extends AppCompatPreferenceActivity {

    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
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

            } /*else if (preference instanceof RingtonePreference) {
                // For ringtone preferences, look up the correct display value
                // using RingtoneManager.
                if (TextUtils.isEmpty(stringValue)) {
                    // Empty values correspond to 'silent' (no ringtone).
                    preference.setSummary(R.string.pref_ringtone_silent);

                } else {
                    Ringtone ringtone = RingtoneManager.getRingtone(
                            preference.getContext(), Uri.parse(stringValue));

                    if (ringtone == null) {
                        // Clear the summary if there was a lookup error.
                        preference.setSummary(null);
                    } else {
                        // Set the summary to reflect the new ringtone display
                        // name.
                        String name = ringtone.getTitle(preference.getContext());
                        preference.setSummary(name);
                    }
                }

            }*/ else {
                // For all other preferences, set the summary to the value's
                // simple string representation.
                preference.setSummary(stringValue);
            }
            return true;
        }
    };


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
        addPreferencesFromResource(R.xml.pref_user);

        setupActionBar();

        bindPreferenceSummaryToValue(findPreference("userNamePref"));
        bindPreferenceSummaryToValue(findPreference("userSexPref"));
        bindPreferenceSummaryToValue(findPreference("userAgePref"));
        bindPreferenceSummaryToValue(findPreference("userStaturePref"));
        bindPreferenceSummaryToValue(findPreference("userWeightPref"));
        bindPreferenceSummaryToValue(findPreference("userBloodGroupPref"));
        bindPreferenceSummaryToValue(findPreference("userMethodOfNutritionPref"));
        bindPreferenceSummaryToValue(findPreference("userTypeOfFoodPref"));
        bindPreferenceSummaryToValue(findPreference("userCompatibilityTypeOfFoodPref"));
        bindPreferenceSummaryToValue(findPreference("userCharacteristicOnePref"));
        bindPreferenceSummaryToValue(findPreference("userCharacteristicTwoPref"));
        bindPreferenceSummaryToValue(findPreference("userCharacteristicThreePref"));
        bindPreferenceSummaryToValue(findPreference("userCharacteristicFourPref"));
        bindPreferenceSummaryToValue(findPreference("userCharacteristicFivePref"));
        bindPreferenceSummaryToValue(findPreference("userLifestylePref"));
        bindPreferenceSummaryToValue(findPreference("userWouldYouLikePref"));






       /* EditTextPreference userNameTextPreference = findPreference("userNamePref");
        SharedPreferences getWeightAndAgeStore = getSharedPreferences("weightAndAgeStorage", Context.MODE_PRIVATE);*/

    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("Настройки пользователя");
            actionBar.setElevation(0);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_host, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
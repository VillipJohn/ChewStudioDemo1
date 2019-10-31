package com.sauno.androiddeveloper.chewstudiodemo;

import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class UserPreferencesActivity extends AppCompatPreferenceActivity {
    public static final String KEY_USER_NAME_PREF = "userNamePref";
    public static final String KEY_USER_SEX_PREF = "userSexPref";
    public static final String KEY_USER_AGE_PREF = "userAgePref";
    public static final String KEY_USER_HEIGHT_PREF = "userHeightPref";
    public static final String KEY_USER_WEIGHT_PREF = "userWeightPref";
    public static final String KEY_USER_BLOOD_GROUPE_PREF = "userBloodGroupPref";
    public static final String KEY_USER_METHOD_OF_NUTRITION_PREF = "userMethodOfNutritionPref";
    //public static final String KEY_USER_TYPE_OF_FOOD_PREF = "userTypeOfFoodPref";
    public static final String KEY_USER_TYPE_OF_FOOD_VEGETARIAN_PREF = "userTypeOfFoodVegetarianPref";
    public static final String KEY_USER_TYPE_OF_FOOD_LENTEN_PREF = "userTypeOfFoodLentenPref";
    public static final String KEY_USER_COMPATIBILITY_TYPE_OF_FOOD_PREF = "userCompatibilityTypeOfFoodPref";
    public static final String KEY_USER_CHARACTERISTIC_ONE_PREF = "userCharacteristicOnePref";
    public static final String KEY_USER_CHARACTERISTIC_TWO_PREF = "userCharacteristicTwoPref";
    public static final String KEY_USER_CHARACTERISTIC_THREE_PREF = "userCharacteristicThreePref";
    public static final String KEY_USER_CHARACTERISTIC_FOUR_PREF = "userCharacteristicFourPref";
    public static final String KEY_USER_CHARACTERISTIC_FIVE_PREF = "userCharacteristicFivePref";
    public static final String KEY_USER_LIFESTYLE_PREF = "userLifestylePref";
    public static final String KEY_USER_WOULD_YOU_LIKE_PREF = "userWouldYouLikePref";
    public static final String KEY_CALORIE_CHECK_BOX_PREF = "calorieCheckBoxPref";
    public static final String KEY_PROTEIN_CHECK_BOX_PREF = "proteinCheckBoxPref";
    public static final String KEY_FAT_CHECK_BOX_PREF = "fatCheckBoxPref";
    public static final String KEY_CARBOHYDRATE_CHECK_BOX_PREF = "carbohydrateCheckBoxPref";
    public static final String KEY_XE_CHECK_BOX_PREF = "xeCheckBoxPref";
    public static final String KEY_VITAMINS_CHECK_BOX_PREF = "vitaminsCheckBoxPref";
    public static final String KEY_MINERALS_CHECK_BOX_PREF = "mineralsCheckBoxPref";

    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();

            Log.d("MyLogUserPreferences", "value - " + stringValue);



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
        }
    };


    private static void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        // Trigger the listener immediately with the preference's
        // current value.
        if (preference instanceof SwitchPreference || preference instanceof CheckBoxPreference) {
            sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                    PreferenceManager
                            .getDefaultSharedPreferences(preference.getContext())
                            .getBoolean(preference.getKey(), true));
        } else {
            sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                    PreferenceManager
                            .getDefaultSharedPreferences(preference.getContext())
                            .getString(preference.getKey(), ""));
        }
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_user);

/*        bindService(new Intent(this, BluetoothLeService.class),                                     // Привязаться к сервису BluetoothLeService
                mLeConnection, Context.BIND_AUTO_CREATE);*/

        setupActionBar();

        bindPreferenceSummaryToValue(findPreference(KEY_USER_NAME_PREF));
        bindPreferenceSummaryToValue(findPreference(KEY_USER_SEX_PREF));
        bindPreferenceSummaryToValue(findPreference(KEY_USER_AGE_PREF));
        bindPreferenceSummaryToValue(findPreference(KEY_USER_HEIGHT_PREF));
        bindPreferenceSummaryToValue(findPreference(KEY_USER_WEIGHT_PREF));
        bindPreferenceSummaryToValue(findPreference(KEY_USER_BLOOD_GROUPE_PREF));
        bindPreferenceSummaryToValue(findPreference(KEY_USER_METHOD_OF_NUTRITION_PREF));
        bindPreferenceSummaryToValue(findPreference(KEY_USER_TYPE_OF_FOOD_VEGETARIAN_PREF));
        bindPreferenceSummaryToValue(findPreference(KEY_USER_TYPE_OF_FOOD_LENTEN_PREF));

        //bindPreferenceSummaryToValue(findPreference(KEY_USER_TYPE_OF_FOOD_PREF));
        bindPreferenceSummaryToValue(findPreference(KEY_USER_COMPATIBILITY_TYPE_OF_FOOD_PREF));
        bindPreferenceSummaryToValue(findPreference(KEY_USER_CHARACTERISTIC_ONE_PREF));
        bindPreferenceSummaryToValue(findPreference(KEY_USER_CHARACTERISTIC_TWO_PREF));
        bindPreferenceSummaryToValue(findPreference(KEY_USER_CHARACTERISTIC_THREE_PREF));
        bindPreferenceSummaryToValue(findPreference(KEY_USER_CHARACTERISTIC_FOUR_PREF));
        bindPreferenceSummaryToValue(findPreference(KEY_USER_CHARACTERISTIC_FIVE_PREF));
        bindPreferenceSummaryToValue(findPreference(KEY_USER_LIFESTYLE_PREF));
        bindPreferenceSummaryToValue(findPreference(KEY_USER_WOULD_YOU_LIKE_PREF));
       /* bindPreferenceSummaryToValue(findPreference(KEY_CALORIE_CHECK_BOX_PREF));
        bindPreferenceSummaryToValue(findPreference(KEY_PROTEIN_CHECK_BOX_PREF));
        bindPreferenceSummaryToValue(findPreference(KEY_FAT_CHECK_BOX_PREF));
        bindPreferenceSummaryToValue(findPreference(KEY_CARBOHYDRATE_CHECK_BOX_PREF));*/
       /* bindPreferenceSummaryToValue(findPreference(KEY_VITAMINS_CHECK_BOX_PREF));
        bindPreferenceSummaryToValue(findPreference(KEY_MINERALS_CHECK_BOX_PREF));*/



       //CheckBoxPreference calorieCheckBoxPref = (CheckBoxPreference) findPreference(KEY_CALORIE_CHECK_BOX_PREF);


        CheckBoxPreference vitaminsCheckBoxPref = (CheckBoxPreference) findPreference(KEY_VITAMINS_CHECK_BOX_PREF);
        vitaminsCheckBoxPref.setEnabled(false);

        CheckBoxPreference mineralsCheckBoxPref = (CheckBoxPreference) findPreference(KEY_MINERALS_CHECK_BOX_PREF);
        mineralsCheckBoxPref.setEnabled(false);



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
        getMenuInflater().inflate(R.menu.menu_preferences_activity, menu);


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
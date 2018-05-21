package com.sauno.androiddeveloper.chewstudiodemo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;

public class UserProfileActivity extends AppCompatActivity {

    TextView userNameTextView;
    TextView userAgeTextView;
    TextView userHeightTextView;
    TextView userWeightTextView;
    TextView imtTextView;
    TextView imtResultTextView;
    TextView countCaloriesInProfileTextView;
    TextView countProteinsProfileTextView;
    TextView countFatsProfileTextView;
    TextView countCarbohydratesProfileTextView;


    String userName;
    String userAge;
    String userHeight;
    String userWeight;
    String userSex;
    String userLifestyle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        setupActionBar();

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        userName = sp.getString("userNamePref", "");
        userAge = sp.getString("userAgePref", "");
        userHeight = sp.getString("userStaturePref", "");
        userWeight = sp.getString("userWeightPref", "");
        userSex = sp.getString("userSexPref", "");

        userNameTextView = findViewById(R.id.nameTextView);
        userAgeTextView = findViewById(R.id.ageTextView);
        userHeightTextView = findViewById(R.id.heightTextView);
        userWeightTextView = findViewById(R.id.weightTextView);
        imtTextView = findViewById(R.id.imtTextView);
        imtResultTextView = findViewById(R.id.imtResultTextView);
        countCaloriesInProfileTextView = findViewById(R.id.countCaloriesInProfileTextView);
        countProteinsProfileTextView = findViewById(R.id.countProteinsProfileTextView);
        countFatsProfileTextView = findViewById(R.id.countFatsProfileTextView);
        countCarbohydratesProfileTextView = findViewById(R.id.countCarbohydratesProfileTextView);


        Button editButton = findViewById(R.id.editButton);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserProfileActivity.this, UserPreferencesActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        userName = sp.getString("userNamePref", "");
        userAge = sp.getString("userAgePref", "");
        userHeight = sp.getString("userStaturePref", "");
        userWeight = sp.getString("userWeightPref", "");
        userSex = sp.getString("userSexPref", "");
        userLifestyle = sp.getString("userLifestylePref", "");

        if(!userName.equals("-1") && !userName.equals("")) {
            userNameTextView.setText("Имя - " + userName);
        }
        if(!userAge.equals("-1") && !userAge.equals("")) {
            userAgeTextView.setText("Возраст - " + userAge);
        }
        if(!userHeight.equals("-1") && !userHeight.equals("")) {
            userHeightTextView.setText("Рост - " + userHeight + "м");
        }
        if(!userWeight.equals("-1") && !userWeight.equals("")) {
            userWeightTextView.setText("Вес - " + userWeight + "кг");
        }

        setImt(userHeight, userWeight);

        setNormaKBJU(userWeight, userHeight, userAge, userSex, userLifestyle);

    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setTitle("Анкета пользователя");
            actionBar.setDisplayHomeAsUpEnabled(true);
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

    private void setImt(String height, String weight) {
        if(!height.equals("-1") && !weight.equals("-1") && !height.equals("") && !weight.equals("")) {
            float imt;


            imt = Float.parseFloat(weight) / (Float.parseFloat(height) * Float.parseFloat(height));

            String pattern = "##0.00";
            DecimalFormat decimalFormat = new DecimalFormat(pattern);
            String formatImt = decimalFormat.format(imt);

            String imtString = "Индекс массы тела - " + formatImt;

            imtTextView.setText(imtString);

            if (imt >= 40) {
                imtResultTextView.setText(" III ст. ожирения");
            } else if ((imt<40)&&(imt>=35)) {
                imtResultTextView.setText(" II ст. ожирения");
            } else if((imt < 35) && (imt >= 30)) {
                imtResultTextView.setText(" I ст. ожирения");
            } else if ((imt < 30) && (imt >=25)) {
                imtResultTextView.setText(" Избыточная масса тела");
            } else if ((imt < 25) && (imt >=18.5)) {
                imtResultTextView.setText(" Норма");
            } else if ((imt < 18.5) && (imt >= 16)) {
                imtResultTextView.setText(" Дифицит массы тела");
            } else if (imt < 16) {
                imtResultTextView.setText(" Выраженный дифицит массы тела");
            }
        }

    }

    private void setNormaKBJU(String weight, String height, String age, String sex, String lifestyle) {
        SharedPreferences mSharedPreferences = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mSharedPreferences.edit();



        //Log.d("MyLogUserProfile", "ageNumberString = " + ageNumber);

        if(sex.equals("Женщина") && !height.equals("-1") && !weight.equals("-1") && !height.equals("") && !weight.equals("")
                && !lifestyle.equals("-1") && !lifestyle.equals("")){
            String ageNumber = age.substring(0,2);

            double bmr = 655.1 + (9.6 * Double.parseDouble(weight)) + (1.85 * Double.parseDouble(height) * 100) - (4.68 * Double.parseDouble(ageNumber));
            int calories = (int)(bmr * Float.parseFloat(lifestyle));
            countCaloriesInProfileTextView.setText("" + calories);

            int proteins = (int)(calories * 0.25 / 4);
            int fats = (int)(calories * 0.15 / 9);
            int carb = (int)(calories * 0.60 / 4);

            countProteinsProfileTextView.setText("" + proteins + "г");
            countFatsProfileTextView.setText("" + fats + "г");
            countCarbohydratesProfileTextView.setText("" + carb + "г");

            editor.putInt("countCalories", calories);
            editor.putInt("countProteins", proteins);
            editor.putInt("countFats", fats);
            editor.putInt("countCarbohydrates", carb);

        } else if(sex.equals("Мужчина") && !height.equals("-1") && !weight.equals("-1") && !height.equals("") && !weight.equals("")
                && !lifestyle.equals("-1") && !lifestyle.equals("")) {
            String ageNumber = age.substring(0,2);

            double bmr = 66.47 + (13.75 * Double.parseDouble(weight)) + (5.0 * Double.parseDouble(height) * 100) - (6.74 * Double.parseDouble(ageNumber));
            int calories = (int)(bmr * Float.parseFloat(lifestyle));
            countCaloriesInProfileTextView.setText("" + calories);

            int proteins = (int)(calories * 0.25 / 4);
            int fats = (int)(calories * 0.15 / 9);
            int carb = (int)(calories * 0.60 / 4);

            countProteinsProfileTextView.setText("" + proteins + "г");
            countFatsProfileTextView.setText("" + fats + "г");
            countCarbohydratesProfileTextView.setText("" + carb + "г");

            editor.putInt("countCalories", calories);
            editor.putInt("countProteins", proteins);
            editor.putInt("countFats", fats);
            editor.putInt("countCarbohydrates", carb);

        } else {
            Toast.makeText(this, "Для отображения всех данных введите: пол, возраст, рост, вес, образ жизни", Toast.LENGTH_LONG).show();
        }
        editor.apply();
    }
}

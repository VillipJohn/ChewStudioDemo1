package com.sauno.androiddeveloper.chewstudiodemo;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class SelectDesiredMealActivity extends AppCompatActivity {
    TextView countCaloriesInSelectDesiredMealTextView;
    TextView countProteinsInSelectDesiredMealTextView;
    TextView countFatsInSelectDesiredMealTextView;
    TextView countCarbohydratesInSelectDesiredMealTextView;

    int calories;
    int proteins;
    int fats;
    int carbohydrates;

    int threeMealsCalories;
    int threeMealsProteins;
    int threeMealsFats;
    int threeMealsCarbohydrates;

    int fiveMealsCalories;
    int fiveMealsProteins;
    int fiveMealsFats;
    int fiveMealsCarbohydrates;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_desired_meal);

        final SharedPreferences mSharedPreferences = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        Boolean isCheckedSwitch = mSharedPreferences.getBoolean("threeOrFiveMeals", false);

        countCaloriesInSelectDesiredMealTextView = findViewById(R.id.countCaloriesInSelectDesiredMealTextView);
        countProteinsInSelectDesiredMealTextView = findViewById(R.id.countProteinsInSelectDesiredMealTextView);
        countFatsInSelectDesiredMealTextView = findViewById(R.id.countFatsInSelectDesiredMealTextView);
        countCarbohydratesInSelectDesiredMealTextView = findViewById(R.id.countCarbohydratesInSelectDesiredMealTextView);

        calories = threeMealsCalories = fiveMealsCalories = mSharedPreferences.getInt("countCalories", 0);
        proteins = threeMealsProteins = fiveMealsProteins = mSharedPreferences.getInt("countProteins", 0);
        fats = threeMealsFats = fiveMealsFats = mSharedPreferences.getInt("countFats", 0);
        carbohydrates = threeMealsCarbohydrates = fiveMealsCarbohydrates = mSharedPreferences.getInt("countCarbohydrates", 0);

        if(calories == 0) {
            Toast.makeText(this, "Для отображения всех данных заполните в Вашей анкете: пол, возраст, рост, вес, образ жизни", Toast.LENGTH_LONG).show();
        }

        countCaloriesInSelectDesiredMealTextView.setText("" + calories);
        countProteinsInSelectDesiredMealTextView.setText("" + proteins + "г");
        countFatsInSelectDesiredMealTextView.setText("" + fats + "г");
        countCarbohydratesInSelectDesiredMealTextView.setText("" + carbohydrates + "г");

        Switch threeOrFiveSwitch = findViewById(R.id.threeOrFiveSwitch);
        final RadioGroup fiveMealsDay = findViewById(R.id.fiveMealsDay);
        final RadioGroup threeMealsDay = findViewById(R.id.threeMealsDay);

        if(isCheckedSwitch) {
            fiveMealsDay.setVisibility(View.VISIBLE);
            threeMealsDay.setVisibility(View.GONE);
            threeOrFiveSwitch.setChecked(true);
        } else {
            fiveMealsDay.setVisibility(View.GONE);
            threeMealsDay.setVisibility(View.VISIBLE);
        }

        threeOrFiveSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                SharedPreferences.Editor editor = mSharedPreferences.edit();
                editor.putBoolean("threeOrFiveMeals", isChecked);
                editor.apply();
                if (isChecked) {
                    fiveMealsDay.setVisibility(View.VISIBLE);
                    threeMealsDay.setVisibility(View.GONE);

                    countCaloriesInSelectDesiredMealTextView.setText("" + fiveMealsCalories);
                    countProteinsInSelectDesiredMealTextView.setText("" + fiveMealsProteins + "г");
                    countFatsInSelectDesiredMealTextView.setText("" + fiveMealsFats + "г");
                    countCarbohydratesInSelectDesiredMealTextView.setText("" + fiveMealsCarbohydrates + "г");
                } else {
                    fiveMealsDay.setVisibility(View.GONE);
                    threeMealsDay.setVisibility(View.VISIBLE);

                    countCaloriesInSelectDesiredMealTextView.setText("" + threeMealsCalories);
                    countProteinsInSelectDesiredMealTextView.setText("" + threeMealsProteins + "г");
                    countFatsInSelectDesiredMealTextView.setText("" + threeMealsFats + "г");
                    countCarbohydratesInSelectDesiredMealTextView.setText("" + threeMealsCarbohydrates + "г");
                }


            }
        });



        setupActionBar();
    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setTitle("Желаемый приём пищи");
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

    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        int partOfCalories = 0;
        int partOfProteins = 0;
        int partOfFats = 0;
        int partOfCarbohydrates = 0;

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.firstInFiveRadioButton:
                if (checked) {
                    partOfCalories = fiveMealsCalories = (int)(calories * 0.25);
                    partOfProteins = fiveMealsProteins = (int)(proteins * 0.25);
                    partOfFats = fiveMealsFats = (int)(fats * 0.25);
                    partOfCarbohydrates = fiveMealsCarbohydrates = (int)(carbohydrates * 0.25);
                }
                    break;
            case R.id.secondInFiveRadioButton:
                if (checked) {
                    partOfCalories = fiveMealsCalories = (int)(calories * 0.05);
                    partOfProteins = fiveMealsProteins = (int)(proteins * 0.05);
                    partOfFats = fiveMealsFats = (int)(fats * 0.05);
                    partOfCarbohydrates = fiveMealsCarbohydrates = (int)(carbohydrates * 0.05);
                }
                    break;
            case R.id.thirdInFiveRadioButton:
                if (checked) {
                    partOfCalories = fiveMealsCalories = (int)(calories * 0.3);
                    partOfProteins = fiveMealsProteins = (int)(proteins * 0.3);
                    partOfFats = fiveMealsFats = (int)(fats * 0.3);
                    partOfCarbohydrates = fiveMealsCarbohydrates = (int)(carbohydrates * 0.3);
                }
                    break;
            case R.id.fourthInFiveRadioButton:
                if (checked) {
                    partOfCalories = fiveMealsCalories = (int)(calories * 0.05);
                    partOfProteins = fiveMealsProteins = (int)(proteins * 0.05);
                    partOfFats = fiveMealsFats = (int)(fats * 0.05);
                    partOfCarbohydrates = fiveMealsCarbohydrates = (int)(carbohydrates * 0.05);
                }
                    break;
            case R.id.fifthInFiveRadioButton:
                if (checked) {
                    partOfCalories = fiveMealsCalories = (int)(calories * 0.25);
                    partOfProteins = fiveMealsProteins = (int)(proteins * 0.25);
                    partOfFats = fiveMealsFats = (int)(fats * 0.25);
                    partOfCarbohydrates = fiveMealsCarbohydrates = (int)(carbohydrates * 0.25);
                }
                    break;
            case R.id.sixthInFiveRadioButton:
                if (checked) {
                    partOfCalories = fiveMealsCalories = (int)(calories * 0.1);
                    partOfProteins = fiveMealsProteins = (int)(proteins * 0.1);
                    partOfFats = fiveMealsFats = (int)(fats * 0.1);
                    partOfCarbohydrates = fiveMealsCarbohydrates = (int)(carbohydrates * 0.1);
                }
                    break;
            case R.id.firstInThreeRadioButton:
                if (checked) {
                    partOfCalories = threeMealsCalories = (int)(calories * 0.35);
                    partOfProteins = threeMealsProteins = (int)(proteins * 0.35);
                    partOfFats = threeMealsFats = (int)(fats * 0.35);
                    partOfCarbohydrates = threeMealsCarbohydrates = (int)(carbohydrates * 0.35);
                }
                    break;
            case R.id.secondInThreeRadioButton:
                if (checked) {
                    partOfCalories = threeMealsCalories = (int)(calories * 0.35);
                    partOfProteins = threeMealsProteins = (int)(proteins * 0.35);
                    partOfFats = threeMealsFats = (int)(fats * 0.35);
                    partOfCarbohydrates = threeMealsCarbohydrates = (int)(carbohydrates * 0.35);
                }
                    break;
            case R.id.thirdInThreeRadioButton:
                if (checked) {
                    partOfCalories = threeMealsCalories = (int)(calories * 0.3);
                    partOfProteins = threeMealsProteins = (int)(proteins * 0.3);
                    partOfFats = threeMealsFats = (int)(fats * 0.3);
                    partOfCarbohydrates = threeMealsCarbohydrates = (int)(carbohydrates * 0.3);
                }
                    break;
        }
        countCaloriesInSelectDesiredMealTextView.setText("" + partOfCalories);
        countProteinsInSelectDesiredMealTextView.setText("" + partOfProteins + "г");
        countFatsInSelectDesiredMealTextView.setText("" + partOfFats + "г");
        countCarbohydratesInSelectDesiredMealTextView.setText("" + partOfCarbohydrates + "г");

    }

}

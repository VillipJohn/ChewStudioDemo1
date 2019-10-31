package com.sauno.androiddeveloper.chewstudiodemo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sauno.androiddeveloper.chewstudiodemo.data.DatabaseCreateHelper;
import com.sauno.androiddeveloper.chewstudiodemo.data.DishDBHelper;
import com.sauno.androiddeveloper.chewstudiodemo.data.EatenDishDBHelper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class StatisticCPFCXeActivity extends AppCompatActivity {
    String meal;

    int sumCaloriesInt = 0;
    int sumProteinsInt = 0;
    int sumFatsInt = 0;
    int sumCarbsInt = 0;
    int sumXeInt = 0;

    int currentUserPartOfCalories;
    int currentUserPartOfProteins;
    int currentUserPartOfFats;
    int currentUserPartOfCarbohydrates;
    int currentUserPartOfXE;

    private LinearLayout containerCPFCXLinearLayout;

    private TextView countCaloriesTextView;
    private TextView countProteinsTextView;
    private TextView countFatsTextView;
    private TextView countCarbohydratesTextView;
    private TextView countXETextView;

    private TextView normCaloriesTextView;
    private TextView normProteinsTextView;
    private TextView normFatsTextView;
    private TextView normCarbsTextView;
    private TextView normXETextView;

    private TextView aboveCaloriesTextView;
    private TextView aboveProteinsTextView;
    private TextView aboveFatsTextView;
    private TextView aboveCarbohydratesTextView;
    private TextView aboveXETextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistic_cpfcxe);

        TextView mealTextView = findViewById(R.id.mealTextView);

        containerCPFCXLinearLayout = findViewById(R.id.containerCPFCXLinearLayout);

        countCaloriesTextView = findViewById(R.id.countCaloriesTextView);
        countProteinsTextView = findViewById(R.id.countProteinsTextView);
        countFatsTextView = findViewById(R.id.countFatsTextView);
        countCarbohydratesTextView = findViewById(R.id.countCarbohydratesTextView);
        countXETextView = findViewById(R.id.countXETextView);

        normCaloriesTextView = findViewById(R.id.normCaloriesTextView);
        normProteinsTextView = findViewById(R.id.normProteinsTextView);
        normFatsTextView = findViewById(R.id.normFatsTextView);
        normCarbsTextView = findViewById(R.id.normCarbsTextView);
        normXETextView = findViewById(R.id.normXETextView);

        aboveCaloriesTextView = findViewById(R.id.aboveCaloriesTextView);
        aboveProteinsTextView = findViewById(R.id.aboveProteinsTextView);
        aboveFatsTextView = findViewById(R.id.aboveFatsTextView);
        aboveCarbohydratesTextView = findViewById(R.id.aboveCarbohydratesTextView);
        aboveXETextView = findViewById(R.id.aboveXETextView);


        Intent intent = getIntent();
        meal = intent.getStringExtra("meal");

        mealTextView.setText(meal);

        setupActionBar();

        getEatenDishes();
    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setTitle("КБЖУХ прима пищи");
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

    private void getEatenDishes() {
        Calendar calendar = Calendar.getInstance();
        //int hour = calendar.get(Calendar.HOUR);
        //int am = calendar.get(Calendar.AM_PM);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH) + 1;


        DatabaseCreateHelper databaseCreateHelper = new DatabaseCreateHelper(getApplicationContext());
        SQLiteDatabase db;
        try {
            db = databaseCreateHelper.getWritableDatabase();
        }
        catch (SQLiteException ex){
            db = databaseCreateHelper.getReadableDatabase();
        }

        String[] projection = {
                EatenDishDBHelper.COLUMN_ID_DISH,
                EatenDishDBHelper.COLUMN_HOUR,
                EatenDishDBHelper.COLUMN_MONTH,
                EatenDishDBHelper.COLUMN_QUANTITY
        };

        String selection = EatenDishDBHelper.COLUMN_DAY + " = ?" + " AND " + EatenDishDBHelper.COLUMN_MONTH + " = ?";
        String dayString = "" + day;
        String monthString = "" + month;
        String[] selectionArgs = {dayString, monthString};

        Cursor cursor = db.query(
                true,
                EatenDishDBHelper.TABLE,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null,
                null
        );

        List<Integer> idList = new ArrayList<>();
        List<Integer> hourList = new ArrayList<>();
        List<Float> quantityList = new ArrayList<>();

        if (cursor.moveToFirst()) {

            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(EatenDishDBHelper.COLUMN_ID_DISH));
                idList.add(id);

                int time =  cursor.getInt(cursor.getColumnIndexOrThrow(EatenDishDBHelper.COLUMN_HOUR));
                hourList.add(time);

                float quantity = cursor.getFloat(cursor.getColumnIndexOrThrow(EatenDishDBHelper.COLUMN_QUANTITY));
                quantityList.add(quantity);

                Log.d("MyLogStatistic", "id - " + id);

            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        for(int n = 0; n < idList.size(); n++) {
            int hour = hourList.get(n);
            String mealString = "";
            if(hour >= 6 && hour < 11) {
                mealString = "Завтрак";
            } else if(hour >= 11 && hour < 17) {
                mealString = "Обед";
            } else if(hour >= 17 && hour < 23) {
                mealString = "Ужин";
            }

            if(meal.equals(mealString)) {
                setData(idList.get(n), quantityList.get(n));
            }
        }

        Log.d("MyLogStatistic", "sumCalories - " + sumCaloriesInt);

        getCurrentUserCPFC();
        setAboveLimitColorForCPFC();
    }

    private void setData(int id, float quantity) {
        DatabaseCreateHelper databaseCreateHelper = new DatabaseCreateHelper(getApplicationContext());
        SQLiteDatabase db;
        try {
            db = databaseCreateHelper.getWritableDatabase();
        }
        catch (SQLiteException ex){
            db = databaseCreateHelper.getReadableDatabase();
        }

        String[] projection = {
                DishDBHelper.COLUMN_CALORIC,
                DishDBHelper.COLUMN_PROTEINS,
                DishDBHelper.COLUMN_FATS,
                DishDBHelper.COLUMN_CARBS,
                DishDBHelper.COLUMN_XE
        };

        String selection = DishDBHelper.COLUMN_ID + " = ?";
        String idString = "" + id;
        String[] selectionArgs = {idString};

        Cursor cursor = db.query(
                true,
                DishDBHelper.TABLE,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null,
                null
        );

        if (cursor.moveToFirst()) {

            do {
                int calories = cursor.getInt(cursor.getColumnIndexOrThrow(DishDBHelper.COLUMN_CALORIC));
                int proteins = cursor.getInt(cursor.getColumnIndexOrThrow(DishDBHelper.COLUMN_PROTEINS));
                int fats = cursor.getInt(cursor.getColumnIndexOrThrow(DishDBHelper.COLUMN_FATS));
                int carbs = cursor.getInt(cursor.getColumnIndexOrThrow(DishDBHelper.COLUMN_CARBS));
                int xe = cursor.getInt(cursor.getColumnIndexOrThrow(DishDBHelper.COLUMN_XE));

                calories = (int)(calories * quantity);
                proteins = (int)(proteins * quantity);
                fats = (int)(fats * quantity);
                carbs = (int)(carbs * quantity);
                xe = (int)(xe * quantity);

                sumCaloriesInt = sumCaloriesInt + calories;
                sumProteinsInt = sumProteinsInt + proteins;
                sumFatsInt = sumFatsInt + fats;
                sumCarbsInt = sumCarbsInt + carbs;
                sumXeInt = sumXeInt + xe;
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
    }

    //реализация отображения выхода за лимиты данных КБЖУХ
    private void setAboveLimitColorForCPFC() {
        boolean isAboveCalories, isAboveProteins, isAboveFats, isAboveCarbs, isAboveXE;
        if(currentUserPartOfCalories < sumCaloriesInt) {
            countCaloriesTextView.setText("" + sumCaloriesInt);
            normCaloriesTextView.setText("" + currentUserPartOfCalories);
            aboveCaloriesTextView.setText("+" + (sumCaloriesInt - currentUserPartOfCalories));
            isAboveCalories = true;
        } else {
            normCaloriesTextView.setText("" + currentUserPartOfCalories);
            aboveCaloriesTextView.setText("");
            isAboveCalories = false;
        }

        if(currentUserPartOfProteins < sumProteinsInt) {
            countProteinsTextView.setText("" + sumProteinsInt + "г");
            normProteinsTextView.setText("" + currentUserPartOfProteins + "г");
            aboveProteinsTextView.setText("+" + (sumProteinsInt - currentUserPartOfProteins) + "г");
            isAboveProteins = true;
        } else {
            aboveProteinsTextView.setText("");
            normProteinsTextView.setText("" + currentUserPartOfProteins + "г");
            isAboveProteins = false;
        }

        if(currentUserPartOfFats < sumFatsInt) {
            countFatsTextView.setText("" + sumFatsInt + "г");
            normFatsTextView.setText("" + currentUserPartOfFats + "г");
            aboveFatsTextView.setText("+" + (sumFatsInt - currentUserPartOfFats) + "г");
            isAboveFats = true;
        } else {
            aboveFatsTextView.setText("");
            normFatsTextView.setText("" + currentUserPartOfFats + "г");
            isAboveFats = false;
        }

        if(currentUserPartOfCarbohydrates < sumCarbsInt) {
            countCarbohydratesTextView.setText("" + sumCarbsInt + "г");
            normCarbsTextView.setText("" + currentUserPartOfCarbohydrates + "г");
            aboveCarbohydratesTextView.setText("+" + (sumCarbsInt - currentUserPartOfCarbohydrates) + "г");
            isAboveCarbs = true;
        } else {
            aboveCarbohydratesTextView.setText("");
            normCarbsTextView.setText("" + currentUserPartOfCarbohydrates + "г");
            isAboveCarbs = false;
        }

        if(sumXeInt > currentUserPartOfXE) {
            countXETextView.setText("" + sumXeInt);
            normXETextView.setText("" + currentUserPartOfXE);
            aboveXETextView.setText("+" + (sumXeInt - currentUserPartOfXE));
            isAboveXE = true;
        } else {
            aboveXETextView.setText("");
            normXETextView.setText("" + currentUserPartOfXE);
            isAboveXE = false;
        }

        if(isAboveCalories || isAboveProteins || isAboveFats || isAboveCarbs || isAboveXE) {
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams)containerCPFCXLinearLayout.getLayoutParams();
            layoutParams.height = dpToPx(80);
            containerCPFCXLinearLayout.setLayoutParams(layoutParams);
        } else {
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams)containerCPFCXLinearLayout.getLayoutParams();
            layoutParams.height = dpToPx(60);
            containerCPFCXLinearLayout.setLayoutParams(layoutParams);
        }


    }

    //перевод dp в пиксели
    private int dpToPx(int dp) {
        float density = getResources()
                .getDisplayMetrics()
                .density;
        return Math.round((float) dp * density);
    }

    //получение текущих данных приёма пищи КБЖУХ
    private void getCurrentUserCPFC() {
        SharedPreferences mSharedPreferences = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);

        int calories = mSharedPreferences.getInt(UserProfileActivity.KEY_COUNT_CALORIES, 0);
        int proteins = mSharedPreferences.getInt(UserProfileActivity.KEY_COUNT_PROTEINS, 0);
        int fats = mSharedPreferences.getInt(UserProfileActivity.KEY_COUNT_FATS, 0);
        int carbohydrates = mSharedPreferences.getInt(UserProfileActivity.KEY_COUNT_CARBOHYDRATES, 0);
        int xe = mSharedPreferences.getInt(UserProfileActivity.KEY_COUNT_XE, 0);

        int percent = 0;

        if(meal.equals("Завтрак")) {
            percent = 35;
        }
        if(meal.equals("Обед")) {
            percent = 35;
        }
        if(meal.equals("Ужин")) {
            percent = 30;
        }

        currentUserPartOfCalories = (int)((float)(calories * percent) / 100);
        currentUserPartOfProteins = (int)((float)(proteins * percent) / 100);
        currentUserPartOfFats = (int)((float)(fats * percent) / 100);
        currentUserPartOfCarbohydrates = (int)((float)(carbohydrates * percent) / 100);
        currentUserPartOfXE = (int)((float)(xe * percent) / 100);
    }
}

package com.sauno.androiddeveloper.chewstudiodemo;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.sauno.androiddeveloper.chewstudiodemo.adapter.MyAdapter;
import com.sauno.androiddeveloper.chewstudiodemo.data.DatabaseCreateHelper;
import com.sauno.androiddeveloper.chewstudiodemo.data.DishDBHelper;

import java.util.ArrayList;
import java.util.List;

public class ChoiceOfDishesActivity extends AppCompatActivity {
    SQLiteDatabase db;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private String[] myDataset;

    Button mOkButton;

    TextView mCountCalories;
    TextView mCountProteins;
    TextView mCountFats;
    TextView mCountCarbs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choice_of_dishes);

        setupActionBar();

        int restaurant = getIntent().getIntExtra("restaurant", 0);
        Log.d("MyLogChoiceOfDishes", "Ресторан - " + restaurant);

        getCategoriesFromDB(restaurant);

        mCountCalories = findViewById(R.id.countCaloriesTextView);
        mCountProteins = findViewById(R.id.countProteinsTextView);
        mCountFats = findViewById(R.id.countFatsTextView);
        mCountCarbs = findViewById(R.id.countCarbohydratesTextView);

        mOkButton = findViewById(R.id.okButton);

        mOkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setNull();
            }
        });

        mRecyclerView = findViewById(R.id.my_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        mAdapter = new MyAdapter(myDataset);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences mSharedPreferences = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);

        int countChosenDishesSP = mSharedPreferences.getInt("CountChosenDishes", 0);
        float countCaloriesSP = mSharedPreferences.getFloat("CountCalories", 0f);
        float countProteinsSP = mSharedPreferences.getFloat("CountProteins", 0f);
        float countFatsSP = mSharedPreferences.getFloat("CountFats", 0f);
        float countCarbohydratesSP = mSharedPreferences.getFloat("CountCarbohydrates", 0f);

        String countChosenDishesString = "Готово" + "(" + countChosenDishesSP + ")";
        String countCaloriesString = "" + countCaloriesSP;
        String countProteinsString = "" + countProteinsSP;
        String countFatsString = "" + countFatsSP;
        String countCarbsString = "" + countCarbohydratesSP;

        mOkButton.setText(countChosenDishesString);
        mCountCalories.setText(countCaloriesString);
        mCountProteins.setText(countProteinsString);
        mCountFats.setText(countFatsString);
        mCountCarbs.setText(countCarbsString);

        Log.i("Choice", countChosenDishesString);
    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setTitle("Выбор блюд");
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

    private void getCategoriesFromDB(int restaurant) {
        DatabaseCreateHelper databaseCreateHelper = new DatabaseCreateHelper(getApplicationContext());
        //SQLiteDatabase db;
        try {
            db = databaseCreateHelper.getWritableDatabase();
        }
        catch (SQLiteException ex){
            db = databaseCreateHelper.getReadableDatabase();
        }

        String[] projection = {
                DishDBHelper.COLUMN_CATEGORY
        };

        String selection = DishDBHelper.COLUMN_RESTAURANT + " = ?";
        String restaurantString = "" + restaurant;
        String[] selectionArgs = {restaurantString};

        Cursor cursor = db.query(
                true,
                DishDBHelper.TABLE,
                projection,
                selection,
                selectionArgs,
                DishDBHelper.COLUMN_CATEGORY,
                null,
                null,
                null
        );

        List<String> categories = new ArrayList<>();

        if (cursor.moveToFirst()) {

            do {
                String category = cursor.getString(cursor.getColumnIndexOrThrow(DishDBHelper.COLUMN_CATEGORY));
                categories.add(category);
                //Log.i("MyLog",category + "\n");

            }while (cursor.moveToNext());

        }

        myDataset = categories.toArray(new String[categories.size()]);

        cursor.close();
    }

    private void setNull() {
        SharedPreferences mSharedPreferences = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = mSharedPreferences.edit();

        editor.putInt("CountChosenDishes", 0);
        editor.putFloat("CountCalories", 0f);
        editor.putFloat("CountProteins", 0f);
        editor.putFloat("CountFats", 0);
        editor.putFloat("CountCarbohydrates", 0);

        editor.apply();

        String countChosenDishesString = "Готово" + "(" + 0 + ")";
        String countCaloriesString = "" + 0.0;
        String countProteinsString = "" + 0.0;
        String countFatsString = "" + 0.0;
        String countCarbsString = "" + 0.0;

        mOkButton.setText(countChosenDishesString);
        mCountCalories.setText(countCaloriesString);
        mCountProteins.setText(countProteinsString);
        mCountFats.setText(countFatsString);
        mCountCarbs.setText(countCarbsString);

    }

    @Override
    public void onDestroy(){
        db.close();
        super.onDestroy();
        // Закрываем подключение и курсор
        //db.close();
        //userCursor.close();
    }
}

package com.sauno.androiddeveloper.chewstudiodemo;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.sauno.androiddeveloper.chewstudiodemo.data.DatabaseCreateHelper;
import com.sauno.androiddeveloper.chewstudiodemo.data.DishDBHelper;
import com.sauno.androiddeveloper.chewstudiodemo.data.EatenDishDBHelper;
import com.sauno.androiddeveloper.chewstudiodemo.data.RestaurantDBHelper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class StatisticHistoryActivity extends AppCompatActivity {
    TextView breakfastTextView;
    TextView lunchTextView;
    TextView dinnerTextView;

    TextView bd1TextView;
    TextView bd2TextView;
    TextView bd3TextView;
    TextView bd4TextView;
    TextView bd5TextView;
    TextView bd6TextView;
    TextView ld1TextView;
    TextView ld2TextView;
    TextView ld3TextView;
    TextView ld4TextView;
    TextView ld5TextView;
    TextView ld6TextView;
    TextView dd1TextView;
    TextView dd2TextView;
    TextView dd3TextView;
    TextView dd4TextView;
    TextView dd5TextView;
    TextView dd6TextView;

    int bd1Id = 0;
    int bd2Id = 0;
    int bd3Id = 0;
    int bd4Id = 0;
    int bd5Id = 0;
    int bd6Id = 0;
    int ld1Id = 0;
    int ld2Id = 0;
    int ld3Id = 0;
    int ld4Id = 0;
    int ld5Id = 0;
    int ld6Id = 0;
    int dd1Id = 0;
    int dd2Id = 0;
    int dd3Id = 0;
    int dd4Id = 0;
    int dd5Id = 0;
    int dd6Id = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistic_history);

        breakfastTextView = findViewById(R.id.breakfastTextView);
        lunchTextView = findViewById(R.id.lunchTextView);
        dinnerTextView = findViewById(R.id.dinnerTextView);

        bd1TextView = findViewById(R.id.bd1TextView);
        bd2TextView = findViewById(R.id.bd2TextView);
        bd3TextView = findViewById(R.id.bd3TextView);
        bd4TextView = findViewById(R.id.bd4TextView);
        bd5TextView = findViewById(R.id.bd5TextView);
        bd6TextView = findViewById(R.id.bd6TextView);
        ld1TextView = findViewById(R.id.ld1TextView);
        ld2TextView = findViewById(R.id.ld2TextView);
        ld3TextView = findViewById(R.id.ld3TextView);
        ld4TextView = findViewById(R.id.ld4TextView);
        ld5TextView = findViewById(R.id.ld5TextView);
        ld6TextView = findViewById(R.id.ld6TextView);
        dd1TextView = findViewById(R.id.dd1TextView);
        dd2TextView = findViewById(R.id.dd2TextView);
        dd3TextView = findViewById(R.id.dd3TextView);
        dd4TextView = findViewById(R.id.dd4TextView);
        dd5TextView = findViewById(R.id.dd5TextView);
        dd6TextView = findViewById(R.id.dd6TextView);

        breakfastTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startStatisticCPFCXe("Завтрак");
            }
        });

        lunchTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startStatisticCPFCXe("Обед");
            }
        });

        dinnerTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startStatisticCPFCXe("Ужин");
            }
        });

        bd1TextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startAboutDish(bd1TextView.getText().toString(), bd1Id);
            }
        });

        bd2TextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startAboutDish(bd2TextView.getText().toString(), bd2Id);
            }
        });

        bd3TextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startAboutDish(bd3TextView.getText().toString(), bd3Id);
            }
        });

        bd4TextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startAboutDish(bd4TextView.getText().toString(), bd4Id);
            }
        });

        bd5TextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startAboutDish(bd5TextView.getText().toString(), bd5Id);
            }
        });

        bd6TextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startAboutDish(bd6TextView.getText().toString(), bd6Id);
            }
        });

        ld1TextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startAboutDish(ld1TextView.getText().toString(), ld1Id);
            }
        });

        ld2TextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startAboutDish(ld2TextView.getText().toString(), ld2Id);
            }
        });

        ld3TextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startAboutDish(ld3TextView.getText().toString(), ld3Id);
            }
        });

        ld4TextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startAboutDish(ld4TextView.getText().toString(), ld4Id);
            }
        });

        ld5TextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startAboutDish(ld5TextView.getText().toString(), ld5Id);
            }
        });

        ld6TextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startAboutDish(ld6TextView.getText().toString(), ld6Id);
            }
        });

        dd1TextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startAboutDish(dd1TextView.getText().toString(), dd1Id);
            }
        });

        dd2TextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startAboutDish(dd2TextView.getText().toString(), dd2Id);
            }
        });

        dd3TextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startAboutDish(dd3TextView.getText().toString(), dd3Id);
            }
        });

        dd4TextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startAboutDish(dd4TextView.getText().toString(), dd4Id);
            }
        });

        dd5TextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startAboutDish(dd5TextView.getText().toString(), dd5Id);
            }
        });

        dd6TextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startAboutDish(dd6TextView.getText().toString(), dd6Id);
            }
        });


        setupActionBar();

        getEatenDishes();
    }

    private void startStatisticCPFCXe(String meal) {
        Intent intent = new Intent(this, StatisticCPFCXeActivity.class);
        intent.putExtra("meal", meal);
        startActivity(intent);
    }

    private void startAboutDish(String dishName, int idDish) {
        if(!dishName.equals("")) {
            Intent intent = new Intent(this, AboutDishStatisticActivity.class);
            intent.putExtra("dishName", dishName);
            intent.putExtra("idDish", idDish);
            startActivity(intent);
        }
    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setTitle("Съедено");
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
        int hour = calendar.get(Calendar.HOUR);
        int am = calendar.get(Calendar.AM_PM);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH) + 1;

        if(am == 1) {
            hour = hour + 12;
        }

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
                EatenDishDBHelper.COLUMN_QUANTITY
        };

        String selection = EatenDishDBHelper.COLUMN_DAY + " = ?";
        String dayString = "" + day;
        String[] selectionArgs = {dayString};

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

                Log.d("MyLogStatistic", "quantity - " + quantity);

            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        for(int n = 0; n < idList.size(); n++) {
            setData(idList.get(n), hourList.get(n), quantityList.get(n));
        }
    }

    private void setData(int id, int hour, float quantity) {
        DatabaseCreateHelper databaseCreateHelper = new DatabaseCreateHelper(getApplicationContext());
        SQLiteDatabase db;
        try {
            db = databaseCreateHelper.getWritableDatabase();
        }
        catch (SQLiteException ex){
            db = databaseCreateHelper.getReadableDatabase();
        }

        String[] projection = {
                DishDBHelper.COLUMN_DESCRIPTION,
                DishDBHelper.COLUMN_RESTAURANT
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

        String name = "";
        int restaurant = 0;

        if (cursor.moveToFirst()) {

            do {
                name = cursor.getString(cursor.getColumnIndexOrThrow(DishDBHelper.COLUMN_DESCRIPTION));

                restaurant = cursor.getInt(cursor.getColumnIndexOrThrow(DishDBHelper.COLUMN_RESTAURANT));

                Log.d("MyLogStatistic", "restaurant  - " + restaurant);

            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        getRestaurantAndSetData(id, hour, quantity, name, restaurant);


    }


    private void getRestaurantAndSetData(int id, int hour, float quantity, String name, int restaurant) {
        DatabaseCreateHelper databaseCreateHelper = new DatabaseCreateHelper(getApplicationContext());
        SQLiteDatabase db;
        try {
            db = databaseCreateHelper.getWritableDatabase();
        }
        catch (SQLiteException ex){
            db = databaseCreateHelper.getReadableDatabase();
        }

        String[] projection = {
                RestaurantDBHelper.COLUMN_NAME
        };

        String selection = RestaurantDBHelper.COLUMN_ID + " = ?";
        String idRestaurant = "" + restaurant;
        String[] selectionArgs = {idRestaurant};

        Cursor cursor = db.query(
                true,
                RestaurantDBHelper.TABLE,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null,
                null
        );

        String restaurantString = "";

        if (cursor.moveToFirst()) {
            do {
                restaurantString = cursor.getString(cursor.getColumnIndexOrThrow(RestaurantDBHelper.COLUMN_NAME));

            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        String check = "";

        String quantityText = getQuantityText(quantity);

        String resultText = name + "(" + restaurantString + ") - " + quantityText;

        if (hour >= 6 && hour < 11) {
            if (check.equals(bd1TextView.getText())) {
                bd1TextView.setText(resultText);
                bd1Id = id;
            } else if (check.equals(bd2TextView.getText())) {
                bd2TextView.setText(resultText);
                bd2Id = id;
            } else if (check.equals(bd3TextView.getText())) {
                bd3TextView.setText(resultText);
                bd3Id = id;
            } else if (check.equals(bd4TextView.getText())) {
                bd4TextView.setText(resultText);
                bd4Id = id;
            } else if (check.equals(bd5TextView.getText())) {
                bd5TextView.setText(resultText);
                bd5Id = id;
            } else if (check.equals(bd6TextView.getText())) {
                bd6TextView.setText(resultText);
                bd6Id = id;
            }

        } else if (hour >= 11 && hour < 17) {
            if (check.equals(ld1TextView.getText())) {
                ld1TextView.setText(resultText);
                ld1Id = id;
            } else if (check.equals(ld2TextView.getText())) {
                ld2TextView.setText(resultText);
                ld2Id = id;
            } else if (check.equals(ld3TextView.getText())) {
                ld3TextView.setText(resultText);
                ld3Id = id;
            } else if (check.equals(ld4TextView.getText())) {
                ld4TextView.setText(resultText);
                ld4Id = id;
            } else if (check.equals(ld5TextView.getText())) {
                ld5TextView.setText(resultText);
                ld5Id = id;
            } else if (check.equals(ld6TextView.getText())) {
                ld6TextView.setText(resultText);
                ld6Id = id;
            }

        } else if (hour >= 17 && hour < 21) {
            if (check.equals(dd1TextView.getText())) {
                dd1TextView.setText(resultText);
                dd1Id = id;
            } else if (check.equals(dd2TextView.getText())) {
                dd2TextView.setText(resultText);
                dd2Id = id;
            } else if (check.equals(dd3TextView.getText())) {
                dd3TextView.setText(resultText);
                dd3Id = id;
            } else if (check.equals(dd4TextView.getText())) {
                dd4TextView.setText(resultText);
                dd4Id = id;
            } else if (check.equals(dd5TextView.getText())) {
                dd5TextView.setText(resultText);
                dd5Id = id;
            } else if (check.equals(dd6TextView.getText())) {
                dd6TextView.setText(resultText);
                dd6Id = id;
            }

        }
    }

    private String getQuantityText(float quantity) {
        String result = "";

        if(quantity == 0.33f) {
            result = "1/3";
        }

        if(quantity == 0.5f) {
            result = "1/2";
        }

        if(quantity == 0.66f) {
            result = "2/3";
        }

        if(quantity >= 1.0) {
            result = "" + (int)quantity;
        }

        return result;
    }
}

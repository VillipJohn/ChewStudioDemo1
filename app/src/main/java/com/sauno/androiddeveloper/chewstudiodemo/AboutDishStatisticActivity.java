package com.sauno.androiddeveloper.chewstudiodemo;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.sauno.androiddeveloper.chewstudiodemo.data.DatabaseCreateHelper;
import com.sauno.androiddeveloper.chewstudiodemo.data.DishDBHelper;
import com.sauno.androiddeveloper.chewstudiodemo.data.FavoriteDishesDBHelper;
import com.sauno.androiddeveloper.chewstudiodemo.data.IngredientsDBHelper;
import com.sauno.androiddeveloper.chewstudiodemo.model.ConflictsForDish;

import java.util.ArrayList;

public class AboutDishStatisticActivity extends AppCompatActivity {
    int idDish;
    String dishName;
    int lentenDish;
    int vegetarianDish;
    String countCalories;
    String countProteins;
    String countFats;
    String countCarbs;
    String countXE;

    TextView dishNameAboutDishTextView;
    TextView lentenDishTextView;
    TextView vegetarianTextView;

    TextView countCaloriesInAboutDishTextView;
    TextView countProteinsInAboutDishTextView;
    TextView countFatsInAboutDishTextView;
    TextView countCarbohydratesInAboutDishTextView;
    TextView countXEInAboutDishTextView;

    ImageView favoriteImageView;

    int numberDishColumn;

    int idForFavoriteDishesTable;

    int[] idFavoriteDishes;

    String[] namesDishColumns = {
            "Dish_1",
            "Dish_2",
            "Dish_3",
            "Dish_4",
            "Dish_5",
            "Dish_6",
            "Dish_7",
            "Dish_8",
            "Dish_9",
            "Dish_10",
            "Dish_11",
            "Dish_12",
            "Dish_13",
            "Dish_14",
            "Dish_15",
            "Dish_16",
            "Dish_17",
            "Dish_18",
            "Dish_19",
            "Dish_20"};

    //совместимость из анкеты
    int compatibilityType;

    ArrayList<ConflictsForDish> conflictsList = new ArrayList<>();

    String ingredientName;
    String orderIngredientName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_dish);

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        String str = sp.getString(UserPreferencesActivity.KEY_USER_COMPATIBILITY_TYPE_OF_FOOD_PREF, "0");
        compatibilityType = Integer.valueOf(str);



        Intent intent = getIntent();
        idDish = intent.getIntExtra("idDish", 0);
        dishName = intent.getStringExtra("dishName");

        Log.d("MyLogAboutDishStatistic", "id - " + idDish);
        //int[] compatibilityArray = intent.getIntArrayExtra("compatibilityArray");
        //int[] ingredientArray = intent.getIntArrayExtra("ingredientArray");


        /*lentenDish = intent.getIntExtra("lenten", 0);
        vegetarianDish = intent.getIntExtra("vegetarian", 0);*/

        dishNameAboutDishTextView = findViewById(R.id.dishNameAboutDishTextView);
        lentenDishTextView = findViewById(R.id.lentenDishTextView);
        vegetarianTextView = findViewById(R.id.vegetarianTextView);


        countCaloriesInAboutDishTextView = findViewById(R.id.countCaloriesInAboutDishTextView);
        countProteinsInAboutDishTextView = findViewById(R.id.countProteinsInAboutDishTextView);
        countFatsInAboutDishTextView = findViewById(R.id.countFatsInAboutDishTextView);
        countCarbohydratesInAboutDishTextView = findViewById(R.id.countCarbohydratesInAboutDishTextView);
        countXEInAboutDishTextView = findViewById(R.id.countXETextView);

        dishNameAboutDishTextView.setText(dishName);

        SharedPreferences mSharedPreferences = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        idForFavoriteDishesTable = mSharedPreferences.getInt("idForFavoriteDishes", -1);

        getAboutDishFromDB();

        setupActionBar();

        favoriteImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isFavoriteDish()) {
                    favoriteImageView.setImageResource(R.drawable.ic_action_bar_favorite_white);
                    deleteFromRowOfFavoriteDishTable();
                    Toast.makeText(view.getContext(), "Удалено из списка любимых блюд", Toast.LENGTH_SHORT).show();
                } else {
                    favoriteImageView.setImageResource(R.drawable.ic_action_bar_favorite_red);
                    addToRowOfFavoriteDishTable();
                    Toast.makeText(view.getContext(), "Добавлено в список любимых блюд", Toast.LENGTH_SHORT).show();
                }
            }
        });

       /* getConflictsOfIngredientFromDB(ingredientArray);

        //boolean check = checkConflicts(ingredientArray, dishName);

        RecyclerView conflictsRecyclerView = findViewById(R.id.conflictsRecyclerView);

        conflictsRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        conflictsRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        ConflictsAboutDishAdapter mAdapter = new ConflictsAboutDishAdapter(conflictsList);
        conflictsRecyclerView.setAdapter(mAdapter);*/
    }


    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setTitle("Описание блюда");
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setElevation(0);

            actionBar.setDisplayOptions(actionBar.getDisplayOptions()
                    | ActionBar.DISPLAY_SHOW_CUSTOM);
            favoriteImageView = new ImageView(actionBar.getThemedContext());
            favoriteImageView.setScaleType(ImageView.ScaleType.CENTER);
            favoriteImageView.setImageResource(R.drawable.ic_action_bar_favorite_white);
            ActionBar.LayoutParams layoutParams = new ActionBar.LayoutParams(
                    ActionBar.LayoutParams.WRAP_CONTENT,
                    ActionBar.LayoutParams.WRAP_CONTENT, Gravity.RIGHT
                    | Gravity.CENTER_VERTICAL);
            layoutParams.rightMargin = 20;
            favoriteImageView.setLayoutParams(layoutParams);
            actionBar.setCustomView(favoriteImageView);
        }

        if(isFavoriteDish()) favoriteImageView.setImageResource(R.drawable.ic_action_bar_favorite_red);
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

    private void getAboutDishFromDB() {
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
                DishDBHelper.COLUMN_XE,
                DishDBHelper.COLUMN_INGREDS_1,
                DishDBHelper.COLUMN_INGREDS_2,
                DishDBHelper.COLUMN_INGREDS_3,
                DishDBHelper.COLUMN_INGREDS_4,
                DishDBHelper.COLUMN_INGREDS_5,
                DishDBHelper.COLUMN_INGREDS_6,
                DishDBHelper.COLUMN_INGREDS_7,
                DishDBHelper.COLUMN_INGREDS_8,
                DishDBHelper.COLUMN_INGREDS_9,
                DishDBHelper.COLUMN_INGREDS_10,
                DishDBHelper.COLUMN_INGREDS_11,
                DishDBHelper.COLUMN_INGREDS_12,
                DishDBHelper.COLUMN_INGREDS_13,
                DishDBHelper.COLUMN_INGREDS_14,
                DishDBHelper.COLUMN_INGREDS_15,
                DishDBHelper.COLUMN_INGREDS_16,
                DishDBHelper.COLUMN_INGREDS_17,
                DishDBHelper.COLUMN_INGREDS_18,
                DishDBHelper.COLUMN_INGREDS_19,
                DishDBHelper.COLUMN_INGREDS_20
        };

        // Filter results WHERE "title" = 'My Title'
        String selection = DishDBHelper.COLUMN_ID + " = ?";
        String[] selectionArgs = {"" + idDish};


        Cursor cursor = db.query(
                DishDBHelper.TABLE,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        //List<String> categories = new ArrayList<>();
        //List<Float> prices = new ArrayList<>();

        int[] ingredientArray = new int[20];

        if (cursor.moveToFirst()) {
            /*lentenDish = cursor.getInt(cursor.getColumnIndexOrThrow(DishDBHelper.COLUMN_LENTEN));
            vegetarianDish = cursor.getInt(cursor.getColumnIndexOrThrow(DishDBHelper.COLUMN_VEGETARIAN));*/

            countCalories = Float.toString(cursor.getFloat(cursor.getColumnIndexOrThrow(DishDBHelper.COLUMN_CALORIC)));
            countProteins = Float.toString(cursor.getFloat(cursor.getColumnIndexOrThrow(DishDBHelper.COLUMN_PROTEINS)));
            countFats = Float.toString(cursor.getFloat(cursor.getColumnIndexOrThrow(DishDBHelper.COLUMN_FATS)));
            countCarbs = Float.toString(cursor.getFloat(cursor.getColumnIndexOrThrow(DishDBHelper.COLUMN_CARBS)));
            countXE = Float.toString(cursor.getFloat(cursor.getColumnIndexOrThrow(DishDBHelper.COLUMN_XE)));

            ingredientArray[0] = cursor.getInt(cursor.getColumnIndexOrThrow(DishDBHelper.COLUMN_INGREDS_1));
            ingredientArray[1] = cursor.getInt(cursor.getColumnIndexOrThrow(DishDBHelper.COLUMN_INGREDS_2));
            ingredientArray[2] = cursor.getInt(cursor.getColumnIndexOrThrow(DishDBHelper.COLUMN_INGREDS_3));
            ingredientArray[3] = cursor.getInt(cursor.getColumnIndexOrThrow(DishDBHelper.COLUMN_INGREDS_4));
            ingredientArray[4] = cursor.getInt(cursor.getColumnIndexOrThrow(DishDBHelper.COLUMN_INGREDS_5));
            ingredientArray[5] = cursor.getInt(cursor.getColumnIndexOrThrow(DishDBHelper.COLUMN_INGREDS_6));
            ingredientArray[6] = cursor.getInt(cursor.getColumnIndexOrThrow(DishDBHelper.COLUMN_INGREDS_7));
            ingredientArray[7] = cursor.getInt(cursor.getColumnIndexOrThrow(DishDBHelper.COLUMN_INGREDS_8));
            ingredientArray[8] = cursor.getInt(cursor.getColumnIndexOrThrow(DishDBHelper.COLUMN_INGREDS_9));
            ingredientArray[9] = cursor.getInt(cursor.getColumnIndexOrThrow(DishDBHelper.COLUMN_INGREDS_10));
            ingredientArray[10] = cursor.getInt(cursor.getColumnIndexOrThrow(DishDBHelper.COLUMN_INGREDS_11));
            ingredientArray[11] = cursor.getInt(cursor.getColumnIndexOrThrow(DishDBHelper.COLUMN_INGREDS_12));
            ingredientArray[12] = cursor.getInt(cursor.getColumnIndexOrThrow(DishDBHelper.COLUMN_INGREDS_13));
            ingredientArray[13] = cursor.getInt(cursor.getColumnIndexOrThrow(DishDBHelper.COLUMN_INGREDS_14));
            ingredientArray[14] = cursor.getInt(cursor.getColumnIndexOrThrow(DishDBHelper.COLUMN_INGREDS_15));
            ingredientArray[15] = cursor.getInt(cursor.getColumnIndexOrThrow(DishDBHelper.COLUMN_INGREDS_16));
            ingredientArray[16] = cursor.getInt(cursor.getColumnIndexOrThrow(DishDBHelper.COLUMN_INGREDS_17));
            ingredientArray[17] = cursor.getInt(cursor.getColumnIndexOrThrow(DishDBHelper.COLUMN_INGREDS_18));
            ingredientArray[18] = cursor.getInt(cursor.getColumnIndexOrThrow(DishDBHelper.COLUMN_INGREDS_19));
            ingredientArray[19] = cursor.getInt(cursor.getColumnIndexOrThrow(DishDBHelper.COLUMN_INGREDS_20));

        }

        countCaloriesInAboutDishTextView.setText(countCalories);
        countProteinsInAboutDishTextView.setText(countProteins);
        countFatsInAboutDishTextView.setText(countFats);
        countCarbohydratesInAboutDishTextView.setText(countCarbs);
        countXEInAboutDishTextView.setText(countXE);

        cursor.close();
        db.close();

        getDataAboutIngredients(ingredientArray);
    }


    private boolean isFavoriteDish() {
        DatabaseCreateHelper databaseCreateHelper = new DatabaseCreateHelper(getApplicationContext());
        SQLiteDatabase db;
        try {
            db = databaseCreateHelper.getWritableDatabase();
        }
        catch (SQLiteException ex){
            db = databaseCreateHelper.getReadableDatabase();
        }

        String[] projection = {
                FavoriteDishesDBHelper.COLUMN_DISH_1,
                FavoriteDishesDBHelper.COLUMN_DISH_2,
                FavoriteDishesDBHelper.COLUMN_DISH_3,
                FavoriteDishesDBHelper.COLUMN_DISH_4,
                FavoriteDishesDBHelper.COLUMN_DISH_5,
                FavoriteDishesDBHelper.COLUMN_DISH_6,
                FavoriteDishesDBHelper.COLUMN_DISH_7,
                FavoriteDishesDBHelper.COLUMN_DISH_8,
                FavoriteDishesDBHelper.COLUMN_DISH_9,
                FavoriteDishesDBHelper.COLUMN_DISH_10,
                FavoriteDishesDBHelper.COLUMN_DISH_11,
                FavoriteDishesDBHelper.COLUMN_DISH_12,
                FavoriteDishesDBHelper.COLUMN_DISH_13,
                FavoriteDishesDBHelper.COLUMN_DISH_14,
                FavoriteDishesDBHelper.COLUMN_DISH_15,
                FavoriteDishesDBHelper.COLUMN_DISH_16,
                FavoriteDishesDBHelper.COLUMN_DISH_17,
                FavoriteDishesDBHelper.COLUMN_DISH_18,
                FavoriteDishesDBHelper.COLUMN_DISH_19,
                FavoriteDishesDBHelper.COLUMN_DISH_20
        };

        String selection = FavoriteDishesDBHelper.COLUMN_ID + " = ?";

        String idString = "" + idForFavoriteDishesTable;

//        String idString = "" + ChoiceOfDishesActivity.idForFavoriteDishesTable;
        String[] selectionArgs = {idString};

        Cursor cursor = db.query(
                true,
                FavoriteDishesDBHelper.TABLE,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null,
                null
        );

        idFavoriteDishes = new int[20];

        if (cursor.moveToFirst()) {
            for(int n = 0; n < 20; n++) {
                int dish = cursor.getInt(n);
                if(dish != 0) {
                    idFavoriteDishes[n] = dish;
                } else {
                    n = 20;
                }
            }
        }

        cursor.close();
        db.close();

        for(int n = 0; n < 20; n++) {

            if(idDish == idFavoriteDishes[n]) {
                numberDishColumn = n;
                return true;
            }
        }

        return false;
    }

    private void deleteFromRowOfFavoriteDishTable() {
        int[] newIdFavoriteDishes = new int[idFavoriteDishes.length];

        for(int i = 0; i < idFavoriteDishes.length; i++) {
            if(i == idFavoriteDishes.length-1) {
                newIdFavoriteDishes[i] = 0;
                break;
            }
            if(i < numberDishColumn) {
                newIdFavoriteDishes[i] = idFavoriteDishes[i];
                continue;
            }
            newIdFavoriteDishes[i] = idFavoriteDishes[i + 1];
        }

        for (int n : newIdFavoriteDishes) {
            Log.i("MyLogAboutDish", "n = " + n);
        }

        idFavoriteDishes = newIdFavoriteDishes;


        DatabaseCreateHelper databaseCreateHelper = new DatabaseCreateHelper(getApplicationContext());
        SQLiteDatabase db = databaseCreateHelper.getWritableDatabase();

        ContentValues favoritDishValue = new ContentValues();
        favoritDishValue.put(namesDishColumns[0], newIdFavoriteDishes[0]);
        favoritDishValue.put(namesDishColumns[1], newIdFavoriteDishes[1]);
        favoritDishValue.put(namesDishColumns[2], newIdFavoriteDishes[2]);
        favoritDishValue.put(namesDishColumns[3], newIdFavoriteDishes[3]);
        favoritDishValue.put(namesDishColumns[4], newIdFavoriteDishes[4]);
        favoritDishValue.put(namesDishColumns[5], newIdFavoriteDishes[5]);
        favoritDishValue.put(namesDishColumns[6], newIdFavoriteDishes[6]);
        favoritDishValue.put(namesDishColumns[7], newIdFavoriteDishes[7]);
        favoritDishValue.put(namesDishColumns[8], newIdFavoriteDishes[8]);
        favoritDishValue.put(namesDishColumns[9], newIdFavoriteDishes[9]);
        favoritDishValue.put(namesDishColumns[10], newIdFavoriteDishes[10]);
        favoritDishValue.put(namesDishColumns[11], newIdFavoriteDishes[11]);
        favoritDishValue.put(namesDishColumns[12], newIdFavoriteDishes[12]);
        favoritDishValue.put(namesDishColumns[13], newIdFavoriteDishes[13]);
        favoritDishValue.put(namesDishColumns[14], newIdFavoriteDishes[14]);
        favoritDishValue.put(namesDishColumns[15], newIdFavoriteDishes[15]);
        favoritDishValue.put(namesDishColumns[16], newIdFavoriteDishes[16]);
        favoritDishValue.put(namesDishColumns[17], newIdFavoriteDishes[17]);
        favoritDishValue.put(namesDishColumns[18], newIdFavoriteDishes[18]);
        favoritDishValue.put(namesDishColumns[19], newIdFavoriteDishes[19]);

        String selection = FavoriteDishesDBHelper.COLUMN_ID + " = ?";
        String idString = "" + idForFavoriteDishesTable;
        String[] selectionArgs = {idString};

        db.update(FavoriteDishesDBHelper.TABLE,
                favoritDishValue,
                selection,
                selectionArgs);

        db.close();
    }

    private void addToRowOfFavoriteDishTable() {
        int emptyNumber = getEmptyNumberDishColumn();

        if(emptyNumber == -1) {
            Toast.makeText(this, "База данных любимых блюд имеет только 20 возможных полей. ", Toast.LENGTH_SHORT).show();
        } else if(isExistDish()) {
            Toast.makeText(this, "Такое блюдо уже существует в базе данных", Toast.LENGTH_SHORT).show();
        } else {
            DatabaseCreateHelper databaseCreateHelper = new DatabaseCreateHelper(getApplicationContext());
            SQLiteDatabase db = databaseCreateHelper.getWritableDatabase();

            ContentValues favoritDishValue = new ContentValues();
            favoritDishValue.put(namesDishColumns[emptyNumber], idDish);

            String selection = FavoriteDishesDBHelper.COLUMN_ID + " = ?";
            String idString = "" + idForFavoriteDishesTable;
            String[] selectionArgs = {idString};

            db.update(FavoriteDishesDBHelper.TABLE,
                    favoritDishValue,
                    selection,
                    selectionArgs);

            db.close();
        }
    }

    //получение пустого место в базе куда можно записать любимое блюдо
    private int getEmptyNumberDishColumn() {
        for(int n = 0; n < idFavoriteDishes.length; n++) {
            if(idFavoriteDishes[n] == 0) {
                return n;
            }
        }
        return -1;
    }

    private boolean isExistDish() {
        for (int n : idFavoriteDishes) {
            if(n == idDish) {
                return true;
            }
        }
        return false;
    }



    private void getDataAboutIngredients(int[] ingredientArray) {
        DatabaseCreateHelper databaseCreateHelper = new DatabaseCreateHelper(getApplicationContext());
        SQLiteDatabase db;
        try {
            db = databaseCreateHelper.getWritableDatabase();
        } catch (SQLiteException ex) {
            db = databaseCreateHelper.getReadableDatabase();
        }

        int lentenRes = 1;
        int vegetarianRes = 1;

        for(int ingredientID : ingredientArray){
            if(ingredientID != 0) {
                //Log.d("MyLogListDishes","Ингредиент ID - " + ingredientID);

                String[] projection = {
                        IngredientsDBHelper.COLUMN_VEGETARIAN,
                        IngredientsDBHelper.COLUMN_LENTEN
                };

                String selection = IngredientsDBHelper.COLUMN_ID + " = ?";
                String[] selectionArgs = {"" + ingredientID};

                Cursor cursor = db.query(
                        IngredientsDBHelper.TABLE,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        null
                );


                String diabet = "можно";
                int lenten = 1;
                int vegetarian = 1;
                int[] compatibilityIntArray = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};

                if (cursor.moveToFirst()) {
                    do {
                        lenten = cursor.getInt(cursor.getColumnIndexOrThrow(IngredientsDBHelper.COLUMN_LENTEN));
                        vegetarian = cursor.getInt(cursor.getColumnIndexOrThrow(IngredientsDBHelper.COLUMN_VEGETARIAN));

                    } while (cursor.moveToNext());
                }
                cursor.close();

                if(lenten == 0) {
                    lentenRes = 0;
                }

                if(vegetarian == 0) {
                   vegetarianRes = 0;
                }
            }
        }

        if(lentenRes == 1) {
            lentenDishTextView.setText("Постное блюдо");
        } else if(lentenRes == 0) {
            lentenDishTextView.setText("Не постное блюдо");
        }
        if(vegetarianRes == 1) {
            vegetarianTextView.setText("Вегетарианское");
        } else if(vegetarianRes == 0) {
            vegetarianTextView.setText("Не вегетарианское");
        }

        db.close();

    }
}

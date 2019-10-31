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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.sauno.androiddeveloper.chewstudiodemo.adapter.DishIngredientsAdapter;
import com.sauno.androiddeveloper.chewstudiodemo.data.DatabaseCreateHelper;
import com.sauno.androiddeveloper.chewstudiodemo.data.DishDBHelper;
import com.sauno.androiddeveloper.chewstudiodemo.data.FavoriteDishesDBHelper;
import com.sauno.androiddeveloper.chewstudiodemo.data.IngredientsDBHelper;

import java.util.ArrayList;
import java.util.List;

public class DishInformationActivity extends AppCompatActivity {
    int idDish;

    ImageView favoriteImageView;

    private int currentUserPartOfCalories;
    private int currentUserPartOfProteins;
    private int currentUserPartOfFats;
    private int currentUserPartOfCarbohydrates;
    private int currentUserPartOfXE;
    private TextView limitExceededCPFCXETextView;
    private TextView compatibilityWithIngredientsTextView;
    private TextView compatibilityWithDishesTextView;
    private TextView diabetesTextView;
    private TextView lentenNameTextView;
    private TextView lentenTextView;
    private TextView veganNameTextView;
    private TextView veganTextView;
    private TextView ifRemoveIngredientsTextView;
    private TextView allowedToEatTextView;

    DatabaseCreateHelper databaseCreateHelper;

    public static List<String> ingredientNameList = new ArrayList<>();

    private RecyclerView ingredientsRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private boolean isCheckLenten;
    private boolean isCheckVegan;

    int idForFavoriteDishesTable;

    int numberDishColumn;

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

    TextView countCaloriesTextView;
    TextView countProteinsTextView;
    TextView countFatsTextView;
    TextView countCarbohydratesTextView;
    TextView countXETextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dish_information);

        Intent intent = getIntent();
        idDish = intent.getIntExtra("idDish", 0);
        String dishName = intent.getStringExtra("dishName");



        limitExceededCPFCXETextView = findViewById(R.id.limitExceededCPFCXETextView);
        compatibilityWithIngredientsTextView = findViewById(R.id.compatibilityWithIngredientsTextView);
        compatibilityWithDishesTextView = findViewById(R.id.compatibilityWithDishesTextView);
        diabetesTextView = findViewById(R.id.diabetesTextView);
        lentenNameTextView = findViewById(R.id.lentenNameTextView);
        lentenTextView = findViewById(R.id.lentenTextView);
        veganNameTextView = findViewById(R.id.veganNameTextView);
        veganTextView = findViewById(R.id.veganTextView);
        ifRemoveIngredientsTextView = findViewById(R.id.ifRemoveIngredientsTextView);
        allowedToEatTextView = findViewById(R.id.allowedToEatTextView);

        ingredientsRecyclerView = findViewById(R.id.ingredientsRecyclerView);

        countCaloriesTextView = findViewById(R.id.countCaloriesTextView);
        countProteinsTextView = findViewById(R.id.countProteinsTextView);
        countFatsTextView = findViewById(R.id.countFatsTextView);
        countCarbohydratesTextView = findViewById(R.id.countCarbohydratesTextView);
        countXETextView = findViewById(R.id.countXETextView);


        databaseCreateHelper = new DatabaseCreateHelper(getApplicationContext());
        getDisheDataFromDB();

        SharedPreferences mSharedPreferences = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        idForFavoriteDishesTable = mSharedPreferences.getInt("idForFavoriteDishes", -1);

        setupActionBar(dishName);

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


    }

    private void setupActionBar(String dishName) {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setTitle(dishName);
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
        Log.d("MyLogDish", "is - " + isExistDish());
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


    private void getDisheDataFromDB() {
        SQLiteDatabase db;
        try {
            db = databaseCreateHelper.getWritableDatabase();
        } catch (SQLiteException ex) {
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
                DishDBHelper.COLUMN_INGREDS_20,
                DishDBHelper.COLUMN_DIABETES,
                DishDBHelper.COLUMN_LENTEN,
                DishDBHelper.COLUMN_VEGETARIAN
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

//        int c1, c2, c3, c4, c5, c6, c7, c8, c9 ,c10 ,c11, c12, c13, c14, c15, c16;


        int i1, i2, i3, i4, i5, i6, i7, i8, i9, i10, i11, i12, i13, i14, i15, i16, i17, i18, i19, i20;
        int[] ingredientArray = {0, 0};

        if (cursor.moveToFirst()) {
            int countCalories = (int)cursor.getFloat(cursor.getColumnIndexOrThrow(DishDBHelper.COLUMN_CALORIC));
            int countProteins = (int)cursor.getFloat(cursor.getColumnIndexOrThrow(DishDBHelper.COLUMN_PROTEINS));
            int countFats = (int)cursor.getFloat(cursor.getColumnIndexOrThrow(DishDBHelper.COLUMN_FATS));
            int countCarbohydrates = (int)cursor.getFloat(cursor.getColumnIndexOrThrow(DishDBHelper.COLUMN_CARBS));
            int countXE = (int)cursor.getFloat(cursor.getColumnIndexOrThrow(DishDBHelper.COLUMN_XE));

            countCaloriesTextView.setText("" + countCalories);
            countProteinsTextView.setText("" + countProteins);
            countFatsTextView.setText("" + countFats);
            countCarbohydratesTextView.setText("" + countCarbohydrates);
            countXETextView.setText("" + countXE);

            checkExceededCPFCXE(countCalories, countProteins, countFats, countCarbohydrates, countXE);

            String diabetes = cursor.getString(cursor.getColumnIndexOrThrow(DishDBHelper.COLUMN_DIABETES));
            diabetesTextView.setText(diabetes);

            String lenten = cursor.getString(cursor.getColumnIndexOrThrow(DishDBHelper.COLUMN_LENTEN));
            String vegetarian = cursor.getString(cursor.getColumnIndexOrThrow(DishDBHelper.COLUMN_VEGETARIAN));

            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
            String ifSetLenten = sp.getString(UserPreferencesActivity.KEY_USER_TYPE_OF_FOOD_LENTEN_PREF, "");
            String ifSetVegan = sp.getString(UserPreferencesActivity.KEY_USER_TYPE_OF_FOOD_VEGETARIAN_PREF, "");

            if(ifSetLenten.equals("да")) {
                lentenTextView.setText(lenten);
                isCheckLenten = true;
            } else {
                lentenNameTextView.setVisibility(View.GONE);
                lentenTextView.setVisibility(View.GONE);
            }

            if(ifSetVegan.equals("да")) {
                veganTextView.setText(vegetarian);
                isCheckVegan = true;
            } else {
                veganNameTextView.setVisibility(View.GONE);
                veganTextView.setVisibility(View.GONE);
            }



                /*c1 = cursor.getInt(cursor.getColumnIndexOrThrow(DishDBHelper.COLUMN_COMPATIBILITY_1));
                c2 = cursor.getInt(cursor.getColumnIndexOrThrow(DishDBHelper.COLUMN_COMPATIBILITY_2));
                c3 = cursor.getInt(cursor.getColumnIndexOrThrow(DishDBHelper.COLUMN_COMPATIBILITY_3));
                c4 = cursor.getInt(cursor.getColumnIndexOrThrow(DishDBHelper.COLUMN_COMPATIBILITY_4));
                c5 = cursor.getInt(cursor.getColumnIndexOrThrow(DishDBHelper.COLUMN_COMPATIBILITY_5));
                c6 = cursor.getInt(cursor.getColumnIndexOrThrow(DishDBHelper.COLUMN_COMPATIBILITY_6));
                c7 = cursor.getInt(cursor.getColumnIndexOrThrow(DishDBHelper.COLUMN_COMPATIBILITY_7));
                c8 = cursor.getInt(cursor.getColumnIndexOrThrow(DishDBHelper.COLUMN_COMPATIBILITY_8));
                c9 = cursor.getInt(cursor.getColumnIndexOrThrow(DishDBHelper.COLUMN_COMPATIBILITY_9));
                c10 = cursor.getInt(cursor.getColumnIndexOrThrow(DishDBHelper.COLUMN_COMPATIBILITY_10));
                c11 = cursor.getInt(cursor.getColumnIndexOrThrow(DishDBHelper.COLUMN_COMPATIBILITY_11));
                c12 = cursor.getInt(cursor.getColumnIndexOrThrow(DishDBHelper.COLUMN_COMPATIBILITY_12));
                c13 = cursor.getInt(cursor.getColumnIndexOrThrow(DishDBHelper.COLUMN_COMPATIBILITY_13));
                c14 = cursor.getInt(cursor.getColumnIndexOrThrow(DishDBHelper.COLUMN_COMPATIBILITY_14));
                c15 = cursor.getInt(cursor.getColumnIndexOrThrow(DishDBHelper.COLUMN_COMPATIBILITY_15));
                c16 = cursor.getInt(cursor.getColumnIndexOrThrow(DishDBHelper.COLUMN_COMPATIBILITY_16));*/

            //compatibilityArraysList.add(new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0});

            i1 = cursor.getInt(cursor.getColumnIndexOrThrow(DishDBHelper.COLUMN_INGREDS_1));
            i2 = cursor.getInt(cursor.getColumnIndexOrThrow(DishDBHelper.COLUMN_INGREDS_2));
            i3 = cursor.getInt(cursor.getColumnIndexOrThrow(DishDBHelper.COLUMN_INGREDS_3));
            i4 = cursor.getInt(cursor.getColumnIndexOrThrow(DishDBHelper.COLUMN_INGREDS_4));
            i5 = cursor.getInt(cursor.getColumnIndexOrThrow(DishDBHelper.COLUMN_INGREDS_5));
            i6 = cursor.getInt(cursor.getColumnIndexOrThrow(DishDBHelper.COLUMN_INGREDS_6));
            i7 = cursor.getInt(cursor.getColumnIndexOrThrow(DishDBHelper.COLUMN_INGREDS_7));
            i8 = cursor.getInt(cursor.getColumnIndexOrThrow(DishDBHelper.COLUMN_INGREDS_8));
            i9 = cursor.getInt(cursor.getColumnIndexOrThrow(DishDBHelper.COLUMN_INGREDS_9));
            i10 = cursor.getInt(cursor.getColumnIndexOrThrow(DishDBHelper.COLUMN_INGREDS_10));
            i11 = cursor.getInt(cursor.getColumnIndexOrThrow(DishDBHelper.COLUMN_INGREDS_11));
            i12 = cursor.getInt(cursor.getColumnIndexOrThrow(DishDBHelper.COLUMN_INGREDS_12));
            i13 = cursor.getInt(cursor.getColumnIndexOrThrow(DishDBHelper.COLUMN_INGREDS_13));
            i14 = cursor.getInt(cursor.getColumnIndexOrThrow(DishDBHelper.COLUMN_INGREDS_14));
            i15 = cursor.getInt(cursor.getColumnIndexOrThrow(DishDBHelper.COLUMN_INGREDS_15));
            i16 = cursor.getInt(cursor.getColumnIndexOrThrow(DishDBHelper.COLUMN_INGREDS_16));
            i17 = cursor.getInt(cursor.getColumnIndexOrThrow(DishDBHelper.COLUMN_INGREDS_17));
            i18 = cursor.getInt(cursor.getColumnIndexOrThrow(DishDBHelper.COLUMN_INGREDS_18));
            i19 = cursor.getInt(cursor.getColumnIndexOrThrow(DishDBHelper.COLUMN_INGREDS_19));
            i20 = cursor.getInt(cursor.getColumnIndexOrThrow(DishDBHelper.COLUMN_INGREDS_20));

            int[] ingredientArray1 = {i1, i2, i3, i4, i5, i6, i7, i8, i9, i10, i11, i12, i13, i14, i15, i16, i17, i18, i19, i20};
            ingredientArray = ingredientArray1;


            //int[] compatibilityIntArray = {c1, c2, c3, c4, c5, c6, c7, c8, c9 ,c10 ,c11, c12, c13, c14, c15, c16};

            //int compatibilityEvaluation = getCompatibilityEvaluation(compatibilityIntArray);
            //compatibilityEvaluationList.add(compatibilityEvaluation);

            //Log.i("MyLogListDishes", "DishName - " + dishName + "   compatibilityEvaluation - " + compatibilityEvaluation);

        }

        cursor.close();
        db.close();


        getIngredientsName(ingredientArray);
    }


    private void getIngredientsName(int[] ingredientArray) {
        for(int ingredientId : ingredientArray) {
            if(ingredientId != 0) {
                String ingredientName = getIngredientsNameFromDB(ingredientId);
                ingredientNameList.add(ingredientName);
            }
        }

        setListIngredients();
    }

    private void setListIngredients() {
        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        ingredientsRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        mAdapter = new DishIngredientsAdapter(ingredientNameList);
        ingredientsRecyclerView.setAdapter(mAdapter);
    }

    private String getIngredientsNameFromDB(int id) {
        SQLiteDatabase db;
        db = databaseCreateHelper.getReadableDatabase();

        String[] projection = {
                IngredientsDBHelper.COLUMN_DESCRIPTION,
                IngredientsDBHelper.COLUMN_LENTEN,
                IngredientsDBHelper.COLUMN_VEGETARIAN,
                IngredientsDBHelper.COLUMN_DIABETES
        };

        String selection = IngredientsDBHelper.COLUMN_ID + " = ?";
        String[] selectionArgs = {"" + id};


        Cursor cursor = db.query(
                IngredientsDBHelper.TABLE,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        String name = "";

        if (cursor.moveToFirst()) {
            name = cursor.getString(cursor.getColumnIndexOrThrow(IngredientsDBHelper.COLUMN_DESCRIPTION));

            if(isCheckLenten) {
                int lenten = cursor.getInt(cursor.getColumnIndexOrThrow(IngredientsDBHelper.COLUMN_LENTEN));
                if(lenten == 0) {
                    name = name + "(нельзя)";
                }
            } else if(isCheckVegan) {
                int vegan = cursor.getInt(cursor.getColumnIndexOrThrow(IngredientsDBHelper.COLUMN_VEGETARIAN));
                if(vegan == 0) {
                    name = name + "(нельзя)";
                }
            } else {
                String diabetes = cursor.getString(cursor.getColumnIndexOrThrow(IngredientsDBHelper.COLUMN_DIABETES));
                if(diabetes.equals("умеренно")) {
                    name = name + "(умеренно)";
                }
            }
        }

        cursor.close();
        db.close();

        return name;
    }



    private void checkExceededCPFCXE(int countCalories, int countProteins, int countFats, int countCarbohydrates, int countXE) {
        getCurrentUserCPFC();

        if(     currentUserPartOfCalories < countCalories ||
                currentUserPartOfProteins < countProteins ||
                currentUserPartOfFats < countFats ||
                currentUserPartOfCarbohydrates < countCarbohydrates ||
                currentUserPartOfXE < countXE) {
            limitExceededCPFCXETextView.setText("превышен");
            limitExceededCPFCXETextView.setTextColor(getResources().getColor(R.color.red));

            // установка процента, который допустим чтобв съесть

            setPermissiblePercentage(countCalories, countProteins, countFats, countCarbohydrates, countXE);
        } else {
            limitExceededCPFCXETextView.setText("не превышен");
            limitExceededCPFCXETextView.setTextColor(getResources().getColor(R.color.colorDarkGreen));
        }
    }

    //получение текущих КБЖУХ
    private void getCurrentUserCPFC() {
        SharedPreferences mSharedPreferences = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        currentUserPartOfCalories = mSharedPreferences.getInt("currentCalories", 0);
        currentUserPartOfProteins = mSharedPreferences.getInt("currentProteins", 0);
        currentUserPartOfFats = mSharedPreferences.getInt("currentFats", 0);
        currentUserPartOfCarbohydrates = mSharedPreferences.getInt("currentCarbs", 0);
        currentUserPartOfXE = mSharedPreferences.getInt("currentXE", 0);
    }

    private void setPermissiblePercentage(int countCalories, int countProteins, int countFats, int countCarbohydrates, int countXE) {
        int permPers = 0;
        int persent;

        if(currentUserPartOfCalories < countCalories) {
            persent = 100 - currentUserPartOfCalories/(countCalories - currentUserPartOfCalories)*100;

            if(persent > permPers) {
                permPers = persent;
            }
        }

        if(currentUserPartOfProteins < countProteins) {
            persent = 100 - currentUserPartOfProteins/(countProteins - currentUserPartOfProteins)*100;

            if(persent > permPers) {
                permPers = persent;
            }
        }

        if(currentUserPartOfFats < countFats) {
            persent = 100 - currentUserPartOfFats/(countFats - currentUserPartOfFats)*100;

            if(persent > permPers) {
                permPers = persent;
            }
        }

        if(currentUserPartOfCarbohydrates < countCarbohydrates) {
            persent = 100 - currentUserPartOfCarbohydrates/(countCarbohydrates - currentUserPartOfCarbohydrates)*100;

            if(persent > permPers) {
                permPers = persent;
            }
        }

        if(currentUserPartOfXE < countXE) {
            persent = 100 - currentUserPartOfXE/(countXE - currentUserPartOfXE)*100;

            if(persent > permPers) {
                permPers = persent;
            }
        }

        allowedToEatTextView.setText("Можно съесть " + permPers + "%");

        if(permPers < 100) {
            allowedToEatTextView.setTextColor(getResources().getColor(R.color.red));
        }
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
}

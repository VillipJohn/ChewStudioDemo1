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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.sauno.androiddeveloper.chewstudiodemo.adapter.ConflictsAboutDishAdapter;
import com.sauno.androiddeveloper.chewstudiodemo.data.DatabaseCreateHelper;
import com.sauno.androiddeveloper.chewstudiodemo.data.DishDBHelper;
import com.sauno.androiddeveloper.chewstudiodemo.data.FavoriteDishesDBHelper;
import com.sauno.androiddeveloper.chewstudiodemo.data.IngredientsDBHelper;
import com.sauno.androiddeveloper.chewstudiodemo.model.ConflictsForDish;
import com.sauno.androiddeveloper.chewstudiodemo.model.DishOrderItem;

import java.util.ArrayList;


public class AboutDishActivity extends AppCompatActivity {
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
        int[] compatibilityArray = intent.getIntArrayExtra("compatibilityArray");
        int[] ingredientArray = intent.getIntArrayExtra("ingredientArray");


        lentenDish = intent.getIntExtra("lenten", 0);
        vegetarianDish = intent.getIntExtra("vegetarian", 0);

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

        getConflictsOfIngredientFromDB(ingredientArray);

        //boolean check = checkConflicts(ingredientArray, dishName);

        RecyclerView conflictsRecyclerView = findViewById(R.id.conflictsRecyclerView);

        conflictsRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        conflictsRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        ConflictsAboutDishAdapter mAdapter = new ConflictsAboutDishAdapter(conflictsList);
        conflictsRecyclerView.setAdapter(mAdapter);
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
                DishDBHelper.COLUMN_XE
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

        if (cursor.moveToFirst()) {
            /*lentenDish = cursor.getInt(cursor.getColumnIndexOrThrow(DishDBHelper.COLUMN_LENTEN));
            vegetarianDish = cursor.getInt(cursor.getColumnIndexOrThrow(DishDBHelper.COLUMN_VEGETARIAN));*/

            countCalories = Float.toString(cursor.getFloat(cursor.getColumnIndexOrThrow(DishDBHelper.COLUMN_CALORIC)));
            countProteins = Float.toString(cursor.getFloat(cursor.getColumnIndexOrThrow(DishDBHelper.COLUMN_PROTEINS)));
            countFats = Float.toString(cursor.getFloat(cursor.getColumnIndexOrThrow(DishDBHelper.COLUMN_FATS)));
            countCarbs = Float.toString(cursor.getFloat(cursor.getColumnIndexOrThrow(DishDBHelper.COLUMN_CARBS)));
            countXE = Float.toString(cursor.getFloat(cursor.getColumnIndexOrThrow(DishDBHelper.COLUMN_XE)));
        }

        if(lentenDish == 1) {
            lentenDishTextView.setText("Постное блюдо");
        } else if(lentenDish == 0) {
            lentenDishTextView.setText("Не постное блюдо");
        }
        if(vegetarianDish == 1) {
            vegetarianTextView.setText("Вегетарианское");
        } else if(vegetarianDish == 0) {
            vegetarianTextView.setText("Не вегетарианское");
        }


        countCaloriesInAboutDishTextView.setText(countCalories);
        countProteinsInAboutDishTextView.setText(countProteins);
        countFatsInAboutDishTextView.setText(countFats);
        countCarbohydratesInAboutDishTextView.setText(countCarbs);
        countXEInAboutDishTextView.setText(countXE);

        cursor.close();
        db.close();
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

    private boolean checkConflicts(int[] compatibilityArray) {
        if(compatibilityType == 1) {
            for(DishOrderItem dishOrderItem : ChoiceOfDishesActivity.dishOrderList) {
                ArrayList<ConflictsForDish> partConflictsList = new ArrayList<>();
                String oneString;
                String twoString;
                String threeString;

                for(Integer ingredientId : dishOrderItem.getIngredientIDArray()) {
                    if(ingredientId != 0) {
                        int[] orderCompatibilityArray = getCompatibilities(ingredientId);

                        int result = getSimpleCompatibilityEvaluation(compatibilityArray, orderCompatibilityArray);

                        if(result == 2) {
                            if(partConflictsList.size() < 1) {
                                oneString = dishName + ":";
                                twoString = dishOrderItem.getDishName() + ":";
                                threeString = "Конфликтуют";

                                partConflictsList.add(new ConflictsForDish(oneString, twoString, threeString));
                            }

                            oneString = ingredientName;
                            twoString = orderIngredientName;
                            threeString = "сильно";

                            partConflictsList.add(new ConflictsForDish(oneString, twoString, threeString));
                            //conflictsList.add(ingredientName + " из блюда " + dishName + " + " + orderIngredientName + " из блюда " + dishOrderItem.getDishName() + " = сильно конфликтуют");
                        }

                        if(result == 3) {
                            if(partConflictsList.size() < 1) {
                                oneString = dishName + ":";
                                twoString = dishOrderItem.getDishName() + ":";
                                threeString = "Конфликтуют";

                                partConflictsList.add(new ConflictsForDish(oneString, twoString, threeString));
                            }

                            oneString = ingredientName;
                            twoString = orderIngredientName;
                            threeString = "средне";

                            partConflictsList.add(new ConflictsForDish(oneString, twoString, threeString));

                            //conflictsList.add(ingredientName + " из блюда " + dishName + " + " + orderIngredientName + " из блюда " + dishOrderItem.getDishName() + " = умеренно конфликтуют");
                        }
                    }
                }

                if(partConflictsList.size() > 0) {
                    oneString = " ";
                    twoString = " ";
                    threeString = " ";

                    partConflictsList.add(new ConflictsForDish(oneString, twoString, threeString));

                    conflictsList.addAll(partConflictsList);
                }
            }
        }



        if(compatibilityType == 2) {
            for(DishOrderItem dishOrderItem : ChoiceOfDishesActivity.dishOrderList) {
                ArrayList<ConflictsForDish> partConflictsList = new ArrayList<>();
                String oneString;
                String twoString;
                String threeString;

                for(Integer ingredientId : dishOrderItem.getIngredientIDArray()) {
                    if(ingredientId != 0) {
                        int[] orderCompatibilityArray = getCompatibilities(ingredientId);

                        int result = getSheltonCompatibilityEvaluation(compatibilityArray, orderCompatibilityArray);

                        if(result == 2) {
                            if(partConflictsList.size() < 1) {
                                oneString = dishName + ":";
                                twoString = dishOrderItem.getDishName() + ":";
                                threeString = "Конфликтуют";

                                partConflictsList.add(new ConflictsForDish(oneString, twoString, threeString));
                            }

                            oneString = ingredientName;
                            twoString = orderIngredientName;
                            threeString = "сильно";

                            partConflictsList.add(new ConflictsForDish(oneString, twoString, threeString));
                            //conflictsList.add(ingredientName + " из блюда " + dishName + " + " + orderIngredientName + " из блюда " + dishOrderItem.getDishName() + " = сильно конфликтуют");
                        }

                        if(result == 3) {
                            if(partConflictsList.size() < 1) {
                                oneString = dishName + ":";
                                twoString = dishOrderItem.getDishName() + ":";
                                threeString = "Конфликтуют";

                                partConflictsList.add(new ConflictsForDish(oneString, twoString, threeString));
                            }

                            oneString = ingredientName;
                            twoString = orderIngredientName;
                            threeString = "средне";

                            partConflictsList.add(new ConflictsForDish(oneString, twoString, threeString));

                            //conflictsList.add(ingredientName + " из блюда " + dishName + " + " + orderIngredientName + " из блюда " + dishOrderItem.getDishName() + " = умеренно конфликтуют");
                        }
                    }
                }

                if(partConflictsList.size() > 0) {
                    oneString = " ";
                    twoString = " ";
                    threeString = " ";

                    partConflictsList.add(new ConflictsForDish(oneString, twoString, threeString));

                    conflictsList.addAll(partConflictsList);
                }
            }
        }

        return true;
    }

    // получение данных по содержащимся ингредиентам совместимости, вегетарианства, пост, диабет
    private int[] getCompatibilities(int ingredientId) {
        DatabaseCreateHelper databaseCreateHelper = new DatabaseCreateHelper(getApplicationContext());
        SQLiteDatabase db;
        try {
            db = databaseCreateHelper.getWritableDatabase();
        } catch (SQLiteException ex) {
            db = databaseCreateHelper.getReadableDatabase();
        }

        int[] compatibilityIntArray = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};

        if(ingredientId != 0) {
            String[] projection = {
                    IngredientsDBHelper.COLUMN_DESCRIPTION,
                    IngredientsDBHelper.COLUMN_COMPATIBILITY_1,
                    IngredientsDBHelper.COLUMN_COMPATIBILITY_2,
                    IngredientsDBHelper.COLUMN_COMPATIBILITY_3,
                    IngredientsDBHelper.COLUMN_COMPATIBILITY_4,
                    IngredientsDBHelper.COLUMN_COMPATIBILITY_5,
                    IngredientsDBHelper.COLUMN_COMPATIBILITY_6,
                    IngredientsDBHelper.COLUMN_COMPATIBILITY_7,
                    IngredientsDBHelper.COLUMN_COMPATIBILITY_8,
                    IngredientsDBHelper.COLUMN_COMPATIBILITY_9,
                    IngredientsDBHelper.COLUMN_COMPATIBILITY_10,
                    IngredientsDBHelper.COLUMN_COMPATIBILITY_11,
                    IngredientsDBHelper.COLUMN_COMPATIBILITY_12,
                    IngredientsDBHelper.COLUMN_COMPATIBILITY_13,
                    IngredientsDBHelper.COLUMN_COMPATIBILITY_14,
                    IngredientsDBHelper.COLUMN_COMPATIBILITY_15,
                    IngredientsDBHelper.COLUMN_COMPATIBILITY_16
            };

            String selection = IngredientsDBHelper.COLUMN_ID + " = ?";
            String[] selectionArgs = {"" + ingredientId};

            Cursor cursor = db.query(
                    IngredientsDBHelper.TABLE,
                    projection,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    null
            );

            if (cursor.moveToFirst()) {
                do {
                    orderIngredientName = cursor.getString(cursor.getColumnIndexOrThrow(IngredientsDBHelper.COLUMN_DESCRIPTION));

                    compatibilityIntArray[0] = cursor.getInt(cursor.getColumnIndexOrThrow(IngredientsDBHelper.COLUMN_COMPATIBILITY_1));
                    compatibilityIntArray[1] = cursor.getInt(cursor.getColumnIndexOrThrow(IngredientsDBHelper.COLUMN_COMPATIBILITY_2));
                    compatibilityIntArray[2] = cursor.getInt(cursor.getColumnIndexOrThrow(IngredientsDBHelper.COLUMN_COMPATIBILITY_3));
                    compatibilityIntArray[3] = cursor.getInt(cursor.getColumnIndexOrThrow(IngredientsDBHelper.COLUMN_COMPATIBILITY_4));
                    compatibilityIntArray[4] = cursor.getInt(cursor.getColumnIndexOrThrow(IngredientsDBHelper.COLUMN_COMPATIBILITY_5));
                    compatibilityIntArray[5] = cursor.getInt(cursor.getColumnIndexOrThrow(IngredientsDBHelper.COLUMN_COMPATIBILITY_6));
                    compatibilityIntArray[6] = cursor.getInt(cursor.getColumnIndexOrThrow(IngredientsDBHelper.COLUMN_COMPATIBILITY_7));
                    compatibilityIntArray[7] = cursor.getInt(cursor.getColumnIndexOrThrow(IngredientsDBHelper.COLUMN_COMPATIBILITY_8));
                    compatibilityIntArray[8] = cursor.getInt(cursor.getColumnIndexOrThrow(IngredientsDBHelper.COLUMN_COMPATIBILITY_9));
                    compatibilityIntArray[9] = cursor.getInt(cursor.getColumnIndexOrThrow(IngredientsDBHelper.COLUMN_COMPATIBILITY_10));
                    compatibilityIntArray[10] = cursor.getInt(cursor.getColumnIndexOrThrow(IngredientsDBHelper.COLUMN_COMPATIBILITY_11));
                    compatibilityIntArray[11] = cursor.getInt(cursor.getColumnIndexOrThrow(IngredientsDBHelper.COLUMN_COMPATIBILITY_12));
                    compatibilityIntArray[12] = cursor.getInt(cursor.getColumnIndexOrThrow(IngredientsDBHelper.COLUMN_COMPATIBILITY_13));
                    compatibilityIntArray[13] = cursor.getInt(cursor.getColumnIndexOrThrow(IngredientsDBHelper.COLUMN_COMPATIBILITY_14));
                    compatibilityIntArray[14] = cursor.getInt(cursor.getColumnIndexOrThrow(IngredientsDBHelper.COLUMN_COMPATIBILITY_15));
                    compatibilityIntArray[15] = cursor.getInt(cursor.getColumnIndexOrThrow(IngredientsDBHelper.COLUMN_COMPATIBILITY_16));
                } while (cursor.moveToNext());
            }
            cursor.close();
        }

        db.close();

        return compatibilityIntArray;
    }

    // получение данных по содержащимся ингредиентам совместимости, вегетарианства, пост, диабет
    private void getConflictsOfIngredientFromDB(int[] ingredientArray) {
        DatabaseCreateHelper databaseCreateHelper = new DatabaseCreateHelper(getApplicationContext());
        SQLiteDatabase db;
        try {
            db = databaseCreateHelper.getWritableDatabase();
        } catch (SQLiteException ex) {
            db = databaseCreateHelper.getReadableDatabase();
        }

        // цикл

        //int[] ingredientArray = new int[20];

        for(int ingredientID : ingredientArray){
            if(ingredientID != 0) {
                    //Log.d("MyLogListDishes","Ингредиент ID - " + ingredientID);

                 String[] projection = {
                            IngredientsDBHelper.COLUMN_DESCRIPTION,
                            IngredientsDBHelper.COLUMN_COMPATIBILITY_1,
                            IngredientsDBHelper.COLUMN_COMPATIBILITY_2,
                            IngredientsDBHelper.COLUMN_COMPATIBILITY_3,
                            IngredientsDBHelper.COLUMN_COMPATIBILITY_4,
                            IngredientsDBHelper.COLUMN_COMPATIBILITY_5,
                            IngredientsDBHelper.COLUMN_COMPATIBILITY_6,
                            IngredientsDBHelper.COLUMN_COMPATIBILITY_7,
                            IngredientsDBHelper.COLUMN_COMPATIBILITY_8,
                            IngredientsDBHelper.COLUMN_COMPATIBILITY_9,
                            IngredientsDBHelper.COLUMN_COMPATIBILITY_10,
                            IngredientsDBHelper.COLUMN_COMPATIBILITY_11,
                            IngredientsDBHelper.COLUMN_COMPATIBILITY_12,
                            IngredientsDBHelper.COLUMN_COMPATIBILITY_13,
                            IngredientsDBHelper.COLUMN_COMPATIBILITY_14,
                            IngredientsDBHelper.COLUMN_COMPATIBILITY_15,
                            IngredientsDBHelper.COLUMN_COMPATIBILITY_16
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


                    int[] compatibilityIntArray = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};

                    if (cursor.moveToFirst()) {
                        do {
                            ingredientName = cursor.getString(cursor.getColumnIndexOrThrow(IngredientsDBHelper.COLUMN_DESCRIPTION));

                            compatibilityIntArray[0] = cursor.getInt(cursor.getColumnIndexOrThrow(IngredientsDBHelper.COLUMN_COMPATIBILITY_1));
                            compatibilityIntArray[1] = cursor.getInt(cursor.getColumnIndexOrThrow(IngredientsDBHelper.COLUMN_COMPATIBILITY_2));
                            compatibilityIntArray[2] = cursor.getInt(cursor.getColumnIndexOrThrow(IngredientsDBHelper.COLUMN_COMPATIBILITY_3));
                            compatibilityIntArray[3] = cursor.getInt(cursor.getColumnIndexOrThrow(IngredientsDBHelper.COLUMN_COMPATIBILITY_4));
                            compatibilityIntArray[4] = cursor.getInt(cursor.getColumnIndexOrThrow(IngredientsDBHelper.COLUMN_COMPATIBILITY_5));
                            compatibilityIntArray[5] = cursor.getInt(cursor.getColumnIndexOrThrow(IngredientsDBHelper.COLUMN_COMPATIBILITY_6));
                            compatibilityIntArray[6] = cursor.getInt(cursor.getColumnIndexOrThrow(IngredientsDBHelper.COLUMN_COMPATIBILITY_7));
                            compatibilityIntArray[7] = cursor.getInt(cursor.getColumnIndexOrThrow(IngredientsDBHelper.COLUMN_COMPATIBILITY_8));
                            compatibilityIntArray[8] = cursor.getInt(cursor.getColumnIndexOrThrow(IngredientsDBHelper.COLUMN_COMPATIBILITY_9));
                            compatibilityIntArray[9] = cursor.getInt(cursor.getColumnIndexOrThrow(IngredientsDBHelper.COLUMN_COMPATIBILITY_10));
                            compatibilityIntArray[10] = cursor.getInt(cursor.getColumnIndexOrThrow(IngredientsDBHelper.COLUMN_COMPATIBILITY_11));
                            compatibilityIntArray[11] = cursor.getInt(cursor.getColumnIndexOrThrow(IngredientsDBHelper.COLUMN_COMPATIBILITY_12));
                            compatibilityIntArray[12] = cursor.getInt(cursor.getColumnIndexOrThrow(IngredientsDBHelper.COLUMN_COMPATIBILITY_13));
                            compatibilityIntArray[13] = cursor.getInt(cursor.getColumnIndexOrThrow(IngredientsDBHelper.COLUMN_COMPATIBILITY_14));
                            compatibilityIntArray[14] = cursor.getInt(cursor.getColumnIndexOrThrow(IngredientsDBHelper.COLUMN_COMPATIBILITY_15));
                            compatibilityIntArray[15] = cursor.getInt(cursor.getColumnIndexOrThrow(IngredientsDBHelper.COLUMN_COMPATIBILITY_16));
                        } while (cursor.moveToNext());
                    }
                    cursor.close();

                checkConflicts(compatibilityIntArray);

                }
            }

        if(compatibilityType == 0) {
            conflictsList.add(new ConflictsForDish("Нет проверки на конфликтность", "", ""));
        }

        if(conflictsList.size() == 0) {
            conflictsList.add(new ConflictsForDish("Блюдо не конфликтует",  "", ""));
        }

        db.close();

            //Log.d("MyLogListDishes"," - " + ingredientID);
        }


    private int getSimpleCompatibilityEvaluation(int[] a, int[] b) {
        int result = 4;

        boolean[] as = {false, false, false, false, false};
        boolean[] bs = {false, false, false, false, false};

        if (a[0] > 0 || a[2] > 0 || a[11] > 0 || a[13] > 0 || a[14]>0 || a[15]>0) {
            as[0] = true;
        }

        if (a[1] > 0 || a[5] > 0 || a[6] > 0) {
            as[1] = true;
        }

        if (a[3] > 0 || a[12] > 0) {
            as[2] = true;
        }

        if (a[4] > 0 || a[9] > 0 || a[10] > 0) {
            as[3] = true;
        }

        if (a[7] > 0 || a[8] > 0) {
            as[4] = true;
        }

        if (b[0] > 0 || b[2] > 0 || b[11] > 0 || b[13] > 0 || b[14]>0 || b[15]>0) {
            bs[0] = true;
        }

        if (b[1] > 0 || b[5] > 0 || b[6] > 0) {
            bs[1] = true;
        }

        if (b[3] > 0 || b[12] > 0) {
            bs[2] = true;
        }

        if (b[4] > 0 || b[9] > 0 || b[10] > 0) {
            bs[3] = true;
        }

        if (b[7] > 0 || b[8] > 0) {
            bs[4] = true;
        }


        if(as[0]&&bs[1] || as[1]&&bs[0] || as[1]&&bs[2] || as[2]&&bs[1] || as[2]&&bs[3] || as[3]&&bs[2]) {
            result = 3;
        }

        if(as[0]&&bs[2] || as[0]&&bs[4] || as[2]&&bs[0] || as[4]&&bs[0]) {
            result = 2;
        }


        return result;
    }

    private int getSheltonCompatibilityEvaluation(int[] a, int[] b) {
        int[][] compatibilityOfElementsArray = {
                {0,2,2,2,2,2,2,2,2,4,3,2,2,2,2,2},
                {2,0,2,4,4,2,3,2,2,4,4,2,2,2,2,2},
                {2,2,0,3,2,2,4,4,2,4,4,3,2,3,2,2},
                {2,4,3,0,3,2,4,4,3,4,4,2,4,3,3,2},
                {2,4,2,3,0,2,4,4,3,4,4,2,2,2,2,4},
                {2,2,2,2,2,0,2,2,2,4,2,2,2,2,2,2},
                {2,3,4,4,4,2,0,2,2,4,4,2,2,3,2,3},
                {2,2,4,4,4,2,2,0,3,4,3,2,3,4,2,4},
                {2,2,2,3,3,2,2,3,0,4,3,3,4,2,2,3},
                {4,4,4,4,4,4,4,4,4,0,4,2,4,4,4,4},
                {3,4,4,4,4,2,4,3,3,4,0,3,4,4,3,4},
                {2,2,3,2,2,2,2,2,3,2,3,0,2,2,2,2},
                {2,2,2,4,2,2,2,3,4,4,4,2,0,4,2,4},
                {2,2,3,3,2,2,3,4,2,4,4,2,4,0,2,3},
                {2,2,2,3,2,2,2,2,2,4,3,2,2,2,0,2},
                {2,3,2,2,4,2,3,4,3,4,4,2,4,3,2,0}};

        int result = 4;
        for(int i = 0; i < 16; i++) {
            if(a[i] != 0) {
                for(int n = 0; n < 16; n++) {
                    if(b[n] != 0) {
                        int x = compatibilityOfElementsArray[i][n];
                        //Log.i("MyLogListDishes", "x = " + x + "\n");
                        if(x == 3) {
                            result = 3;
                        }
                        if(x == 2) {
                            return 2;
                        }
                    }
                }
            }
        }

        return result;
    }

    /*private boolean checkConflicts(int[] compatibilityArray, String dishName) {
        if(compatibilityType == 0) {
            conflictsList.add("Нет проверки на конфликтность");
        }

        if(compatibilityType == 1) {
            for(DishOrderItem dishOrderItem : ChoiceOfDishesActivity.dishOrderList) {
                int result = getSimpleCompatibilityEvaluation(compatibilityArray, dishOrderItem.getCompatibilityArray());

                if(result == 2) {
                    conflictsList.add(dishName + " + " + dishOrderItem.getDishName() + " = сильно конфликтуют");
                }

                if(result == 3) {
                    conflictsList.add(dishName + " + " + dishOrderItem.getDishName() + " = умеренно конфликтуют");
                }
            }
        }

        if(compatibilityType == 2) {
            for(DishOrderItem dishOrderItem : ChoiceOfDishesActivity.dishOrderList) {
                int result = getSheltonCompatibilityEvaluation(compatibilityArray, dishOrderItem.getCompatibilityArray());

                Log.d("MyLogAboutDish", "result - " + result);

                if(result == 2) {
                    conflictsList.add(dishName + " + " + dishOrderItem.getDishName() + " = сильно конфликтуют");
                }

                if(result == 3) {
                    conflictsList.add(dishName + " + " + dishOrderItem.getDishName() + " = умеренно конфликтуют");
                }
            }
        }

        if(conflictsList.size() == 0) {
            conflictsList.add("Блюдо не конфликтует");
        }

        return true;
    }*/
}

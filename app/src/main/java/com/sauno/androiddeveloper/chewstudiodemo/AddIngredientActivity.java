package com.sauno.androiddeveloper.chewstudiodemo;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.sauno.androiddeveloper.chewstudiodemo.adapter.AddIngredientAdapter;
import com.sauno.androiddeveloper.chewstudiodemo.data.DatabaseCreateHelper;
import com.sauno.androiddeveloper.chewstudiodemo.data.IngredientsDBHelper;
import com.sauno.androiddeveloper.chewstudiodemo.model.Ingredient;

import java.util.ArrayList;
import java.util.List;

public class AddIngredientActivity extends AppCompatActivity {
    private CheckBox diabetesCheckBox;
    private CheckBox compatibilityCheckBox;
    private CheckBox lentenCheckBox;
    private CheckBox vegetarianCheckBox;

    public static boolean isCheckedDiabetesCheckBox;
    public static boolean isCheckedCompatibilityCheckBox;
    public static boolean isCheckedLentenCheckBox;
    public static boolean isCheckedVegetarianCheckBox;

    private TextView countCaloriesTextView;
    private TextView countProteinsTextView;
    private TextView countFatsTextView;
    private TextView countCarbohydratesTextView;
    private TextView countXETextView;

    EditText searchEditText;

    public int sumCalories;
    public int sumProteins;
    public int sumFats;
    public int sumCarbs;
    public int sumXE;

    public List<Ingredient> ingredientList = new ArrayList<>();

    RecyclerView ingredientsRecyclerView;
    RecyclerView.Adapter mAdapter;
    RecyclerView.LayoutManager mLayoutManager;

    //совместимость из анкеты
    int compatibilityType;

    //таблица совместимости элементов
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_ingredient);

        diabetesCheckBox = findViewById(R.id.diabetesCheckBox);
        compatibilityCheckBox = findViewById(R.id.compatibilityCheckBox);
        lentenCheckBox = findViewById(R.id.lentenCheckBox);
        vegetarianCheckBox = findViewById(R.id.vegetarianCheckBox);

        checkPreferences();

        getIngredientsFromDB();

        countCaloriesTextView = findViewById(R.id.countCaloriesTextView);
        countProteinsTextView = findViewById(R.id.countProteinsTextView);
        countFatsTextView = findViewById(R.id.countFatsTextView);
        countCarbohydratesTextView = findViewById(R.id.countCarbohydratesTextView);
        countXETextView = findViewById(R.id.countXETextView);

        searchEditText = findViewById(R.id.searchEditText);

        searchEditText.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {}

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                String search = s.toString();
                setSearch(search);
            }
        });

        ingredientsRecyclerView = findViewById(R.id.ingredientsRecyclerView);
        Button saveButton = findViewById(R.id.saveButton);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*AddDishActivity.sumCalories = AddDishActivity.sumCalories + sumCalories;
                AddDishActivity.sumProteins = AddDishActivity.sumProteins + sumProteins;
                AddDishActivity.sumFats = AddDishActivity.sumFats + sumFats;
                AddDishActivity.sumCarbs = AddDishActivity.sumCarbs + sumCarbs;
                AddDishActivity.sumXE = AddDishActivity.sumXE + sumXE;*/

                /*for(String ingredient : ingredientsList) {
                    AddDishActivity.ingredientsList.add(ingredient);
                }*/

                finish();
            }
        });


        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        ingredientsRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        ingredientsRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        mAdapter = new AddIngredientAdapter(this, ingredientList, mLayoutManager);
        ingredientsRecyclerView.setAdapter(mAdapter);

        setupActionBar();

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        String str = sp.getString(UserPreferencesActivity.KEY_USER_COMPATIBILITY_TYPE_OF_FOOD_PREF, "0");
        compatibilityType = Integer.valueOf(str);

        //Log.d("MeLogAddIngredients", "compatibility  -  " + compatibilityType);

        diabetesCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                restartAdapter();
            }
        });

        compatibilityCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                restartAdapter();
            }
        });

        lentenCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                restartAdapter();
            }
        });

        vegetarianCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                restartAdapter();
            }
        });


    }

    private void checkPreferences() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        String compatibilityType = sp.getString(UserPreferencesActivity.KEY_USER_COMPATIBILITY_TYPE_OF_FOOD_PREF, "0");

        String vegetarian = sp.getString(UserPreferencesActivity.KEY_USER_TYPE_OF_FOOD_VEGETARIAN_PREF, "0");
        String lenten = sp.getString(UserPreferencesActivity.KEY_USER_TYPE_OF_FOOD_LENTEN_PREF, "0");
        String characteristicOne = sp.getString(UserPreferencesActivity.KEY_USER_CHARACTERISTIC_ONE_PREF, "");
        String characteristicTwo = sp.getString(UserPreferencesActivity.KEY_USER_CHARACTERISTIC_TWO_PREF, "");

        /*Log.d("MeLogAddIngredients", "compatibilityType  -  " + compatibilityType);
        Log.d("MeLogAddIngredients", "vegetarian  -  " + vegetarian);
        Log.d("MeLogAddIngredients", "lenten  -  " + lenten);
        Log.d("MeLogAddIngredients", "characteristicOne  -  " + characteristicOne);
        Log.d("MeLogAddIngredients", "characteristicTwo  -  " + characteristicTwo);*/


        if(compatibilityType.equals("0")) {
            compatibilityCheckBox.setVisibility(View.GONE);
            isCheckedCompatibilityCheckBox = false;
        }

        if(vegetarian.equals("0")) {
            vegetarianCheckBox.setVisibility(View.GONE);
            isCheckedVegetarianCheckBox = false;
        }

        if(lenten.equals("0")) {
            lentenCheckBox.setVisibility(View.GONE);
            isCheckedLentenCheckBox = false;
        }

        if(characteristicOne.equals("") && characteristicTwo.equals("") ) {
            diabetesCheckBox.setVisibility(View.GONE);
            isCheckedDiabetesCheckBox = false;
        }

        compatibilityCheckBox.setChecked(isCheckedCompatibilityCheckBox);
        vegetarianCheckBox.setChecked(isCheckedVegetarianCheckBox);
        lentenCheckBox.setChecked(isCheckedLentenCheckBox);
        diabetesCheckBox.setChecked(isCheckedDiabetesCheckBox);
    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setTitle("Ингредиенты");
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setElevation(0);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_host, menu);

        /*mainMenu = menu;

        SharedPreferences mSharedPreferences = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);

        if (mSharedPreferences.getBoolean("isConnected", false))                                                                        // Если есть привязка к BLE-сервису, то
        {
            mainMenu.findItem(R.id.action_device).setIcon(R.drawable.chew_logo);
        } else {
            mainMenu.findItem(R.id.action_device).setIcon(R.drawable.black_logo);
        }
*/
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }

        /*if(id == R.id.action_device) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            SearchDevicesFragment searchFragment = new SearchDevicesFragment();
            searchDevicesFragment = searchFragment;
            fragmentTransaction.add(R.id.homeMenuActivity, searchFragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
            return true;
        }*/

        return super.onOptionsItemSelected(item);
    }

    private void getIngredientsFromDB() {
        DatabaseCreateHelper databaseCreateHelper = new DatabaseCreateHelper(getApplicationContext());
        SQLiteDatabase db;
        try {
            db = databaseCreateHelper.getWritableDatabase();
        }
        catch (SQLiteException ex){
            db = databaseCreateHelper.getReadableDatabase();
        }

        String[] projection = {
                IngredientsDBHelper.COLUMN_ID,
                IngredientsDBHelper.COLUMN_DESCRIPTION,
                IngredientsDBHelper.COLUMN_CALORIC,
                IngredientsDBHelper.COLUMN_PROTEINS,
                IngredientsDBHelper.COLUMN_FATS,
                IngredientsDBHelper.COLUMN_CARBS,
                IngredientsDBHelper.COLUMN_XE,
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
                IngredientsDBHelper.COLUMN_COMPATIBILITY_16,
                IngredientsDBHelper.COLUMN_LENTEN,
                IngredientsDBHelper.COLUMN_DIABETES,
                IngredientsDBHelper.COLUMN_VEGETARIAN
        };


        Cursor cursor = db.query(
                true,
                IngredientsDBHelper.TABLE,
                projection,
                null,
                null,
                null,
                null,
                null,
                null
        );

        //набор совместимостей
        int c1, c2, c3, c4, c5, c6, c7, c8, c9 ,c10 ,c11, c12, c13, c14, c15, c16;

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(IngredientsDBHelper.COLUMN_ID));

                String name = cursor.getString(cursor.getColumnIndexOrThrow(IngredientsDBHelper.COLUMN_DESCRIPTION));

                int calories = cursor.getInt(cursor.getColumnIndexOrThrow(IngredientsDBHelper.COLUMN_CALORIC));
                int proteins = cursor.getInt(cursor.getColumnIndexOrThrow(IngredientsDBHelper.COLUMN_PROTEINS));
                int fats = cursor.getInt(cursor.getColumnIndexOrThrow(IngredientsDBHelper.COLUMN_FATS));
                int carbs = cursor.getInt(cursor.getColumnIndexOrThrow(IngredientsDBHelper.COLUMN_CARBS));
                int xe = cursor.getInt(cursor.getColumnIndexOrThrow(IngredientsDBHelper.COLUMN_XE));

                c1 = cursor.getInt(cursor.getColumnIndexOrThrow(IngredientsDBHelper.COLUMN_COMPATIBILITY_1));
                c2 = cursor.getInt(cursor.getColumnIndexOrThrow(IngredientsDBHelper.COLUMN_COMPATIBILITY_2));
                c3 = cursor.getInt(cursor.getColumnIndexOrThrow(IngredientsDBHelper.COLUMN_COMPATIBILITY_3));
                c4 = cursor.getInt(cursor.getColumnIndexOrThrow(IngredientsDBHelper.COLUMN_COMPATIBILITY_4));
                c5 = cursor.getInt(cursor.getColumnIndexOrThrow(IngredientsDBHelper.COLUMN_COMPATIBILITY_5));
                c6 = cursor.getInt(cursor.getColumnIndexOrThrow(IngredientsDBHelper.COLUMN_COMPATIBILITY_6));
                c7 = cursor.getInt(cursor.getColumnIndexOrThrow(IngredientsDBHelper.COLUMN_COMPATIBILITY_7));
                c8 = cursor.getInt(cursor.getColumnIndexOrThrow(IngredientsDBHelper.COLUMN_COMPATIBILITY_8));
                c9 = cursor.getInt(cursor.getColumnIndexOrThrow(IngredientsDBHelper.COLUMN_COMPATIBILITY_9));
                c10 = cursor.getInt(cursor.getColumnIndexOrThrow(IngredientsDBHelper.COLUMN_COMPATIBILITY_10));
                c11 = cursor.getInt(cursor.getColumnIndexOrThrow(IngredientsDBHelper.COLUMN_COMPATIBILITY_11));
                c12 = cursor.getInt(cursor.getColumnIndexOrThrow(IngredientsDBHelper.COLUMN_COMPATIBILITY_12));
                c13 = cursor.getInt(cursor.getColumnIndexOrThrow(IngredientsDBHelper.COLUMN_COMPATIBILITY_13));
                c14 = cursor.getInt(cursor.getColumnIndexOrThrow(IngredientsDBHelper.COLUMN_COMPATIBILITY_14));
                c15 = cursor.getInt(cursor.getColumnIndexOrThrow(IngredientsDBHelper.COLUMN_COMPATIBILITY_15));
                c16 = cursor.getInt(cursor.getColumnIndexOrThrow(IngredientsDBHelper.COLUMN_COMPATIBILITY_16));

                int[] compatibilityIntArray = {c1, c2, c3, c4, c5, c6, c7, c8, c9 ,c10 ,c11, c12, c13, c14, c15, c16};

                int compatibilityEvaluation = 4;

                /*if(compatibilityType == 1) {

                }

                if(compatibilityType == 2) {
                    compatibilityEvaluation = getSheltonCompatibilityEvaluation(compatibilityIntArray);
                }*/

                int vegetarian = cursor.getInt(cursor.getColumnIndexOrThrow(IngredientsDBHelper.COLUMN_VEGETARIAN));
                int lenten = cursor.getInt(cursor.getColumnIndexOrThrow(IngredientsDBHelper.COLUMN_LENTEN));
                String diabetes = cursor.getString(cursor.getColumnIndexOrThrow(IngredientsDBHelper.COLUMN_DIABETES));

                Ingredient ingredient = new Ingredient(id, name, calories, proteins, fats, carbs, xe, 100, compatibilityEvaluation, compatibilityIntArray, vegetarian, lenten, diabetes);
                ingredientList.add(ingredient);

            }while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
    }

    /*private int getSheltonCompatibilityEvaluation(int[] compatibilityIntArray) {
        int result = 4;
        int x;

        for(int n = 0 ; n < 16 ; n++) {
            if(compatibilityIntArray[n] == 1) {
                for(int k = n + 1 ; k < 16 ; k++) {
                    if(compatibilityIntArray[k] == 1) {
                        x = compatibilityOfElementsArray[n][k];
                        //Log.i("MyLogListDishes", "n = " + n + "  k = " + k + "  x = " + x);
                        if(x == 3) {
                            result = 3;
                        }
                        if(x == 2) {
                            result = 2;
                            n = 16;
                            k = 16;
                        }
                    }
                }
            }
        }

        return result;
    }*/


    @Override
    protected void onResume() {
        super.onResume();

        /*InputMethodManager inputMethodManager = (InputMethodManager) this.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);*/

        sumCalories = 0;
        sumProteins = 0;
        sumFats = 0;
        sumCarbs = 0;
        sumXE = 0;

        for(Ingredient ingredient : AddDishActivity.chosenIngredientsList) {
            sumCalories = sumCalories + (int)((float)((ingredient.getCalories() * ingredient.getGrams()) / 100));
            sumProteins = sumProteins + (int)((float)((ingredient.getProteins() * ingredient.getGrams()) / 100));
            sumFats = sumFats + (int)((float)((ingredient.getFats() * ingredient.getGrams()) / 100));
            sumCarbs = sumCarbs + (int)((float)((ingredient.getCarbs() * ingredient.getGrams()) / 100));
            sumXE = sumXE + (int)((float)((ingredient.getXe() * ingredient.getGrams()) / 100));
        }

        setSumsCPFCX();
    }

    public void addIngredient(Ingredient ingredient, Parcelable recylerViewState) {
        sumCalories = sumCalories + (int)((float)((ingredient.getCalories() * ingredient.getGrams()) / 100));
        sumProteins = sumProteins + (int)((float)((ingredient.getProteins() * ingredient.getGrams()) / 100));
        sumFats = sumFats + (int)((float)((ingredient.getFats() * ingredient.getGrams()) / 100));
        sumCarbs = sumCarbs + (int)((float)((ingredient.getCarbs() * ingredient.getGrams()) / 100));
        sumXE = sumXE + (int)((float)((ingredient.getXe() * ingredient.getGrams()) / 100));

        AddDishActivity.chosenIngredientsList.add(ingredient);

        setSumsCPFCX();

        recheckEvaluation();

        ingredientsRecyclerView.removeAllViews();

        List<Ingredient> checkedIngredientList = deleteFromListChecked();

        //Реализация чтобы список не отматывался
        mAdapter = new AddIngredientAdapter(this, checkedIngredientList, mLayoutManager);

        ingredientsRecyclerView.setAdapter(mAdapter);

        if(recylerViewState != null) {
            ingredientsRecyclerView.getLayoutManager().onRestoreInstanceState(recylerViewState);
        }
    }

    public void removeIngredient(Ingredient ingredient,  Parcelable recylerViewState) {
        sumCalories = sumCalories - (int)((float)((ingredient.getCalories() * ingredient.getGrams()) / 100));
        sumProteins = sumProteins - (int)((float)((ingredient.getProteins() * ingredient.getGrams()) / 100));
        sumFats = sumFats - (int)((float)((ingredient.getFats() * ingredient.getGrams()) / 100));
        sumCarbs = sumCarbs - (int)((float)((ingredient.getCarbs() * ingredient.getGrams()) / 100));
        sumXE = sumXE - (int)((float)((ingredient.getXe() * ingredient.getGrams()) / 100));

        for(Ingredient chosenIngredient : AddDishActivity.chosenIngredientsList) {
            if(chosenIngredient.getName().equals(ingredient.getName())) {
                AddDishActivity.chosenIngredientsList.remove(chosenIngredient);
                break;
            }
        }

        setSumsCPFCX();

        recheckEvaluation();

        List<Ingredient> checkedIngredientList = deleteFromListChecked();

        ingredientsRecyclerView.removeAllViews();

        //Реализация чтобы список не отматывался
        mAdapter = new AddIngredientAdapter(this, checkedIngredientList, mLayoutManager);
        ingredientsRecyclerView.setAdapter(mAdapter);

        if(recylerViewState != null) {
            ingredientsRecyclerView.getLayoutManager().onRestoreInstanceState(recylerViewState);
        }
    }

    private void setSumsCPFCX() {


        countCaloriesTextView.setText("" + sumCalories);
        countProteinsTextView.setText(sumProteins + "г");
        countFatsTextView.setText(sumFats + "г");
        countCarbohydratesTextView.setText(sumCarbs + "г");
        countXETextView.setText("" + sumXE);
    }

    private void recheckEvaluation() {
        for (Ingredient ingredient : ingredientList) {
            ingredient.setCompatibilityEvaluation(4);
        }


        if(compatibilityType == 0) {
            return;
        }

        if(compatibilityType == 1) {
            for(Ingredient chosenIngredient : AddDishActivity.chosenIngredientsList) {
                for (Ingredient ingredient : ingredientList) {
                    int currentCompatibility = ingredient.getCompatibilityEvaluation();
                    int result = getSimpleCompatibilityEvaluation(chosenIngredient.getCompatibilityArray(), ingredient.getCompatibilityArray());
                    if(result < currentCompatibility) {
                        ingredient.setCompatibilityEvaluation(result);
                    }
                }
            }
        }

        if(compatibilityType == 2) {
            for(Ingredient chosenIngredient : AddDishActivity.chosenIngredientsList) {
                for (Ingredient ingredient : ingredientList) {
                    int currentCompatibility = ingredient.getCompatibilityEvaluation();
                    int result = getSheltonCompatibilityEvaluation(chosenIngredient.getCompatibilityArray(), ingredient.getCompatibilityArray());

                    Log.d("MyLogAddIngredient", "название - " + ingredient.getName() + "  evaluation - " + ingredient.getCompatibilityArray()[6]);

                    if(result < currentCompatibility) {
                        ingredient.setCompatibilityEvaluation(result);
                    }
                }
            }
        }

        if(AddDishActivity.chosenIngredientsList.size() == 1) {
            AddDishActivity.chosenIngredientsList.get(0).setCompatibilityEvaluation(4);
        }
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

    /*public int konf_Var2(string str1, string str2)
    {
        int ozen = 1;

        if ((str1 == "мясо, рыба, птица (постные)") || (str1 == "хлеб,крупы, картофель") || (str1 == "творог, кисло-молочные продукты") || (str1 == "овощи зеленые и некрахмальные") || (str1 == "фрукты сладкие, сухофрукты"))
            if ((str2 == "мясо, рыба, птица (постные)") || (str2 == "хлеб,крупы, картофель") || (str2 == "творог, кисло-молочные продукты") || (str2 == "овощи зеленые и некрахмальные") || (str2 == "фрукты сладкие, сухофрукты"))
                if (str1 == str2) { ozen = 1; }
                else if (((str1 == "мясо, рыба, птица (постные)") && (str2 == "хлеб,крупы, картофель")) || ((str1 == "хлеб,крупы, картофель") && (str2 == "мясо, рыба, птица (постные)"))) { ozen = 0; }
                else if (((str1 == "мясо, рыба, птица (постные)") && (str2 == "творог, кисло-молочные продукты")) || ((str1 == "творог, кисло-молочные продукты") && (str2 == "мясо, рыба, птица (постные)"))) { ozen = -1; }
                else if (((str1 == "мясо, рыба, птица (постные)") && (str2 == "овощи зеленые и некрахмальные")) || ((str1 == "овощи зеленые и некрахмальные") && (str2 == "мясо, рыба, птица (постные)"))) { ozen = 1; }
                else if (((str1 == "мясо, рыба, птица (постные)") && (str2 == "фрукты сладкие, сухофрукты")) || ((str1 == "фрукты сладкие, сухофрукты") && (str2 == "мясо, рыба, птица (постные)"))) { ozen = -1; }

                else if (((str1 == "хлеб,крупы, картофель") && (str2 == "творог, кисло-молочные продукты")) || ((str1 == "творог, кисло-молочные продукты") && (str2 == "хлеб,крупы, картофель"))) { ozen = 0; }
                else if (((str1 == "хлеб,крупы, картофель") && (str2 == "овощи зеленые и некрахмальные")) || ((str1 == "овощи зеленые и некрахмальные") && (str2 == "хлеб,крупы, картофель"))) { ozen = 1; }
                else if (((str1 == "хлеб,крупы, картофель") && (str2 == "фрукты сладкие, сухофрукты")) || ((str1 == "фрукты сладкие, сухофрукты") && (str2 == "хлеб,крупы, картофель"))) { ozen = 1; }

                else if (((str1 == "творог, кисло-молочные продукты") && (str2 == "овощи зеленые и некрахмальные")) || ((str1 == "овощи зеленые и некрахмальные") && (str2 == "творог, кисло-молочные продукты"))) { ozen = 0; }
                else if (((str1 == "творог, кисло-молочные продукты") && (str2 == "фрукты сладкие, сухофрукты")) || ((str1 == "фрукты сладкие, сухофрукты") && (str2 == "творог, кисло-молочные продукты"))) { ozen = 1; }

                else if (((str1 == "овощи зеленые и некрахмальные") && (str2 == "фрукты сладкие, сухофрукты")) || ((str1 == "фрукты сладкие, сухофрукты") && (str2 == "овощи зеленые и некрахмальные"))) { ozen = 1; }

        return ozen;
    }*/

    /*private int checkCompatibility(int position) {
        int result = 4;
        if(ChoiceOfDishesActivity.dishOrderList.size() > 0) {
            for(int i = 0; i < ChoiceOfDishesActivity.dishOrderList.size(); i++) {
                if(!dishNames[position].equals(ChoiceOfDishesActivity.dishOrderList.get(i).getDishName())) {
                    int x = getSheltonCompatibilityEvaluation(compatibilityArraysList.get(position),
                            ChoiceOfDishesActivity.dishOrderList.get(i).getCompatibilityArray());
                    //Log.i("MyLogListDishes", "checkCompatibility x = " + x + "\n");
                    if(x == 3) {
                        result = 3;
                    }
                    if(x == 2) {
                        return 2;
                    }
                }
            }
        }
        return result;
    }*/

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
                        Log.i("MyLogListDishes", "x = " + x + "\n");
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

    //реализация поиска
    private void setSearch(String search) {
        List<Ingredient> checkedIngredientList = deleteFromListChecked();

        List<Ingredient> searchIngredientList = new ArrayList<>();

        int searchSize = search.length();

        for(Ingredient ingredient : checkedIngredientList) {
            if(search.length() > ingredient.getName().length()) {
                continue;
            }

            String subString = ingredient.getName().substring(0, searchSize);

            if(search.equalsIgnoreCase(subString)) {
                searchIngredientList.add(ingredient);
            }
        }

        ingredientsRecyclerView.removeAllViews();

        mAdapter = new AddIngredientAdapter(this, searchIngredientList, mLayoutManager);
        ingredientsRecyclerView.setAdapter(mAdapter);

        //restartAdapter(searchIngredientList);

        //searchEditText.setKeyboardNavigationCluster(false);

    }

    private void restartAdapter() {
        List<Ingredient> checkedIngredientList = deleteFromListChecked();

        ingredientsRecyclerView.removeAllViews();

        mAdapter = new AddIngredientAdapter(this, checkedIngredientList, mLayoutManager);
        ingredientsRecyclerView.setAdapter(mAdapter);
    }

    private List<Ingredient> deleteFromListChecked() {
        isCheckedDiabetesCheckBox = diabetesCheckBox.isChecked();
        isCheckedCompatibilityCheckBox = compatibilityCheckBox.isChecked();
        isCheckedLentenCheckBox = lentenCheckBox.isChecked();
        isCheckedVegetarianCheckBox = vegetarianCheckBox.isChecked();

        List<Ingredient> checkedIngredientList = new ArrayList<>();

        //checkedIngredientList = ingredientList;

        for(int n = 0; n < ingredientList.size(); n++) {
            if(isCheckedDiabetesCheckBox) {
                if(ingredientList.get(n).getDiabetes().equals("нельзя")) {
                    continue;
                }
            }
            if(isCheckedCompatibilityCheckBox) {
                if(ingredientList.get(n).getCompatibilityEvaluation() == 2) {
                    continue;
                }
            }
            if(isCheckedLentenCheckBox) {
                String lentenString = "" + ingredientList.get(n).getLenten();
                if(lentenString.equals("0")) {
                    continue;
                }
            }
            if(isCheckedVegetarianCheckBox) {
                String vegetarianString = "" + ingredientList.get(n).getVegetarian();
                if(vegetarianString.equals("0")) {
                    continue;
                }
            }

            checkedIngredientList.add(ingredientList.get(n));
        }

        //ingredientList = checkedIngredientList;

        return checkedIngredientList;
    }
}


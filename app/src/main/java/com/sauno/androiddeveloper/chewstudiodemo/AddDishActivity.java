package com.sauno.androiddeveloper.chewstudiodemo;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Canvas;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.sauno.androiddeveloper.chewstudiodemo.adapter.AddDishAdapter;
import com.sauno.androiddeveloper.chewstudiodemo.data.DatabaseCreateHelper;
import com.sauno.androiddeveloper.chewstudiodemo.data.DishDBHelper;
import com.sauno.androiddeveloper.chewstudiodemo.model.Ingredient;
import com.sauno.androiddeveloper.chewstudiodemo.utility.RecyclerItemTouchHelperForAddDish;

import java.util.ArrayList;
import java.util.List;

public class AddDishActivity extends AppCompatActivity implements RecyclerItemTouchHelperForAddDish.RecyclerItemTouchHelperListener {
    EditText dishNameEditText;

    private TextView countCaloriesTextView;
    private TextView countProteinsTextView;
    private TextView countFatsTextView;
    private TextView countCarbohydratesTextView;
    private TextView countXETextView;

    public static int sumCalories;
    public static int sumProteins;
    public static int sumFats;
    public static int sumCarbs;
    public static int sumXE;

    public static List<Ingredient> chosenIngredientsList = new ArrayList<>();

    RecyclerView ingredientsRecyclerView;
    RecyclerView.Adapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_dish);

        setupActionBar();

        dishNameEditText = findViewById(R.id.dishNameEditText);


        Button addIngredientsButton = findViewById(R.id.addIngredientsButton);

        addIngredientsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AddIngredientActivity.class);
                startActivity(intent);
            }
        });

        Button saveButton = findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveToDataBase();
            }
        });

        countCaloriesTextView = findViewById(R.id.countCaloriesTextView);
        countProteinsTextView = findViewById(R.id.countProteinsTextView);
        countFatsTextView = findViewById(R.id.countFatsTextView);
        countCarbohydratesTextView = findViewById(R.id.countCarbohydratesTextView);
        countXETextView = findViewById(R.id.countXETextView);

        ingredientsRecyclerView = findViewById(R.id.ingredientsRecyclerView);

        ingredientsRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        ingredientsRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        mAdapter = new AddDishAdapter(this);
        ingredientsRecyclerView.setAdapter(mAdapter);

        //подкючение обработчика свайпа для удаления
        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerItemTouchHelperForAddDish(0, ItemTouchHelper.RIGHT, this);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(ingredientsRecyclerView);

        ItemTouchHelper.SimpleCallback itemTouchHelperCallback1 = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                // Row is swiped from recycler view
                // remove it from adapter

            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };

        // attaching the touch helper to recycler view
        new ItemTouchHelper(itemTouchHelperCallback1).attachToRecyclerView(ingredientsRecyclerView);
    }

    //обработка скайпа для удаления
    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if (viewHolder instanceof AddDishAdapter.ViewHolder) {
            Ingredient ingredient = chosenIngredientsList.get(viewHolder.getAdapterPosition());

            removeIngredient(ingredient);
        }
    }

    //удаление ингридиента из списка выбранных
    public void removeIngredient(Ingredient ingredient) {
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

        restartAdapter();
    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setTitle("Добавление блюда");
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

    @Override
    protected void onResume() {
        super.onResume();

        /*InputMethodManager inputMethodManager = (InputMethodManager) this.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);*/

        restartAdapter();
        setSumsCPFCX();
    }

    //запись нового значения грамм
    public void restartData(int grams) {
        SharedPreferences mSharedPreferences = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        int ingredientItem = mSharedPreferences.getInt("ingredientItem", 0);

        chosenIngredientsList.get(ingredientItem).setGrams(grams);
        restartAdapter();
    }

    public void restartAdapter() {
        ingredientsRecyclerView.removeAllViews();
        mAdapter = new AddDishAdapter(this);
        ingredientsRecyclerView.setAdapter(mAdapter);

        setSumsCPFCX();
    }

    private void setSumsCPFCX() {
        sumCalories = 0;
        sumProteins = 0;
        sumFats = 0;
        sumCarbs = 0;
        sumXE = 0;

        for(Ingredient ingredient : chosenIngredientsList) {
            sumCalories = sumCalories + (int)((float)((ingredient.getCalories() * ingredient.getGrams()) / 100));
            sumProteins = sumProteins + (int)((float)((ingredient.getProteins() * ingredient.getGrams()) / 100));
            sumFats = sumFats + (int)((float)((ingredient.getFats() * ingredient.getGrams()) / 100));
            sumCarbs = sumCarbs + (int)((float)((ingredient.getCarbs() * ingredient.getGrams()) / 100));
            sumXE = sumXE + (int)((float)((ingredient.getXe() * ingredient.getGrams()) / 100));
        }

        countCaloriesTextView.setText("" + sumCalories);
        countProteinsTextView.setText(sumProteins + "г");
        countFatsTextView.setText(sumFats + "г");
        countCarbohydratesTextView.setText(sumCarbs + "г");
        countXETextView.setText("" + sumXE);
    }

    //сохранение нового блюда в базе данных
    private void saveToDataBase() {
        String nameDish = dishNameEditText.getText().toString();

        if(nameDish.length() > 1 && chosenIngredientsList.size() > 0) {
            DatabaseCreateHelper databaseCreateHelper = new DatabaseCreateHelper(getApplicationContext());
            SQLiteDatabase db = databaseCreateHelper.getWritableDatabase();

            int weight = getWeightSum();

            int[] ingredientsIdArray = new int[20];
            int[] ingredientsWeightArray = new int[20];

            for(int i = 0; i < 20; i++) {
                if(i < chosenIngredientsList.size()) {
                    ingredientsIdArray[i] = chosenIngredientsList.get(i).getId();
                    ingredientsWeightArray[i] = chosenIngredientsList.get(i).getGrams();
                } else {
                    ingredientsIdArray[i] = 0;
                    ingredientsWeightArray[i] = 0;
                }
            }


            ContentValues newDishValue = new ContentValues();

            newDishValue.put(DishDBHelper.COLUMN_DESCRIPTION, nameDish);
            newDishValue.put(DishDBHelper.COLUMN_CALORIC, sumCalories);
            newDishValue.put(DishDBHelper.COLUMN_PROTEINS, sumProteins);
            newDishValue.put(DishDBHelper.COLUMN_FATS, sumFats);
            newDishValue.put(DishDBHelper.COLUMN_CARBS, sumCarbs);
            newDishValue.put(DishDBHelper.COLUMN_XE, sumXE);
            newDishValue.put(DishDBHelper.COLUMN_WEIGHT, weight);
            newDishValue.put(DishDBHelper.COLUMN_RESTAURANT, 11);

            newDishValue.put(DishDBHelper.COLUMN_INGREDS_1, ingredientsIdArray[0]);
            newDishValue.put(DishDBHelper.COLUMN_INGREDS_2, ingredientsIdArray[1]);
            newDishValue.put(DishDBHelper.COLUMN_INGREDS_3, ingredientsIdArray[2]);
            newDishValue.put(DishDBHelper.COLUMN_INGREDS_4, ingredientsIdArray[3]);
            newDishValue.put(DishDBHelper.COLUMN_INGREDS_5, ingredientsIdArray[4]);
            newDishValue.put(DishDBHelper.COLUMN_INGREDS_6, ingredientsIdArray[5]);
            newDishValue.put(DishDBHelper.COLUMN_INGREDS_7, ingredientsIdArray[6]);
            newDishValue.put(DishDBHelper.COLUMN_INGREDS_8, ingredientsIdArray[7]);
            newDishValue.put(DishDBHelper.COLUMN_INGREDS_9, ingredientsIdArray[8]);
            newDishValue.put(DishDBHelper.COLUMN_INGREDS_10, ingredientsIdArray[9]);
            newDishValue.put(DishDBHelper.COLUMN_INGREDS_11, ingredientsIdArray[10]);
            newDishValue.put(DishDBHelper.COLUMN_INGREDS_12, ingredientsIdArray[11]);
            newDishValue.put(DishDBHelper.COLUMN_INGREDS_13, ingredientsIdArray[12]);
            newDishValue.put(DishDBHelper.COLUMN_INGREDS_14, ingredientsIdArray[13]);
            newDishValue.put(DishDBHelper.COLUMN_INGREDS_15, ingredientsIdArray[14]);
            newDishValue.put(DishDBHelper.COLUMN_INGREDS_16, ingredientsIdArray[15]);
            newDishValue.put(DishDBHelper.COLUMN_INGREDS_17, ingredientsIdArray[16]);
            newDishValue.put(DishDBHelper.COLUMN_INGREDS_18, ingredientsIdArray[17]);
            newDishValue.put(DishDBHelper.COLUMN_INGREDS_19, ingredientsIdArray[18]);
            newDishValue.put(DishDBHelper.COLUMN_INGREDS_20, ingredientsIdArray[19]);
            newDishValue.put(DishDBHelper.COLUMN_INGREDS_1_WEIGHT, ingredientsWeightArray[0]);
            newDishValue.put(DishDBHelper.COLUMN_INGREDS_2_WEIGHT, ingredientsWeightArray[1]);
            newDishValue.put(DishDBHelper.COLUMN_INGREDS_3_WEIGHT, ingredientsWeightArray[2]);
            newDishValue.put(DishDBHelper.COLUMN_INGREDS_4_WEIGHT, ingredientsWeightArray[3]);
            newDishValue.put(DishDBHelper.COLUMN_INGREDS_5_WEIGHT, ingredientsWeightArray[4]);
            newDishValue.put(DishDBHelper.COLUMN_INGREDS_6_WEIGHT, ingredientsWeightArray[5]);
            newDishValue.put(DishDBHelper.COLUMN_INGREDS_7_WEIGHT, ingredientsWeightArray[6]);
            newDishValue.put(DishDBHelper.COLUMN_INGREDS_8_WEIGHT, ingredientsWeightArray[7]);
            newDishValue.put(DishDBHelper.COLUMN_INGREDS_9_WEIGHT, ingredientsWeightArray[8]);
            newDishValue.put(DishDBHelper.COLUMN_INGREDS_10_WEIGHT, ingredientsWeightArray[9]);
            newDishValue.put(DishDBHelper.COLUMN_INGREDS_11_WEIGHT, ingredientsWeightArray[10]);
            newDishValue.put(DishDBHelper.COLUMN_INGREDS_12_WEIGHT, ingredientsWeightArray[11]);
            newDishValue.put(DishDBHelper.COLUMN_INGREDS_13_WEIGHT, ingredientsWeightArray[12]);
            newDishValue.put(DishDBHelper.COLUMN_INGREDS_14_WEIGHT, ingredientsWeightArray[13]);
            newDishValue.put(DishDBHelper.COLUMN_INGREDS_15_WEIGHT, ingredientsWeightArray[14]);
            newDishValue.put(DishDBHelper.COLUMN_INGREDS_16_WEIGHT, ingredientsWeightArray[15]);
            newDishValue.put(DishDBHelper.COLUMN_INGREDS_17_WEIGHT, ingredientsWeightArray[16]);
            newDishValue.put(DishDBHelper.COLUMN_INGREDS_18_WEIGHT, ingredientsWeightArray[17]);
            newDishValue.put(DishDBHelper.COLUMN_INGREDS_19_WEIGHT, ingredientsWeightArray[18]);
            newDishValue.put(DishDBHelper.COLUMN_INGREDS_20_WEIGHT, ingredientsWeightArray[19]);


            long check = db.insert(DishDBHelper.TABLE,
                    null,
                    newDishValue);

            db.close();

            dishNameEditText.setText("");
            chosenIngredientsList.clear();
            restartAdapter();
            setSumsCPFCX();

            Toast.makeText(this, "Сохранено", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Добавьте ингредиенты в ваше блюдо", Toast.LENGTH_SHORT).show();
        }

    }

    //получение веса блюда
    private int getWeightSum() {
        int sum = 0;
        for(Ingredient ingredient : chosenIngredientsList) {
            sum = sum + ingredient.getGrams();
        }
        return sum;
    }
}

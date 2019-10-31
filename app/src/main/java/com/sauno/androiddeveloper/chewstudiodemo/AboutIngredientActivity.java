package com.sauno.androiddeveloper.chewstudiodemo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.sauno.androiddeveloper.chewstudiodemo.adapter.ConflictsAdapter;
import com.sauno.androiddeveloper.chewstudiodemo.model.Ingredient;

import java.util.ArrayList;

public class AboutIngredientActivity extends AppCompatActivity {
    //совместимость из анкеты
    int compatibilityType;

    ArrayList<String> conflictsList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_ingredient);

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        String str = sp.getString(UserPreferencesActivity.KEY_USER_COMPATIBILITY_TYPE_OF_FOOD_PREF, "0");
        compatibilityType = Integer.valueOf(str);

        Intent intent = getIntent();

        Ingredient ingredient = (Ingredient)intent.getExtras().getSerializable("ingredient");

        TextView ingredientNameTextView = findViewById(R.id.ingredientNameTextView);
        TextView lentenTextView = findViewById(R.id.lentenTextView);
        TextView diabetesTextView = findViewById(R.id.diabetesTextView);
        TextView checkCompatibilityTextView = findViewById(R.id.checkCompatibilityTextView);

        TextView countCaloriesTextView = findViewById(R.id.countCaloriesTextView);
        TextView countProteinsTextView = findViewById(R.id.countProteinsTextView);
        TextView countFatsTextView = findViewById(R.id.countFatsTextView);
        TextView countCarbohydratesTextView = findViewById(R.id.countCarbohydratesTextView);
        TextView countXETextView = findViewById(R.id.countXETextView);

        ingredientNameTextView.setText(ingredient.getName());

        if(ingredient.getLenten() == 0) {
            lentenTextView.setText("Не постное");
        } else {
            lentenTextView.setText("Постное");
        }

        diabetesTextView.setText("Диабетическое: " + ingredient.getDiabetes());

        countCaloriesTextView.setText("" + ingredient.getCalories());
        countProteinsTextView.setText(ingredient.getProteins() + "г");
        countFatsTextView.setText(ingredient.getFats() + "г");
        countCarbohydratesTextView.setText(ingredient.getCarbs() + "г");
        countXETextView.setText("" + ingredient.getXe());

        setupActionBar();

        checkConflicts(ingredient);

        RecyclerView conflictsRecyclerView = findViewById(R.id.conflictsRecyclerView);

        conflictsRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        conflictsRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        ConflictsAdapter mAdapter = new ConflictsAdapter(conflictsList);
        conflictsRecyclerView.setAdapter(mAdapter);
    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setTitle("Ингредиент");
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

    private void checkConflicts(Ingredient ingredient) {
        if(compatibilityType == 0) {
            conflictsList.add("Нет проверки на конфликтность");
        }

        if(compatibilityType == 1) {
            for(Ingredient chosenIngredient : AddDishActivity.chosenIngredientsList) {
                int result = getSimpleCompatibilityEvaluation(chosenIngredient.getCompatibilityArray(), ingredient.getCompatibilityArray());

                if(result == 2) {
                    conflictsList.add(ingredient.getName() + " + " + chosenIngredient.getName() + " = сильно конфликтуют");
                }

                if(result == 3) {
                    conflictsList.add(ingredient.getName() + " + " + chosenIngredient.getName() + " = умеренно конфликтуют");
                }
            }
        }

        if(compatibilityType == 2) {
            for(Ingredient chosenIngredient : AddDishActivity.chosenIngredientsList) {
                int result = getSheltonCompatibilityEvaluation(chosenIngredient.getCompatibilityArray(), ingredient.getCompatibilityArray());

                if(result == 2) {
                    conflictsList.add(ingredient.getName() + " + " + chosenIngredient.getName() + " = сильно конфликтуют");
                }

                if(result == 3) {
                    conflictsList.add(ingredient.getName() + " + " + chosenIngredient.getName() + " = умеренно конфликтуют");
                }
            }
        }

        if(conflictsList.size() == 0) {
            conflictsList.add("Ингредиент не конфликтует");
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

}

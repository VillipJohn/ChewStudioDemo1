package com.sauno.androiddeveloper.chewstudiodemo.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sauno.androiddeveloper.chewstudiodemo.AboutDishActivity;
import com.sauno.androiddeveloper.chewstudiodemo.ChoiceOfDishesActivity;
import com.sauno.androiddeveloper.chewstudiodemo.ListDishesActivity;
import com.sauno.androiddeveloper.chewstudiodemo.R;
import com.sauno.androiddeveloper.chewstudiodemo.UserPreferencesActivity;
import com.sauno.androiddeveloper.chewstudiodemo.UserProfileActivity;
import com.sauno.androiddeveloper.chewstudiodemo.data.DatabaseCreateHelper;
import com.sauno.androiddeveloper.chewstudiodemo.data.DishDBHelper;
import com.sauno.androiddeveloper.chewstudiodemo.model.DishOrderItem;

import java.math.BigDecimal;
import java.util.List;

//вспомогательный класс для отображения списка на экране ListDishesActivity
public class ListDishesAdapter extends RecyclerView.Adapter<ListDishesAdapter.ViewHolder> {

    // список id блюд
    private List<Integer> idDishesList;

    // список названий блюд
    private List<String> dishNamesList;

    // список колличества блюда, выбранных блюд
    private List<BigDecimal> dishOrderQuantityList;

    // список globalCategory
    private List<String> globalCategoryList;

    // список массивов совместимости из базы
    private List<int[]> compatibilityArraysList;

    // список массивов ID ингредиентов
    private List<int[]> ingredientArrayList;

    //необходимый массив для корректного отображения галочек
    private boolean[] checked;

    // вспомогательный класс для работы с базой данных
    private DatabaseCreateHelper databaseCreateHelper;

    // текущее блюдо
    private String dishName;

    // колличество отображаемых
    private BigDecimal countCalories, countProteins, countFats, countCarbohydrates, countXE;

    // объект в Activity
    private LinearLayout containerCPFCXLinearLayout;

    private TextView countCaloriesInListDishesTextView;
    private TextView countProteinsInListDishesTextView;
    private TextView countFatsInListDishesTextView;
    private TextView countCarbohydratesInListDishesTextView;
    private TextView countXEInListDishesTextView;

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

    private ListDishesActivity listDishesActivity;

    private boolean isExistUserCPFC;

    private int currentUserPartOfCalories;
    private int currentUserPartOfProteins;
    private int currentUserPartOfFats;
    private int currentUserPartOfCarbohydrates;
    private int currentUserPartOfXE;

    private boolean isCheckedCalories;
    private boolean isCheckedProteins;
    private boolean isCheckedFats;
    private boolean isCheckedCarbs;
    private boolean isCheckedSheltonCompatibility;

    //объект необходимый для корректного отображения списка при пересоздании
    private RecyclerView.LayoutManager mLayoutManager;

    // конструктор
    public ListDishesAdapter(Context context,
                             List<Integer> idDishesList,
                             List<String> dishNamesList,
                             List<BigDecimal> dishOrderQuantityList,
                             List<int[]> compatibilityArraysList,
                             List<String> globalCategoryList,
                             List<int[]> ingredientArrayList,
                             RecyclerView.LayoutManager mLayoutManager) {
        this.mLayoutManager = mLayoutManager;

        this.idDishesList = idDishesList;
        this.dishNamesList = dishNamesList;
        this.dishOrderQuantityList = dishOrderQuantityList;
        checked = new boolean[dishNamesList.size()];

        this.compatibilityArraysList = compatibilityArraysList;
        this.globalCategoryList = globalCategoryList;
        this.ingredientArrayList = ingredientArrayList;

        listDishesActivity = (ListDishesActivity)context;

        containerCPFCXLinearLayout = listDishesActivity.findViewById(R.id.containerCPFCXLinearLayout);

        countCaloriesInListDishesTextView = listDishesActivity.findViewById(R.id.countCaloriesInListDishesTextView);
        countProteinsInListDishesTextView = listDishesActivity.findViewById(R.id.countProteinsInListDishesTextView);
        countFatsInListDishesTextView = listDishesActivity.findViewById(R.id.countFatsInListDishesTextView);
        countCarbohydratesInListDishesTextView = listDishesActivity.findViewById(R.id.countCarbohydratesInListDishesTextView);
        countXEInListDishesTextView = listDishesActivity.findViewById(R.id.countXETextView);

        normCaloriesTextView = listDishesActivity.findViewById(R.id.normCaloriesTextView);
        normProteinsTextView = listDishesActivity.findViewById(R.id.normProteinsTextView);
        normFatsTextView = listDishesActivity.findViewById(R.id.normFatsTextView);
        normCarbsTextView = listDishesActivity.findViewById(R.id.normCarbsTextView);
        normXETextView = listDishesActivity.findViewById(R.id.normXETextView);

        aboveCaloriesTextView = listDishesActivity.findViewById(R.id.aboveCaloriesTextView);
        aboveProteinsTextView = listDishesActivity.findViewById(R.id.aboveProteinsTextView);
        aboveFatsTextView = listDishesActivity.findViewById(R.id.aboveFatsTextView);
        aboveCarbohydratesTextView = listDishesActivity.findViewById(R.id.aboveCarbohydratesTextView);
        aboveXETextView = listDishesActivity.findViewById(R.id.aboveXETextView);

        //mSharedPreferences = listDishesActivity.getSharedPreferences(listDishesActivity.getString(R.string.preference_file_key), Context.MODE_PRIVATE);

        listDishesActivity.sumOrderCalories = new BigDecimal(0.00).setScale(0, BigDecimal.ROUND_DOWN);
        listDishesActivity.sumOrderProteins = new BigDecimal(0.00).setScale(0, BigDecimal.ROUND_DOWN);
        listDishesActivity.sumOrderFats = new BigDecimal(0.00).setScale(0, BigDecimal.ROUND_DOWN);
        listDishesActivity.sumOrderCarbs = new BigDecimal(0.00).setScale(0, BigDecimal.ROUND_DOWN);
        listDishesActivity.sumOrderXE = new BigDecimal(0.00).setScale(0, BigDecimal.ROUND_DOWN);

        //проверка существуют ли сохранённые текущие данные КБЖУХ
        checkIfExistUserCPFC();

        if(isExistUserCPFC) {
            //получение текущих сохранённые КБЖУХ
            getCurrentUserCPFC();
        }

        //получение настроек отслеживания КБЖУХ
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        isCheckedCalories = sharedPreferences.getBoolean(UserPreferencesActivity.KEY_CALORIE_CHECK_BOX_PREF, false);
        isCheckedProteins = sharedPreferences.getBoolean(UserPreferencesActivity.KEY_PROTEIN_CHECK_BOX_PREF, false);
        isCheckedFats = sharedPreferences.getBoolean(UserPreferencesActivity.KEY_FAT_CHECK_BOX_PREF, false);
        isCheckedCarbs = sharedPreferences.getBoolean(UserPreferencesActivity.KEY_CARBOHYDRATE_CHECK_BOX_PREF, false);
        String compatibilityPrefString = sharedPreferences.getString(UserPreferencesActivity.KEY_USER_COMPATIBILITY_TYPE_OF_FOOD_PREF, "0");
        int compatibilityPref = Integer.parseInt(compatibilityPrefString);

        if(compatibilityPref == 2) {
            isCheckedSheltonCompatibility = true;
        } else {
            isCheckedSheltonCompatibility = false;
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public CheckBox mDishCheckBox;
        public TextView mDishCountTextView;

        public TextView mDishNameTextView;
        public ImageView mDishImageView;

        public ViewHolder(View view) {
            super(view);
            mDishCheckBox = view.findViewById(R.id.dishCheckBox);
            mDishCountTextView = view.findViewById(R.id.dishCountTextView);

            mDishNameTextView = view.findViewById(R.id.dishNameTextView);
            mDishImageView = view.findViewById(R.id.dishImageView);
        }

    }



    // Create new views (invoked by the layout manager)
    @Override
    public ListDishesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_home_menu, parent, false);

        ViewHolder vh = new ViewHolder(view);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        if(dishOrderQuantityList.get(position).equals(new BigDecimal(0).setScale(2, BigDecimal.ROUND_DOWN))) {
            holder.mDishCheckBox.setChecked(false);
        } else {
            checked[position] = true;
            holder.mDishCheckBox.setChecked(true);

            //добавление и отображение сумм КБЖУХ
            addCPFC();
        }

        dishName = dishNamesList.get(position);

        int evaluationForSetImageColor = setEvaluationForSetImageColor(position);

        setImageColor(holder.mDishImageView, evaluationForSetImageColor, position);

        setData();

        holder.mDishNameTextView.setText(dishName);

        //обработка нажатия чекбокса
        holder.mDishCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checked[position] = !checked[position];

                dishName = dishNamesList.get(position);

                getDataFromDB(dishName);

                //если блюда не было в списке, то добавляется общее КБЖУ, колличество блюд, и само блюдо в список
                if(checked[position]) {
                    ChoiceOfDishesActivity.sumOrderCalories = ChoiceOfDishesActivity.sumOrderCalories.add(countCalories);
                    ChoiceOfDishesActivity.sumOrderProteins = ChoiceOfDishesActivity.sumOrderProteins.add(countProteins);
                    ChoiceOfDishesActivity.sumOrderFats = ChoiceOfDishesActivity.sumOrderFats.add(countFats);
                    ChoiceOfDishesActivity.sumOrderCarbs = ChoiceOfDishesActivity.sumOrderCarbs.add(countCarbohydrates);
                    ChoiceOfDishesActivity.sumOrderXE = ChoiceOfDishesActivity.sumOrderXE.add(countXE);

                    ChoiceOfDishesActivity.countChosenDishes++;

                    //listDishesActivity.dishOrderQuantityList.get(position).add(new BigDecimal(1).setScale(2, BigDecimal.ROUND_DOWN));
                    ChoiceOfDishesActivity.dishOrderList.add(new DishOrderItem(idDishesList.get(position), dishName,
                            new BigDecimal(1).setScale(2, BigDecimal.ROUND_DOWN),
                            countCalories, countProteins, countFats, countCarbohydrates, countXE, compatibilityArraysList.get(position),
                            ListDishesActivity.chosenCategory, ingredientArrayList.get(position)));
                    //holder.mDishCheckBox.setChecked(true);

                } else {
                    ChoiceOfDishesActivity.sumOrderCalories = ChoiceOfDishesActivity.sumOrderCalories.subtract(countCalories);
                    ChoiceOfDishesActivity.sumOrderProteins = ChoiceOfDishesActivity.sumOrderProteins.subtract(countProteins);
                    ChoiceOfDishesActivity.sumOrderFats = ChoiceOfDishesActivity.sumOrderFats.subtract(countFats);
                    ChoiceOfDishesActivity.sumOrderCarbs = ChoiceOfDishesActivity.sumOrderCarbs.subtract(countCarbohydrates);
                    ChoiceOfDishesActivity.sumOrderXE = ChoiceOfDishesActivity.sumOrderXE.subtract(countXE);

                    ChoiceOfDishesActivity.countChosenDishes--;
                    //listDishesActivity.dishOrderQuantityList.get(position).subtract(new BigDecimal(1).setScale(2, BigDecimal.ROUND_DOWN));

                    for (int i = 0; i < ChoiceOfDishesActivity.dishOrderList.size(); i++) {
                        String dish = ChoiceOfDishesActivity.dishOrderList.get(i).getDishName();
                        if (dish.equals(dishName)) {
                            ChoiceOfDishesActivity.dishOrderList.remove(i);
                        }
                    }
                }

                Parcelable recylerViewState = mLayoutManager.onSaveInstanceState();

                listDishesActivity.setAdapter(recylerViewState);
            }
        });

        //обработка длительного нажатие на название блюда и открытие нового окна с подробными данными об этом блюде
        holder.mDishNameTextView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                Intent intent = new Intent(view.getContext(), AboutDishActivity.class);

                int idDish = idDishesList.get(position);
                intent.putExtra("idDish", idDish);

                String dishName = dishNamesList.get(position);
                intent.putExtra("dishName", dishName);

                intent.putExtra("compatibilityArray", compatibilityArraysList.get(position));

                intent.putExtra("ingredientArray", ingredientArrayList.get(position));

                intent.putExtra("lenten", listDishesActivity.checkedLentenArrayList.get(position));

                intent.putExtra("vegetarian", listDishesActivity.checkedVegetarianArrayList.get(position));

                listDishesActivity.startActivity(intent);

                return false;
            }
        });


    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
            return dishNamesList.size();
    }

    //Отображение на экране данных сумм КБЖУХ и соответственного выхода за лимиты
    private void setData() {
        int sumCaloriesInt = ChoiceOfDishesActivity.sumOrderCalories.intValue();
        int sumProteinsInt = ChoiceOfDishesActivity.sumOrderProteins.intValue();
        int sumFatsInt = ChoiceOfDishesActivity.sumOrderFats.intValue();
        int sumCarbsInt = ChoiceOfDishesActivity.sumOrderCarbs.intValue();
        int sumXEInt = ChoiceOfDishesActivity.sumOrderXE.intValue();

        countCaloriesInListDishesTextView.setText("" + sumCaloriesInt);
        countProteinsInListDishesTextView.setText(sumProteinsInt + "г");
        countFatsInListDishesTextView.setText(sumFatsInt + "г");
        countCarbohydratesInListDishesTextView.setText(sumCarbsInt + "г");
        countXEInListDishesTextView.setText("" + sumXEInt);

        if(isExistUserCPFC) {
            setAboveLimitColorForCPFC(sumCaloriesInt, sumProteinsInt, sumFatsInt, sumCarbsInt, sumXEInt);
        }
    }

    //добавление и отображение сумм КБЖУХ
    private void addCPFC() {
        int sumCaloriesInt = ChoiceOfDishesActivity.sumOrderCalories.intValue();
        int sumProteinsInt = ChoiceOfDishesActivity.sumOrderProteins.intValue();
        int sumFatsInt = ChoiceOfDishesActivity.sumOrderFats.intValue();
        int sumCarbsInt = ChoiceOfDishesActivity.sumOrderCarbs.intValue();
        int sumXEInt = ChoiceOfDishesActivity.sumOrderXE.intValue();

        countCaloriesInListDishesTextView.setText("" + sumCaloriesInt);
        countProteinsInListDishesTextView.setText(sumProteinsInt + "г");
        countFatsInListDishesTextView.setText(sumFatsInt + "г");
        countCarbohydratesInListDishesTextView.setText(sumCarbsInt + "г");
        countXEInListDishesTextView.setText("" + sumXEInt);
    }

    //получение данных с базы данных
    private void getDataFromDB(String dishNameDB) {
        databaseCreateHelper = new DatabaseCreateHelper(listDishesActivity);

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
        String selection = DishDBHelper.COLUMN_DESCRIPTION + " = ?";
        String[] selectionArgs = {dishNameDB};

        Cursor cursor = db.query(
                DishDBHelper.TABLE,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        if (cursor.moveToFirst()) {
            countCalories = new BigDecimal(cursor.getFloat(cursor.getColumnIndexOrThrow(DishDBHelper.COLUMN_CALORIC))).setScale(2, BigDecimal.ROUND_DOWN);
            countProteins = new BigDecimal(cursor.getFloat(cursor.getColumnIndexOrThrow(DishDBHelper.COLUMN_PROTEINS))).setScale(2, BigDecimal.ROUND_DOWN);
            countFats = new BigDecimal(cursor.getFloat(cursor.getColumnIndexOrThrow(DishDBHelper.COLUMN_FATS))).setScale(2, BigDecimal.ROUND_DOWN);
            countCarbohydrates = new BigDecimal(cursor.getFloat(cursor.getColumnIndexOrThrow(DishDBHelper.COLUMN_CARBS))).setScale(2, BigDecimal.ROUND_DOWN);
            countXE = new BigDecimal(cursor.getFloat(cursor.getColumnIndexOrThrow(DishDBHelper.COLUMN_XE))).setScale(2, BigDecimal.ROUND_DOWN);
        }

        cursor.close();
        db.close();
    }

    //проверка существуют ли сохранённые текущие данные КБЖУХ
    private void checkIfExistUserCPFC() {
        SharedPreferences mSharedPreferences = listDishesActivity.getSharedPreferences(listDishesActivity.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        int userDayCalories = mSharedPreferences.getInt(UserProfileActivity.KEY_COUNT_CALORIES, 0);

        if(userDayCalories == 0) {
            Toast.makeText(listDishesActivity, "Для подсчёта рекомендуемых Вам ежедневно калорий, белков, жиров, углеводов введите: пол, возраст, рост, вес, образ жизни", Toast.LENGTH_LONG).show();
            isExistUserCPFC = false;
        } else {
            isExistUserCPFC =true;
        }

    }

    //реализация отображения выхода за лимиты данных КБЖУХ
    private void setAboveLimitColorForCPFC(int sumCaloriesInt, int sumProteinsInt, int sumFatsInt, int sumCarbsInt, int sumXEInt) {
        boolean isAboveCalories, isAboveProteins, isAboveFats, isAboveCarbs, isAboveXE;
        if(currentUserPartOfCalories < sumCaloriesInt) {
            countCaloriesInListDishesTextView.setText("" + sumCaloriesInt);
            normCaloriesTextView.setText("" + currentUserPartOfCalories);
            aboveCaloriesTextView.setText("+" + (sumCaloriesInt - currentUserPartOfCalories));
            isAboveCalories = true;
        } else {
            normCaloriesTextView.setText("" + currentUserPartOfCalories);
            aboveCaloriesTextView.setText("");
            isAboveCalories = false;
        }

        if(currentUserPartOfProteins < sumProteinsInt) {
            countProteinsInListDishesTextView.setText("" + sumProteinsInt + "г");
            normProteinsTextView.setText("" + currentUserPartOfProteins + "г");
            aboveProteinsTextView.setText("+" + (sumProteinsInt - currentUserPartOfProteins) + "г");
            isAboveProteins = true;
        } else {
            aboveProteinsTextView.setText("");
            normProteinsTextView.setText("" + currentUserPartOfProteins + "г");
            isAboveProteins = false;
        }

        if(currentUserPartOfFats < sumFatsInt) {
            countFatsInListDishesTextView.setText("" + sumFatsInt + "г");
            normFatsTextView.setText("" + currentUserPartOfFats + "г");
            aboveFatsTextView.setText("+" + (sumFatsInt - currentUserPartOfFats) + "г");
            isAboveFats = true;
        } else {
            aboveFatsTextView.setText("");
            normFatsTextView.setText("" + currentUserPartOfFats + "г");
            isAboveFats = false;
        }

        if(currentUserPartOfCarbohydrates < sumCarbsInt) {
            countCarbohydratesInListDishesTextView.setText("" + sumCarbsInt + "г");
            normCarbsTextView.setText("" + currentUserPartOfCarbohydrates + "г");
            aboveCarbohydratesTextView.setText("+" + (sumCarbsInt - currentUserPartOfCarbohydrates) + "г");
            isAboveCarbs = true;
        } else {
            aboveCarbohydratesTextView.setText("");
            normCarbsTextView.setText("" + currentUserPartOfCarbohydrates + "г");
            isAboveCarbs = false;
        }

        if(sumXEInt > currentUserPartOfXE) {
            countXEInListDishesTextView.setText("" + sumXEInt);
            normXETextView.setText("" + currentUserPartOfXE);
            aboveXETextView.setText("+" + (sumXEInt - currentUserPartOfXE));
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
        float density = listDishesActivity.getResources()
                .getDisplayMetrics()
                .density;
        return Math.round((float) dp * density);
    }

    //получение текущих данных приёма пищи КБЖУХ
    private void getCurrentUserCPFC() {
        SharedPreferences mSharedPreferences = listDishesActivity.getSharedPreferences(listDishesActivity.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        currentUserPartOfCalories = mSharedPreferences.getInt("currentCalories", 0);
        currentUserPartOfProteins = mSharedPreferences.getInt("currentProteins", 0);
        currentUserPartOfFats = mSharedPreferences.getInt("currentFats", 0);
        currentUserPartOfCarbohydrates = mSharedPreferences.getInt("currentCarbs", 0);
        currentUserPartOfXE = mSharedPreferences.getInt("currentXE", 0);
    }



   //установка оценки совместимости
   private int setEvaluationForSetImageColor(int position) {
        int compatibilityEvaluation = checkCompatibility(position);
        //Log.i("MyLogListDishesAdapter", "compEval - " + compatibilityEvaluation + "\n");

        if(isCheckedSheltonCompatibility && compatibilityEvaluation == 2) {
            return 2;
        }

        if(isExistUserCPFC) {
            dishName = dishNamesList.get(position);
            databaseCreateHelper = new DatabaseCreateHelper(listDishesActivity);
            getDataFromDB(dishName);

            int sumAndCurrentCalories = ChoiceOfDishesActivity.sumOrderCalories.add(countCalories).intValue();
            int sumAndCurrentProteins = ChoiceOfDishesActivity.sumOrderProteins.add(countProteins).intValue();
            int sumAndCurrentFats = ChoiceOfDishesActivity.sumOrderFats.add(countFats).intValue();
            int sumAndCurrentCarbs = ChoiceOfDishesActivity.sumOrderCarbs.add(countCarbohydrates).intValue();

            if(isCheckedCalories && sumAndCurrentCalories > currentUserPartOfCalories) {
                return 2;
            }
            if(isCheckedProteins && sumAndCurrentProteins > currentUserPartOfProteins) {
                return 2;
            }
            if(isCheckedFats && sumAndCurrentFats > currentUserPartOfFats) {
                return 2;
            }
            if(isCheckedCarbs && sumAndCurrentCarbs > currentUserPartOfCarbohydrates) {
                return 2;
            }

            if(sumAndCurrentCalories > currentUserPartOfCalories) {
                return 3;
            }
            if(sumAndCurrentProteins > currentUserPartOfProteins) {
                return 3;
            }
            if(sumAndCurrentFats > currentUserPartOfFats) {
                return 3;
            }
            if(sumAndCurrentCarbs > currentUserPartOfCarbohydrates) {
                return 3;
            }

        } else if(isCheckedSheltonCompatibility && compatibilityEvaluation == 3) {
            return 3;
        }
        return 4;
   }

   //проверка совместимости и установка оценки совместимости
    private int checkCompatibility(int position) {
        int result = 4;
        if(ChoiceOfDishesActivity.dishOrderList.size() > 0) {
            for(int i = 0; i < ChoiceOfDishesActivity.dishOrderList.size(); i++) {
                if(!dishNamesList.get(position).equals(ChoiceOfDishesActivity.dishOrderList.get(i).getDishName())) {
                    int x = getCompatibilityEvaluation(compatibilityArraysList.get(position),
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
    }

    //получение оценки совместимости
    private int getCompatibilityEvaluation(int[] a, int[] b) {
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




    //установка цвета по оценки совместимости
   private void setImageColor(ImageView dishImageView, int evaluation, int position) {
        String globalCategory = globalCategoryList.get(position);

        switch(globalCategory) {
            case "супы" :
                if(evaluation == 2) {
                    dishImageView.setImageResource(R.drawable.soup_red);
                }
                if(evaluation == 3) {
                    dishImageView.setImageResource(R.drawable.soup_yellow);
                }
                if(evaluation == 4) {
                    dishImageView.setImageResource(R.drawable.soup_green);
                }
                break;
            case "горячие блюда из мяса" :
                if(evaluation == 2) {
                    dishImageView.setImageResource(R.drawable.hot_meat_dish_red);
                }
                if(evaluation == 3) {
                    dishImageView.setImageResource(R.drawable.hot_meat_dish_yellow);
                }
                if(evaluation == 4) {
                    dishImageView.setImageResource(R.drawable.hot_meat_dish_green);
                }
                break;
            case "горячие блюда из птицы" :
                if(evaluation == 2) {
                    dishImageView.setImageResource(R.drawable.hot_dish_of_poultry_red);
                }
                if(evaluation == 3) {
                    dishImageView.setImageResource(R.drawable.hot_dish_of_poultry_yellow);
                }
                if(evaluation == 4) {
                    dishImageView.setImageResource(R.drawable.hot_dish_of_poultry_green);
                }
                break;
            case "горячие блюда из рыбы и морепродуктов" :
                if(evaluation == 2) {
                    dishImageView.setImageResource(R.drawable.hot_fish_dish_red);
                }
                if(evaluation == 3) {
                    dishImageView.setImageResource(R.drawable.hot_fish_dish_yellow);
                }
                if(evaluation == 4) {
                    dishImageView.setImageResource(R.drawable.hot_fish_dish_green);
                }
                break;
            case "блюда из яиц" :
                if(evaluation == 2) {
                    dishImageView.setImageResource(R.drawable.egg_dish_red);
                }
                if(evaluation == 3) {
                    dishImageView.setImageResource(R.drawable.egg_dish_yellow);
                }
                if(evaluation == 4) {
                    dishImageView.setImageResource(R.drawable.egg_dish_green);
                }
                break;
            case "салаты" :
                if(evaluation == 2) {
                    dishImageView.setImageResource(R.drawable.salad_red);
                }
                if(evaluation == 3) {
                    dishImageView.setImageResource(R.drawable.salad_yellow);
                }
                if(evaluation == 4) {
                    dishImageView.setImageResource(R.drawable.salad_green);
                }
                break;
            case "гарниры" :
                if(evaluation == 2) {
                    dishImageView.setImageResource(R.drawable.garnish_red);
                }
                if(evaluation == 3) {
                    dishImageView.setImageResource(R.drawable.garnish_yellow);
                }
                if(evaluation == 4) {
                    dishImageView.setImageResource(R.drawable.garnish_green);
                }
                break;
            case "паста, спагетти, макароны, wok" :
                if(evaluation == 2) {
                    dishImageView.setImageResource(R.drawable.pasta_red);
                }
                if(evaluation == 3) {
                    dishImageView.setImageResource(R.drawable.pasta_yellow);
                }
                if(evaluation == 4) {
                    dishImageView.setImageResource(R.drawable.pasta_green);
                }
                break;
            case "плов, рис" :
                if(evaluation == 2) {
                    dishImageView.setImageResource(R.drawable.rice_red);
                }
                if(evaluation == 3) {
                    dishImageView.setImageResource(R.drawable.rice_yellow);
                }
                if(evaluation == 4) {
                    dishImageView.setImageResource(R.drawable.rice_green);
                }
                break;
            case "каши, мюсли" :
                if(evaluation == 2) {
                    dishImageView.setImageResource(R.drawable.kasha_red);
                }
                if(evaluation == 3) {
                    dishImageView.setImageResource(R.drawable.kasha_yellow);
                }
                if(evaluation == 4) {
                    dishImageView.setImageResource(R.drawable.kasha_green);
                }
                break;
            case "блины, оладьи, панкейки, драники" :
                if(evaluation == 2) {
                    dishImageView.setImageResource(R.drawable.pancakes_red);
                }
                if(evaluation == 3) {
                    dishImageView.setImageResource(R.drawable.pancakes_yellow);
                }
                if(evaluation == 4) {
                    dishImageView.setImageResource(R.drawable.pancakes_green);
                }
                break;
            case "сырники, запеканки" :
                if(evaluation == 2) {
                    dishImageView.setImageResource(R.drawable.cheesecake_red);
                }
                if(evaluation == 3) {
                    dishImageView.setImageResource(R.drawable.cheesecake_yellow);
                }
                if(evaluation == 4) {
                    dishImageView.setImageResource(R.drawable.cheesecake_green);
                }
                break;
            case "хлеб, булочки, багеты, чиабата, лепешки, лаваш " :
                if(evaluation == 2) {
                    dishImageView.setImageResource(R.drawable.bread_red);
                }
                if(evaluation == 3) {
                    dishImageView.setImageResource(R.drawable.bread_yellow);
                }
                if(evaluation == 4) {
                    dishImageView.setImageResource(R.drawable.bread_green);
                }
                break;
            case "вареники, пельмени, равиоли" :
                if(evaluation == 2) {
                    dishImageView.setImageResource(R.drawable.dumplings_red);
                }
                if(evaluation == 3) {
                    dishImageView.setImageResource(R.drawable.dumplings_yellow);
                }
                if(evaluation == 4) {
                    dishImageView.setImageResource(R.drawable.dumplings_green);
                }
                break;
            case "курзе, хинкал, манты" :
                if(evaluation == 2) {
                    dishImageView.setImageResource(R.drawable.khinkali_red);
                }
                if(evaluation == 3) {
                    dishImageView.setImageResource(R.drawable.khinkali_yellow);
                }
                if(evaluation == 4) {
                    dishImageView.setImageResource(R.drawable.khinkali_green);
                }
                break;
            case "хачапури, чуду, фокачча" :
                if(evaluation == 2) {
                    dishImageView.setImageResource(R.drawable.khachapuri_red);
                }
                if(evaluation == 3) {
                    dishImageView.setImageResource(R.drawable.khachapuri_yellow);
                }
                if(evaluation == 4) {
                    dishImageView.setImageResource(R.drawable.khachapuri_green);
                }
                break;
            case "голубцы, долма" :
                if(evaluation == 2) {
                    dishImageView.setImageResource(R.drawable.cabbage_rolls_red);
                }
                if(evaluation == 3) {
                    dishImageView.setImageResource(R.drawable.cabbage_rolls_yellow);
                }
                if(evaluation == 4) {
                    dishImageView.setImageResource(R.drawable.cabbage_rolls_green);
                }
                break;
            case "суши, сашими, роллы" :
                if(evaluation == 2) {
                    dishImageView.setImageResource(R.drawable.sushi_red);
                }
                if(evaluation == 3) {
                    dishImageView.setImageResource(R.drawable.sushi_yellow);
                }
                if(evaluation == 4) {
                    dishImageView.setImageResource(R.drawable.sushi_green);
                }
                break;
            case "сэндвичи, бургеры, бутерброды, буррито" :
                if(evaluation == 2) {
                    dishImageView.setImageResource(R.drawable.sandwich_red);
                }
                if(evaluation == 3) {
                    dishImageView.setImageResource(R.drawable.sandwich_yellow);
                }
                if(evaluation == 4) {
                    dishImageView.setImageResource(R.drawable.sandwich_green);
                }
                break;
            case "пироги, пирожки, чебуреки" :
                if(evaluation == 2) {
                    dishImageView.setImageResource(R.drawable.pie_red);
                }
                if(evaluation == 3) {
                    dishImageView.setImageResource(R.drawable.pie_yellow);
                }
                if(evaluation == 4) {
                    dishImageView.setImageResource(R.drawable.pie_green);
                }
                break;
            case "пицца, кальцоне" :
                if(evaluation == 2) {
                    dishImageView.setImageResource(R.drawable.pizza_red);
                }
                if(evaluation == 3) {
                    dishImageView.setImageResource(R.drawable.pizza_yellow);
                }
                if(evaluation == 4) {
                    dishImageView.setImageResource(R.drawable.pizza_green);
                }
                break;
            case "закуски холодные, соленья" :
                if(evaluation == 2) {
                    dishImageView.setImageResource(R.drawable.cold_snacks_red);
                }
                if(evaluation == 3) {
                    dishImageView.setImageResource(R.drawable.cold_snacks_yellow);
                }
                if(evaluation == 4) {
                    dishImageView.setImageResource(R.drawable.cold_snacks_green);
                }
                break;
            case "закуски горячие" :
                if(evaluation == 2) {
                    dishImageView.setImageResource(R.drawable.hot_snacks_red);
                }
                if(evaluation == 3) {
                    dishImageView.setImageResource(R.drawable.hot_snacks_yellow);
                }
                if(evaluation == 4) {
                    dishImageView.setImageResource(R.drawable.hot_snacks_green);
                }
                break;
            case "соусы" :
                if(evaluation == 2) {
                    dishImageView.setImageResource(R.drawable.sauce_red);
                }
                if(evaluation == 3) {
                    dishImageView.setImageResource(R.drawable.sauce_yellow);
                }
                if(evaluation == 4) {
                    dishImageView.setImageResource(R.drawable.sauce_green);
                }
                break;
            case "сметана, масло" :
                if(evaluation == 2) {
                    dishImageView.setImageResource(R.drawable.butter_red);
                }
                if(evaluation == 3) {
                    dishImageView.setImageResource(R.drawable.butter_yellow);
                }
                if(evaluation == 4) {
                    dishImageView.setImageResource(R.drawable.butter_green);
                }
                break;
            case "паштеты" :
                if(evaluation == 2) {
                    dishImageView.setImageResource(R.drawable.paste_red);
                }
                if(evaluation == 3) {
                    dishImageView.setImageResource(R.drawable.paste_yellow);
                }
                if(evaluation == 4) {
                    dishImageView.setImageResource(R.drawable.paste_green);
                }
                break;
            case "холодные напитки" :
                if(evaluation == 2) {
                    dishImageView.setImageResource(R.drawable.cold_drink_red);
                }
                if(evaluation == 3) {
                    dishImageView.setImageResource(R.drawable.cold_drink_yellow);
                }
                if(evaluation == 4) {
                    dishImageView.setImageResource(R.drawable.cold_drink_green);
                }
                break;
            case "горячие напитки" :
                if(evaluation == 2) {
                    dishImageView.setImageResource(R.drawable.hot_drink_red);
                }
                if(evaluation == 3) {
                    dishImageView.setImageResource(R.drawable.hot_drink_yellow);
                }
                if(evaluation == 4) {
                    dishImageView.setImageResource(R.drawable.hot_drink_green);
                }
                break;
            case "алкоголь, пиво" :
                if(evaluation == 2) {
                    dishImageView.setImageResource(R.drawable.alcohol_red);
                }
                if(evaluation == 3) {
                    dishImageView.setImageResource(R.drawable.alcohol_yellow);
                }
                if(evaluation == 4) {
                    dishImageView.setImageResource(R.drawable.alcohol_green);
                }
                break;

            case "десерты" :
                if(evaluation == 2) {
                    dishImageView.setImageResource(R.drawable.dessert_red);
                }
                if(evaluation == 3) {
                    dishImageView.setImageResource(R.drawable.dessert_yellow);
                }
                if(evaluation == 4) {
                    dishImageView.setImageResource(R.drawable.dessert_green);
                }
                break;
            case "варенье, джем, повидло, сироп" :
                if(evaluation == 2) {
                    dishImageView.setImageResource(R.drawable.jam_red);
                }
                if(evaluation == 3) {
                    dishImageView.setImageResource(R.drawable.jam_yellow);
                }
                if(evaluation == 4) {
                    dishImageView.setImageResource(R.drawable.jam_green);
                }
                break;
            case "мороженое" :
                if(evaluation == 2) {
                    dishImageView.setImageResource(R.drawable.ice_cream_red);
                }
                if(evaluation == 3) {
                    dishImageView.setImageResource(R.drawable.ice_cream_yellow);
                }
                if(evaluation == 4) {
                    dishImageView.setImageResource(R.drawable.ice_cream_green);
                }
                break;
            case "фрукты" :
                if(evaluation == 2) {
                    dishImageView.setImageResource(R.drawable.fruit_red);
                }
                if(evaluation == 3) {
                    dishImageView.setImageResource(R.drawable.fruit_yellow);
                }
                if(evaluation == 4) {
                    dishImageView.setImageResource(R.drawable.fruit_green);
                }
                break;
            case "орехи" :
                if(evaluation == 2) {
                    dishImageView.setImageResource(R.drawable.nuts_red);
                }
                if(evaluation == 3) {
                    dishImageView.setImageResource(R.drawable.nuts_yellow);
                }
                if(evaluation == 4) {
                    dishImageView.setImageResource(R.drawable.nuts_green);
                }
                break;
            case "добавки" :
                if(evaluation == 2) {
                    dishImageView.setImageResource(R.drawable.supplements_red);
                }
                if(evaluation == 3) {
                    dishImageView.setImageResource(R.drawable.supplements_yellow);
                }
                if(evaluation == 4) {
                    dishImageView.setImageResource(R.drawable.supplements_green);
                }
                break;
            default:
                if(evaluation == 2) {
                    dishImageView.setImageResource(R.drawable.star_red);
                }
                if(evaluation == 3) {
                    dishImageView.setImageResource(R.drawable.star_yellow);
                }
                if(evaluation == 4) {
                    dishImageView.setImageResource(R.drawable.star_green);
                }
                break;
        }
   }


}

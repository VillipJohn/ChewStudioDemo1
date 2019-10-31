package com.sauno.androiddeveloper.chewstudiodemo;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.Spinner;

import com.androidplot.util.PixelUtils;
import com.androidplot.xy.CatmullRomInterpolator;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.PanZoom;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.StepMode;
import com.androidplot.xy.XYGraphWidget;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYSeries;
import com.androidplot.xy.ZoomEstimator;
import com.sauno.androiddeveloper.chewstudiodemo.data.DatabaseCreateHelper;
import com.sauno.androiddeveloper.chewstudiodemo.data.DishDBHelper;
import com.sauno.androiddeveloper.chewstudiodemo.data.EatenDishDBHelper;

import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Android developer on 23.03.2018.
 */

public class StatisticsFragment extends Fragment {
    private XYPlot todayThreePlot;
    private XYPlot todayFivePlot;
    private XYPlot weekPlot;
    private XYPlot monthPlot;

    private Spinner graphSpinner;
    private Spinner rangeSpinner;

    private int positionGraphSpinner = 0;
    private int positionRangeSpinner = 0;

    XYSeries seriesTodayThreePlotGraph;
    XYSeries seriesTodayThreePlotNorm;
    XYSeries seriesTodayFivePlotGraph;
    XYSeries seriesTodayFivePlotNorm;
    XYSeries seriesWeekPlotGraph;
    XYSeries seriesWeekPlotNorm;
    XYSeries seriesMonthPlotGraph;
    XYSeries seriesMonthPlotNorm;

    int breakfastCalories = 0;
    int breakfastProteins= 0;
    int breakfastFats = 0;
    int breakfastCarbs = 0;
    int breakfastXe = 0;

    int lunchCalories = 0;
    int lunchProteins= 0;
    int lunchFats = 0;
    int lunchCarbs = 0;
    int lunchXe = 0;

    int dinnerCalories = 0;
    int dinnerProteins= 0;
    int dinnerFats = 0;
    int dinnerCarbs = 0;
    int dinnerXe = 0;

    int breakfastNormCalories = 0;
    int breakfastNormProteins= 0;
    int breakfastNormFats = 0;
    int breakfastNormCarbs = 0;
    int breakfastNormXe = 0;

    int lunchNormCalories = 0;
    int lunchNormProteins= 0;
    int lunchNormFats = 0;
    int lunchNormCarbs = 0;
    int lunchNormXe = 0;

    int dinnerNormCalories = 0;
    int dinnerNormProteins= 0;
    int dinnerNormFats = 0;
    int dinnerNormCarbs = 0;
    int dinnerNormXe = 0;

    Number[] dataTodayThreeGraph = {breakfastCalories, lunchCalories, dinnerCalories};
    Number[] dataTodayThreeNorm = {900, 900, 800};

    Number[] dataTodayFiveGraph = {800, 200, 700, 70, 600, 100};
    Number[] dataTodayFiveNorm = {425, 85, 510, 85, 425, 170};

    Number[] dataWeekGraph = {1600, 2350, 1990, 1550, 2150, 1850, 2000};
    Number[] dataWeekNorm = {1700, 1700, 1700, 1700, 1700, 1700, 1700};

    Number[] dataMonthGraph = {
            1600, 2350, 1990, 1550, 2150, 1850, 2000, 1600, 2350, 1990,
            1600, 2350, 1990, 1550, 2150, 1850, 2000, 1600, 2350, 1990,
            1600, 2350, 1990, 1550, 2150, 1850, 2000, 1600, 2350, 1990
    };
    Number[] dataMonthNorm = {
            1700, 1700, 1700, 1700, 1700, 1700, 1700, 1700, 1700, 1700,
            1700, 1700, 1700, 1700, 1700, 1700, 1700, 1700, 1700, 1700,
            1700, 1700, 1700, 1700, 1700, 1700, 1700, 1700, 1700, 1700
    };

    String elementString = "Калории";



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_statistics, container, false);

        getEatenDishes();
        //setNorms();

        FrameLayout graphFrameLayout = rootView.findViewById(R.id.graphFrameLayout);

        graphFrameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), StatisticHistoryActivity.class);
                getActivity().startActivity(intent);
            }
        });

        graphSpinner = rootView.findViewById(R.id.graphSpinner);
        rangeSpinner = rootView.findViewById(R.id.rangeSpinner);

        todayThreePlot = rootView.findViewById(R.id.todayThreePlot);
        todayFivePlot = rootView.findViewById(R.id.todayFivePlot);
        weekPlot = rootView.findViewById(R.id.weekPlot);
        monthPlot = rootView.findViewById(R.id.monthPlot);

     /*   monthCaloricPlot = rootView.findViewById(R.id.monthCaloricPlot);
        monthPFCXePlot = rootView.findViewById(R.id.monthPFCXePlot);*/

        graphSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {



                positionGraphSpinner = position;
                setGraph();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        rangeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                positionRangeSpinner = position;
                setGraph();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

       /* todayThreeCaloricGraph();
        todayThreePFCXeGraph();
        todayFiveCaloricGraph();
        todayFivePFCXeGraph();
        weekCaloricGraph();
        weekPFCXeGraph();*/


        todayThreeGraph();
        todayFiveGraph();
        weekGraph();
        monthGraph();

        setupActionBar();

        return rootView;
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

        DatabaseCreateHelper databaseCreateHelper = new DatabaseCreateHelper(getContext());
        SQLiteDatabase db;
        try {
            db = databaseCreateHelper.getWritableDatabase();
        }
        catch (SQLiteException ex){
            db = databaseCreateHelper.getReadableDatabase();
        }

        String[] projection = {
                EatenDishDBHelper.COLUMN_ID_DISH,
                EatenDishDBHelper.COLUMN_HOUR
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

        if (cursor.moveToFirst()) {

            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(EatenDishDBHelper.COLUMN_ID_DISH));

                idList.add(id);

                int time =  cursor.getInt(cursor.getColumnIndexOrThrow(EatenDishDBHelper.COLUMN_HOUR));

                hourList.add(time);

                Log.d("MyLogStatistic", "id  - " + id);

            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        for(int n = 0; n < idList.size(); n++) {
            setData(idList.get(n), hourList.get(n));
        }
    }

    private void setData(int id, int hour) {
        DatabaseCreateHelper databaseCreateHelper = new DatabaseCreateHelper(getContext());
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

        String meal = "";

        if (cursor.moveToFirst()) {

            do {
                int calories = cursor.getInt(cursor.getColumnIndexOrThrow(DishDBHelper.COLUMN_CALORIC));
                int proteins = cursor.getInt(cursor.getColumnIndexOrThrow(DishDBHelper.COLUMN_PROTEINS));
                int fats = cursor.getInt(cursor.getColumnIndexOrThrow(DishDBHelper.COLUMN_FATS));
                int carbs = cursor.getInt(cursor.getColumnIndexOrThrow(DishDBHelper.COLUMN_CARBS));
                int xe = cursor.getInt(cursor.getColumnIndexOrThrow(DishDBHelper.COLUMN_XE));

                if(hour >= 6 && hour < 11) {
                    breakfastCalories = breakfastCalories + calories;
                    breakfastProteins = breakfastProteins + proteins;
                    breakfastFats = breakfastFats + fats;
                    breakfastCarbs = breakfastCarbs + carbs;
                    breakfastXe = breakfastXe + xe;
                } else if(hour >= 11 && hour < 17) {
                    lunchCalories = lunchCalories + calories;
                    lunchProteins = lunchProteins + proteins;
                    lunchFats = lunchFats + fats;
                    lunchCarbs = lunchCarbs + carbs;
                    lunchXe = lunchXe + xe;
                } else if(hour >= 17 && hour < 21) {
                    dinnerCalories = dinnerCalories + calories;
                    dinnerProteins = dinnerProteins + proteins;
                    dinnerFats = dinnerFats + fats;
                    dinnerCarbs = dinnerCarbs + carbs;
                    dinnerXe = dinnerXe + xe;
                }

            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
    }

    /*private void setNorms() {
        SharedPreferences mSharedPreferences = getContext().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);

        int userDayCalories = 0;
        int userDayProteins = 0;
        int userDayFats = 0;
        int userDayCarbs = 0;
        int userDayXe = 0;

        userDayCalories = mSharedPreferences.getInt(UserProfileActivity.KEY_COUNT_CALORIES, 0);
        userDayProteins = mSharedPreferences.getInt(UserProfileActivity.KEY_COUNT_PROTEINS, 0);
        userDayFats = mSharedPreferences.getInt(UserProfileActivity.KEY_COUNT_FATS, 0);
        userDayCarbs = mSharedPreferences.getInt(UserProfileActivity.KEY_COUNT_CARBOHYDRATES, 0);
        userDayXe = mSharedPreferences.getInt(UserProfileActivity.KEY_COUNT_XE, 0);

            case "Завтрак":
                partOfCalories = (int) (userDayCalories * 0.35);
                partOfProteins = (int) (userDayProteins * 0.35);
                partOfFats = (int) (userDayFats * 0.35);
                partOfCarbohydrates = (int) (userDayCarbs * 0.35);
                partOfXe = (int)(userDayXe * 0.35);
                break;
            case "Обед":
                partOfCalories = (int) (userDayCalories * 0.35);
                partOfProteins = (int) (userDayProteins * 0.35);
                partOfFats = (int) (userDayFats * 0.35);
                partOfCarbohydrates = (int) (userDayCarbs * 0.35);
                partOfXe = (int)(userDayXe * 0.35);
                break;
            case "Ужин":
                partOfCalories = (int) (userDayCalories * 0.3);
                partOfProteins = (int) (userDayProteins * 0.3);
                partOfFats = (int) (userDayFats * 0.3);
                partOfCarbohydrates = (int) (userDayCarbs * 0.3);
                partOfXe = (int)(userDayXe * 0.3);
                break;
        }
    }*/

    private void setupActionBar() {
        ActionBar actionBar = ((MainActivity)getActivity()).getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setTitle("Статистика");
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setElevation(0);
        }
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        //getMenuInflater().inflate(R.menu.menu_host, menu);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {

            getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
            return true;
        }



        return super.onOptionsItemSelected(item);
    }


    private void setGraph() {
        switch(positionRangeSpinner) {
            case 0:
                todayThreePlot.setVisibility(View.VISIBLE);
                todayFivePlot.setVisibility(View.INVISIBLE);
                weekPlot.setVisibility(View.INVISIBLE);
                monthPlot.setVisibility(View.INVISIBLE);

                todayThreePlot.removeSeries(seriesTodayThreePlotGraph);
                todayThreePlot.removeSeries(seriesTodayThreePlotNorm);

                switch(positionGraphSpinner) {
                    case 0:
                        //calories
                        Number[] dataCalories = {breakfastCalories, lunchCalories, dinnerCalories};
                        Number[] dataCaloriesNorm = {900, 900, 800};
                        dataTodayThreeGraph = dataCalories;
                        dataTodayThreeNorm = dataCaloriesNorm;
                        elementString = "Калории";
                        todayThreePlot.getRangeTitle().setText("ккал");
                        break;

                    case 1:
                        //proteins
                        Number[] dataProteins = {breakfastProteins, lunchProteins, dinnerProteins};
                        Number[] dataProteinsNorm = {70, 70, 60};
                        dataTodayThreeGraph = dataProteins;
                        dataTodayThreeNorm = dataProteinsNorm;
                        elementString = "Белки";
                        todayThreePlot.getRangeTitle().setText("грамм");
                        break;
                    case 2:
                        //fats
                        Number[] dataFats = {breakfastFats, lunchFats, dinnerFats};
                        Number[] dataFatsNorm = {18, 18, 15};
                        dataTodayThreeGraph = dataFats;
                        dataTodayThreeNorm = dataFatsNorm;
                        elementString = "Жиры";
                        todayThreePlot.getRangeTitle().setText("грамм");
                        break;
                    case 3:
                        //carbs
                        Number[] dataCarbs = {breakfastCarbs, lunchCarbs, dinnerCarbs};
                        Number[] dataCarbsNorm = {130, 130, 105};
                        dataTodayThreeGraph = dataCarbs;
                        dataTodayThreeNorm = dataCarbsNorm;
                        elementString = "Углеводы";
                        todayThreePlot.getRangeTitle().setText("грамм");
                        break;
                    case 4:
                        //xe
                        Number[] dataXe = {breakfastXe, lunchXe, dinnerXe};
                        Number[] dataXeNorm = {3, 3, 2};
                        dataTodayThreeGraph = dataXe;
                        dataTodayThreeNorm = dataXeNorm;
                        elementString = "Хлебные единицы";
                        todayThreePlot.getRangeTitle().setText("хлебные единицы");
                        break;
                    case 5:
                        //glucose
                        Number[] dataGlucose = {3.2, 5.1, 4.5};
                        Number[] dataGlucoseNorm = {4.0, 4.5, 4.0};
                        dataTodayThreeGraph = dataGlucose;
                        dataTodayThreeNorm = dataGlucoseNorm;
                        elementString = "Глюкоза";
                        todayThreePlot.getRangeTitle().setText("ммоль/л");
                        break;
                }
                todayThreePlot.redraw();
                todayThreeGraph();
                break;
            case 1:
                todayThreePlot.setVisibility(View.INVISIBLE);
                todayFivePlot.setVisibility(View.VISIBLE);
                weekPlot.setVisibility(View.INVISIBLE);
                monthPlot.setVisibility(View.INVISIBLE);

                todayFivePlot.removeSeries(seriesTodayFivePlotGraph);
                todayFivePlot.removeSeries(seriesTodayFivePlotNorm);
                switch(positionGraphSpinner) {
                    case 0:
                        //calories
                        Number[] dataCalories = {800, 200, 700, 70, 600, 100};
                        Number[] dataCaloriesNorm = {425, 85, 510, 85, 425, 170};
                        dataTodayFiveGraph = dataCalories;
                        dataTodayFiveNorm = dataCaloriesNorm;
                        elementString = "Калории";
                        todayFivePlot.getRangeTitle().setText("ккал");
                        break;

                    case 1:
                        //proteins
                        Number[] dataProteins = {30, 15, 70, 10, 45, 12};
                        Number[] dataProteinsNorm = {50, 10, 60, 10, 50, 20};
                        dataTodayFiveGraph = dataProteins;
                        dataTodayFiveNorm = dataProteinsNorm;
                        elementString = "Белки";
                        todayFivePlot.getRangeTitle().setText("грамм");
                        break;
                    case 2:
                        //fats
                        Number[] dataFats = {10, 6, 18, 3, 13, 2};
                        Number[] dataFatsNorm = {11, 2, 14, 2, 11, 4};
                        dataTodayFiveGraph = dataFats;
                        dataTodayFiveNorm = dataFatsNorm;
                        elementString = "Жиры";
                        todayFivePlot.getRangeTitle().setText("грамм");
                        break;
                    case 3:
                        //carbs
                        Number[] dataCarbs = {90, 40, 150, 22, 70, 30};
                        Number[] dataCarbsNorm = {91, 18, 109, 18, 91, 36};
                        dataTodayFiveGraph = dataCarbs;
                        dataTodayFiveNorm = dataCarbsNorm;
                        elementString = "Углеводы";
                        todayFivePlot.getRangeTitle().setText("грамм");
                        break;
                    case 4:
                        //xe
                        Number[] dataXe = {2, 1, 3, 1, 2, 1};
                        Number[] dataXeNorm = {2, 0.4, 2.4, 0.4, 2, 0.8};
                        dataTodayFiveGraph = dataXe;
                        dataTodayFiveNorm = dataXeNorm;
                        elementString = "Хлебные единицы";
                        todayFivePlot.getRangeTitle().setText("хлебные единицы");
                        break;
                    case 5:
                        //glucose
                        Number[] dataGlucose = {3.2, 5.1, 4.5, 4.0, 5.5, 3.0};
                        Number[] dataGlucoseNorm = {4.0, 4.5, 4.0, 4.2, 4.4, 4.0};
                        dataTodayThreeGraph = dataGlucose;
                        dataTodayThreeNorm = dataGlucoseNorm;
                        elementString = "Глюкоза";
                        todayFivePlot.getRangeTitle().setText("ммоль/л");
                        break;
                }
                todayFivePlot.redraw();
                todayFiveGraph();
                break;
            case 2:
                todayThreePlot.setVisibility(View.INVISIBLE);
                todayFivePlot.setVisibility(View.INVISIBLE);
                weekPlot.setVisibility(View.VISIBLE);
                monthPlot.setVisibility(View.INVISIBLE);

                weekPlot.removeSeries(seriesWeekPlotGraph);
                weekPlot.removeSeries(seriesWeekPlotNorm);
                switch(positionGraphSpinner) {
                    case 0:
                        //calories
                        Number[] dataCalories = {1600, 2350, 1990, 1550, 2150, 1850, 2000};
                        Number[] dataCaloriesNorm = {1700, 1700, 1700, 1700, 1700, 1700, 1700};
                        dataWeekGraph = dataCalories;
                        dataWeekNorm = dataCaloriesNorm;
                        elementString = "Калории";
                        weekPlot.getRangeTitle().setText("ккал");
                        break;
                    case 1:
                        //proteins
                        Number[] dataProteins = {244, 180, 230, 170, 155, 185, 145};
                        Number[] dataProteinsNorm = {200, 200, 200, 200, 200, 200, 200};
                        dataWeekGraph = dataProteins;
                        dataWeekNorm = dataProteinsNorm;
                        elementString = "Белки";
                        weekPlot.getRangeTitle().setText("грамм");
                        break;
                    case 2:
                        //fats
                        Number[] dataFats = {38, 30, 45, 36, 43, 50, 55};
                        Number[] dataFatsNorm = {47, 47, 47, 47, 47, 47, 47};
                        dataWeekGraph = dataFats;
                        dataWeekNorm = dataFatsNorm;
                        elementString = "Жиры";
                        weekPlot.getRangeTitle().setText("грамм");
                        break;
                    case 3:
                        //carbs
                        Number[] dataCarbs = {345, 320, 410, 354, 372, 399, 450};
                        Number[] dataCarbsNorm = {365, 365, 365, 365, 365, 365, 365};
                        dataWeekGraph = dataCarbs;
                        dataWeekNorm = dataCarbsNorm;
                        elementString = "Углеводы";
                        weekPlot.getRangeTitle().setText("грамм");
                        break;
                    case 4:
                        //xe
                        Number[] dataXe = {10, 8, 13, 11, 9, 15, 12};
                        Number[] dataXeNorm = {8, 8, 8, 8, 8, 8, 8};
                        dataWeekGraph = dataXe;
                        dataWeekNorm = dataXeNorm;
                        elementString = "Хлебные единицы";
                        weekPlot.getRangeTitle().setText("хлебные единицы");
                        break;
                    case 5:
                        //glucose
                        Number[] dataGlucose = {3.2, 5.1, 4.5, 4.0, 5.5, 3.0, 5.3};
                        Number[] dataGlucoseNorm = {4.8, 4.8, 4.8, 4.8, 4.8, 4.8, 4.8};
                        dataTodayThreeGraph = dataGlucose;
                        dataTodayThreeNorm = dataGlucoseNorm;
                        elementString = "Глюкоза";
                        weekPlot.getRangeTitle().setText("ммоль/л");
                        break;
                }
                weekPlot.redraw();
                weekGraph();
                break;
            case 3:
                todayThreePlot.setVisibility(View.INVISIBLE);
                todayFivePlot.setVisibility(View.INVISIBLE);
                weekPlot.setVisibility(View.INVISIBLE);
                monthPlot.setVisibility(View.VISIBLE);

                monthPlot.removeSeries(seriesMonthPlotGraph);
                monthPlot.removeSeries(seriesMonthPlotNorm);
                switch(positionGraphSpinner) {
                    case 0:
                        //calories
                        Number[] dataCalories = {
                                1600, 2350, 1990, 1550, 2150, 1850, 2000, 1600, 2350, 1990,
                                1600, 2350, 1990, 1550, 2150, 1850, 2000, 1600, 2350, 1990,
                                1600, 2350, 1990, 1550, 2150, 1850, 2000, 1600, 2350, 1990
                        };
                        Number[] dataCaloriesNorm = {
                                1700, 1700, 1700, 1700, 1700, 1700, 1700, 1700, 1700, 1700,
                                1700, 1700, 1700, 1700, 1700, 1700, 1700, 1700, 1700, 1700,
                                1700, 1700, 1700, 1700, 1700, 1700, 1700, 1700, 1700, 1700
                        };
                        dataMonthGraph = dataCalories;
                        dataMonthNorm = dataCaloriesNorm;
                        elementString = "Калории";
                        monthPlot.getRangeTitle().setText("ккал");
                        break;
                    case 1:
                        //proteins
                        Number[] dataProteins = {
                                244, 180, 230, 170, 155, 185, 145, 244, 180, 230,
                                244, 180, 230, 170, 155, 185, 145, 244, 180, 230,
                                244, 180, 230, 170, 155, 185, 145, 244, 180, 230};
                        Number[] dataProteinsNorm = {
                                200, 200, 200, 200, 200, 200, 200, 200, 200, 200,
                                200, 200, 200, 200, 200, 200, 200, 200, 200, 200,
                                200, 200, 200, 200, 200, 200, 200, 200, 200, 200};
                        dataMonthGraph = dataProteins;
                        dataMonthNorm = dataProteinsNorm;
                        elementString = "Белки";
                        monthPlot.getRangeTitle().setText("грамм");
                        break;
                    case 2:
                        //fats
                        Number[] dataFats = {
                                38, 30, 45, 36, 43, 50, 55, 38, 30, 45,
                                38, 30, 45, 36, 43, 50, 55, 38, 30, 45,
                                38, 30, 45, 36, 43, 50, 55, 38, 30, 45};
                        Number[] dataFatsNorm = {
                                47, 47, 47, 47, 47, 47, 47, 47, 47, 47,
                                47, 47, 47, 47, 47, 47, 47, 47, 47, 47,
                                47, 47, 47, 47, 47, 47, 47, 47, 47, 47};
                        dataMonthGraph = dataFats;
                        dataMonthNorm = dataFatsNorm;
                        elementString = "Жиры";
                        monthPlot.getRangeTitle().setText("грамм");
                        break;
                    case 3:
                        //carbs
                        Number[] dataCarbs = {
                                345, 320, 410, 354, 372, 399, 450, 345, 320, 410,
                                345, 320, 410, 354, 372, 399, 450, 345, 320, 410,
                                345, 320, 410, 354, 372, 399, 450, 345, 320, 410};
                        Number[] dataCarbsNorm = {
                                365, 365, 365, 365, 365, 365, 365, 365, 365, 365,
                                365, 365, 365, 365, 365, 365, 365, 365, 365, 365,
                                365, 365, 365, 365, 365, 365, 365, 365, 365, 365};
                        dataMonthGraph = dataCarbs;
                        dataMonthNorm = dataCarbsNorm;
                        elementString = "Углеводы";
                        monthPlot.getRangeTitle().setText("грамм");
                        break;
                    case 4:
                        //xe
                        Number[] dataXe = {
                                10, 8, 13, 11, 9, 15, 12, 10, 8, 13,
                                10, 8, 13, 11, 9, 15, 12, 10, 8, 13,
                                10, 8, 13, 11, 9, 15, 12, 10, 8, 13};
                        Number[] dataXeNorm = {
                                8, 8, 8, 8, 8, 8, 8, 8, 8, 8,
                                8, 8, 8, 8, 8, 8, 8, 8, 8, 8,
                                8, 8, 8, 8, 8, 8, 8, 8, 8, 8};
                        dataMonthGraph = dataXe;
                        dataMonthNorm = dataXeNorm;
                        elementString = "Хлебные единицы";
                        monthPlot.getRangeTitle().setText("хлебные единицы");
                        break;
                    case 5:
                        //glucose
                        Number[] dataGlucose = {
                                3.2, 5.1, 4.5, 4.0, 5.5, 3.0, 5.3, 3.2, 5.1, 4.5,
                                3.2, 5.1, 4.5, 4.0, 5.5, 3.0, 5.3, 3.2, 5.1, 4.5,
                                3.2, 5.1, 4.5, 4.0, 5.5, 3.0, 5.3, 3.2, 5.1, 4.5};
                        Number[] dataGlucoseNorm = {
                                4.8, 4.8, 4.8, 4.8, 4.8, 4.8, 4.8, 4.8, 4.8, 4.8,
                                4.8, 4.8, 4.8, 4.8, 4.8, 4.8, 4.8, 4.8, 4.8, 4.8,
                                4.8, 4.8, 4.8, 4.8, 4.8, 4.8, 4.8, 4.8, 4.8, 4.8};
                        dataTodayThreeGraph = dataGlucose;
                        dataTodayThreeNorm = dataGlucoseNorm;
                        elementString = "Глюкоза";
                        monthPlot.getRangeTitle().setText("ммоль/л");
                        break;
                }
                monthPlot.redraw();
                monthGraph();
                break;
        }
    }

    private void monthGraph() {
        final String[] domainLabels =
                       {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10",
                        "11", "12", "13", "14", "15", "16", "17", "18", "19", "20",
                        "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31"};

        Number[] seriesGraph = dataMonthGraph;
        Number[] seriesNorm = dataMonthNorm;

        seriesMonthPlotGraph = new SimpleXYSeries(Arrays.asList(seriesGraph), SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, elementString);
        seriesMonthPlotNorm = new SimpleXYSeries(Arrays.asList(seriesNorm), SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Норма");

        LineAndPointFormatter seriesGraphFormat = new LineAndPointFormatter(Color.BLUE, Color.BLUE, null, null);

        seriesGraphFormat.setInterpolationParams(
                new CatmullRomInterpolator.Params(10, CatmullRomInterpolator.Type.Centripetal));


        LineAndPointFormatter seriesNormFormat =
                new LineAndPointFormatter(getActivity(), R.xml.line_point_formatter_with_labels);

        seriesNormFormat.getLinePaint().setPathEffect(new DashPathEffect(new float[] {

                // always use DP when specifying pixel sizes, to keep things consistent across devices:
                PixelUtils.dpToPix(20),
                PixelUtils.dpToPix(15)}, 0));



        monthPlot.addSeries(seriesMonthPlotGraph, seriesGraphFormat);
        monthPlot.addSeries(seriesMonthPlotNorm, seriesNormFormat);

        monthPlot.setDomainStep(StepMode.INCREMENT_BY_VAL, 1);

        monthPlot.getGraph().getLineLabelStyle(XYGraphWidget.Edge.BOTTOM).setFormat(new Format() {
            @Override
            public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) {
                int i = Math.round(((Number) obj).floatValue());

                float f = ((Number) obj).floatValue();
                Log.i("MyLogStatistic", "i = " + f);

                //return toAppendTo.append(domainLabels[i]);
                return toAppendTo.append(domainLabels[i]);
            }
            @Override
            public Object parseObject(String source, ParsePosition pos) {
                return null;
            }
        });

        monthPlot.getGraph().setMarginLeft(50);


        PanZoom.attach(monthPlot, PanZoom.Pan.HORIZONTAL, PanZoom.Zoom.STRETCH_HORIZONTAL, PanZoom.ZoomLimit.MIN_TICKS);
        // cap pan/zoom limits for panning and zooming to a 100x100 space:
        monthPlot.getOuterLimits().set(0, 30, 0, 2500);


        // enable autoselect of sampling level based on visible boundaries:
        monthPlot.getRegistry().setEstimator(new ZoomEstimator());


        /*plot.getGraph().position(
        PixelUtils.dpToPix(10), HorizontalPositioning.ABSOLUTE_FROM_RIGHT,
                PixelUtils.dpToPix(10), VerticalPositioning.RELATIVE_TO_BOTTOM);*/

        //weekCaloricPlot.getGraph().setMarginBottom(110);
    }

    private void weekGraph() {
        final String[] domainLabels = {"Пн", "Вт", "Ср", "Чт", "Пт", "Сб", "Вс"};

        Number[] seriesGraph = dataWeekGraph;
        Number[] seriesNorm = dataWeekNorm;

        seriesWeekPlotGraph = new SimpleXYSeries(Arrays.asList(seriesGraph), SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, elementString);
        seriesWeekPlotNorm = new SimpleXYSeries(Arrays.asList(seriesNorm), SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Норма");

        LineAndPointFormatter seriesGraphFormat = new LineAndPointFormatter(Color.BLUE, Color.BLUE, null, null);

        seriesGraphFormat.setInterpolationParams(
                new CatmullRomInterpolator.Params(10, CatmullRomInterpolator.Type.Centripetal));


        LineAndPointFormatter seriesNormFormat =
                new LineAndPointFormatter(getActivity(), R.xml.line_point_formatter_with_labels);

        seriesNormFormat.getLinePaint().setPathEffect(new DashPathEffect(new float[] {

                // always use DP when specifying pixel sizes, to keep things consistent across devices:
                PixelUtils.dpToPix(20),
                PixelUtils.dpToPix(15)}, 0));



        weekPlot.addSeries(seriesWeekPlotGraph, seriesGraphFormat);
        weekPlot.addSeries(seriesWeekPlotNorm, seriesNormFormat);

        weekPlot.setDomainStep(StepMode.INCREMENT_BY_VAL, 1);

        weekPlot.getGraph().getLineLabelStyle(XYGraphWidget.Edge.BOTTOM).setFormat(new Format() {
            @Override
            public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) {
                int i = Math.round(((Number) obj).floatValue());

                float f = ((Number) obj).floatValue();
                Log.i("MyLogStatistic", "i = " + f);

                //return toAppendTo.append(domainLabels[i]);
                return toAppendTo.append(domainLabels[i]);
            }
            @Override
            public Object parseObject(String source, ParsePosition pos) {
                return null;
            }
        });

        weekPlot.getGraph().setMarginLeft(50);
        //weekCaloricPlot.getGraph().setMarginBottom(110);
    }




    private void todayFiveGraph() {
        final String[] domainLabels = {"Завтрак", "1-й перекус", "Обед", "2-й перекус", "Ужин", "3-й перекус"};

        Number[] seriesGraph = dataTodayFiveGraph;
        Number[] seriesNorm = dataTodayFiveNorm;

        seriesTodayFivePlotGraph = new SimpleXYSeries(Arrays.asList(seriesGraph), SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, elementString);
        seriesTodayFivePlotNorm = new SimpleXYSeries(Arrays.asList(seriesNorm), SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Норма");

        LineAndPointFormatter seriesGraphFormat = new LineAndPointFormatter(Color.BLUE, Color.BLUE, null, null);

        seriesGraphFormat.setInterpolationParams(
                new CatmullRomInterpolator.Params(10, CatmullRomInterpolator.Type.Centripetal));


        LineAndPointFormatter seriesNormFormat =
                new LineAndPointFormatter(getActivity(), R.xml.line_point_formatter_with_labels);

        seriesNormFormat.getLinePaint().setPathEffect(new DashPathEffect(new float[] {

                // always use DP when specifying pixel sizes, to keep things consistent across devices:
                PixelUtils.dpToPix(20),
                PixelUtils.dpToPix(15)}, 0));



        todayFivePlot.addSeries(seriesTodayFivePlotGraph, seriesGraphFormat);
        todayFivePlot.addSeries(seriesTodayFivePlotNorm, seriesNormFormat);

        todayFivePlot.setDomainStep(StepMode.INCREMENT_BY_VAL, 1);

        todayFivePlot.getGraph().getLineLabelStyle(XYGraphWidget.Edge.BOTTOM).setFormat(new Format() {
            @Override
            public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) {
                int i = Math.round(((Number) obj).floatValue());

                float f = ((Number) obj).floatValue();
                Log.i("MyLogStatistic", "i = " + f);

                //return toAppendTo.append(domainLabels[i]);
                return toAppendTo.append(domainLabels[i]);
            }
            @Override
            public Object parseObject(String source, ParsePosition pos) {
                return null;
            }
        });

        todayFivePlot.getGraph().setMarginLeft(50);
        todayFivePlot.getGraph().setMarginBottom(110);
    }

    private void todayThreeGraph() {

        //plot = new XYPlot(getActivity(), "");

        // create a couple arrays of y-values to plot:
        //final Number[] domainLabels = {1, 2, 3, 6, 7, 8, 9, 10, 13, 14};
        final String[] domainLabels = {"Завтрак", "Обед", "Ужин"};
        //Number[] series1Numbers = {1, 4, 2, 8, 4, 16, 8, 32, 16, 40};

        Number[] seriesGraph = dataTodayThreeGraph;
        Number[] seriesNorm = dataTodayThreeNorm;


        // turn the above arrays into XYSeries':
        // (Y_VALS_ONLY means use the element index as the x value)
        seriesTodayThreePlotGraph = new SimpleXYSeries(Arrays.asList(seriesGraph), SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, elementString);
        seriesTodayThreePlotNorm = new SimpleXYSeries(Arrays.asList(seriesNorm), SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Норма");


        // create formatters to use for drawing a series using LineAndPointRenderer
        // and configure them from xml:
        LineAndPointFormatter seriesGraphFormat = new LineAndPointFormatter(Color.BLUE, Color.BLUE, null, null);

        seriesGraphFormat.setInterpolationParams(
                new CatmullRomInterpolator.Params(10, CatmullRomInterpolator.Type.Centripetal));


        LineAndPointFormatter seriesNormFormat =
                new LineAndPointFormatter(getActivity(), R.xml.line_point_formatter_with_labels);

        /*seriesNormaFormat.setInterpolationParams(
        new CatmullRomInterpolator.Params(3, CatmullRomInterpolator.Type.Centripetal));*/

        // add an "dash" effect to the series2 line:
        seriesNormFormat.getLinePaint().setPathEffect(new DashPathEffect(new float[] {

                // always use DP when specifying pixel sizes, to keep things consistent across devices:
                PixelUtils.dpToPix(20),
                PixelUtils.dpToPix(15)}, 0));



        todayThreePlot.addSeries(seriesTodayThreePlotGraph, seriesGraphFormat);
        todayThreePlot.addSeries(seriesTodayThreePlotNorm, seriesNormFormat);

        // add a new series' to the xyplot:

        //plot.addSeries(series2, series2Format);

        todayThreePlot.setDomainStep(StepMode.INCREMENT_BY_VAL, 1);

        //plot.getGraph().setLinesPerRangeLabel(1);

        //plot.seriesToScreenX(3);

        todayThreePlot.getGraph().getLineLabelStyle(XYGraphWidget.Edge.BOTTOM).setFormat(new Format() {
            @Override
            public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) {
                int i = Math.round(((Number) obj).floatValue());

                float f = ((Number) obj).floatValue();
                Log.i("MyLogStatistic", "i = " + f);

                //return toAppendTo.append(domainLabels[i]);
                return toAppendTo.append(domainLabels[i]);
            }
            @Override
            public Object parseObject(String source, ParsePosition pos) {
                return null;
            }
        });

        todayThreePlot.getGraph().setMarginLeft(50);



        //plot.getGraph().setPaddingLeft(1);

        //PanZoom.attach(plot, PanZoom.Pan.HORIZONTAL, PanZoom.Zoom.STRETCH_HORIZONTAL);
        // cap pan/zoom limits for panning and zooming to a 100x100 space:
        //plot.getOuterLimits().set(0, 100, 0, 100);


        /*plot.getGraph().position(
        PixelUtils.dpToPix(10), HorizontalPositioning.ABSOLUTE_FROM_RIGHT,
                PixelUtils.dpToPix(10), VerticalPositioning.RELATIVE_TO_BOTTOM);*/

        //plot.redraw();
    }

  /*  private void weekPFCXeGraph() {
        final String[] domainLabels = {"Пн", "Вт", "Ср", "Чт", "Пт", "Сб", "Вс"};



        XYSeries series2 = new SimpleXYSeries(Arrays.asList(seriesProteins), SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Б");
        XYSeries series3 = new SimpleXYSeries(Arrays.asList(seriesFats), SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Ж");
        XYSeries series4 = new SimpleXYSeries(Arrays.asList(seriesCarbs), SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "У");
        XYSeries series5 = new SimpleXYSeries(Arrays.asList(seriesXe), SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Хе");

        LineAndPointFormatter seriesProteinsFormat = new LineAndPointFormatter(Color.BLUE, Color.BLUE, null, null);
        LineAndPointFormatter seriesFatsFormat = new LineAndPointFormatter(Color.RED, Color.RED, null, null);
        LineAndPointFormatter seriesCarbsFormat = new LineAndPointFormatter(Color.GRAY, Color.GRAY, null, null);
        LineAndPointFormatter seriesXeFormat = new LineAndPointFormatter(Color.YELLOW, Color.YELLOW, null, null);

        weekPFCXePlot.addSeries(series2, seriesProteinsFormat);
        weekPFCXePlot.addSeries(series3, seriesFatsFormat);
        weekPFCXePlot.addSeries(series4, seriesCarbsFormat);
        weekPFCXePlot.addSeries(series5, seriesXeFormat);

        weekPFCXePlot.setDomainStep(StepMode.INCREMENT_BY_VAL, 1);

        weekPFCXePlot.getGraph().getLineLabelStyle(XYGraphWidget.Edge.BOTTOM).setFormat(new Format() {
            @Override
            public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) {
                int i = Math.round(((Number) obj).floatValue());

                float f = ((Number) obj).floatValue();
                Log.i("MyLogStatistic", "i = " + f);

                //return toAppendTo.append(domainLabels[i]);
                return toAppendTo.append(domainLabels[i]);
            }
            @Override
            public Object parseObject(String source, ParsePosition pos) {
                return null;
            }
        });

        weekPFCXePlot.getGraph().setMarginLeft(50);
        //weekPFCXePlot.getGraph().setMarginBottom(110);
    }
*/



/*

    private void todayFiveCaloricGraph() {
        final String[] domainLabels = {"Завтрак", "1-й перекус", "Обед", "2-й перекус", "Ужин", "3-й перекус"};

        Number[] seriesCalorics = {800, 200, 700, 250, 600, 100};

        XYSeries series1 = new SimpleXYSeries(Arrays.asList(seriesCalorics), SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Калории");

        LineAndPointFormatter seriesCaloricsFormat = new LineAndPointFormatter(Color.BLUE, Color.BLUE, null, null);

        seriesCaloricsFormat.setInterpolationParams(
                new CatmullRomInterpolator.Params(10, CatmullRomInterpolator.Type.Centripetal));

        todayFiveCaloricPlot.addSeries(series1, seriesCaloricsFormat);

        todayFiveCaloricPlot.setDomainStep(StepMode.INCREMENT_BY_VAL, 1);

        todayFiveCaloricPlot.getGraph().getLineLabelStyle(XYGraphWidget.Edge.BOTTOM).setFormat(new Format() {
            @Override
            public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) {
                int i = Math.round(((Number) obj).floatValue());

                float f = ((Number) obj).floatValue();
                Log.i("MyLogStatistic", "i = " + f);

                //return toAppendTo.append(domainLabels[i]);
                return toAppendTo.append(domainLabels[i]);
            }
            @Override
            public Object parseObject(String source, ParsePosition pos) {
                return null;
            }
        });

        todayFiveCaloricPlot.getGraph().setMarginLeft(50);
        todayFiveCaloricPlot.getGraph().setMarginBottom(110);
    }

    private void todayFivePFCXeGraph() {
        final String[] domainLabels = {"Завтрак", "1-й перекус", "Обед", "2-й перекус", "Ужин", "3-й перекус"};

        Number[] seriesProteins = {30, 15, 70, 10, 45, 12};
        Number[] seriesFats = {10, 6,  18, 3, 13, 2};
        Number[] seriesCarbs = {90, 40, 150, 22, 70, 30};
        Number[] seriesXe = {2, 1, 3, 1, 2, 1};

        XYSeries series2 = new SimpleXYSeries(Arrays.asList(seriesProteins), SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Б");
        XYSeries series3 = new SimpleXYSeries(Arrays.asList(seriesFats), SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Ж");
        XYSeries series4 = new SimpleXYSeries(Arrays.asList(seriesCarbs), SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "У");
        XYSeries series5 = new SimpleXYSeries(Arrays.asList(seriesXe), SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Хе");

        LineAndPointFormatter seriesProteinsFormat = new LineAndPointFormatter(Color.BLUE, Color.BLUE, null, null);
        LineAndPointFormatter seriesFatsFormat = new LineAndPointFormatter(Color.RED, Color.RED, null, null);
        LineAndPointFormatter seriesCarbsFormat = new LineAndPointFormatter(Color.GRAY, Color.GRAY, null, null);
        LineAndPointFormatter seriesXeFormat = new LineAndPointFormatter(Color.YELLOW, Color.YELLOW, null, null);

        todayFivePFCXePlot.addSeries(series2, seriesProteinsFormat);
        todayFivePFCXePlot.addSeries(series3, seriesFatsFormat);
        todayFivePFCXePlot.addSeries(series4, seriesCarbsFormat);
        todayFivePFCXePlot.addSeries(series5, seriesXeFormat);

        todayFivePFCXePlot.setDomainStep(StepMode.INCREMENT_BY_VAL, 1);

        todayFivePFCXePlot.getGraph().getLineLabelStyle(XYGraphWidget.Edge.BOTTOM).setFormat(new Format() {
            @Override
            public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) {
                int i = Math.round(((Number) obj).floatValue());

                float f = ((Number) obj).floatValue();
                Log.i("MyLogStatistic", "i = " + f);

                //return toAppendTo.append(domainLabels[i]);
                return toAppendTo.append(domainLabels[i]);
            }
            @Override
            public Object parseObject(String source, ParsePosition pos) {
                return null;
            }
        });

        todayFivePFCXePlot.getGraph().setMarginLeft(50);
        todayFivePFCXePlot.getGraph().setMarginBottom(110);
    }

    */

    @Override
    public void onDetach() {
        super.onDetach();

        ((MainActivity) getActivity()).getSupportActionBar().setTitle("SmartChew");
    }
}

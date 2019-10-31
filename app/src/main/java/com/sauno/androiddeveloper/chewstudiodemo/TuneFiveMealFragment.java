package com.sauno.androiddeveloper.chewstudiodemo;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.util.ArrayList;

public class TuneFiveMealFragment extends Fragment {
    private static String TAG = "TuneFiveMealFragment";

    public int breakfastPercentFiveMeal;
    public int firstSnackPercentFiveMeal;
    public int lunchPercentFiveMeal;
    public int secondSnackPercentFiveMeal;
    public int dinnerPercentFiveMeal;

    TextView chosenMealTextView;
    TextView percentTextView;
    ImageView minusImageView;
    ImageView plusImageView;
    Button okButton;

    TextView countCaloriesTextView;
    TextView countProteinsTextView;
    TextView countFatsTextView;
    TextView countCarbohydratesTextView;
    TextView countXETextView;

    PieChart pieChart;

    private int[] yData = new int[5];
    private String[] xData = {"Завтрак", "Первый перекус", "Обед", "Второй перекус", "Ужин"};

    int chosenMeal = 0;

    int calories;
    int proteins;
    int fats;
    int carbs;
    int xe;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_tune_four_meal, container, false);

        SharedPreferences mSharedPreferences = getActivity().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);

        breakfastPercentFiveMeal = mSharedPreferences.getInt("breakfastPercentFiveMeal", 25);
        firstSnackPercentFiveMeal = mSharedPreferences.getInt("firstSnackPercentFiveMeal", 10);
        lunchPercentFiveMeal = mSharedPreferences.getInt("lunchPercentFiveMeal", 30);
        secondSnackPercentFiveMeal = mSharedPreferences.getInt("secondSnackPercentFiveMeal", 10);
        dinnerPercentFiveMeal = mSharedPreferences.getInt("dinnerPercentFiveMeal", 25);


        setDataPercents();

        chosenMealTextView = rootView.findViewById(R.id.chosenMealTextView);
        percentTextView = rootView.findViewById(R.id.percentTextView);
        minusImageView = rootView.findViewById(R.id.minusImageView);
        plusImageView = rootView.findViewById(R.id.plusImageView);

        okButton = rootView.findViewById(R.id.okButton);

        /*animatedPieView = rootView.findViewById(R.id.pieView);
        setPie();*/

        countCaloriesTextView = rootView.findViewById(R.id.countCaloriesTextView);
        countProteinsTextView = rootView.findViewById(R.id.countProteinsTextView);
        countFatsTextView = rootView.findViewById(R.id.countFatsTextView);
        countCarbohydratesTextView = rootView.findViewById(R.id.countCarbohydratesTextView);
        countXETextView = rootView.findViewById(R.id.countXETextView);

        minusImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (chosenMeal) {
                    case 0:
                        if(breakfastPercentFiveMeal > 3) {
                            breakfastPercentFiveMeal = breakfastPercentFiveMeal - 1;
                            firstSnackPercentFiveMeal = firstSnackPercentFiveMeal + 1;
                            setData(breakfastPercentFiveMeal);
                        }
                        break;
                    case 1:
                        if(firstSnackPercentFiveMeal > 3) {
                            firstSnackPercentFiveMeal = firstSnackPercentFiveMeal - 1;
                            lunchPercentFiveMeal = lunchPercentFiveMeal + 1;
                            setData(firstSnackPercentFiveMeal);
                        }
                        break;
                    case 2:
                        if(lunchPercentFiveMeal > 3) {
                            lunchPercentFiveMeal = lunchPercentFiveMeal -1;
                            secondSnackPercentFiveMeal = secondSnackPercentFiveMeal + 1;
                            setData(lunchPercentFiveMeal);
                        }
                        break;
                    case 3:
                        if(secondSnackPercentFiveMeal > 0) {
                            secondSnackPercentFiveMeal = secondSnackPercentFiveMeal -1;
                            dinnerPercentFiveMeal = dinnerPercentFiveMeal + 1;
                            setData(secondSnackPercentFiveMeal);
                        }
                        break;
                    case 4:
                        if(dinnerPercentFiveMeal > 3) {
                            dinnerPercentFiveMeal = dinnerPercentFiveMeal -1;
                            breakfastPercentFiveMeal = breakfastPercentFiveMeal + 1;
                            setData(dinnerPercentFiveMeal);
                        }
                        break;
                    default:
                        break;
                }
                setDataPercents();
                addDataSet();
            }
        });

        plusImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (chosenMeal) {
                    case 0:
                        if(breakfastPercentFiveMeal < 91 && dinnerPercentFiveMeal > 3) {
                            breakfastPercentFiveMeal = breakfastPercentFiveMeal + 1;
                            dinnerPercentFiveMeal = dinnerPercentFiveMeal -1;
                            setData(breakfastPercentFiveMeal);
                        }
                        break;
                    case 1:
                        if(firstSnackPercentFiveMeal < 91 && breakfastPercentFiveMeal > 3) {
                            firstSnackPercentFiveMeal = firstSnackPercentFiveMeal + 1;
                            breakfastPercentFiveMeal = breakfastPercentFiveMeal - 1;
                            setData(firstSnackPercentFiveMeal);
                        }
                        break;
                    case 2:
                        if(lunchPercentFiveMeal < 91 && firstSnackPercentFiveMeal > 3) {
                            lunchPercentFiveMeal = lunchPercentFiveMeal + 1;
                            firstSnackPercentFiveMeal = firstSnackPercentFiveMeal - 1;
                            setData(lunchPercentFiveMeal);
                        }
                        break;
                    case 3:
                        if(secondSnackPercentFiveMeal < 91 && lunchPercentFiveMeal > 3) {
                            secondSnackPercentFiveMeal = secondSnackPercentFiveMeal + 1;
                            lunchPercentFiveMeal = lunchPercentFiveMeal - 1;
                            setData(secondSnackPercentFiveMeal);
                        }
                        break;
                    case 4:
                        if(dinnerPercentFiveMeal < 91 && secondSnackPercentFiveMeal > 3) {
                            dinnerPercentFiveMeal = dinnerPercentFiveMeal + 1;
                            secondSnackPercentFiveMeal = secondSnackPercentFiveMeal - 1;
                            setData(dinnerPercentFiveMeal);
                        }
                        break;
                    default:
                        break;
                }
                setDataPercents();
                addDataSet();
            }
        });

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SelectDesiredMealActivity selectDesiredMealActivity = (SelectDesiredMealActivity)getActivity();
                selectDesiredMealActivity.restartFromTuneFiveMealFragment();
            }
        });

        //setCurrentMeal();

        pieChart = rootView.findViewById(R.id.pieChart);
        pieChart.setDescription(null);

        //pieChart.setDescription("Sales by employee (In Thousands $) ");
        pieChart.setRotationEnabled(true);
        //pieChart.setUsePercentValues(true);
        //pieChart.setHoleColor(Color.BLUE);
        //pieChart.setCenterTextColor(Color.BLACK);
        pieChart.setHoleRadius(0);
        pieChart.setTransparentCircleAlpha(0);
        /*pieChart.setCenterText("Super Cool Chart");
        pieChart.setCenterTextSize(10);*/

        addDataSet();

        pieChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                chosenMeal = (int)(h.getX());

                switch (chosenMeal) {
                    case 0:
                        setData(breakfastPercentFiveMeal);
                        break;
                    case 1:
                        setData(firstSnackPercentFiveMeal);
                        break;
                    case 2:
                        setData(lunchPercentFiveMeal);
                        break;
                    case 3:
                        setData(secondSnackPercentFiveMeal);
                        break;
                    case 4:
                        setData(dinnerPercentFiveMeal);
                        break;
                    default:
                        break;
                }

                //Log.d(TAG, "index: " + index);

               /* int pos1 = e.toString().indexOf("y: ");
                String sales = e.toString().substring(pos1 + 3);

                for(int i = 0; i < yData.length; i++){
                    if(yData[i] == Float.parseFloat(sales)){
                        pos1 = i;
                        break;
                    }
                }
                String employee = xData[pos1 + 1];
                Toast.makeText(getContext(), "Employee " + employee + "\n" + "Sales: $" + sales + "K", Toast.LENGTH_LONG).show();*/

            }

            @Override
            public void onNothingSelected() {

            }
        });

        setData(breakfastPercentFiveMeal);

        return rootView;
    }

    // установление реальных процентов
    private void setDataPercents() {
        yData[0] = breakfastPercentFiveMeal;
        yData[1] = firstSnackPercentFiveMeal;
        yData[2] = lunchPercentFiveMeal;
        yData[3] = secondSnackPercentFiveMeal;
        yData[4] = dinnerPercentFiveMeal;
    }

    private void addDataSet() {
        ArrayList<PieEntry> yEntrys = new ArrayList<>();
        ArrayList<String> xEntrys = new ArrayList<>();

        for(int i = 0; i < yData.length; i++) {
            yEntrys.add(new PieEntry(yData[i], i));
        }

        for(int i = 0; i < xData.length; i++) {
            xEntrys.add(xData[i]);
        }

        // создание dataset
        PieDataSet pieDataSet = new PieDataSet(yEntrys, null);
        pieDataSet.setSliceSpace(2);
        pieDataSet.setValueTextSize(12);

        // добавление цветов в dataset
        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(Color.RED);
        colors.add(Color.BLUE);
        colors.add(Color.YELLOW);
        colors.add(Color.BLUE);
        colors.add(Color.GREEN);

        pieDataSet.setColors(colors);

        // добавление надписей
        Legend legend = pieChart.getLegend();
        legend.setEnabled(false);
        /*legend.setForm(Legend.LegendForm.CIRCLE);
        legend.setPosition(Legend.LegendPosition.LEFT_OF_CHART);*/

        // создание объекта pieData
        PieData pieData = new PieData(pieDataSet);
        pieData.setValueFormatter(new PercentFormatter());
        pieChart.setData(pieData);
        pieChart.invalidate();

    }

    private void setData(int percent) {
        chosenMealTextView.setText(xData[chosenMeal]);
        percentTextView.setText("" + percent + "%");

        float percentFloat = percent * 0.01f;

        calories = (int)(SelectDesiredMealActivity.calories * percentFloat);
        proteins = (int)(SelectDesiredMealActivity.proteins * percentFloat);
        fats = (int)(SelectDesiredMealActivity.fats * percentFloat);
        carbs = (int)(SelectDesiredMealActivity.carbohydrates * percentFloat);
        xe = (int)(SelectDesiredMealActivity.xe * percentFloat);


        countCaloriesTextView.setText(calories + "");
        countProteinsTextView.setText(proteins + "г");
        countFatsTextView.setText(fats + "г");
        countCarbohydratesTextView.setText(carbs + "г");
        countXETextView.setText(xe + "");
    }


    @Override
    public void onDetach() {
        super.onDetach();

        SelectDesiredMealActivity selectDesiredMealActivity = (SelectDesiredMealActivity)getActivity();
        selectDesiredMealActivity.restartFromTuneFiveMealFragment();
    }
}
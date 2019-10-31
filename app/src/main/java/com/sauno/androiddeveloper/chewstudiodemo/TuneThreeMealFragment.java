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

public class TuneThreeMealFragment extends Fragment {
    private static String TAG = "TuneThreeMealFragment";

    public int breakfastPercentThreeMeal;
    public int lunchPercentThreeMeal;
    public int dinnerPercentThreeMeal;

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

    private int[] yData = new int[3];
    private String[] xData = {"Завтрак", "Обед", "Ужин"};

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

        breakfastPercentThreeMeal = mSharedPreferences.getInt("breakfastPercentThreeMeal", 35);
        lunchPercentThreeMeal = mSharedPreferences.getInt("lunchPercentThreeMeal", 35);
        dinnerPercentThreeMeal = mSharedPreferences.getInt("dinnerPercentThreeMeal", 30);

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
                        if(breakfastPercentThreeMeal > 3) {
                            breakfastPercentThreeMeal = breakfastPercentThreeMeal - 1;
                            lunchPercentThreeMeal = lunchPercentThreeMeal + 1;
                            setData(breakfastPercentThreeMeal);
                        }
                        break;
                    case 1:
                        if(lunchPercentThreeMeal > 3) {
                            lunchPercentThreeMeal = lunchPercentThreeMeal - 1;
                            dinnerPercentThreeMeal = dinnerPercentThreeMeal + 1;
                            setData(lunchPercentThreeMeal);
                        }
                        break;
                    case 2:
                        if(dinnerPercentThreeMeal > 3) {
                            dinnerPercentThreeMeal = dinnerPercentThreeMeal -1;
                            breakfastPercentThreeMeal = breakfastPercentThreeMeal + 1;
                            setData(dinnerPercentThreeMeal);
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
                        if(breakfastPercentThreeMeal < 91 && dinnerPercentThreeMeal > 3) {
                            breakfastPercentThreeMeal = breakfastPercentThreeMeal + 1;
                            dinnerPercentThreeMeal = dinnerPercentThreeMeal -1;
                            setData(breakfastPercentThreeMeal);
                        }
                        break;
                    case 1:
                        if(lunchPercentThreeMeal < 91 && breakfastPercentThreeMeal > 3) {
                            lunchPercentThreeMeal = lunchPercentThreeMeal + 1;
                            breakfastPercentThreeMeal = breakfastPercentThreeMeal - 1;
                            setData(lunchPercentThreeMeal);
                        }
                        break;
                    case 2:
                        if(dinnerPercentThreeMeal < 91 && lunchPercentThreeMeal > 3) {
                            dinnerPercentThreeMeal = dinnerPercentThreeMeal + 1;
                            lunchPercentThreeMeal = lunchPercentThreeMeal - 1;
                            setData(dinnerPercentThreeMeal);
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
                selectDesiredMealActivity.restartFromTuneThreeMealFragment();
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
                        setData(breakfastPercentThreeMeal);
                        break;
                    case 1:
                        setData(lunchPercentThreeMeal);
                        break;
                    case 2:
                        setData(dinnerPercentThreeMeal);
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

        setData(breakfastPercentThreeMeal);

        return rootView;
    }

    // установление реальных процентов
    private void setDataPercents() {
        yData[0] = breakfastPercentThreeMeal;
        yData[1] = lunchPercentThreeMeal;
        yData[2] = dinnerPercentThreeMeal;
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
        selectDesiredMealActivity.restartFromTuneThreeMealFragment();
    }
}

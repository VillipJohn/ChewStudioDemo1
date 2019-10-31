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

public class TuneSixMealFragment extends Fragment {
    private static String TAG = "TuneSixMealFragment";

    public int breakfastPercentSixMeal;
    public int firstSnackPercentSixMeal;
    public int lunchPercentSixMeal;
    public int secondSnackPercentSixMeal;
    public int dinnerPercentSixMeal;
    public int thirdSnackPercentSixMeal;

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

    private int[] yData = new int[6];
    private String[] xData = {"Завтрак", "Первый перекус", "Обед", "Второй перекус", "Ужин", "Третий перекус"};

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

        breakfastPercentSixMeal = mSharedPreferences.getInt("breakfastPercentSixMeal", 25);
        firstSnackPercentSixMeal = mSharedPreferences.getInt("firstSnackPercentSixMeal", 5);
        lunchPercentSixMeal = mSharedPreferences.getInt("lunchPercentSixMeal", 30);
        secondSnackPercentSixMeal = mSharedPreferences.getInt("secondSnackPercentSixMeal", 5);
        dinnerPercentSixMeal = mSharedPreferences.getInt("dinnerPercentSixMeal", 25);
        thirdSnackPercentSixMeal = mSharedPreferences.getInt("thirdSnackPercentSixMeal", 10);

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
                        if(breakfastPercentSixMeal > 3) {
                            breakfastPercentSixMeal = breakfastPercentSixMeal - 1;
                            firstSnackPercentSixMeal = firstSnackPercentSixMeal + 1;
                            setData(breakfastPercentSixMeal);
                        }
                        break;
                    case 1:
                        if(firstSnackPercentSixMeal > 3) {
                            firstSnackPercentSixMeal = firstSnackPercentSixMeal - 1;
                            lunchPercentSixMeal = lunchPercentSixMeal + 1;
                            setData(firstSnackPercentSixMeal);
                        }
                        break;
                    case 2:
                        if(lunchPercentSixMeal > 3) {
                            lunchPercentSixMeal = lunchPercentSixMeal -1;
                            secondSnackPercentSixMeal = secondSnackPercentSixMeal + 1;
                            setData(lunchPercentSixMeal);
                        }
                        break;
                    case 3:
                        if(secondSnackPercentSixMeal > 0) {
                            secondSnackPercentSixMeal = secondSnackPercentSixMeal -1;
                            dinnerPercentSixMeal = dinnerPercentSixMeal + 1;
                            setData(secondSnackPercentSixMeal);
                        }
                        break;
                    case 4:
                        if(dinnerPercentSixMeal > 3) {
                            dinnerPercentSixMeal = dinnerPercentSixMeal -1;
                            thirdSnackPercentSixMeal = thirdSnackPercentSixMeal + 1;
                            setData(dinnerPercentSixMeal);
                        }
                        break;
                    case 5:
                        if(thirdSnackPercentSixMeal > 0) {
                            thirdSnackPercentSixMeal = thirdSnackPercentSixMeal -1;
                            breakfastPercentSixMeal = breakfastPercentSixMeal + 1;
                            setData(thirdSnackPercentSixMeal);
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
                        if(breakfastPercentSixMeal < 91 && thirdSnackPercentSixMeal > 3) {
                            breakfastPercentSixMeal = breakfastPercentSixMeal + 1;
                            thirdSnackPercentSixMeal = thirdSnackPercentSixMeal -1;
                            setData(breakfastPercentSixMeal);
                        }
                        break;
                    case 1:
                        if(firstSnackPercentSixMeal < 91 && breakfastPercentSixMeal > 3) {
                            firstSnackPercentSixMeal = firstSnackPercentSixMeal + 1;
                            breakfastPercentSixMeal = breakfastPercentSixMeal - 1;
                            setData(firstSnackPercentSixMeal);
                        }
                        break;
                    case 2:
                        if(lunchPercentSixMeal < 91 && firstSnackPercentSixMeal > 3) {
                            lunchPercentSixMeal = lunchPercentSixMeal + 1;
                            firstSnackPercentSixMeal = firstSnackPercentSixMeal - 1;
                            setData(lunchPercentSixMeal);
                        }
                        break;
                    case 3:
                        if(secondSnackPercentSixMeal < 91 && lunchPercentSixMeal > 3) {
                            secondSnackPercentSixMeal = secondSnackPercentSixMeal + 1;
                            lunchPercentSixMeal = lunchPercentSixMeal - 1;
                            setData(secondSnackPercentSixMeal);
                        }
                        break;
                    case 4:
                        if(dinnerPercentSixMeal < 91 && secondSnackPercentSixMeal > 3) {
                            dinnerPercentSixMeal = dinnerPercentSixMeal + 1;
                            secondSnackPercentSixMeal = secondSnackPercentSixMeal - 1;
                            setData(dinnerPercentSixMeal);
                        }
                        break;
                    case 5:
                        if(thirdSnackPercentSixMeal < 91 && dinnerPercentSixMeal > 3) {
                            thirdSnackPercentSixMeal = thirdSnackPercentSixMeal + 1;
                            dinnerPercentSixMeal = dinnerPercentSixMeal - 1;
                            setData(thirdSnackPercentSixMeal);
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
                selectDesiredMealActivity.restartFromTuneSixMealFragment();
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
                        setData(breakfastPercentSixMeal);
                        break;
                    case 1:
                        setData(firstSnackPercentSixMeal);
                        break;
                    case 2:
                        setData(lunchPercentSixMeal);
                        break;
                    case 3:
                        setData(secondSnackPercentSixMeal);
                        break;
                    case 4:
                        setData(dinnerPercentSixMeal);
                        break;
                    case 5:
                        setData(thirdSnackPercentSixMeal);
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

        setData(breakfastPercentSixMeal);

        return rootView;
    }

    // установление реальных процентов
    private void setDataPercents() {
        yData[0] = breakfastPercentSixMeal;
        yData[1] = firstSnackPercentSixMeal;
        yData[2] = lunchPercentSixMeal;
        yData[3] = secondSnackPercentSixMeal;
        yData[4] = dinnerPercentSixMeal;
        yData[5] = thirdSnackPercentSixMeal;
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
        colors.add(Color.BLUE);

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

    private void setData(int persent) {
        chosenMealTextView.setText(xData[chosenMeal]);
        percentTextView.setText("" + persent + "%");

        float persentFloat = persent * 0.01f;

        calories = (int)(SelectDesiredMealActivity.calories * persentFloat);
        proteins = (int)(SelectDesiredMealActivity.proteins * persentFloat);
        fats = (int)(SelectDesiredMealActivity.fats * persentFloat);
        carbs = (int)(SelectDesiredMealActivity.carbohydrates * persentFloat);
        xe = (int)(SelectDesiredMealActivity.xe * persentFloat);


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
        selectDesiredMealActivity.restartFromTuneSixMealFragment();
    }
}

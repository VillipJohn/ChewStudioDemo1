package com.sauno.androiddeveloper.chewstudiodemo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.sauno.androiddeveloper.chewstudiodemo.dialogFragments.SelectRestaurantFromListDialogFragment;

import java.util.Calendar;


/**
 * Created by Android developer on 23.03.2018.
 */

public class Tab1FoodFragment extends Fragment{
    Button mRecommendedMealButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.tab1_food, container, false);

        mRecommendedMealButton = rootView.findViewById(R.id.recommendedMealButton);
        Button mHomeMenuButton = rootView.findViewById(R.id.homeMenuButton);
        Button mListOfRestaurantsButton = rootView.findViewById(R.id.listOfRestaurantsButton);
        Button mLocalMenuButton = rootView.findViewById(R.id.localMenuButton);
        Button mIdentifyRestaurantsButton = rootView.findViewById(R.id.identifyRestaurantsButton);

        determineCurrentMeal();

        mHomeMenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Crashlytics.getInstance().crash();
            }
        });


        mRecommendedMealButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               /* SelectDesiredMealDialogFragment mChooseDesiredMealDialogFragment = new SelectDesiredMealDialogFragment();
                mChooseDesiredMealDialogFragment.show(getActivity().getSupportFragmentManager(), "dialogChooseDesiredMeal");*/

                /*FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                SelectDesiredMealFragment fragment = new SelectDesiredMealFragment();
                fragmentTransaction.add(R.id.drawer_layout, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();*/

                Intent intent = new Intent(getActivity(), SelectDesiredMealActivity.class);
                getActivity().startActivity(intent);
            }
        });

        mListOfRestaurantsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SelectRestaurantFromListDialogFragment mChooseRestaurantFromListDialogFragment = new SelectRestaurantFromListDialogFragment();
                mChooseRestaurantFromListDialogFragment.show(getActivity().getSupportFragmentManager(), "dialogChooseRestaurantFromList");
                /*Intent intent = new Intent(getActivity(), FakeActivity.class);
                startActivity(intent);*/

               /* FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                ItemFragment fragment = new  ItemFragment();
                fragmentTransaction.add(R.id.drawer_layout, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();*/


            }
        });

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        SharedPreferences mSharedPreferences = getActivity().getSharedPreferences("mypref", Context.MODE_PRIVATE);
        String mealString = mSharedPreferences.getString("meal", " ");

        if(!mealString.equals(" ")){

        }

        determineCurrentMeal();
    }

    private void determineCurrentMeal() {
        SharedPreferences mSharedPreferences = getActivity().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        Boolean threeOrFiveMeals = mSharedPreferences.getBoolean("threeOrFiveMeals", false);

        String[] fiveMealsArray = getResources().getStringArray(R.array.five_meals_array);
        String[] threeMealsArray = getResources().getStringArray(R.array.three_meals_array);

        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR);
        int am = calendar.get(Calendar.AM_PM);
        //int minutes = calendar.get(Calendar.MINUTE);

        //Log.i("MyLog", "Часов - " + hour + "   Минут - " + minutes + "   AM - " + am);

        if(am == 1) {
            hour = hour + 12;
        }

        if(threeOrFiveMeals) {
            if(hour >= 6 && hour < 8) {
                mRecommendedMealButton.setText(fiveMealsArray[0]);
            } else if(hour >= 8 && hour < 11) {
                mRecommendedMealButton.setText(fiveMealsArray[1]);
            } else if(hour >= 11 && hour < 14) {
                mRecommendedMealButton.setText(fiveMealsArray[2]);
            } else if(hour >= 14 && hour < 17) {
                mRecommendedMealButton.setText(fiveMealsArray[3]);
            } else if(hour >= 17 && hour < 20) {
                mRecommendedMealButton.setText(fiveMealsArray[4]);
            } else {
                mRecommendedMealButton.setText(fiveMealsArray[5]);
            }
        } else {
            if(hour >= 6 && hour < 11) {
                mRecommendedMealButton.setText(threeMealsArray[0]);
            } else if(hour >= 11 && hour < 17) {
                mRecommendedMealButton.setText(threeMealsArray[1]);
            } else if(hour >= 17 && hour < 21) {
                mRecommendedMealButton.setText(threeMealsArray[2]);
            } else {
                mRecommendedMealButton.setText("---");
            }
        }



    }
}

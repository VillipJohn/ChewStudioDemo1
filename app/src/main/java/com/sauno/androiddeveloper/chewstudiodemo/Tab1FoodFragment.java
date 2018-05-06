package com.sauno.androiddeveloper.chewstudiodemo;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.sauno.androiddeveloper.chewstudiodemo.dialogFragments.ChooseDesiredMealDialogFragment;
import com.sauno.androiddeveloper.chewstudiodemo.dialogFragments.ChooseRestaurantFromListDialogFragment;

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


        mRecommendedMealButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChooseDesiredMealDialogFragment mChooseDesiredMealDialogFragment = new ChooseDesiredMealDialogFragment();
                mChooseDesiredMealDialogFragment.show(getActivity().getSupportFragmentManager(), "dialogChooseDesiredMeal");
            }
        });

        mListOfRestaurantsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ChooseRestaurantFromListDialogFragment mChooseRestaurantFromListDialogFragment = new ChooseRestaurantFromListDialogFragment();
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
        //long time= System.currentTimeMillis();

        String[] desiredMealArray = getResources().getStringArray(R.array.desired_meal_array);

        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR);
        int am = calendar.get(Calendar.AM_PM);
        int minutes = calendar.get(Calendar.MINUTE);

        Log.i("MyLog", "Часов - " + hour + "   Минут - " + minutes + "   AM - " + am);

        if(am == 1) {
            hour = hour + 12;
        }

        if(hour >= 6 && hour < 8) {
            mRecommendedMealButton.setText(desiredMealArray[0]);
        } else if(hour >= 8 && hour < 11) {
            mRecommendedMealButton.setText(desiredMealArray[1]);
        } else if(hour >= 11 && hour < 14) {
            mRecommendedMealButton.setText(desiredMealArray[2]);
        } else if(hour >= 14 && hour < 17) {
            mRecommendedMealButton.setText(desiredMealArray[3]);
        } else if(hour >= 17 && hour < 20) {
            mRecommendedMealButton.setText(desiredMealArray[4]);
        } else {
            mRecommendedMealButton.setText(desiredMealArray[5]);
        }
/*
        6.00-8.00 - завтрак(25%)
        8.00-11.00 - первый перекус(5%)

        11.00-14.00 - обед(30%)
        14.00-17.00 - второй перекус(5%)
        17.00-20.00 - ужин(25%)
        20.00-6.00 - третий перекус(10%)*/

    }
}

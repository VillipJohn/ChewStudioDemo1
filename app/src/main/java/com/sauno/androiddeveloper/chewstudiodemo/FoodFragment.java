package com.sauno.androiddeveloper.chewstudiodemo;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.sauno.androiddeveloper.chewstudiodemo.dialogFragments.SelectRestaurantFromListDialogFragment;

import java.util.Calendar;


/**
 * Created by Android developer on 23.03.2018.
 */

public class FoodFragment extends Fragment{
    Button recommendedMealButton;

    public static String currentMeal;
    public static int currentMealInt;

    int userDayCalories;
    int userDayProteins;
    int userDayFats;
    int userDayCarbs;
    int userDayXe;

    boolean isExistUserCPFC;

    private OnFragmentInteractionListener mListener;

    // Идентификатор уведомления
    private static final int NOTIFY_ID = 1;
    private static final String CHANNEL_ID = "myId";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.tab1_food, container, false);

        recommendedMealButton = rootView.findViewById(R.id.recommendedMealButton);
        Button homeMenuButton = rootView.findViewById(R.id.homeMenuButton);
        Button listOfRestaurantsButton = rootView.findViewById(R.id.listOfRestaurantsButton);
        Button localMenuButton = rootView.findViewById(R.id.localMenuButton);
        Button identifyRestaurantsButton = rootView.findViewById(R.id.identifyRestaurantsButton);

        determineCurrentMeal();

        homeMenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               /* if (mListener != null) {
                    String onButton = "onHomeMenuButton";
                    mListener.onFragmentInteraction(onButton);
                }*/
                Intent intent = new Intent(getActivity(), HomeMenuActivity.class);
                getActivity().startActivity(intent);

            }
        });


        recommendedMealButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), SelectDesiredMealActivity.class);
                getActivity().startActivity(intent);
            }
        });

        listOfRestaurantsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SelectRestaurantFromListDialogFragment mChooseRestaurantFromListDialogFragment = new SelectRestaurantFromListDialogFragment();
                mChooseRestaurantFromListDialogFragment.show(getActivity().getSupportFragmentManager(), "dialogChooseRestaurantFromList");
            }
        });

        identifyRestaurantsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent notificationIntent = new Intent(getActivity(), MainActivity.class);
               /* PendingIntent contentIntent = PendingIntent.getActivity(getActivity(),
                        0, notificationIntent,
                        PendingIntent.FLAG_CANCEL_CURRENT);*/

                PendingIntent contentIntent = null;

                Resources res = getActivity().getResources();

                // до версии Android 8.0 API 26
                NotificationCompat.Builder builder = new NotificationCompat.Builder(getActivity());

                Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

                builder.setContentIntent(contentIntent)
                        // обязательные настройки
                        .setSmallIcon(R.drawable.ic_notification)
                        //.setContentTitle(res.getString(R.string.notifytitle)) // Заголовок уведомления
                        .setContentTitle("Напоминание")
                        //.setContentText(res.getString(R.string.notifytext))
                        .setContentText("Проверьте уровень глюкозы") // Текст уведомления
                        // необязательные настройки
                        //.setLargeIcon(BitmapFactory.decodeResource(res, R.drawable.hungrycat)) // большая
                        // картинка
                        //.setTicker(res.getString(R.string.warning)) // текст в строке состояния
                        .setTicker("Проверьте уровень глюкозы")
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .setWhen(System.currentTimeMillis())
                        .setSound(alarmSound)
                        .setAutoCancel(true); // автоматически закрыть уведомление после нажатия

                NotificationManager notificationManager =
                        (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
                // Альтернативный вариант
                // NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
                notificationManager.notify(NOTIFY_ID, builder.build());

                createNotificationChannel();
            }
        });

        checkIfExistUserCPFC();

        determineCurrentMeal();

        setupActionBar();

        return rootView;
    }

    private void setupActionBar() {
        ActionBar actionBar = ((MainActivity)getActivity()).getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setTitle("Питание");
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


    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Напоминание";
            String description = "Проверьте уровень глюкозы";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getActivity().getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);

            Notification notification = new Notification.Builder(getActivity(), CHANNEL_ID)
                    .setContentTitle("Напоминание")
                    .setContentText("Проверьте уровень глюкозы")
                    .setBadgeIconType(R.drawable.ic_notification)
                    .setNumber(5)
                    .setSmallIcon(R.drawable.ic_notification)
                    .setAutoCancel(true)
                    .build();

            notificationManager.notify(101, notification);

            //Log.d("FoodFragment", notificationManager.toString());
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        recommendedMealButton.setText(currentMeal);
    }

    //определение текущего приёма пищи
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
                recommendedMealButton.setText(fiveMealsArray[0]);
                currentMeal = fiveMealsArray[0];
                currentMealInt = 8;
            } else if(hour >= 8 && hour < 11) {
                recommendedMealButton.setText(fiveMealsArray[1]);
                currentMeal = fiveMealsArray[1];
                currentMealInt = 9;
            } else if(hour >= 11 && hour < 14) {
                recommendedMealButton.setText(fiveMealsArray[2]);
                currentMeal = fiveMealsArray[2];
                currentMealInt = 10;
            } else if(hour >= 14 && hour < 17) {
                recommendedMealButton.setText(fiveMealsArray[3]);
                currentMeal = fiveMealsArray[3];
                currentMealInt = 11;
            } else if(hour >= 17 && hour < 20) {
                recommendedMealButton.setText(fiveMealsArray[4]);
                currentMeal = fiveMealsArray[4];
                currentMealInt = 12;
            } else {
                recommendedMealButton.setText(fiveMealsArray[5]);
                currentMeal = fiveMealsArray[5];
                currentMealInt = 12;
            }
        } else {
            if(hour >= 6 && hour < 11) {
                recommendedMealButton.setText(threeMealsArray[0]);
                currentMeal = threeMealsArray[0];
                currentMealInt = 1;
            } else if(hour >= 11 && hour < 17) {
                recommendedMealButton.setText(threeMealsArray[1]);
                currentMeal = threeMealsArray[1];
                currentMealInt = 2;
            } else if(hour >= 17 && hour < 21) {
                recommendedMealButton.setText(threeMealsArray[2]);
                currentMeal = threeMealsArray[2];
                currentMealInt = 3;
            } else {
                recommendedMealButton.setText("---");
                currentMeal = "---";
            }
        }

        if(isExistUserCPFC) {
            saveCurrentCPFC(currentMeal, threeOrFiveMeals);
        }
    }

    private void saveCurrentCPFC(String currentMeal, boolean threeOrFiveMeals) {
        int partOfCalories = 0;
        int partOfProteins = 0;
        int partOfFats = 0;
        int partOfCarbohydrates = 0;
        int partOfXe = 0;

        if(threeOrFiveMeals) {
            switch(currentMeal) {
                case "Завтрак":
                    partOfCalories = (int)(userDayCalories * 0.25);
                    partOfProteins = (int)(userDayProteins * 0.25);
                    partOfFats = (int)(userDayFats * 0.25);
                    partOfCarbohydrates = (int)(userDayCarbs * 0.25);
                    partOfXe = (int)(userDayXe * 0.25);
                    break;
                case "Первый перекус":
                    partOfCalories = (int)(userDayCalories * 0.05);
                    partOfProteins = (int)(userDayProteins * 0.05);
                    partOfFats = (int)(userDayFats * 0.05);
                    partOfCarbohydrates = (int)(userDayCarbs * 0.05);
                    partOfXe = (int)(userDayXe * 0.05);
                    break;
                case "Обед":
                    partOfCalories = (int)(userDayCalories * 0.3);
                    partOfProteins = (int)(userDayProteins * 0.3);
                    partOfFats = (int)(userDayFats * 0.3);
                    partOfCarbohydrates = (int)(userDayCarbs * 0.3);
                    partOfXe = (int)(userDayXe * 0.3);
                    break;
                case "Второй перекус":
                    partOfCalories = (int)(userDayCalories * 0.05);
                    partOfProteins = (int)(userDayProteins * 0.05);
                    partOfFats = (int)(userDayFats * 0.05);
                    partOfCarbohydrates = (int)(userDayCarbs * 0.05);
                    partOfXe = (int)(userDayXe * 0.05);
                    break;
                case "Ужин":
                    partOfCalories = (int)(userDayCalories * 0.25);
                    partOfProteins = (int)(userDayProteins * 0.25);
                    partOfFats = (int)(userDayFats * 0.25);
                    partOfCarbohydrates = (int)(userDayCarbs * 0.25);
                    partOfXe = (int)(userDayXe * 0.25);
                    break;
                case "Третий перекус":
                    partOfCalories = (int)(userDayCalories * 0.1);
                    partOfProteins = (int)(userDayProteins * 0.1);
                    partOfFats = (int)(userDayFats * 0.1);
                    partOfCarbohydrates = (int)(userDayCarbs * 0.1);
                    partOfXe = (int)(userDayXe * 0.1);
                    break;
            }
        } else {
            switch(currentMeal) {
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
        }

        SharedPreferences mSharedPreferences = getActivity().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putInt("currentCalories", partOfCalories);
        editor.putInt("currentProteins", partOfProteins);
        editor.putInt("currentFats", partOfFats);
        editor.putInt("currentCarbs", partOfCarbohydrates);
        editor.putInt("currentXE", partOfXe);
        editor.apply();

    }

    private void checkIfExistUserCPFC() {
        SharedPreferences mSharedPreferences = getActivity().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);

        userDayCalories = mSharedPreferences.getInt(UserProfileActivity.KEY_COUNT_CALORIES, 0);
        userDayProteins = mSharedPreferences.getInt(UserProfileActivity.KEY_COUNT_PROTEINS, 0);
        userDayFats = mSharedPreferences.getInt(UserProfileActivity.KEY_COUNT_FATS, 0);
        userDayCarbs = mSharedPreferences.getInt(UserProfileActivity.KEY_COUNT_CARBOHYDRATES, 0);
        userDayXe = mSharedPreferences.getInt(UserProfileActivity.KEY_COUNT_XE, 0);

        if(userDayCalories == 0) {
            Toast.makeText(getActivity(), "Для подсчёта рекомендуемых Вам ежедневно калорий, белков, жиров, углеводов введите: пол, возраст, рост, вес, образ жизни", Toast.LENGTH_LONG).show();
            isExistUserCPFC = false;
        } else {
            isExistUserCPFC =true;
        }

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;

        ((MainActivity) getActivity()).getSupportActionBar().setTitle("SmartChew");
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(String onButton);
    }

}

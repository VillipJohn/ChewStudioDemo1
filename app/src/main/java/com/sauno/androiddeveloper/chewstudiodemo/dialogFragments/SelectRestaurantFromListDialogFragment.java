package com.sauno.androiddeveloper.chewstudiodemo.dialogFragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import com.google.android.gms.common.util.ArrayUtils;
import com.sauno.androiddeveloper.chewstudiodemo.ChoiceOfDishesActivity;
import com.sauno.androiddeveloper.chewstudiodemo.R;
import com.sauno.androiddeveloper.chewstudiodemo.data.DatabaseCreateHelper;
import com.sauno.androiddeveloper.chewstudiodemo.data.RestaurantDBHelper;

import java.util.ArrayList;
import java.util.List;

//Диалоговое окно выбора ресторана из списка
public class SelectRestaurantFromListDialogFragment extends DialogFragment {
    int[] restaurantIntArray;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final String[] restaurantStringArray = getRestaurantsFromDB();

        //final String[] restaurantStringArray = {"Чайхона 1"};

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AppDialog);
        builder.setTitle("Выбор ресторана:")
                .setCancelable(false)

                /*// добавляем одну кнопку для закрытия диалога
                .setNegativeButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int id) {
                                // Нужно ещё передавать название ресторана чтоб от этого шла выборка из базы
                                Intent intent = new Intent(getActivity(), ChoiceOfDishesActivity.class);
                                startActivity(intent);
                                dialog.cancel();
                            }
                        })*/
                .setItems(restaurantStringArray,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int item) {
                                // Нужно ещё передавать название ресторана чтоб от этого шла выборка из базы
                                Intent intent = new Intent(getActivity(), ChoiceOfDishesActivity.class);
                                intent.putExtra("restaurant", restaurantIntArray[item]);
                                //intent.putExtra("restaurant", 9);
                                startActivity(intent);
                                dialog.cancel();

                        }
                });


        return builder.create();
    }


    //получение списка всех ресторанов с базы данных
    private String[] getRestaurantsFromDB() {
        DatabaseCreateHelper databaseCreateHelper = new DatabaseCreateHelper(getActivity());
        SQLiteDatabase db;
        try {
            db = databaseCreateHelper.getWritableDatabase();
        }
        catch (SQLiteException ex){
            db = databaseCreateHelper.getReadableDatabase();
        }

        String[] projection = {
                RestaurantDBHelper.COLUMN_NAME,
                RestaurantDBHelper.COLUMN_ID
        };

        Cursor cursor = db.query(
                true,
                RestaurantDBHelper.TABLE,
                projection,
                null,
                null,
                RestaurantDBHelper.COLUMN_NAME,
                null,
                null,
                null
        );

        List<String> restaurants = new ArrayList<>();
        List<Integer> restaurantIds = new ArrayList<>();

        if (cursor.moveToFirst()) {

            do {
                if(cursor.getInt(cursor.getColumnIndexOrThrow(RestaurantDBHelper.COLUMN_ID)) != 11) {
                    String restaurant = cursor.getString(cursor.getColumnIndexOrThrow(RestaurantDBHelper.COLUMN_NAME));
                    restaurants.add(restaurant);

                    int restaurantId = cursor.getInt(cursor.getColumnIndexOrThrow(RestaurantDBHelper.COLUMN_ID));
                    restaurantIds.add(restaurantId);
                }
                //Log.i("MyLog",category + "\n");

            }while (cursor.moveToNext());

        }

        String[] restaurantsArray = restaurants.toArray(new String[restaurants.size()]);
        restaurantIntArray = ArrayUtils.toPrimitiveArray(restaurantIds);

        cursor.close();
        db.close();

        return restaurantsArray;
    }
}
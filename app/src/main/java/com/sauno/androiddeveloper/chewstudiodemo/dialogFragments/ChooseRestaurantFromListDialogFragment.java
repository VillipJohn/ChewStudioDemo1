package com.sauno.androiddeveloper.chewstudiodemo.dialogFragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import com.sauno.androiddeveloper.chewstudiodemo.ChoiceOfDishesActivity;
import com.sauno.androiddeveloper.chewstudiodemo.R;

public class ChooseRestaurantFromListDialogFragment extends DialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

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
                .setItems(R.array.list_of_restaurants_array,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int item) {
                                // Нужно ещё передавать название ресторана чтоб от этого шла выборка из базы
                                Intent intent = new Intent(getActivity(), ChoiceOfDishesActivity.class);
                                startActivity(intent);
                                dialog.cancel();

                        }
                });


        return builder.create();
    }
}
package com.sauno.androiddeveloper.chewstudiodemo.dialogFragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.sauno.androiddeveloper.chewstudiodemo.AddDishActivity;
import com.sauno.androiddeveloper.chewstudiodemo.R;

//ЭТОТ КЛАСС НЕ ЗАДЕЙСТВОВАН, ПОЗЖЕ БУДЕТ УДАЛЁН
public class SelectGramsDialogFragment extends DialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        int minValue = 1;
        int maxValue = 100;
        int value = 10;

        final String dishName = "Название блюда";
        final String hostActivity = "hostActivity";



        //private final NumberPicker.OnValueChangeListener valueChangeListener;
        final TextView mCountDishTextView = getActivity().findViewById(R.id.dishCountTextView);

        final NumberPicker numberPicker = new NumberPicker(getActivity());

        numberPicker.setMinValue(minValue);
        numberPicker.setMaxValue(maxValue);
        numberPicker.setValue(value);

        NumberPicker.Formatter formatter = new NumberPicker.Formatter() {
            @Override
            public String format(int value) {
                int temp = value * 10;
                return "" + temp;
            }
        };
        numberPicker.setFormatter(formatter);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AppDialog);
        builder.setTitle("Выберите количество грамм:");


        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                int chosenValue = numberPicker.getValue() * 10;

                dialogInterface.cancel();

                AddDishActivity addDishActivity = (AddDishActivity) getActivity();
                addDishActivity.restartData(chosenValue);

                /*if(hostActivity.equals("BasketActivity")) {
                    for(int n = 0; n < ChoiceOfDishesActivity.dishOrderList.size(); n++) {
                        if((ChoiceOfDishesActivity.dishOrderList.get(n).getDishName()).equals(dishName)) {
                            ChoiceOfDishesActivity.dishOrderList.get(n).setQuantityDishes(quantity);
                        }
                    }




                } else if(hostActivity.equals("ListDishesActivity")) {

                }*/

            }
        });
        builder.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        builder.setView(numberPicker);

        return builder.create();

    }

}

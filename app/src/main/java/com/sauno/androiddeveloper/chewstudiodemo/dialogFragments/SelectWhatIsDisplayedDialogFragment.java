package com.sauno.androiddeveloper.chewstudiodemo.dialogFragments;


import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.widget.TextView;

import com.sauno.androiddeveloper.chewstudiodemo.R;

public class SelectWhatIsDisplayedDialogFragment extends DialogFragment {
    TextView mEatenCountTextView;
    TextView mSpentCountTextView;
    TextView mRemainCountTextView;

    SharedPreferences sharedPref;

    String[] listItems;
    boolean[] checkedItems;


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        mEatenCountTextView = getActivity().findViewById(R.id.eatenCountTextView);
        mSpentCountTextView = getActivity().findViewById(R.id.spentCountTextView);
        mRemainCountTextView = getActivity().findViewById(R.id.remainCountTextView);

        sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        //int numberWhatIsDisplayed = sharedPref.getInt("WhatIsDisplayed", 0);

        listItems = getResources().getStringArray(R.array.what_is_displayed_array);
        checkedItems = new boolean[listItems.length];

        boolean isCheckedCalories = sharedPref.getBoolean("IsCheckedCalories", false);
        boolean isCheckedProteins = sharedPref.getBoolean("IsCheckedProteins", false);
        boolean isCheckedFats = sharedPref.getBoolean("IsCheckedFats", false);
        boolean isCheckedCarbohydrates = sharedPref.getBoolean("IsCheckedCarbohydrates", false);

        checkedItems[0] = isCheckedCalories;
        checkedItems[1] = isCheckedProteins;
        checkedItems[2] = isCheckedFats;
        checkedItems[3] = isCheckedCarbohydrates;

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AppDialog);

        builder.setTitle("Отображать информацию о:")
                .setCancelable(false)
                // добавляем одну кнопку для закрытия диалога
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int id) {
                                String eatenCountString = "";
                                String spentCountString = "";
                                String remainCountString = "";

                                boolean isCheckedCalories = sharedPref.getBoolean("IsCheckedCalories", false);
                                boolean isCheckedProteins = sharedPref.getBoolean("IsCheckedProteins", false);
                                boolean isCheckedFats = sharedPref.getBoolean("IsCheckedFats", false);
                                boolean isCheckedCarbohydrates = sharedPref.getBoolean("IsCheckedCarbohydrates", false);


                                if (isCheckedCalories) {
                                    eatenCountString = "K - 100";
                                    spentCountString = "K - 50";
                                    remainCountString = "K - 50";
                                } if (isCheckedProteins) {
                                    if (eatenCountString.equals("")) {
                                        eatenCountString = "Б - 200";
                                        spentCountString = "Б - 100";
                                        remainCountString = "Б - 100";
                                    } else {
                                        eatenCountString = eatenCountString + "\n" + "Б - 200";
                                        spentCountString = spentCountString + "\n" + "Б - 100";
                                        remainCountString = remainCountString + "\n" + "Б - 100";
                                    }
                                } if(isCheckedFats) {
                                    if (eatenCountString.equals("")) {
                                        eatenCountString = "Ж - 300";
                                        spentCountString = "Ж - 150";
                                        remainCountString = "Ж - 150";
                                    } else {
                                        eatenCountString = eatenCountString + "\n" + "Ж - 300";
                                        spentCountString = spentCountString + "\n" + "Ж - 150";
                                        remainCountString = remainCountString + "\n" + "Ж - 150";
                                    }
                                } if(isCheckedCarbohydrates) {
                                    if (eatenCountString.equals("")) {
                                        eatenCountString = "У - 400";
                                        spentCountString = "У - 200";
                                        remainCountString = "У - 200";
                                    } else {
                                        eatenCountString = eatenCountString + "\n" + "У - 400";
                                        spentCountString = spentCountString + "\n" + "У - 200";
                                        remainCountString = remainCountString + "\n" + "У - 200";
                                    }
                                } if(!isCheckedCalories && !isCheckedProteins && !isCheckedFats && !isCheckedCarbohydrates) {
                                    eatenCountString = "---";
                                    spentCountString = "---";
                                    remainCountString = "---";
                                }
                                mEatenCountTextView.setText(eatenCountString);
                                mSpentCountTextView.setText(spentCountString);
                                mRemainCountTextView.setText(remainCountString);

                                dialog.cancel();
                            }
                        })
                // Specify the list array, the items to be selected by default (null for none),
                // and the listener through which to receive callbacks when items are selected
                .setMultiChoiceItems(listItems, checkedItems,
                        new DialogInterface.OnMultiChoiceClickListener() {


                            @Override
                            public void onClick(DialogInterface dialog, int item,
                                                boolean isChecked) {

                                SharedPreferences.Editor editor = sharedPref.edit();

                                if (item == 0) {
                                    if (isChecked) {
                                        editor.putBoolean("IsCheckedCalories", true);
                                    } else {
                                        editor.putBoolean("IsCheckedCalories", false);
                                    }

                                } else if (item == 1) {
                                    if (isChecked) {
                                        editor.putBoolean("IsCheckedProteins", true);
                                    } else {
                                        editor.putBoolean("IsCheckedProteins", false);
                                    }

                                } else if (item == 2) {
                                    if (isChecked) {
                                        editor.putBoolean("IsCheckedFats", true);
                                    } else {
                                        editor.putBoolean("IsCheckedFats", false);
                                    }

                                } else if (item == 3) {
                                    if (isChecked) {
                                        editor.putBoolean("IsCheckedCarbohydrates", true);
                                    } else {
                                        editor.putBoolean("IsCheckedCarbohydrates", false);
                                    }
                                }
                                editor.apply();
                            }
                        });



               /* // добавляем переключатели
                .setSingleChoiceItems(R.array.what_is_displayed_array, numberWhatIsDisplayed,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int item) {
                                SharedPreferences.Editor editor = sharedPref.edit();
                                editor.putInt("WhatIsDisplayed", item);
                                editor.apply();

                                if(item == 0) {
                                    mEatenCountTextView.setText("К-100" + "\n" + "Б-100");
                                    mSpentCountTextView.setText("К-100" + "\n" + "Б-100");
                                    mRemainCountTextView.setText("К-100" + "\n" + "Б-100");

                                } else if (item == 1) {
                                    mEatenCountTextView.setText("200");
                                    mSpentCountTextView.setText("100");
                                    mRemainCountTextView.setText("100");

                                } else  if(item == 2) {
                                    mEatenCountTextView.setText("300");
                                    mSpentCountTextView.setText("150");
                                    mRemainCountTextView.setText("150");

                                } else if (item == 3) {
                                    mEatenCountTextView.setText("400");
                                    mSpentCountTextView.setText("200");
                                    mRemainCountTextView.setText("200");

                                } else {
                                    mEatenCountTextView.setText("---");
                                    mSpentCountTextView.setText("---");
                                    mRemainCountTextView.setText("---");
                                }


                              *//* Intent intent = new Intent(Intent.ACTION_VIEW);
                               intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                               getActivity().startActivity(intent);*//*
                            }
                        })*/
        return builder.create();
    }



}

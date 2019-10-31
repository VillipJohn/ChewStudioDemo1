package com.sauno.androiddeveloper.chewstudiodemo.dialogFragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.sauno.androiddeveloper.chewstudiodemo.R;
import com.sauno.androiddeveloper.chewstudiodemo.UserPreferencesActivity;
import com.sauno.androiddeveloper.chewstudiodemo.UserProfileActivity;

//диологовое окно с выбором пользователей
public class SelectUserDialogFragment extends DialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        //final String[] restaurantStringArray = getRestaurantsFromDB();

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AppDialog);
        builder.setTitle("Выбор пользователя:")
                .setCancelable(false)
                .setPositiveButton("Новый пользователь",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                preferencesSetDefault();

                                Intent intent = new Intent(getActivity(), UserPreferencesActivity.class);
                                startActivity(intent);
                                dialogInterface.cancel();
                            }
                        })
                .setItems(UserProfileActivity.userNameArray,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int item) {
                                // Нужно ещё передавать название ресторана чтоб от этого шла выборка из базы
                               /* Intent intent = new Intent(getActivity(), ChoiceOfDishesActivity.class);
                                intent.putExtra("restaurant", restaurantIntArray[item]);
                                startActivity(intent);
                                dialog.cancel();*/
                                ((UserProfileActivity)getActivity()).setUserFromDB(UserProfileActivity.userNameArray[item]);
                            }

                });


                final AlertDialog dialog = builder.create();
                dialog.show();

                //YEEEEEEESSS
        final Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        LinearLayout.LayoutParams positiveButtonLL = (LinearLayout.LayoutParams) positiveButton.getLayoutParams();
        positiveButtonLL.width = ViewGroup.LayoutParams.MATCH_PARENT;
        positiveButton.setLayoutParams(positiveButtonLL);

        return dialog;
    }

    //установление настроек пользователя по умолчанию
    private void preferencesSetDefault() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        SharedPreferences.Editor editor = sp.edit();

        editor.putString(UserPreferencesActivity.KEY_USER_NAME_PREF, "");
        editor.putString(UserPreferencesActivity.KEY_USER_SEX_PREF, "");
        editor.putString(UserPreferencesActivity.KEY_USER_AGE_PREF, "");
        editor.putString(UserPreferencesActivity.KEY_USER_HEIGHT_PREF, "");
        editor.putString(UserPreferencesActivity.KEY_USER_WEIGHT_PREF, "");
        editor.putString(UserPreferencesActivity.KEY_USER_BLOOD_GROUPE_PREF, "");
        editor.putString(UserPreferencesActivity.KEY_USER_METHOD_OF_NUTRITION_PREF, "");
        editor.putString(UserPreferencesActivity.KEY_USER_TYPE_OF_FOOD_VEGETARIAN_PREF, "0");
        editor.putString(UserPreferencesActivity.KEY_USER_TYPE_OF_FOOD_LENTEN_PREF, "0");
        editor.putString(UserPreferencesActivity.KEY_USER_COMPATIBILITY_TYPE_OF_FOOD_PREF, "0");
        editor.putString(UserPreferencesActivity.KEY_USER_CHARACTERISTIC_ONE_PREF, "0");
        editor.putString(UserPreferencesActivity.KEY_USER_CHARACTERISTIC_TWO_PREF, "0");
        editor.putString(UserPreferencesActivity.KEY_USER_CHARACTERISTIC_THREE_PREF, "0");
        editor.putString(UserPreferencesActivity.KEY_USER_CHARACTERISTIC_FOUR_PREF, "0");
        editor.putString(UserPreferencesActivity.KEY_USER_CHARACTERISTIC_FIVE_PREF, "0");
        editor.putString(UserPreferencesActivity.KEY_USER_LIFESTYLE_PREF, "0");
        editor.putString(UserPreferencesActivity.KEY_USER_WOULD_YOU_LIKE_PREF, "0");
        editor.apply();
    }
}

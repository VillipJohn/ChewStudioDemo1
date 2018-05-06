package com.sauno.androiddeveloper.chewstudiodemo.dialogFragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.widget.Button;

import com.sauno.androiddeveloper.chewstudiodemo.R;

public class ChooseDesiredMealDialogFragment extends DialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final Button mRecommendedMealButton = getActivity().findViewById(R.id.recommendedMealButton);

        /*final TextView mCountCaloriesTextView = getActivity().findViewById(R.id.countCaloriesTextView);
        final TextView mCountProteinsTextView = getActivity().findViewById(R.id.countProteinsTextView);
        final TextView mCountGreaseTextView = getActivity().findViewById(R.id.countGreaseTextView);
        final TextView mCountCarbohydratesTextView = getActivity().findViewById(R.id.countCarbohydratesTextView);*/

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AppDialog);
        builder.setTitle("Выберите желаемый приём пищи:")
                .setCancelable(false)

                // добавляем одну кнопку для закрытия диалога
                .setNegativeButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int id) {
                                dialog.cancel();
                            }
                        })
                .setSingleChoiceItems(R.array.desired_meal_array_with_percent, -1,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int item) {
                                Resources res = getResources();
                                String[] desiredMealArray = res.getStringArray(R.array.desired_meal_array);

                                mRecommendedMealButton.setText(desiredMealArray[item]);




                              /* Intent intent = new Intent(Intent.ACTION_VIEW);
                               intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                               getActivity().startActivity(intent);*/
                            }
                        });



        return builder.create();
    }
}
package com.sauno.androiddeveloper.chewstudiodemo.adapter;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.sauno.androiddeveloper.chewstudiodemo.R;
import com.sauno.androiddeveloper.chewstudiodemo.data.DatabaseCreateHelper;
import com.sauno.androiddeveloper.chewstudiodemo.data.DishDBHelper;

public class ListDishesAdapter extends RecyclerView.Adapter<ListDishesAdapter.ViewHolder> {

    private String[] mDataset;

    boolean[] checked;

    private SharedPreferences mSharedPreferences;

    DatabaseCreateHelper databaseCreateHelper;

    private String dishName;

    private float countCalories;
    private float countProteins;
    private float countFats;
    private float countCarbohydrates;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        //public final View mView;
        // each data item is just a string in this case
        public TextView mDishTextView;
        public CheckBox mDishCheckBox;
        public ImageView mDishImageView;

        public ViewHolder(View view) {
            super(view);
            //mView = view;
            mDishTextView = view.findViewById(R.id.dishTextView);
            mDishCheckBox = view.findViewById(R.id.dishCheckBox);
            mDishImageView = view.findViewById(R.id.dishImageView);
        }

    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public ListDishesAdapter(String[] dataset) {
        mDataset = dataset;
        checked = new boolean[dataset.length];
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ListDishesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.dish_item, parent, false);

        ViewHolder vh = new ViewHolder(view);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element

        AlphaAnimation blinkanimation= new AlphaAnimation(1, 0); // Change alpha from fully visible to invisible
        blinkanimation.setDuration(300); // duration
        blinkanimation.setInterpolator(new LinearInterpolator()); // do not alter animation rate
        blinkanimation.setRepeatCount(1000); // Repeat animation infinitely
        blinkanimation.setRepeatMode(Animation.REVERSE);

        holder.mDishImageView.setAnimation(blinkanimation);



        holder.mDishTextView.setText(mDataset[position]);

        holder.mDishCheckBox.setChecked(checked[position]);
        holder.mDishCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checked[position] = !checked[position];

                String text = mDataset[position];
                Toast.makeText(view.getContext(), text, Toast.LENGTH_SHORT).show();

            }
        });
        holder.mDishCheckBox.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                String text = mDataset[position] + "LONG CLICK";
                Toast.makeText(view.getContext(), text, Toast.LENGTH_SHORT).show();
                return false;
            }
        });

            //final String category = mDataset[position];
            dishName = mDataset[position];

            /*holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    databaseCreateHelper = new DatabaseCreateHelper(v.getContext());
                    getDataFromDB();

                    mSharedPreferences = v.getContext().getSharedPreferences("smartchewpref", Context.MODE_PRIVATE);

                    setChange();

                    ((ListDishesActivity)v.getContext()).finish();


                   *//* Intent intent = new Intent(v.getContext(), UserPreferencesActivity.class);
                    intent.putExtra("category", category);
                    v.getContext().startActivity(intent);*//*
                }
            });*/

            /*holder.mDishCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {

                }
            });
*/
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
            return mDataset.length;
    }

    private void setChange() {
        int countChosenDishes = mSharedPreferences.getInt("CountChosenDishes", 0);
        float countCaloriesSP = mSharedPreferences.getFloat("CountCalories", 0f);
        float countProteinsSP = mSharedPreferences.getFloat("CountProteins", 0f);
        float countFatsSP = mSharedPreferences.getFloat("CountFats", 0f);
        float countCarbohydratesSP = mSharedPreferences.getFloat("CountCarbohydrates", 0f);

        countChosenDishes++;
        countCalories = countCalories + countCaloriesSP;
        countProteins = countProteins + countProteinsSP;
        countFats = countFats + countFatsSP;
        countCarbohydrates = countCarbohydrates + countCarbohydratesSP;

        Log.i("MyLog", "countCalories = " + countCalories);
        Log.i("MyLog", "countProteins = " + countProteins);
        Log.i("MyLog", "countFats = " + countFats);
        Log.i("MyLog", "countCarbohydrates = " + countCarbohydrates);

        SharedPreferences.Editor editor = mSharedPreferences.edit();

        editor.putInt("CountChosenDishes", countChosenDishes);
        editor.putFloat("CountCalories", countCalories);
        editor.putFloat("CountProteins", countProteins);
        editor.putFloat("CountFats", countFats);
        editor.putFloat("CountCarbohydrates", countCarbohydrates);

        editor.apply();

    }

    private void getDataFromDB() {

        SQLiteDatabase db;
        try {
            db = databaseCreateHelper.getWritableDatabase();
        }
        catch (SQLiteException ex){
            db = databaseCreateHelper.getReadableDatabase();
        }

        String[] projection = {
                DishDBHelper.COLUMN_CALORIC,
                DishDBHelper.COLUMN_PROTEINS,
                DishDBHelper.COLUMN_FATS,
                DishDBHelper.COLUMN_CARBS
        };

        // Filter results WHERE "title" = 'My Title'
        String selection = DishDBHelper.COLUMN_DESCRIPTION + " = ?";
        String[] selectionArgs = {dishName};


        Cursor cursor = db.query(
                DishDBHelper.TABLE,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        if (cursor.moveToFirst()) {
            countCalories = cursor.getFloat(cursor.getColumnIndexOrThrow(DishDBHelper.COLUMN_CALORIC));
            countProteins = cursor.getFloat(cursor.getColumnIndexOrThrow(DishDBHelper.COLUMN_PROTEINS));
            countFats = cursor.getFloat(cursor.getColumnIndexOrThrow(DishDBHelper.COLUMN_FATS));
            countCarbohydrates = cursor.getFloat(cursor.getColumnIndexOrThrow(DishDBHelper.COLUMN_CARBS));
        }


        //List<String> categories = new ArrayList<>();

        /*if (cursor.moveToFirst()) {

            do {
                String category = cursor.getString(cursor.getColumnIndexOrThrow(DishDBHelper.COLUMN_DESCRIPTION));
                categories.add(category);
                Log.i("MyLog",category + "\n");

            }while (cursor.moveToNext());

        }*/

        //myDataset = categories.toArray(new String[categories.size()]);

        cursor.close();
        db.close();
    }

}

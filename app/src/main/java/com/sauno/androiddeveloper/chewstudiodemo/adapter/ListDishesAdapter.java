package com.sauno.androiddeveloper.chewstudiodemo.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sauno.androiddeveloper.chewstudiodemo.ListDishesActivity;
import com.sauno.androiddeveloper.chewstudiodemo.R;
import com.sauno.androiddeveloper.chewstudiodemo.data.DatabaseCreateHelper;
import com.sauno.androiddeveloper.chewstudiodemo.data.DishDBHelper;

public class ListDishesAdapter extends RecyclerView.Adapter<ListDishesAdapter.ViewHolder> {

    private String[] mDataset;

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
            public final View mView;
            // each data item is just a string in this case
            public TextView mTextView;
            public ViewHolder(View view) {
                super(view);
                mView = view;

                mTextView = view.findViewById(R.id.content);
            }
        }

        // Provide a suitable constructor (depends on the kind of dataset)
        public ListDishesAdapter(String[] myDataset) {
            mDataset = myDataset;
        }

        // Create new views (invoked by the layout manager)
        @Override
        public ListDishesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                       int viewType) {
            // create a new view
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.dish_item, parent, false);

            ViewHolder vh = new ViewHolder(view);
            return vh;
        }

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            // - get element from your dataset at this position
            // - replace the contents of the view with that element
            holder.mTextView.setText(mDataset[position]);

            //final String category = mDataset[position];
            dishName = mDataset[position];

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    databaseCreateHelper = new DatabaseCreateHelper(v.getContext());
                    getDataFromDB();

                    mSharedPreferences = v.getContext().getSharedPreferences("smartchewpref", Context.MODE_PRIVATE);

                    setChange();

                    ((ListDishesActivity)v.getContext()).finish();


                   /* Intent intent = new Intent(v.getContext(), UserPreferencesActivity.class);
                    intent.putExtra("category", category);
                    v.getContext().startActivity(intent);*/
                }
            });

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

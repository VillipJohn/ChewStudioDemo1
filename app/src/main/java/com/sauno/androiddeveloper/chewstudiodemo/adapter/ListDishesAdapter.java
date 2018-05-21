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

import com.sauno.androiddeveloper.chewstudiodemo.ListDishesActivity;
import com.sauno.androiddeveloper.chewstudiodemo.R;
import com.sauno.androiddeveloper.chewstudiodemo.data.DatabaseCreateHelper;
import com.sauno.androiddeveloper.chewstudiodemo.data.DishDBHelper;
import com.sauno.androiddeveloper.chewstudiodemo.dialogFragments.SelectNumberOfDishes;

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

    private String[] compatibilityArray = new String[7];

    private final int[][] compatibilityOfElementsArray = {
            {0,2,2,2,2,2,2,2,2,4,3,2,2,2,2,2},
            {2,0,2,4,4,2,3,2,2,4,4,2,2,2,2,2},
            {2,2,0,3,2,2,4,4,2,4,4,3,2,3,2,2},
            {2,4,3,0,3,2,4,4,3,4,4,2,4,3,3,2},
            {2,4,2,3,0,2,4,4,3,4,4,2,2,2,2,4},
            {2,2,2,2,2,0,2,2,2,4,2,2,2,2,2,2},
            {2,3,4,4,4,2,0,2,2,4,4,2,2,3,2,3},
            {2,2,4,4,4,2,2,0,3,4,3,2,3,4,2,4},
            {2,2,2,3,3,2,2,3,0,4,3,3,4,2,2,3},
            {4,4,4,4,4,4,4,4,4,0,4,2,4,4,4,4},
            {3,4,4,4,4,2,4,3,3,4,0,3,4,4,3,4},
            {2,2,3,2,2,2,2,2,3,2,3,0,2,2,2,2},
            {2,2,2,4,2,2,2,3,4,4,4,2,0,4,2,4},
            {2,2,3,3,2,2,3,4,2,4,4,2,4,0,2,3},
            {2,2,2,3,2,2,2,2,2,4,3,2,2,2,0,2},
            {2,3,2,2,4,2,3,4,3,4,4,2,4,3,2,0}};

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        //public final View mView;
        // each data item is just a string in this case
        public CheckBox mDishCheckBox;
        public TextView mDishCountTextView;

        public TextView mDishNameTextView;
        public ImageView mDishImageView;


        public ViewHolder(View view) {
            super(view);
            //mView = view;
            mDishCheckBox = view.findViewById(R.id.dishCheckBox);
            mDishCountTextView = view.findViewById(R.id.dishCountTextView);

            mDishNameTextView = view.findViewById(R.id.dishNameTextView);
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
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element

       /* AlphaAnimation blinkanimation= new AlphaAnimation(1, 0); // Change alpha from fully visible to invisible
        blinkanimation.setDuration(300); // duration
        blinkanimation.setInterpolator(new LinearInterpolator()); // do not alter animation rate
        blinkanimation.setRepeatCount(1000); // Repeat animation infinitely
        blinkanimation.setRepeatMode(Animation.REVERSE);
*/
        //holder.mDishImageView.setAnimation(blinkanimation);

        if(position == 0 || position == 4 || position == 8) {
            holder.mDishImageView.setImageResource(R.drawable.star_green);
        }
        if(position == 1 || position == 5 || position == 9) {
            holder.mDishImageView.setImageResource(R.drawable.star_yellow);
        }
        if(position == 2 || position == 6 || position == 10) {
            holder.mDishImageView.setImageResource(R.drawable.star_red);
        }
        if(position == 3 || position == 7 || position == 11) {
            AlphaAnimation blinkanimation= new AlphaAnimation(1, 0); // Change alpha from fully visible to invisible
            blinkanimation.setDuration(300); // duration
            blinkanimation.setInterpolator(new LinearInterpolator()); // do not alter animation rate
            blinkanimation.setRepeatCount(1000); // Repeat animation infinitely
            blinkanimation.setRepeatMode(Animation.REVERSE);
            holder.mDishImageView.setAnimation(blinkanimation);
        }


        //holder.mDishCheckBox.setVisibility(View.GONE);

        //holder.mDishCountTextView.setVisibility(View.VISIBLE);



        holder.mDishNameTextView.setText(mDataset[position]);

        holder.mDishCheckBox.setChecked(checked[position]);
        holder.mDishCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checked[position] = !checked[position];

                String text = mDataset[position];
                //Toast.makeText(view.getContext(), text, Toast.LENGTH_SHORT).show();

                dishName = mDataset[position];

                databaseCreateHelper = new DatabaseCreateHelper(view.getContext());
                getDataFromDB();

            }
        });
        holder.mDishCheckBox.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                SelectNumberOfDishes mSelectNumberOfDishes = new SelectNumberOfDishes();

                //Context context = view.getContext();

                //ListDishesActivity myActivity = getActivity(view);

                ListDishesActivity myActivity = (ListDishesActivity) view.getRootView().getContext();

                mSelectNumberOfDishes.show(myActivity.getSupportFragmentManager(),
                        "selectNumberOfDishes");

                //view.holder.mDishCountTextView.setVisibility(View.VISIBLE);

                String text = mDataset[position] + "LONG CLICK";

                holder.mDishCheckBox.setVisibility(View.GONE);
                holder.mDishCountTextView.setVisibility(View.VISIBLE);

                //Toast.makeText(view.getContext(), text, Toast.LENGTH_SHORT).show();
                return true;

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
                DishDBHelper.COLUMN_CARBS,
                DishDBHelper.COLUMN_COMPATIBILITY_1,
                DishDBHelper.COLUMN_COMPATIBILITY_2,
                DishDBHelper.COLUMN_COMPATIBILITY_3,
                DishDBHelper.COLUMN_COMPATIBILITY_4,
                DishDBHelper.COLUMN_COMPATIBILITY_5,
                DishDBHelper.COLUMN_COMPATIBILITY_6,
                DishDBHelper.COLUMN_COMPATIBILITY_7,
        };

        /*String[] projection = {
                DishDBHelper.COLUMN_CALORIC,
                DishDBHelper.COLUMN_PROTEINS,
                DishDBHelper.COLUMN_FATS,
                DishDBHelper.COLUMN_CARBS
        };*/

        // Filter results WHERE "title" = 'My Title'
        String selection = DishDBHelper.COLUMN_DESCRIPTION + " = ?";
        String[] selectionArgs = {dishName};

        String compatibility1 = "";
        String compatibility2 = "";
        String compatibility3 = "";
        String compatibility4 = "";
        String compatibility5 = "";
        String compatibility6 = "";
        String compatibility7 = "";



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
            compatibility1 = cursor.getString(cursor.getColumnIndexOrThrow(DishDBHelper.COLUMN_COMPATIBILITY_1));
            compatibility2 = cursor.getString(cursor.getColumnIndexOrThrow(DishDBHelper.COLUMN_COMPATIBILITY_2));
            compatibility3 = cursor.getString(cursor.getColumnIndexOrThrow(DishDBHelper.COLUMN_COMPATIBILITY_3));
            compatibility4 = cursor.getString(cursor.getColumnIndexOrThrow(DishDBHelper.COLUMN_COMPATIBILITY_4));
            compatibility5 = cursor.getString(cursor.getColumnIndexOrThrow(DishDBHelper.COLUMN_COMPATIBILITY_5));
            compatibility6 = cursor.getString(cursor.getColumnIndexOrThrow(DishDBHelper.COLUMN_COMPATIBILITY_6));
            compatibility7 = cursor.getString(cursor.getColumnIndexOrThrow(DishDBHelper.COLUMN_COMPATIBILITY_7));
        }
       /* if (cursor.moveToFirst()) {
            countCalories = cursor.getFloat(cursor.getColumnIndexOrThrow(DishDBHelper.COLUMN_CALORIC));
            countProteins = cursor.getFloat(cursor.getColumnIndexOrThrow(DishDBHelper.COLUMN_PROTEINS));
            countFats = cursor.getFloat(cursor.getColumnIndexOrThrow(DishDBHelper.COLUMN_FATS));
            countCarbohydrates = cursor.getFloat(cursor.getColumnIndexOrThrow(DishDBHelper.COLUMN_CARBS));
        }*/



       /* int compInt1 = setIntForCompatibility(compatibility1);
        int compInt1 = setIntForCompatibility(compatibility1);
        int compInt1 = setIntForCompatibility(compatibility1);
        int compInt1 = setIntForCompatibility(compatibility1);
        int compInt1 = setIntForCompatibility(compatibility1);
        int compInt1 = setIntForCompatibility(compatibility1);
        int compInt1 = setIntForCompatibility(compatibility1);*/

        //Log.i("MyLog", "comp1  -  " + compInt1);

        int[] compatibilityIntArray = {
                setIntForCompatibility(compatibility1),
                setIntForCompatibility(compatibility2),
                setIntForCompatibility(compatibility3),
                setIntForCompatibility(compatibility4),
                setIntForCompatibility(compatibility5),
                setIntForCompatibility(compatibility6),
                setIntForCompatibility(compatibility7)};

        Log.i("MyLog",
                compatibilityIntArray[0] + "\n" +
                        compatibilityIntArray[1] + "\n" +
                        compatibilityIntArray[2] + "\n" +
                        compatibilityIntArray[3] + "\n" +
                        compatibilityIntArray[4] + "\n" +
                        compatibilityIntArray[5] + "\n" +
                        compatibilityIntArray[6]);


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

    private int setIntForCompatibility(String compatibility) {
        if(!(compatibility == null)) {
            switch(compatibility) {
                case "мясо, рыба, птица (постные)":
                    return 0;
                case "зернобобовые":
                    return 1;
                case "масло сливочное, сливки":
                    return 2;
                case "сметана":
                    return 3;
                case "масло растительное":
                    return 4;
                case "сахар, кондитерские изделия":
                    return 5;
                case "хлеб, крупы, картофель":
                    return 6;
                case "фрукты кислые, помидоры":
                    return 7;
                case "фрукты сладкие, сухофрукты":
                    return 8;
                case"овощи зеленые и некрахмальные":
                    return 9;
                case"овощи крахмальные":
                    return 10;
                case"молоко":
                    return 11;
                case"творог, кисло-молочные продукты":
                    return 12;
                case"сыр, брынза":
                    return 13;
                case"яйца":
                    return 14;
                case"орехи":
                    return 15;
                default:
                    return -1;
            }

        }
        return -1;
        }

}

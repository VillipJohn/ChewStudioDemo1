package com.sauno.androiddeveloper.chewstudiodemo.adapter;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.sauno.androiddeveloper.chewstudiodemo.R;
import com.sauno.androiddeveloper.chewstudiodemo.ResultsOfEatingActivity;
import com.sauno.androiddeveloper.chewstudiodemo.data.DatabaseCreateHelper;
import com.sauno.androiddeveloper.chewstudiodemo.data.FavoriteDishesDBHelper;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

//вспомогательный класс для отображения списка на экране ResultOfEatingActivity
public class ResultsOfEatingAdapter extends RecyclerView.Adapter<ResultsOfEatingAdapter.ViewHolder> {
    private ResultsOfEatingActivity resultsOfEatingActivity;

    private ArrayList<String> dishNameArrayList;

    private ArrayList<Boolean> isFavoriteDishArray = new ArrayList<>();

    private boolean isFavoriteDish = false;

    private int idForFavoriteDishesTable;

    private String[] namesDishColumns = {
            "Dish_1",
            "Dish_2",
            "Dish_3",
            "Dish_4",
            "Dish_5",
            "Dish_6",
            "Dish_7",
            "Dish_8",
            "Dish_9",
            "Dish_10",
            "Dish_11",
            "Dish_12",
            "Dish_13",
            "Dish_14",
            "Dish_15",
            "Dish_16",
            "Dish_17",
            "Dish_18",
            "Dish_19",
            "Dish_20"};

    // Provide a suitable constructor (depends on the kind of dataset)
    public ResultsOfEatingAdapter(Activity resultOfEatingActivity, ArrayList<String> dishNameArrayList) {
        this.resultsOfEatingActivity = (ResultsOfEatingActivity) resultOfEatingActivity;
        this.dishNameArrayList = dishNameArrayList;

        SharedPreferences mSharedPreferences = resultOfEatingActivity.getSharedPreferences(resultOfEatingActivity.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        idForFavoriteDishesTable = mSharedPreferences.getInt("idForFavoriteDishes", -1);
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;

        public TextView mTextView;
        public TextView percentTextView;
        public ImageView minusImageView;
        public ImageView plusImageView;
        public ImageView favoriteImageView;

        public ViewHolder(View view) {
            super(view);
            mView = view;

            mTextView = view.findViewById(R.id.dishNameTextView);
            percentTextView = view.findViewById(R.id.percentTextView);
            minusImageView = view.findViewById(R.id.minusImageView);
            plusImageView = view.findViewById(R.id.plusImageView);
            favoriteImageView = view.findViewById(R.id.favoriteImageView);
        }
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ResultsOfEatingAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                                int viewType) {
        // create a new view
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.dish_results_item, parent, false);

        ViewHolder vh = new ViewHolder(view);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.mTextView.setText(dishNameArrayList.get(position));

        holder.percentTextView.setText(resultsOfEatingActivity.percentArrayList.get(position) + "%");

        Log.i("MyLogResultAdapter", "id = " + resultsOfEatingActivity.idesArrayList.get(position));

        //проверка есть ли это блюдо в списке любимых блюд и установление соответственной картинки напротив него
        if(checkIsFavoriteDish(position)) {
            holder.favoriteImageView.setImageResource(R.drawable.ic_action_bar_favorite_red);
            isFavoriteDishArray.add(true);
        } else {
            holder.favoriteImageView.setImageResource(R.drawable.ic_action_bar_favorite_white);
            isFavoriteDishArray.add(false);
        }

        //реализация обработчика кнопки МИНУС
        holder.minusImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(resultsOfEatingActivity.percentArrayList.get(position) > 0) {
                    int res = resultsOfEatingActivity.percentArrayList.get(position).intValue() - 10;
                    resultsOfEatingActivity.percentArrayList.set(position, res);

                    holder.percentTextView.setText(resultsOfEatingActivity.percentArrayList.get(position) + "%");

                    //установление изменённых данных КБЖУХ
                    setChangedCPFCX();
                }
            }
        });

        //реализация обработчика кнопки ПЛЮС
        holder.plusImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(resultsOfEatingActivity.percentArrayList.get(position) < 100) {
                    int res = resultsOfEatingActivity.percentArrayList.get(position).intValue() + 10;
                    resultsOfEatingActivity.percentArrayList.set(position, res);

                    holder.percentTextView.setText(resultsOfEatingActivity.percentArrayList.get(position) + "%");

                    //установление изменённых данных КБЖУХ
                    setChangedCPFCX();
                }

            }
        });

        //реализация обработчика ЛЮБИМОЕ БЛЮДО
        holder.favoriteImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isFavoriteDishArray.get(position)) {
                    holder.favoriteImageView.setImageResource(R.drawable.ic_action_bar_favorite_white);
                    deleteFromRowOfFavoriteDishTable(position);
                    Toast toast = Toast.makeText(view.getContext(), "Удалено из списка любимых блюд", Toast.LENGTH_SHORT);
                    showMyToast(toast, 1000);
                    isFavoriteDishArray.set(position, false);
                } else {
                    holder.favoriteImageView.setImageResource(R.drawable.ic_action_bar_favorite_red);
                    addToRowOfFavoriteDishTable(position);
                    Toast toast = Toast.makeText(view.getContext(), "Добавлено в список любимых блюд", Toast.LENGTH_SHORT);
                    showMyToast(toast, 1000);
                    isFavoriteDishArray.set(position, true);
                }
            }
        });

    }

    public void showMyToast(final Toast toast, final int delay) {
        final Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                toast.show();
            }
        }, 0, 1000);
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                toast.cancel();
                timer.cancel();
            }
        }, delay);
    }

    //проверка есть ли это блюдо в списке любимых блюд
    private boolean checkIsFavoriteDish(int position) {
        for(int n : resultsOfEatingActivity.idFavoriteDishes) {
            Log.i("MyLogResultAdapter", "idFavoriteDish = " + n);
            if(resultsOfEatingActivity.idesArrayList.get(position) == n) {
                return true;
            }
        }
        return false;
    }

    //установление изменённых данных КБЖУХ
    private void setChangedCPFCX() {
        resultsOfEatingActivity.setSumCPFCX(resultsOfEatingActivity.percentArrayList);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return dishNameArrayList.size();
    }

    //удаление блюда из таблицы любимых блюд
    private void deleteFromRowOfFavoriteDishTable(int position) {
        int[] idFavoriteDishes = resultsOfEatingActivity.idFavoriteDishes;

        int numberDishColumn = 21;

        for(int i = 0; i < idFavoriteDishes.length; i++) {
            if(resultsOfEatingActivity.idesArrayList.get(position) == idFavoriteDishes[i]) {
                numberDishColumn = i;
            }
        }

        int[] newIdFavoriteDishes = new int[idFavoriteDishes.length];

        for(int i = 0; i < idFavoriteDishes.length; i++) {
            if(i == idFavoriteDishes.length-1) {
                newIdFavoriteDishes[i] = 0;
                break;
            }
            if(i < numberDishColumn) {
                newIdFavoriteDishes[i] = idFavoriteDishes[i];
                continue;
            }
            newIdFavoriteDishes[i] = idFavoriteDishes[i + 1];
        }

        resultsOfEatingActivity.idFavoriteDishes = newIdFavoriteDishes;

        for (int n : newIdFavoriteDishes) {
            Log.i("MyLogAboutDish", "n = " + n);
        }

        DatabaseCreateHelper databaseCreateHelper = new DatabaseCreateHelper(resultsOfEatingActivity);
        SQLiteDatabase db = databaseCreateHelper.getWritableDatabase();

        ContentValues favoritDishValue = new ContentValues();
        favoritDishValue.put(namesDishColumns[0], newIdFavoriteDishes[0]);
        favoritDishValue.put(namesDishColumns[1], newIdFavoriteDishes[1]);
        favoritDishValue.put(namesDishColumns[2], newIdFavoriteDishes[2]);
        favoritDishValue.put(namesDishColumns[3], newIdFavoriteDishes[3]);
        favoritDishValue.put(namesDishColumns[4], newIdFavoriteDishes[4]);
        favoritDishValue.put(namesDishColumns[5], newIdFavoriteDishes[5]);
        favoritDishValue.put(namesDishColumns[6], newIdFavoriteDishes[6]);
        favoritDishValue.put(namesDishColumns[7], newIdFavoriteDishes[7]);
        favoritDishValue.put(namesDishColumns[8], newIdFavoriteDishes[8]);
        favoritDishValue.put(namesDishColumns[9], newIdFavoriteDishes[9]);
        favoritDishValue.put(namesDishColumns[10], newIdFavoriteDishes[10]);
        favoritDishValue.put(namesDishColumns[11], newIdFavoriteDishes[11]);
        favoritDishValue.put(namesDishColumns[12], newIdFavoriteDishes[12]);
        favoritDishValue.put(namesDishColumns[13], newIdFavoriteDishes[13]);
        favoritDishValue.put(namesDishColumns[14], newIdFavoriteDishes[14]);
        favoritDishValue.put(namesDishColumns[15], newIdFavoriteDishes[15]);
        favoritDishValue.put(namesDishColumns[16], newIdFavoriteDishes[16]);
        favoritDishValue.put(namesDishColumns[17], newIdFavoriteDishes[17]);
        favoritDishValue.put(namesDishColumns[18], newIdFavoriteDishes[18]);
        favoritDishValue.put(namesDishColumns[19], newIdFavoriteDishes[19]);

        String selection = FavoriteDishesDBHelper.COLUMN_ID + " = ?";
        String idString = "" + idForFavoriteDishesTable;
        String[] selectionArgs = {idString};

        db.update(FavoriteDishesDBHelper.TABLE,
                favoritDishValue,
                selection,
                selectionArgs);

        db.close();
    }

    //добавление блюда в таблицу любимых блюд
    private void addToRowOfFavoriteDishTable(int position) {
        int emptyNumber = getEmptyNumberDishColumn();

        if(emptyNumber == -1) {
            Toast.makeText(resultsOfEatingActivity, "База двнных любимых блюд имеет только 20 возможных полей. ", Toast.LENGTH_SHORT).show();
        } else {
            DatabaseCreateHelper databaseCreateHelper = new DatabaseCreateHelper(resultsOfEatingActivity);
            SQLiteDatabase db = databaseCreateHelper.getWritableDatabase();

            ContentValues favoritDishValue = new ContentValues();
            favoritDishValue.put(namesDishColumns[emptyNumber], resultsOfEatingActivity.idesArrayList.get(position));

            String selection = FavoriteDishesDBHelper.COLUMN_ID + " = ?";
            String idString = "" + idForFavoriteDishesTable;
            String[] selectionArgs = {idString};

            db.update(FavoriteDishesDBHelper.TABLE,
                    favoritDishValue,
                    selection,
                    selectionArgs);

            db.close();
        }
    }

    //получение пустого номера таблицы в таблице любимых блюд
    private int getEmptyNumberDishColumn() {
        for(int n = 0; n < resultsOfEatingActivity.idFavoriteDishes.length; n++) {
            if(resultsOfEatingActivity.idFavoriteDishes[n] == 0) {
                return n;
            }
        }
        return -1;
    }


}

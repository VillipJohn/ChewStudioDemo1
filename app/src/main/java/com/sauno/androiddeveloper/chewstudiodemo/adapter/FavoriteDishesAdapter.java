package com.sauno.androiddeveloper.chewstudiodemo.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sauno.androiddeveloper.chewstudiodemo.AboutDishStatisticActivity;
import com.sauno.androiddeveloper.chewstudiodemo.FavoriteDishesActivity;
import com.sauno.androiddeveloper.chewstudiodemo.R;

//вспомогательный класс для отображения списка на экране FavoriteDishesActivity
public class FavoriteDishesAdapter extends RecyclerView.Adapter<FavoriteDishesAdapter.ViewHolder> {

    //объект той активити, в которой отображается список
    private FavoriteDishesActivity favoriteDishesActivity;

    // массив id
    private int[] idFavoriteDishes;

    // массив имён
    private String[] dishNames;

    // конструктор
    public FavoriteDishesAdapter(Context context, int[] idFavoriteDishes, String[] dishNames) {
        favoriteDishesActivity = (FavoriteDishesActivity)context;

        this.idFavoriteDishes = idFavoriteDishes;
        this.dishNames = dishNames;
    }

    // Создание новых View. Вызывает LayoutManager
    @Override
    public FavoriteDishesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        // create a new view
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_favorite_dish, parent, false);

        return new ViewHolder(view);
    }

    // удерживает объект View
    public static class ViewHolder extends RecyclerView.ViewHolder {

        // элемент содержащийся в item_favorite_dish
        public TextView favoriteDishNameTextView;

        public ViewHolder(View view) {
            super(view);

            favoriteDishNameTextView = view.findViewById(R.id.favoriteDishNameTextView);

        }

    }

    // Меняет содержимое view вызывается LayoutManager
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        // устанавливаем отображаемые данные
        holder.favoriteDishNameTextView.setText(dishNames[position]);

        //обработка длительного нажатия на название и вызов справке о блюде
        holder.favoriteDishNameTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // запускаем окно AboutDishActivity
                Intent intent = new Intent(view.getContext(), AboutDishStatisticActivity.class);

                int idDish = idFavoriteDishes[position];

                // передаём idDish данного блюда
                intent.putExtra("idDish", idDish);

                // передаём dishName данного блюда
                intent.putExtra("dishName", dishNames[position]);
                favoriteDishesActivity.startActivity(intent);
            }
        });

    }

    // Возвращает размер данных списка, вызывается LayoutManager
    @Override
    public int getItemCount() {
        return dishNames.length;
    }
}

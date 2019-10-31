package com.sauno.androiddeveloper.chewstudiodemo.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sauno.androiddeveloper.chewstudiodemo.R;
import com.sauno.androiddeveloper.chewstudiodemo.model.ConflictsForDish;

import java.util.ArrayList;

public class ConflictsAboutDishAdapter extends RecyclerView.Adapter<ConflictsAboutDishAdapter.ViewHolder> {

    // список конфликтующих ингредиентов
    ArrayList<ConflictsForDish> conflictsList;

    // конструктор
    public ConflictsAboutDishAdapter(ArrayList<ConflictsForDish> conflictsList) {
        this.conflictsList = conflictsList;
    }

    // Создание новых View. Вызывает LayoutManager
    @Override
    public ConflictsAboutDishAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                          int viewType) {
        // create a new view
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_item_conflict_about_dish, parent, false);

        ViewHolder vh = new ViewHolder(view);
        return vh;
    }

    // удерживает объект View
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public View mView;

        // элемент содержащийся в adapter_item_conflict
        public TextView oneTextView;
        public TextView twoTextView;
        public TextView threeTextView;


        public ViewHolder(View view) {
            super(view);
            mView = view;

            oneTextView = view.findViewById(R.id.oneTextView);
            twoTextView = view.findViewById(R.id.twoTextView);
            threeTextView = view.findViewById(R.id.threeTextView);

        }
    }


    // Меняет содержимое view вызывается LayoutManager
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        // устанавливаем отображаемые данные
        holder.oneTextView.setText(conflictsList.get(position).getOneString());
        holder.twoTextView.setText(conflictsList.get(position).getTwoString());
        holder.threeTextView.setText(conflictsList.get(position).getThreeString());

    }

    // Возвращает размер данных списка, вызывается LayoutManager
    @Override
    public int getItemCount() {
        return conflictsList.size();
    }
}




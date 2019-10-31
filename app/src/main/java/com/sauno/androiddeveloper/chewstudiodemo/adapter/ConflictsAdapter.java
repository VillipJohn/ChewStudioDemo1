package com.sauno.androiddeveloper.chewstudiodemo.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sauno.androiddeveloper.chewstudiodemo.R;

import java.util.ArrayList;

//вспомогательный класс для отображения списка на экране AboutIngredientActivity
public class ConflictsAdapter extends RecyclerView.Adapter<ConflictsAdapter.ViewHolder> {

    // список конфликтующих ингредиентов
    ArrayList<String> conflictsList;

    // конструктор
    public ConflictsAdapter(ArrayList<String> conflictsList) {
        this.conflictsList = conflictsList;
    }

    // Создание новых View. Вызывает LayoutManager
    @Override
    public ConflictsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                          int viewType) {
        // create a new view
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_item_conflict, parent, false);

        ViewHolder vh = new ViewHolder(view);
        return vh;
    }

    // удерживает объект View
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public View mView;

        // элемент содержащийся в adapter_item_conflict
        public TextView conflictTextView;


        public ViewHolder(View view) {
            super(view);
            mView = view;

            conflictTextView = view.findViewById(R.id.conflictTextView);

        }
    }


    // Меняет содержимое view вызывается LayoutManager
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        // устанавливаем отображаемые данные
        holder.conflictTextView.setText(conflictsList.get(position));

    }

    // Возвращает размер данных списка, вызывается LayoutManager
    @Override
    public int getItemCount() {
        return conflictsList.size();
    }
}



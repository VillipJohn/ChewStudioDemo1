package com.sauno.androiddeveloper.chewstudiodemo.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sauno.androiddeveloper.chewstudiodemo.R;

import java.util.List;

public class DishIngredientsAdapter extends RecyclerView.Adapter<DishIngredientsAdapter.ViewHolder> {
    // список блюд
    private List<String> ingredientNameList;

    // конструктор
    public DishIngredientsAdapter(List<String> ingredientNameList) {
        this.ingredientNameList = ingredientNameList;
    }

    @Override
    public DishIngredientsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                         int viewType) {
        // create a new view
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_dish_ingredients, parent, false);

        return new ViewHolder(view);
    }

    // удерживает объект View
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public View mView;

        public TextView ingredientNameTextView;

        public ViewHolder(View view) {
            super(view);
            mView = view;

            ingredientNameTextView = view.findViewById(R.id.ingredientNameTextView);
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.ingredientNameTextView.setText(ingredientNameList.get(position));
    }

    // Возвращает размер данных списка, вызывается LayoutManager
    @Override
    public int getItemCount() {
        return ingredientNameList.size();
    }
}

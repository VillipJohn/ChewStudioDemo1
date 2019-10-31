package com.sauno.androiddeveloper.chewstudiodemo.adapter;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sauno.androiddeveloper.chewstudiodemo.ChoiceOfDishesActivity;
import com.sauno.androiddeveloper.chewstudiodemo.ListDishesActivity;
import com.sauno.androiddeveloper.chewstudiodemo.R;
import com.sauno.androiddeveloper.chewstudiodemo.model.DishOrderItem;

//вспомогательный класс для отображения списка на экране ChoiceOfDishesActivity
public class ChoiceOfDishesAdapter extends RecyclerView.Adapter<ChoiceOfDishesAdapter.ViewHolder> {

    // массив названий категорий
    private String[] categoriesArray;

    // конструктор
    public ChoiceOfDishesAdapter(String[] categoriesArray) {
        this.categoriesArray = categoriesArray;
    }

    // Создание новых View. Вызывает LayoutManager
    @Override
    public ChoiceOfDishesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                               int viewType) {
        // create a new view
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_choice_of_dishes, parent, false);

        return new ViewHolder(view);
    }

    // удерживает объект View
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;

        // элементы содержащиеся в item_choice_of_dishes
        public TextView categoryTextView;
        public TextView quantityDishesTextView;
        public ViewHolder(View view) {
            super(view);
            mView = view;

            categoryTextView = view.findViewById(R.id.categoryTextView);
            quantityDishesTextView = view.findViewById(R.id.quantityDishesTextView);
        }
    }

    // Меняет содержимое view вызывается LayoutManager
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final String category = categoriesArray[position];

        // устанавливаем отображаемые данные
        holder.categoryTextView.setText(category);

        // Проверка какое колличество выбранных блюд в данной категории
        int quantity = checkQuantityInCategory(category);

        // Проверка просмотрена ли эта категория ранее
        boolean viewedCategory = isViewedCategory(category);

        // отображаем результаты проверки
        if(quantity > 0) {
            holder.quantityDishesTextView.setText("" + quantity);
        } else if(viewedCategory) {
            holder.quantityDishesTextView.setText("" + 0);
        } else {
            holder.quantityDishesTextView.setText("");
        }

        //обработка нажатия на данную категорию и вызов экрана списка блюд
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // добавляем данную категорию в список просмотренных
                ChoiceOfDishesActivity.viewedCategoriesList.add(category);

                // запускаем окно ListDishesActivity
                Intent intent = new Intent(v.getContext(), ListDishesActivity.class);

                // передаём название category
                intent.putExtra("category", category);
                v.getContext().startActivity(intent);
            }
        });

    }

    // Проверка какое колличество выбранных блюд в данной категории
    private int checkQuantityInCategory(String category) {
        int res = 0;

        for(DishOrderItem dish : ChoiceOfDishesActivity.dishOrderList) {
            if(dish.getCategory().equals(category)) {
                res = res + dish.getQuantityDishes().intValue();
            }
        }

        return res;
    }

    //Проверка просмотрена ли эта категория ранее
    private boolean isViewedCategory(String category) {
        boolean res = false;

        for(String viewedCategory : ChoiceOfDishesActivity.viewedCategoriesList) {
            if(viewedCategory.equals(category)) {
                res = true;
            }
        }

        return res;
    }

    // Возвращает размер данных списка, вызывается LayoutManager
    @Override
    public int getItemCount() {
        return categoriesArray.length;
    }
}

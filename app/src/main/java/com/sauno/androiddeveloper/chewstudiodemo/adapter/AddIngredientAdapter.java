package com.sauno.androiddeveloper.chewstudiodemo.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.sauno.androiddeveloper.chewstudiodemo.AboutIngredientActivity;
import com.sauno.androiddeveloper.chewstudiodemo.AddDishActivity;
import com.sauno.androiddeveloper.chewstudiodemo.AddIngredientActivity;
import com.sauno.androiddeveloper.chewstudiodemo.R;
import com.sauno.androiddeveloper.chewstudiodemo.model.Ingredient;

import java.util.List;

//вспомогательный класс для отображения списка на экране AddIngredientActivity
public class AddIngredientAdapter extends RecyclerView.Adapter<AddIngredientAdapter.ViewHolder> {

    //объект той активити, в которой отображается список
    private AddIngredientActivity addIngredientActivity;

    //список ингредиентов из базы данных
    private List<Ingredient> ingredientList;

    //массив необходимый для корректного отображения галочек
    private boolean[] checked;

    //объект необходимый для корректного отображения списка при пересоздании
    private RecyclerView.LayoutManager layoutManager;

    //конструктор
    public AddIngredientAdapter(Context context, List<Ingredient> ingredientList, RecyclerView.LayoutManager layoutManager) {
        this.ingredientList = ingredientList;

        checked = new boolean[ingredientList.size()];

        addIngredientActivity = (AddIngredientActivity) context;

        this.layoutManager = layoutManager;
    }

    // Создание новых View. Вызывает LayoutManager
    @Override
    public AddIngredientAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                              int viewType) {
        // create a new view
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_add_ingredient, parent, false);

        return new ViewHolder(view);
    }

    // удерживает объект View
    public static class ViewHolder extends RecyclerView.ViewHolder {

        // элементы содержащиеся в item_add_ingredient
        public TextView ingredientNameTextView;
        public CheckBox ingredientCheckBox;
        public ImageView ingredientImageView;

        public ViewHolder(View view) {
            super(view);

            ingredientNameTextView = view.findViewById(R.id.ingredientNameTextView);
            ingredientCheckBox = view.findViewById(R.id.ingredientCheckBox);
            ingredientImageView = view.findViewById(R.id.ingredientImageView);
        }
    }

    // Меняет содержимое view вызывается LayoutManager
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        // устанавливаем отображаемые данные
        holder.ingredientNameTextView.setText(ingredientList.get(position).getName());

        // проверка существует ли данный ингридиент в списке уже выбранных
        if(checkExistIngredient(position)) {
            holder.ingredientCheckBox.setChecked(true);

            // устанавливаем для корректного отображения галочек
            checked[position] = true;
        } else {

            // устанавливаем для корректного отображения галочек
            checked[position] = false;
        }

        // устанавливаем галочку(или не устанавливаем)
        holder.ingredientCheckBox.setChecked(checked[position]);

        //реализация обработчика чекбокса
        holder.ingredientCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // переустанавливаем корректное отображение галочки в данном элементе
                checked[position] = !checked[position];

                // необходимый объект для корректного отображения списка при пересоздании, в частности для открутки списка в то же место где и находилось
                Parcelable recylerViewState = layoutManager.onSaveInstanceState();

                if(checked[position]) {
                    // добавляем ингредиент в список
                    addIngredientActivity.addIngredient(ingredientList.get(position), recylerViewState);
                } else {
                    // удаляем ингредиент из списка
                    addIngredientActivity.removeIngredient(ingredientList.get(position), recylerViewState);
                }
            }
        });

        // получаем оценку совместимости
        int evaluation = ingredientList.get(position).getCompatibilityEvaluation();

        // установка картинки соответственно оценки совместимости
        if(evaluation == 2) {
            holder.ingredientImageView.setImageResource(R.drawable.star_red);
        }
        if(evaluation == 3) {
            holder.ingredientImageView.setImageResource(R.drawable.star_yellow);
        }
        if(evaluation == 4) {
            holder.ingredientImageView.setImageResource(R.drawable.star_green);
        }

        // обработчик события нажатия на ingredientImageView
        holder.ingredientImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // запускаем окно AboutIngredientActivity
                Intent intent = new Intent(addIngredientActivity, AboutIngredientActivity.class);

                // передаём данный объект Ingredient из списка ingredientList
                intent.putExtra("ingredient", ingredientList.get(position));
                addIngredientActivity.startActivity(intent);
            }
        });

    }



    //проверка выбран ли уже ингредиент
    private boolean checkExistIngredient(int position) {
        for(Ingredient ingredient : AddDishActivity.chosenIngredientsList) {
            if(ingredient.getName().equals(ingredientList.get(position).getName())) {
                return true;
            }
        }
        return false;
    }




    // Возвращает размер данных списка, вызывается LayoutManager
    @Override
    public int getItemCount() {
        return ingredientList.size();
    }
}


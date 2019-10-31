package com.sauno.androiddeveloper.chewstudiodemo.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sauno.androiddeveloper.chewstudiodemo.AboutDishActivity;
import com.sauno.androiddeveloper.chewstudiodemo.DishReplacementActivity;
import com.sauno.androiddeveloper.chewstudiodemo.R;
import com.sauno.androiddeveloper.chewstudiodemo.model.Dish;

import java.util.List;

import static android.app.Activity.RESULT_OK;

//вспомогательный класс для отображения списка на экране DishReplacementActivity
public class DishReplacementAdapter extends RecyclerView.Adapter<DishReplacementAdapter.ViewHolder>  {

    // список блюд
    private List<Dish> dishesList;

    //объект той активити, в которой отображается список
    private DishReplacementActivity dishReplacementActivity;

    // конструктор
    public DishReplacementAdapter(Context context, List<Dish> dishesList) {
        dishReplacementActivity = (DishReplacementActivity) context;

        this.dishesList = dishesList;

    }

    // Создание новых View. Вызывает LayoutManager
    @Override
    public DishReplacementAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                                int viewType) {
        // create a new view
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_dish_replacement, parent, false);

        return new ViewHolder(view);
    }

    // удерживает объект View
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public View mView;

        // элементы содержащиеся в item_dish_replacement
        public TextView dishNameReplacementTextView;
        public ImageView dishImageView;

        public ViewHolder(View view) {
            super(view);
            mView = view;

            dishNameReplacementTextView = view.findViewById(R.id.dishNameReplacementTextView);
            dishImageView = view.findViewById(R.id.dishImageView);
        }
    }



    // Меняет содержимое view вызывается LayoutManager
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        // устанавливаем отображаемые данные
        holder.dishNameReplacementTextView.setText(dishesList.get(position).getDishName());

        //установление цвета картинки
        setImageColor(holder.dishImageView, position);

        //обработка нажатия-выбора блюда, которое заменяется
        holder.dishNameReplacementTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // отправляем результат в вызывающую активити
                Intent intent = new Intent();

                // передаём данные
                intent.putExtra("idDish", dishesList.get(position).getIdDish());
                intent.putExtra("dishName", dishesList.get(position).getDishName());
                intent.putExtra("calories", dishesList.get(position).getDishCalories());
                intent.putExtra("proteins", dishesList.get(position).getDishProteins());
                intent.putExtra("fats", dishesList.get(position).getDishFats());
                intent.putExtra("carbs", dishesList.get(position).getDishCarbs());
                intent.putExtra("xe", dishesList.get(position).getDishXE());

                dishReplacementActivity.setResult(RESULT_OK, intent);
                dishReplacementActivity.finish();

            }
        });

        //обработка длительного нажатия и вызов справки о данном блюде
        holder.dishNameReplacementTextView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                // запускаем окно AboutDishActivity
                Intent intent = new Intent(view.getContext(), AboutDishActivity.class);

                int idDish = dishesList.get(position).getIdDish();

                // передаём idDish данного блюда
                intent.putExtra("idDish", idDish);

                // передаём dishName данного блюда
                intent.putExtra("dishName", dishesList.get(position).getDishName());
                dishReplacementActivity.startActivity(intent);

                return false;
            }
        });

    }

    // Возвращает размер данных списка, вызывается LayoutManager
    @Override
    public int getItemCount() {
        return dishesList.size();
    }

    //устанавливаем цвет картинки
    private void setImageColor(ImageView dishImageView, int position) {

        // получаем globalCategory данного блюда
        String globalCategory = dishesList.get(position).getGlobalCategory();

        // получаем оценку совместимости данного блюда
        int evaluation = dishesList.get(position).getCompatibilityEvaluation();

        switch(globalCategory) {
            case "супы" :
                if(evaluation == 2) {
                    dishImageView.setImageResource(R.drawable.soup_red);
                }
                if(evaluation == 3) {
                    dishImageView.setImageResource(R.drawable.soup_yellow);
                }
                if(evaluation == 4) {
                    dishImageView.setImageResource(R.drawable.soup_green);
                }
                break;
            case "горячие блюда из мяса" :
                if(evaluation == 2) {
                    dishImageView.setImageResource(R.drawable.hot_meat_dish_red);
                }
                if(evaluation == 3) {
                    dishImageView.setImageResource(R.drawable.hot_meat_dish_yellow);
                }
                if(evaluation == 4) {
                    dishImageView.setImageResource(R.drawable.hot_meat_dish_green);
                }
                break;
            case "горячие блюда из птицы" :
                if(evaluation == 2) {
                    dishImageView.setImageResource(R.drawable.hot_dish_of_poultry_red);
                }
                if(evaluation == 3) {
                    dishImageView.setImageResource(R.drawable.hot_dish_of_poultry_yellow);
                }
                if(evaluation == 4) {
                    dishImageView.setImageResource(R.drawable.hot_dish_of_poultry_green);
                }
                break;
            case "горячие блюда из рыбы и морепродуктов" :
                if(evaluation == 2) {
                    dishImageView.setImageResource(R.drawable.hot_fish_dish_red);
                }
                if(evaluation == 3) {
                    dishImageView.setImageResource(R.drawable.hot_fish_dish_yellow);
                }
                if(evaluation == 4) {
                    dishImageView.setImageResource(R.drawable.hot_fish_dish_green);
                }
                break;
            case "блюда из яиц" :
                if(evaluation == 2) {
                    dishImageView.setImageResource(R.drawable.egg_dish_red);
                }
                if(evaluation == 3) {
                    dishImageView.setImageResource(R.drawable.egg_dish_yellow);
                }
                if(evaluation == 4) {
                    dishImageView.setImageResource(R.drawable.egg_dish_green);
                }
                break;
            case "салаты" :
                if(evaluation == 2) {
                    dishImageView.setImageResource(R.drawable.salad_red);
                }
                if(evaluation == 3) {
                    dishImageView.setImageResource(R.drawable.salad_yellow);
                }
                if(evaluation == 4) {
                    dishImageView.setImageResource(R.drawable.salad_green);
                }
                break;
            case "гарниры" :
                if(evaluation == 2) {
                    dishImageView.setImageResource(R.drawable.garnish_red);
                }
                if(evaluation == 3) {
                    dishImageView.setImageResource(R.drawable.garnish_yellow);
                }
                if(evaluation == 4) {
                    dishImageView.setImageResource(R.drawable.garnish_green);
                }
                break;
            case "паста, спагетти, макароны, wok" :
                if(evaluation == 2) {
                    dishImageView.setImageResource(R.drawable.pasta_red);
                }
                if(evaluation == 3) {
                    dishImageView.setImageResource(R.drawable.pasta_yellow);
                }
                if(evaluation == 4) {
                    dishImageView.setImageResource(R.drawable.pasta_green);
                }
                break;
            case "плов, рис" :
                if(evaluation == 2) {
                    dishImageView.setImageResource(R.drawable.rice_red);
                }
                if(evaluation == 3) {
                    dishImageView.setImageResource(R.drawable.rice_yellow);
                }
                if(evaluation == 4) {
                    dishImageView.setImageResource(R.drawable.rice_green);
                }
                break;
            case "каши, мюсли" :
                if(evaluation == 2) {
                    dishImageView.setImageResource(R.drawable.kasha_red);
                }
                if(evaluation == 3) {
                    dishImageView.setImageResource(R.drawable.kasha_yellow);
                }
                if(evaluation == 4) {
                    dishImageView.setImageResource(R.drawable.kasha_green);
                }
                break;
            case "блины, оладьи, панкейки, драники" :
                if(evaluation == 2) {
                    dishImageView.setImageResource(R.drawable.pancakes_red);
                }
                if(evaluation == 3) {
                    dishImageView.setImageResource(R.drawable.pancakes_yellow);
                }
                if(evaluation == 4) {
                    dishImageView.setImageResource(R.drawable.pancakes_green);
                }
                break;
            case "сырники, запеканки" :
                if(evaluation == 2) {
                    dishImageView.setImageResource(R.drawable.cheesecake_red);
                }
                if(evaluation == 3) {
                    dishImageView.setImageResource(R.drawable.cheesecake_yellow);
                }
                if(evaluation == 4) {
                    dishImageView.setImageResource(R.drawable.cheesecake_green);
                }
                break;
            case "хлеб, булочки, багеты, чиабата, лепешки, лаваш " :
                if(evaluation == 2) {
                    dishImageView.setImageResource(R.drawable.bread_red);
                }
                if(evaluation == 3) {
                    dishImageView.setImageResource(R.drawable.bread_yellow);
                }
                if(evaluation == 4) {
                    dishImageView.setImageResource(R.drawable.bread_green);
                }
                break;
            case "вареники, пельмени, равиоли" :
                if(evaluation == 2) {
                    dishImageView.setImageResource(R.drawable.dumplings_red);
                }
                if(evaluation == 3) {
                    dishImageView.setImageResource(R.drawable.dumplings_yellow);
                }
                if(evaluation == 4) {
                    dishImageView.setImageResource(R.drawable.dumplings_green);
                }
                break;
            case "курзе, хинкал, манты" :
                if(evaluation == 2) {
                    dishImageView.setImageResource(R.drawable.khinkali_red);
                }
                if(evaluation == 3) {
                    dishImageView.setImageResource(R.drawable.khinkali_yellow);
                }
                if(evaluation == 4) {
                    dishImageView.setImageResource(R.drawable.khinkali_green);
                }
                break;
            case "хачапури, чуду, фокачча" :
                if(evaluation == 2) {
                    dishImageView.setImageResource(R.drawable.khachapuri_red);
                }
                if(evaluation == 3) {
                    dishImageView.setImageResource(R.drawable.khachapuri_yellow);
                }
                if(evaluation == 4) {
                    dishImageView.setImageResource(R.drawable.khachapuri_green);
                }
                break;
            case "голубцы, долма" :
                if(evaluation == 2) {
                    dishImageView.setImageResource(R.drawable.cabbage_rolls_red);
                }
                if(evaluation == 3) {
                    dishImageView.setImageResource(R.drawable.cabbage_rolls_yellow);
                }
                if(evaluation == 4) {
                    dishImageView.setImageResource(R.drawable.cabbage_rolls_green);
                }
                break;
            case "суши, сашими, роллы" :
                if(evaluation == 2) {
                    dishImageView.setImageResource(R.drawable.sushi_red);
                }
                if(evaluation == 3) {
                    dishImageView.setImageResource(R.drawable.sushi_yellow);
                }
                if(evaluation == 4) {
                    dishImageView.setImageResource(R.drawable.sushi_green);
                }
                break;
            case "сэндвичи, бургеры, бутерброды, буррито" :
                if(evaluation == 2) {
                    dishImageView.setImageResource(R.drawable.sandwich_red);
                }
                if(evaluation == 3) {
                    dishImageView.setImageResource(R.drawable.sandwich_yellow);
                }
                if(evaluation == 4) {
                    dishImageView.setImageResource(R.drawable.sandwich_green);
                }
                break;
            case "пироги, пирожки, чебуреки" :
                if(evaluation == 2) {
                    dishImageView.setImageResource(R.drawable.pie_red);
                }
                if(evaluation == 3) {
                    dishImageView.setImageResource(R.drawable.pie_yellow);
                }
                if(evaluation == 4) {
                    dishImageView.setImageResource(R.drawable.pie_green);
                }
                break;
            case "пицца, кальцоне" :
                if(evaluation == 2) {
                    dishImageView.setImageResource(R.drawable.pizza_red);
                }
                if(evaluation == 3) {
                    dishImageView.setImageResource(R.drawable.pizza_yellow);
                }
                if(evaluation == 4) {
                    dishImageView.setImageResource(R.drawable.pizza_green);
                }
                break;
            case "закуски холодные, соленья" :
                if(evaluation == 2) {
                    dishImageView.setImageResource(R.drawable.cold_snacks_red);
                }
                if(evaluation == 3) {
                    dishImageView.setImageResource(R.drawable.cold_snacks_yellow);
                }
                if(evaluation == 4) {
                    dishImageView.setImageResource(R.drawable.cold_snacks_green);
                }
                break;
            case "закуски горячие" :
                if(evaluation == 2) {
                    dishImageView.setImageResource(R.drawable.hot_snacks_red);
                }
                if(evaluation == 3) {
                    dishImageView.setImageResource(R.drawable.hot_snacks_yellow);
                }
                if(evaluation == 4) {
                    dishImageView.setImageResource(R.drawable.hot_snacks_green);
                }
                break;
            case "соусы" :
                if(evaluation == 2) {
                    dishImageView.setImageResource(R.drawable.sauce_red);
                }
                if(evaluation == 3) {
                    dishImageView.setImageResource(R.drawable.sauce_yellow);
                }
                if(evaluation == 4) {
                    dishImageView.setImageResource(R.drawable.sauce_green);
                }
                break;
            case "сметана, масло" :
                if(evaluation == 2) {
                    dishImageView.setImageResource(R.drawable.butter_red);
                }
                if(evaluation == 3) {
                    dishImageView.setImageResource(R.drawable.butter_yellow);
                }
                if(evaluation == 4) {
                    dishImageView.setImageResource(R.drawable.butter_green);
                }
                break;
            case "паштеты" :
                if(evaluation == 2) {
                    dishImageView.setImageResource(R.drawable.paste_red);
                }
                if(evaluation == 3) {
                    dishImageView.setImageResource(R.drawable.paste_yellow);
                }
                if(evaluation == 4) {
                    dishImageView.setImageResource(R.drawable.paste_green);
                }
                break;
            case "холодные напитки" :
                if(evaluation == 2) {
                    dishImageView.setImageResource(R.drawable.cold_drink_red);
                }
                if(evaluation == 3) {
                    dishImageView.setImageResource(R.drawable.cold_drink_yellow);
                }
                if(evaluation == 4) {
                    dishImageView.setImageResource(R.drawable.cold_drink_green);
                }
                break;
            case "горячие напитки" :
                if(evaluation == 2) {
                    dishImageView.setImageResource(R.drawable.hot_drink_red);
                }
                if(evaluation == 3) {
                    dishImageView.setImageResource(R.drawable.hot_drink_yellow);
                }
                if(evaluation == 4) {
                    dishImageView.setImageResource(R.drawable.hot_drink_green);
                }
                break;
            case "алкоголь, пиво" :
                if(evaluation == 2) {
                    dishImageView.setImageResource(R.drawable.alcohol_red);
                }
                if(evaluation == 3) {
                    dishImageView.setImageResource(R.drawable.alcohol_yellow);
                }
                if(evaluation == 4) {
                    dishImageView.setImageResource(R.drawable.alcohol_green);
                }
                break;

            case "десерты" :
                if(evaluation == 2) {
                    dishImageView.setImageResource(R.drawable.dessert_red);
                }
                if(evaluation == 3) {
                    dishImageView.setImageResource(R.drawable.dessert_yellow);
                }
                if(evaluation == 4) {
                    dishImageView.setImageResource(R.drawable.dessert_green);
                }
                break;
            case "варенье, джем, повидло, сироп" :
                if(evaluation == 2) {
                    dishImageView.setImageResource(R.drawable.jam_red);
                }
                if(evaluation == 3) {
                    dishImageView.setImageResource(R.drawable.jam_yellow);
                }
                if(evaluation == 4) {
                    dishImageView.setImageResource(R.drawable.jam_green);
                }
                break;
            case "мороженое" :
                if(evaluation == 2) {
                    dishImageView.setImageResource(R.drawable.ice_cream_red);
                }
                if(evaluation == 3) {
                    dishImageView.setImageResource(R.drawable.ice_cream_yellow);
                }
                if(evaluation == 4) {
                    dishImageView.setImageResource(R.drawable.ice_cream_green);
                }
                break;
            case "фрукты" :
                if(evaluation == 2) {
                    dishImageView.setImageResource(R.drawable.fruit_red);
                }
                if(evaluation == 3) {
                    dishImageView.setImageResource(R.drawable.fruit_yellow);
                }
                if(evaluation == 4) {
                    dishImageView.setImageResource(R.drawable.fruit_green);
                }
                break;
            case "орехи" :
                if(evaluation == 2) {
                    dishImageView.setImageResource(R.drawable.nuts_red);
                }
                if(evaluation == 3) {
                    dishImageView.setImageResource(R.drawable.nuts_yellow);
                }
                if(evaluation == 4) {
                    dishImageView.setImageResource(R.drawable.nuts_green);
                }
                break;
            case "добавки" :
                if(evaluation == 2) {
                    dishImageView.setImageResource(R.drawable.supplements_red);
                }
                if(evaluation == 3) {
                    dishImageView.setImageResource(R.drawable.supplements_yellow);
                }
                if(evaluation == 4) {
                    dishImageView.setImageResource(R.drawable.supplements_green);
                }
                break;
            default:
                if(evaluation == 2) {
                    dishImageView.setImageResource(R.drawable.star_red);
                }
                if(evaluation == 3) {
                    dishImageView.setImageResource(R.drawable.star_yellow);
                }
                if(evaluation == 4) {
                    dishImageView.setImageResource(R.drawable.star_green);
                }
                break;
        }
    }
}

package com.sauno.androiddeveloper.chewstudiodemo.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sauno.androiddeveloper.chewstudiodemo.AboutDishStatisticActivity;
import com.sauno.androiddeveloper.chewstudiodemo.BasketActivity;
import com.sauno.androiddeveloper.chewstudiodemo.ChoiceOfDishesActivity;
import com.sauno.androiddeveloper.chewstudiodemo.R;
import com.sauno.androiddeveloper.chewstudiodemo.SetQuantityOfDishesActivity;
import com.sauno.androiddeveloper.chewstudiodemo.utility.OnSwipeTouchListener;

import java.math.BigDecimal;

//вспомогательный класс для отображения списка на экране BasketActivity
public class BasketAdapter extends RecyclerView.Adapter<BasketAdapter.ViewHolder> {

    //объект той активити, в которой отображается список
    private BasketActivity basketActivity;

    // массив названий блюд заказа
    private String[] dishOrderNames;

    // массив колличеств блюд заказа
    private BigDecimal[] dishOrderQuantity;

    // массивы процентов каждого блюда от общей суммы заказов
    private int[] percentageOfCaloriesArray;
    private int[] percentageOfProteinsArray;
    private int[] percentageOfFatsArray;
    private int[] percentageOfCarbsArray;
    private int[] percentageOfXEArray;

    // конструктор
    public BasketAdapter(Context context, String[] dishOrderNames, BigDecimal[] dishOrderQuantity,
                         int[] percentageOfCaloriesArray,
                         int[] percentageOfProteinsArray,
                         int[] percentageOfFatsArray,
                         int[] percentageOfCarbsArray,
                         int[] percentageOfXEArray) {
        basketActivity = (BasketActivity) context;

        this.dishOrderNames = dishOrderNames;
        this.dishOrderQuantity = dishOrderQuantity;
        this.percentageOfCaloriesArray = percentageOfCaloriesArray;
        this.percentageOfProteinsArray = percentageOfProteinsArray;
        this.percentageOfFatsArray = percentageOfFatsArray;
        this.percentageOfCarbsArray = percentageOfCarbsArray;
        this.percentageOfXEArray = percentageOfXEArray;
    }

    // Создание новых View. Вызывает LayoutManager
    @Override
    public BasketAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                       int viewType) {
        // create a new view
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_basket, parent, false);

        return new ViewHolder(view);
    }

    // удерживает объект View
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public View mView;

        // элементы содержащиеся в item_basket
        public TextView dishCountOrderTextView;
        public TextView dishNameOrderTextView;
        public TextView percentageOfCaloriesTextView;
        public TextView percentageOfProteinsTextView;
        public TextView percentageOfFatsTextView;
        public TextView percentageOfCarbsTextView;
        public TextView percentageOfXETextView;

        //Необходимые view для реализации класса RecyclerItemTouchHelperForBasket
        public LinearLayout viewForeground;
        public RelativeLayout viewBackground;

        public ViewHolder(View view) {
            super(view);
            mView = view;

            dishCountOrderTextView = view.findViewById(R.id.dishCountOrderTextView);
            dishNameOrderTextView = view.findViewById(R.id.dishNameOrderTextView);
            percentageOfCaloriesTextView = view.findViewById(R.id.percentageOfCaloriesTextView);
            percentageOfProteinsTextView = view.findViewById(R.id.percentageOfProteinsTextView);
            percentageOfFatsTextView = view.findViewById(R.id.percentageOfFatsTextView);
            percentageOfCarbsTextView = view.findViewById(R.id.percentageOfCarbsTextView);
            percentageOfXETextView = view.findViewById(R.id.percentageOfXETextView);

            //необходимые вьюхи для реализации свайпа
            viewForeground = view.findViewById(R.id.viewForeground);
            viewBackground = view.findViewById(R.id.viewBackground);
        }
    }

    // Меняет содержимое view вызывается LayoutManager
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        // получаем даные типа float
        float quantityFloat = dishOrderQuantity[position].floatValue();

        // сравниваем варианты колличества блюда и устанавливаем соответственный текст в dishCountOrderTextView
        if(Float.compare(quantityFloat, 0.33f) == 0) {
            holder.dishCountOrderTextView.setText("1/3");
            holder.dishCountOrderTextView.setTextSize(16);
        } else if(Float.compare(quantityFloat, 0.5f) == 0) {
            holder.dishCountOrderTextView.setText("1/2");
            holder.dishCountOrderTextView.setTextSize(16);
        } else if(Float.compare(quantityFloat, 0.66f) == 0) {
            holder.dishCountOrderTextView.setText("2/3");
            holder.dishCountOrderTextView.setTextSize(16);
        } else {
            holder.dishCountOrderTextView.setText("" + dishOrderQuantity[position].intValue());
        }

        // устанавливаем отображаемые данные
        holder.dishNameOrderTextView.setText("" + dishOrderNames[position]);

        // устанавливаем отображаемые данные
        holder.percentageOfCaloriesTextView.setText("К: " + percentageOfCaloriesArray[position] + "%");
        holder.percentageOfProteinsTextView.setText("Б: " + percentageOfProteinsArray[position] + "%");
        holder.percentageOfFatsTextView.setText("Ж: " + percentageOfFatsArray[position] + "%");
        holder.percentageOfCarbsTextView.setText("У: " + percentageOfCarbsArray[position] + "%");
        holder.percentageOfXETextView.setText("ХЕ: " + percentageOfXEArray[position] + "%");

        // обработчик события долгого нажатия на dishNameOrderTextView
        holder.dishNameOrderTextView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                // запускаем окно AboutDishActivity
                Intent intent = new Intent(view.getContext(), AboutDishStatisticActivity.class);

                // передаём idDish данного блюда
                intent.putExtra("idDish", ChoiceOfDishesActivity.dishOrderList.get(position).getIdDish());

                // передаём dishName данного блюда
                intent.putExtra("dishName", dishOrderNames[position]);
                basketActivity.startActivity(intent);

                return false;
            }
        });

        //устанавливаем количество
        holder.dishCountOrderTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // запускаем окно SetQuantityOfDishesActivity с ожиданием результата
                Intent intent = new Intent(basketActivity, SetQuantityOfDishesActivity.class);

                // передаём dishName данного блюда
                intent.putExtra("dishName", dishOrderNames[position]);
                basketActivity.startActivityForResult(intent, BasketActivity.PICK_CONTACT_REQUEST);
            }
        });

        // реализовываем swipe
        holder.mView.setOnTouchListener(new OnSwipeTouchListener(basketActivity) {
            @Override
            public void onSwipeRight() {
                super.onSwipeRight();

                Log.i("MyLogBasketAdapter", "Right");
                //Toast.makeText(basketActivity, "right", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Возвращает размер данных списка, вызывается LayoutManager
    @Override
    public int getItemCount() {
        return dishOrderNames.length;
    }
}


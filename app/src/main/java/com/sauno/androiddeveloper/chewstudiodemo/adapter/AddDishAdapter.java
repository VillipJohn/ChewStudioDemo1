package com.sauno.androiddeveloper.chewstudiodemo.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sauno.androiddeveloper.chewstudiodemo.AddDishActivity;
import com.sauno.androiddeveloper.chewstudiodemo.R;
import com.sauno.androiddeveloper.chewstudiodemo.utility.OnSwipeTouchListener;

//вспомогательный класс для отображения списка на экране AddDishActivity
public class AddDishAdapter extends RecyclerView.Adapter<AddDishAdapter.ViewHolder> {
    //объект той активити, в которой отображается список
    private AddDishActivity addDishActivity;

    //конструктор
    public AddDishAdapter(Context context) {
        addDishActivity = (AddDishActivity) context;
    }

    // Создание новых View. Вызывает LayoutManager
    @Override
    public AddDishAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                            int viewType) {
        // create a new view
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_add_dish, parent, false);

        return new ViewHolder(view);
    }

    // удерживает объект View
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public View mView;

        // элементы содержащиеся в item_add_dish
        public TextView dishNameTextView;
        public TextView gramsTextView;
        public EditText gramsEditText;

        //Необходимые view для реализации класса RecyclerItemTouchHelperForAddDish
        public LinearLayout viewForeground;
        public RelativeLayout viewBackground;

        public ViewHolder(View view) {
            super(view);

            mView = view;

            dishNameTextView = view.findViewById(R.id.dishNameTextView);
            gramsTextView = view.findViewById(R.id.gramsTextView);
            gramsEditText = view.findViewById(R.id.gramsEditText);

            viewForeground = view.findViewById(R.id.viewForeground);
            viewBackground = view.findViewById(R.id.viewBackground);
        }
    }

    // Меняет содержимое view вызывается LayoutManager
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        // устанавливаем отображаемые данные
        holder.dishNameTextView.setText(AddDishActivity.chosenIngredientsList.get(position).getName());
        holder.gramsTextView.setText(AddDishActivity.chosenIngredientsList.get(position).getGrams() + "г");
        holder.gramsEditText.setHint("" + AddDishActivity.chosenIngredientsList.get(position).getGrams());

        //обработка нажатия на gramsTextView
        holder.gramsTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.gramsTextView.setVisibility(View.GONE);
                holder.gramsEditText.setVisibility(View.VISIBLE);
            }
        });

        //обработка событий от gramsEditText
        holder.gramsEditText.setOnKeyListener(new View.OnKeyListener()
                                  {
                                      public boolean onKey(View v, int keyCode, KeyEvent event)
                                      {
                                          if(event.getAction() == KeyEvent.ACTION_DOWN &&
                                                  (keyCode == KeyEvent.KEYCODE_ENTER))
                                          {
                                              if (!holder.gramsEditText.getText().toString().equals("")) {
                                                  String newGrams = holder.gramsEditText.getText().toString();
                                                  //holder.gramsTextView.setText(newGrams + "г");
                                                  AddDishActivity.chosenIngredientsList.get(position).setGrams(Integer.parseInt(newGrams));

                                                  //вызываем этот метод для обновления данных списка
                                                  addDishActivity.restartAdapter();

                                              }
                                              else {
                                                  Toast.makeText(addDishActivity, "Введите количество грамм", Toast.LENGTH_SHORT);
                                              }
                                              return true;
                                          }
                                          return false;
                                      }
                                  }
        );


        // реализовываем swipe
        holder.mView.setOnTouchListener(new OnSwipeTouchListener(addDishActivity) {
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
        return AddDishActivity.chosenIngredientsList.size();
    }
}



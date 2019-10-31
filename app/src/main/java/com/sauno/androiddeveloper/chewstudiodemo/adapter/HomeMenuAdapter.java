package com.sauno.androiddeveloper.chewstudiodemo.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sauno.androiddeveloper.chewstudiodemo.DishInformationActivity;
import com.sauno.androiddeveloper.chewstudiodemo.HomeMenuActivity;
import com.sauno.androiddeveloper.chewstudiodemo.R;
import com.sauno.androiddeveloper.chewstudiodemo.model.Dish;
import com.sauno.androiddeveloper.chewstudiodemo.utility.OnSwipeTouchListener;

import java.util.List;

//вспомогательный класс для отображения списка на экране HomeMenuActivity
public class HomeMenuAdapter extends RecyclerView.Adapter<HomeMenuAdapter.ViewHolder> {

    //объект той активити, в которой отображается список
    private HomeMenuActivity homeMenuActivity;

    // список блюд
    private List<Dish> dishesList;

    //необходимый массив для корректного отображения галочек
    private boolean[] checked;

    // конструктор
    public HomeMenuAdapter(Context context, List<Dish> dishesList) {
        homeMenuActivity = (HomeMenuActivity) context;

        this.dishesList = dishesList;

        checked = new boolean[dishesList.size()];
    }

    // Создание новых View. Вызывает LayoutManager
    @Override
    public HomeMenuAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        // create a new view
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_home_menu, parent, false);

        return new ViewHolder(view);
    }

    // удерживает объект View
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public View mView;

        // элементы содержащиеся в item_home_menu
        public CheckBox mDishCheckBox;
        public TextView mDishNameTextView;
        public ImageView mDishImageView;

        //Необходимые view для реализации класса RecyclerItemTouchHelperForBasket
        public LinearLayout viewForeground;
        public RelativeLayout viewBackground;

        public ViewHolder(View view) {
            super(view);
            mView = view;

            mDishCheckBox = view.findViewById(R.id.dishCheckBox);
            mDishNameTextView = view.findViewById(R.id.dishNameTextView);
            mDishImageView = view.findViewById(R.id.dishImageView);

            //необходимые вьюхи для реализации свайпа
            viewForeground = view.findViewById(R.id.viewForeground);
            viewBackground = view.findViewById(R.id.viewBackground);
        }
    }

    // Меняет содержимое view вызывается LayoutManager
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        // устанавливаем отображаемые данные
        holder.mDishNameTextView.setText(dishesList.get(position).getDishName());

        //holder.mDishImageView.setImageBitmap(setGrade());

        holder.mDishImageView.setImageDrawable(homeMenuActivity.getResources().getDrawable(R.drawable.shape_circle_green));

        //устанавливаем корректно галочки
        if(dishesList.get(position).getQuantity() > 0) {
            holder.mDishCheckBox.setChecked(true);
            checked[position] = true;
        } else {
            checked[position] = false;
            holder.mDishCheckBox.setChecked(false);
        }

        holder.mDishNameTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int idDish = dishesList.get(position).getIdDish();
                String dishName = dishesList.get(position).getDishName();

                Intent intent = new Intent(homeMenuActivity, DishInformationActivity.class);
                intent.putExtra("idDish", idDish);
                intent.putExtra("dishName", dishName);
                homeMenuActivity.startActivity(intent);
            }
        });

        // реализовываем swipe
        holder.mView.setOnTouchListener(new OnSwipeTouchListener(homeMenuActivity) {
            @Override
            public void onSwipeRight() {
                super.onSwipeRight();
            }
        });
    }

    private Bitmap setGrade() {
        // Initialize a new Bitmap object
        Bitmap bitmap = Bitmap.createBitmap(
                50, // Width
                50, // Height
                Bitmap.Config.ARGB_8888 // Config
        );

        // Initialize a new Canvas instance
        Canvas canvas = new Canvas(bitmap);



       /* Paint p = new Paint();
        RectF rectF = new RectF(50, 20, 100, 80);
        p.setColor(Color.BLACK);
        canvas.drawArc (rectF, 90, 45, true, p);*/

       /* // Draw a solid color to the canvas background
        canvas.drawColor(Color.LTGRAY);

        // Initialize a new Paint instance to draw the Circle
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.RED);
        paint.setAntiAlias(true);*/


        int height = canvas.getHeight() / 2;
        int width = canvas.getWidth() / 2;


        final RectF oval = new RectF();
        oval.set(width - 25, height - 25, width + 25, height + 25);

        Paint paintGreen = new Paint();
        paintGreen.setColor(Color.GREEN);

        Paint paintRed = new Paint();
        paintRed.setColor(Color.RED);

        Paint paint = new Paint();
        paint.setColor(Color.GREEN);

        int radius = Math.min(canvas.getWidth(),canvas.getHeight()/2);



       /* canvas.drawArc(oval, 0F, 90F, true, paintGreen);
        canvas.drawArc(oval, 90F, 270F, true, paintRed);*/

        canvas.drawCircle(
                canvas.getWidth() / 2, // cx
                canvas.getHeight() / 2, // cy
                radius, // Radius
                paint // Paint
        );

        /*canvas.drawArc(oval, 90F, 270F, true, paintRed);*/




        /*canvas.drawArc(oval, 0F, 90F, true, paintGreen);

        canvas.drawArc(oval, 90F, 270F, true, paintRed);*/

       /* canvas.drawArc(oval, 180F, 90F, true, yellowPaint)

        canvas.drawArc(oval, 270F, 90F, true, hotPinkPaint)*/

       /* // Calculate the available radius of canvas
        int radius = Math.min(canvas.getWidth(),canvas.getHeight()/2);

        // Set a pixels value to padding around the circle
        int padding = 0;

                *//*
                    public void drawCircle (float cx, float cy, float radius, Paint paint)
                        Draw the specified circle using the specified paint. If radius is <= 0, then
                        nothing will be drawn. The circle will be filled or framed based on the
                        Style in the paint.

                    Parameters
                        cx : The x-coordinate of the center of the circle to be drawn
                        cy : The y-coordinate of the center of the circle to be drawn
                        radius : The radius of the cirle to be drawn
                        paint : The paint used to draw the circle
                *//*
        // Finally, draw the circle on the canvas
        canvas.drawCircle(
                canvas.getWidth() / 2, // cx
                canvas.getHeight() / 2, // cy
                radius - padding, // Radius
                paint // Paint
        );
*/
        return bitmap;
    }

    // Возвращает размер данных списка, вызывается LayoutManager
    @Override
    public int getItemCount() {
        return dishesList.size();
    }
}



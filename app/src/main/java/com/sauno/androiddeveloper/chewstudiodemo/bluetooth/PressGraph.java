package com.sauno.androiddeveloper.chewstudiodemo.bluetooth;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.widget.ImageView;

//**************************************************************************************************
//
//                     Класс PressGraph для рисования шкалы прижатия сенсора
//
//**************************************************************************************************
public class PressGraph
{
    final int SMALPHA = 60;                                                                         // Коэффициент сглаживания фильтра отслеживания постоянки (чем больше значение, тем медленнее реакция)
    final int CENTRALBOUNDARY = 450;                                                                // Граница корректного прижатия датчика (+-)
    final int SENCEOFFBOUNDARY = 900;                                                               // Граница, за которой прибор считается снятым с человека
    final int MAXVISIBLE = 1000;                                                                    // Ширина отображаемого поля (+-)

    float filterValue = 0;                                                                          // Текущее значение выхода фильтра постоянки

    Bitmap bitmap = null;                                                                           // Битмап для рисования
    Canvas canvas;                                                                                  // Холст для рисования

    Paint fillOuterPaint;                                                                           // Стиль заливки зоны плохого прижатия
    Paint fillOffPaint;                                                                             // Стиль заливки зоны снятия датчика
    Paint sliderPaint;                                                                              // Стиль рисования ползунка
    Paint textPaint;                                                                                // Стиль для текста


    //**********************************************************************************************
    //
    //                                      Конструктор
    //
    //**********************************************************************************************
    public PressGraph()
    {

        fillOuterPaint = new Paint();                                                               // Определяем тип заливки зоны плохого прижатия
        fillOuterPaint.setARGB(255, 240, 88, 88);                                                   // Цвет: ?

        fillOffPaint = new Paint();                                                                 // Определяем тип заливки зоны снятия датчика
        fillOffPaint.setARGB(255, 128, 0, 255);                                                     // Цвет: ?

        sliderPaint = new Paint();                                                                  // Определяем тип рисования ползунка
        sliderPaint.setAntiAlias(true);                                                             // Сглаживание включено
        sliderPaint.setARGB(255, 240, 240, 240);                                                    // Цвет: ?

        textPaint = new Paint();                                                                    // Определяем тип написания текста
        textPaint.setARGB(255, 255, 255, 255);                                                      // Цвет: ?
        textPaint.setAntiAlias(true);                                                               // Сглаживание включено

    }

    //**********************************************************************************************
    //
    //                                Публичный метод Refresh()
    //                             Обновить шкалу прижатия датчика
    //
    //**********************************************************************************************
    public void Refresh(ImageView imageView, int[] Data)
    {
        int i, val;                                                                                 // Переменные общего назначения

        int width, height;                                                                          // Размер холста в пикселях
        int x;                                                                                      // Переменная общего назначения
        int halfWidth, halfHeight;                                                                  // x и y - поцизии центра холста

        width = imageView.getWidth();                                                               // Получить размер ImageView
        height = imageView.getHeight();                                                             //

        // Первичная инициализация битмапа и холста

        if ((width == 0) || (height == 0))                                                          // Если хотя бы один из размеров равен 0,
            return;                                                                                 // то ImageView еще не проинициализирован, выход

        if (bitmap == null)                                                                         // Если bitmap еще не инициализирован, то
        {
            bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);                   // Задать bitmap
            canvas = new Canvas(bitmap);                                                            // Задать холст
        }

        halfWidth = width / 2;                                                                      // Позиция x центра холста
        halfHeight = height / 2;                                                                    // Позиция y центра холста

        for (i=0; i<Data.length; i++)                                                               // Цикл на длину добавляемого блока данных
        {
            filterValue = (Data[i] + (filterValue * SMALPHA)) / ( 1 + SMALPHA);                     // Получить через фильтр новое значение постоянки
        }

        float sliderWidth = halfHeight * 0.8f;                                                      // Вычислить размер слайдера (половина ширины/высоты)

        // Само рисование на холсте

        canvas.drawColor(Color.rgb(0, 200, 100));                                                   // Залить холст сплошным цветом. Цвет: ?

        x = (halfWidth * CENTRALBOUNDARY) / MAXVISIBLE;                                             // Получить координату начала поля плохого прижатия

        canvas.drawRect(halfWidth + x, 0, width - 1, height, fillOuterPaint);                       // Рисуем правое поле плохого прижатия
        canvas.drawRect(0, 0, halfWidth - x, height, fillOuterPaint);                               // Рисуем левое поле плохого прижатия

        x = (halfWidth * SENCEOFFBOUNDARY) / MAXVISIBLE;                                            //
        canvas.drawRect(halfWidth + x, 0, width - 1, height, fillOffPaint);                         // Рисуем поле снятия датчика

        x = halfWidth + (halfWidth * (int)filterValue) / MAXVISIBLE;                                // Вычислить x координату ползунка

        if (x > (width - sliderWidth))                                                              // Ограничиваем выход прямоугольника за пределы графика
            x = (int)(width - sliderWidth);                                                         //
        if (x < sliderWidth)                                                                        //
            x = (int)sliderWidth;                                                                   //

        canvas.drawRoundRect(new RectF(x - sliderWidth, halfHeight - sliderWidth,                   // Рисовать слайдер, как прямоугольник с закругленными концами
                        x + sliderWidth, halfHeight + sliderWidth),                  //
                50, 50, sliderPaint);                                                  //

        textPaint.setTextSize(height * 0.9f);                                                       // Установить размер текста как 0.9 от высоты битмапа

        String textWeak = "Слабое";                                                                 // Вычислить ширину надписи 'Слабое'
        int textWidth =  (int)textPaint.measureText(textWeak);                                      //

        canvas.drawText("Сильное", height * 0.3f, height * 0.8f, textPaint);                        // Печатаем слева надпись 'Сильное'

        x = halfWidth + (halfWidth * SENCEOFFBOUNDARY) / MAXVISIBLE;                                // x - позиция поля снятия датчика,
        if (x > width)                                                                              // но не левее края экрана
            x = width;                                                                              //
        canvas.drawText(textWeak, x - height * 0.3f - textWidth, height * 0.8f, textPaint);         // Печатаем справа, но левее поля снятия датчика, надпись 'Слабое'


        imageView.setImageBitmap(bitmap);                                                           // Вывести bitmap в ImageView

    }

}


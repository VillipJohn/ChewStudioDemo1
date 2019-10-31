package com.sauno.androiddeveloper.chewstudiodemo.bluetooth;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;
import android.widget.ImageView;

import com.sauno.androiddeveloper.chewstudiodemo.ChewingProcessActivity;

import java.util.Timer;
import java.util.TimerTask;

//**************************************************************************************************
//
//                      Класс ChewGraph для рисования графика датчика жевания
//
//**************************************************************************************************
public class ChewGraph
{
    final int BUFSIZE = 2048;                                                                       // Размер буфера отсчетов. Должен быть кратен степени двойки. 2048 взято с большим запасом
    final int BUFMASK = BUFSIZE - 1;                                                                // Маска закольцовки буфера отсчетов
    final int VIRTZEROSIZE = 16;                                                                    // Число отсчетов для вычисления виртуальной средней линии
    final int SMALPHA = 200;                                                                        // Коэффициент сглаживания фильтра отслеживания постоянки (чем больше значение, тем медленнее реакция)
    final float MAXYMUL = 1.0f;                                                                     // Максимальное увеличение по Y
    final float MINYMUL = 0.1f;                                                                     // Минимальное увеличение по Y (для управление увеличением извне класса)
    final float MULSTEP = 1.05f;                                                                    // Фактор автоувеличения множителя по Y

    final int CHEWBUFSIZE = 128;                                                                    // Размер буфера номеров жеваний. Должен быть кратен степени двойки.
    final int CHEWBUFMASK = CHEWBUFSIZE - 1;                                                        // Маска закольцовки буфера номеров жеваний

    int[] chewBufDisp = new int[CHEWBUFSIZE];                                                       // Кольцевой буфер смещений жеваний
    int[] chewBufNum = new int[CHEWBUFSIZE];                                                        // Кольцеваой буфер номеров жеваний
    int chewBufPtr = 0;                                                                             // Указатель на позицию в буфере жеваний

    int[] buffer = new int[BUFSIZE];                                                                // Кольцевой буфер отсчетов
    int bufPtr = 0;                                                                                 // Указатель на позицию в буфере отсчетов

    float filterValue = 0;                                                                          // Текущее значение выхода фильтра постоянки

    int centralBoundary = 450;                                                                      // Граница (+-) корректного зоны прижатия датчика

    float virtualZeroVal = 0;                                                                       // Положжение виртуальной средней линии
    float graphPixelSize = 3.0f;                                                                    // Размер пикселя для рисования графика
    float xStep = 3;                                                                                // Шаг графика по X
    float mulYFactor = 0.2f;                                                                        // Множитель по вертикали для рисования графика
    boolean autoMag = true;                                                                         // Разрешить автоувеличение
    boolean autoCenter = true;                                                                      // Разрешить автоцентровку

    Bitmap bitmap = null;                                                                           // Битмап для рисования
    Canvas canvas;                                                                                  // Холст для рисования

    Paint graphPaint;                                                                               // Стиль рисования графика
    Paint zeroPaint;                                                                                // Стиль рисования средней линии
    Paint virtualZeroPaint;                                                                         // Стиль рисования виртуальной средней линии
    Paint fillTopPaint;                                                                             // Стиль заливки верхнего прямоугольника
    Paint fillBottomPaint;                                                                          // Стиль заливки нижнего прямоугольника
    Paint textPaint;                                                                                // Стиль для текста
    DashPathEffect zeroDashPath;                                                                    // Эффект рисования средней линии

    Paint strokeX;
    Paint textF;
    Paint textT;
    Paint textNumbers;
    Paint textNumbersTime;
    Paint textNumbersTimeClear;

    public static Timer timer;
    boolean isTimerStarted = false;
    int n = 0;

    //**********************************************************************************************
    //
    //                                      Конструктор
    //
    //**********************************************************************************************
    public ChewGraph()
    {
        zeroDashPath = new DashPathEffect(new float[] { 10.0f, 5.0f }, 0);                    // Тип пунктира для средних линий 10/5, фаза 0

        zeroPaint = new Paint();                                                                    // Определяем тип рисования средней линии
        zeroPaint.setAntiAlias(true);                                                               // Сглаживание включено
        zeroPaint.setColor(Color.RED);                                                              // Цвет: красный
        zeroPaint.setPathEffect(zeroDashPath);                                                      // Описание пунктира линии
        zeroPaint.setStyle(Paint.Style.STROKE);                                                     // [?] Стиль рисования
        zeroPaint.setStrokeWidth(graphPixelSize);                                                   // Ширина линии

        virtualZeroPaint = new Paint();                                                             // Определяем тип рисования виртуальной средней линии
        virtualZeroPaint.setAntiAlias(true);                                                        // Сглаживание включено
        virtualZeroPaint.setColor(Color.rgb(0, 160, 0));                                            // Цвет: зеленый
        virtualZeroPaint.setPathEffect(zeroDashPath);                                               // Описание пунктира линии
        virtualZeroPaint.setStyle(Paint.Style.STROKE);                                              // [?] Стиль рисования
        virtualZeroPaint.setStrokeWidth(graphPixelSize);                                            // Ширина линии

        graphPaint = new Paint();                                                                   // Определяем тип рисования графика
        graphPaint.setAntiAlias(true);                                                              // Сглаживание включено
        graphPaint.setARGB(160, 0, 0, 255);                                                         // Цвет: синий
        graphPaint.setStrokeWidth(3.0f);                                                            // Ширина линии

        fillTopPaint = new Paint();                                                                 // Определяем тип заливки верхнего прямугольника
        fillTopPaint.setARGB(255, 255, 230, 128);                                                   // Цвет: ?

        fillBottomPaint = new Paint();                                                              // Определяем тип заливки нижнего прямугольника
        fillBottomPaint.setARGB(255, 128, 230, 255);                                                // Цвет: ?

        textPaint = new Paint();                                                                    // Определяем тип написания текста
        textPaint.setAntiAlias(true);                                                               // Сглаживание включено
        textPaint.setARGB(255, 255, 0, 255);                                                        // Цвет: ?
        textPaint.setTextSize(20);                                                                  // Размер текста


        strokeX = new Paint();
        // настройка кисти
        // красный цвет
        strokeX.setColor(Color.BLACK);
        // толщина линии = 10
        strokeX.setStrokeWidth(4);

        textF = new Paint();
        textF.setColor(Color.BLUE);
        textF.setTextSize(40);
        textF.setTextAlign(Paint.Align.LEFT);

        textT = new Paint();
        textT.setColor(Color.BLUE);
        textT.setTextSize(40);
        textT.setTextAlign(Paint.Align.RIGHT);

        textNumbers = new Paint();
        textNumbers.setColor(Color.BLUE);
        textNumbers.setTextSize(30);

        textNumbersTime = new Paint();
        textNumbersTime.setColor(Color.BLUE);
        textNumbersTime.setTextSize(30);
        textNumbersTime.setTextAlign(Paint.Align.CENTER);

        textNumbersTimeClear = new Paint();
        textNumbersTimeClear.setARGB(255, 255, 230, 128);
        textNumbersTimeClear.setTextSize(30);
        textNumbersTimeClear.setTextAlign(Paint.Align.CENTER);
    }

    //**********************************************************************************************
    //
    //                                Публичный метод Refresh()
    //                                 Обновить график жеваний
    //
    //**********************************************************************************************
    public void Refresh(ImageView imageView, int[] Data, int ChewDisp, int ChewCtr)
    {
        int i, val;                                                                                 // Переменные общего назначения

        int width, height;                                                                          // Размер холста в пикселях
        int halfHeight;                                                                             // Y-поцизия центра холста

        float fY, fX, fLastY = 0, fLastX = 0;
        // Обновляем буфер жеваний

        for (i=0; i<CHEWBUFSIZE; i++)                                                               // Увеличиваем все смещения в буфере номеров жеваний
        {                                                                                           // на 8 отсчетов
            if (chewBufDisp[i] < 4096)                                                              // Если смещение >= 4096, то не увеличиваем и обнуляем
                chewBufDisp[i] = chewBufDisp[i] + 8;                                                // число жеваний
            else                                                                                    //
                chewBufNum[i] = 0;                                                                  //
        }

        if (ChewCtr > 0)                                                                            // Если номер жевания 1..127, то
        {                                                                                           //
            chewBufDisp[chewBufPtr] = ChewDisp;                                                     // Добавляем данные в буфер
            chewBufNum[chewBufPtr] = ChewCtr;                                                       //
            chewBufPtr = (chewBufPtr + 1) & CHEWBUFMASK;                                            // Прирастить указатель
        }
        else if (ChewCtr < 0)                                                                       // Иначе, если номер жевания -1 (отмена жевания), то
        {                                                                                           //
            for (i=0; i<CHEWBUFSIZE; i++)                                                           // Ищем жевание с заданным смещением,
            {                                                                                       // и обнуляем число жеваний по заданному смещению
                if (chewBufDisp[i] == ChewDisp)                                                     //
                    chewBufNum[i] = 0;                                                              //
            }                                                                                       //
        }


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




        halfHeight = height / 2;                                                                    // Вычислить центр экрана по Y

        for (i=0; i<Data.length; i++)                                                               // Цикл на длину добавляемого блока данных
        {
            val = Data[i];                                                                          // Берем данные из входного массива
            buffer[bufPtr] = val;                                                                   // Добавляем данные в буфер
            bufPtr = (bufPtr + 1) & BUFMASK;                                                        // Прирастить указатель

            filterValue = (val + (filterValue * SMALPHA)) / ( 1 + SMALPHA);                         // Получить через фильтр новое значение постоянки
        }

        if (!autoCenter)                                                                            // Если не режим автоцентровки, то
            filterValue = 0;                                                                        // обнулить значение фильтра

        if (autoMag)                                                                                // Если режим автоувеличения, то
            mulYFactor = mulYFactor * MULSTEP;                                                      // потихоньку увеличивать вертикальный масштаб

        // Само рисование на холсте

        canvas.drawColor(Color.WHITE);                                                              // Залить холст сплошным цветом. Цвет: белый

        fY = halfHeight - ((centralBoundary - filterValue) * mulYFactor);                           // Вычислить верхнюю линию границы центральной зоны

        if (fY > 0)                                                                                 // Если верхняя граница присутствует на экране, то
            canvas.drawRect(0, 0, width - 1, fY, fillTopPaint);                       // рисуем верхний прямоугольник границы

        fY = halfHeight + ((centralBoundary + filterValue) * mulYFactor);                           // Вычислить нижнюю линию границы центральной зоны

        if (fY < height)                                                                            // Если верхняя граница присутствует на экране, то
            canvas.drawRect(0, fY, width - 1, height - 1, fillBottomPaint);        // рисуем нижний прямоугольник границы

        fY = halfHeight - ((virtualZeroVal - filterValue) * mulYFactor);                            // Вычислить положение горизонтальной виртуальной нулевой линии
        canvas.drawLine(0, fY, width - 1, fY, virtualZeroPaint);                        // Рисовать горизонтальную виртуальную нулевую линию

        fY = halfHeight + filterValue * mulYFactor;                                                 // Вычислить положение горизонтальной нулевой линии
        canvas.drawLine(0, fY, width - 1, fY, zeroPaint);                               // Рисовать горизонтальную нулевую линию

        // Вычисляем максимумы и минимумы видимого графика
        int lBufPtr = bufPtr - 1;
        float maxY = 0;

        for (fX = (width - 1); fX >= 0; fX = fX - xStep)                                            // Цикл на width/xStep точек графика
        {
            fY = ((buffer[lBufPtr & BUFMASK] - filterValue) * mulYFactor);                          // Получить Y координату точки для отсчета без смещения нуля
            lBufPtr--;                                                                              //

            if (fY > maxY)                                                                          // Обновить максимальный размах по Y (+-)
                maxY = fY;                                                                          //

            if (-fY > maxY)                                                                         //
                maxY = -fY;                                                                         //
        }

        if (autoMag)                                                                                // Если режим автоувеличения, то
            if (maxY > (halfHeight - graphPixelSize))                                               // Если график не умещается по Y, то
                mulYFactor = mulYFactor * ((halfHeight - graphPixelSize) / maxY);                   // подогнать вертикальный масштаб

        if (mulYFactor > MAXYMUL)                                                                   // Ограничить максимальное увеличение
            mulYFactor = MAXYMUL;                                                                   //

//            canvas.drawText("Mag: " + String.format("%.1f", mulYFactor).replace(',','.'),           // Печать текста "Mag: 0.0"
//                            8, 24, textPaint);

        // Рисуем сам график
        lBufPtr = bufPtr - 1;

        for (fX = (width - 1); fX >= 0; fX = fX - xStep)                                            // Цикл на width/xStep точек графика
        {
            fY = halfHeight - ((buffer[lBufPtr & BUFMASK] - filterValue) * mulYFactor);             // Получить Y координату точки для отсчета
            lBufPtr--;

            if (fX == (width - 1))                                                                  // Если первая точка в графике, то
            {                                                                                       //
                fLastX = fX;                                                                        // рисуем ее как точку, а не линию
                fLastY = fY;                                                                        //
            }                                                                                       //

            canvas.drawLine(fLastX, fLastY, fX, fY, graphPaint);                                    // Рисуем линию соединияющую 2 отсчета

            fLastX = fX;                                                                            // Сохранить текущее значение точки
            fLastY = fY;                                                                            //
        }
        // Рисуем подписи к графику
        int Numb, Disp;
        int MaxDisp = (int)(width / xStep) + 64;                                                    // Диапазон сканирования: ширина экрана + 64 пикселя
        for (i=1; i<=CHEWBUFSIZE; i++)                                                              // Цикл на не более, чем число значений в буфере жеваний
        {
            Numb = chewBufNum[(chewBufPtr - i) & CHEWBUFMASK];                                      // Numb - число жеваний
            Disp = chewBufDisp[(chewBufPtr - i) & CHEWBUFMASK];                                     // Смещение времени для жевания

//                statusUpdate("Numb = " + Numb + " , Disp = " + Disp);

            if (Numb <= 0)                                                                          // Если жевание не в диапазоне 1..127, то
                continue;                                                                           // пропускаем итерацию

            if (Disp > MaxDisp)                                                                     // Если вышли за границу сканирования,
                break;                                                                              // то прервать цикл


            fY = halfHeight - ((buffer[(bufPtr - 1 - Disp) & BUFMASK] - filterValue) * mulYFactor); // Получить Y координату низа вертикальной линии
            fX = width - 1 - (Disp * xStep);                                                        // Получить X координату вертикальной линии

            String text = Integer.toString(Numb);                                                   // text - текст с надписью числа жеваний

            Rect bounds = new Rect();                                                               // Вычислить ширину и высоту текста с запасом 5 точек по X, и 5 точек по Y
            textPaint.getTextBounds(text, 0, text.length(), bounds);                                //
            int textHeight =  bounds.height() + 5;                                                  //
            int textWidth =  (int)textPaint.measureText(text) + 5;                                  //

            float textX = fX - textWidth / 2;                                                       // textX - X координата начала текста
            float textY = textHeight + 1;                                                           // textY - Y координата начала текста

            canvas.drawText(text, textX + 2, textY - 3, textPaint);                                 // Печатаем текст с номером жевания

            canvas.drawLine(fX, fY, fX, textY, textPaint);                                          // Рисуем вертикальную линию от синусоиды до номера жевания

            canvas.drawLine(textX, 1, textX + textWidth, 1, textPaint);                             // Рисуем прямоугольник, обводящий число номера жевания
            canvas.drawLine(textX, 1, textX, textY, textPaint);                                     //
            canvas.drawLine(textX, textY, textX + textWidth, textY, textPaint);                     //
            canvas.drawLine(textX + textWidth, 1, textX + textWidth, textY, textPaint);             //

//                statusUpdate("h = " + textHeight + " , w = " + textWidth);



        }

        setStrokes(height, width);

        if(!isTimerStarted) {
            startTime();
            isTimerStarted = true;
        }


        imageView.setImageBitmap(bitmap);                                                           // Вывести bitmap в ImageView
    }

    private void setStrokes(int height, int width) {
        // рисуем линию от (100,100) до (500,50)
        canvas.drawLine(1,height,width,height,strokeX);
        canvas.drawLine(1,1,1,height,strokeX);

        canvas.drawText("t", width - 10, height - 15, textT);
        canvas.drawText("|f|", 5, 40, textF);

        canvas.drawLine(1,height/2,10,height/2,strokeX);
        canvas.drawText("0", 20, height/2 + 8, textNumbers);

        canvas.drawLine(1,height/2 + height/4,10,height/2 + height/4,strokeX);
        canvas.drawText("-1", 15, height/2 + height/4 + 8, textNumbers);

        canvas.drawLine(1,height/2 - height/4,10,height/2 - height/4,strokeX);
        canvas.drawText("1", 20, height/2 - height/4 + 8, textNumbers);

        canvas.drawLine(width/2,height,width/2,height-10,strokeX);
        canvas.drawLine(width/6,height,width/6,height-10,strokeX);
        canvas.drawLine(width/3,height,width/3,height-10,strokeX);
        canvas.drawLine(width/2 + width/6,height,width/2 + width/6,height-10,strokeX);
        canvas.drawLine(width/2 + width/3,height,width/2 + width/3,height-10,strokeX);


        canvas.drawText("" + n, width/2 + width/3, height - 20, textNumbersTime);

        if(n-1 > 0) {
            int m = n - 1;
            canvas.drawText("" + m, width/2 + width/6, height - 20, textNumbersTime);
        }

        if(n-2 > 0) {
            int m = n - 2;
            canvas.drawText("" + m, width/2, height - 20, textNumbersTime);
        }

        if(n-3 > 0) {
            int m = n - 3;
            canvas.drawText("" + m, width/3, height - 20, textNumbersTime);
        }

        if(n-4 > 0) {
            int m = n - 4;
            canvas.drawText("" + m, width/6, height - 20, textNumbersTime);
        }

    }

    private void startTime() {
        //long startTime = SystemClock.uptimeMillis();

        //long timeInMilliseconds = SystemClock.uptimeMillis() - startTime;



        if(!ChewingProcessActivity.isStartedTimerCounter) {
            ChewingProcessActivity.isStartedTimerCounter = true;

            timer = new Timer();
            TimerTask t = new TimerTask() {
                //int sec = 0;
                @Override
                public void run() {
                    Log.i("MyLogChewGraph", "сработало - " + n++);



                    /*canvas.drawText("" + n, width/2 + width/3, height - 30, textNumbers);

                    if(n-1 > 0) {
                        int n1 = n - 1;
                        canvas.drawText("" + n1, width/2 + width/6, height - 30, textNumbers);
                    }*/
//                canvas.drawText("0", 20, height/2 + 8, textNumbers);
//
//
//                canvas.drawLine(width/2,height,width/2,height-10,strokeX);
//                canvas.drawLine(width/6,height,width/6,height-10,strokeX);
//                canvas.drawLine(width/3,height,width/3,height-10,strokeX);
//                canvas.drawLine(width/2 + width/6,height,width/2 + width/6,height-10,strokeX);
//                canvas.drawLine(width/2 + width/3,height,width/2 + width/3,height-10,strokeX);
                }
            };
            timer.schedule(t,0, 1000);


        }
    }

    //**********************************************************************************************
    //
    //                            Публичный метод CalcVirtualZero()
    //                      Вычислить уровень виртуальной нулевой линии
    //
    //**********************************************************************************************
    public void CalcVirtualZero()
    {
        float sum = 0;

        for (int i=1; i<(VIRTZEROSIZE + 1); i++)                                                    // Цикл на VIRTZEROSIZE отсчетов
        {
            sum += buffer[(bufPtr - i) & BUFMASK];                                                  // Вычисляем среднеарифметическое
        }

        virtualZeroVal = sum / VIRTZEROSIZE;                                                        // Записать новое значение виртуальной средней линии
    }


    //**********************************************************************************************
    //
    //                  Функция вывода отладочных сообщений на консоль
    //
    //**********************************************************************************************
    private void debugMessage(String text) {
        Log.w("BLE01", text);
    }

}

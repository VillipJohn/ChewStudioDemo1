package com.sauno.androiddeveloper.chewstudiodemo;

import android.bluetooth.BluetoothAdapter;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.sauno.androiddeveloper.chewstudiodemo.bluetooth.BluetoothLeService;
import com.sauno.androiddeveloper.chewstudiodemo.bluetooth.ChewGraph;

import java.lang.ref.WeakReference;
import java.util.Timer;
import java.util.TimerTask;

public class ChewingProcessActivity extends AppCompatActivity {
    SearchDevicesFragment searchDevicesFragment;
    Menu mainMenu;

    SharedPreferences mSharedPreferences;

    ChewGraph mChewGraph;

    ImageView chewImageView;
    TextView textConnectStatus;
    TextView textViewRate;

    TextView maxChewTextView;
    TextView numberOfIngestionTextView;

    int maxChew;

    Boolean fEnableBTRequest = false;

    BluetoothLeService mLeService = null;                                                           // Указатель на экземпляр сервиса BluetoothLeService
    boolean            mLeServiceBound = false;                                                     // Флаг наличия привязки к сервису

    static final int REQUEST_ENABLE_BT = 1;

    Timer myTimer;

    int checkChewCounter = 1;

    public static boolean isStartedTimerCounter = false;

    //**********************************************************************************************
    //
    //                    Класс mLeConnection для получения ответа от сервиса при
    //                      соединении и разьединении привязки к сервису
    //
    // Замечание: При первом (а так же повторном коннекте), вызывается после OnResume(),
    //            что гарантирует наличие инициализированных в OnCreate() и OnResume()
    //            view-элементов. Хотя, для надежности желательно все инициализировать в OnCreate()
    //
    //**********************************************************************************************
    private ServiceConnection mLeConnection = new ServiceConnection()
    {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service)                    // При соединении с сервисом
        {
            //statusUpdate("LeService Connected");

            BluetoothLeService.leBinder binder = (BluetoothLeService.leBinder)service;              // Получаем указатель на наш LeBinder
            mLeService = binder.getService();                                                       // Вызвать метод getService() для получения указателя на класс BluetoothLeService
            mLeServiceBound = true;                                                                 // Установить флаг того, что есть привязка к сервису

            mLeService.weakChewingProcessActivity = new WeakReference<>(ChewingProcessActivity.this);       // Создать слабую ссылку для MainActivity

            if (mLeService.initialize())                                                            // Если BluetoothLE поддерживается устройством, то
            {
                if (!mLeService.mBluetoothAdapter.isEnabled())                                      // Если Bluetooth запрешен, то
                {
                    if (!fEnableBTRequest)                                                          // Если запрос на активацию Bluetooth еще не создан, то
                    {
                        fEnableBTRequest = true;                                                    // Установить флаг запроса
                        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE); // Создать намерение для открытия новой активити
                        startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);                  // Запустить окно системной активити с запросом разрешения Bluetooth
                    }
                }
            }
            else                                                                                    // Иначе, BluetoothLE не поддерживается устройством
            {
                //Toast.makeText(SecondActivity.this, "BLE not supported on this device",               // Печатаем Toast-сообщение 'BLE not supported on this device'
                        //Toast.LENGTH_SHORT).show();                                                 //
                finish();                                                                           // Закрываем приложение
                return;                                                                             // Выход
            }

            //-------------------------------------------------------------------------------------- Восстановление элементов интерфейса

            updateConnectStatus(mLeService.getConnectStatus());                                     // Восстановить сообщение о соединении
            //updateMaxChew(mLeService.maxChewCounter);                                               // Восстановить счетчик максимального числа жеваний

//            mLeService.testMethod();                                                                // Запустить тестовый метод сервиса

        }

        //------------------------------------------------------------------------------------------

        @Override
        public void onServiceDisconnected(ComponentName arg0)                                       // При разьединении с сервисом
        {
            //statusUpdate("LeService Disonnected");
            mLeServiceBound = false;                                                                // Снять флаг привязки к сервису
            mLeService = null;                                                                      // Освободить ссылку на сервис
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chewing_process);

        mSharedPreferences = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);

        chewImageView = findViewById(R.id.imageViewGraph);
        textConnectStatus = findViewById(R.id.TextViewAlert);
        textViewRate = findViewById(R.id.textViewRate);

        maxChewTextView = findViewById(R.id.maxChewTextView);
        numberOfIngestionTextView = findViewById(R.id.numberOfIngestionTextView);

        mChewGraph = new ChewGraph();                                                               // Создать экземпляр класса графика жевания

        setupActionBar();

        bindService(new Intent(this, BluetoothLeService.class),                        // Привязаться к сервису BluetoothLeService
                mLeConnection, Context.BIND_AUTO_CREATE);

        textConnectStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                SearchDevicesFragment searchFragment = new SearchDevicesFragment();
                searchDevicesFragment = searchFragment;
                fragmentTransaction.add(R.id.chewingProcessConstraintLayout, searchDevicesFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        //setChewGraphStrokes();

    }

    /*private void setStrokes() {
        mChewGraph.Refresh(chewImageView, data, chewDisp, chewCtr);
    }
*/


    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setTitle("Процесс пережёвывания");
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setElevation(0);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_host, menu);

        mainMenu = menu;

        if (mSharedPreferences.getBoolean("isConnected", false))                                                                        // Если есть привязка к BLE-сервису, то
        {
            mainMenu.findItem(R.id.action_device).setIcon(R.drawable.chew_logo);
        } else {
            mainMenu.findItem(R.id.action_device).setIcon(R.drawable.black_logo);
        }


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }

        if(id == R.id.action_device) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            SearchDevicesFragment searchFragment = new SearchDevicesFragment();
            searchDevicesFragment = searchFragment;
            fragmentTransaction.add(R.id.chewingProcessConstraintLayout, searchFragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();

        //получаем максимальное количество жеваний по умолчанию
        SharedPreferences mSharedPreferences = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        maxChew = mSharedPreferences.getInt("maxChewCounter", 17);

        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString("currentActivityForService", "ChewingProcessActivity");

        editor.apply();

        maxChewTextView.setText("Максимальное кол-во жеваний: " + maxChew);

       /* if(isConnected) {
            handler.postDelayed(new Runnable() {
                public void run() {
                    //do something
                    onSetZeroClick();

                    Log.i("MyLogChewingProcess", "run");

                    runnable=this;

                    handler.postDelayed(runnable, delay);
                }
            }, delay);
        }
*/

       // устанавливаем нулевую линию каждые две секунды
        myTimer = new Timer();
        myTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                // If you want to modify a view in your Activity
                runOnUiThread(new Runnable() {
                    public void run() {
                        onSetZeroClick();
                    }
                });
            }
        }, 2000, 2000); // initial delay 2 second, interval 2 second


        /*if(mLeService.getConnectStatus()) {

            String deviceName = mLeService.deviceName;

            textConnectStatus.setBackgroundResource(R.color.colorDarkGreen);                                             // Установить цвет фона по идентификатору ресурса
            textConnectStatus.setText(deviceName);
        }*/


    }

    @Override
    protected void onPause() {
        //handler.removeCallbacks(runnable); //stop handler when activity not visible
        super.onPause();

        myTimer.cancel();
    }



    //**********************************************************************************************
    //
    //           Публичная функция updateChewCounter() для сервиса BluetoothLeService
    //                            Обновление строки счетчика жеваний
    //
    //  Замечание: Запускается уже сразу в UI-потоке.
    //
    //  Замечание: Так же может вызываться из MainActivity для восстановления  состояния
    //             графического интерфейса
    //
    //  Входные данные: msg - сообщщение о числе жеваний (или любое другое)
    //
    //**********************************************************************************************
    public void updateChewCounter(int chewCounter)
    {


        textViewRate.setText("Жевание: " + String.valueOf(chewCounter));                            // Обновить текстовое поле счетчика жеваний

        numberOfIngestionTextView.setText("Количество циклов(глотаний): " + Dish18Activity.countIngestion);

        if(checkChewCounter < 2 || (checkChewCounter + 1 == chewCounter)) {

        } else if(checkChewCounter < maxChew) {
            Toast.makeText(this, "Не торопитесь", Toast.LENGTH_SHORT).show();
        }

        checkChewCounter = chewCounter;
    }


    //**********************************************************************************************
    //
    //            Публичная функция updateChewData() для сервиса BluetoothLeService
    //                      Обновление графика жеваний и шкалы прижатия
    //
    //  Замечание: Запускается уже сразу в UI-потоке.
    //
    //  Замечание: Так же может вызываться из MainActivity для восстановления  состояния
    //             графического интерфейса
    //
    //  Входные данные: data     - массив данных с сенсора
    //                  chewDisp - позиция жевательного импульса
    //                  chewCtr  - номер жевательного импульса
    //
    //**********************************************************************************************
    public void updateChewData(int[] data, int chewDisp, int chewCtr)
    {
        mChewGraph.Refresh(chewImageView, data, chewDisp, chewCtr);
        //mPressGraph.Refresh(pressImageView, data);
    }

    //**********************************************************************************************
    //
    //          Публичная функция updateConnectStatus() для сервиса BluetoothLeService
    //                      Обновление информации о статусе соединения
    //
    //  Замечание: Запускается уже сразу в UI-потоке.
    //
    //  Замечание: Так же может вызываться из MainActivity для восстановления  состояния
    //             графического интерфейса
    //
    //  Входные данные: status - статус соединения (0 - нет соединения, 1 - есть соединение)
    //
    //**********************************************************************************************
    public void updateConnectStatus(boolean status)
    {


        String msg;
        int    color;

        if (status)                                                                                 // Если есть соединение, то
        {
            msg = "ПОДКЛЮЧЕНО";
            color = R.color.colorDarkGreen;


            /*String deviceName = mLeService.deviceName;

            textConnectStatus.setBackgroundResource(R.color.colorDarkGreen);                        // Установить цвет фона по идентификатору ресурса
            textConnectStatus.setText(deviceName);*/


        }
        else                                                                                        // Иначе нет соединения
        {
            msg = "НЕ ПОДКЛЮЧЕНО";
            color = R.color.colorAlert;

        }

        textConnectStatus.setBackgroundResource(color);                                             // Установить цвет фона по идентификатору ресурса
        textConnectStatus.setText(msg);                                                             // Установить сообщение

    }



    //**********************************************************************************************
    //
    //                            Метод для работы с кнопкой SetZero
    //
    //**********************************************************************************************
    public void onSetZeroClick()
    {
        mChewGraph.CalcVirtualZero();                                                               // Вычислить новый уровень виртуального нуля
    }

    //**********************************************************************************************
    //
    //               Публичная функция onLeListerClick() для класса LeLister
    //            Обрабатывает выбор BluetoothLE-устройства из списка LeLister
    //
    //  Замечание: Запускается уже сразу в UI-потоке.
    //
    //  Входные данные: position - позиция устройства в списке ListView
    //                  address - MAC-адрес выбранного BluetoothLE-устройства
    //
    //**********************************************************************************************
    public void onConnected(String address)
    {
        //mLeLister.removeUnusedLe(position);                                                         // Удалить неиспользуемые записи в списке

        if (mLeServiceBound)                                                                        // Если есть привязка к BLE-сервису, то
        {
            mLeService.stopLeScan();                                                                // Остановить сканирование устройств

            //pbProgressBar.setProgress(100);                                                         // Заполнить ProgressBar сканирования целиком

            mLeService.connectLeDevice(getApplicationContext(), address);

            // Соединиться с устройством в позиции 0 в списке

            Toast.makeText(this, "Соединение установлено", Toast.LENGTH_SHORT).show();

            //updateConnectStatus(mLeService.getConnectStatus());

            //onSetZeroClick();

            //getSupportMenuInflater().inflate(R.menu.activity_main_menu, mainMenu);
            //mainMenu.findItem(R.id.action_no_device).setVisible(false);
            //mainMenu.findItem(R.id.action_device).setIcon(R.drawable.chew_logo);

            mainMenu.findItem(R.id.action_device).setIcon(R.drawable.chew_logo);

            SharedPreferences mSharedPreferences = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);

            SharedPreferences.Editor editor = mSharedPreferences.edit();
            editor.putBoolean("isConnected", true);

            editor.apply();
        }
    }

    //**********************************************************************************************
    //
    //              Публичная функция updateLeList() для сервиса BluetoothLeService
    //   Добавляет новое BluetoothLE-устройство в список, или обновляет информацию о существующем
    //
    //  Замечание: Запускается уже сразу в UI-потоке.
    //
    //  Замечание: При повороте экрана, найденные в этот момент устройства могут быть потеряны,
    //             т.к. в этот момент главная активити не существует. А кешировать устройства
    //             в сервисе ради избежания такого редкого случая - не целесообразно. В крайнем
    //             случае пользователь запустит процесс сканирования эфира еще раз.
    //
    //  Входные данные: name - имя устройства
    //                  address - адрес устройства
    //                  rssi - уровень сигнала устройства
    //
    //**********************************************************************************************
    public void updateLeList(String name, String address, int rssi)
    {
        searchDevicesFragment.setUpdate(name);
        onConnected(address);
    }

    //**********************************************************************************************
    //
    //             Публичная функция updateScanProgress() для сервиса BluetoothLeService
    //                      Отображает прогресс поиска BluetoothLE-устройств
    //
    //  Замечание: Запускается уже сразу в UI-потоке.
    //
    //  Входные данные: progress - процент прогрессса поиска (0..100)
    //
    //**********************************************************************************************
    public void updateScanProgress(int progress)
    {
        searchDevicesFragment.pbProgressBar.setProgress(progress);                                        // Обновить ProgressBar процесса сканирования BLE-устройств

        Log.i("MyLogMainActivity", "\nprogress  -  " + progress);
//        statusUpdate("Progress: " + progress);
        if(progress == 99) {
            Toast.makeText(this, "Поиск завершён", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(ChewGraph.timer != null) {
            ChewGraph.timer.cancel();
        }
    }
}

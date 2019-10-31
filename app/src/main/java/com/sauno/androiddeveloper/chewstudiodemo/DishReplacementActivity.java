package com.sauno.androiddeveloper.chewstudiodemo;

import android.bluetooth.BluetoothAdapter;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.sauno.androiddeveloper.chewstudiodemo.adapter.DishReplacementAdapter;
import com.sauno.androiddeveloper.chewstudiodemo.bluetooth.BluetoothLeService;
import com.sauno.androiddeveloper.chewstudiodemo.bluetooth.LeLister;
import com.sauno.androiddeveloper.chewstudiodemo.data.DatabaseCreateHelper;
import com.sauno.androiddeveloper.chewstudiodemo.data.DishDBHelper;
import com.sauno.androiddeveloper.chewstudiodemo.model.Dish;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class DishReplacementActivity extends AppCompatActivity {
    String dishName;
    int restaurantInt;
    String categoryString;
    boolean observeCalories, observeProteins, observeFats, observeCarbs;
    int dishCalories, dishProteins, dishFats, dishCarbs, dishXE;

    List<Dish> dishesList = new ArrayList<>();

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private final int[][] compatibilityOfElementsArray = {
            {0,2,2,2,2,2,2,2,2,4,3,2,2,2,2,2},
            {2,0,2,4,4,2,3,2,2,4,4,2,2,2,2,2},
            {2,2,0,3,2,2,4,4,2,4,4,3,2,3,2,2},
            {2,4,3,0,3,2,4,4,3,4,4,2,4,3,3,2},
            {2,4,2,3,0,2,4,4,3,4,4,2,2,2,2,4},
            {2,2,2,2,2,0,2,2,2,4,2,2,2,2,2,2},
            {2,3,4,4,4,2,0,2,2,4,4,2,2,3,2,3},
            {2,2,4,4,4,2,2,0,3,4,3,2,3,4,2,4},
            {2,2,2,3,3,2,2,3,0,4,3,3,4,2,2,3},
            {4,4,4,4,4,4,4,4,4,0,4,2,4,4,4,4},
            {3,4,4,4,4,2,4,3,3,4,0,3,4,4,3,4},
            {2,2,3,2,2,2,2,2,3,2,3,0,2,2,2,2},
            {2,2,2,4,2,2,2,3,4,4,4,2,0,4,2,4},
            {2,2,3,3,2,2,3,4,2,4,4,2,4,0,2,3},
            {2,2,2,3,2,2,2,2,2,4,3,2,2,2,0,2},
            {2,3,2,2,4,2,3,4,3,4,4,2,4,3,2,0}};

    SearchDevicesFragment searchDevicesFragment;
    Menu mainMenu;

    SharedPreferences mSharedPreferences;

//    String chosenMeal;

    // Флаг наличия запроса к системному активити на активацию Bluetooth
    public static Boolean fEnableBTRequest = false;

    static final int REQUEST_ENABLE_BT = 1;
    static final int SCAN_PERIOD = 15000;                                                            // Время поиска устройств - 5 секунд
    static final int SCAN_STEP = 25;                                                                // Шаг обновления ProgressBar - 25 мс
    // (SCAN_PERIOD должен быть кратен SCAN_STEP

    LeLister mLeLister = null;                                                                      // Обьект LeLister для списка устройств
    BluetoothLeService mLeService = null;                                                           // Указатель на экземпляр сервиса BluetoothLeService
    boolean            mLeServiceBound = false;                                                     // Флаг наличия привязки к сервису

    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;

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

            mLeService.weakDishReplacementActivity = new WeakReference<>(DishReplacementActivity.this);       // Создать слабую ссылку для MainActivity

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
                Toast.makeText(DishReplacementActivity.this, "BLE not supported on this device",               // Печатаем Toast-сообщение 'BLE not supported on this device'
                        Toast.LENGTH_SHORT).show();                                                 //
                finish();                                                                           // Закрываем приложение
                return;                                                                             // Выход
            }

            //-------------------------------------------------------------------------------------- Восстановление элементов интерфейса

            //updateConnectStatus(mLeService.getConnectStatus());                                     // Восстановить сообщение о соединении
            //updateMaxChew(mLeService.maxChewCounter);                                                 // Восстановить счетчик максимального числа жеваний

//            mLeService.testMethod();                                                                // Запустить тестовый метод сервиса

        }

        //------------------------------------------------------------------------------------------

        @Override
        public void onServiceDisconnected(ComponentName arg0)                                       // При разьединении с сервисом
        {
            //statusUpdate("LeService Disonnected");
            mLeServiceBound = false;                                                                // Снять флаг привязки к сервису
            mLeService = null;                                                                      // Освободить ссылку на сервис
            Log.i("BLE01", "MainActivity - LeService Disconnected");
            //mainMenu.findItem(R.id.action_no_device).setVisible(true);
        }
    };

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

            //getSupportMenuInflater().inflate(R.menu.activity_main_menu, mainMenu);
            //mainMenu.findItem(R.id.action_no_device).setVisible(false);
            mainMenu.findItem(R.id.action_device).setIcon(R.drawable.chew_logo);

            SharedPreferences mSharedPreferences = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);

            SharedPreferences.Editor editor = mSharedPreferences.edit();
            editor.putBoolean("isConnected", true);

            editor.apply();
        }
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dish_replacement);

        mSharedPreferences = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);

        bindService(new Intent(this, BluetoothLeService.class),                                     // Привязаться к сервису BluetoothLeService
                mLeConnection, Context.BIND_AUTO_CREATE);

        setupActionBar();

        Intent intent = getIntent();
        dishName = intent.getStringExtra("dishName");
        restaurantInt = intent.getIntExtra("dishRestaurant", 0);
        categoryString = intent.getStringExtra("dishCategory");
        dishCalories = intent.getIntExtra("dishCalories", 0);
        dishProteins = intent.getIntExtra("dishProteins", 0);
        dishFats = intent.getIntExtra("dishFats", 0);
        dishCarbs = intent.getIntExtra("dishCarbs", 0);
        dishXE = intent.getIntExtra("dishXE", 0);

        Log.i("MyLogDishReplacement", "name - " + dishName + "\nrestaurant - " + restaurantInt + "\ncategory - " + categoryString +
        "\ncalories - " + dishCalories + "\nproteins - " + dishProteins + "\nfats - " + dishFats + "\ncarbs - " + dishCarbs);

        getDishesFromDB();

        mRecyclerView = findViewById(R.id.listReplacementDishesRecyclerView);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        mAdapter = new DishReplacementAdapter(this, dishesList);
        mRecyclerView.setAdapter(mAdapter);


    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setTitle("Рекомендуемые блюда");
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
            Intent intent = new Intent();
            setResult(RESULT_OK, intent);
            finish();
            return true;
        }

        if(id == R.id.action_device) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            SearchDevicesFragment searchFragment = new SearchDevicesFragment();
            searchDevicesFragment = searchFragment;
            fragmentTransaction.add(R.id.dishReplacementActivity, searchFragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString("currentActivityForService", "HomeMenuActivity");

        editor.apply();

        if(mainMenu != null) {
            if (mSharedPreferences.getBoolean("isConnected", false))                                                                        // Если есть привязка к BLE-сервису, то
            {
                mainMenu.findItem(R.id.action_device).setIcon(R.drawable.chew_logo);
            } else {
                mainMenu.findItem(R.id.action_device).setIcon(R.drawable.black_logo);
            }
        }
    }

    private void getDishesFromDB() {
        DatabaseCreateHelper databaseCreateHelper = new DatabaseCreateHelper(this);
        SQLiteDatabase db = databaseCreateHelper.getWritableDatabase();


        String[] projection = {
                DishDBHelper.COLUMN_ID,
                DishDBHelper.COLUMN_DESCRIPTION,
                DishDBHelper.COLUMN_CALORIC,
                DishDBHelper.COLUMN_PROTEINS,
                DishDBHelper.COLUMN_FATS,
                DishDBHelper.COLUMN_CARBS,
                DishDBHelper.COLUMN_XE,
                DishDBHelper.COLUMN_GLOBAL_CATEGORY
        };
        String selection = DishDBHelper.COLUMN_RESTAURANT + " = ?" + " AND " + DishDBHelper.COLUMN_CATEGORY + " = ?";
        String[] selectionArgs = {restaurantInt + "", categoryString};

        Cursor cursor = db.query(
                true,
                DishDBHelper.TABLE,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null,
                null
        );

        List<Dish> dishes = new ArrayList<>();

        int c1, c2, c3, c4, c5, c6, c7, c8, c9 ,c10 ,c11, c12, c13, c14, c15, c16;

        if (cursor.moveToFirst()) {
            do {
                int idDish = cursor.getInt(cursor.getColumnIndexOrThrow(DishDBHelper.COLUMN_ID));
                String dishName = cursor.getString(cursor.getColumnIndexOrThrow(DishDBHelper.COLUMN_DESCRIPTION));
                int calories = cursor.getInt(cursor.getColumnIndexOrThrow(DishDBHelper.COLUMN_CALORIC));
                int proteins = cursor.getInt(cursor.getColumnIndexOrThrow(DishDBHelper.COLUMN_PROTEINS));
                int fats = cursor.getInt(cursor.getColumnIndexOrThrow(DishDBHelper.COLUMN_FATS));
                int carbs = cursor.getInt(cursor.getColumnIndexOrThrow(DishDBHelper.COLUMN_CARBS));
                int xe = cursor.getInt(cursor.getColumnIndexOrThrow(DishDBHelper.COLUMN_XE));

               /* c1 = cursor.getInt(cursor.getColumnIndexOrThrow(DishDBHelper.COLUMN_COMPATIBILITY_1));
                c2 = cursor.getInt(cursor.getColumnIndexOrThrow(DishDBHelper.COLUMN_COMPATIBILITY_2));
                c3 = cursor.getInt(cursor.getColumnIndexOrThrow(DishDBHelper.COLUMN_COMPATIBILITY_3));
                c4 = cursor.getInt(cursor.getColumnIndexOrThrow(DishDBHelper.COLUMN_COMPATIBILITY_4));
                c5 = cursor.getInt(cursor.getColumnIndexOrThrow(DishDBHelper.COLUMN_COMPATIBILITY_5));
                c6 = cursor.getInt(cursor.getColumnIndexOrThrow(DishDBHelper.COLUMN_COMPATIBILITY_6));
                c7 = cursor.getInt(cursor.getColumnIndexOrThrow(DishDBHelper.COLUMN_COMPATIBILITY_7));
                c8 = cursor.getInt(cursor.getColumnIndexOrThrow(DishDBHelper.COLUMN_COMPATIBILITY_8));
                c9 = cursor.getInt(cursor.getColumnIndexOrThrow(DishDBHelper.COLUMN_COMPATIBILITY_9));
                c10 = cursor.getInt(cursor.getColumnIndexOrThrow(DishDBHelper.COLUMN_COMPATIBILITY_10));
                c11 = cursor.getInt(cursor.getColumnIndexOrThrow(DishDBHelper.COLUMN_COMPATIBILITY_11));
                c12 = cursor.getInt(cursor.getColumnIndexOrThrow(DishDBHelper.COLUMN_COMPATIBILITY_12));
                c13 = cursor.getInt(cursor.getColumnIndexOrThrow(DishDBHelper.COLUMN_COMPATIBILITY_13));
                c14 = cursor.getInt(cursor.getColumnIndexOrThrow(DishDBHelper.COLUMN_COMPATIBILITY_14));
                c15 = cursor.getInt(cursor.getColumnIndexOrThrow(DishDBHelper.COLUMN_COMPATIBILITY_15));
                c16 = cursor.getInt(cursor.getColumnIndexOrThrow(DishDBHelper.COLUMN_COMPATIBILITY_16));

                int[] compatibilityIntArray = {c1, c2, c3, c4, c5, c6, c7, c8, c9 ,c10 ,c11, c12, c13, c14, c15, c16};*/

                //int compatibilityEvaluation = getCompatibilityEvaluation(compatibilityIntArray);

                String globalCategory = cursor.getString(cursor.getColumnIndexOrThrow(DishDBHelper.COLUMN_GLOBAL_CATEGORY));


                Dish dish = new Dish(idDish, dishName, calories, proteins, fats, carbs, xe, 4, globalCategory);

                dishes.add(dish);

            }while (cursor.moveToNext());
        }

        checkLimitCPFC(dishes);

        cursor.close();
        db.close();

        if(dishesList.size() == 0) {
            Toast.makeText(this, "Замены нет", Toast.LENGTH_SHORT).show();
            finish();
        }
    }


    private void checkLimitCPFC(List<Dish> dishes) {
        SharedPreferences mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        observeCalories = mSharedPreferences.getBoolean(UserPreferencesActivity.KEY_CALORIE_CHECK_BOX_PREF, false);
        observeProteins = mSharedPreferences.getBoolean(UserPreferencesActivity.KEY_PROTEIN_CHECK_BOX_PREF, false);
        observeFats = mSharedPreferences.getBoolean(UserPreferencesActivity.KEY_FAT_CHECK_BOX_PREF, false);
        observeCarbs = mSharedPreferences.getBoolean(UserPreferencesActivity.KEY_CARBOHYDRATE_CHECK_BOX_PREF, false);

        for(int i = 0 ; i < dishes.size() ; i++) {
            if(observeCalories) {
                if(dishes.get(i).getDishCalories() >= dishCalories) {
                    continue;
                }
            }
            if(observeProteins) {
                if(dishes.get(i).getDishProteins() >= dishProteins) {
                    continue;
                }
            }
            if(observeFats) {
                if(dishes.get(i).getDishFats() >= dishFats) {
                    continue;
                }
            }
            if(observeCarbs) {
                if(dishes.get(i).getDishCarbs() >= dishCarbs) {
                    continue;
                }
            }

            if(!isExistInOrderList(dishes.get(i).getDishName())) {
                dishesList.add(dishes.get(i));
            }
        }

        //Для тестирования
        for(int i = 0 ; i < dishesList.size() ; i++) {
            Log.i("MyLogReplacement", "Итоговые блюда - " + dishesList.get(i).getDishName());
        }

    }


    private int getCompatibilityEvaluation(int[] compatibilityIntArray) {
        int result = 4;

        for(int n = 0 ; n < 16 ; n++) {
            if(compatibilityIntArray[n] == 1) {
                for(int k = n + 1 ; k < 16 ; k++) {
                    if(compatibilityIntArray[k] == 1) {
                        int x = 4;
                        x = compatibilityOfElementsArray[n][k];
                        //Log.i("MyLogListDishes", "n = " + n + "  k = " + k + "  x = " + x);
                        if(x == 3) {
                            result = 3;
                        }
                        if(x == 2) {
                            result = 2;
                            n = 16;
                            k = 16;
                        }
                    }
                }
            }
        }

        return result;
    }


    private boolean isExistInOrderList(String checkDishName) {

        for(int n = 0; n < ChoiceOfDishesActivity.dishOrderList.size(); n++) {
            if(ChoiceOfDishesActivity.dishOrderList.get(n).getDishName().equals(checkDishName)) {
                return true;
            }
        }

        return false;
    }


}

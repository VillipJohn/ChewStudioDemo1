package com.sauno.androiddeveloper.chewstudiodemo;

import android.bluetooth.BluetoothAdapter;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.sauno.androiddeveloper.chewstudiodemo.adapter.HomeMenuAdapter;
import com.sauno.androiddeveloper.chewstudiodemo.bluetooth.BluetoothLeService;
import com.sauno.androiddeveloper.chewstudiodemo.bluetooth.LeLister;
import com.sauno.androiddeveloper.chewstudiodemo.data.DatabaseCreateHelper;
import com.sauno.androiddeveloper.chewstudiodemo.data.DishDBHelper;
import com.sauno.androiddeveloper.chewstudiodemo.model.Dish;
import com.sauno.androiddeveloper.chewstudiodemo.utility.RecyclerItemTouchHelperForHomeMenu;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class HomeMenuActivity extends AppCompatActivity implements RecyclerItemTouchHelperForHomeMenu.RecyclerItemTouchHelperListener{

    SearchDevicesFragment searchDevicesFragment;
    Menu mainMenu;

    SharedPreferences mSharedPreferences;

    Button addDishButton;

    RecyclerView homeMenuRecyclerView;

    List<Dish> dishesList = new ArrayList<>();

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

            mLeService.weakHomeMenuActivity = new WeakReference<>(HomeMenuActivity.this);       // Создать слабую ссылку для MainActivity

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
                Toast.makeText(HomeMenuActivity.this, "BLE not supported on this device",               // Печатаем Toast-сообщение 'BLE not supported on this device'
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
    private EditText dishSearchEditText;

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
        setContentView(R.layout.activity_home_menu);

        mSharedPreferences = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);

        bindService(new Intent(this, BluetoothLeService.class),                                     // Привязаться к сервису BluetoothLeService
                mLeConnection, Context.BIND_AUTO_CREATE);

        setupActionBar();

        addDishButton = findViewById(R.id.addDishButton);
        addDishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AddDishActivity.class);
                startActivity(intent);
            }
        });

        dishSearchEditText = findViewById(R.id.dishSearchEditText);
        dishSearchEditText.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {}

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                String search = s.toString();
                setSearch(search);
            }
        });

        homeMenuRecyclerView = findViewById(R.id.homeMenuRecyclerView);

    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setTitle("Домашнее меню");
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
            fragmentTransaction.add(R.id.homeMenuActivity, searchFragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();

        getDishesFromDB();

        homeMenuRecyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        homeMenuRecyclerView.setLayoutManager(mLayoutManager);


        RecyclerView.Adapter mAdapter = new HomeMenuAdapter(this, dishesList);
        homeMenuRecyclerView.setAdapter(mAdapter);

        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerItemTouchHelperForHomeMenu(0, ItemTouchHelper.RIGHT, this);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(homeMenuRecyclerView);

        ItemTouchHelper.SimpleCallback itemTouchHelperCallback1 = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                // Row is swiped from recycler view
                // remove it from adapter

            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };

        // attaching the touch helper to recycler view
        new ItemTouchHelper(itemTouchHelperCallback1).attachToRecyclerView(homeMenuRecyclerView);



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

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if (viewHolder instanceof HomeMenuAdapter.ViewHolder) {
            String dishName = dishesList.get(viewHolder.getAdapterPosition()).getDishName();

            deleteDish(dishName);

           /* String dishName = dishOrderNames[viewHolder.getAdapterPosition()];

            for (int i = 0; i < ChoiceOfDishesActivity.dishOrderList.size(); i++) {
                String dish = ChoiceOfDishesActivity.dishOrderList.get(i).getDishName();
                if (dish.equals(dishName)) {
                    setDishToLastThreeRemoteDishes(ChoiceOfDishesActivity.dishOrderList.get(i));
                    ChoiceOfDishesActivity.dishOrderList.remove(i);
                }
            }
            dataToAdapter();*/
        }
    }

    //удаление блюда из таблицы любимых блюд
    private void deleteDish(String dishName) {
       /* int[] idFavoriteDishes = resultsOfEatingActivity.idFavoriteDishes;

        int numberDishColumn = 21;

        for(int i = 0; i < idFavoriteDishes.length; i++) {
            if(resultsOfEatingActivity.idesArrayList.get(position) == idFavoriteDishes[i]) {
                numberDishColumn = i;
            }
        }

        int[] newIdFavoriteDishes = new int[idFavoriteDishes.length];

        for(int i = 0; i < idFavoriteDishes.length; i++) {
            if(i == idFavoriteDishes.length-1) {
                newIdFavoriteDishes[i] = 0;
                break;
            }
            if(i < numberDishColumn) {
                newIdFavoriteDishes[i] = idFavoriteDishes[i];
                continue;
            }
            newIdFavoriteDishes[i] = idFavoriteDishes[i + 1];
        }

        resultsOfEatingActivity.idFavoriteDishes = newIdFavoriteDishes;

        for (int n : newIdFavoriteDishes) {
            Log.i("MyLogAboutDish", "n = " + n);
        }*/

        DatabaseCreateHelper databaseCreateHelper = new DatabaseCreateHelper(this);
        SQLiteDatabase db = databaseCreateHelper.getWritableDatabase();

        ContentValues favoritDishValue = new ContentValues();

        String selection = DishDBHelper.COLUMN_RESTAURANT + " = ? AND " + DishDBHelper.COLUMN_DESCRIPTION + " =? ";

        Log.d("MyLogHomeMenu", "dishName - " + dishName);

       /*
        TABLE_COLUMN_ONE + " = ? AND " + TABLE_COLUMN_TWO + " = ?"
        String idString = "" + idForFavoriteDishesTable;*/
        String[] selectionArgs = {"11", dishName};

        db.delete(DishDBHelper.TABLE,
                selection,
                selectionArgs);

        db.close();

        refreshList();
    }

    private void refreshList() {
        getDishesFromDB();

        homeMenuRecyclerView.removeAllViews();

        RecyclerView.Adapter mAdapter = new HomeMenuAdapter(this, dishesList);
        homeMenuRecyclerView.setAdapter(mAdapter);
    }

    private void setSearch(String search) {
        int searchSize = search.length();

        List<Dish> searchDishesList = new ArrayList<>();

        for(Dish dish: dishesList) {
            if(search.length() > dish.getDishName().length()) {
                continue;
            }

            String subString = dish.getDishName().substring(0, searchSize);

            if(search.equalsIgnoreCase(subString)) {
                searchDishesList.add(dish);
            }
        }

        homeMenuRecyclerView.removeAllViews();

        RecyclerView.Adapter mAdapter = new HomeMenuAdapter(this, searchDishesList);
        homeMenuRecyclerView.setAdapter(mAdapter);
    }






    private void getDishesFromDB() {
        DatabaseCreateHelper databaseCreateHelper = new DatabaseCreateHelper(getApplicationContext());
        SQLiteDatabase db;
        try {
            db = databaseCreateHelper.getWritableDatabase();
        }
        catch (SQLiteException ex){
            db = databaseCreateHelper.getReadableDatabase();
        }

        String[] projection = {
                DishDBHelper.COLUMN_ID,
                DishDBHelper.COLUMN_DESCRIPTION,
                DishDBHelper.COLUMN_CALORIC,
                DishDBHelper.COLUMN_PROTEINS,
                DishDBHelper.COLUMN_FATS,
                DishDBHelper.COLUMN_CARBS,
                DishDBHelper.COLUMN_XE,
                DishDBHelper.COLUMN_COMPATIBILITY_1,
                DishDBHelper.COLUMN_COMPATIBILITY_2,
                DishDBHelper.COLUMN_COMPATIBILITY_3,
                DishDBHelper.COLUMN_COMPATIBILITY_4,
                DishDBHelper.COLUMN_COMPATIBILITY_5,
                DishDBHelper.COLUMN_COMPATIBILITY_6,
                DishDBHelper.COLUMN_COMPATIBILITY_7,
                DishDBHelper.COLUMN_COMPATIBILITY_8,
                DishDBHelper.COLUMN_COMPATIBILITY_9,
                DishDBHelper.COLUMN_COMPATIBILITY_10,
                DishDBHelper.COLUMN_COMPATIBILITY_11,
                DishDBHelper.COLUMN_COMPATIBILITY_12,
                DishDBHelper.COLUMN_COMPATIBILITY_13,
                DishDBHelper.COLUMN_COMPATIBILITY_14,
                DishDBHelper.COLUMN_COMPATIBILITY_15,
                DishDBHelper.COLUMN_COMPATIBILITY_16,
                DishDBHelper.COLUMN_GLOBAL_CATEGORY
        };

        // Filter results WHERE "title" = 'My Title'
        String selection = DishDBHelper.COLUMN_RESTAURANT + " = ?";
        String[] selectionArgs = {"11"};


        Cursor cursor = db.query(
                DishDBHelper.TABLE,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );


        int c1, c2, c3, c4, c5, c6, c7, c8, c9 ,c10 ,c11, c12, c13, c14, c15, c16;

        dishesList.clear();

        if (cursor.moveToFirst()) {

            do {
                int idDish = cursor.getInt(cursor.getColumnIndexOrThrow(DishDBHelper.COLUMN_ID));
                String dishName = cursor.getString(cursor.getColumnIndexOrThrow(DishDBHelper.COLUMN_DESCRIPTION));
                int calories = cursor.getInt(cursor.getColumnIndexOrThrow(DishDBHelper.COLUMN_CALORIC));
                int proteins = cursor.getInt(cursor.getColumnIndexOrThrow(DishDBHelper.COLUMN_PROTEINS));
                int fats = cursor.getInt(cursor.getColumnIndexOrThrow(DishDBHelper.COLUMN_FATS));
                int carbs = cursor.getInt(cursor.getColumnIndexOrThrow(DishDBHelper.COLUMN_CARBS));
                int xe = cursor.getInt(cursor.getColumnIndexOrThrow(DishDBHelper.COLUMN_XE));

                Dish dish = new Dish(
                        idDish,
                        dishName,
                        calories, proteins, fats, carbs, xe,
                        4,
                        ""
                        );

                dishesList.add(dish);


                //BigDecimal dishOrderQuantity = new BigDecimal(0).setScale(2, BigDecimal.ROUND_DOWN);


                c1 = cursor.getInt(cursor.getColumnIndexOrThrow(DishDBHelper.COLUMN_COMPATIBILITY_1));
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

                //compatibilityArraysList.add(new int[] {c1, c2, c3, c4, c5, c6, c7, c8, c9 ,c10 ,c11, c12, c13, c14, c15, c16});

                //int[] compatibilityIntArray = {c1, c2, c3, c4, c5, c6, c7, c8, c9 ,c10 ,c11, c12, c13, c14, c15, c16};

                //int compatibilityEvaluation = getCompatibilityEvaluation(compatibilityIntArray);
                //compatibilityEvaluationList.add(compatibilityEvaluation);

                //Log.i("MyLogListDishes", "DishName - " + dishName + "   compatibilityEvaluation - " + compatibilityEvaluation);

                String globalCategory = cursor.getString(cursor.getColumnIndexOrThrow(DishDBHelper.COLUMN_GLOBAL_CATEGORY));
                //globalCategoryList.add(globalCategory);
            }while (cursor.moveToNext());

        }

        //dishNamesArray = dishNames.toArray(new String[dishNames.size()]);

        //dishOrderQuantity = new int[dishNamesArray.length];

        //dishOrderQuantity = getDataForDishOrderQuantity();


        db.close();
        cursor.close();
    }
}

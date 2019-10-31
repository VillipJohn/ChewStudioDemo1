package com.sauno.androiddeveloper.chewstudiodemo;

import android.bluetooth.BluetoothAdapter;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sauno.androiddeveloper.chewstudiodemo.adapter.ChoiceOfDishesAdapter;
import com.sauno.androiddeveloper.chewstudiodemo.bluetooth.BluetoothLeService;
import com.sauno.androiddeveloper.chewstudiodemo.bluetooth.LeLister;
import com.sauno.androiddeveloper.chewstudiodemo.data.DatabaseCreateHelper;
import com.sauno.androiddeveloper.chewstudiodemo.data.DishDBHelper;
import com.sauno.androiddeveloper.chewstudiodemo.data.FavoriteDishesDBHelper;
import com.sauno.androiddeveloper.chewstudiodemo.model.DishOrderItem;

import java.lang.ref.WeakReference;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ChoiceOfDishesActivity extends AppCompatActivity {
    public static List<DishOrderItem> dishOrderList;

    public static List<String> viewedCategoriesList = new ArrayList<>();

    public static BigDecimal sumOrderCalories;
    public static BigDecimal sumOrderProteins;
    public static BigDecimal sumOrderFats;
    public static BigDecimal sumOrderCarbs;
    public static BigDecimal sumOrderXE;

    public static int countChosenDishes;

    SQLiteDatabase db;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private String[] categoriesArray;

    Button setFavoriteDishesButton;
    Button mOkButton;

    private LinearLayout containerCPFCXLinearLayout;

    TextView countCaloriesTextView;
    TextView countProteinsTextView;
    TextView countFatsTextView;
    TextView countCarbohydratesTextView;
    TextView countXETextView;

    private TextView normCaloriesTextView;
    private TextView normProteinsTextView;
    private TextView normFatsTextView;
    private TextView normCarbsTextView;
    private TextView normXETextView;

    private TextView aboveCaloriesTextView;
    private TextView aboveProteinsTextView;
    private TextView aboveFatsTextView;
    private TextView aboveCarbohydratesTextView;
    private TextView aboveXETextView;

    int currentUserPartOfCalories;
    int currentUserPartOfProteins;
    int currentUserPartOfFats;
    int currentUserPartOfCarbohydrates;
    private int currentUserPartOfXE;

    boolean isExistUserCPFC;

    private int restaurant;

    public static int idForFavoriteDishesTable = -1;
    //private int[] idDishesOfCategoryArray;
    private int[] idFavoriteDishesArray = new int[20];
    private  List<Integer> idDishesOfCategoryList = new ArrayList<>();

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

            mLeService.weakChoiceOfDishesActivity = new WeakReference<>(ChoiceOfDishesActivity.this);       // Создать слабую ссылку для MainActivity

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
                Toast.makeText(ChoiceOfDishesActivity.this, "BLE not supported on this device",               // Печатаем Toast-сообщение 'BLE not supported on this device'
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
        setContentView(R.layout.activity_choice_of_dishes);

        mSharedPreferences = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);

        bindService(new Intent(this, BluetoothLeService.class),                        // Привязаться к сервису BluetoothLeService
                mLeConnection, Context.BIND_AUTO_CREATE);

        setupActionBar();

        restaurant = getIntent().getIntExtra("restaurant", 0);
        //Log.d("MyLogChoiceOfDishes", "Ресторан - " + restaurant);

        getCategoriesFromDB();

        containerCPFCXLinearLayout = findViewById(R.id.containerCPFCXLinearLayout);

        countCaloriesTextView = findViewById(R.id.countCaloriesTextView);
        countProteinsTextView = findViewById(R.id.countProteinsTextView);
        countFatsTextView = findViewById(R.id.countFatsTextView);
        countCarbohydratesTextView = findViewById(R.id.countCarbohydratesTextView);
        countXETextView = findViewById(R.id.countXETextView);

        normCaloriesTextView = findViewById(R.id.normCaloriesTextView);
        normProteinsTextView = findViewById(R.id.normProteinsTextView);
        normFatsTextView = findViewById(R.id.normFatsTextView);
        normCarbsTextView = findViewById(R.id.normCarbsTextView);
        normXETextView = findViewById(R.id.normXETextView);

        aboveCaloriesTextView = findViewById(R.id.aboveCaloriesTextView);
        aboveProteinsTextView = findViewById(R.id.aboveProteinsTextView);
        aboveFatsTextView = findViewById(R.id.aboveFatsTextView);
        aboveCarbohydratesTextView = findViewById(R.id.aboveCarbohydratesTextView);
        aboveXETextView = findViewById(R.id.aboveXETextView);



        /*favoriteDishesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), FavoriteDishesActivity.class);
                intent.putExtra("idForFavoriteDishesTable", idForFavoriteDishesTable);
                view.getContext().startActivity(intent);
            }
        });*/

        Button setFavoriteDishesButton = findViewById(R.id.setFavoriteDishesButton);
        setFavoriteDishesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setFavoriteDishes();
            }
        });

        mOkButton = findViewById(R.id.okButton);

        mOkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), BasketActivity.class);
                view.getContext().startActivity(intent);
            }
        });

        mRecyclerView = findViewById(R.id.my_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        mAdapter = new ChoiceOfDishesAdapter(categoriesArray);
        mRecyclerView.setAdapter(mAdapter);

        countChosenDishes = 0;

        int lastRestaurant = mSharedPreferences.getInt("lastRestaurant", -1);

        if(restaurant != lastRestaurant) {
            dishOrderList = new ArrayList<>();
        } else {
            countChosenDishes = dishOrderList.size();
        }

        checkIfExistUserCPFC();

        if(isExistUserCPFC) {
            getCurrentUserCPFC();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString("currentActivityForService", "ChoiceOfDishesActivity");

        editor.apply();

        setData();

        //BigDecimal dishQuantity = new BigDecimal(0).setScale(2, BigDecimal.ROUND_DOWN);



        //Log.i("Choice", countChosenDishesString);

        /*for (int i = 0; i < dishOrderList.size(); i++) {
            String dish = dishOrderList.get(i).getDishName();
            Log.i("MyLogListDishesAdapter", "Dish - " + dish);
            Log.i("MyLogListDishesAdapter", "Размер - " + dishOrderList.size());
        }*/

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
    protected void onPause() {
        super.onPause();

        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putInt("lastRestaurant", restaurant);

        editor.apply();


    }

    //установка и отображение данных на экране
    private void setData() {
        mRecyclerView.removeAllViews();
        mAdapter = new ChoiceOfDishesAdapter(categoriesArray);
        mRecyclerView.setAdapter(mAdapter);

        sumOrderCalories = new BigDecimal(0.00).setScale(0, BigDecimal.ROUND_DOWN);
        sumOrderProteins = new BigDecimal(0.00).setScale(0, BigDecimal.ROUND_DOWN);
        sumOrderFats = new BigDecimal(0.00).setScale(0, BigDecimal.ROUND_DOWN);
        sumOrderCarbs = new BigDecimal(0.00).setScale(0, BigDecimal.ROUND_DOWN);
        sumOrderXE = new BigDecimal(0.00).setScale(0, BigDecimal.ROUND_DOWN);

        for(int i = 0; i < dishOrderList.size(); i++) {
            BigDecimal dishQuantity = dishOrderList.get(i).getQuantityDishes();

            sumOrderCalories = sumOrderCalories.add((dishOrderList.get(i).getCalories()).multiply(dishQuantity));
            sumOrderProteins = sumOrderProteins.add((dishOrderList.get(i).getProteins()).multiply(dishQuantity));
            sumOrderFats = sumOrderFats.add((dishOrderList.get(i).getFats()).multiply(dishQuantity));
            sumOrderCarbs = sumOrderCarbs.add((dishOrderList.get(i).getCarbs()).multiply(dishQuantity));
            sumOrderXE = sumOrderXE.add((dishOrderList.get(i).getXe()).multiply(dishQuantity));

        }


        int sumOrederCaloriesInt = sumOrderCalories.intValue();
        int sumOrderProteinsInt = sumOrderProteins.intValue();
        int sumOrderFatsInt = sumOrderFats.intValue();
        int sumOrderCarbsInt = sumOrderCarbs.intValue();
        int sumOrderXEInt = sumOrderXE.intValue();

        countCaloriesTextView.setText("" + sumOrederCaloriesInt);
        countProteinsTextView.setText(sumOrderProteinsInt + "г");
        countFatsTextView.setText(sumOrderFatsInt + "г");
        countCarbohydratesTextView.setText(sumOrderCarbsInt + "г");
        countXETextView.setText("" + sumOrderXEInt);

        if(isExistUserCPFC) {
            setAboveLimitColorForCPFC();
        }


        String countChosenDishesString = "Готово" + "(" + countChosenDishes + ")";
        mOkButton.setText(countChosenDishesString);
    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setTitle("Выбор блюд");
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
            fragmentTransaction.add(R.id.choiceOfDishesContent, searchFragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        }

        return super.onOptionsItemSelected(item);
    }

    private void getCategoriesFromDB() {
        DatabaseCreateHelper databaseCreateHelper = new DatabaseCreateHelper(getApplicationContext());
        //SQLiteDatabase db;
        try {
            db = databaseCreateHelper.getWritableDatabase();
        }
        catch (SQLiteException ex){
            db = databaseCreateHelper.getReadableDatabase();
        }

        String[] projection = {
                DishDBHelper.COLUMN_CATEGORY
        };

        String selection = DishDBHelper.COLUMN_RESTAURANT + " = ?";
        String restaurantString = "" + restaurant;
        String[] selectionArgs = {restaurantString};

        Cursor cursor = db.query(
                true,
                DishDBHelper.TABLE,
                projection,
                selection,
                selectionArgs,
                DishDBHelper.COLUMN_CATEGORY,
                null,
                null,
                null
        );

        List<String> categories = new ArrayList<>();

        if (cursor.moveToFirst()) {
            do {
                String category = cursor.getString(cursor.getColumnIndexOrThrow(DishDBHelper.COLUMN_CATEGORY));
                if(category != null && !category.equals("")) {
                    categories.add(category);
                }

                //Log.i("MyLog",category + "\n");

            }while (cursor.moveToNext());
        }

        categoriesArray = categories.toArray(new String[categories.size()]);

        cursor.close();
        db.close();
    }

/*    private void setAboveLimitColorForCPFC() {
        int sumCaloriesInt = sumOrderCalories.intValue();
        int sumProteinsInt = sumOrderProteins.intValue();
        int sumFatsInt = sumOrderFats.intValue();
        int sumCarbsInt = sumOrderCarbs.intValue();

        if(currentUserPartOfCalories < sumCaloriesInt) {
            countCaloriesTextView.setTextColor(Color.RED);
        } else {
            countCaloriesTextView.setTextColor(Color.BLACK);
        }

        if(currentUserPartOfProteins < sumProteinsInt) {
            countProteinsTextView.setTextColor(Color.RED);
        } else {
            countProteinsTextView.setTextColor(Color.BLACK);
        }

        if(currentUserPartOfFats < sumFatsInt) {
            countFatsTextView.setTextColor(Color.RED);
        } else {
            countFatsTextView.setTextColor(Color.BLACK);
        }

        if(currentUserPartOfCarbohydrates < sumCarbsInt) {
            countCarbohydratesTextView.setTextColor(Color.RED);
        } else {
            countCarbohydratesTextView.setTextColor(Color.BLACK);
        }
    }*/

    private void setAboveLimitColorForCPFC() {
        int sumCaloriesInt = sumOrderCalories.intValue();
        int sumProteinsInt = sumOrderProteins.intValue();
        int sumFatsInt = sumOrderFats.intValue();
        int sumCarbsInt = sumOrderCarbs.intValue();
        int sumXEInt = sumOrderXE.intValue();

        boolean isAboveCalories, isAboveProteins, isAboveFats, isAboveCarbs, isAboveXE;
        if(currentUserPartOfCalories < sumCaloriesInt) {
            countCaloriesTextView.setText("" + sumCaloriesInt);
            normCaloriesTextView.setText("" + currentUserPartOfCalories);
            aboveCaloriesTextView.setText("+" + (sumCaloriesInt - currentUserPartOfCalories));
            isAboveCalories = true;
        } else {
            normCaloriesTextView.setText("" + currentUserPartOfCalories);
            aboveCaloriesTextView.setText("");
            isAboveCalories = false;
        }

        if(currentUserPartOfProteins < sumProteinsInt) {
            countProteinsTextView.setText("" + sumProteinsInt + "г");
            normProteinsTextView.setText("" + currentUserPartOfProteins + "г");
            aboveProteinsTextView.setText("+" + (sumProteinsInt - currentUserPartOfProteins) + "г");
            isAboveProteins = true;
        } else {
            aboveProteinsTextView.setText("");
            normProteinsTextView.setText("" + currentUserPartOfProteins + "г");
            isAboveProteins = false;
        }

        if(currentUserPartOfFats < sumFatsInt) {
            countFatsTextView.setText("" + sumFatsInt + "г");
            normFatsTextView.setText("" + currentUserPartOfFats + "г");
            aboveFatsTextView.setText("+" + (sumFatsInt - currentUserPartOfFats) + "г");
            isAboveFats = true;
        } else {
            aboveFatsTextView.setText("");
            normFatsTextView.setText("" + currentUserPartOfFats + "г");
            isAboveFats = false;
        }

        if(currentUserPartOfCarbohydrates < sumCarbsInt) {
            countCarbohydratesTextView.setText("" + sumCarbsInt + "г");
            normCarbsTextView.setText("" + currentUserPartOfCarbohydrates + "г");
            aboveCarbohydratesTextView.setText("+" + (sumCarbsInt - currentUserPartOfCarbohydrates) + "г");
            isAboveCarbs = true;
        } else {
            aboveCarbohydratesTextView.setText("");
            normCarbsTextView.setText("" + currentUserPartOfCarbohydrates + "г");
            isAboveCarbs = false;
        }

        if(sumXEInt > currentUserPartOfXE) {
            countXETextView.setText("" + sumXEInt);
            normXETextView.setText("" + currentUserPartOfXE);
            aboveXETextView.setText("+" + (sumXEInt - currentUserPartOfXE));
            isAboveXE = true;
        } else {
            aboveXETextView.setText("");
            normXETextView.setText("" + currentUserPartOfXE);
            isAboveXE = false;
        }

        if(isAboveCalories || isAboveProteins || isAboveFats || isAboveCarbs || isAboveXE) {
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams)containerCPFCXLinearLayout.getLayoutParams();
            layoutParams.height = dpToPx(80);
            containerCPFCXLinearLayout.setLayoutParams(layoutParams);
        } else {
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams)containerCPFCXLinearLayout.getLayoutParams();
            layoutParams.height = dpToPx(60);
            containerCPFCXLinearLayout.setLayoutParams(layoutParams);
        }
    }

    private int dpToPx(int dp) {
        float density = this.getResources()
                .getDisplayMetrics()
                .density;
        return Math.round((float) dp * density);
    }

    //получение текущих КБЖУХ
    private void getCurrentUserCPFC() {
        SharedPreferences mSharedPreferences = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        currentUserPartOfCalories = mSharedPreferences.getInt("currentCalories", 0);
        currentUserPartOfProteins = mSharedPreferences.getInt("currentProteins", 0);
        currentUserPartOfFats = mSharedPreferences.getInt("currentFats", 0);
        currentUserPartOfCarbohydrates = mSharedPreferences.getInt("currentCarbs", 0);
        currentUserPartOfXE = mSharedPreferences.getInt("currentXE", 0);
    }

    //проверка есть ли текущие КБЖУХ
    private void checkIfExistUserCPFC() {
        SharedPreferences mSharedPreferences = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        int userDayCalories = mSharedPreferences.getInt(UserProfileActivity.KEY_COUNT_CALORIES, 0);

        if(userDayCalories == 0) {
            Toast.makeText(this, "Для подсчёта рекомендуемых Вам ежедневно калорий, белков, жиров, углеводов введите: пол, возраст, рост, вес, образ жизни", Toast.LENGTH_SHORT).show();
            isExistUserCPFC = false;
        } else {
            isExistUserCPFC =true;
        }

    }

    //добавление любимых блюд
    private void setFavoriteDishes() {
        getIdOfFavoriteDishesCurrentUser();

        if(idForFavoriteDishesTable == -1) {
            Toast.makeText(this, "Профиль не выбран. Эта функция не может работать", Toast.LENGTH_SHORT).show();
        } else {
            getIdDishesOfCategory();
            getIdFavoriteDishes();

            int[] favoriteDishesInCategoryArray = new int[20];
            int n = 0;

            for(int i=0; i<20; i++) {
                if(idDishesOfCategoryList.contains(idFavoriteDishesArray[i])) {
                    favoriteDishesInCategoryArray[n] = idFavoriteDishesArray[i];
                    n++;
                }
            }

            for(int i=0; i<20; i++) {
                if(favoriteDishesInCategoryArray[i] != 0) {
                    Log.i("MyLogChoiceOfDishes", "айдишник  -  " + favoriteDishesInCategoryArray[i]);

                    getFavoriteDishAndSetToList(favoriteDishesInCategoryArray[i]);
                }
            }
            setData();
        }
    }

    private void getIdOfFavoriteDishesCurrentUser() {
        idForFavoriteDishesTable = mSharedPreferences.getInt("idForFavoriteDishes", -1);
    }


    private void getIdDishesOfCategory() {
        DatabaseCreateHelper databaseCreateHelper = new DatabaseCreateHelper(this);
        SQLiteDatabase db = databaseCreateHelper.getWritableDatabase();

        String[] projection = {
                DishDBHelper.COLUMN_ID
        };


        String selection = DishDBHelper.COLUMN_RESTAURANT + " = ?";
        String restaurantString = "" + restaurant;
        String[] selectionArgs = {restaurantString};

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

        //List<Integer> idDishesOfCategoryList = new ArrayList<>();

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(DishDBHelper.COLUMN_ID));
                idDishesOfCategoryList.add(id);
                //Log.i("MyLog",category + "\n");

            }while (cursor.moveToNext());
        }

        //idDishesOfCategoryArray = ArrayUtils.toPrimitive(idDishesList.toArray(new Integer[idDishesList.size()]));

        cursor.close();
        db.close();

    }

    private void getIdFavoriteDishes() {
        DatabaseCreateHelper databaseCreateHelper = new DatabaseCreateHelper(this);
        SQLiteDatabase db = databaseCreateHelper.getWritableDatabase();

        String[] projection = {
                FavoriteDishesDBHelper.COLUMN_DISH_1,
                FavoriteDishesDBHelper.COLUMN_DISH_2,
                FavoriteDishesDBHelper.COLUMN_DISH_3,
                FavoriteDishesDBHelper.COLUMN_DISH_4,
                FavoriteDishesDBHelper.COLUMN_DISH_5,
                FavoriteDishesDBHelper.COLUMN_DISH_6,
                FavoriteDishesDBHelper.COLUMN_DISH_7,
                FavoriteDishesDBHelper.COLUMN_DISH_8,
                FavoriteDishesDBHelper.COLUMN_DISH_9,
                FavoriteDishesDBHelper.COLUMN_DISH_10,
                FavoriteDishesDBHelper.COLUMN_DISH_11,
                FavoriteDishesDBHelper.COLUMN_DISH_12,
                FavoriteDishesDBHelper.COLUMN_DISH_13,
                FavoriteDishesDBHelper.COLUMN_DISH_14,
                FavoriteDishesDBHelper.COLUMN_DISH_15,
                FavoriteDishesDBHelper.COLUMN_DISH_16,
                FavoriteDishesDBHelper.COLUMN_DISH_17,
                FavoriteDishesDBHelper.COLUMN_DISH_18,
                FavoriteDishesDBHelper.COLUMN_DISH_19,
                FavoriteDishesDBHelper.COLUMN_DISH_20
        };


        String selection = FavoriteDishesDBHelper.COLUMN_ID + " = ?";
        String id = "" + idForFavoriteDishesTable;
        String[] selectionArgs = {id};

        Cursor cursor = db.query(
                true,
                FavoriteDishesDBHelper.TABLE,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null,
                null
        );

        if (cursor.moveToFirst()) {
            idFavoriteDishesArray[0] = cursor.getInt(cursor.getColumnIndexOrThrow(FavoriteDishesDBHelper.COLUMN_DISH_1));
            idFavoriteDishesArray[1] = cursor.getInt(cursor.getColumnIndexOrThrow(FavoriteDishesDBHelper.COLUMN_DISH_2));
            idFavoriteDishesArray[2] = cursor.getInt(cursor.getColumnIndexOrThrow(FavoriteDishesDBHelper.COLUMN_DISH_3));
            idFavoriteDishesArray[3] = cursor.getInt(cursor.getColumnIndexOrThrow(FavoriteDishesDBHelper.COLUMN_DISH_4));
            idFavoriteDishesArray[4] = cursor.getInt(cursor.getColumnIndexOrThrow(FavoriteDishesDBHelper.COLUMN_DISH_5));
            idFavoriteDishesArray[5] = cursor.getInt(cursor.getColumnIndexOrThrow(FavoriteDishesDBHelper.COLUMN_DISH_6));
            idFavoriteDishesArray[6] = cursor.getInt(cursor.getColumnIndexOrThrow(FavoriteDishesDBHelper.COLUMN_DISH_7));
            idFavoriteDishesArray[7] = cursor.getInt(cursor.getColumnIndexOrThrow(FavoriteDishesDBHelper.COLUMN_DISH_8));
            idFavoriteDishesArray[8] = cursor.getInt(cursor.getColumnIndexOrThrow(FavoriteDishesDBHelper.COLUMN_DISH_9));
            idFavoriteDishesArray[9] = cursor.getInt(cursor.getColumnIndexOrThrow(FavoriteDishesDBHelper.COLUMN_DISH_10));
            idFavoriteDishesArray[10] = cursor.getInt(cursor.getColumnIndexOrThrow(FavoriteDishesDBHelper.COLUMN_DISH_11));
            idFavoriteDishesArray[11] = cursor.getInt(cursor.getColumnIndexOrThrow(FavoriteDishesDBHelper.COLUMN_DISH_12));
            idFavoriteDishesArray[12] = cursor.getInt(cursor.getColumnIndexOrThrow(FavoriteDishesDBHelper.COLUMN_DISH_13));
            idFavoriteDishesArray[13] = cursor.getInt(cursor.getColumnIndexOrThrow(FavoriteDishesDBHelper.COLUMN_DISH_14));
            idFavoriteDishesArray[14] = cursor.getInt(cursor.getColumnIndexOrThrow(FavoriteDishesDBHelper.COLUMN_DISH_15));
            idFavoriteDishesArray[15] = cursor.getInt(cursor.getColumnIndexOrThrow(FavoriteDishesDBHelper.COLUMN_DISH_16));
            idFavoriteDishesArray[16] = cursor.getInt(cursor.getColumnIndexOrThrow(FavoriteDishesDBHelper.COLUMN_DISH_17));
            idFavoriteDishesArray[17] = cursor.getInt(cursor.getColumnIndexOrThrow(FavoriteDishesDBHelper.COLUMN_DISH_18));
            idFavoriteDishesArray[18] = cursor.getInt(cursor.getColumnIndexOrThrow(FavoriteDishesDBHelper.COLUMN_DISH_19));
            idFavoriteDishesArray[19] = cursor.getInt(cursor.getColumnIndexOrThrow(FavoriteDishesDBHelper.COLUMN_DISH_20));

        }

        cursor.close();
        db.close();
    }

    //получение любимого блюда и добавление в список
    private void getFavoriteDishAndSetToList(int id) {
        DatabaseCreateHelper databaseCreateHelper = new DatabaseCreateHelper(this);
        SQLiteDatabase db = databaseCreateHelper.getWritableDatabase();

        String[] projection = {
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
                DishDBHelper.COLUMN_CATEGORY
        };


        String selection = DishDBHelper.COLUMN_ID + " = ?";
        String idString = "" + id;
        String[] selectionArgs = {idString};

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

        //List<Integer> idDishesOfCategoryList = new ArrayList<>();

        int c1, c2, c3, c4, c5, c6, c7, c8, c9 ,c10 ,c11, c12, c13, c14, c15, c16;

        if (cursor.moveToFirst()) {
            String dishName = cursor.getString(cursor.getColumnIndexOrThrow(DishDBHelper.COLUMN_DESCRIPTION));
            BigDecimal countCalories = new BigDecimal(cursor.getFloat(cursor.getColumnIndexOrThrow(DishDBHelper.COLUMN_CALORIC))).setScale(2, BigDecimal.ROUND_DOWN);
            BigDecimal countProteins = new BigDecimal(cursor.getFloat(cursor.getColumnIndexOrThrow(DishDBHelper.COLUMN_PROTEINS))).setScale(2, BigDecimal.ROUND_DOWN);
            BigDecimal countFats = new BigDecimal(cursor.getFloat(cursor.getColumnIndexOrThrow(DishDBHelper.COLUMN_FATS))).setScale(2, BigDecimal.ROUND_DOWN);
            BigDecimal countCarbohydrates = new BigDecimal(cursor.getFloat(cursor.getColumnIndexOrThrow(DishDBHelper.COLUMN_CARBS))).setScale(2, BigDecimal.ROUND_DOWN);
            BigDecimal countXE = new BigDecimal(cursor.getFloat(cursor.getColumnIndexOrThrow(DishDBHelper.COLUMN_XE))).setScale(2, BigDecimal.ROUND_DOWN);

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

            int[] compatibilityArray = new int[] {c1, c2, c3, c4, c5, c6, c7, c8, c9 ,c10 ,c11, c12, c13, c14, c15, c16};

            String category = cursor.getString(cursor.getColumnIndexOrThrow(DishDBHelper.COLUMN_CATEGORY));

            if(!isExistDishInList(dishName)) {
                dishOrderList.add(new DishOrderItem(id, dishName,
                        new BigDecimal(1).setScale(2, BigDecimal.ROUND_DOWN),
                        countCalories, countProteins, countFats, countCarbohydrates, countXE, compatibilityArray, category));
                countChosenDishes++;
            }
        }

        //idDishesOfCategoryArray = ArrayUtils.toPrimitive(idDishesList.toArray(new Integer[idDishesList.size()]));

        cursor.close();
        db.close();
    }

    private boolean isExistDishInList(String dishName) {
        boolean result = false;

        for(int i = 0; i < dishOrderList.size(); i++) {
            if((dishOrderList.get(i).getDishName()).equals(dishName)) {
                result = true;
                return result;
            }
        }
        return result;
    }


}

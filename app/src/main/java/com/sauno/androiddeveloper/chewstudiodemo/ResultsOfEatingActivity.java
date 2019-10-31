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

import com.sauno.androiddeveloper.chewstudiodemo.adapter.ResultsOfEatingAdapter;
import com.sauno.androiddeveloper.chewstudiodemo.bluetooth.BluetoothLeService;
import com.sauno.androiddeveloper.chewstudiodemo.bluetooth.LeLister;
import com.sauno.androiddeveloper.chewstudiodemo.data.DatabaseCreateHelper;
import com.sauno.androiddeveloper.chewstudiodemo.data.EatenDishDBHelper;
import com.sauno.androiddeveloper.chewstudiodemo.data.FavoriteDishesDBHelper;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Calendar;

public class ResultsOfEatingActivity extends AppCompatActivity {
    SearchDevicesFragment searchDevicesFragment;
    Menu mainMenu;

    public int[] idFavoriteDishes;
    int idForFavoriteDishesTable;

    public ArrayList<Integer> idesArrayList;

    public ArrayList<Integer> percentArrayList;

    private ArrayList<String> quantityArrayList = new ArrayList<>();

    SharedPreferences mSharedPreferences;

    ArrayList<Integer> caloriesArrayList;
    ArrayList<Integer> proteinsArrayList;
    ArrayList<Integer> fatsArrayList;
    ArrayList<Integer> carbsArrayList;
    ArrayList<Integer> xeArrayList;

    private LinearLayout containerCPFCXLinearLayout;

    TextView countCaloriesTextView;
    TextView countProteinsTextView;
    TextView countFatsTextView;
    TextView countCarbsTextView;
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


    boolean isExistUserCPFC;

    int currentUserPartOfCalories;
    int currentUserPartOfProteins;
    int currentUserPartOfFats;
    int currentUserPartOfCarbohydrates;
    int currentUserPartOfXE;

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

            mLeService.weakResultsOfEatingActivity = new WeakReference<>(ResultsOfEatingActivity.this);       // Создать слабую ссылку для MainActivity

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
                Toast.makeText(ResultsOfEatingActivity.this, "BLE not supported on this device",               // Печатаем Toast-сообщение 'BLE not supported on this device'
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
        setContentView(R.layout.activity_results_of_eating);

        mSharedPreferences = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);

        idForFavoriteDishesTable = mSharedPreferences.getInt("idForFavoriteDishes", -1);

        bindService(new Intent(this, BluetoothLeService.class),                                     // Привязаться к сервису BluetoothLeService
                mLeConnection, Context.BIND_AUTO_CREATE);

        setupActionBar();

        Intent intent = getIntent();

        idesArrayList = intent.getIntegerArrayListExtra("idesArrayList");

        ArrayList<String> dishNameArrayList = intent.getStringArrayListExtra("dishNameArrayList");
        percentArrayList = intent.getIntegerArrayListExtra("percentArrayList");
        quantityArrayList = intent.getStringArrayListExtra("quantityArrayList");

        caloriesArrayList = intent.getIntegerArrayListExtra("caloriesArrayList");
        proteinsArrayList = intent.getIntegerArrayListExtra("proteinsArrayList");
        fatsArrayList = intent.getIntegerArrayListExtra("fatsArrayList");
        carbsArrayList = intent.getIntegerArrayListExtra("carbsArrayList");
        xeArrayList = intent.getIntegerArrayListExtra("xeArrayList");

        /*intent.putStringArrayListExtra("dishNameArrayList", dishNameArrayList);
        intent.putIntegerArrayListExtra("caloriesArrayList", caloriesArrayList);
        intent.putIntegerArrayListExtra("proteinsArrayList", proteinsArrayList);
        intent.putIntegerArrayListExtra("fatsArrayList", fatsArrayList);
        intent.putIntegerArrayListExtra("carbsArrayList", carbsArrayList);
        intent.putIntegerArrayListExtra("xeArrayList", xeArrayList);
        intent.putIntegerArrayListExtra("percentArrayList", percentArrayList);*/

        RecyclerView mRecyclerView = findViewById(R.id.recyclerView);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        RecyclerView.Adapter mAdapter = new ResultsOfEatingAdapter(this, dishNameArrayList);
        mRecyclerView.setAdapter(mAdapter);

        containerCPFCXLinearLayout = findViewById(R.id.containerCPFCXLinearLayout);

        countCaloriesTextView = findViewById(R.id.countCaloriesTextView);
        countProteinsTextView = findViewById(R.id.countProteinsTextView);
        countFatsTextView = findViewById(R.id.countFatsTextView);
        countCarbsTextView = findViewById(R.id.countCarbohydratesTextView);
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

        int sumOrderCaloriesInt = getSum(caloriesArrayList, percentArrayList);
        int sumOrderProteinsInt = getSum(proteinsArrayList, percentArrayList);
        int sumOrderFatsInt = getSum(fatsArrayList, percentArrayList);
        int sumOrderCarbsInt = getSum(carbsArrayList, percentArrayList);
        int sumOrderXEInt = getSum(xeArrayList, percentArrayList);

        countCaloriesTextView.setText("" + getSum(caloriesArrayList, percentArrayList));
        countProteinsTextView.setText("" + getSum(proteinsArrayList, percentArrayList));
        countFatsTextView.setText("" + getSum(fatsArrayList, percentArrayList));
        countCarbsTextView.setText("" + getSum(carbsArrayList, percentArrayList));
        countXETextView.setText("" + getSum(xeArrayList, percentArrayList));

        checkIfExistUserCPFC();

        if(isExistUserCPFC) {
            getCurrentUserCPFC();
            setAboveLimitColorForCPFC(sumOrderCaloriesInt, sumOrderProteinsInt, sumOrderFatsInt, sumOrderCarbsInt, sumOrderXEInt);
        }

        Button allRightButton = findViewById(R.id.allRightButton);
        allRightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setStatisticData();

                ChoiceOfDishesActivity.dishOrderList = null;

                Intent intent = new Intent(view.getContext(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setTitle("Итоги приёма пищи");
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
            intent.putIntegerArrayListExtra("percentArrayList", percentArrayList);
            setResult(RESULT_OK, intent);
            finish();
            return true;
        }

        if(id == R.id.action_device) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            SearchDevicesFragment searchFragment = new SearchDevicesFragment();
            searchDevicesFragment = searchFragment;
            fragmentTransaction.add(R.id.resultOfEatingActivity, searchFragment);
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
        editor.putString("currentActivityForService", "ResultsOfEatingActivity");

        editor.apply();

        if(mainMenu != null) {
            if (mSharedPreferences.getBoolean("isConnected", false))                                                                        // Если есть привязка к BLE-сервису, то
            {
                mainMenu.findItem(R.id.action_device).setIcon(R.drawable.chew_logo);
            } else {
                mainMenu.findItem(R.id.action_device).setIcon(R.drawable.black_logo);
            }
        }

        getFavoriteDishes();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putIntegerArrayListExtra("percentArrayList", percentArrayList);
        setResult(RESULT_OK, intent);

        super.onBackPressed();
        }

    private int getSum(ArrayList<Integer>arrayList, ArrayList<Integer> percentArrayList) {
        int sum = 0;

        for(int n = 0; n < arrayList.size(); n++) {
            sum += (int)(arrayList.get(n) * percentArrayList.get(n) * 0.01);
        }

        return sum;
    }

    public void setSumCPFCX(ArrayList<Integer> percentArrayList) {
        int sumOrderCaloriesInt = getSum(caloriesArrayList, percentArrayList);
        int sumOrderProteinsInt = getSum(proteinsArrayList, percentArrayList);
        int sumOrderFatsInt = getSum(fatsArrayList, percentArrayList);
        int sumOrderCarbsInt = getSum(carbsArrayList, percentArrayList);
        int sumOrderXEInt = getSum(xeArrayList, percentArrayList);

        countCaloriesTextView.setText("" + getSum(caloriesArrayList, percentArrayList));
        countProteinsTextView.setText("" + getSum(proteinsArrayList, percentArrayList));
        countFatsTextView.setText("" + getSum(fatsArrayList, percentArrayList));
        countCarbsTextView.setText("" + getSum(carbsArrayList, percentArrayList));
        countXETextView.setText("" + getSum(xeArrayList, percentArrayList));

        if(isExistUserCPFC) {
            getCurrentUserCPFC();
            setAboveLimitColorForCPFC(sumOrderCaloriesInt, sumOrderProteinsInt, sumOrderFatsInt, sumOrderCarbsInt, sumOrderXEInt);
        }
    }

    private void setStatisticData() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR);
        int am = calendar.get(Calendar.AM_PM);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH) + 1;

        if(am == 1) {
            hour = hour + 12;
        }

        int meal = FoodFragment.currentMealInt;

        if(meal == 1 || meal == 4 || meal == 8 || meal == 9 || meal == 13 || meal == 14) {
            hour = 10;
        }

        if(meal == 2 || meal == 5 || meal == 10 || meal == 11 || meal == 15 || meal == 16) {
            hour = 12;
        }

        if(meal == 3 || meal == 6 || meal == 7 || meal == 12 || meal == 17 || meal == 18) {
            hour = 18;
        }

        Log.d("MyLog", "hour - " + hour + "  day - " + day + "  month - " + month);


        DatabaseCreateHelper databaseCreateHelper = new DatabaseCreateHelper(getApplicationContext());
        SQLiteDatabase db = databaseCreateHelper.getWritableDatabase();

        for(int n = 0; n < idesArrayList.size(); n++) {
            ContentValues newValues = new ContentValues();
            newValues.put(EatenDishDBHelper.COLUMN_ID_DISH, idesArrayList.get(n));
            newValues.put(EatenDishDBHelper.COLUMN_DAY, day);
            newValues.put(EatenDishDBHelper.COLUMN_MONTH, month);
            newValues.put(EatenDishDBHelper.COLUMN_HOUR, hour);
            newValues.put(EatenDishDBHelper.COLUMN_QUANTITY, quantityArrayList.get(n));

            db.insert(EatenDishDBHelper.TABLE, null, newValues);

            //Log.d("MyLogResult", "Quantity - " + );
        }


        db.close();

    }

    private void checkIfExistUserCPFC() {
        SharedPreferences mSharedPreferences = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        int userDayCalories = mSharedPreferences.getInt(UserProfileActivity.KEY_COUNT_CALORIES, 0);

        if(userDayCalories == 0) {
            Toast.makeText(this, "Для подсчёта рекомендуемых Вам ежедневно калорий, белков, жиров," +
                    " углеводов введите: пол, возраст, рост, вес, образ жизни", Toast.LENGTH_SHORT).show();
            isExistUserCPFC = false;
        } else {
            isExistUserCPFC =true;
        }

    }

    private void setAboveLimitColorForCPFC(int sumCaloriesInt, int sumProteinsInt, int sumFatsInt, int sumCarbsInt, int sumXEInt) {
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
            countCarbsTextView.setText("" + sumCarbsInt + "г");
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

    private void getCurrentUserCPFC() {
        SharedPreferences mSharedPreferences = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        currentUserPartOfCalories = mSharedPreferences.getInt("currentCalories", 0);
        currentUserPartOfProteins = mSharedPreferences.getInt("currentProteins", 0);
        currentUserPartOfFats = mSharedPreferences.getInt("currentFats", 0);
        currentUserPartOfCarbohydrates = mSharedPreferences.getInt("currentCarbs", 0);
        currentUserPartOfXE = mSharedPreferences.getInt("currentXE", 0);
    }

    private void getFavoriteDishes() {
        DatabaseCreateHelper databaseCreateHelper = new DatabaseCreateHelper(getApplicationContext());
        SQLiteDatabase db;
        try {
            db = databaseCreateHelper.getWritableDatabase();
        }
        catch (SQLiteException ex){
            db = databaseCreateHelper.getReadableDatabase();
        }

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
        String idString = "" + idForFavoriteDishesTable;
        String[] selectionArgs = {idString};

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

        idFavoriteDishes = new int[20];

        if (cursor.moveToFirst()) {
            for(int n = 0; n < 20; n++) {
                int dish = cursor.getInt(n);
                if(dish != 0) {
                    idFavoriteDishes[n] = dish;
                } else {
                    n = 20;
                }
            }
        }

        cursor.close();
        db.close();
    }
}

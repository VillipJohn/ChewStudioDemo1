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
import android.os.Parcelable;
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
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;

import com.sauno.androiddeveloper.chewstudiodemo.adapter.ListDishesAdapter;
import com.sauno.androiddeveloper.chewstudiodemo.bluetooth.BluetoothLeService;
import com.sauno.androiddeveloper.chewstudiodemo.bluetooth.LeLister;
import com.sauno.androiddeveloper.chewstudiodemo.data.DatabaseCreateHelper;
import com.sauno.androiddeveloper.chewstudiodemo.data.DishDBHelper;
import com.sauno.androiddeveloper.chewstudiodemo.data.IngredientsDBHelper;
import com.sauno.androiddeveloper.chewstudiodemo.model.DishOrderItem;

import java.lang.ref.WeakReference;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ListDishesActivity extends AppCompatActivity {
    private CheckBox diabetesCheckBox;
    private CheckBox compatibilityCheckBox;
    private CheckBox lentenCheckBox;
    private CheckBox vegetarianCheckBox;
    private CheckBox cpfcxCheckBox;

    public static boolean isCheckedDiabetesCheckBox;
    public static boolean isCheckedCompatibilityCheckBox;
    public static boolean isCheckedLentenCheckBox;
    public static boolean isCheckedVegetarianCheckBox;
    public static boolean isCheckedCpfcxCheckBox;


    SearchDevicesFragment searchDevicesFragment;
    Menu mainMenu;

    SharedPreferences mSharedPreferences;

    private RecyclerView mRecyclerView;
    //айдишники, которые используются в адапторе для открытия AboutDish
    private List<Integer> idDishesList = new ArrayList<>();

    private List<String> dishNamesList = new ArrayList<>();

    //для отображения фона, оценка совместимости
    private List<Integer> compatibilityEvaluationList = new ArrayList<>();

    // список индентификаторов ингредиентов для каждого блюда
    private List<int[]> ingredientArrayList = new ArrayList<>();

    // список постное или не постное блюдо, 1 - постное, 0 - не постное
    private List<Integer> lentenArrayList = new ArrayList<>();

    // список вегетарианское или не вегетарианское блюдо, 1 - вегетарианское, 0 - не вегетарианское
    private List<Integer> vegetarianArrayList = new ArrayList<>();

    // список постное или не постное блюдо, 1 - постное, 0 - не постное
    public List<Integer> checkedLentenArrayList = new ArrayList<>();

    // список вегетарианское или не вегетарианское блюдо, 1 - вегетарианское, 0 - не вегетарианское
    public List<Integer> checkedVegetarianArrayList = new ArrayList<>();

    // список на сколько диабетическое
    private List<String> diabetArrayList = new ArrayList<>();


    private List<int[]> compatibilityArraysList = new ArrayList<>();

    private List<BigDecimal> dishOrderQuantityList = new ArrayList<>();

    //для отображения соответствующих картинок
    private List<String> globalCategoryList = new ArrayList<>();

    public static String chosenCategory;

    public List<DishOrderItem> dishOrderList = new ArrayList<>();

    public BigDecimal sumOrderCalories;
    public BigDecimal sumOrderProteins;
    public BigDecimal sumOrderFats;
    public BigDecimal sumOrderCarbs;
    public BigDecimal sumOrderXE;

    public int countChosenDishes;

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

            mLeService.weakListDishesActivity = new WeakReference<>(ListDishesActivity.this);       // Создать слабую ссылку для MainActivity

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
                Toast.makeText(ListDishesActivity.this, "BLE not supported on this device",               // Печатаем Toast-сообщение 'BLE not supported on this device'
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

            SharedPreferences mSharedPreferences = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);

            SharedPreferences.Editor editor = mSharedPreferences.edit();
            editor.putBoolean("isConnected", false);

            editor.apply();
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
        setContentView(R.layout.activity_list_dishes);

        diabetesCheckBox = findViewById(R.id.diabetesCheckBox);
        compatibilityCheckBox = findViewById(R.id.compatibilityCheckBox);
        lentenCheckBox = findViewById(R.id.lentenCheckBox);
        vegetarianCheckBox = findViewById(R.id.vegetarianCheckBox);
        cpfcxCheckBox = findViewById(R.id.cpfcxCheckBox);

        checkPreferences();

        mSharedPreferences = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);

        bindService(new Intent(this, BluetoothLeService.class),                                     // Привязаться к сервису BluetoothLeService
                mLeConnection, Context.BIND_AUTO_CREATE);

        chosenCategory = getIntent().getStringExtra("category");

        getDishesFromDB();

        setupActionBar();

        mRecyclerView = findViewById(R.id.listDishesRecyclerView);

        setAdapter(null);

        diabetesCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setAdapter(null);
            }
        });

        compatibilityCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setAdapter(null);
            }
        });

        lentenCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setAdapter(null);
            }
        });

        vegetarianCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setAdapter(null);
            }
        });

        cpfcxCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setAdapter(null);
            }
        });
    }

    private void checkPreferences() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        String compatibilityType = sp.getString(UserPreferencesActivity.KEY_USER_COMPATIBILITY_TYPE_OF_FOOD_PREF, "0");

        String vegetarian = sp.getString(UserPreferencesActivity.KEY_USER_TYPE_OF_FOOD_VEGETARIAN_PREF, "0");
        String lenten = sp.getString(UserPreferencesActivity.KEY_USER_TYPE_OF_FOOD_LENTEN_PREF, "0");
        String characteristicOne = sp.getString(UserPreferencesActivity.KEY_USER_CHARACTERISTIC_ONE_PREF, "0");
        String characteristicTwo = sp.getString(UserPreferencesActivity.KEY_USER_CHARACTERISTIC_TWO_PREF, "0");

        SharedPreferences mSharedPreferences = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        int userDayCalories = mSharedPreferences.getInt(UserProfileActivity.KEY_COUNT_CALORIES, 0);

        /*Log.d("MeLogAddIngredients", "compatibilityType  -  " + compatibilityType);
        Log.d("MeLogAddIngredients", "vegetarian  -  " + vegetarian);
        Log.d("MeLogAddIngredients", "lenten  -  " + lenten);
        Log.d("MeLogAddIngredients", "characteristicOne  -  " + characteristicOne);
        Log.d("MeLogAddIngredients", "characteristicTwo  -  " + characteristicTwo);*/

        /*Log.d("MyLogAddIngredients", "characteristicOne  -  " + characteristicOne);
        Log.d("MyLogAddIngredients", "characteristicTwo  -  " + characteristicTwo);*/


        if(compatibilityType.equals("0")) {
            compatibilityCheckBox.setVisibility(View.GONE);
            isCheckedCompatibilityCheckBox = false;
        }

        if(vegetarian.equals("0")) {
            vegetarianCheckBox.setVisibility(View.GONE);
            isCheckedVegetarianCheckBox = false;
        }

        if(lenten.equals("0")) {
            lentenCheckBox.setVisibility(View.GONE);
            isCheckedLentenCheckBox = false;
        }

        if(characteristicOne.equals("0") && characteristicTwo.equals("0") ) {
            diabetesCheckBox.setVisibility(View.GONE);
            isCheckedDiabetesCheckBox = false;
        }

        if(userDayCalories == 0) {
            cpfcxCheckBox.setVisibility(View.GONE);
            isCheckedCpfcxCheckBox = false;
        }

        compatibilityCheckBox.setChecked(isCheckedCompatibilityCheckBox);
        vegetarianCheckBox.setChecked(isCheckedVegetarianCheckBox);
        lentenCheckBox.setChecked(isCheckedLentenCheckBox);
        diabetesCheckBox.setChecked(isCheckedDiabetesCheckBox);
        cpfcxCheckBox.setChecked(isCheckedCpfcxCheckBox);
    }


    public void setAdapter(Parcelable recylerViewState) {
        dishOrderQuantityList = getDishOrderQuantity();

        for(int i = 0; i < dishNamesList.size(); i++) {
            compatibilityEvaluationList.add(checkCompatibility(i));
        }


        isCheckedDiabetesCheckBox = diabetesCheckBox.isChecked();
        isCheckedCompatibilityCheckBox = compatibilityCheckBox.isChecked();
        isCheckedLentenCheckBox = lentenCheckBox.isChecked();
        isCheckedVegetarianCheckBox = vegetarianCheckBox.isChecked();
        isCheckedCpfcxCheckBox = cpfcxCheckBox.isChecked();

        //checkedIngredientList = ingredientList;

        List<Integer> checkedIdDishesList = new ArrayList<>();
        List<String> checkedDishNamesList = new ArrayList<>();
        List<BigDecimal> checkedDishOrderQuantityList = new ArrayList<>();
        List<int[]> checkedCompatibilityArraysList = new ArrayList<>();
        List<String> checkedGlobalCategoryList = new ArrayList<>();
        List<int[]> checkedIngredientArrayList = new ArrayList<>();

        for(int n = 0; n < dishNamesList.size(); n++) {
            if(isCheckedDiabetesCheckBox) {
                if(diabetArrayList.get(n).equals("нельзя")) {
                    continue;
                }
            }
            if(isCheckedCompatibilityCheckBox) {
                if(compatibilityEvaluationList.get(n) == 2) {
                    continue;
                }
            }
            if(isCheckedLentenCheckBox) {
                if(lentenArrayList.get(n) == 0) {
                    continue;
                }
            }
            if(isCheckedVegetarianCheckBox) {
                if(vegetarianArrayList.get(n) == 0) {
                    continue;
                }
            }
            if(isCheckedCpfcxCheckBox) {
                String dishName = dishNamesList.get(n);
                if(checkIfAboveCPFCX(dishName)) {
                    continue;
                }
            }

            checkedIdDishesList.add(idDishesList.get(n));
            checkedDishNamesList.add(dishNamesList.get(n));
            checkedDishOrderQuantityList.add(dishOrderQuantityList.get(n));
            checkedCompatibilityArraysList.add(compatibilityArraysList.get(n));
            checkedGlobalCategoryList.add(globalCategoryList.get(n));
            checkedIngredientArrayList.add(ingredientArrayList.get(n));

            checkedLentenArrayList.add(lentenArrayList.get(n));
            checkedVegetarianArrayList.add(vegetarianArrayList.get(n));

           /* Log.d("MyLogListDishActivity", "id - " + idDishesList.get(n));
            Log.d("MyLogListDishActivity", "dishName - " + dishNamesList.get(n));
            Log.d("MyLogListDishActivity", "dishOrderQuantity - " + dishOrderQuantityList.get(n));
            //Log.d("MyLogListDishActivity", "id - " + idDishesList.get(n));
            Log.d("MyLogListDishActivity", "globalCategory - " + globalCategoryList.get(n));*/
        }

        if(mRecyclerView != null) {
            mRecyclerView.removeAllViews();
        }

        /*for(int i = 0; i < dishOrderQuantityList.size(); i++) {

            Log.d("MyLogListDishActivity", "dishQuantity[" + i + "]" + " = " + dishOrderQuantityList.get(i).intValue());
        }
*/
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        RecyclerView.Adapter mAdapter = new ListDishesAdapter(this,
                checkedIdDishesList,
                checkedDishNamesList,
                checkedDishOrderQuantityList,
                checkedCompatibilityArraysList,
                checkedGlobalCategoryList,
                checkedIngredientArrayList,
                mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        if(recylerViewState != null) {
            mRecyclerView.getLayoutManager().onRestoreInstanceState(recylerViewState);
        }
    }

    //проверка совместимости и установка оценки совместимости
    private int checkCompatibility(int position) {
        int result = 4;
        if(ChoiceOfDishesActivity.dishOrderList.size() > 0) {
            for(int i = 0; i < ChoiceOfDishesActivity.dishOrderList.size(); i++) {
                if(!dishNamesList.get(position).equals(ChoiceOfDishesActivity.dishOrderList.get(i).getDishName())) {
                    int x = getCompatibilityEvaluation(compatibilityArraysList.get(position),
                            ChoiceOfDishesActivity.dishOrderList.get(i).getCompatibilityArray());
                    //Log.i("MyLogListDishes", "checkCompatibility x = " + x + "\n");
                    if(x == 3) {
                        result = 3;
                    }
                    if(x == 2) {
                        return 2;
                    }
                }
            }
        }
        return result;
    }

    //получение оценки совместимости
    private int getCompatibilityEvaluation(int[] a, int[] b) {
        int[][] compatibilityOfElementsArray = {
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

        int result = 4;
        for(int i = 0; i < 16; i++) {
            if(a[i] != 0) {
                for(int n = 0; n < 16; n++) {
                    if(b[n] != 0) {
                        int x = compatibilityOfElementsArray[i][n];
                        //Log.i("MyLogListDishes", "x = " + x + "\n");
                        if(x == 3) {
                            result = 3;
                        }
                        if(x == 2) {
                            return 2;
                        }
                    }
                }
            }
        }

        return result;
    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setTitle(chosenCategory);
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
            fragmentTransaction.add(R.id.listDishesActivity, searchFragment);
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
        editor.putString("currentActivityForService", "ListDishesActivity");

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
        DatabaseCreateHelper databaseCreateHelper = new DatabaseCreateHelper(getApplicationContext());
        SQLiteDatabase db;
        try {
            db = databaseCreateHelper.getWritableDatabase();
        }
        catch (SQLiteException ex){
            db = databaseCreateHelper.getReadableDatabase();
        }

        /*String[] projection = {
                DishDBHelper.COLUMN_ID,
                DishDBHelper.COLUMN_DESCRIPTION,
                DishDBHelper.COLUMN_CATEGORY,
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
        };*/

        String[] projection = {
                DishDBHelper.COLUMN_ID,
                DishDBHelper.COLUMN_DESCRIPTION,
                DishDBHelper.COLUMN_CATEGORY,
                DishDBHelper.COLUMN_INGREDS_1,
                DishDBHelper.COLUMN_INGREDS_2,
                DishDBHelper.COLUMN_INGREDS_3,
                DishDBHelper.COLUMN_INGREDS_4,
                DishDBHelper.COLUMN_INGREDS_5,
                DishDBHelper.COLUMN_INGREDS_6,
                DishDBHelper.COLUMN_INGREDS_7,
                DishDBHelper.COLUMN_INGREDS_8,
                DishDBHelper.COLUMN_INGREDS_9,
                DishDBHelper.COLUMN_INGREDS_10,
                DishDBHelper.COLUMN_INGREDS_11,
                DishDBHelper.COLUMN_INGREDS_12,
                DishDBHelper.COLUMN_INGREDS_13,
                DishDBHelper.COLUMN_INGREDS_14,
                DishDBHelper.COLUMN_INGREDS_15,
                DishDBHelper.COLUMN_INGREDS_16,
                DishDBHelper.COLUMN_INGREDS_17,
                DishDBHelper.COLUMN_INGREDS_18,
                DishDBHelper.COLUMN_INGREDS_19,
                DishDBHelper.COLUMN_INGREDS_20,
                DishDBHelper.COLUMN_GLOBAL_CATEGORY
        };

        // Filter results WHERE "title" = 'My Title'
        String selection = DishDBHelper.COLUMN_CATEGORY + " = ?";
        String[] selectionArgs = {chosenCategory};


        Cursor cursor = db.query(
                DishDBHelper.TABLE,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

//        int c1, c2, c3, c4, c5, c6, c7, c8, c9 ,c10 ,c11, c12, c13, c14, c15, c16;




        int i1, i2, i3, i4, i5, i6, i7, i8, i9 ,i10 ,i11, i12, i13, i14, i15, i16, i17, i18, i19, i20;

        if (cursor.moveToFirst()) {

            do {
                int idDish = cursor.getInt(cursor.getColumnIndexOrThrow(DishDBHelper.COLUMN_ID));
                idDishesList.add(idDish);

                String dishName = cursor.getString(cursor.getColumnIndexOrThrow(DishDBHelper.COLUMN_DESCRIPTION));
                dishNamesList.add(dishName);


                //BigDecimal dishOrderQuantity = new BigDecimal(0).setScale(2, BigDecimal.ROUND_DOWN);


                /*c1 = cursor.getInt(cursor.getColumnIndexOrThrow(DishDBHelper.COLUMN_COMPATIBILITY_1));
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
                c16 = cursor.getInt(cursor.getColumnIndexOrThrow(DishDBHelper.COLUMN_COMPATIBILITY_16));*/

                //compatibilityArraysList.add(new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0});

                i1 = cursor.getInt(cursor.getColumnIndexOrThrow(DishDBHelper.COLUMN_INGREDS_1));
                i2 = cursor.getInt(cursor.getColumnIndexOrThrow(DishDBHelper.COLUMN_INGREDS_2));
                i3 = cursor.getInt(cursor.getColumnIndexOrThrow(DishDBHelper.COLUMN_INGREDS_3));
                i4 = cursor.getInt(cursor.getColumnIndexOrThrow(DishDBHelper.COLUMN_INGREDS_4));
                i5 = cursor.getInt(cursor.getColumnIndexOrThrow(DishDBHelper.COLUMN_INGREDS_5));
                i6 = cursor.getInt(cursor.getColumnIndexOrThrow(DishDBHelper.COLUMN_INGREDS_6));
                i7 = cursor.getInt(cursor.getColumnIndexOrThrow(DishDBHelper.COLUMN_INGREDS_7));
                i8 = cursor.getInt(cursor.getColumnIndexOrThrow(DishDBHelper.COLUMN_INGREDS_8));
                i9 = cursor.getInt(cursor.getColumnIndexOrThrow(DishDBHelper.COLUMN_INGREDS_9));
                i10 = cursor.getInt(cursor.getColumnIndexOrThrow(DishDBHelper.COLUMN_INGREDS_10));
                i11 = cursor.getInt(cursor.getColumnIndexOrThrow(DishDBHelper.COLUMN_INGREDS_11));
                i12 = cursor.getInt(cursor.getColumnIndexOrThrow(DishDBHelper.COLUMN_INGREDS_12));
                i13 = cursor.getInt(cursor.getColumnIndexOrThrow(DishDBHelper.COLUMN_INGREDS_13));
                i14 = cursor.getInt(cursor.getColumnIndexOrThrow(DishDBHelper.COLUMN_INGREDS_14));
                i15 = cursor.getInt(cursor.getColumnIndexOrThrow(DishDBHelper.COLUMN_INGREDS_15));
                i16 = cursor.getInt(cursor.getColumnIndexOrThrow(DishDBHelper.COLUMN_INGREDS_16));
                i17 = cursor.getInt(cursor.getColumnIndexOrThrow(DishDBHelper.COLUMN_INGREDS_17));
                i18 = cursor.getInt(cursor.getColumnIndexOrThrow(DishDBHelper.COLUMN_INGREDS_18));
                i19 = cursor.getInt(cursor.getColumnIndexOrThrow(DishDBHelper.COLUMN_INGREDS_19));
                i20 = cursor.getInt(cursor.getColumnIndexOrThrow(DishDBHelper.COLUMN_INGREDS_20));

                int[] ingredientArray = {i1, i2, i3, i4, i5, i6, i7, i8, i9, i10, i11, i12, i13, i14, i15, i16, i17, i18, i19, i20};

                ingredientArrayList.add(ingredientArray);

                //int[] compatibilityIntArray = {c1, c2, c3, c4, c5, c6, c7, c8, c9 ,c10 ,c11, c12, c13, c14, c15, c16};

                //int compatibilityEvaluation = getCompatibilityEvaluation(compatibilityIntArray);
                //compatibilityEvaluationList.add(compatibilityEvaluation);

                //Log.i("MyLogListDishes", "DishName - " + dishName + "   compatibilityEvaluation - " + compatibilityEvaluation);

                String globalCategory = cursor.getString(cursor.getColumnIndexOrThrow(DishDBHelper.COLUMN_GLOBAL_CATEGORY));
                globalCategoryList.add(globalCategory);
            }while (cursor.moveToNext());

        }

//        dishNamesArray = dishNames.toArray(new String[dishNames.size()]);

        //dishOrderQuantity = new int[dishNamesArray.length];

        //dishOrderQuantity = getDataForDishOrderQuantity();


        db.close();
        cursor.close();

        getDataFromDB();
    }

    // получение данных по содержащимся ингредиентам совместимости, вегетарианства, пост, диабет
    private void getDataFromDB() {
        DatabaseCreateHelper databaseCreateHelper = new DatabaseCreateHelper(getApplicationContext());
        SQLiteDatabase db;
        try {
            db = databaseCreateHelper.getWritableDatabase();
        } catch (SQLiteException ex) {
            db = databaseCreateHelper.getReadableDatabase();
        }

        // цикл

        //int[] ingredientArray = new int[20];

        for(int n = 0; n < ingredientArrayList.size(); n++) {
            lentenArrayList.add(1);
            vegetarianArrayList.add(1);
            diabetArrayList.add("можно");
            compatibilityArraysList.add(new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0});

            int[] ingredientArray = ingredientArrayList.get(n);
            for(int ingredientID : ingredientArray){
                if(ingredientID != 0) {
                    //Log.d("MyLogListDishes","Ингредиент ID - " + ingredientID);

                    String[] projection = {
                            IngredientsDBHelper.COLUMN_VEGETARIAN,
                            IngredientsDBHelper.COLUMN_LENTEN,
                            IngredientsDBHelper.COLUMN_DIABETES,
                            IngredientsDBHelper.COLUMN_COMPATIBILITY_1,
                            IngredientsDBHelper.COLUMN_COMPATIBILITY_2,
                            IngredientsDBHelper.COLUMN_COMPATIBILITY_3,
                            IngredientsDBHelper.COLUMN_COMPATIBILITY_4,
                            IngredientsDBHelper.COLUMN_COMPATIBILITY_5,
                            IngredientsDBHelper.COLUMN_COMPATIBILITY_6,
                            IngredientsDBHelper.COLUMN_COMPATIBILITY_7,
                            IngredientsDBHelper.COLUMN_COMPATIBILITY_8,
                            IngredientsDBHelper.COLUMN_COMPATIBILITY_9,
                            IngredientsDBHelper.COLUMN_COMPATIBILITY_10,
                            IngredientsDBHelper.COLUMN_COMPATIBILITY_11,
                            IngredientsDBHelper.COLUMN_COMPATIBILITY_12,
                            IngredientsDBHelper.COLUMN_COMPATIBILITY_13,
                            IngredientsDBHelper.COLUMN_COMPATIBILITY_14,
                            IngredientsDBHelper.COLUMN_COMPATIBILITY_15,
                            IngredientsDBHelper.COLUMN_COMPATIBILITY_16
                    };

                    String selection = IngredientsDBHelper.COLUMN_ID + " = ?";
                    String[] selectionArgs = {"" + ingredientID};

                    Cursor cursor = db.query(
                            IngredientsDBHelper.TABLE,
                            projection,
                            selection,
                            selectionArgs,
                            null,
                            null,
                            null
                    );

                    int lenten = 1;
                    int vegetarian = 1;
                    String diabet = "можно";
                    int c1, c2, c3, c4, c5, c6, c7, c8, c9 ,c10 ,c11, c12, c13, c14, c15, c16;
                    int[] compatibilityIntArray = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};

                    if (cursor.moveToFirst()) {
                        do {
                            lenten = cursor.getInt(cursor.getColumnIndexOrThrow(IngredientsDBHelper.COLUMN_LENTEN));
                            vegetarian = cursor.getInt(cursor.getColumnIndexOrThrow(IngredientsDBHelper.COLUMN_VEGETARIAN));
                            diabet = cursor.getString(cursor.getColumnIndexOrThrow(IngredientsDBHelper.COLUMN_DIABETES));

                            compatibilityIntArray[0] = cursor.getInt(cursor.getColumnIndexOrThrow(IngredientsDBHelper.COLUMN_COMPATIBILITY_1));
                            compatibilityIntArray[1] = cursor.getInt(cursor.getColumnIndexOrThrow(IngredientsDBHelper.COLUMN_COMPATIBILITY_2));
                            compatibilityIntArray[2] = cursor.getInt(cursor.getColumnIndexOrThrow(IngredientsDBHelper.COLUMN_COMPATIBILITY_3));
                            compatibilityIntArray[3] = cursor.getInt(cursor.getColumnIndexOrThrow(IngredientsDBHelper.COLUMN_COMPATIBILITY_4));
                            compatibilityIntArray[4] = cursor.getInt(cursor.getColumnIndexOrThrow(IngredientsDBHelper.COLUMN_COMPATIBILITY_5));
                            compatibilityIntArray[5] = cursor.getInt(cursor.getColumnIndexOrThrow(IngredientsDBHelper.COLUMN_COMPATIBILITY_6));
                            compatibilityIntArray[6] = cursor.getInt(cursor.getColumnIndexOrThrow(IngredientsDBHelper.COLUMN_COMPATIBILITY_7));
                            compatibilityIntArray[7] = cursor.getInt(cursor.getColumnIndexOrThrow(IngredientsDBHelper.COLUMN_COMPATIBILITY_8));
                            compatibilityIntArray[8] = cursor.getInt(cursor.getColumnIndexOrThrow(IngredientsDBHelper.COLUMN_COMPATIBILITY_9));
                            compatibilityIntArray[9] = cursor.getInt(cursor.getColumnIndexOrThrow(IngredientsDBHelper.COLUMN_COMPATIBILITY_10));
                            compatibilityIntArray[10] = cursor.getInt(cursor.getColumnIndexOrThrow(IngredientsDBHelper.COLUMN_COMPATIBILITY_11));
                            compatibilityIntArray[11] = cursor.getInt(cursor.getColumnIndexOrThrow(IngredientsDBHelper.COLUMN_COMPATIBILITY_12));
                            compatibilityIntArray[12] = cursor.getInt(cursor.getColumnIndexOrThrow(IngredientsDBHelper.COLUMN_COMPATIBILITY_13));
                            compatibilityIntArray[13] = cursor.getInt(cursor.getColumnIndexOrThrow(IngredientsDBHelper.COLUMN_COMPATIBILITY_14));
                            compatibilityIntArray[14] = cursor.getInt(cursor.getColumnIndexOrThrow(IngredientsDBHelper.COLUMN_COMPATIBILITY_15));
                            compatibilityIntArray[15] = cursor.getInt(cursor.getColumnIndexOrThrow(IngredientsDBHelper.COLUMN_COMPATIBILITY_16));
                        } while (cursor.moveToNext());
                    }
                    cursor.close();

                    if(lentenArrayList.get(n) != 0) {
                        if(lenten == 0) {
                            lentenArrayList.set(n, 0);
                        }
                    }

                    if(vegetarianArrayList.get(n) != 0) {
                        if(vegetarian == 0) {
                            vegetarianArrayList.set(n, 0);
                        }
                    }

                    if(!diabetArrayList.get(n).equals("нельзя")) {
                        if(diabet != null && diabet.equals("нельзя")) {
                            diabetArrayList.set(n, "нельзя");
                        }
                    }

                    for(int i = 0; i < compatibilityIntArray.length; i++) {
                        if(compatibilityIntArray[i] == 1) {
                            int[] comp = compatibilityArraysList.get(n);
                            comp[i] = 1;
                            compatibilityArraysList.set(n, comp);
                        }
                    }
                }
            }

            //Log.d("MyLogListDishes"," - " + ingredientID);
        }

        db.close();
    }

   /* private int[] getDataForDishOrderQuantity() {
        int[] realDishOrderQuantity = dishOrderQuantity;

        for(int i = 0; i < dishNamesArray.length; i++) {
            boolean isQuantity = false;
            for(int n = 0; n < dishOrderList.size(); n++) {
                if((ChoiceOfDishesActivity.dishOrderList.get(n).getDishName()).equals(dishNamesArray[i])) {
                    realDishOrderQuantity[i] = ChoiceOfDishesActivity.dishOrderList.get(n).getQuantityDishes();
                    isQuantity = true;
                }
            }
            if(!isQuantity) {
                realDishOrderQuantity[i] = 0;
            }
        }

        return realDishOrderQuantity;
    }
*/
    @Override
    protected void onPause() {
        super.onPause();

        /*ChoiceOfDishesActivity.sumOrderCalories =  ChoiceOfDishesActivity.sumOrderCalories.add(sumOrderCalories);
        ChoiceOfDishesActivity.sumOrderProteins =  ChoiceOfDishesActivity.sumOrderProteins.add(sumOrderProteins);
        ChoiceOfDishesActivity.sumOrderFats =  ChoiceOfDishesActivity.sumOrderFats.add(sumOrderFats);
        ChoiceOfDishesActivity.sumOrderCarbs =  ChoiceOfDishesActivity.sumOrderCarbs.add(sumOrderCarbs);
        ChoiceOfDishesActivity.sumOrderXE =  ChoiceOfDishesActivity.sumOrderXE.add(sumOrderXE);

        ChoiceOfDishesActivity.countChosenDishes = ChoiceOfDishesActivity.countChosenDishes + countChosenDishes;

        if (dishOrderList.size() > 0) {
            ChoiceOfDishesActivity.dishOrderList.addAll(dishOrderList);
        }*/
    }


    private int getCompatibilityEvaluation(int[] compatibilityIntArray) {
        int result = 4;
        int x;

        for(int n = 0 ; n < 16 ; n++) {
            if(compatibilityIntArray[n] == 1) {
                for(int k = n + 1 ; k < 16 ; k++) {
                    if(compatibilityIntArray[k] == 1) {
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

    private List<BigDecimal> getDishOrderQuantity() {
        List<BigDecimal> dishOrderQuantityList = new ArrayList<>();
        for(int n = 0; n < dishNamesList.size(); n++) {
            boolean isExistDish = false;
            for(int i = 0; i < ChoiceOfDishesActivity.dishOrderList.size(); i++) {
                if((ChoiceOfDishesActivity.dishOrderList.get(i).getDishName()).equals(dishNamesList.get(n))) {
                    isExistDish = true;
                }
            }
            if(isExistDish) {
                dishOrderQuantityList.add(new BigDecimal(1).setScale(2, BigDecimal.ROUND_DOWN));
            } else {
                dishOrderQuantityList.add(new BigDecimal(0).setScale(2, BigDecimal.ROUND_DOWN));
            }
        }
        return dishOrderQuantityList;

    }

    /*public void restartRecycler() {
        mRecyclerView.removeAllViews();
        mAdapter = new BasketAdapter(this, dishOrderNames, dishOrderQuantity,
                percentageOfCaloriesArray, percentageOfProteinsArray, percentageOfFatsArray, percentageOfCarbsArray, percentageOfXEArray);
        mRecyclerView.setAdapter(mAdapter);

    }*/

    //получение данных с базы данных
    private boolean checkIfAboveCPFCX(String dishNameDB) {
        int countCalories = 0;
        int countProteins = 0;
        int countFats = 0;
        int countCarbohydrates = 0;
        int countXE = 0;

        DatabaseCreateHelper databaseCreateHelper = new DatabaseCreateHelper(this);

        SQLiteDatabase db;
        try {
            db = databaseCreateHelper.getWritableDatabase();
        }
        catch (SQLiteException ex){
            db = databaseCreateHelper.getReadableDatabase();
        }

        String[] projection = {
                DishDBHelper.COLUMN_CALORIC,
                DishDBHelper.COLUMN_PROTEINS,
                DishDBHelper.COLUMN_FATS,
                DishDBHelper.COLUMN_CARBS,
                DishDBHelper.COLUMN_XE
        };

        // Filter results WHERE "title" = 'My Title'
        String selection = DishDBHelper.COLUMN_DESCRIPTION + " = ?";
        String[] selectionArgs = {dishNameDB};

        Cursor cursor = db.query(
                DishDBHelper.TABLE,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        if (cursor.moveToFirst()) {
            countCalories = cursor.getInt(cursor.getColumnIndexOrThrow(DishDBHelper.COLUMN_CALORIC));
            countProteins = cursor.getInt(cursor.getColumnIndexOrThrow(DishDBHelper.COLUMN_PROTEINS));
            countFats = cursor.getInt(cursor.getColumnIndexOrThrow(DishDBHelper.COLUMN_FATS));
            countCarbohydrates = cursor.getInt(cursor.getColumnIndexOrThrow(DishDBHelper.COLUMN_CARBS));
            countXE = cursor.getInt(cursor.getColumnIndexOrThrow(DishDBHelper.COLUMN_XE));
        }

        cursor.close();
        db.close();

        //mSharedPreferences = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        int currentUserPartOfCalories = mSharedPreferences.getInt("currentCalories", 0);
        int currentUserPartOfProteins = mSharedPreferences.getInt("currentProteins", 0);
        int currentUserPartOfFats = mSharedPreferences.getInt("currentFats", 0);
        int currentUserPartOfCarbohydrates = mSharedPreferences.getInt("currentCarbs", 0);
        int currentUserPartOfXE = mSharedPreferences.getInt("currentXE", 0);

        int sumCaloriesInt = ChoiceOfDishesActivity.sumOrderCalories.intValue();
        int sumProteinsInt = ChoiceOfDishesActivity.sumOrderProteins.intValue();
        int sumFatsInt = ChoiceOfDishesActivity.sumOrderFats.intValue();
        int sumCarbsInt = ChoiceOfDishesActivity.sumOrderCarbs.intValue();
        int sumXEInt = ChoiceOfDishesActivity.sumOrderXE.intValue();

        int newSumCalories = sumCaloriesInt + countCalories;
        int newSumProteins = sumProteinsInt + countProteins;
        int newSumFats = sumFatsInt + countFats;
        int newSumCarbs = sumCarbsInt + countCarbohydrates;
        int newSumXE = sumXEInt + countXE;

        if(newSumCalories > currentUserPartOfCalories) {
            return true;
        }

        if(newSumProteins > currentUserPartOfProteins) {
            return true;
        }

        if(newSumFats > currentUserPartOfFats) {
            return true;
        }

        if(newSumCarbs > currentUserPartOfCarbohydrates) {
            return true;
        }

        if(newSumXE > currentUserPartOfXE) {
            return true;
        }

        return false;
    }

}

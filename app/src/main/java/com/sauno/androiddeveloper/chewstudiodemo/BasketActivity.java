package com.sauno.androiddeveloper.chewstudiodemo;

import android.bluetooth.BluetoothAdapter;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sauno.androiddeveloper.chewstudiodemo.adapter.BasketAdapter;
import com.sauno.androiddeveloper.chewstudiodemo.bluetooth.BluetoothLeService;
import com.sauno.androiddeveloper.chewstudiodemo.bluetooth.LeLister;
import com.sauno.androiddeveloper.chewstudiodemo.model.DishOrderItem;
import com.sauno.androiddeveloper.chewstudiodemo.utility.RecyclerItemTouchHelperForBasket;

import java.lang.ref.WeakReference;
import java.math.BigDecimal;
import java.util.Arrays;

import static android.view.View.GONE;

public class BasketActivity extends AppCompatActivity implements RecyclerItemTouchHelperForBasket.RecyclerItemTouchHelperListener {
    public static final int PICK_CONTACT_REQUEST = 1;

    private Button restoreDishesButton;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private String[] dishOrderNames;
    private BigDecimal[] dishOrderQuantity;

    public static BigDecimal sumOrderCalories;
    public static BigDecimal sumOrderProteins;
    public static BigDecimal sumOrderFats;
    public static BigDecimal sumOrderCarbs;
    public static BigDecimal sumOrderXE;

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


    boolean isExistUserCPFC;

    int currentUserPartOfCalories;
    int currentUserPartOfProteins;
    int currentUserPartOfFats;
    int currentUserPartOfCarbohydrates;
    int currentUserPartOfXE;

    int[] percentageOfCaloriesArray;
    int[] percentageOfProteinsArray;
    int[] percentageOfFatsArray;
    int[] percentageOfCarbsArray;
    int[] percentageOfXEArray;

    private static DishOrderItem[] lastThreeRemoteDishes = new DishOrderItem[3];

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

            mLeService.weakBasketActivity = new WeakReference<>(BasketActivity.this);       // Создать слабую ссылку для MainActivity

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
                Toast.makeText(BasketActivity.this, "BLE not supported on this device",               // Печатаем Toast-сообщение 'BLE not supported on this device'
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
        //mLeLister.updateItem(name, address, rssi);
        //Обновить запись в списке BLE-устройств

        searchDevicesFragment.setUpdate(name);
        onConnected(address);

        Log.i("MyLogMainActivity", "Сработал метод в MainActivity updateLeList");

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basket);

        mSharedPreferences = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);

        bindService(new Intent(this, BluetoothLeService.class),                                     // Привязаться к сервису BluetoothLeService
                mLeConnection, Context.BIND_AUTO_CREATE);

        setupActionBar();

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

        getDataForAdapter();

        mRecyclerView = findViewById(R.id.basketActivityRecyclerView);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new BasketAdapter(this, dishOrderNames, dishOrderQuantity,
                percentageOfCaloriesArray, percentageOfProteinsArray, percentageOfFatsArray, percentageOfCarbsArray, percentageOfXEArray);
        mRecyclerView.setAdapter(mAdapter);

        restoreDishesButton = findViewById(R.id.restoreDishesButton);
        restoreDishesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for(int n = 0; n < 3; n++) {
                    if(lastThreeRemoteDishes[n] != null) {
                        ChoiceOfDishesActivity.dishOrderList.add(lastThreeRemoteDishes[n]);
                    }
                }
                lastThreeRemoteDishes = null;
                lastThreeRemoteDishes = new DishOrderItem[3];
                dataToAdapter();
            }
        });


        Button nextButton = findViewById(R.id.nextButton);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ChoiceOfDishesActivity.dishOrderList.size() == 0) {
                    Toast.makeText(view.getContext(), "Список блюд пуст", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(view.getContext(), Dish18Activity.class);
                    intent.putExtra("dishName", ChoiceOfDishesActivity.dishOrderList.get(0).getDishName());
                    intent.putExtra("calories", ChoiceOfDishesActivity.dishOrderList.get(0).getCalories().intValue());
                    intent.putExtra("proteins", ChoiceOfDishesActivity.dishOrderList.get(0).getProteins().intValue());
                    intent.putExtra("fats", ChoiceOfDishesActivity.dishOrderList.get(0).getFats().intValue());
                    intent.putExtra("carbs", ChoiceOfDishesActivity.dishOrderList.get(0).getCarbs().intValue());
                    intent.putExtra("xe", ChoiceOfDishesActivity.dishOrderList.get(0).getXe().intValue());
                    startActivity(intent);
                }
            }
        });


        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerItemTouchHelperForBasket(0, ItemTouchHelper.RIGHT, this);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(mRecyclerView);

        setViewData();

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
        new ItemTouchHelper(itemTouchHelperCallback1).attachToRecyclerView(mRecyclerView);
    }


    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setTitle("Корзина");
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
            fragmentTransaction.add(R.id.basketActivity, searchFragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString("currentActivityForService", "BasketActivity");

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

    private void getDataForAdapter() {
        sumOrderCalories = new BigDecimal(0.0).setScale(2, BigDecimal.ROUND_DOWN);
        sumOrderProteins = new BigDecimal(0.0).setScale(2, BigDecimal.ROUND_DOWN);
        sumOrderFats = new BigDecimal(0.0).setScale(2, BigDecimal.ROUND_DOWN);
        sumOrderCarbs = new BigDecimal(0.0).setScale(2, BigDecimal.ROUND_DOWN);
        sumOrderXE = new BigDecimal(0.0).setScale(2, BigDecimal.ROUND_DOWN);

        dishOrderNames = new String[ChoiceOfDishesActivity.dishOrderList.size()];
        dishOrderQuantity = new BigDecimal[ChoiceOfDishesActivity.dishOrderList.size()];

        ChoiceOfDishesActivity.countChosenDishes = 0;
        for(int i = 0; i < ChoiceOfDishesActivity.dishOrderList.size(); i++) {
            dishOrderNames[i] = ChoiceOfDishesActivity.dishOrderList.get(i).getDishName();
            dishOrderQuantity[i] = ChoiceOfDishesActivity.dishOrderList.get(i).getQuantityDishes();

            ChoiceOfDishesActivity.countChosenDishes = ChoiceOfDishesActivity.countChosenDishes + 1;

            BigDecimal dishQuantity = dishOrderQuantity[i];

            sumOrderCalories = sumOrderCalories.add((ChoiceOfDishesActivity.dishOrderList.get(i).getCalories()).multiply(dishQuantity));
            sumOrderProteins = sumOrderProteins.add((ChoiceOfDishesActivity.dishOrderList.get(i).getProteins()).multiply(dishQuantity));
            sumOrderFats = sumOrderFats.add((ChoiceOfDishesActivity.dishOrderList.get(i).getFats()).multiply(dishQuantity));
            sumOrderCarbs = sumOrderCarbs.add((ChoiceOfDishesActivity.dishOrderList.get(i).getCarbs()).multiply(dishQuantity));
            sumOrderXE = sumOrderXE.add((ChoiceOfDishesActivity.dishOrderList.get(i).getXe()).multiply(dishQuantity));
        }

        int sumOrderCaloriesInt = sumOrderCalories.intValue();
        int sumOrderProteinsInt = sumOrderProteins.intValue();
        int sumOrderFatsInt = sumOrderFats.intValue();
        int sumOrderCarbsInt = sumOrderCarbs.intValue();
        int sumOrderXEInt = sumOrderXE.intValue();

        percentageOfCaloriesArray = new int[ChoiceOfDishesActivity.dishOrderList.size()];
        percentageOfProteinsArray = new int[ChoiceOfDishesActivity.dishOrderList.size()];
        percentageOfFatsArray = new int[ChoiceOfDishesActivity.dishOrderList.size()];
        percentageOfCarbsArray = new int[ChoiceOfDishesActivity.dishOrderList.size()];
        percentageOfXEArray = new int[ChoiceOfDishesActivity.dishOrderList.size()];

        for(int i = 0; i < ChoiceOfDishesActivity.dishOrderList.size(); i++) {
            if(sumOrderCaloriesInt != 0)percentageOfCaloriesArray[i] = (ChoiceOfDishesActivity.dishOrderList.get(i).getCalories()).multiply(dishOrderQuantity[i]).multiply(new BigDecimal(100)).intValue()/sumOrderCaloriesInt;
            if(sumOrderProteinsInt != 0)percentageOfProteinsArray[i] = (ChoiceOfDishesActivity.dishOrderList.get(i).getProteins()).multiply(dishOrderQuantity[i]).multiply(new BigDecimal(100)).intValue()/sumOrderProteinsInt;
            if(sumOrderFatsInt != 0)percentageOfFatsArray[i] = (ChoiceOfDishesActivity.dishOrderList.get(i).getFats()).multiply(dishOrderQuantity[i]).multiply(new BigDecimal(100)).intValue()/sumOrderFatsInt;
            if(sumOrderCarbsInt != 0)percentageOfCarbsArray[i] = (ChoiceOfDishesActivity.dishOrderList.get(i).getCarbs()).multiply(dishOrderQuantity[i]).multiply(new BigDecimal(100)).intValue()/sumOrderCarbsInt;
            if(sumOrderXEInt != 0)percentageOfXEArray[i] = (ChoiceOfDishesActivity.dishOrderList.get(i).getXe()).multiply(dishOrderQuantity[i]).multiply(new BigDecimal(100)).intValue()/sumOrderXEInt;
        }

    }

    /*public void restartData(int quantity, final String dishName) {
        if(quantity == 0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AppDialog);
            builder.setTitle("Вы хотите удалить это блюдо из списка?");
            builder.setPositiveButton("Да", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int n) {
                    for (int i = 0; i < ChoiceOfDishesActivity.dishOrderList.size(); i++) {
                        String dish = ChoiceOfDishesActivity.dishOrderList.get(i).getDishName();
                        if (dish.equals(dishName)) {
                            ChoiceOfDishesActivity.dishOrderList.remove(i);
                        }
                    }

                    dataToAdapter();
                }
            });
            builder.setNegativeButton("Нет", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    for(int n = 0; n < ChoiceOfDishesActivity.dishOrderList.size(); n++) {
                        if((ChoiceOfDishesActivity.dishOrderList.get(n).getDishName()).equals(dishName)) {
                            ChoiceOfDishesActivity.dishOrderList.get(n).setQuantityDishes(1);
                        }
                    }
                    dataToAdapter();
                }
            });

            AlertDialog alert = builder.create();
            alert.show();
        } else {
            dataToAdapter();
        }
    }*/

    private void dataToAdapter() {
        getDataForAdapter();

        mRecyclerView.removeAllViews();
        mAdapter = new BasketAdapter(this, dishOrderNames, dishOrderQuantity,
                percentageOfCaloriesArray, percentageOfProteinsArray, percentageOfFatsArray, percentageOfCarbsArray, percentageOfXEArray);
        mRecyclerView.setAdapter(mAdapter);

        setViewData();
    }

    private void setViewData() {
        //orderSumTextView.setText("Сумма вашего заказа - " + sumPrices.intValue() + "руб.");

        int sumOrderCaloriesInt = sumOrderCalories.intValue();
        int sumOrderProteinsInt = sumOrderProteins.intValue();
        int sumOrderFatsInt = sumOrderFats.intValue();
        int sumOrderCarbsInt = sumOrderCarbs.intValue();
        int sumOrderXEInt = sumOrderXE.intValue();

        countCaloriesTextView.setText("" + sumOrderCaloriesInt);
        countProteinsTextView.setText(sumOrderProteinsInt + "г");
        countFatsTextView.setText(sumOrderFatsInt + "г");
        countCarbohydratesTextView.setText(sumOrderCarbsInt + "г");
        countXETextView.setText("" + sumOrderXEInt);

        checkIfExistUserCPFC();

        if(isExistUserCPFC) {
            getCurrentUserCPFC();
            setAboveLimitColorForCPFC(sumOrderCaloriesInt, sumOrderProteinsInt, sumOrderFatsInt, sumOrderCarbsInt, sumOrderXEInt);
        }

        if(lastThreeRemoteDishes[0] == null) {
            restoreDishesButton.setVisibility(GONE);
        } else {
            restoreDishesButton.setVisibility(View.VISIBLE);
            restoreDishesButton.setClickable(true);
        }
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

    private void getCurrentUserCPFC() {
        SharedPreferences mSharedPreferences = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        currentUserPartOfCalories = mSharedPreferences.getInt("currentCalories", 0);
        currentUserPartOfProteins = mSharedPreferences.getInt("currentProteins", 0);
        currentUserPartOfFats = mSharedPreferences.getInt("currentFats", 0);
        currentUserPartOfCarbohydrates = mSharedPreferences.getInt("currentCarbs", 0);
        currentUserPartOfXE = mSharedPreferences.getInt("currentXE", 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            dataToAdapter();
        }
    }
/*
    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        String name = dishOrderNames[viewHolder.getAdapterPosition()];
        Log.i("MyLogBasketActivity", "name - " + name);
        if (viewHolder instanceof BasketAdapter.ViewHolder) {
            // get the removed item name
           *//* String name = dishOrderNames[viewHolder.getAdapterPosition()];
            Log.i("MyLogBasketActivity", "name - " + name);*//*
        }
    }*/

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if (viewHolder instanceof BasketAdapter.ViewHolder) {
            String dishName = dishOrderNames[viewHolder.getAdapterPosition()];

            for (int i = 0; i < ChoiceOfDishesActivity.dishOrderList.size(); i++) {
                String dish = ChoiceOfDishesActivity.dishOrderList.get(i).getDishName();
                if (dish.equals(dishName)) {
                    setDishToLastThreeRemoteDishes(ChoiceOfDishesActivity.dishOrderList.get(i));
                    ChoiceOfDishesActivity.dishOrderList.remove(i);
                }
            }
            dataToAdapter();
        }
    }

    public static void setDishToLastThreeRemoteDishes(DishOrderItem dishOrderItem) {
        DishOrderItem[] dishOrderItemsArray = new DishOrderItem[3];

        if(lastThreeRemoteDishes[0] == null) {
            lastThreeRemoteDishes[0] = dishOrderItem;
        } else if(lastThreeRemoteDishes[1] == null) {
            lastThreeRemoteDishes[1] = dishOrderItem;
        } else if(lastThreeRemoteDishes[2] == null) {
            lastThreeRemoteDishes[2] = dishOrderItem;
        } else {
            dishOrderItemsArray[0] = lastThreeRemoteDishes[1];
            dishOrderItemsArray[1] = lastThreeRemoteDishes[2];
            dishOrderItemsArray[2] = dishOrderItem;
            lastThreeRemoteDishes = Arrays.copyOf(dishOrderItemsArray,3);
        }

/*

        Log.i("MyLogBasketActivity", "1 - " + lastThreeRemoteDishes[0].getDishName());

        if(lastThreeRemoteDishes[1] != null) {
            Log.i("MyLogBasketActivity", "2 - " + lastThreeRemoteDishes[1].getDishName());
        }
        if(lastThreeRemoteDishes[2] != null) {
            Log.i("MyLogBasketActivity", "3 - " + lastThreeRemoteDishes[2].getDishName());
        }
*/

    }
}

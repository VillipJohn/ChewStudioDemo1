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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sauno.androiddeveloper.chewstudiodemo.bluetooth.BluetoothLeService;
import com.sauno.androiddeveloper.chewstudiodemo.bluetooth.LeLister;
import com.sauno.androiddeveloper.chewstudiodemo.data.DatabaseCreateHelper;
import com.sauno.androiddeveloper.chewstudiodemo.data.DishDBHelper;

import java.lang.ref.WeakReference;
import java.math.BigDecimal;

public class SetQuantityOfDishesActivity extends AppCompatActivity {
    SearchDevicesFragment searchDevicesFragment;
    Menu mainMenu;

    SharedPreferences mSharedPreferences;

    private ImageView plusImageView;
    private ImageView minusImageView;

    private TextView dishCountTextView;
    private TextView dishNameTextView;

    private LinearLayout containerCPFCXLinearLayout;

    private TextView countCaloriesTextView;
    private TextView countProteinsTextView;
    private TextView countFatsTextView;
    private TextView countCarbsTextView;
    private TextView countXETextView;

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

    String dishName;

    BigDecimal quantity;
    int price, calories, proteins, fats, carbs, xe;

    int restaurantInt;
    String categoryString;
    boolean observeCalories, observeProteins, observeFats, observeCarbs;
    int dishCalories, dishProteins, dishFats, dishCarbs, dishXE;

    static final int CODE_REQUEST = 1;  // The request code

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

            mLeService.weakSetQuantityOfDishesActivity = new WeakReference<>(SetQuantityOfDishesActivity.this);       // Создать слабую ссылку для MainActivity

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
                Toast.makeText(SetQuantityOfDishesActivity.this, "BLE not supported on this device",               // Печатаем Toast-сообщение 'BLE not supported on this device'
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
        setContentView(R.layout.activity_set_quantity_of_dishes);

        mSharedPreferences = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);

        // Привязаться к сервису BluetoothLeService
        bindService(new Intent(this, BluetoothLeService.class),
                mLeConnection, Context.BIND_AUTO_CREATE);

        setupActionBar();

        plusImageView = findViewById(R.id.plusImageView);
        minusImageView  = findViewById(R.id.minusImageView);

        dishCountTextView = findViewById(R.id.dishCountTextView);
        dishNameTextView = findViewById(R.id.dishNameTextView);

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

        Intent intent = getIntent();
        dishName = intent.getStringExtra("dishName");

        setData();

        plusImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(quantity.intValue() >= 1) {
                    for(int i = 0; i < ChoiceOfDishesActivity.dishOrderList.size(); i++) {
                        if(ChoiceOfDishesActivity.dishOrderList.get(i).getDishName().equals(dishName)) {
                            ChoiceOfDishesActivity.dishOrderList.get(i).setQuantityDishes(quantity.add(new BigDecimal(1)));
                            break;
                        }
                    }
                } else {
                    for(int i = 0; i < ChoiceOfDishesActivity.dishOrderList.size(); i++) {
                        if(ChoiceOfDishesActivity.dishOrderList.get(i).getDishName().equals(dishName)) {
                            float quantityFloat = ChoiceOfDishesActivity.dishOrderList.get(i).getQuantityDishes().floatValue();
                            if(Float.compare(quantityFloat, 0.66f) == 0) {
                                ChoiceOfDishesActivity.dishOrderList.get(i).setQuantityDishes(new BigDecimal(1).setScale(2, BigDecimal.ROUND_DOWN));
                            }
                            if(Float.compare(quantityFloat, 0.5f) == 0) {
                                ChoiceOfDishesActivity.dishOrderList.get(i).setQuantityDishes(new BigDecimal(0.66).setScale(2, BigDecimal.ROUND_DOWN));
                            }
                            if(Float.compare(quantityFloat, 0.33f) == 0) {
                                ChoiceOfDishesActivity.dishOrderList.get(i).setQuantityDishes(new BigDecimal(0.5).setScale(2, BigDecimal.ROUND_DOWN));
                            }
                            break;
                        }
                    }
                }

                setData();
            }
        });

        minusImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(quantity.intValue() > 1) {
                    for(int i = 0; i < ChoiceOfDishesActivity.dishOrderList.size(); i++) {
                        if(ChoiceOfDishesActivity.dishOrderList.get(i).getDishName().equals(dishName)) {
                            ChoiceOfDishesActivity.dishOrderList.get(i).setQuantityDishes(quantity.subtract(new BigDecimal(1)));
                            break;
                        }
                    }
                } else {
                    for(int i = 0; i < ChoiceOfDishesActivity.dishOrderList.size(); i++) {
                        if(ChoiceOfDishesActivity.dishOrderList.get(i).getDishName().equals(dishName)) {
                            float quantityFloat = ChoiceOfDishesActivity.dishOrderList.get(i).getQuantityDishes().floatValue();
                            if(Float.compare(quantityFloat, 1f) == 0) {
                                ChoiceOfDishesActivity.dishOrderList.get(i).setQuantityDishes(new BigDecimal(0.66).setScale(2, BigDecimal.ROUND_DOWN));
                            }
                            if(Float.compare(quantityFloat, 0.66f) == 0) {
                                ChoiceOfDishesActivity.dishOrderList.get(i).setQuantityDishes(new BigDecimal(0.5).setScale(2, BigDecimal.ROUND_DOWN));
                            }
                            if(Float.compare(quantityFloat, 0.5f) == 0) {
                                ChoiceOfDishesActivity.dishOrderList.get(i).setQuantityDishes(new BigDecimal(0.33).setScale(2, BigDecimal.ROUND_DOWN));
                            }

                            break;
                        }
                    }

                }
                setData();
            }
        });

        Button changeOkButton = findViewById(R.id.okButton);
        changeOkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
            }
        });


        Button changeDishButton = findViewById(R.id.changeDishButton);
        changeDishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                getRestaurantAndCategoryFromDB();



                if(checkIsObserveCPFC()) {
                    Intent intent = new Intent(view.getContext(), DishReplacementActivity.class);
                    intent.putExtra("dishName", dishName);
                    intent.putExtra("dishRestaurant", restaurantInt);
                    intent.putExtra("dishCategory", categoryString);
                    intent.putExtra("dishCalories", dishCalories);
                    intent.putExtra("dishProteins", dishProteins);
                    intent.putExtra("dishFats", dishFats);
                    intent.putExtra("dishCarbs", dishCarbs);
                    intent.putExtra("dishXE", dishXE);
                    intent.putExtra("dishXE", dishXE);

                    startActivityForResult(intent, CODE_REQUEST);
                } else {
                    Intent intent = new Intent(view.getContext(), DishReplacementActivity.class);
                    intent.putExtra("dishName", dishName);
                    intent.putExtra("dishRestaurant", restaurantInt);
                    intent.putExtra("dishCategory", categoryString);
                    intent.putExtra("dishCalories", dishCalories);
                    intent.putExtra("dishProteins", dishProteins);
                    intent.putExtra("dishFats", dishFats);
                    intent.putExtra("dishCarbs", dishCarbs);
                    intent.putExtra("dishXE", dishXE);


                    startActivityForResult(intent, CODE_REQUEST);
                }
            }
        });

        Button deleteDishButton = findViewById(R.id.deleteDishButton);
        deleteDishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (int i = 0; i < ChoiceOfDishesActivity.dishOrderList.size(); i++) {
                    String dish = ChoiceOfDishesActivity.dishOrderList.get(i).getDishName();
                    if (dish.equals(dishName)) {
                        BasketActivity.setDishToLastThreeRemoteDishes(ChoiceOfDishesActivity.dishOrderList.get(i));
                        ChoiceOfDishesActivity.dishOrderList.remove(i);
                    }
                }

                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
            }
        });



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == CODE_REQUEST) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                String newDishName = data.getStringExtra("dishName");
                int idDish = data.getIntExtra("idDish", 0);
                int calories = data.getIntExtra("calories", 0);
                int proteins = data.getIntExtra("proteins", 0);
                int fats = data.getIntExtra("fats", 0);
                int carbs = data.getIntExtra("carbs", 0);
                int xe = data.getIntExtra("xe", 0);

                for(int i = 0; i < ChoiceOfDishesActivity.dishOrderList.size(); i++) {
                    if(ChoiceOfDishesActivity.dishOrderList.get(i).getDishName().equals(dishName)) {
                        ChoiceOfDishesActivity.dishOrderList.get(i).setIdDish(idDish);
                        ChoiceOfDishesActivity.dishOrderList.get(i).setDishName(newDishName);
                        ChoiceOfDishesActivity.dishOrderList.get(i).setQuantityDishes(new BigDecimal(1).setScale(1));
                        ChoiceOfDishesActivity.dishOrderList.get(i).setCalories(new BigDecimal(calories).setScale(1));
                        ChoiceOfDishesActivity.dishOrderList.get(i).setProteins(new BigDecimal(proteins).setScale(1));
                        ChoiceOfDishesActivity.dishOrderList.get(i).setFats(new BigDecimal(fats).setScale(1));
                        ChoiceOfDishesActivity.dishOrderList.get(i).setCarbs(new BigDecimal(carbs).setScale(1));
                        ChoiceOfDishesActivity.dishOrderList.get(i).setXe(new BigDecimal(xe).setScale(1));
                    }
                }

                dishName = newDishName;
                setData();

            }
        }
    }

    private void setData() {
        setQuantityTextView();

        dishNameTextView.setText(dishName);

        BigDecimal sumOrderCalories = new BigDecimal(0.0).setScale(2, BigDecimal.ROUND_DOWN);
        BigDecimal sumOrderProteins = new BigDecimal(0.0).setScale(2, BigDecimal.ROUND_DOWN);
        BigDecimal sumOrderFats = new BigDecimal(0.0).setScale(2, BigDecimal.ROUND_DOWN);
        BigDecimal sumOrderCarbs = new BigDecimal(0.0).setScale(2, BigDecimal.ROUND_DOWN);
        BigDecimal sumOrderXE = new BigDecimal(0.0).setScale(2, BigDecimal.ROUND_DOWN);

        for(int i = 0; i < ChoiceOfDishesActivity.dishOrderList.size(); i++) {
            BigDecimal dishQuantity = ChoiceOfDishesActivity.dishOrderList.get(i).getQuantityDishes();

            sumOrderCalories = sumOrderCalories.add((ChoiceOfDishesActivity.dishOrderList.get(i).getCalories()).multiply(dishQuantity));
            sumOrderProteins = sumOrderProteins.add((ChoiceOfDishesActivity.dishOrderList.get(i).getProteins()).multiply(dishQuantity));
            sumOrderFats = sumOrderFats.add((ChoiceOfDishesActivity.dishOrderList.get(i).getFats()).multiply(dishQuantity));
            sumOrderCarbs = sumOrderCarbs.add((ChoiceOfDishesActivity.dishOrderList.get(i).getCarbs()).multiply(dishQuantity));
            sumOrderXE = sumOrderXE.add((ChoiceOfDishesActivity.dishOrderList.get(i).getXe()).multiply(dishQuantity));
        }



        String caloriesString = "" + sumOrderCalories.intValue();
        String proteinsString = sumOrderProteins.intValue() + "г";
        String fatsString = sumOrderFats.intValue() + "г";
        String carbsString = sumOrderCarbs.intValue() + "г";
        String xeString = "" + sumOrderXE.intValue();

        countCaloriesTextView.setText(caloriesString);
        countProteinsTextView.setText(proteinsString);
        countFatsTextView.setText(fatsString);
        countCarbsTextView.setText(carbsString);
        countXETextView.setText(xeString);

        /*checkIfExistUserCPFC(
                sumOrderCalories.intValue(),
                sumOrderProteins.intValue(),
                sumOrderFats.intValue(),
                sumOrderCarbs.intValue());*/

        checkIfExistUserCPFC();

        if(isExistUserCPFC) {
            getCurrentUserCPFC();
            setAboveLimitColorForCPFC(sumOrderCalories.intValue(), sumOrderProteins.intValue(), sumOrderFats.intValue(), sumOrderCarbs.intValue(), sumOrderXE.intValue());
        }
    }

    /*private void checkIfExistUserCPFC(int sumOrderCaloriesInt,
                                      int sumOrderProteinsInt,
                                      int sumOrderFatsInt,
                                      int sumOrderCarbsInt) {
        SharedPreferences mSharedPreferences = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        int userDayCalories = mSharedPreferences.getInt(UserProfileActivity.KEY_COUNT_CALORIES, 0);

        boolean isExistUserCPFC;

        if (userDayCalories == 0) {
            Toast.makeText(this, "Для подсчёта рекомендуемых Вам ежедневно калорий, белков, жиров, углеводов введите: пол, возраст, рост, вес, образ жизни", Toast.LENGTH_SHORT).show();
            isExistUserCPFC = false;
        } else {
            isExistUserCPFC = true;
        }

        if (isExistUserCPFC) {
            setAboveLimitColorForCPFC(sumOrderCaloriesInt, sumOrderProteinsInt, sumOrderFatsInt, sumOrderCarbsInt);

        }
    }*/

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

    /*private void setAboveLimitColorForCPFC(int sumCaloriesInt, int sumProteinsInt, int sumFatsInt, int sumCarbsInt) {
        SharedPreferences mSharedPreferences = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        int currentUserPartOfCalories = mSharedPreferences.getInt("currentCalories", 0);
        int currentUserPartOfProteins = mSharedPreferences.getInt("currentProteins", 0);
        int currentUserPartOfFats = mSharedPreferences.getInt("currentFats", 0);
        int currentUserPartOfCarbohydrates = mSharedPreferences.getInt("currentCarbs", 0);

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
            countCarbsTextView.setTextColor(Color.RED);
        } else {
            countCarbsTextView.setTextColor(Color.BLACK);
        }
    }*/

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setTitle("Выбор количества блюд");
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
            fragmentTransaction.add(R.id.setQuantityOfDishesActivity, searchFragment);
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
        editor.putString("currentActivityForService", "SetQuantityOfDishesActivity");

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



    private void setQuantityTextView() {
        for(int i = 0; i < ChoiceOfDishesActivity.dishOrderList.size(); i++) {
            if(ChoiceOfDishesActivity.dishOrderList.get(i).getDishName().equals(dishName)) {
                quantity = ChoiceOfDishesActivity.dishOrderList.get(i).getQuantityDishes();
                break;
            }
        }

        float quantityFloat = quantity.floatValue();

        Log.i("MyLogSetQ", "float  =  " + quantityFloat);

        if (Float.compare(quantityFloat, 0.33f) == 0) {
            dishCountTextView.setText("1/3");
        } else if (Float.compare(quantityFloat, 0.5f) == 0) {
            dishCountTextView.setText("1/2");
        } else if (Float.compare(quantityFloat, 0.66f) == 0) {
            dishCountTextView.setText("2/3");
        } else {
            dishCountTextView.setText("" + quantity.intValue());
        }
    }

    //Проверка отслеживает ли пользователь калории или белки или жиры или углеводы в настройках
    private boolean checkIsObserveCPFC() {
        SharedPreferences mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        observeCalories = mSharedPreferences.getBoolean(UserPreferencesActivity.KEY_CALORIE_CHECK_BOX_PREF, false);
        observeProteins = mSharedPreferences.getBoolean(UserPreferencesActivity.KEY_PROTEIN_CHECK_BOX_PREF, false);
        observeFats = mSharedPreferences.getBoolean(UserPreferencesActivity.KEY_FAT_CHECK_BOX_PREF, false);
        observeCarbs = mSharedPreferences.getBoolean(UserPreferencesActivity.KEY_CARBOHYDRATE_CHECK_BOX_PREF, false);

        String name = mSharedPreferences.getString(UserPreferencesActivity.KEY_USER_NAME_PREF, "");

        Log.i("MyLogSetQuantity",  "Три булена " + observeCalories + "  " + observeProteins + "  " + observeFats + "  " + observeCarbs);
        Log.i("MyLogSetQuantity", "\n Имя - " + name);


        boolean res = false;

        if(observeCalories || observeProteins || observeFats || observeCarbs) {
            res = true;
        }

        return res;
    }

    //Получаем по имени блюда ресторан, категорию и КБЖУ
    private void getRestaurantAndCategoryFromDB() {

        DatabaseCreateHelper databaseCreateHelper = new DatabaseCreateHelper(this);
        SQLiteDatabase db = databaseCreateHelper.getWritableDatabase();


        String[] projection = {
                DishDBHelper.COLUMN_CATEGORY,
                DishDBHelper.COLUMN_RESTAURANT,
                DishDBHelper.COLUMN_CALORIC,
                DishDBHelper.COLUMN_PROTEINS,
                DishDBHelper.COLUMN_FATS,
                DishDBHelper.COLUMN_CARBS,
                DishDBHelper.COLUMN_XE
        };
        String selection = DishDBHelper.COLUMN_DESCRIPTION + " = ?";
        String[] selectionArgs = {dishName};

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


        if (cursor.moveToFirst()) {
            restaurantInt = cursor.getInt(cursor.getColumnIndexOrThrow(DishDBHelper.COLUMN_RESTAURANT));
            categoryString = cursor.getString(cursor.getColumnIndexOrThrow(DishDBHelper.COLUMN_CATEGORY));
            dishCalories = cursor.getInt(cursor.getColumnIndexOrThrow(DishDBHelper.COLUMN_CALORIC));
            dishProteins  = cursor.getInt(cursor.getColumnIndexOrThrow(DishDBHelper.COLUMN_PROTEINS));
            dishFats = cursor.getInt(cursor.getColumnIndexOrThrow(DishDBHelper.COLUMN_FATS));
            dishCarbs = cursor.getInt(cursor.getColumnIndexOrThrow(DishDBHelper.COLUMN_CARBS));
            dishXE = cursor.getInt(cursor.getColumnIndexOrThrow(DishDBHelper.COLUMN_XE));
        }

        cursor.close();
        db.close();
    }
}

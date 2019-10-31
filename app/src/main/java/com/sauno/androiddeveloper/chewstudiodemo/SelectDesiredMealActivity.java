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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.sauno.androiddeveloper.chewstudiodemo.bluetooth.BluetoothLeService;
import com.sauno.androiddeveloper.chewstudiodemo.bluetooth.LeLister;

import java.lang.ref.WeakReference;
import java.util.Calendar;

public class SelectDesiredMealActivity extends AppCompatActivity {
    SearchDevicesFragment searchDevicesFragment;
    Menu mainMenu;

    SharedPreferences mSharedPreferences;

    TextView countCaloriesInSelectDesiredMealTextView;
    TextView countProteinsInSelectDesiredMealTextView;
    TextView countFatsInSelectDesiredMealTextView;
    TextView countCarbohydratesInSelectDesiredMealTextView;
    TextView countXETextView;

    RadioButton firstInThreeRadioButton;
    RadioButton secondInThreeRadioButton;
    RadioButton thirdInThreeRadioButton;

    RadioButton firstInFourRadioButton;
    RadioButton secondInFourRadioButton;
    RadioButton thirdInFourRadioButton;
    RadioButton fourthInFourRadioButton;

    RadioButton firstInFiveRadioButton;
    RadioButton secondInFiveRadioButton;
    RadioButton thirdInFiveRadioButton;
    RadioButton fourthInFiveRadioButton;
    RadioButton fifthInFiveRadioButton;

    RadioButton firstInSixRadioButton;
    RadioButton secondInSixRadioButton;
    RadioButton thirdInSixRadioButton;
    RadioButton fourthInSixRadioButton;
    RadioButton fifthInSixRadioButton;
    RadioButton sixthInSixRadioButton;

    Button tuneMealButton;

    Spinner mealsSpinner;

    public static int calories;
    public static int proteins;
    public static int fats;
    public static int carbohydrates;
    public static int xe;

    public static int currentMeal;

    // Питание: Трёхразовое - 3, четырёхразовое - 4, пятиразовое - 5, шестиразовое - 6
    int checkedMeal;

    //Boolean isCheckedSwitch;

    TuneThreeMealFragment tuneThreeMealFragment;
    TuneFourMealFragment tuneFourMealFragment;
    TuneFiveMealFragment tuneFiveMealFragment;
    TuneSixMealFragment tuneSixMealFragment;


    // Переменные в которых хранятся проценты каждого приёма пищи
    int breakfastPercentThreeMeal;
    int lunchPercentThreeMeal;
    int dinnerPercentThreeMeal;

    int breakfastPercentFourMeal;
    int lunchPercentFourMeal;
    int dinnerPercentFourMeal;
    int snackPercentFourMeal;

    int breakfastPercentFiveMeal;
    int firstSnackPercentFiveMeal;
    int lunchPercentFiveMeal;
    int secondSnackPercentFiveMeal;
    int dinnerPercentFiveMeal;

    int breakfastPercentSixMeal;
    int firstSnackPercentSixMeal;
    int lunchPercentSixMeal;
    int secondSnackPercentSixMeal;
    int dinnerPercentSixMeal;
    int thirdSnackPercentSixMeal;

    RadioGroup threeMealsDay;
    RadioGroup fourMealsDay;
    RadioGroup fiveMealsDay;
    RadioGroup sixMealsDay;

    int partOfCalories;
    int partOfProteins;
    int partOfFats;
    int partOfCarbohydrates;
    int partOfXE;

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

            mLeService.weakSelectDesiredMealActivity = new WeakReference<>(SelectDesiredMealActivity.this);       // Создать слабую ссылку для MainActivity

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
                Toast.makeText(SelectDesiredMealActivity.this, "BLE not supported on this device",               // Печатаем Toast-сообщение 'BLE not supported on this device'
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
        setContentView(R.layout.activity_select_desired_meal);

        mSharedPreferences = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        //isCheckedSwitch = mSharedPreferences.getBoolean("threeOrFiveMeals", false);
        checkedMeal = mSharedPreferences.getInt("checkedMeal", 3);

        mealsSpinner = findViewById(R.id.mealsSpinner);

        countCaloriesInSelectDesiredMealTextView = findViewById(R.id.countCaloriesInSelectDesiredMealTextView);
        countProteinsInSelectDesiredMealTextView = findViewById(R.id.countProteinsInSelectDesiredMealTextView);
        countFatsInSelectDesiredMealTextView = findViewById(R.id.countFatsInSelectDesiredMealTextView);
        countCarbohydratesInSelectDesiredMealTextView = findViewById(R.id.countCarbohydratesInSelectDesiredMealTextView);
        countXETextView = findViewById(R.id.countXETextView);

        firstInThreeRadioButton = findViewById(R.id.firstInThreeRadioButton);                       //1
        secondInThreeRadioButton = findViewById(R.id.secondInThreeRadioButton);                     //2
        thirdInThreeRadioButton = findViewById(R.id.thirdInThreeRadioButton);                       //3

        firstInFourRadioButton = findViewById(R.id.firstInFourRadioButton);                         //4
        secondInFourRadioButton = findViewById(R.id.secondInFourRadioButton);                       //5
        thirdInFourRadioButton = findViewById(R.id.thirdInFourRadioButton);                         //6
        fourthInFourRadioButton = findViewById(R.id.fourthInFourRadioButton);                       //7

        firstInFiveRadioButton = findViewById(R.id.firstInFiveRadioButton);                         //8
        secondInFiveRadioButton = findViewById(R.id.secondInFiveRadioButton);                       //9
        thirdInFiveRadioButton = findViewById(R.id.thirdInFiveRadioButton);                         //10
        fourthInFiveRadioButton = findViewById(R.id.fourthInFiveRadioButton);                       //11
        fifthInFiveRadioButton = findViewById(R.id.fifthInFiveRadioButton);                         //12

        firstInSixRadioButton = findViewById(R.id.firstInSixRadioButton);                           //13
        secondInSixRadioButton = findViewById(R.id.secondInSixRadioButton);                         //14
        thirdInSixRadioButton = findViewById(R.id.thirdInSixRadioButton);                           //15
        fourthInSixRadioButton = findViewById(R.id.fourthInSixRadioButton);                         //16
        fifthInSixRadioButton = findViewById(R.id.fifthInSixRadioButton);                           //17
        sixthInSixRadioButton = findViewById(R.id.sixthInSixRadioButton);                           //18


        tuneMealButton = findViewById(R.id.tuneMealButton);
        tuneMealButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                if(isCheckedSwitch) {
                    tuneFiveMealFragment = new TuneFiveMealFragment();
                    fragmentTransaction.add(R.id.selectDesiredMeal, tuneFiveMealFragment);
                } else {
                    tuneThreeMealFragment = new TuneThreeMealFragment();
                    fragmentTransaction.add(R.id.selectDesiredMeal, tuneThreeMealFragment);
                }
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();*/

                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();


                switch(checkedMeal) {
                    case 3:
                        tuneThreeMealFragment = new TuneThreeMealFragment();
                        fragmentTransaction.add(R.id.selectDesiredMeal, tuneThreeMealFragment);
                        break;
                    case 4:
                        tuneFourMealFragment = new TuneFourMealFragment();
                        fragmentTransaction.add(R.id.selectDesiredMeal, tuneFourMealFragment);
                        break;
                    case 5:
                        tuneFiveMealFragment = new TuneFiveMealFragment();
                        fragmentTransaction.add(R.id.selectDesiredMeal, tuneFiveMealFragment);
                        break;
                    case 6:
                        tuneSixMealFragment = new TuneSixMealFragment();
                        fragmentTransaction.add(R.id.selectDesiredMeal, tuneSixMealFragment);
                        break;
                }
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        calories = mSharedPreferences.getInt(UserProfileActivity.KEY_COUNT_CALORIES, 0);
        proteins = mSharedPreferences.getInt(UserProfileActivity.KEY_COUNT_PROTEINS, 0);
        fats = mSharedPreferences.getInt(UserProfileActivity.KEY_COUNT_FATS, 0);
        carbohydrates = mSharedPreferences.getInt(UserProfileActivity.KEY_COUNT_CARBOHYDRATES, 0);
        xe = mSharedPreferences.getInt(UserProfileActivity.KEY_COUNT_XE, 0);

        if(calories == 0) {
            Toast.makeText(this, "Для отображения всех данных заполните в Вашей анкете: пол, возраст, рост, вес, образ жизни", Toast.LENGTH_LONG).show();
        }

        //Switch threeOrFiveSwitch = findViewById(R.id.threeOrFiveSwitch);
        threeMealsDay = findViewById(R.id.threeMealsDay);
        fourMealsDay = findViewById(R.id.fourMealsDay);
        fiveMealsDay = findViewById(R.id.fiveMealsDay);
        sixMealsDay = findViewById(R.id.sixMealsDay);



        /*if(isCheckedSwitch) {
            fiveMealsDay.setVisibility(View.VISIBLE);
            threeMealsDay.setVisibility(View.GONE);
            threeOrFiveSwitch.setChecked(true);
        } else {
            fiveMealsDay.setVisibility(View.GONE);
            threeMealsDay.setVisibility(View.VISIBLE);
        }*/


        mealsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                checkedMeal = position + 3;

                SharedPreferences.Editor editor = mSharedPreferences.edit();
                editor.putInt("checkedMeal", checkedMeal);
                editor.apply();

                setVisibilityRadioGroup();
                determineCurrentMeal();
                setRadioButtonClicked();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        /*threeOrFiveSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                isCheckedSwitch = isChecked;

                SharedPreferences.Editor editor = mSharedPreferences.edit();
                editor.putBoolean("threeOrFiveMeals", isChecked);
                editor.apply();
                if (isChecked) {
                    fiveMealsDay.setVisibility(View.VISIBLE);
                    threeMealsDay.setVisibility(View.GONE);

                    *//*countCaloriesInSelectDesiredMealTextView.setText("" + fiveMealsCalories);
                    countProteinsInSelectDesiredMealTextView.setText("" + fiveMealsProteins + "г");
                    countFatsInSelectDesiredMealTextView.setText("" + fiveMealsFats + "г");
                    countCarbohydratesInSelectDesiredMealTextView.setText("" + fiveMealsCarbohydrates + "г");
                    countXETextView.setText("" + fiveMealsXE);*//*
                } else {
                    fiveMealsDay.setVisibility(View.GONE);
                    threeMealsDay.setVisibility(View.VISIBLE);

                    *//*countCaloriesInSelectDesiredMealTextView.setText("" + threeMealsCalories);
                    countProteinsInSelectDesiredMealTextView.setText("" + threeMealsProteins + "г");
                    countFatsInSelectDesiredMealTextView.setText("" + threeMealsFats + "г");
                    countCarbohydratesInSelectDesiredMealTextView.setText("" + threeMealsCarbohydrates + "г");
                    countXETextView.setText("" + threeMealsXE);*//*
                }

                determineCurrentMeal();
                setRadioButtonClicked();
            }
        });
*/
        setVisibilityRadioGroup();

        setupActionBar();

        bindService(new Intent(this, BluetoothLeService.class),                                     // Привязаться к сервису BluetoothLeService
                mLeConnection, Context.BIND_AUTO_CREATE);

    }

    private void setVisibilityRadioGroup() {
        switch(checkedMeal) {
            case 3:
                threeMealsDay.setVisibility(View.VISIBLE);
                fourMealsDay.setVisibility(View.GONE);
                fiveMealsDay.setVisibility(View.GONE);
                sixMealsDay.setVisibility(View.GONE);
                break;
            case 4:
                threeMealsDay.setVisibility(View.GONE);
                fourMealsDay.setVisibility(View.VISIBLE);
                fiveMealsDay.setVisibility(View.GONE);
                sixMealsDay.setVisibility(View.GONE);
                break;
            case 5:
                threeMealsDay.setVisibility(View.GONE);
                fourMealsDay.setVisibility(View.GONE);
                fiveMealsDay.setVisibility(View.VISIBLE);
                sixMealsDay.setVisibility(View.GONE);
                break;
            case 6:
                threeMealsDay.setVisibility(View.GONE);
                fourMealsDay.setVisibility(View.GONE);
                fiveMealsDay.setVisibility(View.GONE);
                sixMealsDay.setVisibility(View.VISIBLE);
                break;
        }
    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setTitle("Желаемый приём пищи");
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setElevation(0);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_host, menu);

        mainMenu = menu;

        SharedPreferences mSharedPreferences = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);

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

            /*if(isCheckedSwitch && tuneFiveMealFragment != null && tuneFiveMealFragment.isAdded()) {
                restartFromTuneFiveMealFragment();
                return true;
            }*/

         /*   if(checkedMeal == 3 && tuneThreeMealFragment != null && tuneThreeMealFragment.isAdded()) {
                restartFromTuneThreeMealFragment();
            } else {
                finish();
            }*/
            if(checkedMeal == 3 && tuneThreeMealFragment != null && tuneThreeMealFragment.isAdded()) {
                restartFromTuneThreeMealFragment();
            } else if(checkedMeal == 4 && tuneFourMealFragment != null && tuneFourMealFragment.isAdded()) {
                restartFromTuneFourMealFragment();
            } else if(checkedMeal == 5 && tuneFiveMealFragment != null && tuneFiveMealFragment.isAdded()) {
                restartFromTuneFiveMealFragment();
            } else if(checkedMeal == 6 && tuneSixMealFragment != null && tuneSixMealFragment.isAdded()) {
                restartFromTuneSixMealFragment();
            } else {
                finish();
            }

            //FoodFragment.currentMeal = chosenMeal;

            return true;
        }

        if(id == R.id.action_device) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            SearchDevicesFragment searchFragment = new SearchDevicesFragment();
            searchDevicesFragment = searchFragment;
            fragmentTransaction.add(R.id.selectDesiredMeal, searchFragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    //обновление данных с фрагмента TuneThreeMealFragment
    public void restartFromTuneThreeMealFragment() {
        breakfastPercentThreeMeal = tuneThreeMealFragment.breakfastPercentThreeMeal;
        lunchPercentThreeMeal = tuneThreeMealFragment.lunchPercentThreeMeal;
        dinnerPercentThreeMeal = tuneThreeMealFragment.dinnerPercentThreeMeal;

        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putInt("breakfastPercentThreeMeal", breakfastPercentThreeMeal);
        editor.putInt("lunchPercentThreeMeal", lunchPercentThreeMeal);
        editor.putInt("dinnerPercentThreeMeal", dinnerPercentThreeMeal);
        editor.apply();

        firstInThreeRadioButton.setText("Завтрак " + breakfastPercentThreeMeal + "%");
        secondInThreeRadioButton.setText("Обед " + lunchPercentThreeMeal + "%");
        thirdInThreeRadioButton.setText("Ужин " + dinnerPercentThreeMeal + "%");

        setRadioButtonClicked();

        tuneThreeMealFragment.getActivity().getSupportFragmentManager().popBackStack();
    }

    //обновление данных с фрагмента TuneFourMealFragment
    public void restartFromTuneFourMealFragment() {
        breakfastPercentFourMeal = tuneFourMealFragment.breakfastPercentFourMeal;
        lunchPercentFourMeal = tuneFourMealFragment.lunchPercentFourMeal;
        dinnerPercentFourMeal = tuneFourMealFragment.dinnerPercentFourMeal;
        snackPercentFourMeal = tuneFourMealFragment.snackPercentFourMeal;

        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putInt("breakfastPercentFourMeal", breakfastPercentFourMeal);
        editor.putInt("lunchPercentFourMeal", lunchPercentFourMeal);
        editor.putInt("dinnerPercentFourMeal", dinnerPercentFourMeal);
        editor.putInt("snackPercentFourMeal", snackPercentFourMeal);
        editor.apply();

        firstInFourRadioButton.setText("Завтрак " + breakfastPercentFourMeal + "%");
        secondInFourRadioButton.setText("Обед " + lunchPercentFourMeal + "%");
        thirdInFourRadioButton.setText("Ужин " + dinnerPercentFourMeal + "%");
        fourthInFourRadioButton.setText("Перекус " + snackPercentFourMeal + "%");

        setRadioButtonClicked();

        tuneFourMealFragment.getActivity().getSupportFragmentManager().popBackStack();
    }


    //обновление данных с фрагмента TuneFiveMealFragment
    public void restartFromTuneFiveMealFragment() {
        breakfastPercentFiveMeal = tuneFiveMealFragment.breakfastPercentFiveMeal;
        firstSnackPercentFiveMeal = tuneFiveMealFragment.firstSnackPercentFiveMeal;
        lunchPercentFiveMeal = tuneFiveMealFragment.lunchPercentFiveMeal;
        secondSnackPercentFiveMeal = tuneFiveMealFragment.secondSnackPercentFiveMeal;
        dinnerPercentFiveMeal = tuneFiveMealFragment.dinnerPercentFiveMeal;

        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putInt("breakfastPercentFiveMeal", breakfastPercentFiveMeal);
        editor.putInt("firstSnackPercentFiveMeal", firstSnackPercentFiveMeal);
        editor.putInt("lunchPercentFiveMeal", lunchPercentFiveMeal);
        editor.putInt("secondSnackPercentFiveMeal", secondSnackPercentFiveMeal);
        editor.putInt("dinnerPercentFiveMeal", dinnerPercentFiveMeal);
        editor.apply();

        firstInFiveRadioButton.setText("Завтрак " + breakfastPercentFiveMeal + "%");
        secondInFiveRadioButton.setText("Первый перекус " + firstSnackPercentFiveMeal + "%");
        thirdInFiveRadioButton.setText("Обед " + lunchPercentFiveMeal + "%");
        fourthInFiveRadioButton.setText("Второй перекус " + secondSnackPercentFiveMeal + "%");
        fifthInFiveRadioButton.setText("Ужин " + dinnerPercentFiveMeal + "%");

        setRadioButtonClicked();

        tuneFiveMealFragment.getActivity().getSupportFragmentManager().popBackStack();
    }

    //обновление данных с фрагмента TuneSixMealFragment
    public void restartFromTuneSixMealFragment() {
        breakfastPercentSixMeal = tuneSixMealFragment.breakfastPercentSixMeal;
        firstSnackPercentSixMeal = tuneSixMealFragment.firstSnackPercentSixMeal;
        lunchPercentSixMeal = tuneSixMealFragment.lunchPercentSixMeal;
        secondSnackPercentSixMeal = tuneSixMealFragment.secondSnackPercentSixMeal;
        dinnerPercentSixMeal = tuneSixMealFragment.dinnerPercentSixMeal;
        thirdSnackPercentSixMeal = tuneSixMealFragment.thirdSnackPercentSixMeal;

        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putInt("breakfastPercentSixMeal", breakfastPercentSixMeal);
        editor.putInt("firstSnackPercentSixMeal", firstSnackPercentSixMeal);
        editor.putInt("lunchPercentFiveMeal", lunchPercentSixMeal);
        editor.putInt("secondSnackPercentFiveMeal", secondSnackPercentSixMeal);
        editor.putInt("dinnerPercentFiveMeal", dinnerPercentSixMeal);
        editor.putInt("thirdSnackPercentFiveMeal", thirdSnackPercentSixMeal);
        editor.apply();

        firstInSixRadioButton.setText("Завтрак " + breakfastPercentSixMeal + "%");
        secondInSixRadioButton.setText("Первый перекус " + firstSnackPercentSixMeal + "%");
        thirdInSixRadioButton.setText("Обед " + lunchPercentSixMeal + "%");
        fourthInSixRadioButton.setText("Второй перекус " + secondSnackPercentSixMeal + "%");
        fifthInSixRadioButton.setText("Ужин " + dinnerPercentSixMeal + "%");
        sixthInSixRadioButton.setText("Третий перекус " + thirdSnackPercentSixMeal + "%");

        setRadioButtonClicked();

        tuneSixMealFragment.getActivity().getSupportFragmentManager().popBackStack();
    }


    @Override
    protected void onResume() {
        super.onResume();

        mSharedPreferences = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString("currentActivityForService", "SelectDesiredMealActivity");

        editor.apply();

        if(mainMenu != null) {
            if (mSharedPreferences.getBoolean("isConnected", false))                                                                        // Если есть привязка к BLE-сервису, то
            {
                mainMenu.findItem(R.id.action_device).setIcon(R.drawable.chew_logo);
            } else {
                mainMenu.findItem(R.id.action_device).setIcon(R.drawable.black_logo);
            }
        }

        // Получение и отображение процентов трёхразового питания
        breakfastPercentThreeMeal = mSharedPreferences.getInt("breakfastPercentThreeMeal", 35);
        lunchPercentThreeMeal = mSharedPreferences.getInt("lunchPercentThreeMeal", 35);
        dinnerPercentThreeMeal = mSharedPreferences.getInt("dinnerPercentThreeMeal", 30);

        firstInThreeRadioButton.setText("Завтрак " + breakfastPercentThreeMeal + "%");
        secondInThreeRadioButton.setText("Обед " + lunchPercentThreeMeal + "%");
        thirdInThreeRadioButton.setText("Ужин " + dinnerPercentThreeMeal + "%");

        // Получение и отображение процентов четырёхразового питания
        breakfastPercentFourMeal = mSharedPreferences.getInt("breakfastPercentFourMeal", 30);
        lunchPercentFourMeal = mSharedPreferences.getInt("lunchPercentFourMeal", 30);
        dinnerPercentFourMeal = mSharedPreferences.getInt("dinnerPercentFourMeal", 30);
        snackPercentFourMeal = mSharedPreferences.getInt("snackPercentFourMeal", 10);

        firstInFourRadioButton.setText("Завтрак " + breakfastPercentFourMeal + "%");
        secondInFourRadioButton.setText("Обед " + lunchPercentFourMeal + "%");
        thirdInFourRadioButton.setText("Ужин " + dinnerPercentFourMeal + "%");
        fourthInFourRadioButton.setText("Перекус " + snackPercentFourMeal + "%");

        // Получение и отображение процентов пятиразового питания
        breakfastPercentFiveMeal = mSharedPreferences.getInt("breakfastPercentFiveMeal", 25);
        firstSnackPercentFiveMeal = mSharedPreferences.getInt("firstSnackPercentFiveMeal", 10);
        lunchPercentFiveMeal = mSharedPreferences.getInt("lunchPercentFiveMeal", 30);
        secondSnackPercentFiveMeal = mSharedPreferences.getInt("secondSnackPercentFiveMeal", 10);
        dinnerPercentFiveMeal = mSharedPreferences.getInt("dinnerPercentFiveMeal", 25);

        firstInFiveRadioButton.setText("Завтрак " + breakfastPercentFiveMeal + "%");
        secondInFiveRadioButton.setText("Первый перекус " + firstSnackPercentFiveMeal + "%");
        thirdInFiveRadioButton.setText("Обед " + lunchPercentFiveMeal + "%");
        fourthInFiveRadioButton.setText("Второй перекус " + secondSnackPercentFiveMeal + "%");
        fifthInFiveRadioButton.setText("Ужин " + dinnerPercentFiveMeal + "%");

        // Получение и отображение процентов шестиразового питания
        breakfastPercentSixMeal = mSharedPreferences.getInt("breakfastPercentSixMeal", 25);
        firstSnackPercentSixMeal = mSharedPreferences.getInt("firstSnackPercentSixMeal", 5);
        lunchPercentSixMeal = mSharedPreferences.getInt("lunchPercentSixMeal", 30);
        secondSnackPercentSixMeal = mSharedPreferences.getInt("secondSnackPercentSixMeal", 5);
        dinnerPercentSixMeal = mSharedPreferences.getInt("dinnerPercentSixMeal", 25);
        thirdSnackPercentSixMeal = mSharedPreferences.getInt("thirdSnackPercentSixMeal", 10);

        firstInSixRadioButton.setText("Завтрак " + breakfastPercentSixMeal + "%");
        secondInSixRadioButton.setText("Первый перекус " + firstSnackPercentSixMeal + "%");
        thirdInSixRadioButton.setText("Обед " + lunchPercentSixMeal + "%");
        fourthInSixRadioButton.setText("Второй перекус " + secondSnackPercentSixMeal + "%");
        fifthInSixRadioButton.setText("Ужин " + dinnerPercentSixMeal + "%");
        sixthInSixRadioButton.setText("Третий перекус " + thirdSnackPercentSixMeal + "%");

        setRadioButtonClicked();
    }

    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        /*String[] fiveMealsArray = getResources().getStringArray(R.array.five_meals_array);
        String[] threeMealsArray = getResources().getStringArray(R.array.three_meals_array);*/

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.firstInThreeRadioButton:
                if (checked) {
                    FoodFragment.currentMealInt = 1;
                    FoodFragment.currentMeal = "Завтрак";
                    setDataCPFCXE(breakfastPercentThreeMeal);
                }
                    break;
            case R.id.secondInThreeRadioButton:
                if (checked) {
                    FoodFragment.currentMealInt = 2;
                    FoodFragment.currentMeal = "Обед";
                    setDataCPFCXE(lunchPercentThreeMeal);
                }
                    break;
            case R.id.thirdInThreeRadioButton:
                if (checked) {
                    FoodFragment.currentMealInt = 3;
                    FoodFragment.currentMeal = "Ужин";
                    setDataCPFCXE(dinnerPercentThreeMeal);
                }
                    break;
            case R.id.firstInFourRadioButton:
                if (checked) {
                    FoodFragment.currentMealInt = 4;
                    FoodFragment.currentMeal = "Завтрак";
                    setDataCPFCXE(breakfastPercentFourMeal);
                }
                    break;
            case R.id.secondInFourRadioButton:
                if (checked) {
                    FoodFragment.currentMealInt = 5;
                    FoodFragment.currentMeal = "Обед";
                    setDataCPFCXE(lunchPercentFourMeal);
                }
                    break;
            case R.id.thirdInFourRadioButton:
                if (checked) {
                    FoodFragment.currentMealInt = 6;
                    FoodFragment.currentMeal = "Ужин";
                    setDataCPFCXE(dinnerPercentFourMeal);
                }
                    break;
            case R.id.fourthInFourRadioButton:
                if (checked) {
                    FoodFragment.currentMealInt = 7;
                    FoodFragment.currentMeal = "Перекус";
                    setDataCPFCXE(snackPercentFourMeal);
                }
                    break;
            case R.id.firstInFiveRadioButton:
                if (checked) {
                    FoodFragment.currentMealInt = 8;
                    FoodFragment.currentMeal = "Завтрак";
                    setDataCPFCXE(breakfastPercentFiveMeal);
                }
                    break;
            case R.id.secondInFiveRadioButton:
                if (checked) {
                    FoodFragment.currentMealInt = 9;
                    FoodFragment.currentMeal = "Первый перекус";
                    setDataCPFCXE(firstSnackPercentFiveMeal);
                }
                    break;
            case R.id.thirdInFiveRadioButton:
                if (checked) {
                    FoodFragment.currentMealInt = 10;
                    FoodFragment.currentMeal = "Обед";
                    setDataCPFCXE(lunchPercentFiveMeal);
                }
                break;
            case R.id.fourthInFiveRadioButton:
                if (checked) {
                    FoodFragment.currentMealInt = 11;
                    FoodFragment.currentMeal = "Второй перекус";
                    setDataCPFCXE(secondSnackPercentFiveMeal);
                }
                break;
            case R.id.fifthInFiveRadioButton:
                if (checked) {
                    FoodFragment.currentMealInt = 12;
                    FoodFragment.currentMeal = "Ужин";
                    setDataCPFCXE(dinnerPercentFiveMeal);
                }
                break;
            case R.id.firstInSixRadioButton:
                if (checked) {
                    FoodFragment.currentMealInt = 13;
                    FoodFragment.currentMeal = "Завтрак";
                    setDataCPFCXE(breakfastPercentSixMeal);
                }
                break;
            case R.id.secondInSixRadioButton:
                if (checked) {
                    FoodFragment.currentMealInt = 14;
                    FoodFragment.currentMeal = "Первый перекус";
                    setDataCPFCXE(firstSnackPercentSixMeal);
                }
                break;
            case R.id.thirdInSixRadioButton:
                if (checked) {
                    FoodFragment.currentMealInt = 15;
                    FoodFragment.currentMeal = "Обед";
                    setDataCPFCXE(lunchPercentSixMeal);
                }
                break;
            case R.id.fourthInSixRadioButton:
                if (checked) {
                    FoodFragment.currentMealInt = 16;
                    FoodFragment.currentMeal = "Второй перекус";
                    setDataCPFCXE(secondSnackPercentSixMeal);
                }
                break;
            case R.id.fifthInSixRadioButton:
                if (checked) {
                    FoodFragment.currentMealInt = 17;
                    FoodFragment.currentMeal = "Ужин";
                    setDataCPFCXE(dinnerPercentSixMeal);
                }
                break;
            case R.id.sixthInSixRadioButton:
                if (checked) {
                    FoodFragment.currentMealInt = 18;
                    FoodFragment.currentMeal = "Третий перекус";
                    setDataCPFCXE(thirdSnackPercentSixMeal);
                }
                break;
        }
    }

    private void setRadioButtonClicked() {
        switch(FoodFragment.currentMealInt) {
            case 1:
                firstInThreeRadioButton.setChecked(true);
                setDataCPFCXE(breakfastPercentThreeMeal);
                break;
            case 2:
                secondInThreeRadioButton.setChecked(true);
                setDataCPFCXE(lunchPercentThreeMeal);
                break;
            case 3:
                thirdInThreeRadioButton.setChecked(true);
                setDataCPFCXE(dinnerPercentThreeMeal);
                break;
            case 4:
                firstInFourRadioButton.setChecked(true);
                setDataCPFCXE(breakfastPercentFourMeal);
                break;
            case 5:
                secondInFourRadioButton.setChecked(true);
                setDataCPFCXE(lunchPercentFourMeal);
                break;
            case 6:
                thirdInFourRadioButton.setChecked(true);
                setDataCPFCXE(dinnerPercentFourMeal);
                break;
            case 7:
                fourthInFourRadioButton.setChecked(true);
                setDataCPFCXE(snackPercentFourMeal);
                break;
            case 8:
                firstInFiveRadioButton.setChecked(true);
                setDataCPFCXE(breakfastPercentFiveMeal);
                break;
            case 9:
                secondInFiveRadioButton.setChecked(true);
                setDataCPFCXE(firstSnackPercentFiveMeal);
                break;
            case 10:
                thirdInFiveRadioButton.setChecked(true);
                setDataCPFCXE(lunchPercentFiveMeal);
                break;
            case 11:
                fourthInFiveRadioButton.setChecked(true);
                setDataCPFCXE(secondSnackPercentFiveMeal);
                break;
            case 12:
                fifthInFiveRadioButton.setChecked(true);
                setDataCPFCXE(dinnerPercentFiveMeal);
                break;
            case 13:
                firstInSixRadioButton.setChecked(true);
                setDataCPFCXE(breakfastPercentSixMeal);
                break;
            case 14:
                secondInSixRadioButton.setChecked(true);
                setDataCPFCXE(firstSnackPercentSixMeal);
                break;
            case 15:
                thirdInSixRadioButton.setChecked(true);
                setDataCPFCXE(lunchPercentSixMeal);
                break;
            case 16:
                fourthInSixRadioButton.setChecked(true);
                setDataCPFCXE(secondSnackPercentSixMeal);
                break;
            case 17:
                fifthInSixRadioButton.setChecked(true);
                setDataCPFCXE(dinnerPercentSixMeal);
                break;
            case 18:
                sixthInSixRadioButton.setChecked(true);
                setDataCPFCXE(thirdSnackPercentSixMeal);
                break;
        }
    }

    // текущий выбранный приём пищи, отображение данных
    private void setDataCPFCXE(int percent) {
        partOfCalories = (int)((float)(calories * percent) / 100);
        partOfProteins = (int)((float)(proteins * percent) / 100);
        partOfFats = (int)((float)(fats * percent) / 100);
        partOfCarbohydrates = (int)((float)(carbohydrates * percent) / 100);
        partOfXE = (int)((float)(10 * percent) / 100);

        countCaloriesInSelectDesiredMealTextView.setText("" + partOfCalories);
        countProteinsInSelectDesiredMealTextView.setText("" + partOfProteins + "г");
        countFatsInSelectDesiredMealTextView.setText("" + partOfFats + "г");
        countCarbohydratesInSelectDesiredMealTextView.setText("" + partOfCarbohydrates + "г");
        countXETextView.setText("" + partOfXE);

        SharedPreferences mSharedPreferences = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putInt("currentCalories", partOfCalories);
        editor.putInt("currentProteins", partOfProteins);
        editor.putInt("currentFats", partOfFats);
        editor.putInt("currentCarbs", partOfCarbohydrates);
        editor.putInt("currentXE", partOfXE);
        editor.apply();
    }

    private void determineCurrentMeal() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR);
        int am = calendar.get(Calendar.AM_PM);

        if(am == 1) {
            hour = hour + 12;
        }

        switch(checkedMeal) {
            case 3:
                if(hour >= 6 && hour < 11) {
                    FoodFragment.currentMealInt = 1;
                } else if(hour >= 11 && hour < 17) {
                    FoodFragment.currentMealInt = 2;
                } else if(hour >= 17 && hour < 21) {
                    FoodFragment.currentMealInt = 3;
                }
                break;
            case 4:
                if(hour >= 6 && hour < 11) {
                    FoodFragment.currentMealInt = 4;
                } else if(hour >= 11 && hour < 17) {
                    FoodFragment.currentMealInt = 5;
                } else if(hour >= 17 && hour < 20) {
                    FoodFragment.currentMealInt = 6;
                } else if(hour >= 20 && hour < 21) {
                    FoodFragment.currentMealInt = 7;
                }
                break;
            case 5:
                if(hour >= 6 && hour < 8) {
                    FoodFragment.currentMealInt = 8;
                } else if(hour >= 8 && hour < 11) {
                    FoodFragment.currentMealInt = 9;
                } else if(hour >= 11 && hour < 14) {
                    FoodFragment.currentMealInt = 10;
                } else if(hour >= 14 && hour < 17) {
                    FoodFragment.currentMealInt = 11;
                } else if(hour >= 17 && hour < 21) {
                    FoodFragment.currentMealInt = 12;
                }
                break;
            case 6:
                if(hour >= 6 && hour < 8) {
                    FoodFragment.currentMealInt = 13;
                } else if(hour >= 8 && hour < 11) {
                    FoodFragment.currentMealInt = 14;
                } else if(hour >= 11 && hour < 14) {
                    FoodFragment.currentMealInt = 15;
                } else if(hour >= 14 && hour < 17) {
                    FoodFragment.currentMealInt = 16;
                } else if(hour >= 17 && hour < 20) {
                    FoodFragment.currentMealInt = 17;
                } else if(hour >= 20 && hour < 21) {
                    FoodFragment.currentMealInt = 18;
                }
                break;
        }
    }
}

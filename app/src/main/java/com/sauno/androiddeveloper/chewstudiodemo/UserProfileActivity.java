package com.sauno.androiddeveloper.chewstudiodemo;

import android.bluetooth.BluetoothAdapter;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.sauno.androiddeveloper.chewstudiodemo.bluetooth.BluetoothLeService;
import com.sauno.androiddeveloper.chewstudiodemo.bluetooth.LeLister;
import com.sauno.androiddeveloper.chewstudiodemo.data.DatabaseCreateHelper;
import com.sauno.androiddeveloper.chewstudiodemo.data.FavoriteDishesDBHelper;
import com.sauno.androiddeveloper.chewstudiodemo.data.UserDBHelper;
import com.sauno.androiddeveloper.chewstudiodemo.dialogFragments.SelectUserDialogFragment;

import java.lang.ref.WeakReference;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class UserProfileActivity extends AppCompatActivity {
    SearchDevicesFragment searchDevicesFragment;
    Menu mainMenu;

    SharedPreferences mSharedPreferences;



    public static final String KEY_COUNT_CALORIES = "countCalories";
    public static final String KEY_COUNT_PROTEINS = "countProteins";
    public static final String KEY_COUNT_FATS = "countFats";
    public static final String KEY_COUNT_CARBOHYDRATES = "countCarbohydrates";
    public static final String KEY_COUNT_XE = "countXE";

    public static String[] userNameArray;
    public static String userNameFromDB;

    TextView userNameTextView;
    TextView userAgeTextView;
    TextView userHeightTextView;
    TextView userWeightTextView;
    TextView imtTextView;
    TextView imtResultTextView;
    TextView countCaloriesInProfileTextView;
    TextView countProteinsProfileTextView;
    TextView countFatsProfileTextView;
    TextView countCarbohydratesProfileTextView;
    TextView countXETextView;


    String userName;
    String userAge;
    String userHeight;
    String userWeight;
    String userSex;
    String userLifestyle;

    boolean observeCalorie, observeProtein, observeFat, observeCarb, observeXE;


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

            mLeService.weakUserProfileActivity = new WeakReference<>(UserProfileActivity.this);       // Создать слабую ссылку для MainActivity

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
                Toast.makeText(UserProfileActivity.this, "BLE not supported on this device",               // Печатаем Toast-сообщение 'BLE not supported on this device'
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
        setContentView(R.layout.activity_user_profile);

        mSharedPreferences = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);

        bindService(new Intent(this, BluetoothLeService.class),                                     // Привязаться к сервису BluetoothLeService
                mLeConnection, Context.BIND_AUTO_CREATE);

        setupActionBar();

        /*SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        userName = sp.getString("userNamePref", "");
        userAge = sp.getString("userAgePref", "");
        userHeight = sp.getString("userStaturePref", "");
        userWeight = sp.getString("userWeightPref", "");
        userSex = sp.getString("userSexPref", "");
*/
        userNameTextView = findViewById(R.id.nameTextView);
        userAgeTextView = findViewById(R.id.ageTextView);
        userHeightTextView = findViewById(R.id.heightTextView);
        userWeightTextView = findViewById(R.id.weightTextView);
        imtTextView = findViewById(R.id.imtTextView);
        imtResultTextView = findViewById(R.id.imtResultTextView);
        countCaloriesInProfileTextView = findViewById(R.id.countCaloriesInProfileTextView);
        countProteinsProfileTextView = findViewById(R.id.countProteinsProfileTextView);
        countFatsProfileTextView = findViewById(R.id.countFatsProfileTextView);
        countCarbohydratesProfileTextView = findViewById(R.id.countCarbohydratesProfileTextView);
        countXETextView = findViewById(R.id.countXETextView);


        Button editButton = findViewById(R.id.editButton);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserProfileActivity.this, UserPreferencesActivity.class);
                startActivity(intent);
            }
        });

        Button changeUserButton = findViewById(R.id.changeUserButton);
        changeUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userNameArray = getUserNames();

                if(userNameArray.length == 0 || userNameArray.length == 1) {
                    Toast.makeText(UserProfileActivity.this, "На вашем устройстве меньше чем два пользователя :)", Toast.LENGTH_LONG).show();

                } else {
                    SelectUserDialogFragment selectUserDialogFragment = new SelectUserDialogFragment();
                    selectUserDialogFragment.show(getSupportFragmentManager(), "dialogSelectUserDialogFragment");
                }
            }
        });

        Button deleteUserProfileButton = findViewById(R.id.deleteUserProfileButton);
        deleteUserProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                choiceDeleteCurrentUser();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString("currentActivityForService", "UserProfileActivity");

        editor.apply();

        Log.d("MyLogUserProfile", "onResume");

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        userName = sp.getString(UserPreferencesActivity.KEY_USER_NAME_PREF, "");
        userAge = sp.getString(UserPreferencesActivity.KEY_USER_AGE_PREF, "");
        userHeight = sp.getString(UserPreferencesActivity.KEY_USER_HEIGHT_PREF, "");
        userWeight = sp.getString(UserPreferencesActivity.KEY_USER_WEIGHT_PREF, "");
        userSex = sp.getString(UserPreferencesActivity.KEY_USER_SEX_PREF, "");
        userLifestyle = sp.getString(UserPreferencesActivity.KEY_USER_LIFESTYLE_PREF, "");

        if(!userName.equals("")) {
            userNameTextView.setText("Имя - " + userName);
        } else {
            userNameTextView.setText("Имя");
        }
        if(!userAge.equals("-1") && !userAge.equals("")) {
            userAgeTextView.setText("Возраст - " + userAge);
        } else {
            userAgeTextView.setText("Возраст");
        }
        if(!userHeight.equals("-1") && !userHeight.equals("")) {
            userHeightTextView.setText("Рост - " + userHeight + " м");
        } else {
            userHeightTextView.setText("Рост");
        }
        if(!userWeight.equals("-1") && !userWeight.equals("")) {
            userWeightTextView.setText("Вес - " + userWeight + " кг");
        } else {
            userWeightTextView.setText("Вес");
        }

        setImt(userHeight, userWeight);

        setNormaKBJU(userWeight, userHeight, userAge, userSex, userLifestyle);

        //Тест
       /* SharedPreferences.Editor editor = sp.edit();
        editor.putString("userNamePref", "Сирожа");
        editor.apply();*/

       //Log.d("MyLogUserProfile", "userName - " + userName);

        observeCalorie = sp.getBoolean(UserPreferencesActivity.KEY_CALORIE_CHECK_BOX_PREF, false);
        observeProtein = sp.getBoolean(UserPreferencesActivity.KEY_PROTEIN_CHECK_BOX_PREF, false);
        observeFat = sp.getBoolean(UserPreferencesActivity.KEY_FAT_CHECK_BOX_PREF, false);
        observeCarb = sp.getBoolean(UserPreferencesActivity.KEY_CARBOHYDRATE_CHECK_BOX_PREF, false);
        observeXE = sp.getBoolean(UserPreferencesActivity.KEY_XE_CHECK_BOX_PREF, false);

        insertNewUser();

        if(mainMenu != null) {
            if (mSharedPreferences.getBoolean("isConnected", false))                                                                        // Если есть привязка к BLE-сервису, то
            {
                mainMenu.findItem(R.id.action_device).setIcon(R.drawable.chew_logo);
            } else {
                mainMenu.findItem(R.id.action_device).setIcon(R.drawable.black_logo);
            }
        }
    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setTitle("Анкета пользователя");
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
            fragmentTransaction.add(R.id.userProfileActivity, searchFragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //установление индекса массы тела
    private void setImt(String height, String weight) {
        if(!height.equals("-1") && !weight.equals("-1") && !height.equals("") && !weight.equals("")) {
            float imt;


            imt = Float.parseFloat(weight) / (Float.parseFloat(height) * Float.parseFloat(height));

            Locale locale = new Locale("en", "US");
            String pattern = "###.##";
            DecimalFormat decimalFormat = (DecimalFormat) NumberFormat.getNumberInstance(locale);
            decimalFormat.applyPattern(pattern);
            String formatImt = decimalFormat.format(imt);

            String imtString = "Индекс массы тела - " + formatImt;

            imtTextView.setText(imtString);

            if (imt >= 40) {
                imtResultTextView.setText(" III ст. ожирения");
            } else if ((imt<40)&&(imt>=35)) {
                imtResultTextView.setText(" II ст. ожирения");
            } else if((imt < 35) && (imt >= 30)) {
                imtResultTextView.setText(" I ст. ожирения");
            } else if ((imt < 30) && (imt >=25)) {
                imtResultTextView.setText(" Избыточная масса тела");
            } else if ((imt < 25) && (imt >=18.5)) {
                imtResultTextView.setText(" Норма");
            } else if ((imt < 18.5) && (imt >= 16)) {
                imtResultTextView.setText(" Дифицит массы тела");
            } else if (imt < 16) {
                imtResultTextView.setText(" Выраженный дифицит массы тела");
            }
        } else {
            imtTextView.setText("");
            imtResultTextView.setText("");
        }

    }

    //установление нормы КБЖУХ
    private void setNormaKBJU(String weight, String height, String age, String sex, String lifestyle) {
        SharedPreferences mSharedPreferences = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mSharedPreferences.edit();

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        String wouldYouLike = sp.getString(UserPreferencesActivity.KEY_USER_WOULD_YOU_LIKE_PREF, "");

        float wylFloat = 1f;

        if(wouldYouLike.equals("Уменьшить вес")) {
            wylFloat = 0.8f;
        }
        if(wouldYouLike.equals("Прибавить вес")) {
            wylFloat = 1.2f;
        }



        Log.d("MyLogUserProfile", "wylFloat = " + wylFloat);

        if(!sex.equals("") && sex.equals("Женщина") && !height.equals("-1") && !weight.equals("-1") && !height.equals("") && !weight.equals("")
                && !lifestyle.equals("-1") && !lifestyle.equals("")){
            String ageNumber = age.substring(0,2);

            double bmr = 655.1 + (9.6 * Double.parseDouble(weight)) + (1.85 * Double.parseDouble(height) * 100) - (4.68 * Double.parseDouble(ageNumber));
            int calories = (int)(bmr * Float.parseFloat(lifestyle));

            int proteins = (int)(calories * 0.25 / 4);
            int fats = (int)(calories * 0.15 / 9);
            int carb = (int)(calories * 0.60 / 4);

            //УТОЧНИТЬ У НАСТИ ФОРМУЛУ
            int xe = 10;

            calories = (int)(calories*wylFloat);
            proteins = (int)(proteins*wylFloat);
            fats = (int)(fats*wylFloat);
            carb = (int)(carb*wylFloat);
            xe = (int)(xe*wylFloat);


            countCaloriesInProfileTextView.setText("" + calories);
            countProteinsProfileTextView.setText("" + proteins + "г");
            countFatsProfileTextView.setText("" + fats + "г");
            countCarbohydratesProfileTextView.setText("" + carb + "г");
            countXETextView.setText("" + xe);

            editor.putInt(KEY_COUNT_CALORIES, calories);
            editor.putInt(KEY_COUNT_PROTEINS, proteins);
            editor.putInt(KEY_COUNT_FATS, fats);
            editor.putInt(KEY_COUNT_CARBOHYDRATES, carb);
            editor.putInt(KEY_COUNT_XE, xe);

        } else if(!sex.equals("") && sex.equals("Мужчина") && !height.equals("-1") && !weight.equals("-1") && !height.equals("") && !weight.equals("")
                && !lifestyle.equals("-1") && !lifestyle.equals("")) {
            String ageNumber = age.substring(0,2);

            double bmr = 66.47 + (13.75 * Double.parseDouble(weight)) + (5.0 * Double.parseDouble(height) * 100) - (6.74 * Double.parseDouble(ageNumber));
            int calories = (int)(bmr * Float.parseFloat(lifestyle));


            int proteins = (int)(calories * 0.25 / 4);
            int fats = (int)(calories * 0.15 / 9);
            int carb = (int)(calories * 0.60 / 4);
            int xe = 10;

            calories = (int)(calories*wylFloat);
            proteins = (int)(proteins*wylFloat);
            fats = (int)(fats*wylFloat);
            carb = (int)(carb*wylFloat);
            xe = (int)(xe*wylFloat);

            countCaloriesInProfileTextView.setText("" + calories);
            countProteinsProfileTextView.setText("" + proteins + "г");
            countFatsProfileTextView.setText("" + fats + "г");
            countCarbohydratesProfileTextView.setText("" + carb + "г");
            countXETextView.setText("" + xe);

            editor.putInt(KEY_COUNT_CALORIES, calories);
            editor.putInt(KEY_COUNT_PROTEINS, proteins);
            editor.putInt(KEY_COUNT_FATS, fats);
            editor.putInt(KEY_COUNT_CARBOHYDRATES, carb);
            editor.putInt(KEY_COUNT_XE, xe);

        } else {
            countCaloriesInProfileTextView.setText("");
            countProteinsProfileTextView.setText("");
            countFatsProfileTextView.setText("");
            countCarbohydratesProfileTextView.setText("");
            countXETextView.setText("");


            editor.putInt(KEY_COUNT_CALORIES, 0);
            editor.putInt(KEY_COUNT_PROTEINS, 0);
            editor.putInt(KEY_COUNT_FATS, 0);
            editor.putInt(KEY_COUNT_CARBOHYDRATES, 0);
            editor.putInt(KEY_COUNT_XE, 0);

            Toast.makeText(this, "Для отображения всех данных введите: пол, возраст, рост, вес, образ жизни", Toast.LENGTH_SHORT).show();
        }
        editor.apply();
    }

    private String[] getUserNames() {
        DatabaseCreateHelper databaseCreateHelper = new DatabaseCreateHelper(this);
        SQLiteDatabase db;
        try {
            db = databaseCreateHelper.getWritableDatabase();
        }
        catch (SQLiteException ex){
            db = databaseCreateHelper.getReadableDatabase();
        }

        String[] projection = {
                UserDBHelper.COLUMN_NAME
        };

        Cursor cursor = db.query(
                true,
                UserDBHelper.TABLE,
                projection,
                null,
                null,
                UserDBHelper.COLUMN_NAME,
                null,
                null,
                null
        );

        List<String> users = new ArrayList<>();


        if (cursor.moveToFirst()) {

            do {
                String user = cursor.getString(cursor.getColumnIndexOrThrow(UserDBHelper.COLUMN_NAME));
                users.add(user);

                //Log.i("MyLog",category + "\n");

            }while (cursor.moveToNext());

        }

        String[] usersArray = users.toArray(new String[users.size()]);

        cursor.close();
        db.close();

        return usersArray;
    }

    public void setUserFromDB(String name) {

        DatabaseCreateHelper databaseCreateHelper = new DatabaseCreateHelper(this);
        SQLiteDatabase db;
        try {
            db = databaseCreateHelper.getWritableDatabase();
        }
        catch (SQLiteException ex){
            db = databaseCreateHelper.getReadableDatabase();
        }

        String[] projection = {
                UserDBHelper.COLUMN_SEX,
                UserDBHelper.COLUMN_AGE,
                UserDBHelper.COLUMN_HEIGHT,
                UserDBHelper.COLUMN_WEIGHT,
                UserDBHelper.COLUMN_BLOOD_GROUP,
                UserDBHelper.COLUMN_TYPE_OF_FOOD,
                UserDBHelper.COLUMN_SPECIFICITY_1,
                UserDBHelper.COLUMN_SPECIFICITY_2,
                UserDBHelper.COLUMN_SPECIFICITY_3,
                UserDBHelper.COLUMN_SPECIFICITY_4,
                UserDBHelper.COLUMN_SPECIFICITY_5,
                UserDBHelper.COLUMN_LIFESTYLE,
                UserDBHelper.COLUMN_YOUR_WISH,
                UserDBHelper.COLUMN_CALORIE,
                UserDBHelper.COLUMN_PROTEIN,
                UserDBHelper.COLUMN_FAT,
                UserDBHelper.COLUMN_CARBOHYDRATE
        };

        String selection = UserDBHelper.COLUMN_NAME + " = ?";
        String[] selectionArgs = {name};

        Cursor cursor = db.query(
                true,
                UserDBHelper.TABLE,
                projection,
                selection,
                selectionArgs,
                UserDBHelper.COLUMN_NAME,
                null,
                null,
                null
        );

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sp.edit();

        if (cursor.moveToFirst()) {
            editor.putString(UserPreferencesActivity.KEY_USER_NAME_PREF, name);
            editor.putString(UserPreferencesActivity.KEY_USER_SEX_PREF, cursor.getString(cursor.getColumnIndexOrThrow(UserDBHelper.COLUMN_SEX)));
            editor.putString(UserPreferencesActivity.KEY_USER_AGE_PREF, cursor.getString(cursor.getColumnIndexOrThrow(UserDBHelper.COLUMN_AGE)));
            editor.putString(UserPreferencesActivity.KEY_USER_HEIGHT_PREF, cursor.getString(cursor.getColumnIndexOrThrow(UserDBHelper.COLUMN_HEIGHT)));
            editor.putString(UserPreferencesActivity.KEY_USER_WEIGHT_PREF, cursor.getString(cursor.getColumnIndexOrThrow(UserDBHelper.COLUMN_WEIGHT)));
            editor.putString(UserPreferencesActivity.KEY_USER_BLOOD_GROUPE_PREF, cursor.getString(cursor.getColumnIndexOrThrow(UserDBHelper.COLUMN_BLOOD_GROUP)));
            //editor.putString(UserPreferencesActivity.KEY_USER_TYPE_OF_FOOD_PREF, cursor.getString(cursor.getColumnIndexOrThrow(UserDBHelper.COLUMN_TYPE_OF_FOOD)));
            editor.putString(UserPreferencesActivity.KEY_USER_CHARACTERISTIC_ONE_PREF, cursor.getString(cursor.getColumnIndexOrThrow(UserDBHelper.COLUMN_SPECIFICITY_1)));
            editor.putString(UserPreferencesActivity.KEY_USER_CHARACTERISTIC_TWO_PREF, cursor.getString(cursor.getColumnIndexOrThrow(UserDBHelper.COLUMN_SPECIFICITY_2)));
            editor.putString(UserPreferencesActivity.KEY_USER_CHARACTERISTIC_THREE_PREF, cursor.getString(cursor.getColumnIndexOrThrow(UserDBHelper.COLUMN_SPECIFICITY_3)));
            editor.putString(UserPreferencesActivity.KEY_USER_CHARACTERISTIC_FOUR_PREF, cursor.getString(cursor.getColumnIndexOrThrow(UserDBHelper.COLUMN_SPECIFICITY_4)));
            editor.putString(UserPreferencesActivity.KEY_USER_CHARACTERISTIC_FIVE_PREF, cursor.getString(cursor.getColumnIndexOrThrow(UserDBHelper.COLUMN_SPECIFICITY_5)));
            editor.putString(UserPreferencesActivity.KEY_USER_LIFESTYLE_PREF, cursor.getString(cursor.getColumnIndexOrThrow(UserDBHelper.COLUMN_LIFESTYLE)));
            editor.putString(UserPreferencesActivity.KEY_USER_WOULD_YOU_LIKE_PREF, cursor.getString(cursor.getColumnIndexOrThrow(UserDBHelper.COLUMN_YOUR_WISH)));
            editor.putBoolean(UserPreferencesActivity.KEY_CALORIE_CHECK_BOX_PREF, cursor.getInt(cursor.getColumnIndexOrThrow(UserDBHelper.COLUMN_CALORIE)) == 1);
            editor.putBoolean(UserPreferencesActivity.KEY_PROTEIN_CHECK_BOX_PREF, cursor.getInt(cursor.getColumnIndexOrThrow(UserDBHelper.COLUMN_PROTEIN)) == 1);
            editor.putBoolean(UserPreferencesActivity.KEY_FAT_CHECK_BOX_PREF, cursor.getInt(cursor.getColumnIndexOrThrow(UserDBHelper.COLUMN_FAT)) == 1);
            editor.putBoolean(UserPreferencesActivity.KEY_CARBOHYDRATE_CHECK_BOX_PREF, cursor.getInt(cursor.getColumnIndexOrThrow(UserDBHelper.COLUMN_CARBOHYDRATE)) == 1);


        }
        editor.putString(UserPreferencesActivity.KEY_USER_TYPE_OF_FOOD_VEGETARIAN_PREF, "0");
        editor.putString(UserPreferencesActivity.KEY_USER_TYPE_OF_FOOD_LENTEN_PREF, "0");
        editor.apply();

        userName = sp.getString(UserPreferencesActivity.KEY_USER_NAME_PREF, "");
        userAge = sp.getString(UserPreferencesActivity.KEY_USER_AGE_PREF, "");
        userHeight = sp.getString(UserPreferencesActivity.KEY_USER_HEIGHT_PREF, "");
        userWeight = sp.getString(UserPreferencesActivity.KEY_USER_WEIGHT_PREF, "");
        userSex = sp.getString(UserPreferencesActivity.KEY_USER_SEX_PREF, "");
        userLifestyle = sp.getString(UserPreferencesActivity.KEY_USER_LIFESTYLE_PREF, "");

        if(!userName.equals("")) {
            userNameTextView.setText("Имя - " + userName);
        } else {
            userNameTextView.setText("Имя");
        }
        if(!userAge.equals("-1") && !userAge.equals("")) {
            userAgeTextView.setText("Возраст - " + userAge);
        } else {
            userAgeTextView.setText("Возраст");
        }
        if(!userHeight.equals("-1") && !userHeight.equals("")) {
            userHeightTextView.setText("Рост - " + userHeight + " м");
        } else {
            userHeightTextView.setText("Рост");
        }
        if(!userWeight.equals("-1") && !userWeight.equals("")) {
            userWeightTextView.setText("Вес - " + userWeight + " кг");
        } else {
            userWeightTextView.setText("Вес");
        }

        setImt(userHeight, userWeight);

        setNormaKBJU(userWeight, userHeight, userAge, userSex, userLifestyle);

        //Добавить в базу
        //public static final String KEY_USER_METHOD_OF_NUTRITION_PREF = "userMethodOfNutritionPref";
        //public static final String KEY_USER_COMPATIBILITY_TYPE_OF_FOOD_PREF = "userCompatibilityTypeOfFoodPref";


       /* List<String> users = new ArrayList<>();


        if (cursor.moveToFirst()) {

            do {
                String user = cursor.getString(cursor.getColumnIndexOrThrow(UserDBHelper.COLUMN_NAME));
                users.add(user);

                //Log.i("MyLog",category + "\n");

            }while (cursor.moveToNext());

        }

        String[] usersArray = users.toArray(new String[users.size()]);*/

        cursor.close();
        db.close();

    }

    private void choiceDeleteCurrentUser() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AppDialog);
        builder.setTitle("Вы хотите удалить эту анкету?");
        builder.setPositiveButton("Да", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int n) {
                deleteCurrentUser();
            }
        });
        builder.setNegativeButton("Нет", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    private void deleteCurrentUser() {
        DatabaseCreateHelper databaseCreateHelper = new DatabaseCreateHelper(this);
        SQLiteDatabase db;
        try {
            db = databaseCreateHelper.getWritableDatabase();
        }
        catch (SQLiteException ex){
            db = databaseCreateHelper.getReadableDatabase();
        }

        String selection = UserDBHelper.COLUMN_NAME + " = ?";
        String[] selectionArgs = {userName};

        db.delete(
                UserDBHelper.TABLE,
                selection,
                selectionArgs
        );
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sp.edit();

        editor.putString(UserPreferencesActivity.KEY_USER_NAME_PREF, "");
        editor.putString(UserPreferencesActivity.KEY_USER_SEX_PREF, "");
        editor.putString(UserPreferencesActivity.KEY_USER_AGE_PREF, "");
        editor.putString(UserPreferencesActivity.KEY_USER_HEIGHT_PREF, "");
        editor.putString(UserPreferencesActivity.KEY_USER_WEIGHT_PREF, "");
        editor.putString(UserPreferencesActivity.KEY_USER_BLOOD_GROUPE_PREF, "");
        editor.putString(UserPreferencesActivity.KEY_USER_METHOD_OF_NUTRITION_PREF, "");
        editor.putString(UserPreferencesActivity.KEY_USER_TYPE_OF_FOOD_VEGETARIAN_PREF, "0");
        editor.putString(UserPreferencesActivity.KEY_USER_TYPE_OF_FOOD_LENTEN_PREF, "0");
        editor.putString(UserPreferencesActivity.KEY_USER_COMPATIBILITY_TYPE_OF_FOOD_PREF, "0");
        editor.putString(UserPreferencesActivity.KEY_USER_CHARACTERISTIC_ONE_PREF, "0");
        editor.putString(UserPreferencesActivity.KEY_USER_CHARACTERISTIC_TWO_PREF, "0");
        editor.putString(UserPreferencesActivity.KEY_USER_CHARACTERISTIC_THREE_PREF, "0");
        editor.putString(UserPreferencesActivity.KEY_USER_CHARACTERISTIC_FOUR_PREF, "0");
        editor.putString(UserPreferencesActivity.KEY_USER_CHARACTERISTIC_FIVE_PREF, "0");
        editor.putString(UserPreferencesActivity.KEY_USER_LIFESTYLE_PREF, "");
        editor.putString(UserPreferencesActivity.KEY_USER_WOULD_YOU_LIKE_PREF, "");
        editor.putBoolean(UserPreferencesActivity.KEY_CALORIE_CHECK_BOX_PREF, false);
        editor.putBoolean(UserPreferencesActivity.KEY_PROTEIN_CHECK_BOX_PREF, false);
        editor.putBoolean(UserPreferencesActivity.KEY_FAT_CHECK_BOX_PREF, false);
        editor.putBoolean(UserPreferencesActivity.KEY_CARBOHYDRATE_CHECK_BOX_PREF, false);
        editor.apply();

        userNameTextView.setText("Имя");
        userAgeTextView.setText("Возраст");
        userHeightTextView.setText("Рост");
        userWeightTextView.setText("Вес");

        setImt("", "");
        setNormaKBJU("", "", "", "", "");

        db.close();
    }

    private void insertNewUser() {
        String[] namesUsersInDB = getUserNames();
        boolean isExistNameInBD = false;
        for(String name : namesUsersInDB) {
            if(name.equals(userName)) {
                isExistNameInBD = true;
            }
        }

        if(!isExistNameInBD && !userName.equals("")) {
            int idRowFavoriteDishes = getIdForNewRowFavoriteDish();

            DatabaseCreateHelper databaseCreateHelper = new DatabaseCreateHelper(this);
            SQLiteDatabase db = databaseCreateHelper.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(UserDBHelper.COLUMN_NAME, userName);
            values.put(UserDBHelper.COLUMN_AGE, userAge);
            values.put(UserDBHelper.COLUMN_HEIGHT, userHeight);
            values.put(UserDBHelper.COLUMN_WEIGHT, userWeight);
            values.put(UserDBHelper.COLUMN_SEX, userSex);
            values.put(UserDBHelper.COLUMN_LIFESTYLE, userLifestyle);
            values.put(UserDBHelper.COLUMN_CALORIE, observeCalorie);
            values.put(UserDBHelper.COLUMN_PROTEIN, observeProtein);
            values.put(UserDBHelper.COLUMN_FAT, observeFat);
            values.put(UserDBHelper.COLUMN_CARBOHYDRATE, observeCarb);
            values.put(UserDBHelper.COLUMN_FAVORITE_DISH, idRowFavoriteDishes);

            db.insert(UserDBHelper.TABLE, null, values);

            db.close();
        }
    }

    private int getIdForNewRowFavoriteDish() {
        int idForNewRow = 0;

        DatabaseCreateHelper databaseCreateHelper = new DatabaseCreateHelper(this);
        SQLiteDatabase db = databaseCreateHelper.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(FavoriteDishesDBHelper.COLUMN_DISH_1, 0);

        idForNewRow = (int)db.insert(FavoriteDishesDBHelper.TABLE, null, values);

        db.close();

        Log.i("MyLogUserProfile", "Новая строка в таблице Любимые блюда - " + idForNewRow);

        return idForNewRow;
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();

        if (mLeServiceBound)                                                                        // Если есть привязка к BLE-сервису, то
        {
            mLeService = null;                                                                      // Освободить ссылку на сервис
        }

        Log.w("BLE01", "On destroy");
    }


}

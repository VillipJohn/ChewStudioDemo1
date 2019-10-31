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
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.sauno.androiddeveloper.chewstudiodemo.bluetooth.BluetoothLeService;
import com.sauno.androiddeveloper.chewstudiodemo.data.DatabaseCreateHelper;
import com.sauno.androiddeveloper.chewstudiodemo.data.DishDBHelper;
import com.sauno.androiddeveloper.chewstudiodemo.model.DishOrderItem;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class Dish18Activity extends AppCompatActivity {
    SearchDevicesFragment searchDevicesFragment;
    Menu mainMenu;

    SharedPreferences mSharedPreferences;

    public static List<DishOrderItem> dishOrderList;

    public static int countIngestion = 0;

    //счётчик колличества блюд
    static int count = 0;

    int percentageEaten = 100;
    ArrayList<Integer> percentArrayList = new ArrayList<>();

    static final int PICK_PERSENTAGE_REQUEST = 1;  // The request code

    TextView percentageEatenTextView;

    TextView dishNameTextView;
    ImageView dishImageView;

    //TextView percentageEatenTextView;

    TextView caloriesTextView;
    TextView proteinsTextView;
    TextView fatsTextView;
    TextView carbsTextView;
    TextView xeTextView;

    String dishName;
    int calories;
    int proteins;
    int fats;
    int carbs;
    int xe;
    String globalCategory;

    EditText editTextMaxChew;

    Boolean fEnableBTRequest = false;

    BluetoothLeService mLeService = null;                                                           // Указатель на экземпляр сервиса BluetoothLeService
    boolean            mLeServiceBound = false;                                                     // Флаг наличия привязки к сервису

    static final int REQUEST_ENABLE_BT = 1;

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

            mLeService.weakDish18Activity = new WeakReference<>(Dish18Activity.this);       // Создать слабую ссылку для MainActivity

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

            //updateConnectStatus(mLeService.getConnectStatus());Восстановить сообщение о соединении

            SharedPreferences mSharedPreferences = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
            int maxChewCounter = mSharedPreferences.getInt("maxChewCounter", 17);

            updateMaxChew(maxChewCounter);                                               // Восстановить счетчик максимального числа жеваний

//            mLeService.testMethod();                                                                // Запустить тестовый метод сервиса

        }

        @Override
        public void onServiceDisconnected(ComponentName arg0)                                       // При разьединении с сервисом
        {
            //statusUpdate("LeService Disonnected");
            mLeServiceBound = false;                                                                // Снять флаг привязки к сервису
            mLeService = null;                                                                      // Освободить ссылку на сервис
        }
    };

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


    public void updateScanProgress(int progress)
    {
        searchDevicesFragment.pbProgressBar.setProgress(progress);                                        // Обновить ProgressBar процесса сканирования BLE-устройств

        Log.i("MyLogMainActivity", "\nprogress  -  " + progress);
//        statusUpdate("Progress: " + progress);
        if(progress == 99) {
            Toast.makeText(this, "Поиск завершён", Toast.LENGTH_SHORT).show();
        }

    }

    public void updateLeList(String name, String address, int rssi)
    {
        searchDevicesFragment.setUpdate(name);
        onConnected(address);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dish18);

        mSharedPreferences = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);

        dishOrderList = ChoiceOfDishesActivity.dishOrderList;


        bindService(new Intent(this, BluetoothLeService.class),                        // Привязаться к сервису BluetoothLeService
                mLeConnection, Context.BIND_AUTO_CREATE);

        dishName = getIntent().getStringExtra("dishName");
        calories = getIntent().getIntExtra("calories", 0);
        proteins = getIntent().getIntExtra("proteins", 0);
        fats = getIntent().getIntExtra("fats", 0);
        carbs = getIntent().getIntExtra("carbs", 0);
        xe = getIntent().getIntExtra("xe", 0);

        getGlobalCategoryOfDishFromDB();

        dishNameTextView = findViewById(R.id.dishNameTextView);
        dishImageView = findViewById(R.id.dishImageView);

        //percentageEatenTextView = findViewById(R.id.percentageEatenTextView);

        caloriesTextView = findViewById(R.id.countCaloriesTextView);
        proteinsTextView = findViewById(R.id.countProteinsTextView);
        fatsTextView = findViewById(R.id.countFatsTextView);
        carbsTextView = findViewById(R.id.countCarbsTextView);
        xeTextView = findViewById(R.id.countXETextView);

        Button showChewGraph = findViewById(R.id.showChewGraph);
        Button nextDishButton = findViewById(R.id.nextDishButton);

        showChewGraph.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), ChewingProcessActivity.class);
                startActivity(intent);
            }
        });

        nextDishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                count++;

                if(dishOrderList.size() != count) {
                    dishName = dishOrderList.get(count).getDishName();
                    calories = dishOrderList.get(count).getCalories().intValue();
                    proteins = dishOrderList.get(count).getProteins().intValue();
                    fats = dishOrderList.get(count).getFats().intValue();
                    carbs = dishOrderList.get(count).getCarbs().intValue();
                    xe = dishOrderList.get(count).getXe().intValue();

                    percentArrayList.add(percentageEaten);

                    getGlobalCategoryOfDishFromDB();

                    setData();
                } else {
                    count = 0;
                    percentArrayList.add(percentageEaten);

                    ArrayList<Integer> idesArrayList = new ArrayList<>();
                    for(int n = 0; n < dishOrderList.size(); n++) {
                        idesArrayList.add(dishOrderList.get(n).getIdDish());
                    }

                    ArrayList<String> dishNameArrayList = new ArrayList<>();
                    for(int n = 0; n < dishOrderList.size(); n++) {
                        dishNameArrayList.add(dishOrderList.get(n).getDishName());
                    }

                    ArrayList<Integer> caloriesArrayList = new ArrayList<>();
                    for(int n = 0; n < dishOrderList.size(); n++) {
                        caloriesArrayList.add(dishOrderList.get(n).getCalories().intValue());
                    }

                    ArrayList<Integer> proteinsArrayList = new ArrayList<>();
                    for(int n = 0; n < dishOrderList.size(); n++) {
                        proteinsArrayList.add(dishOrderList.get(n).getProteins().intValue());
                    }

                    ArrayList<Integer> fatsArrayList = new ArrayList<>();
                    for(int n = 0; n < dishOrderList.size(); n++) {
                        fatsArrayList.add(dishOrderList.get(n).getFats().intValue());
                    }

                    ArrayList<Integer> carbsArrayList = new ArrayList<>();
                    for(int n = 0; n < dishOrderList.size(); n++) {
                        carbsArrayList.add(dishOrderList.get(n).getCarbs().intValue());
                    }

                    ArrayList<Integer> xeArrayList = new ArrayList<>();
                    for(int n = 0; n < dishOrderList.size(); n++) {
                        xeArrayList.add(dishOrderList.get(n).getXe().intValue());
                    }

                    ArrayList<String> quantityArrayList = new ArrayList<>();
                    for(int n = 0; n < dishOrderList.size(); n++) {
                        quantityArrayList.add("" + dishOrderList.get(n).getQuantityDishes().floatValue());
                    }


                    Intent intent = new Intent(view.getContext(), ResultsOfEatingActivity.class);
                    intent.putIntegerArrayListExtra("idesArrayList", idesArrayList);
                    intent.putStringArrayListExtra("dishNameArrayList", dishNameArrayList);
                    intent.putIntegerArrayListExtra("caloriesArrayList", caloriesArrayList);
                    intent.putIntegerArrayListExtra("proteinsArrayList", proteinsArrayList);
                    intent.putIntegerArrayListExtra("fatsArrayList", fatsArrayList);
                    intent.putIntegerArrayListExtra("carbsArrayList", carbsArrayList);
                    intent.putIntegerArrayListExtra("xeArrayList", xeArrayList);
                    intent.putIntegerArrayListExtra("percentArrayList", percentArrayList);
                    intent.putStringArrayListExtra("quantityArrayList", quantityArrayList);
                    startActivityForResult(intent, PICK_PERSENTAGE_REQUEST);
                }

            }
        });

        editTextMaxChew = findViewById(R.id.maxChewEditText);
        //editTextMaxChew.setText("17");

        editTextMaxChew.setOnKeyListener(new View.OnKeyListener()
        {
            public boolean onKey(View v, int keyCode, KeyEvent event)
            {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER))
                {

                    int maxChew = Integer.parseInt(editTextMaxChew.getText().toString());           // сохраняем текст, введенный до нажатия Enter в переменную

//                    statusUpdate("MaxChew: " + maxChew);

                    if (mLeServiceBound)                                                            // Если есть привязка к BLE-сервису, то
                    {
                        mLeService.sendMaxChew(maxChew);                                            // Записать новое значение максимального числа жеваний на удаленное устройство

                        SharedPreferences mSharedPreferences = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = mSharedPreferences.edit();
                        editor.putInt("maxChewCounter", maxChew);
                        editor.apply();
                    }

                    return true;
                }
                return false;
            }
        });

        percentageEatenTextView = findViewById(R.id.percentageEatenTextView);
        ImageView minusImageView = findViewById(R.id.minusImageView);
        ImageView plusImageView = findViewById(R.id.plusImageView);

        minusImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(percentageEaten != 0) {
                    percentageEaten = percentageEaten-10;
                    percentageEatenTextView.setText(percentageEaten + "%");
                }
            }
        });

        plusImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(percentageEaten != 100) {
                    percentageEaten = percentageEaten+10;
                    percentageEatenTextView.setText(percentageEaten + "%");
                }
            }
        });


        setupActionBar();

        setData();
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();

        count--;

        if(count > -1) {
            dishName = dishOrderList.get(count).getDishName();
            calories = dishOrderList.get(count).getCalories().intValue();
            proteins = dishOrderList.get(count).getProteins().intValue();
            fats = dishOrderList.get(count).getFats().intValue();
            carbs = dishOrderList.get(count).getCarbs().intValue();
            xe = dishOrderList.get(count).getXe().intValue();

            getGlobalCategoryOfDishFromDB();
            setImage();

            percentArrayList.add(percentageEaten);

            dishNameTextView.setText(dishName);
            caloriesTextView.setText("" + calories);
            proteinsTextView.setText("" + proteins);
            fatsTextView.setText("" + fats);
            carbsTextView.setText("" + carbs);
            xeTextView.setText("" + xe);

            percentageEatenTextView.setText(percentArrayList.get(count) + "%");

        } else {
            count = 0;

            finish();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Log.i("MyLogDish18Activity", "Сработало");
            percentArrayList = data.getIntegerArrayListExtra("percentArrayList");
            //data.getIntExtra("percentArrayList", Color.WHITE);

            count = percentArrayList.size() - 1;

            dishName = dishOrderList.get(count).getDishName();
            calories = dishOrderList.get(count).getCalories().intValue();
            proteins = dishOrderList.get(count).getProteins().intValue();
            fats = dishOrderList.get(count).getFats().intValue();
            carbs = dishOrderList.get(count).getCarbs().intValue();
            xe = dishOrderList.get(count).getXe().intValue();

            getGlobalCategoryOfDishFromDB();
            setImage();

            dishNameTextView.setText(dishName);
            caloriesTextView.setText("" + calories);
            proteinsTextView.setText("" + proteins);
            fatsTextView.setText("" + fats);
            carbsTextView.setText("" + carbs);
            xeTextView.setText("" + xe);

            percentageEatenTextView.setText(percentArrayList.get(count) + "%");
        }

        if (requestCode == PICK_PERSENTAGE_REQUEST) {

        }
    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setTitle("Блюдо");
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
            fragmentTransaction.add(R.id.dish18Activity, searchFragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString("currentActivityForService", "Dish18Activity");

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

    private void getGlobalCategoryOfDishFromDB() {
        DatabaseCreateHelper databaseCreateHelper = new DatabaseCreateHelper(getApplicationContext());
        SQLiteDatabase db;
        try {
            db = databaseCreateHelper.getWritableDatabase();
        }
        catch (SQLiteException ex){
            db = databaseCreateHelper.getReadableDatabase();
        }

        String[] projection = {
                DishDBHelper.COLUMN_GLOBAL_CATEGORY
        };

        // Filter results WHERE "title" = 'My Title'
        String selection = DishDBHelper.COLUMN_DESCRIPTION + " = ?";
        String[] selectionArgs = {dishName};


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
            globalCategory = cursor.getString(cursor.getColumnIndexOrThrow(DishDBHelper.COLUMN_GLOBAL_CATEGORY));
        }

        db.close();
        cursor.close();
    }

    private void setData() {
        setImage();
        dishNameTextView.setText(dishName);
        caloriesTextView.setText("" + calories);
        proteinsTextView.setText("" + proteins);
        fatsTextView.setText("" + fats);
        carbsTextView.setText("" + carbs);
        xeTextView.setText("" + xe);

        percentageEaten = 100;
        percentageEatenTextView.setText(percentageEaten + "%");
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
        if(chewCounter == 2) {
            countIngestion++;
        }
        //percentageEatenTextView.setText(msg);                                                                  // Обновить текстовое поле счетчика жеваний
    }

    //**********************************************************************************************
    //
    //             Публичная функция updateMaxChew() для сервиса BluetoothLeService
    //                        Обновление поля максимального числа жеваний
    //
    //  Замечание: Запускается уже сразу в UI-потоке.
    //
    //  Замечание: Так же может вызываться из MainActivity для восстановления  состояния
    //             графического интерфейса
    //
    //  Входные данные: val - максимальное число жеваний
    //
    //**********************************************************************************************
    public void updateMaxChew(int val)
    {
        editTextMaxChew.setText(String.format("%d", val));                                          // Обновить поле EditText с максимальным числом жеваний

        SharedPreferences mSharedPreferences = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putInt("maxChewCounter", val);
        editor.apply();
    }

    private void setImage() {
        switch(globalCategory) {
            case "супы" :
                dishImageView.setImageResource(R.drawable.soup_green);
                break;
            case "горячие блюда из мяса" :
                dishImageView.setImageResource(R.drawable.hot_meat_dish_green);
                break;
            case "горячие блюда из птицы" :
                dishImageView.setImageResource(R.drawable.hot_dish_of_poultry_green);
                break;
            case "горячие блюда из рыбы и морепродуктов" :
                dishImageView.setImageResource(R.drawable.hot_fish_dish_green);
                break;
            case "блюда из яиц" :
                dishImageView.setImageResource(R.drawable.egg_dish_green);
                break;
            case "салаты" :
                dishImageView.setImageResource(R.drawable.salad_green);
                break;
            case "гарниры" :
                dishImageView.setImageResource(R.drawable.garnish_green);
                break;
            case "паста, спагетти, макароны, wok" :
                dishImageView.setImageResource(R.drawable.pasta_green);
                break;
            case "плов, рис" :
                dishImageView.setImageResource(R.drawable.rice_green);
                break;
            case "каши, мюсли" :
                dishImageView.setImageResource(R.drawable.kasha_green);
                break;
            case "блины, оладьи, панкейки, драники" :
                dishImageView.setImageResource(R.drawable.pancakes_green);
                break;
            case "сырники, запеканки" :
                dishImageView.setImageResource(R.drawable.cheesecake_green);
                break;
            case "хлеб, булочки, багеты, чиабата, лепешки, лаваш " :
                dishImageView.setImageResource(R.drawable.bread_green);
                break;
            case "вареники, пельмени, равиоли" :
                dishImageView.setImageResource(R.drawable.dumplings_green);
                break;
            case "курзе, хинкал, манты" :
                dishImageView.setImageResource(R.drawable.khinkali_green);
                break;
            case "хачапури, чуду, фокачча" :
                dishImageView.setImageResource(R.drawable.khachapuri_green);
                break;
            case "голубцы, долма" :
                dishImageView.setImageResource(R.drawable.cabbage_rolls_green);
                break;
            case "суши, сашими, роллы" :
                dishImageView.setImageResource(R.drawable.sushi_green);
                break;
            case "сэндвичи, бургеры, бутерброды, буррито" :
                dishImageView.setImageResource(R.drawable.sandwich_green);
                break;
            case "пироги, пирожки, чебуреки" :
                dishImageView.setImageResource(R.drawable.pie_green);
                break;
            case "пицца, кальцоне" :
                dishImageView.setImageResource(R.drawable.pizza_green);
                break;
            case "закуски холодные, соленья" :
                dishImageView.setImageResource(R.drawable.cold_snacks_green);
                break;
            case "закуски горячие" :
                dishImageView.setImageResource(R.drawable.hot_snacks_green);
                break;
            case "соусы" :
                dishImageView.setImageResource(R.drawable.sauce_green);
                break;
            case "сметана, масло" :
                dishImageView.setImageResource(R.drawable.butter_green);
                break;
            case "паштеты" :
                dishImageView.setImageResource(R.drawable.paste_green);
                break;
            case "холодные напитки" :
                dishImageView.setImageResource(R.drawable.cold_drink_green);
                break;
            case "горячие напитки" :
                dishImageView.setImageResource(R.drawable.hot_drink_green);
                break;
            case "алкоголь, пиво" :
                dishImageView.setImageResource(R.drawable.alcohol_green);
                break;

            case "десерты" :
                dishImageView.setImageResource(R.drawable.dessert_green);
                break;
            case "варенье, джем, повидло, сироп" :
                dishImageView.setImageResource(R.drawable.jam_green);
                break;
            case "мороженое" :
                dishImageView.setImageResource(R.drawable.ice_cream_green);
                break;
            case "фрукты" :
                dishImageView.setImageResource(R.drawable.fruit_green);
                break;
            case "орехи" :
                dishImageView.setImageResource(R.drawable.nuts_green);
                break;
            case "добавки" :
                dishImageView.setImageResource(R.drawable.supplements_green);
                break;
            default:
                dishImageView.setImageResource(R.drawable.star_green);
                break;
        }
    }
}

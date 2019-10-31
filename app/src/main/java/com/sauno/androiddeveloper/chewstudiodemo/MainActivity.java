package com.sauno.androiddeveloper.chewstudiodemo;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.sauno.androiddeveloper.chewstudiodemo.bluetooth.BluetoothLeService;
import com.sauno.androiddeveloper.chewstudiodemo.bluetooth.LeLister;
import com.sauno.androiddeveloper.chewstudiodemo.data.DatabaseCreateHelper;
import com.sauno.androiddeveloper.chewstudiodemo.data.UserDBHelper;
import com.sauno.androiddeveloper.chewstudiodemo.navigationItemFragments.AboutAppFragment;
import com.sauno.androiddeveloper.chewstudiodemo.navigationItemFragments.DefaultResetFragment;
import com.sauno.androiddeveloper.chewstudiodemo.navigationItemFragments.SoundsAndNotificationsFragment;

import java.lang.ref.WeakReference;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, MainSettingsFragment.OnFragmentInteractionListener, FoodFragment.OnFragmentInteractionListener {
    SearchDevicesFragment searchDevicesFragment;

    Menu mainMenu;

    public Toolbar toolbar;
    DrawerLayout drawer;
    public ActionBarDrawerToggle toggle;

    SharedPreferences mSharedPreferences;

    // Флаг наличия запроса к системному активити на активацию Bluetooth
    public static Boolean fEnableBTRequest = false;

    static final int REQUEST_ENABLE_BT = 1;
    static final int SCAN_PERIOD = 15000;                                                           // Время поиска устройств - 5 секунд
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

            mLeService.weakMainActivity = new WeakReference<>(MainActivity.this);       // Создать слабую ссылку для MainActivity

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
                Toast.makeText(MainActivity.this, "BLE not supported on this device",               // Печатаем Toast-сообщение 'BLE not supported on this device'
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

           /* SharedPreferences mSharedPreferences = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = mSharedPreferences.edit();
            editor.putBoolean("isConnected", false);
            editor.apply();*/
        }
    };

    public void onFragmentInteraction(String onButton) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        switch(onButton) {
            case "onUserSettingsButton":
                Intent intent = new Intent(this, UserProfileActivity.class);
                startActivity(intent);
                break;
            case "onSoundSettingsButton":
                SoundsAndNotificationsFragment fragment = new SoundsAndNotificationsFragment();
                fragmentTransaction.replace(R.id.content_main, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                break;
            case "onDeviceSettingsButton":
                SearchDevicesFragment searchFragment = new SearchDevicesFragment();
                searchDevicesFragment = searchFragment;
                fragmentTransaction.replace(R.id.content_main, searchFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                break;
           /* case "onHomeMenuButton":
                HomeMenuFragment homeMenuFragment = new HomeMenuFragment();
                fragmentTransaction.replace(R.id.drawer_layout, homeMenuFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                break;*/
            default:
                break;
        }
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bindService(new Intent(this, BluetoothLeService.class),                                     // Привязаться к сервису BluetoothLeService
                mLeConnection, Context.BIND_AUTO_CREATE);

        //Вызываем метод записывающий базу для дальнейшего использования
        write_db();

        toolbar = findViewById(R.id.toolbar);


//        getSupportActionBar().setLogo(R.drawable.ic_menu_user_settings_dark);

        drawer = findViewById(R.id.drawer_layout);


        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ImageView foodButtonImageView = findViewById(R.id.foodButtonImageView);
        ImageView settingsButtonImageView = findViewById(R.id.settingsButtonImageView);
        ImageView statisticsButtonImageView = findViewById(R.id.statisticsButtonImageView);

        foodButtonImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                FoodFragment fragment = new  FoodFragment();
                fragmentTransaction.add(R.id.content_main, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        settingsButtonImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                MainSettingsFragment fragment = new MainSettingsFragment();
                fragmentTransaction.add(R.id.content_main, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();

                /*SettingsFragment fragment = new  SettingsFragment();
                searchDevicesFragment = fragment;
                fragmentTransaction.add(R.id.content_main, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();*/
            }
        });

        statisticsButtonImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                StatisticsFragment fragment = new  StatisticsFragment();
                fragmentTransaction.add(R.id.content_main, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        //SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        //ViewPager mViewPager = findViewById(R.id.viewPager);

        //mViewPager.setAdapter(mSectionsPagerAdapter);

        //TabLayout tabLayout = findViewById(R.id.tabs);

        //mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        //tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

        /*LinearLayout mEatenSpentKeepLinearLayout = findViewById(R.id.eatenSpentKeepLinearLayout);
        mEatenSpentKeepLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SelectWhatIsDisplayedDialogFragment mSelectWhatIsDisplayedDialogFragment = new SelectWhatIsDisplayedDialogFragment();
                mSelectWhatIsDisplayedDialogFragment.show(getSupportFragmentManager(), "dialogSelectWhatIsDisplayed");
            }
        });*/

        mSharedPreferences = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);

        /*mEatenCountTextView = findViewById(R.id.eatenCountTextView);
        mSpentCountTextView = findViewById(R.id.spentCountTextView);
        mRemainCountTextView = findViewById(R.id.remainCountTextView);
*/



        //Запрос на включение разрешения геолокации для устройств Andrpoid 6 и выше
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Android M Permission check 
            if (this.checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Это приложение использует энергосберегающий режим блютуза");
                builder.setMessage("Пожалуйста включите разрешение использования геолокации для работы этого приложения");
                builder.setPositiveButton(android.R.string.ok, null);
                builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_COARSE_LOCATION);
                    }
                });
                builder.show();
            }
        }

        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putInt("lastRestaurant", -1);

        editor.apply();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[],
                                           int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_COARSE_LOCATION: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("MyLog", "coarse location permission granted");
                } else {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Функциональность ограничена");
                    builder.setMessage("Пока вы не включите разрешение ГЕОЛОКАЦИИ, приложение не сможет использовать энергосберегающий фоновый блютуз(BLE). Вы можете исправить это добавив разрешение через настройки вашего телефона");
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                        }
                    });
                    builder.show();
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_settings) {
            Intent intent = new Intent(this, UserProfileActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_favorite_dishes) {
            Intent intent = new Intent(this, FavoriteDishesActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_about_app) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            AboutAppFragment fragment = new  AboutAppFragment();
            fragmentTransaction.add(R.id.drawer_layout, fragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();

        } else if (id == R.id.nav_sounds) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            SoundsAndNotificationsFragment fragment = new SoundsAndNotificationsFragment();

            fragmentTransaction.add(R.id.content_main, fragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();


        } else if (id == R.id.nav_default_reset) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            DefaultResetFragment fragment = new DefaultResetFragment();
            fragmentTransaction.add(R.id.drawer_layout, fragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    public void recreationToggle() {
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        toolbar.setTitle("SmartChew");
    }


    @Override
    protected void onResume() {
        super.onResume();

        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        TextView userName = headerView.findViewById(R.id.userNameHeaderTextView);

        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setElevation(0);
        }

        toolbar.setTitle("SmartChew");

        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        drawer.addDrawerListener(toggle);

        toggle.syncState();

       /*
        */

        //toggle.setDrawerIndicatorEnabled(false);

        //toggle.setHomeAsUpIndicator(android.R.id.home);


        //supportInvalidateOptionsMenu();






       /* drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        toggle.onDrawerStateChanged(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

        toggle.syncState();*/



        int idForFavoriteDishes = getIdOfFavoriteDishesCurrentUser();

        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString("currentActivityForService", "MainActivity");
        editor.putInt("idForFavoriteDishes", idForFavoriteDishes);

        editor.apply();

       /* Navigation navHeaderView= navigationView.inflateHeaderView(R.layout.nav_header_main);

        View hview = navigationView.getHeaderView(0);*/



        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        String user = sp.getString("userNamePref", "");

        userName.setText(user);

        if(!BluetoothLeService.getConnectStatus()) {
            SharedPreferences mSharedPreferences = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
            SharedPreferences.Editor mEditor = mSharedPreferences.edit();
            mEditor.putBoolean("isConnected", false);
            mEditor.apply();
        }

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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_main_menu, menu);

        mainMenu = menu;

        if (mSharedPreferences.getBoolean("isConnected", false))                                                                        // Если есть привязка к BLE-сервису, то
        {
            mainMenu.findItem(R.id.action_device).setIcon(R.drawable.chew_logo);
        } else {
            mainMenu.findItem(R.id.action_device).setIcon(R.drawable.black_logo);
        }

        //mainMenu.findItem(R.id.action_no_device).setVisible(true);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        Log.d("MyLogMainActivity", item.toString());

        if (id == R.id.action_user_settings) {
            Intent intent = new Intent(this, UserProfileActivity.class);
            startActivity(intent);
            return true;
        }

        if(id == R.id.action_device) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            SearchDevicesFragment searchFragment = new SearchDevicesFragment();
            searchDevicesFragment = searchFragment;
            fragmentTransaction.add(R.id.content_main, searchFragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        }

        if (id == android.R.id.home) {

            Log.d("MyLogMainActivity", "HOME");
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private void write_db() {
        DatabaseCreateHelper databaseCreateHelper = new DatabaseCreateHelper(getApplicationContext());
        // создаем базу данных
        databaseCreateHelper.create_db();
    }

    private int getIdOfFavoriteDishesCurrentUser() {
        int idForFavoriteDishesTable = -1;

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        String userName = sp.getString(UserPreferencesActivity.KEY_USER_NAME_PREF, "");

        if(!userName.equals("")) {
            DatabaseCreateHelper databaseCreateHelper = new DatabaseCreateHelper(this);
            SQLiteDatabase db = databaseCreateHelper.getWritableDatabase();


            String[] projection = {UserDBHelper.COLUMN_FAVORITE_DISH};
            String selection = UserDBHelper.COLUMN_NAME + " = ?";
            String[] selectionArgs = {userName};

            Cursor cursor = db.query(
                    true,
                    UserDBHelper.TABLE,
                    projection,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    null,
                    null
            );


            if (cursor.moveToFirst()) {
                idForFavoriteDishesTable = cursor.getInt(cursor.getColumnIndexOrThrow(UserDBHelper.COLUMN_FAVORITE_DISH));
            }

            cursor.close();
            db.close();
        }

        return idForFavoriteDishesTable;
    }

    //**********************************************************************************************
    //
    //        Публичная функция displayToastMessage() для сервиса BluetoothLeService
    //                      Обновление информации о статусе соединения
    //
    //  Замечание: Запускается уже сразу в UI-потоке.
    //
    //  Замечание: Так же может вызываться из MainActivity для печати всплывающих сообщений
    //
    //  Входные данные: msg - текст всплывающего сообщения
    //
    //**********************************************************************************************
    public void displayToastMessage(String msg)
    {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();                                       // Печать всплывающего сообщения
    }


    //**********************************************************************************************
    //
    //            Ответ от системного активити с запросом разрешения включения Bluetooth
    //
    //**********************************************************************************************
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if ((requestCode == REQUEST_ENABLE_BT))                                                     // Если пришел ответ от активити запроса разрешения Bluetooth, то
        {
            fEnableBTRequest = false;                                                               // Снять флаг запроса

            if (resultCode == Activity.RESULT_CANCELED)                                             // Если пользователь не разрешил Bluetooth, то
            {
                Toast.makeText(this, "Bluetooth отключён", Toast.LENGTH_SHORT).show();              // Вывести тост "Bluetooth disabled"
                //finish();                                                                           // Закрываем приложение
                return;
            }
            else if (resultCode == Activity.RESULT_OK)                                              // Иначе, если пользователь разрешил Bluetooth, то
            {
                Toast.makeText(this, "Bluetooth включён", Toast.LENGTH_SHORT).show();               // Вывести тост "Bluetooth enabled"
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
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




    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mLeServiceBound) {                                                                      // Если есть привязка к BLE-сервису, то{
            mLeService = null;                                                                      // Освободить ссылку на сервис
        }

        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putInt("lastRestaurant", -1);

        editor.apply();
    }

}

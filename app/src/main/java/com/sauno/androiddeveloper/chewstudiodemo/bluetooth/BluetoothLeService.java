package com.sauno.androiddeveloper.chewstudiodemo.bluetooth;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.sauno.androiddeveloper.chewstudiodemo.BasketActivity;
import com.sauno.androiddeveloper.chewstudiodemo.ChewingProcessActivity;
import com.sauno.androiddeveloper.chewstudiodemo.ChoiceOfDishesActivity;
import com.sauno.androiddeveloper.chewstudiodemo.Dish18Activity;
import com.sauno.androiddeveloper.chewstudiodemo.DishReplacementActivity;
import com.sauno.androiddeveloper.chewstudiodemo.FavoriteDishesActivity;
import com.sauno.androiddeveloper.chewstudiodemo.HomeMenuActivity;
import com.sauno.androiddeveloper.chewstudiodemo.ListDishesActivity;
import com.sauno.androiddeveloper.chewstudiodemo.MainActivity;
import com.sauno.androiddeveloper.chewstudiodemo.R;
import com.sauno.androiddeveloper.chewstudiodemo.ResultsOfEatingActivity;
import com.sauno.androiddeveloper.chewstudiodemo.SelectDesiredMealActivity;
import com.sauno.androiddeveloper.chewstudiodemo.SetQuantityOfDishesActivity;
import com.sauno.androiddeveloper.chewstudiodemo.UserPreferencesActivity;
import com.sauno.androiddeveloper.chewstudiodemo.UserProfileActivity;

import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

public class BluetoothLeService extends Service {


    // 128-битные UUID
    final static UUID CCCD_UUID           = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");// UUID для стандартного CCCD (Client Characteristic Configuration Descriptor)
    final static UUID CHEWFON_SERVICVE    = UUID.fromString("00001523-1212-efde-1523-785feabcd123");// UUID сервиса CHEWFON
    final static UUID CHEW_CHARACTERISTIC = UUID.fromString("00001524-1212-efde-1523-785feabcd123");// UUID характеристики CHEW
    final static UUID RAW_CHARACTERISTIC  = UUID.fromString("00001526-1212-efde-1523-785feabcd123");// UUID характеристики RAW
    final static UUID PREF_CHARACTERISTIC = UUID.fromString("00001525-1212-efde-1523-785feabcd123");// UUID характеристики PREF

    public WeakReference<MainActivity> weakMainActivity = new WeakReference<MainActivity>(null);       // Слабая ссылка на MainActivity
    public WeakReference<ChewingProcessActivity> weakChewingProcessActivity = new WeakReference<>(null);
    public WeakReference<Dish18Activity> weakDish18Activity = new WeakReference<>(null);
    public WeakReference<SelectDesiredMealActivity> weakSelectDesiredMealActivity = new WeakReference<>(null);
    public WeakReference<BasketActivity> weakBasketActivity = new WeakReference<BasketActivity>(null);
    public WeakReference<ChoiceOfDishesActivity> weakChoiceOfDishesActivity = new WeakReference<ChoiceOfDishesActivity>(null);
    public WeakReference<DishReplacementActivity> weakDishReplacementActivity = new WeakReference<DishReplacementActivity>(null);
    public WeakReference<FavoriteDishesActivity> weakFavoriteDishesActivity = new WeakReference<FavoriteDishesActivity>(null);
    public WeakReference<HomeMenuActivity> weakHomeMenuActivity = new WeakReference<HomeMenuActivity>(null);
    public WeakReference<ListDishesActivity> weakListDishesActivity = new WeakReference<ListDishesActivity>(null);
    public WeakReference<ResultsOfEatingActivity> weakResultsOfEatingActivity = new WeakReference<ResultsOfEatingActivity>(null);
    public WeakReference<SetQuantityOfDishesActivity> weakSetQuantityOfDishesActivity = new WeakReference<SetQuantityOfDishesActivity>(null);
    public WeakReference<UserPreferencesActivity> weakUserPreferencesActivity = new WeakReference<UserPreferencesActivity>(null);
    public WeakReference<UserProfileActivity> weakUserProfileActivity = new WeakReference<UserProfileActivity>(null);



    private final IBinder mBinder = new leBinder();                                                 // mBinder - Binder для привязки к нашему сервису
    Handler mHandler = new Handler();                                                               // Хендлер для отложенных задач

    BluetoothManager mBluetoothManager     = null;                                                  // Дескриптор Bluetooht-менеджера
    public BluetoothAdapter mBluetoothAdapter     = null;                                           // Дескриптор Bluetooth-адептера
    BluetoothDevice mBluetoothDevice      = null;                                                   // Дескриптор BLE-устройства
    static BluetoothGatt mBluetoothGatt        = null;                                                     // Дескриптор GATT-сервера (по нему фактически можем определить, есть связь или нет)
    BluetoothGattService mBluetoothGattService = null;                                              // Дескриптор нашего GATT-сервиса
    BluetoothGattCharacteristic mCharacteristicPREF   = null;                                       // Характеристика PREF
    BluetoothGattCharacteristic mCharacteristicCHEW;                                                // Характеристика CHEW
    BluetoothGattDescriptor mCharDescriptorCHEW;                                                    // CCCD дескриптор характеристики CHEW
    BluetoothGattCharacteristic mCharacteristicRAW;                                                 // Характеристика RAW
    BluetoothGattDescriptor     mCharDescriptorRAW;                                                 // CCCD дескриптор характеристики RAW

    public volatile boolean mScanning = false;                                                      // Флаг того, что идет сканирование эфира на наличие BLE-устройств
    int scanCounterInitial;                                                                         // Начальное значение счетчика времени сканирования (в миллисекундах)
    int scanCounter;                                                                                // Обратный счетчик времени сканирования (в миллисекундах)
    int counterStep;                                                                                // Шаг счетчика времени сканирования (в миллисекундах)

    public int maxChewCounter = 30;                                                                 // Счетчик максимального числа жеваний (локальная копия, для восстановления состояния MainActivity)

    public String deviceName = "";


    //**********************************************************************************************
    //
    //                  Класс для упрощенного подключения к нашему локальному
    //                         (работающему в то же процессе) сервису
    //
    //**********************************************************************************************
    public class leBinder extends Binder {
        public BluetoothLeService getService() {
            return BluetoothLeService.this;                                                         // Метод возвращает указатель на экземпляр нашего сервиса
        }
    }


    //**********************************************************************************************
    //
    //                  Функция вывода отладочных сообщений на консоль
    //
    //**********************************************************************************************
    private void debugMessage(String text) {
        Log.w("BLE01", text);
    }


    //**********************************************************************************************
    //
    //                  Вызов onCreate() запускается при первом запуске сервиса
    //
    //**********************************************************************************************
    @Override
    public void onCreate() {
        super.onCreate();
        debugMessage("Service: On Create");

        SharedPreferences mSharedPreferences = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putBoolean("isConnected", false);
        editor.apply();
    }


    //**********************************************************************************************
    //
    //             Вызов onStartCommand() запускается при обращении к сервису
    //                  (в режиме привязки (IBinder) не используется)
    //
    //**********************************************************************************************
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        debugMessage("Service: On Start Command");

        return START_NOT_STICKY;                                                                    // Сообщаем системе, что при уничтожении сервиса, его перезапускать не нужно.
        // (по умолчанию super-метод дает выход с START_STICKY (автоперезапуск))
    }


    //**********************************************************************************************
    //
    //                      Вызов onDestroy() - уничтожение сервиса
    //
    //  Вызывается в следующих случаях:
    //  * Если активити, привязанная к сервису, закрыта по команде 'выход'
    //    (не при повороте экрана)
    //  * Если от активити пришла команда unbindService
    //
    //**********************************************************************************************
    @Override
    public void onDestroy()
    {
        super.onDestroy();

        stopLeScan();                                                                               // Остановить сканирование, если оно идет
                                                                                                    // (последний из запущенных хендлеров сканирования, остановится через counterStep миллисекунд)

        if (mBluetoothGatt != null)                                                                 // Если соединение есть, то
        {
            mBluetoothGatt.close();                                                                 // Закрыть соединение
            mBluetoothGatt = null;                                                                  // Отпустить обьект mBluetoothGatt
        }

        debugMessage("Service: On Destroy");

        SharedPreferences mSharedPreferences = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putBoolean("isConnected", false);
        editor.apply();
    }


    //**********************************************************************************************
    //
    //           Вызов onBind() запускается при обращении к сервису посредством привязки
    //
    //  Замечания:
    //  * От отдного активити привязка может быть только один раз. Если активити перезапущена
    //    посредством поворота экрана, то активити заново получает вызов
    //    onServiceConnected(), при этом повторный вызов onBind не происходит.
    //
    //**********************************************************************************************
    @Override
    public IBinder onBind(Intent intent)
    {
        debugMessage("Service: On Bind");

        return (mBinder);                                                                           // Возвращаем указатель на Binder для привязки к нашему сервису
    }


    //**********************************************************************************************
    //
    //                            Публичный метод startLeScan()
    //                Сканирование эфира на наличие BluetoothLE устройств
    //
    //  Входные данные: duration - время сканирования эфира (в миллисекундах)
    //                  timeStep - шаг по времени обновления прогресса сканирования
    //
    //  Замечание: Чем ближе timeStep к дискретности переключения задач системы, тем хуже точность
    //             общей выдержки сканирования duration.
    //
    //  Замечание: Метод использует два способа выполнения в параллель с основным потоком:
    //             1) Функции отложенных вызовов в этом же потоке - Handler.post()
    //             2) Колбек в новом потоке при нахождении нового устройства - LeScanCallback()
    //
    //**********************************************************************************************
    public void startLeScan(int duration, int timeStep)
    {
        debugMessage("Service: startLeScan");

        if (mScanning)                                                                              // Если уже идет сканирование
            return;                                                                                 // то выйти

        mScanning = true;                                                                           // Установить флаг сканирования

        scanCounterInitial = scanCounter = duration;                                                // Инициализируем время сканирования
        counterStep = timeStep;                                                                     // и шаг сканирования

        mBluetoothAdapter.startLeScan(mLeScanCallback);                                             // Запустить сканирование BluetoothLE устройств

        mHandler.post(new Runnable()                                                                // Запустить хендлер автовызова обновления прогресса сканирования
        {
            public void run() {

                scanCounter -= counterStep;                                                         // Уменьшаем таймер сканирования

                if (scanCounter <= 0)                                                               // Если исчерпали таймер, то
                {
                    mBluetoothAdapter.stopLeScan(mLeScanCallback);                                  // Закончить сканирование BluetoothLE устройств
                    mScanning = false;                                                              // Снять флаг сканирования
                    debugMessage("Service: stopLeScan");
                }
                else
                {
                    localUpdateScanProgress(((scanCounterInitial - scanCounter) * 100) /            // Вызвать обработчик обновления прогресса MainActivity (прогресс 0..100%)
                        scanCounterInitial);

                    mHandler.postDelayed(this, counterStep);                                        // Перезапустить наш Runnable опять через counterStep миллисекунд
                }
            }
        });
    }


    //**********************************************************************************************
    //
    //                            Публичный метод stopLeScan()
    //            Остановить сканирование эфира на наличие BluetoothLE устройств
    //
    //**********************************************************************************************
    public void stopLeScan()
    {
        if (!mScanning)                                                                             // Если сканирование не идет, то
            return;                                                                                 // выйти

        scanCounter = 0;                                                                            // Обнулить счетчик оставшегося время сканирования
        mBluetoothAdapter.stopLeScan(mLeScanCallback);                                              // Закончить сканирование BluetoothLE устройств
        mScanning = false;                                                                          // Снять флаг сканирования

    }


    //**********************************************************************************************
    //
    //           Класс колбека вызываемый при нахождении/обновлении BluetoothLE устройства
    //
    //  Замечание: Каждый вызов - это новый поток
    //
    //**********************************************************************************************
    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(BluetoothDevice device, final int rssi, byte[] scanRecord) {
            final String name = device.getName();                                                   // Получить имя устройства
            final String address = device.getAddress();                                             // Получить адрес устройства

            debugMessage("Service: name - " + name);
            debugMessage("Service: address - " + address);

            if (name == null)                                                                       // Если устройство без имени,
                return;                                                                             // то выход

            localUpdateLeList(name, address, rssi);                                                 // Передаем информацию о новом BLE-устройстве MainActivity

            debugMessage("Service: mLeScanCallback.onLeScan");
        }
    };


    //**********************************************************************************************
    //
    //                           Публичный метод getConnectStatus()
    //                         Получить статус соединения (есть/нет)
    //
    //**********************************************************************************************
    public static boolean getConnectStatus()
    {
        return mBluetoothGatt != null;                          // Если обьект mBluetoothGatt существует, то связь есть
    }


    //**********************************************************************************************
    //
    //                             Публичный метод connectLeDevice()
    //                           Соединение с Bluetooth LE устройством
    //
    //**********************************************************************************************
    public boolean connectLeDevice(Context context, String address) {

        debugMessage("Thread connectLeDevice: " + Thread.currentThread().getId());

        if (!BluetoothAdapter.checkBluetoothAddress(address))                                       // Если адрес устройства некорректен, то
        {
            debugMessage("Incorrect BluetoothLE address!");
            return false;                                                                          // Выйти с ошибкой
        }

        mBluetoothDevice = mBluetoothAdapter.getRemoteDevice(address);                              // Получить устройство по его адресу

        if (mBluetoothDevice == null)                                                               // Если устройства с таким адресом нет, то
        {
            debugMessage("Device with address " + address + " not found!");
            return false;                                                                          // Выйти с ошибкой
        }

        if (mBluetoothGatt == null)                                                                 // Если обьект mBluetoothGatt еще не существует,
        {
            mBluetoothGatt = mBluetoothDevice.connectGatt(context, false, mGattCallback);           // то создать его и организовать соединение
            return true;                                                                           // Выйти с подтверждением
        }

        // Иначе подключение уже существует
        debugMessage("Device is already connected!");

        return false;                                                                              // Выход с ошибкой
    }


    //**********************************************************************************************
    //
    //                          Класс колбека для connectLeDevice
    //                     Вызывается при соединении с GATT сервером
    //
    //  Замечание: Каждый вызов каждого метода в классе - это может быть новый поток
    //
    //**********************************************************************************************
    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        private List<BluetoothGattDescriptor> descriptorQueue =                                     // Потокозащищенный список для организации очереди
                Collections.synchronizedList(new LinkedList<BluetoothGattDescriptor>());            // инициализации дескрипторов

        //------------------------------------------------------------------------------------------
        //
        //                  Метод, вызываемый при изменении состояния соединения
        //
        //------------------------------------------------------------------------------------------
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState)
        {

            debugMessage("Thread onConnectionStateChange: " + Thread.currentThread().getId());

            if (status == BluetoothGatt.GATT_SUCCESS)                                               // Если операция прошла успешно, то
            {
                if (newState == BluetoothProfile.STATE_CONNECTED)                                   // Если соединились, то
                {
                    debugMessage("Connected to GATT, searching for services");

                    descriptorQueue.clear();                                                        // Очистить список очереди дескрипторов

                    mBluetoothGatt.discoverServices();                                              // Искать сервисы (вызывает коллбек onServicesDiscovered)

                    localUpdateConnectStatus();                                                     // Обновляем строку статуса для пользовательского интерфейса
                }
                else if (newState == BluetoothProfile.STATE_DISCONNECTED)                           // Если разьединились, то
                {
                    // На телефоне Samsung S4 mini при разрыве связи попадает сюда
                    debugMessage("Disconnected from GATT server: " + status);

                    disconnectGatt();                                                               // Завершить разьединение связи

                    displayToast("Connect Error: " + status);                                       // Печать сообщения об ошибке соединения и номере ошибки

                    SharedPreferences mSharedPreferences = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = mSharedPreferences.edit();
                    editor.putBoolean("isConnected", false);
                    editor.apply();
                }
            }
            else                                                                                    // Если какая-либо ошибка, то
            {
                debugMessage("GATT connection error: " + status);                                   // Обычно происходит, если Bluetooth-устройство не отвечает (выключено),
                // либо при ошибке связи
                // На телефоне ZTE при разрыве связи выводится Gatt Error: 8 (GATT CONN TIMEOUT)

                disconnectGatt();                                                                   // Завершить разьединение связи

                displayToast("Connect Error: " + status);                                           // Печать сообщения об ошибке соединения и номере ошибки

                SharedPreferences mSharedPreferences = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = mSharedPreferences.edit();
                editor.putBoolean("isConnected", false);
                editor.apply();
            }
        }

        //------------------------------------------------------------------------------------------
        //
        //                  Метод окончательно закрывающий текущее совдинение
        //
        //------------------------------------------------------------------------------------------
        private void disconnectGatt()
        {
            mCharacteristicPREF = null;                                                             // Отпустить характеристику PREF (т.к. в нее может быть запись из MainActivity)

            mBluetoothGatt.close();                                                                 // Закрыть соединение (нужно ли? Или обьект и так закрывается автоматически?)
            mBluetoothGatt = null;                                                                  // Отпустить обьект mBluetoothGatt

            localUpdateConnectStatus();                                                             // Обновляем строку статуса для пользовательского интерфейса
        }



        //------------------------------------------------------------------------------------------
        //
        //                  Метод вызываемый при обнаружении нового сервиса
        //
        //------------------------------------------------------------------------------------------
        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status)
        {

            debugMessage("Thread onServicesDiscovered: " + Thread.currentThread().getId());

            if (status == BluetoothGatt.GATT_SUCCESS)                                               // Если успешно, то
            {
                mBluetoothGattService = mBluetoothGatt.getService(CHEWFON_SERVICVE);                // Ищем сервис CHEWFON

                if (mBluetoothGattService != null)                                                  // Если сервис найден, то
                {
                    mCharacteristicPREF =
                            mBluetoothGattService.getCharacteristic(PREF_CHARACTERISTIC);           // Найти характеристику PREF в сервисе CHEWFON

                    if (mCharacteristicPREF == null)
                        debugMessage("Characteristic PREF not found!");
                    // [!] Характеристика CHEW устаревшая и в дальнейших версиях будет упразднена
                    mCharacteristicCHEW =                                                           // Найти характеристику CHEW в сервисе CHEWFON
                            mBluetoothGattService.getCharacteristic(CHEW_CHARACTERISTIC);

                    if (mCharacteristicCHEW != null)                                                // Если характеристика CHEW найдена, то
                        mCharDescriptorCHEW = enableNotification(mCharacteristicCHEW);              // Разрешить нотификацию для характеристики
                    else
                        debugMessage("Characteristic CHEW not found!");

                    mCharacteristicRAW =                                                            // Найти характеристику RAW в сервисе CHEWFON
                            mBluetoothGattService.getCharacteristic(RAW_CHARACTERISTIC);

                    if (mCharacteristicRAW != null)                                                 // Если характеристика RAW найдена, то
                        mCharDescriptorRAW = enableNotification(mCharacteristicRAW);                // Разрешить нотификацию для характеристики
                    else
                        debugMessage("Characteristic RAW not found!");

                    processDescriptor();                                                            // Запустить процесс инициализации дескрипторов

                }
                else                                                                                // Иначе сервис не найден
                {
                    disconnectGatt();                                                               // Разьединение связи
                    debugMessage("Service CHEWFON not found!");

                    displayToast("Alien device!");                                                  // Печать сообщения "Alien device!"
                }
            }

            else
            {
                debugMessage("onServicesDiscovered received: " + status);                           // Иначе ошибка обнаружения сервиса
            }

        }

        //------------------------------------------------------------------------------------------
        //
        //                            Приватный метод enableNotification()
        //              Добавить дескриптор характеристики в очередь разрешения нотификации
        //
        //------------------------------------------------------------------------------------------
        private BluetoothGattDescriptor enableNotification(BluetoothGattCharacteristic characteristic)
        {
            BluetoothGattDescriptor descriptor;

            mBluetoothGatt.setCharacteristicNotification(characteristic, true);                     // Разрешить нотификацию для характеристики (локально)
            descriptor = characteristic.getDescriptor(CCCD_UUID);                                   // Найти дескриптор CCCD для характеристики

            descriptorQueue.add(descriptor);                                                        // Добавить дескриптор в очередь

            return(descriptor);                                                                     // Вернуть указатель на дескриптор
        }

        //------------------------------------------------------------------------------------------
        //
        //                        Приватный метод processDescriptor()
        //       Если очередь инициализации дексрипторов не пуста, то сделать запрос
        //                       на нотификацию для нового дескриптора
        //
        //------------------------------------------------------------------------------------------
        private void processDescriptor()
        {
            if (descriptorQueue.size() == 0)                                                        // Если очередь пуста, то
                return;                                                                             // выход

            BluetoothGattDescriptor descriptor = descriptorQueue.get(0);                            // Получить дескриптор из очереди (потокобезопасно)
            descriptorQueue.remove(0);                                                              // Убрать дескриптор из очереди (потокобезопасно)

            // Разрешить нотификацию для характеристики (удаленно)
            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);                 // Установить для дескриптора разрешение нотификации
            mBluetoothGatt.writeDescriptor(descriptor);                                             // Записать дескриптор на удаленное устройство
            // (после успешной записи будет вызван метод onDescriptorWrite)
        }

        //------------------------------------------------------------------------------------------
        //
        //                 Метод, вызываемый при записи нового значения дескриптора
        //
        //------------------------------------------------------------------------------------------
        @Override
        public void onDescriptorWrite(BluetoothGatt gatt,
                                      BluetoothGattDescriptor descriptor, int status)
        {
            debugMessage("Thread onDescriptorWrite: " + Thread.currentThread().getId());

            if (status == BluetoothGatt.GATT_SUCCESS)                                               // Если успешно, то
            {
                processDescriptor();                                                                // Запустиь процесс инициализации следующего дескриптора
                // (если существует в очереди)
            }

        }


        //------------------------------------------------------------------------------------------
        //
        //                     Метод вызываемый при чтении характеристики
        //
        //------------------------------------------------------------------------------------------
        @Override
        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic characteristic, int status)
        {
            if (status == BluetoothGatt.GATT_SUCCESS)
            {
                debugMessage("Characteristic Readed");
            }

            //Log.i("MyLogBluetooth", "onCharacteristicRead");
        }

        //------------------------------------------------------------------------------------------
        //
        //                     Колбек вызываемый при записи характеристики
        //
        //------------------------------------------------------------------------------------------
        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt,
                                          BluetoothGattCharacteristic characteristic, int status)
        {
            if (status == BluetoothGatt.GATT_SUCCESS)
            {
                debugMessage("Characteristic Written");
            }
        }

        //------------------------------------------------------------------------------------------
        //
        //                      Колбек вызываемый при изменении характеристики
        //
        //------------------------------------------------------------------------------------------
        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt,
                                            final BluetoothGattCharacteristic characteristic)
        {
            // Замечание - длина характеристики софтдевайса S110 Nordic ограничена 20 байтами!
            // (нюансы смотреть в документации и на devzone.nordicsemi.com)

            debugMessage("Characteristic Changed");

            if (characteristic == mCharacteristicRAW)                                               // Если обновилась характеристика RAW, то
            {
                final int FIRST_FIELD_SIZE = 6;                                                     // Длина первого поля массива (заголовок и InfoBlock)

                byte[] data = characteristic.getValue();                                            // Получить массив данных значения характеристики

//                debugMessage("len = " + data.length);

                if (data.length < FIRST_FIELD_SIZE)                                                 // Если массив данных меньше FIRST_FIELD_SIZE,
                    return;                                                                         // то блок ошибочный, выход

                // Разбор InfoBlock

                int chewValue = 0,                                                                  // Номер жевания и позиция
                        chewPos = 0;                                                                    //

                if (data[2] == 0x00)                                                                // Если команда 0x00 (описывает жевательный импульс), то
                {
                    chewValue = data[3];                                                            // Номер жевательного импульса
                    chewPos = (data[4] << 8) | (data[5] & 0xFF);                                    // Позиция жевательного импульса

                    if (chewValue > 0)                                                              // Если число жеваний 1..127, то
                    {
                        localUpdateChewCounter(chewValue);                                          // Печать числа жеваний
                    }
                }
                else if (data[2] == 0x01)                                                           // Если команда 0x01 (информация о настройках), то
                {
                    localUpdateMaxChewCounter(data[3]);                                             // Обновить настройки числа жеваний, после которых предлагается сделать глоток
                }

                // Разбор RawBlock (если присутствует)

                int sensDataSize = ((data.length - FIRST_FIELD_SIZE) / 3) * 2;                      // Вычислить число отсчетов в массиве данных
                // (адаптивно для любой длины поля данных)

                if (sensDataSize == 0)                                                              // Если длина блока данных с сенсора равна нулю, то
                    return;                                                                         // выход

                int[] sensData = new int[sensDataSize];                                             // Определить массив для отсчетов
                int dataPtr = FIRST_FIELD_SIZE;                                                     // Указатель на данные исходного массива

                for (int i=0; i<sensDataSize; i+=2)                                                 // Цикл на распаковку данных отсчетов (3 байта -> 2 отсчета)
                {
                    sensData[i] = ((data[dataPtr] << 4) |                                           // Первое слово
                            ((data[dataPtr + 1] >> 4) & 0xF)) << 1;                           //

                    sensData[i + 1] = ((data[dataPtr + 2] << 4) |                                   // Второе слово
                            (data[dataPtr + 1] & 0xF)) << 1;                              //

                    dataPtr += 3;                                                                   // Перейти к следующей тройке байт
                }

//                String text = "";                                                                   // Преобразовать массив данных в строку текста
//                for (int databyte : sensData)
//                    text = text + databyte + ", ";

//                debugMessage("value: " + text);

                localUpdateChewData(sensData, chewPos, chewValue);                                   // Обновить график жеваний
                // [T] Замечание: Если в дальнейшем данные о жевательных импульсах будут приходить
                // без данных с сенсора, то данныю функцию надо разделить на две
            }

            // [T] Характеристика CHEW, возможно, в дальнейшем будет упразднена
            if (characteristic == mCharacteristicCHEW)                                              // Если обновилась характеристика CHEW, то
            {
                byte[] data = characteristic.getValue();                                            // data - указатель на массив байт значения характеристики

                localUpdateChewCounter(data[0]);                                                                // Печать числа жеваний

            }


        }

/*
        //------------------------------------------------------------------------------------------
        //
        //              Метод печати числа жеваний для информирования MainActivity
        //
        //------------------------------------------------------------------------------------------
        private void updateChew(final int val)
        {
            final MainActivity mainActivity = mMainActivity;                                        // Сделать локальную копию ссылку на MainActivity из mMainActivity
                                                                                                    // (это защитит MainActivity от уничтожения на время существования ссылки.
                                                                                                    // Также, копия этой ссылки передается во внутренний класс Runable)

            if (mainActivity != null)                                                               // Если ссылка на MainActivity не пуста, то
            {

                mainActivity.runOnUiThread(new Runnable()                                           // Запускаем Runnable в MainActivity
                {
                    @Override
                    public void run()
                    {
                        mainActivity.updateChewCounter("Жевание: " + String.valueOf(val));          // Вызываем обновление строки счетчика жеваний MainActivity
                    }
                });

            }
        }


        //------------------------------------------------------------------------------------------
        //
        //              Метод печати числа жеваний для информирования MainActivity
        //
        //------------------------------------------------------------------------------------------
        private void updateChew(final int val)
        {
            if (mMainActivity != null)                                                              // Если есть ссылка на главную активити, то
            {
                try                                                                                 // Обертываем вызов из главной активити в try/catch во избежании исключения,
                {                                                                                   // хотя если есть ссылка на активити, то экземпляр активити существует в памяти,
                    // и, следовательно, все ее методы так же должны корректно вызываться. Возможно,
                    // за исключением случаев нехватки памяти самого устройства.

                    mMainActivity.runOnUiThread(new Runnable()                                      // Запускаем Runnable в главной активити
                    {
                        @Override
                        public void run()
                        {
                            try
                            {

                                mMainActivity.updateChewCounter("Жевание: " + String.valueOf(val));     // Вызываем обновление строки счетчика жеваний MainActivity
                            }
                            catch (NullPointerException e)
                            {
                                Log.w("BLE01", "NullPointerException: " + e);
                            }
                        }
                    });
                }
                catch (Exception e)
                {
                    debugMessage("Exception: " + e);
                }
            }
        }
*/




    };



    //**********************************************************************************************
    //
    //                      Инициализация BluetoothLE в системе
    //
    //  Выходные данные: true - BluetoothLE поддерживается
    //                   false - BluetoothLE не поддерживается
    //
    //**********************************************************************************************
    public boolean initialize()
    {
        if (mBluetoothAdapter == null)
        {
            if (mBluetoothManager == null)
            {
                mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);

                if (mBluetoothManager == null)
                {
                    debugMessage("Unable to initialize BluetoothManager.");
                    return false;
                }
            }

            mBluetoothAdapter = mBluetoothManager.getAdapter();

            if (mBluetoothAdapter == null)
            {
                debugMessage("Unable to obtain a BluetoothAdapter.");
                return false;
            }

        }

        return true;
    }



    //**********************************************************************************************
    //
    //                     Публичный метод для записи нового значения счетчика
    //                          максимального числа жеваний в устройство
    //
    //  [?] Замечание: Потокобезопасно ли? Или за этим следит сам менеджер BLE?
    //
    //**********************************************************************************************
    public void sendMaxChew(int val)
    {
        if (mCharacteristicPREF != null)                                                            // Если существует связь и присутствует характеристика PRRF, то
        {
            maxChewCounter = val;                                                                   // Сохранить максимальное число жеваний в переменной сервиса
            mCharacteristicPREF.setValue(new byte[] {0x01, (byte)val});                             // Записать в характеристику PREF команду 0x01 и максимальное значение жеваний для сигнала к глотанию
            mBluetoothGatt.writeCharacteristic(mCharacteristicPREF);                                // Записать характеристику PREF на удаленное устройство
        }
    }





    //==============================================================================================
    //
    //                    Группа методов для передачи данных в MainActivity
    //
    //==============================================================================================


    //**********************************************************************************************
    //
    //                 Метод для передачи MainActivity всплывающих сообщений
    //
    //**********************************************************************************************
    private void displayToast(final String msg)
    {
        final MainActivity mainActivity = weakMainActivity.get();                                   // Получить слабую ссылку на MainActivity

        if (mainActivity == null)                                                                   // Если MainActivity не существует, то выход
            return;                                                                                 //

        mainActivity.runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                mainActivity.displayToastMessage(msg);                                              // Вызываем печать всплывающего сообщения MainActivity
            }
        });

    }


    //**********************************************************************************************
    //
    //                Метод для передачи MainActivity прогресса сканирования эфира
    //
    //**********************************************************************************************
    private void localUpdateScanProgress(final int progress)
    {
        SharedPreferences mSharedPreferences = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        String currentActivity = mSharedPreferences.getString("currentActivityForService", "");
        boolean isVisibleSearchFragment = mSharedPreferences.getBoolean("isVisibleSearchFragment", false);

        if(isVisibleSearchFragment) {
            switch(currentActivity) {
                case "MainActivity":
                    final MainActivity mainActivity = weakMainActivity.get();
                    mainActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mainActivity.updateScanProgress(progress);                                          // Вызвать обработчик обновления прогресса для MainActivity
                        }
                    });
                    break;
                case "SelectDesiredMealActivity":
                    final SelectDesiredMealActivity selectDesiredMealActivity = weakSelectDesiredMealActivity.get();
                    selectDesiredMealActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            selectDesiredMealActivity.updateScanProgress(progress);                                          // Вызвать обработчик обновления прогресса для MainActivity
                        }
                    });
                    break;
                case "ChewingProcessActivity":
                    final ChewingProcessActivity chewingProcessActivity = weakChewingProcessActivity.get();
                    chewingProcessActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            chewingProcessActivity.updateScanProgress(progress);                                          // Вызвать обработчик обновления прогресса для MainActivity
                        }
                    });
                    break;
                case "BasketActivity":
                    final BasketActivity basketActivity = weakBasketActivity.get();
                    basketActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            basketActivity.updateScanProgress(progress);                                          // Вызвать обработчик обновления прогресса для MainActivity
                        }
                    });
                    break;
                case "ChoiceOfDishesActivity":
                    final ChoiceOfDishesActivity choiceOfDishesActivity = weakChoiceOfDishesActivity.get();
                    choiceOfDishesActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            choiceOfDishesActivity.updateScanProgress(progress);                                          // Вызвать обработчик обновления прогресса для MainActivity
                        }
                    });
                    break;
                case "Dish18Activity":
                    final Dish18Activity dish18Activity = weakDish18Activity.get();
                    dish18Activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dish18Activity.updateScanProgress(progress);                                          // Вызвать обработчик обновления прогресса для MainActivity
                        }
                    });
                    break;
                case "DishReplacementActivity":
                    final DishReplacementActivity dishReplacementActivity = weakDishReplacementActivity.get();
                    dishReplacementActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dishReplacementActivity.updateScanProgress(progress);                                          // Вызвать обработчик обновления прогресса для MainActivity
                        }
                    });
                    break;
                case "FavoriteDishesActivity":
                    final FavoriteDishesActivity favoriteDishesActivity = weakFavoriteDishesActivity.get();
                    favoriteDishesActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            favoriteDishesActivity.updateScanProgress(progress);                                          // Вызвать обработчик обновления прогресса для MainActivity
                        }
                    });
                    break;
                case "HomeMenuActivity":
                    final HomeMenuActivity homeMenuActivity = weakHomeMenuActivity.get();
                    homeMenuActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            homeMenuActivity.updateScanProgress(progress);                                          // Вызвать обработчик обновления прогресса для MainActivity
                        }
                    });
                    break;
                case "ListDishesActivity":
                    final ListDishesActivity listDishesActivity = weakListDishesActivity.get();
                    listDishesActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            listDishesActivity.updateScanProgress(progress);                                          // Вызвать обработчик обновления прогресса для MainActivity
                        }
                    });
                    break;
                case "ResultsOfEatingActivity":
                    final ResultsOfEatingActivity resultsOfEatingActivity = weakResultsOfEatingActivity.get();
                    resultsOfEatingActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            resultsOfEatingActivity.updateScanProgress(progress);                                          // Вызвать обработчик обновления прогресса для MainActivity
                        }
                    });
                    break;
                case "SetQuantityOfDishesActivity":
                    final SetQuantityOfDishesActivity setQuantityOfDishesActivity = weakSetQuantityOfDishesActivity.get();
                    setQuantityOfDishesActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            setQuantityOfDishesActivity.updateScanProgress(progress);                                          // Вызвать обработчик обновления прогресса для MainActivity
                        }
                    });
                    break;
                case "UserProfileActivity":
                    final UserProfileActivity userProfileActivity = weakUserProfileActivity.get();
                    userProfileActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            userProfileActivity.updateScanProgress(progress);                                          // Вызвать обработчик обновления прогресса для MainActivity
                        }
                    });
                    break;
            }

        }


                                         // Получить слабую ссылку на MainActivity
/*
        final ChewingProcessActivity chewingProcessActivity = weakChewingProcessActivity.get();



        if (chewingProcessActivity != null) {
            chewingProcessActivity.runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    chewingProcessActivity.updateScanProgress(progress);                                          // Вызвать обработчик обновления прогресса для MainActivity
                }
            });
        } else {

        }*/
       /* if (mainActivity != null) {

        }*/

    }


    //**********************************************************************************************
    //
    //                Метод обновления числа жеваний для информирования MainActivity
    //
    //**********************************************************************************************
    private void localUpdateChewCounter(final int val)
    {
        Log.i("MyLogBluetooth", "localUpdateChewCounter");
        final ChewingProcessActivity chewingProcessActivity = weakChewingProcessActivity.get();      // Получить слабую ссылку на MainActivity

        final Dish18Activity dish18Activity = weakDish18Activity.get();

        if (dish18Activity == null)                                                                   // Если MainActivity не существует, то выход
            return;

        dish18Activity.runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                dish18Activity.updateChewCounter(val);                  // Вызываем обновление строки счетчика жеваний MainActivity
            }
        });

        if (chewingProcessActivity == null)                                                                   // Если MainActivity не существует, то выход
            return; //

        chewingProcessActivity.runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                chewingProcessActivity.updateChewCounter(val);                  // Вызываем обновление строки счетчика жеваний MainActivity
            }
        });
    }


    //**********************************************************************************************
    //
    //        Метод обновления максимального числа жеваний для информирования MainActivity
    //
    //**********************************************************************************************
    private void localUpdateMaxChewCounter(final int val)
    {
        //final MainActivity mainActivity = weakMainActivity.get();                                   // Получить слабую ссылку на MainActivity
        final Dish18Activity activity = weakDish18Activity.get();

        if (activity == null)                                                                   // Если MainActivity не существует, то выход
            return;                                                                                 //

        maxChewCounter = val;                                                                       // Сохранить локальное состояние счетчика

        activity.runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                activity.updateMaxChew(val);                                                        // Вызываем обновление счетчика максимального числа жеваний для MainActivity


            }
        });
    }


    //**********************************************************************************************
    //
    //                Метод для информирования MainActivity о статусе соединения
    //
    //**********************************************************************************************
    private void localUpdateConnectStatus()
    {
        final boolean status = getConnectStatus();

        SharedPreferences mSharedPreferences = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putBoolean("isConnected", status);
        editor.apply();

        Log.i("MyLogBluetooth", "localUpdateConnectStatus status = " + status);

        final ChewingProcessActivity chewingProcessActivity = weakChewingProcessActivity.get();                                // Получить слабую ссылку на MainActivity

        if (chewingProcessActivity == null)                                                                   // Если MainActivity не существует, то выход
            return;                                                                                 //

                                                         // Получить статус соединения


        chewingProcessActivity.runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                chewingProcessActivity.updateConnectStatus(status);                                           // Вызываем обновление строки статуса MainActivity
            }
        });
    }


    //**********************************************************************************************
    //
    //            Метод для передачи MainActivity информации о новом BLE-устройстве
    //
    //**********************************************************************************************
    public void localUpdateLeList(final String name, final String address, final int rssi)
    {
        deviceName = name;

        SharedPreferences mSharedPreferences = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        String currentActivity = mSharedPreferences.getString("currentActivityForService", "");
        boolean isVisibleSearchFragment = mSharedPreferences.getBoolean("isVisibleSearchFragment", false);

        if(isVisibleSearchFragment) {
            switch(currentActivity) {
                case "MainActivity":
                    final MainActivity mainActivity = weakMainActivity.get();
                    mainActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mainActivity.updateLeList(name, address, rssi);                                          // Вызвать обработчик обновления прогресса для MainActivity
                        }
                    });
                    break;
                case "SelectDesiredMealActivity":
                    final SelectDesiredMealActivity selectDesiredMealActivity = weakSelectDesiredMealActivity.get();
                    selectDesiredMealActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            selectDesiredMealActivity.updateLeList(name, address, rssi);                                           // Вызвать обработчик обновления прогресса для MainActivity
                        }
                    });
                    break;
                case "ChewingProcessActivity":
                    final ChewingProcessActivity chewingProcessActivity = weakChewingProcessActivity.get();
                    chewingProcessActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            chewingProcessActivity.updateLeList(name, address, rssi);                                          // Вызвать обработчик обновления прогресса для MainActivity
                        }
                    });
                    break;
                case "BasketActivity":
                    final BasketActivity basketActivity = weakBasketActivity.get();
                    basketActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            basketActivity.updateLeList(name, address, rssi);                                           // Вызвать обработчик обновления прогресса для MainActivity
                        }
                    });
                    break;
                case "ChoiceOfDishesActivity":
                    final ChoiceOfDishesActivity choiceOfDishesActivity = weakChoiceOfDishesActivity.get();
                    choiceOfDishesActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            choiceOfDishesActivity.updateLeList(name, address, rssi);                                            // Вызвать обработчик обновления прогресса для MainActivity
                        }
                    });
                    break;
                case "Dish18Activity":
                    final Dish18Activity dish18Activity = weakDish18Activity.get();
                    dish18Activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dish18Activity.updateLeList(name, address, rssi);                                            // Вызвать обработчик обновления прогресса для MainActivity
                        }
                    });
                    break;
                case "DishReplacementActivity":
                    final DishReplacementActivity dishReplacementActivity = weakDishReplacementActivity.get();
                    dishReplacementActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dishReplacementActivity.updateLeList(name, address, rssi);                                           // Вызвать обработчик обновления прогресса для MainActivity
                        }
                    });
                    break;
                case "FavoriteDishesActivity":
                    final FavoriteDishesActivity favoriteDishesActivity = weakFavoriteDishesActivity.get();
                    favoriteDishesActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            favoriteDishesActivity.updateLeList(name, address, rssi);                                          // Вызвать обработчик обновления прогресса для MainActivity
                        }
                    });
                    break;
                case "HomeMenuActivity":
                    final HomeMenuActivity homeMenuActivity = weakHomeMenuActivity.get();
                    homeMenuActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            homeMenuActivity.updateLeList(name, address, rssi);                                            // Вызвать обработчик обновления прогресса для MainActivity
                        }
                    });
                    break;
                case "ListDishesActivity":
                    final ListDishesActivity listDishesActivity = weakListDishesActivity.get();
                    listDishesActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            listDishesActivity.updateLeList(name, address, rssi);                                           // Вызвать обработчик обновления прогресса для MainActivity
                        }
                    });
                    break;
                case "ResultsOfEatingActivity":
                    final ResultsOfEatingActivity resultsOfEatingActivity = weakResultsOfEatingActivity.get();
                    resultsOfEatingActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            resultsOfEatingActivity.updateLeList(name, address, rssi);                                            // Вызвать обработчик обновления прогресса для MainActivity
                        }
                    });
                    break;
                case "SetQuantityOfDishesActivity":
                    final SetQuantityOfDishesActivity setQuantityOfDishesActivity = weakSetQuantityOfDishesActivity.get();
                    setQuantityOfDishesActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            setQuantityOfDishesActivity.updateLeList(name, address, rssi);                                          // Вызвать обработчик обновления прогресса для MainActivity
                        }
                    });
                    break;
            /*case "UserPreferencesActivity":
                final UserPreferencesActivity userPreferencesActivity = weakUserPreferencesActivity.get();
                userPreferencesActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        userPreferencesActivity.updateLeList(name, address, rssi);                                           // Вызвать обработчик обновления прогресса для MainActivity
                    }
                });
                break;*/
                case "UserProfileActivity":
                    final UserProfileActivity userProfileActivity = weakUserProfileActivity.get();
                    userProfileActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            userProfileActivity.updateLeList(name, address, rssi);                                           // Вызвать обработчик обновления прогресса для MainActivity
                        }
                    });
                    break;
            }

        }


        //SharedPreferences mSharedPreferences = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putBoolean("isConnected", true);
        editor.apply();

        /*final MainActivity mainActivity = weakMainActivity.get();                                   // Получить слабую ссылку на MainActivity
        // Получить слабую ссылку на MainActivity

        final ChewingProcessActivity chewingProcessActivity = weakChewingProcessActivity.get();


        if (chewingProcessActivity != null) {
            chewingProcessActivity.runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    chewingProcessActivity.updateLeList(name, address, rssi);                                    //  Вызываем обработчик обновления списка BLE-устройств MainActivity
                }
            });
        } else {
            mainActivity.runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    mainActivity.updateLeList(name, address, rssi);                                    //  Вызываем обработчик обновления списка BLE-устройств MainActivity
                }
            });
        }
*/
        // Если MainActivity не существует, то выход
        //

        //final boolean status = getConnectStatus();                                                  // Получить статус соединения

    }




    //**********************************************************************************************
    //
    //                 Метод для передачи MainActivity жевательных данных
    //
    //**********************************************************************************************
    private void localUpdateChewData(final int[] data, final int chewDisp, final int chewCtr)
    {
        final ChewingProcessActivity chewingProcessActivity = weakChewingProcessActivity.get();                      // Получить слабую ссылку на MainActivity

        if (chewingProcessActivity == null)                                                                   // Если MainActivity не существует, то выход
            return;                                                                                 //

        chewingProcessActivity.runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                chewingProcessActivity.updateChewData(data, chewDisp, chewCtr);                               // Вызвать обработчик обновления графиков жевания и прижатия для MainActivity
            }
        });

        final boolean status = getConnectStatus();

        SharedPreferences mSharedPreferences = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putBoolean("isConnected", status);
        editor.apply();


    }











    //**********************************************************************************************

/*
    public void testMethod()
    {
        debugMessage("test Begin");

        Handler mHandler = new Handler();

        mHandler.postDelayed(new Runnable()
        {
            public void run()
            {
                try                                                                                 // Оборачиваем в try/catch во избежение несуществующей ссылки или активити
                {
                    mMainActivity.textViewRate.setText("Уррра!");
                }
                catch (Exception e)
                {
                    debugMessage("Error: " + e);
                }

                debugMessage("test End " + Thread.currentThread().getId());
            }
        }, 5000);

    }
*/



}

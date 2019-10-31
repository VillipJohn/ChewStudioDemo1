package com.sauno.androiddeveloper.chewstudiodemo.bluetooth;

import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.sauno.androiddeveloper.chewstudiodemo.MainActivity;
import com.sauno.androiddeveloper.chewstudiodemo.R;

import java.util.ArrayList;
import java.util.HashMap;

public class LeLister {

    MainActivity mMainActivity = null;                                                              // Текущий указатель на главную активити

    ListView lvLeListView;                                                                          // Указатель на ListView

    SimpleAdapter simpleAdapter;                                                                    // Адаптер для ListView списка Bluetooth LE устройств

    ArrayList<LeListEntry> leListEntries = new ArrayList<LeListEntry>();                                      // Массив-список Bluetooth LE устройств для ListView


    // Ключи и константы для ListView списка устройств и его SimpleAdapter
    final static String IDT_LIST_NAME = "name";                                                     // Текстовый ключ поля имени устройства
    final static String IDT_LIST_ADDRESS = "address";                                               // Текстовый ключ поля адреса устройства
    final static String IDT_LIST_RSSIINT = "rssiint";                                               // Текстовый ключ поля мощности (целочисленное значение, для сохранения в savedInstance)
    final static String IDT_LIST_RSSI = "rssi";                                                     // Текстовый ключ поля мощности принимаемого сигнала устройства (строковое значение)
    final static String IDT_LIST_ICON = "icon";                                                     // Текстовый ключ поля иконки мощности

    final static String[] LIST_FROM = {                                                              // Массиив имен атрибутов, из каких полей HashMap будут читаться данные
            IDT_LIST_NAME,
            IDT_LIST_ADDRESS,
            IDT_LIST_RSSI,
            IDT_LIST_ICON
    };

    final static int[] LIST_TO = {                                                                   // Массив ID View-компонентов, в которые будут записываться данные
            R.id.device_name,
            124253,
            123,
            1234
    };


    public class LeListEntry extends HashMap<String, Object>                                        // Класс-производное от HashMap, описывающий одну запись ListView для списка устройств
    {
        public LeListEntry(String name, String address, int rssi)                                   // Конструктор с параметрами
        {
            super();                                                                                // Вызов конструктора суперкласса

            super.put(IDT_LIST_NAME, name);                                                         // Заносим запись в поле имени устройства
            super.put(IDT_LIST_ADDRESS, address);                                                   // Заносим запись в поле адреса устройства
            super.put(IDT_LIST_RSSIINT, rssi);                                                      // Заносим запись в поле мощности устройства (целочисленное значение, для сохранения в SavedInstance)
            super.put(IDT_LIST_RSSI, Integer.toString(rssi) + " dBm");                              // Заносим запись в поле мощности устройства (строковое значение)
            super.put(IDT_LIST_ICON, rssiToIcon(rssi));                                             // Заносим запись в поле иконки мощности
        }

        private int rssiToIcon(int rssi)                                                            // Метод преобразования значения rssi в иконку
        {
            int img = R.drawable.signal_strength_bar_0;                                             // Выбор иконки мощности в зависимости от параметра rssi
            if (rssi > -90)
                img = R.drawable.signal_strength_bar_1;
            if (rssi > -85)
                img = R.drawable.signal_strength_bar_2;
            if (rssi > -80)
                img = R.drawable.signal_strength_bar_3;
            if (rssi > -70)
                img = R.drawable.signal_strength_bar_4;
            if (rssi > -60)
                img = R.drawable.signal_strength_bar_5;

            return(img);
        }

    }



    //**********************************************************************************************
    //
    //                                Конструктор класса LeLister
    //
    //**********************************************************************************************
   /* public LeLister(MainActivity mainActivity, ListView listView)
    {
        mMainActivity = mainActivity;                                                               // Сохраняем указатель на главную активити
        lvLeListView = listView;                                                                    // Сохраняем указатель на ListView

        simpleAdapter = new SimpleAdapter(mainActivity, leListEntries, R.layout.devicelist_item,    // Создаем статический адаптер для ListView списка устройств
                LIST_FROM, LIST_TO);                                      //

        lvLeListView.setAdapter(simpleAdapter);                                                     // Присваиваем списку устройств адаптер


        lvLeListView.setOnItemClickListener(new AdapterView.OnItemClickListener()                   // Настроить обработку нажатия на элемент ListView
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View itemClicked, int position, long id) // Создаем обработчик нажатия на элэмент ListView
            {

                debugMessage("Clicked to " + leListEntries.get(position).get(IDT_LIST_NAME));

                mMainActivity.onLeListerClick(position,                                             // Вызываем обработчик нажатия onLeListerClick()
                        (String)leListEntries.get(position).get(IDT_LIST_ADDRESS));                 // главной активити

            }

        });




    }*/


    //**********************************************************************************************
    //
    //                  Функция вывода отладочных сообщений на консоль
    //
    //**********************************************************************************************
    private void debugMessage(String text)
    {
        Log.w("BLE01", text);
    }



    //**********************************************************************************************
    //
    //                            Публичный метод updateItem()
    //         Добавление новой записи, или обновление старой, если адрес уже есть в списке
    //
    //  Входные данные: name    - имя устройства
    //                  address - адрес устройства
    //                  rssi    - сила сигнала устройства
    //
    //**********************************************************************************************
    public void updateItem(String name, String address, int rssi)
    {

        for (int i=0; i<leListEntries.size(); i++)                                                  // Цикл на все записи в списке
        {
            if (((String)leListEntries.get(i).get(IDT_LIST_ADDRESS)).equals(address))               // Если адрес совпал, то
            {
                leListEntries.set(i, new LeListEntry(name, address, rssi));                         // Заменить запись в текущей позиции на новую

                return;                                                                             // Выход из метода
            }
        }
        // Иначе, если не один из адресов не совпал, то
        leListEntries.add(new LeListEntry(name, address, rssi));                                    // добавить запись в список

        simpleAdapter.notifyDataSetChanged();                                                       // Указать адаптеру, что список изменился

    }


    //**********************************************************************************************
    //
    //                         Публичный метод removeUnusedLe()
    //               Удалить из списка все неиспользуемые BLE-устройства
    //
    //**********************************************************************************************
    public void removeUnusedLe(int pos)
    {
        leListEntries.set(0, leListEntries.get(pos));                                               // Переместить необходимую запись в 0-ю позицию списка

        for (int i=(leListEntries.size()-1); i>0; i--)                                              // Удалить все остальные элементы из списка
            leListEntries.remove(i);

        simpleAdapter.notifyDataSetChanged();                                                       // Указать адаптеру, что список изменился

    }

    //**********************************************************************************************
    //
    //                         Публичный метод getAddress()
    //               Получить адрес устройства в заданной позиции списка
    //
    //**********************************************************************************************
    public String getAddress(int pos)
    {
        return((String)leListEntries.get(pos).get(IDT_LIST_ADDRESS));                               // Получить адрес устройства в данной позиции списка

    }

    //**********************************************************************************************
    //
    //                            Публичный метод saveLister()
    //                  Сохранение состояние списка BLE-устройств в Bundle
    //
    //  Входные данные: bundle  - указатель на Bundle для сохранения данных
    //
    //**********************************************************************************************
    public void saveLister(Bundle bundle)
    {
        int size = leListEntries.size();                                                            // Определить размер списка

        // Для каждого поля листера создаем свой массив
        String[] names = new String[size];                                                          // Массив имен
        String[] addresses = new String[size];                                                      // Массив адресов
        int[] rssis = new int[size];                                                                // Массив rssi

        // Заполняем массивы
        for (int i=0; i<size; i++)                                                                  // Цикл на длину списка
        {
            names[i]     = (String)leListEntries.get(i).get(IDT_LIST_NAME);                         // Имя
            addresses[i] = (String)leListEntries.get(i).get(IDT_LIST_ADDRESS);                      // Адрес
            rssis[i]     = (Integer)leListEntries.get(i).get(IDT_LIST_RSSIINT);                     // rssi (сила сигнала)
        }

        bundle.putStringArray("LELIST_NAME_KEY", names);                                            // Сохранить массив имен
        bundle.putStringArray("LELIST_ADDRESSS_KEY", addresses);                                    // Сохранить массив адресов
        bundle.putIntArray("LELIST_RSSI_KEY", rssis);                                            // Сохранить массив

    }


    //**********************************************************************************************
    //
    //                            Публичный метод restoreLister()
    //                  Восстановление состояния списка BLE-устройств из Bundle
    //
    //  Входные данные: bundle  - указатель на Bundle для восстановления данных
    //
    //**********************************************************************************************
    public void restoreLister(Bundle bundle)
    {
        int size = bundle.getStringArray("LELIST_NAME_KEY").length;                                 // Определить размер списка по размеру массива имен

        // Восстанавливаем список
        for (int i=0; i<size; i++)                                                                  // Цикл на длину списка
        {
            leListEntries.add(new LeListEntry(                                                      // Добавить новую запись в список BLE-устройств,
                    bundle.getStringArray("LELIST_NAME_KEY")[i],                        // восстановленную из трех соответствующих массивов
                    bundle.getStringArray("LELIST_ADDRESSS_KEY")[i],                    // в Bundle
                    bundle.getIntArray   ("LELIST_RSSI_KEY")[i]
            ));
        }

        simpleAdapter.notifyDataSetChanged();                                                       // Указать адаптеру, что список изменился

    }






}

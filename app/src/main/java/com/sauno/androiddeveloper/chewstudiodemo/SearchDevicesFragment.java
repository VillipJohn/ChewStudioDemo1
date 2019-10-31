package com.sauno.androiddeveloper.chewstudiodemo;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class SearchDevicesFragment extends Fragment {
    RelativeLayout relativeLayout;
    TextView deviceNameTextView;

    ProgressBar pbProgressBar;

    String currentActivityString;

    SharedPreferences mSharedPreferences;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.search_devices_fragment, container, false);
        relativeLayout = rootView.findViewById(R.id.relativeLayout);
        deviceNameTextView = rootView.findViewById(R.id.device_name);
        //deviceAdress = rootView.findViewById(R.id.device_address);

        pbProgressBar = rootView.findViewById(R.id.progressBar1);

        mSharedPreferences = getActivity().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        currentActivityString = mSharedPreferences.getString("currentActivityForService", "");

        //activity = (ChoiceOfDishesActivity) getActivity();

        ImageView searchDevicesButtonImageView = rootView.findViewById(R.id.searchDevicesButtonImageView);
        searchDevicesButtonImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onScanning();
            }
        });

        relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        getConnectedDevice();

        return rootView;
    }

    private void onScanning() {
        switch(currentActivityString) {
            case "MainActivity":
                MainActivity mainActivity = (MainActivity) getActivity();
                if (mainActivity.mLeServiceBound) {
                    if(!mainActivity.mLeService.getConnectStatus()) {
                        if (!mainActivity.mLeService.mScanning) {// Указать адаптеру, что список изменился

                            mainActivity.mLeService.startLeScan(MainActivity.SCAN_PERIOD, MainActivity.SCAN_STEP);       // Вызвать метод сканирования BLE-устройств
                        }
                    } else {
                        Toast.makeText(mainActivity, "Соединение уже существует", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case "SelectDesiredMealActivity":
                SelectDesiredMealActivity selectDesiredMealActivity = (SelectDesiredMealActivity) getActivity();
                if (selectDesiredMealActivity.mLeServiceBound) {
                    if(!selectDesiredMealActivity.mLeService.getConnectStatus()) {
                        if (!selectDesiredMealActivity.mLeService.mScanning) {// Указать адаптеру, что список изменился

                            selectDesiredMealActivity.mLeService.startLeScan(MainActivity.SCAN_PERIOD, MainActivity.SCAN_STEP);       // Вызвать метод сканирования BLE-устройств
                        }
                    } else {
                        Toast.makeText(selectDesiredMealActivity, "Соединение уже существует", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case "ChewingProcessActivity":
                ChewingProcessActivity chewingProcessActivity = (ChewingProcessActivity) getActivity();
                if (chewingProcessActivity.mLeServiceBound) {
                    if(!chewingProcessActivity.mLeService.getConnectStatus()) {
                        if (!chewingProcessActivity.mLeService.mScanning) {// Указать адаптеру, что список изменился

                            chewingProcessActivity.mLeService.startLeScan(MainActivity.SCAN_PERIOD, MainActivity.SCAN_STEP);       // Вызвать метод сканирования BLE-устройств
                        }
                    } else {
                        Toast.makeText(chewingProcessActivity, "Соединение уже существует", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case "BasketActivity":
                BasketActivity basketActivity = (BasketActivity) getActivity();
                if (basketActivity.mLeServiceBound) {
                    if(!basketActivity.mLeService.getConnectStatus()) {
                        if (!basketActivity.mLeService.mScanning) {// Указать адаптеру, что список изменился

                            basketActivity.mLeService.startLeScan(MainActivity.SCAN_PERIOD, MainActivity.SCAN_STEP);       // Вызвать метод сканирования BLE-устройств
                        }
                    } else {
                        Toast.makeText(basketActivity, "Соединение уже существует", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case "ChoiceOfDishesActivity":
                ChoiceOfDishesActivity choiceOfDishesActivity = (ChoiceOfDishesActivity) getActivity();
                if (choiceOfDishesActivity.mLeServiceBound) {
                    if(!choiceOfDishesActivity.mLeService.getConnectStatus()) {
                        if (!choiceOfDishesActivity.mLeService.mScanning) {// Указать адаптеру, что список изменился

                            choiceOfDishesActivity.mLeService.startLeScan(MainActivity.SCAN_PERIOD, MainActivity.SCAN_STEP);       // Вызвать метод сканирования BLE-устройств
                        }
                    } else {
                        Toast.makeText(choiceOfDishesActivity, "Соединение уже существует", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case "Dish18Activity":
                Dish18Activity dish18Activity = (Dish18Activity) getActivity();
                if (dish18Activity.mLeServiceBound) {
                    if(!dish18Activity.mLeService.getConnectStatus()) {
                        if (!dish18Activity.mLeService.mScanning) {// Указать адаптеру, что список изменился

                            dish18Activity.mLeService.startLeScan(MainActivity.SCAN_PERIOD, MainActivity.SCAN_STEP);       // Вызвать метод сканирования BLE-устройств
                        }
                    } else {
                        Toast.makeText(dish18Activity, "Соединение уже существует", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case "DishReplacementActivity":
                DishReplacementActivity dishReplacementActivity = (DishReplacementActivity) getActivity();
                if (dishReplacementActivity.mLeServiceBound) {
                    if(!dishReplacementActivity.mLeService.getConnectStatus()) {
                        if (!dishReplacementActivity.mLeService.mScanning) {// Указать адаптеру, что список изменился

                            dishReplacementActivity.mLeService.startLeScan(MainActivity.SCAN_PERIOD, MainActivity.SCAN_STEP);       // Вызвать метод сканирования BLE-устройств
                        }
                    } else {
                        Toast.makeText(dishReplacementActivity, "Соединение уже существует", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case "FavoriteDishesActivity":
                FavoriteDishesActivity favoriteDishesActivity = (FavoriteDishesActivity) getActivity();
                if (favoriteDishesActivity.mLeServiceBound) {
                    if(!favoriteDishesActivity.mLeService.getConnectStatus()) {
                        if (!favoriteDishesActivity.mLeService.mScanning) {// Указать адаптеру, что список изменился

                            favoriteDishesActivity.mLeService.startLeScan(MainActivity.SCAN_PERIOD, MainActivity.SCAN_STEP);       // Вызвать метод сканирования BLE-устройств
                        }
                    } else {
                        Toast.makeText(favoriteDishesActivity, "Соединение уже существует", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case "HomeMenuActivity":
                HomeMenuActivity homeMenuActivity = (HomeMenuActivity) getActivity();
                if (homeMenuActivity.mLeServiceBound) {
                    if(!homeMenuActivity.mLeService.getConnectStatus()) {
                        if (!homeMenuActivity.mLeService.mScanning) {// Указать адаптеру, что список изменился
                            homeMenuActivity.mLeService.startLeScan(MainActivity.SCAN_PERIOD, MainActivity.SCAN_STEP);       // Вызвать метод сканирования BLE-устройств
                        }
                    } else {
                        Toast.makeText(homeMenuActivity, "Соединение уже существует", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case "ListDishesActivity":
                ListDishesActivity listDishesActivity = (ListDishesActivity) getActivity();
                if (listDishesActivity.mLeServiceBound) {
                    if(!listDishesActivity.mLeService.getConnectStatus()) {
                        if (!listDishesActivity.mLeService.mScanning) {// Указать адаптеру, что список изменился

                            listDishesActivity.mLeService.startLeScan(MainActivity.SCAN_PERIOD, MainActivity.SCAN_STEP);       // Вызвать метод сканирования BLE-устройств
                        }
                    } else {
                        Toast.makeText(listDishesActivity, "Соединение уже существует", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case "ResultsOfEatingActivity":
                ResultsOfEatingActivity resultsOfEatingActivity = (ResultsOfEatingActivity) getActivity();
                if (resultsOfEatingActivity.mLeServiceBound) {
                    if(!resultsOfEatingActivity.mLeService.getConnectStatus()) {
                        if (!resultsOfEatingActivity.mLeService.mScanning) {// Указать адаптеру, что список изменился

                            resultsOfEatingActivity.mLeService.startLeScan(MainActivity.SCAN_PERIOD, MainActivity.SCAN_STEP);       // Вызвать метод сканирования BLE-устройств
                        }
                    } else {
                        Toast.makeText(resultsOfEatingActivity, "Соединение уже существует", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case "SetQuantityOfDishesActivity":
                SetQuantityOfDishesActivity setQuantityOfDishesActivity = (SetQuantityOfDishesActivity) getActivity();
                if (setQuantityOfDishesActivity.mLeServiceBound) {
                    if(!setQuantityOfDishesActivity.mLeService.getConnectStatus()) {
                        if (!setQuantityOfDishesActivity.mLeService.mScanning) {// Указать адаптеру, что список изменился

                            setQuantityOfDishesActivity.mLeService.startLeScan(MainActivity.SCAN_PERIOD, MainActivity.SCAN_STEP);       // Вызвать метод сканирования BLE-устройств
                        }
                    } else {
                        Toast.makeText(setQuantityOfDishesActivity, "Соединение уже существует", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
           /* case "UserPreferencesActivity":
                UserPreferencesActivity userPreferencesActivity = (UserPreferencesActivity) getContext();
                if (userPreferencesActivity.mLeServiceBound) {
                    if(!userPreferencesActivity.mLeService.getConnectStatus()) {
                        if (!userPreferencesActivity.mLeService.mScanning) {// Указать адаптеру, что список изменился

                            userPreferencesActivity.mLeService.startLeScan(MainActivity.SCAN_PERIOD, MainActivity.SCAN_STEP);       // Вызвать метод сканирования BLE-устройств
                        }
                    } else {
                        Toast.makeText(userPreferencesActivity, "Соединение уже существует", Toast.LENGTH_SHORT).show();
                    }
                }
                break;*/
            case "UserProfileActivity":
                UserProfileActivity userProfileActivity = (UserProfileActivity) getActivity();
                if (userProfileActivity.mLeServiceBound) {
                    if(!userProfileActivity.mLeService.getConnectStatus()) {
                        if (!userProfileActivity.mLeService.mScanning) {// Указать адаптеру, что список изменился
                            userProfileActivity.mLeService.startLeScan(MainActivity.SCAN_PERIOD, MainActivity.SCAN_STEP);       // Вызвать метод сканирования BLE-устройств
                        }
                    } else {
                        Toast.makeText(userProfileActivity, "Соединение уже существует", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putBoolean("isVisibleSearchFragment", true);

        editor.apply();

        getConnectedDevice();
    }

    public void setUpdate(String name) {
        relativeLayout.setVisibility(View.VISIBLE);
        deviceNameTextView.setText(name);
        pbProgressBar.setProgress(100);
    }


    private void getConnectedDevice() {
        switch(currentActivityString) {
            case "MainActivity":
                MainActivity mainActivity = (MainActivity) getActivity();
                if(mainActivity.mLeService.getConnectStatus()) {
                    relativeLayout.setVisibility(View.VISIBLE);
                    String deviceName = mainActivity.mLeService.deviceName;
                    deviceNameTextView.setText(deviceName);
                }
                break;
            case "SelectDesiredMealActivity":
                SelectDesiredMealActivity selectDesiredMealActivity = (SelectDesiredMealActivity) getActivity();
                if(selectDesiredMealActivity.mLeService.getConnectStatus()) {
                    relativeLayout.setVisibility(View.VISIBLE);
                    String deviceName = selectDesiredMealActivity.mLeService.deviceName;
                    deviceNameTextView.setText(deviceName);
                }
                break;
            case "ChewingProcessActivity":
                ChewingProcessActivity chewingProcessActivity = (ChewingProcessActivity) getActivity();
                if(chewingProcessActivity.mLeService.getConnectStatus()) {
                    relativeLayout.setVisibility(View.VISIBLE);
                    String deviceName = chewingProcessActivity.mLeService.deviceName;
                    deviceNameTextView.setText(deviceName);
                }
                break;
            case "BasketActivity":
                BasketActivity basketActivity = (BasketActivity) getActivity();
                if(basketActivity.mLeService.getConnectStatus()) {
                    relativeLayout.setVisibility(View.VISIBLE);
                    String deviceName = basketActivity.mLeService.deviceName;
                    deviceNameTextView.setText(deviceName);
                }
                break;
            case "ChoiceOfDishesActivity":
                ChoiceOfDishesActivity choiceOfDishesActivity = (ChoiceOfDishesActivity) getActivity();
                if(choiceOfDishesActivity.mLeService.getConnectStatus()) {
                    relativeLayout.setVisibility(View.VISIBLE);
                    String deviceName = choiceOfDishesActivity.mLeService.deviceName;
                    deviceNameTextView.setText(deviceName);
                }
                break;
            case "Dish18Activity":
                Dish18Activity dish18Activity = (Dish18Activity) getActivity();
                if(dish18Activity.mLeService.getConnectStatus()) {
                    relativeLayout.setVisibility(View.VISIBLE);
                    String deviceName = dish18Activity.mLeService.deviceName;
                    deviceNameTextView.setText(deviceName);
                }
                break;
            case "DishReplacementActivity":
                DishReplacementActivity dishReplacementActivity = (DishReplacementActivity) getActivity();
                if(dishReplacementActivity.mLeService.getConnectStatus()) {
                    relativeLayout.setVisibility(View.VISIBLE);
                    String deviceName = dishReplacementActivity.mLeService.deviceName;
                    deviceNameTextView.setText(deviceName);
                }
                break;
            case "FavoriteDishesActivity":
                FavoriteDishesActivity favoriteDishesActivity = (FavoriteDishesActivity) getActivity();
                if(favoriteDishesActivity.mLeService.getConnectStatus()) {
                    relativeLayout.setVisibility(View.VISIBLE);
                    String deviceName = favoriteDishesActivity.mLeService.deviceName;
                    deviceNameTextView.setText(deviceName);
                }
                break;
            case "HomeMenuActivity":
                HomeMenuActivity homeMenuActivity = (HomeMenuActivity) getActivity();
                if(homeMenuActivity.mLeService.getConnectStatus()) {
                    relativeLayout.setVisibility(View.VISIBLE);
                    String deviceName = homeMenuActivity.mLeService.deviceName;
                    deviceNameTextView.setText(deviceName);
                }
                break;
            case "ListDishesActivity":
                ListDishesActivity listDishesActivity = (ListDishesActivity) getActivity();
                if(listDishesActivity.mLeService.getConnectStatus()) {
                    relativeLayout.setVisibility(View.VISIBLE);
                    String deviceName = listDishesActivity.mLeService.deviceName;
                    deviceNameTextView.setText(deviceName);
                }
                break;
            case "ResultsOfEatingActivity":
                ResultsOfEatingActivity resultsOfEatingActivity = (ResultsOfEatingActivity) getActivity();
                if(resultsOfEatingActivity.mLeService.getConnectStatus()) {
                    relativeLayout.setVisibility(View.VISIBLE);
                    String deviceName = resultsOfEatingActivity.mLeService.deviceName;
                    deviceNameTextView.setText(deviceName);
                }
                break;
            case "SetQuantityOfDishesActivity":
                SetQuantityOfDishesActivity setQuantityOfDishesActivity = (SetQuantityOfDishesActivity) getActivity();
                if(setQuantityOfDishesActivity.mLeService.getConnectStatus()) {
                    relativeLayout.setVisibility(View.VISIBLE);
                    String deviceName = setQuantityOfDishesActivity.mLeService.deviceName;
                    deviceNameTextView.setText(deviceName);
                }
                break;
          /*  case "UserPreferencesActivity":
                UserPreferencesActivity userPreferencesActivity = (UserPreferencesActivity) getContext();
                if(userPreferencesActivity.mLeService.getConnectStatus()) {
                    relativeLayout.setVisibility(View.VISIBLE);
                    String deviceName = userPreferencesActivity.mLeService.deviceName;
                    deviceNameTextView.setText(deviceName);
                }
                break;*/
            case "UserProfileActivity":
                UserProfileActivity userProfileActivity = (UserProfileActivity) getActivity();
                if(userProfileActivity.mLeService.getConnectStatus()) {
                    relativeLayout.setVisibility(View.VISIBLE);
                    String deviceName = userProfileActivity.mLeService.deviceName;
                    deviceNameTextView.setText(deviceName);
                }
                break;
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putBoolean("isVisibleSearchFragment", false);

        editor.apply();
    }
}

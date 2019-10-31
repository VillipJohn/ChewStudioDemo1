package com.sauno.androiddeveloper.chewstudiodemo;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Android developer on 23.03.2018.
 */

public class SettingsFragment extends Fragment {
    RelativeLayout relativeLayout;
    TextView deviceNameTextView;
    //TextView deviceAdress;

    MainActivity mainActivity;

    ProgressBar pbProgressBar;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.search_devices_fragment, container, false);

        relativeLayout = rootView.findViewById(R.id.relativeLayout);
        deviceNameTextView = rootView.findViewById(R.id.device_name);
        //deviceAdress = rootView.findViewById(R.id.device_address);

        pbProgressBar = rootView.findViewById(R.id.progressBar1);


        mainActivity = (MainActivity) getActivity();

        ImageView searchDevicesButtonImageView = rootView.findViewById(R.id.searchDevicesButtonImageView);
        searchDevicesButtonImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mainActivity.mLeServiceBound)                                                                        // Если есть привязка к BLE-сервису, то
                {
                    if(!mainActivity.mLeService.getConnectStatus()) {
                        if (!mainActivity.mLeService.mScanning)                                                              // Если еще не идет процесс сканирования устройств, то
                        {
                        /*mLeLister.leListEntries.clear();                                                    // Очистить список BLE-устройств
                        mLeLister.simpleAdapter.notifyDataSetChanged();    */                                 // Указать адаптеру, что список изменился

                            mainActivity.mLeService.startLeScan(MainActivity.SCAN_PERIOD, MainActivity.SCAN_STEP);       // Вызвать метод сканирования BLE-устройств
                            // (метод работает параллельно, и сразу отдаст управление обратно)
                        }
                    } else {
                        Toast.makeText(mainActivity, "Соединение уже существует", Toast.LENGTH_SHORT).show();
                    }

                }

            }
        });

        relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        getConnectedDevice();

        setupActionBar();

        return rootView;
    }

    private void setupActionBar() {
        ActionBar actionBar = ((MainActivity)getActivity()).getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setTitle("Настройки");
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setElevation(0);
        }
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onResume() {
        super.onResume();

        getConnectedDevice();
    }

    public void setUpdate(String name, String address) {
        relativeLayout.setVisibility(View.VISIBLE);
        deviceNameTextView.setText(name);
        //deviceAdress.setText(address);

        mainActivity.onConnected(address);

        pbProgressBar.setProgress(100);
    }


    private void getConnectedDevice() {
        if(mainActivity.mLeService.getConnectStatus()) {
            relativeLayout.setVisibility(View.VISIBLE);
            String deviceName = mainActivity.mLeService.deviceName;
            deviceNameTextView.setText(deviceName);
        }
    }



}

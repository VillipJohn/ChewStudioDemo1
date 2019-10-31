package com.sauno.androiddeveloper.chewstudiodemo;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MainSettingsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MainSettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainSettingsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    MainActivity mainActivity;
    ActionBar actionBar;

    public MainSettingsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MainSettingsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MainSettingsFragment newInstance(String param1, String param2) {
        MainSettingsFragment fragment = new MainSettingsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        mainActivity = (MainActivity)getActivity();

        //setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main_settings, container, false);

        Button userSettingsButton = view.findViewById(R.id.userSettingsButton);
        Button soundSettingsButton = view.findViewById(R.id.soundSettingsButton);
        Button deviceSettingsButton = view.findViewById(R.id.deviceSettingsButton);

        userSettingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    String onButton = "onUserSettingsButton";
                    mListener.onFragmentInteraction(onButton);
                }
            }
        });

        soundSettingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    String onButton = "onSoundSettingsButton";
                    mListener.onFragmentInteraction(onButton);
                }
            }
        });

        deviceSettingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    String onButton = "onDeviceSettingsButton";
                    mListener.onFragmentInteraction(onButton);
                }
            }
        });

        setupActionBar();

        return view;
    }



    private void setupActionBar() {
        /*mainActivity.toolbar.setContentInsetsAbsolute(0,0);*/

        actionBar = mainActivity.getSupportActionBar();

        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setTitle("Настройки");
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setElevation(0);



            /*mainActivity.toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    Log.d("MainSettingsFragment", menuItem.toString());

                    int id = menuItem.getItemId();
                    if (id == android.R.id.home) {

                        getActivity().getSupportFragmentManager().beginTransaction().remove(MainSettingsFragment.this).commit();
                        return true;
                    }

                    return false;
                }
            });*/

           /* actionBar.set

            toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    return false;
                }
            });*/
        }
    }


   /* @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        //menu.clear();

        inflater.inflate(R.menu.menu_host, menu);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d("MainSettingsFragment", item.toString());

        int id = item.getItemId();
        if (id == android.R.id.home) {

            getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
            return true;
        }



        return super.onOptionsItemSelected(item);
    }*/


    /*// TODO: Rename method, update argument and hook method into UI event
    public void onDeviceSettingsButton(View view) {
        if (mListener != null) {
            mListener.onFragmentInteraction();
        }
    }*/

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;

//        ((MainActivity) getActivity()).getSupportActionBar().setTitle("SmartChew");
        actionBar.setDisplayHomeAsUpEnabled(false);

        mainActivity.recreationToggle();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(String onButton);
    }
}

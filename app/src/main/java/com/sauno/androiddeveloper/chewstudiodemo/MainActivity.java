package com.sauno.androiddeveloper.chewstudiodemo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sauno.androiddeveloper.chewstudiodemo.data.DatabaseCreateHelper;
import com.sauno.androiddeveloper.chewstudiodemo.dialogFragments.SelectWhatIsDisplayedDialogFragment;
import com.sauno.androiddeveloper.chewstudiodemo.navigationItemFragments.AboutAppFragment;
import com.sauno.androiddeveloper.chewstudiodemo.navigationItemFragments.DefaultResetFragment;
import com.sauno.androiddeveloper.chewstudiodemo.navigationItemFragments.FavoriteDishesFragment;
import com.sauno.androiddeveloper.chewstudiodemo.navigationItemFragments.SoundsAndNotificationsFragment;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    SharedPreferences mSharedPreferences;

    TextView mEatenCountTextView;
    TextView mSpentCountTextView;
    TextView mRemainCountTextView;

    TextView mUserNameHeaderTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Вызываем метод записывающий базу для дальнейшего использования
        write_db();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setElevation(0);
        }

//        getSupportActionBar().setLogo(R.drawable.ic_menu_user_settings_dark);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        ViewPager mViewPager = findViewById(R.id.viewPager);

        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = findViewById(R.id.tabs);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

        LinearLayout mEatenSpentKeepLinearLayout = findViewById(R.id.eatenSpentKeepLinearLayout);
        mEatenSpentKeepLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SelectWhatIsDisplayedDialogFragment mSelectWhatIsDisplayedDialogFragment = new SelectWhatIsDisplayedDialogFragment();
                mSelectWhatIsDisplayedDialogFragment.show(getSupportFragmentManager(), "dialogSelectWhatIsDisplayed");
            }
        });

        mSharedPreferences = getPreferences(Context.MODE_PRIVATE);

        mEatenCountTextView = findViewById(R.id.eatenCountTextView);
        mSpentCountTextView = findViewById(R.id.spentCountTextView);
        mRemainCountTextView = findViewById(R.id.remainCountTextView);

        mUserNameHeaderTextView = findViewById(R.id.userNameHeaderTextView);
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
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            FavoriteDishesFragment fragment = new FavoriteDishesFragment();
            fragmentTransaction.add(R.id.drawer_layout, fragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();

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
            fragmentTransaction.add(R.id.drawer_layout, fragment);
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


    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            //return PlaceholderFragment.newInstance(position + 1);

            switch (position) {
                case 0:
                    return new Tab1FoodFragment();
                case 1:
                    return new Tab2StatisticsFragment();
                case 2:
                    return new Tab3Devices();
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch(position) {
                case 0:
                    return getString(R.string.food_page_title);
                case 1:
                    return getString(R.string.statistics_page_title);
                case 2:
                    return getString(R.string.devices_page_title);
                default:
                    return null;
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        String eatenCountString = "";
        String spentCountString = "";
        String remainCountString = "";

        boolean isCheckedCalories = mSharedPreferences.getBoolean("IsCheckedCalories", false);
        boolean isCheckedProteins = mSharedPreferences.getBoolean("IsCheckedProteins", false);
        boolean isCheckedFats = mSharedPreferences.getBoolean("IsCheckedFats", false);
        boolean isCheckedCarbohydrates = mSharedPreferences.getBoolean("IsCheckedCarbohydrates", false);


        if (isCheckedCalories) {
            eatenCountString = "K - 100";
            spentCountString = "K - 50";
            remainCountString = "K - 50";
        } if (isCheckedProteins) {
            if (eatenCountString.equals("")) {
                eatenCountString = "Б - 200";
                spentCountString = "Б - 100";
                remainCountString = "Б - 100";
            } else {
                eatenCountString = eatenCountString + "\n" + "Б - 200";
                spentCountString = spentCountString + "\n" + "Б - 100";
                remainCountString = remainCountString + "\n" + "Б - 100";
            }
        } if(isCheckedFats) {
            if (eatenCountString.equals("")) {
                eatenCountString = "Ж - 300";
                spentCountString = "Ж - 150";
                remainCountString = "Ж - 150";
            } else {
                eatenCountString = eatenCountString + "\n" + "Ж - 300";
                spentCountString = spentCountString + "\n" + "Ж - 150";
                remainCountString = remainCountString + "\n" + "Ж - 150";
            }
        } if(isCheckedCarbohydrates) {
            if (eatenCountString.equals("")) {
                eatenCountString = "У - 400";
                spentCountString = "У - 200";
                remainCountString = "У - 200";
            } else {
                eatenCountString = eatenCountString + "\n" + "У - 400";
                spentCountString = spentCountString + "\n" + "У - 200";
                remainCountString = remainCountString + "\n" + "У - 200";
            }
        } if(!isCheckedCalories && !isCheckedProteins && !isCheckedFats && !isCheckedCarbohydrates) {
            eatenCountString = "---";
            spentCountString = "---";
            remainCountString = "---";
        }

        mEatenCountTextView.setText(eatenCountString);
        mSpentCountTextView.setText(spentCountString);
        mRemainCountTextView.setText(remainCountString);

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        String user = sp.getString("userNamePref", "");

        if(user != null && !user.equals("")) {
            //mUserNameHeaderTextView.setText("Иван");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_user_settings) {
            Intent intent = new Intent(this, UserProfileActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void write_db() {
        DatabaseCreateHelper databaseCreateHelper = new DatabaseCreateHelper(getApplicationContext());
        // создаем базу данных
        databaseCreateHelper.create_db();
    }

}

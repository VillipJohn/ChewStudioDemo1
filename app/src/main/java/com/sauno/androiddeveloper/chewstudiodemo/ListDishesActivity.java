package com.sauno.androiddeveloper.chewstudiodemo;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.sauno.androiddeveloper.chewstudiodemo.adapter.ListDishesAdapter;
import com.sauno.androiddeveloper.chewstudiodemo.data.DatabaseCreateHelper;
import com.sauno.androiddeveloper.chewstudiodemo.data.DishDBHelper;

import java.util.ArrayList;
import java.util.List;

public class ListDishesActivity extends AppCompatActivity {
    SQLiteDatabase db;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private String[] myDataset;
    String chosenCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_dishes);

        chosenCategory = getIntent().getStringExtra("category");

        getDishesFromDB();

        setupActionBar();

        mRecyclerView = findViewById(R.id.listDishesRecyclerView);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        mAdapter = new ListDishesAdapter(myDataset);
        mRecyclerView.setAdapter(mAdapter);
    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setTitle(chosenCategory);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setElevation(0);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_host, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void getDishesFromDB() {
        DatabaseCreateHelper databaseCreateHelper = new DatabaseCreateHelper(getApplicationContext());
        //SQLiteDatabase db;
        try {
            db = databaseCreateHelper.getWritableDatabase();
        }
        catch (SQLiteException ex){
            db = databaseCreateHelper.getReadableDatabase();
        }

        String[] projection = {
                DishDBHelper.COLUMN_DESCRIPTION,
                DishDBHelper.COLUMN_CATEGORY
        };

        // Filter results WHERE "title" = 'My Title'
        String selection = DishDBHelper.COLUMN_CATEGORY + " = ?";
        String[] selectionArgs = {chosenCategory};


        Cursor cursor = db.query(
                DishDBHelper.TABLE,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        List<String> categories = new ArrayList<>();

        if (cursor.moveToFirst()) {

            do {
                String category = cursor.getString(cursor.getColumnIndexOrThrow(DishDBHelper.COLUMN_DESCRIPTION));
                categories.add(category);
                Log.i("MyLog",category + "\n");

            }while (cursor.moveToNext());

        }

        myDataset = categories.toArray(new String[categories.size()]);

        cursor.close();
    }

    @Override
    public void onDestroy(){
        db.close();
        super.onDestroy();
        // Закрываем подключение и курсор
        //db.close();
        //userCursor.close();
    }
}

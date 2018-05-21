package com.sauno.androiddeveloper.chewstudiodemo.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class RestaurantDBHelper extends SQLiteOpenHelper {
    private static String DB_NAME = "maindb.db";
    private static final int SCHEMA = 1;

    public static final String TABLE = "Restaurants";

    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_NAME = "Name_Restaurant";
    public static final String COLUMN_DESCRIPTION = "Description";


    public RestaurantDBHelper(Context context) {
        super (context, DB_NAME, null, SCHEMA);
    }
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}

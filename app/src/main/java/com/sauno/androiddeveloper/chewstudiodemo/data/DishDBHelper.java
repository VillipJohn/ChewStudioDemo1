package com.sauno.androiddeveloper.chewstudiodemo.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DishDBHelper extends SQLiteOpenHelper{
    private static String DB_PATH; // полный путь к базе данных
    private static String DB_NAME = "maindb.db";
    private static final int SCHEMA = 1; // версия базы данных
    public static final String TABLE = "Dishes"; // название таблицы в бд
    // названия столбцов
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_DESCRIPTION = "Description";
    public static final String COLUMN_CATEGORY = "Category";
    public static final String COLUMN_CALORIC = "Caloric";
    public static final String COLUMN_PROTEINS = "Proteins";
    public static final String COLUMN_FATS = "Fats";
    public static final String COLUMN_CARBS = "Carbs";
    public static final String COLUMN_COMPATIBILITY_1 = "Compatibility_1";
    public static final String COLUMN_COMPATIBILITY_2 = "Compatibility_2";
    public static final String COLUMN_COMPATIBILITY_3 = "Compatibility_3";
    public static final String COLUMN_COMPATIBILITY_4 = "Compatibility_4";
    public static final String COLUMN_COMPATIBILITY_5 = "Compatibility_5";
    public static final String COLUMN_COMPATIBILITY_6 = "Compatibility_6";
    public static final String COLUMN_COMPATIBILITY_7 = "Compatibility_7";
    public static final String COLUMN_GLOBAL_CATEGORY = "Ingreds_1";
    public static final String COLUMN_RESTAURANT = "Restaurant";



    public DishDBHelper(Context context) {
        super (context, DB_NAME, null, SCHEMA);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    /*public List<Dish> categoryOfDishesList() {

    }*/
}

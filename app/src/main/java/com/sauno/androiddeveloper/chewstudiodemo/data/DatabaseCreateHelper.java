package com.sauno.androiddeveloper.chewstudiodemo.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class DatabaseCreateHelper extends SQLiteOpenHelper {
    private static String DB_PATH; // полный путь к базе данных
    private static String DB_NAME = "maindb.db";
    private static final int SCHEMA = 1; // версия базы данных
    //static final String TABLE = "users"; // название таблицы в бд
    // названия столбцов
    //static final String COLUMN_ID = "_id";
    //static final String COLUMN_NAME = "name";
    //static final String COLUMN_YEAR = "year";
    private Context myContext;

    public DatabaseCreateHelper(Context context) {
        super(context, DB_NAME, null, SCHEMA);
        myContext = context;
        //DB_PATH =context.getFilesDir().getPath() + DB_NAME;

        DB_PATH = myContext.getDatabasePath(DB_NAME).getAbsolutePath();

        Log.i("MyLog", DB_PATH);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion,  int newVersion) {
    }

    public void create_db(){
        InputStream myInput = null;
        OutputStream myOutput = null;
        try {
            File file = new File(DB_PATH);
            if (!file.exists()) {
                this.getReadableDatabase();
                //получаем локальную бд как поток
                myInput = myContext.getAssets().open(DB_NAME);
                // Путь к новой бд
                String outFileName = DB_PATH;

                // Открываем пустую бд
                myOutput = new FileOutputStream(outFileName);

                // побайтово копируем данные
                byte[] buffer = new byte[1024];
                int length;
                while ((length = myInput.read(buffer)) > 0) {
                    myOutput.write(buffer, 0, length);
                }

                myOutput.flush();
                myOutput.close();
                myInput.close();
            }
        }
        catch(IOException ex){
            Log.d("DatabaseCreateHelper", ex.getMessage());
        }
    }
    /*public SQLiteDatabase open()throws SQLException {

        return SQLiteDatabase.openDatabase(DB_PATH, null, SQLiteDatabase.OPEN_READWRITE);
    }*/
}

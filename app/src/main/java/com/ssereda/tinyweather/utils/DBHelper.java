package com.ssereda.tinyweather.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
    public static final String ID = "_id";
    public static final String TABLE_PLACES = "tiny_weather_places";
    public static final String PLACES_ID = "places_id";
    public static final String PLACES_COUNTRY = "places_country";
    public static final String PLACES_REGION = "places_region";
    public static final String PLACES_NAME = "places_name";
    private static final String DATABASE_NAME = "tiny_weather";
    private static final int DATABASE_VERSION = 1;
    private static DBHelper instance;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static synchronized DBHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DBHelper(context.getApplicationContext());
        }
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_PLACES + " ("
                + ID + " integer primary key autoincrement,"
                + PLACES_ID + " text, "
                + PLACES_COUNTRY + " text, "
                + PLACES_REGION + " text, "
                + PLACES_NAME + " text"
                + ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}

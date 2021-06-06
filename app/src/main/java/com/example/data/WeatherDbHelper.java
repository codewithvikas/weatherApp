package com.example.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class WeatherDbHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "weather.db";

    public static final int DATABASE_VERSION = 1;

    public WeatherDbHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        final String SQL_CREATE_WEATHER_TABLE =
                "CREATE TABLE "+
                        WeatherContract.WeatherEntry.TABLE_NAME +
                        " ("+
                        WeatherContract.WeatherEntry._ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "+

                        WeatherContract.WeatherEntry.COLUMN_DATE+" INTEGER, "+

                        WeatherContract.WeatherEntry.COLUMN_WEATHER_ID + " INTEGER, "+

                        WeatherContract.WeatherEntry.COLUMN_MIN_TEMP +" REAL, "+
                        WeatherContract.WeatherEntry.COLUMN_MAX_TEMP +" REAL, "+

                        WeatherContract.WeatherEntry.COLUMN_HUMIDITY +" REAL, "+
                        WeatherContract.WeatherEntry.COLUMN_PRESSURE +" REAL, "+

                        WeatherContract.WeatherEntry.COLUMN_WIND_SPEED +" REAL, "+
                        WeatherContract.WeatherEntry.COLUMN_DEGREE +" REAL "+
                        ");";

        db.execSQL(SQL_CREATE_WEATHER_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}

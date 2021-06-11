package com.example.aac;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

@Database(entities = {WeatherEntity.class},version = 1,exportSchema = false)
@TypeConverters(DateConverter.class)
public abstract class WeatherDatabase extends RoomDatabase {

    private static final Object LOCK = new Object();
    private static final String DATABASE_NAME = "weatherdb";

    public static WeatherDatabase sInstance;

    public static WeatherDatabase getInstance(Context context){
        synchronized (LOCK){
            if (sInstance==null){
                sInstance = Room.databaseBuilder(context.getApplicationContext(),WeatherDatabase.class,DATABASE_NAME)
                        .allowMainThreadQueries()// Enabled just only to test Room.Never allow it for release version.
                        .build();
            }

        }
        return sInstance;
    }

    public abstract WeatherDao weatherDao();
}

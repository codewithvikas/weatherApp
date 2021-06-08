package com.example.utils;

import android.content.ContentValues;
import android.content.Context;

import com.example.data.WeatherContract;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class FakeDataUtils {

    private static int[] weatherIds = {200,300,500,700,900,962};

    private static ContentValues createTestWeatherContentValues(long date){
        ContentValues testContent = new ContentValues();
        testContent.put(WeatherContract.WeatherEntry.COLUMN_DATE,date);
        testContent.put(WeatherContract.WeatherEntry.COLUMN_DEGREE,Math.random()*2);
        double maxTemp = Math.random() * 100;
        testContent.put(WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,maxTemp);
        testContent.put(WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,maxTemp-Math.random()*10);
        testContent.put(WeatherContract.WeatherEntry.COLUMN_HUMIDITY,Math.random() * 100);
        testContent.put(WeatherContract.WeatherEntry.COLUMN_PRESSURE,870+Math.random()*100);
        testContent.put(WeatherContract.WeatherEntry.COLUMN_WIND_SPEED,Math.random()*30);
        testContent.put(WeatherContract.WeatherEntry.COLUMN_WEATHER_ID, weatherIds[(int) ((Math.random()*10)%5)]);
        return testContent;
    }

    public static void insertFakeData(Context context){
        long today = WeatherDateUtils.normalizeDate(System.currentTimeMillis());
        List<ContentValues> fakeValues = new ArrayList<>();
        for (int i = 0;i<7;i++){
            fakeValues.add(createTestWeatherContentValues(today+ TimeUnit.DAYS.toMillis(i)));
        }
        context.getContentResolver().bulkInsert(WeatherContract.WeatherEntry.CONTENT_URI,fakeValues.toArray(new ContentValues[7]));
    }
}

package com.example.data;

import android.net.Uri;
import android.provider.BaseColumns;

import com.example.utils.WeatherDateUtils;

public class WeatherContract {

    public static final String CONTENT_AUTHORITY = "com.example.weather.data";
    public static final String WEATHER_PATH = "weather";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://"+CONTENT_AUTHORITY);

    public static final class WeatherEntry  implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(WEATHER_PATH).build();
        public static final String TABLE_NAME = "weather";

        public static final String COLUMN_WEATHER_ID = "weather_id";

        public static final String COLUMN_DATE = "date";

        public static final String COLUMN_MIN_TEMP = "min";

        public static final String COLUMN_MAX_TEMP = "max";

        public static final String COLUMN_HUMIDITY = "humidity";

        public static final String COLUMN_PRESSURE = "pressure";

        public static final String COLUMN_WIND_SPEED = "wind";

        public static final String COLUMN_DEGREE = "degree";

        public static String getSqlSelectTodayOnwards(){
            long normalizeUtcNow = WeatherDateUtils.getNormalizedUtcDateForToday();
            return WeatherEntry.COLUMN_DATE+" >= "+normalizeUtcNow;
        }

        public static Uri buildWeatherUriWithDate(long date){
            return CONTENT_URI.buildUpon().appendPath(String.valueOf(date)).build();

        }
    }
}

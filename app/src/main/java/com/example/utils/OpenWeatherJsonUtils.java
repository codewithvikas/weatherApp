package com.example.utils;

import android.content.ContentValues;
import android.content.Context;

import com.example.aac.database.WeatherEntity;
import com.example.data.WeatherContract;
import com.example.data.WeatherPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public final class OpenWeatherJsonUtils {

    static final String OWM_MESSAGE_CODE = "cod";

    static final String OWM_CITY = "city";
    static final String OWM_CITY_NAME = "name";

    static final String OWM_COORD = "coord";
    static final String OWM_LAT = "lat";
    static final String OWM_LON = "lon";

    static final String OWM_LIST = "list";
    /* All temperatures are children of the "temp" object */
    static final String OWM_TEMPERATURE = "temp";

    /* Max temperature for the day */
    static final String OWM_MAX = "max";
    static final String OWM_MIN = "min";


    static final String OWM_PRESSURE = "pressure";

    static final String OWM_HUMIDITY = "humidity";

    static final String OWM_WEATHER = "weather";
    static final String OWM_WEATHER_ID = "id";

    static final String OWM_DESCRIPTION = "description";

    static final String OWM_WIND_SPEED = "speed";
    static final String OWN_WIND_DEG = "deg";

    /**
     * This method parses JSON from a web response and returns an array of Strings
     * describing the weather over various days from the forecast.
     * <p/>
     * Later on, we'll be parsing the JSON into structured data within the
     * getFullWeatherDataFromJson function, leveraging the data we have stored in the JSON. For
     * now, we just convert the JSON into human-readable strings.
     *
     * @param forecastJsonStr JSON response from server
     *
     * @return Array of Strings describing weather data
     *
     * @throws JSONException If JSON data cannot be properly parsed
     */
    public static String[] getSimpleWeatherStringsFromJson(Context context, String forecastJsonStr)
            throws JSONException {


        /* String array to hold each day's weather String */
        String[] parsedWeatherData = null;

        JSONObject forecastJson = new JSONObject(forecastJsonStr);

        /* Is there an error? */
        if (forecastJson.has(OWM_MESSAGE_CODE)) {
            int errorCode = forecastJson.getInt(OWM_MESSAGE_CODE);

            switch (errorCode) {
                case HttpURLConnection.HTTP_OK:
                    break;
                case HttpURLConnection.HTTP_NOT_FOUND:
                    /* Location invalid */
                    return null;
                default:
                    /* Server probably down */
                    return null;
            }
        }

        JSONArray weatherArray = forecastJson.getJSONArray(OWM_LIST);

        parsedWeatherData = new String[weatherArray.length()];

        long localDate = System.currentTimeMillis();
        long utcDate = WeatherDateUtils.getUTCDateFromLocal(localDate);
        long startDay = WeatherDateUtils.normalizeDate(utcDate);

        for (int i = 0; i < weatherArray.length(); i++) {
            String date;
            String highAndLow;

            /* These are the values that will be collected */
            long dateTimeMillis;
            double high;
            double low;
            String description;

            /* Get the JSON object representing the day */
            JSONObject dayForecast = weatherArray.getJSONObject(i);

            /*
             * We ignore all the datetime values embedded in the JSON and assume that
             * the values are returned in-order by day (which is not guaranteed to be correct).
             */
            dateTimeMillis = startDay + WeatherDateUtils.DAY_IN_MILLIS * i;
            date = WeatherDateUtils.getFriendlyDateString(context, dateTimeMillis, false);

            /*
             * Description is in a child array called "weather", which is 1 element long.
             * That element also contains a weather code.
             */
            JSONObject weatherObject =
                    dayForecast.getJSONArray(OWM_WEATHER).getJSONObject(0);
            description = weatherObject.getString(OWM_DESCRIPTION);

            /*
             * Temperatures are sent by Open Weather Map in a child object called "temp".
             *
             * Editor's Note: Try not to name variables "temp" when working with temperature.
             * It confuses everybody. Temp could easily mean any number of things, including
             * temperature, temporary and is just a bad variable name.
             */
            JSONObject temperatureObject = dayForecast.getJSONObject(OWM_TEMPERATURE);
            high = temperatureObject.getDouble(OWM_MAX);
            low = temperatureObject.getDouble(OWM_MIN);
            highAndLow = WeatherUtils.formatHighLows(context, high, low);

            parsedWeatherData[i] = date + " - " + description + " - " + highAndLow;
        }

        return parsedWeatherData;
    }

    /**
     * Parse the JSON and convert it into ContentValues that can be inserted into our database.
     *
     * @param context         An application context, such as a service or activity context.
     * @param forecastJsonStr The JSON to parse into ContentValues.
     *
     * @return An array of ContentValues parsed from the JSON.
     */
    public static ContentValues[] getFullWeatherDataFromJson(Context context, String forecastJsonStr) throws  JSONException{

        JSONObject jsonWeatherObject = new JSONObject(forecastJsonStr);

        if (jsonWeatherObject.has(OWM_MESSAGE_CODE)){
            int errorCode = jsonWeatherObject.getInt(OWM_MESSAGE_CODE);
            switch (errorCode){
                case HttpURLConnection.HTTP_OK:
                    break;
                case HttpURLConnection.HTTP_NOT_FOUND:
                    return null;
                default:
                    return null;
            }
        }

        JSONArray jsonWeatherArray = jsonWeatherObject.getJSONArray(OWM_LIST);
        JSONObject cityJson = jsonWeatherObject.getJSONObject(OWM_CITY);
        JSONObject coord = cityJson.getJSONObject(OWM_COORD);
        double lat = coord.getDouble(OWM_LAT);
        double lon = coord.getDouble(OWM_LON);
        String city = cityJson.getString(OWM_CITY_NAME);

        WeatherPreferences.setLocationDetails(context,city,lat,lon);

        ContentValues[] weatherContentValues = new ContentValues[jsonWeatherArray.length()];

        long normalizedUtcStartDay = WeatherDateUtils.getNormalizedUtcDateForToday();

        for (int i =0;i<jsonWeatherArray.length();i++){

            long dateTimeMilli;
            double pressure;
            double humidity;
            double windSpeed;
            double windDirection;
            double high;
            double low;
            int weatherId;
            String description;

            JSONObject dayForecast = jsonWeatherArray.getJSONObject(i);

            dateTimeMilli = normalizedUtcStartDay + WeatherDateUtils.DAY_IN_MILLIS*i;

            pressure = dayForecast.getDouble(OWM_PRESSURE);
            humidity = dayForecast.getDouble(OWM_HUMIDITY);

            windSpeed = dayForecast.getDouble(OWM_WIND_SPEED);
            windDirection = dayForecast.getDouble(OWN_WIND_DEG);

             JSONObject weather = dayForecast.getJSONObject(OWM_WEATHER);

             description = weather.getString(OWM_DESCRIPTION);
             weatherId = weather.getInt(OWM_WEATHER_ID);

            JSONObject temp = dayForecast.getJSONObject(OWM_TEMPERATURE);

            high = temp.getDouble(OWM_MAX);
            low = temp.getDouble(OWM_MIN);

            ContentValues contentValues = new ContentValues();
            contentValues.put(WeatherContract.WeatherEntry.COLUMN_DATE,dateTimeMilli);
            contentValues.put(WeatherContract.WeatherEntry.COLUMN_PRESSURE,pressure);
            contentValues.put(WeatherContract.WeatherEntry.COLUMN_HUMIDITY,humidity);
            contentValues.put(WeatherContract.WeatherEntry.COLUMN_WIND_SPEED,windSpeed);
            contentValues.put(WeatherContract.WeatherEntry.COLUMN_DEGREE,windDirection);
            contentValues.put(WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,high);
            contentValues.put(WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,low);
            contentValues.put(WeatherContract.WeatherEntry.COLUMN_WEATHER_ID,weatherId);

            weatherContentValues[i] = contentValues;
        }
        return weatherContentValues;
    }

    public static List<WeatherEntity> getFullWeatherEntitiesFromJson(Context context, String forecastJsonStr) throws  JSONException{

        JSONObject jsonWeatherObject = new JSONObject(forecastJsonStr);

        if (jsonWeatherObject.has(OWM_MESSAGE_CODE)){
            int errorCode = jsonWeatherObject.getInt(OWM_MESSAGE_CODE);
            switch (errorCode){
                case HttpURLConnection.HTTP_OK:
                    break;
                case HttpURLConnection.HTTP_NOT_FOUND:
                    return null;
                default:
                    return null;
            }
        }

        JSONArray jsonWeatherArray = jsonWeatherObject.getJSONArray(OWM_LIST);
        JSONObject cityJson = jsonWeatherObject.getJSONObject(OWM_CITY);
        JSONObject coord = cityJson.getJSONObject(OWM_COORD);
        double lat = coord.getDouble(OWM_LAT);
        double lon = coord.getDouble(OWM_LON);
        String city = cityJson.getString(OWM_CITY_NAME);

        WeatherPreferences.setLocationDetails(context,city,lat,lon);

        List<WeatherEntity> weatherEntities = new ArrayList<>();

        long normalizedUtcStartDay = WeatherDateUtils.getNormalizedUtcDateForToday();

        for (int i =0;i<jsonWeatherArray.length();i++){

            long dateTimeMilli;
            double pressure;
            double humidity;
            double windSpeed;
            double windDirection;
            double high;
            double low;
            int weatherId;
            String description;

            JSONObject dayForecast = jsonWeatherArray.getJSONObject(i);

            dateTimeMilli = normalizedUtcStartDay + WeatherDateUtils.DAY_IN_MILLIS*i;

            pressure = dayForecast.getDouble(OWM_PRESSURE);
            humidity = dayForecast.getDouble(OWM_HUMIDITY);

            windSpeed = dayForecast.getDouble(OWM_WIND_SPEED);
            windDirection = dayForecast.getDouble(OWN_WIND_DEG);

            JSONObject weather = dayForecast.getJSONArray(OWM_WEATHER).getJSONObject(0);

            weatherId = weather.getInt(OWM_WEATHER_ID);

            JSONObject temp = dayForecast.getJSONObject(OWM_TEMPERATURE);

            high = temp.getDouble(OWM_MAX);
            low = temp.getDouble(OWM_MIN);

           WeatherEntity weatherEntity = new WeatherEntity(weatherId,new Date(dateTimeMilli),high,low,humidity,pressure,windSpeed,windDirection);

            weatherEntities.add(weatherEntity);
        }
        return weatherEntities;
    }


}

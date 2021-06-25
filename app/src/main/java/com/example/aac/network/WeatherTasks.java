package com.example.aac.network;

import android.content.Context;

import com.example.aac.database.WeatherEntity;
import com.example.aac.network.WeatherNetworkDataSource;
import com.example.data.WeatherPreferences;
import com.example.utils.NetworkUtils;
import com.example.utils.NotificationUtil;
import com.example.utils.OpenWeatherJsonUtils;
import com.example.weatherapp.AppExecutors;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;
import java.util.List;

public class WeatherTasks {
    public static final String ACTION_UPDATE_LOCATION = "update-weather-location";
    public static final String ACTION_CLEAR_NOTIFICATION = "clear-notification";


        public static void executeTask(Context context,String action){
            if (ACTION_UPDATE_LOCATION.equals(action)){
                AppExecutors  appExecutors = AppExecutors.getInstance();
                WeatherNetworkDataSource weatherNetworkDataSource = WeatherNetworkDataSource.getInstance(context,appExecutors);
                weatherNetworkDataSource.fetchWeather();
            }
            else if (ACTION_CLEAR_NOTIFICATION.equals(action)){
                NotificationUtil.clearAllNotification(context);
            }
        }
}

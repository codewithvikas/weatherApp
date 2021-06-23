package com.example.aac.network;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.aac.database.WeatherEntity;
import com.example.data.WeatherPreferences;
import com.example.utils.NetworkUtils;
import com.example.utils.OpenWeatherJsonUtils;
import com.example.weatherapp.AppExecutors;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;
import java.util.List;

public class WeatherNetworkDataSource {


    private static final String LOG_TAG = WeatherNetworkDataSource.class.getSimpleName();
    private static final Object LOCK = new Object();
    private static  WeatherNetworkDataSource sInstance;
    private final Context mContext;
    private final AppExecutors mExecutors;
    private final MutableLiveData<List<WeatherEntity>> mDownloadedWeatherForecasts;
    private WeatherNetworkDataSource(Context context, AppExecutors executors){
        mContext = context;
        mExecutors = executors;
        mDownloadedWeatherForecasts = new MutableLiveData<>();
    }

    public static WeatherNetworkDataSource getInstance(Context context,AppExecutors executors){
        Log.d(LOG_TAG,"Getting the network data source");
        if (sInstance==null){
            synchronized (LOCK){
                sInstance = new WeatherNetworkDataSource(context.getApplicationContext(),executors);
                Log.d(LOG_TAG,"Made new network data source");
            }
        }
        return sInstance;
    }

    public MutableLiveData<List<WeatherEntity>> getmDownloadedWeatherForecasts() {
        return mDownloadedWeatherForecasts;
    }

     void fetchWeather(){
        Log.d(LOG_TAG,"Fetch weather started");
        mExecutors.networkIO().execute(new Runnable() {
            @Override
            public void run() {
                String location = WeatherPreferences.getPreferredWeatherLocation(mContext);

                URL url = NetworkUtils.buildUrl(location);
                try {
                    String jsonResponse = NetworkUtils.getResponseFromHttpUrl(url);
                    List<WeatherEntity> weatherEntities = OpenWeatherJsonUtils.getFullWeatherEntitiesFromJson(mContext,jsonResponse);

                    if (weatherEntities!=null && weatherEntities.size()!=0){
                        mDownloadedWeatherForecasts.postValue(weatherEntities);
                    }
                    else {
                        throw new RuntimeException("Data not downloaded from Network");
                    }

                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void startWeatherService(){
        Intent weatherServiceIntent = new Intent(mContext, WeatherIntentService.class);
        weatherServiceIntent.setAction(WeatherTasks.ACTION_UPDATE_LOCATION);
        mContext.startService(weatherServiceIntent);
    }
}

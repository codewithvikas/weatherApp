package com.example.aac;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import com.example.aac.database.WeatherDao;
import com.example.aac.database.WeatherEntity;
import com.example.aac.network.WeatherNetworkDataSource;
import com.example.utils.NetworkUtils;
import com.example.utils.WeatherDateUtils;
import com.example.weatherapp.AppExecutors;

import java.util.Date;
import java.util.List;

public class WeatherRepository {

    private static final String LOG_TAG = WeatherRepository.class.getSimpleName();
    private static final Object LOCK = new Object();
    private static WeatherRepository sInstance;


    private final WeatherDao weatherDao;
    private final WeatherNetworkDataSource weatherNetworkDataSource;
    private final AppExecutors executors;

    private boolean mInitialized = false;
    private WeatherRepository(WeatherDao weatherDao, WeatherNetworkDataSource weatherNetworkDataSource, AppExecutors executors){

        this.weatherDao = weatherDao;
        this.weatherNetworkDataSource = weatherNetworkDataSource;
        this.executors = executors;
        LiveData<List<WeatherEntity>> weatherListLiveData = weatherNetworkDataSource.getmDownloadedWeatherForecasts();
        weatherListLiveData.observeForever(new Observer<List<WeatherEntity>>() {
            @Override
            public void onChanged(List<WeatherEntity> weatherEntities) {
                executors.diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        Date date = new Date(WeatherDateUtils.getNormalizedUtcDateForToday());
                       weatherDao.deleteOldWeather(date);
                        weatherDao.insertAllWeather(weatherEntities);
                    }
                });
            }
        });
    }

    private synchronized void initializeData(){
        if (mInitialized)return;
        mInitialized = true;
        weatherNetworkDataSource.fetchWeather();
    }
    public synchronized static WeatherRepository getInstance(WeatherDao weatherDao,WeatherNetworkDataSource weatherNetworkDataSource,AppExecutors executors){
        Log.d(LOG_TAG,"Getting the repository");
        if (sInstance==null){
            synchronized (LOCK){
                sInstance = new WeatherRepository(weatherDao,weatherNetworkDataSource,executors);
                Log.d(LOG_TAG,"Made new Repository");
            }
        }
        return sInstance;
    }
    public LiveData<List<WeatherEntity>> getWeatherList(){
        initializeData();
        return weatherDao.loadAllWeather();
    }
    public LiveData<WeatherEntity> getWeatherByDate(Date date){
        initializeData();
        return weatherDao.loadWeatherByDate(date);
    }

    public void deleteWeather(WeatherEntity weatherEntity) {
        weatherDao.deleteWeather(weatherEntity);
    }
}

package com.example.aac.ui;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.aac.database.WeatherDatabase;
import com.example.aac.database.WeatherEntity;

public class WeatherDetailViewModel extends ViewModel {

    private LiveData<WeatherEntity> weatherEntityLiveData;

    WeatherDetailViewModel(WeatherDatabase weatherDatabase, long date){
            weatherEntityLiveData = weatherDatabase.weatherDao().loadWeatherByDate(date);
    }

    public LiveData<WeatherEntity> getWeatherEntityLiveData() {
        return weatherEntityLiveData;
    }
}

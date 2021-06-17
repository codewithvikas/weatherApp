package com.example.aac;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import java.util.Date;

public class WeatherDetailViewModel extends ViewModel {

    private LiveData<WeatherEntity> weatherEntityLiveData;

    WeatherDetailViewModel(WeatherDatabase weatherDatabase, long date){
            weatherEntityLiveData = weatherDatabase.weatherDao().loadWeatherByDate(date);
    }

    public LiveData<WeatherEntity> getWeatherEntityLiveData() {
        return weatherEntityLiveData;
    }
}

package com.example.aac.ui;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.aac.WeatherRepository;
import com.example.aac.database.WeatherDatabase;
import com.example.aac.database.WeatherEntity;

import java.util.Date;

public class WeatherDetailViewModel extends ViewModel {

    private LiveData<WeatherEntity> weatherEntityLiveData;

    WeatherDetailViewModel(WeatherRepository weatherRepository, Date date){
            weatherEntityLiveData = weatherRepository.getWeatherByDate(date);
    }

    public LiveData<WeatherEntity> getWeatherEntityLiveData() {
        return weatherEntityLiveData;
    }
}

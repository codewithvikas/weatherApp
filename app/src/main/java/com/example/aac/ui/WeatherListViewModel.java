package com.example.aac.ui;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.aac.WeatherRepository;
import com.example.aac.database.WeatherDatabase;
import com.example.aac.database.WeatherEntity;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class WeatherListViewModel extends ViewModel {

    private LiveData<List<WeatherEntity>> weathersLiveData ;
    private WeatherRepository weatherRepository;

    public WeatherListViewModel(WeatherRepository weatherRepository) {
        this.weatherRepository = weatherRepository;
        weathersLiveData = this.weatherRepository.getWeatherList();
    }

    public LiveData<List<WeatherEntity>> getWeathersLiveData() {
        return weathersLiveData;
    }

    public void deleteWeather(WeatherEntity weatherEntity) {
        weatherRepository.deleteWeather(weatherEntity);
    }
}

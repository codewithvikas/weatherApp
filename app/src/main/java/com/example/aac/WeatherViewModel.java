package com.example.aac;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class WeatherViewModel extends AndroidViewModel {

    private LiveData<List<WeatherEntity>> weathersLiveData ;

    public WeatherViewModel(@NonNull @NotNull Application application) {
        super(application);
        weathersLiveData = WeatherDatabase.getInstance(this.getApplication()).weatherDao().loadAllWeather();
    }

    public LiveData<List<WeatherEntity>> getWeathersLiveData() {
        return weathersLiveData;
    }
}

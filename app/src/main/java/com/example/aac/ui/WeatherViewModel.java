package com.example.aac.ui;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.aac.database.WeatherDatabase;
import com.example.aac.database.WeatherEntity;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class WeatherViewModel extends AndroidViewModel {

    private LiveData<List<WeatherEntity>> weathersLiveData ;

    public WeatherViewModel(@NonNull @NotNull Application application) {
        super(application);
        WeatherDatabase database = WeatherDatabase.getInstance(this.getApplication());
        weathersLiveData = database.weatherDao().loadAllWeather();
    }

    public LiveData<List<WeatherEntity>> getWeathersLiveData() {
        return weathersLiveData;
    }
}

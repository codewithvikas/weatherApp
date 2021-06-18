package com.example.aac.ui;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.aac.WeatherRepository;

import org.jetbrains.annotations.NotNull;

public class WeatherListViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private WeatherRepository weatherRepository;

    public WeatherListViewModelFactory(WeatherRepository weatherRepository){

        this.weatherRepository = weatherRepository;
    }

    @NonNull
    @NotNull
    @Override
    public <T extends ViewModel> T create(@NonNull @NotNull Class<T> modelClass) {
        return (T) new WeatherListViewModel(weatherRepository);
    }
}

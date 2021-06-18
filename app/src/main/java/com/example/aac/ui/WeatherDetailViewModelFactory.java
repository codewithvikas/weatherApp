package com.example.aac.ui;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.aac.WeatherRepository;

import org.jetbrains.annotations.NotNull;

import java.util.Date;

public class WeatherDetailViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    WeatherRepository weatherRepository;
    Date mDate;
    /**
     * Creates a {@code AndroidViewModelFactory}
     *
     * @param weatherRepository an application to pass in {@link AndroidViewModel}
     */
    public WeatherDetailViewModelFactory(@NonNull WeatherRepository weatherRepository, Date date) {
        this.weatherRepository = weatherRepository;
        this.mDate = date;

    }

    @NonNull
    @NotNull
    @Override
    public <T extends ViewModel> T create(@NonNull @NotNull Class<T> modelClass) {
        return (T) new WeatherDetailViewModel(weatherRepository,mDate);
    }
}

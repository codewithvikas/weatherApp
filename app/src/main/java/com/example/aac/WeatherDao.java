package com.example.aac;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.data.WeatherContract;

import java.util.Date;
import java.util.List;

@Dao
public interface WeatherDao {

    @Query("Select * from weather ORDER BY date")
    List<WeatherEntity> loadAllWeather();

    @Query("Select * from weather where date = :givenDate")
    WeatherEntity loadWeatherByDate(long givenDate);

    @Insert
    void insertWeather(WeatherEntity weatherEntity);
    @Insert
    void insertAllWeather(List<WeatherEntity> weatherEntities);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateWeather(WeatherEntity weatherEntity);

    @Delete
    void deleteWeather(WeatherEntity weatherEntity);
}

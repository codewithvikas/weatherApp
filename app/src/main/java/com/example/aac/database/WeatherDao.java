package com.example.aac.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.Date;
import java.util.List;

@Dao
public interface WeatherDao {

    @Query("Select * from weather ORDER BY date")
    LiveData<List<WeatherEntity>> loadAllWeather();

    @Query("Select * from weather where date = :givenDate")
    LiveData<WeatherEntity> loadWeatherByDate(Date givenDate);

    @Insert
    void insertWeather(WeatherEntity weatherEntity);
    @Insert
    void insertAllWeather(List<WeatherEntity> weatherEntities);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateWeather(WeatherEntity weatherEntity);

    @Delete
    void deleteWeather(WeatherEntity weatherEntity);

    @Query("DELETE from weather WHERE date < :date")
    void deleteOldWeather(Date date);
}

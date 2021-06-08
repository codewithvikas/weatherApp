package com.example.weatherapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ShareCompat;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.data.WeatherContract;
import com.example.utils.WeatherDateUtils;
import com.example.utils.WeatherUtils;

import org.jetbrains.annotations.NotNull;

public class DetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String FORECAST_SHARE_HASHTAG = "#SunshineApp";

    TextView dateTv,descTv,maxTv,minTv,humidityTv,windTv,pressureTv;
    String mWeatherSummary;

    public static final String[] projection = {
            WeatherContract.WeatherEntry.COLUMN_WEATHER_ID,
            WeatherContract.WeatherEntry.COLUMN_DATE,
            WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
            WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,
            WeatherContract.WeatherEntry.COLUMN_HUMIDITY,
            WeatherContract.WeatherEntry.COLUMN_PRESSURE,
            WeatherContract.WeatherEntry.COLUMN_DEGREE,
            WeatherContract.WeatherEntry.COLUMN_WIND_SPEED,
    };

    public static final int INDEX_COL_WEATHER_ID = 0;
    public static final int INDEX_COL_DATE = 1;
    public static final int INDEX_COL_TEMP_HIGH = 2;
    public static final int INDEX_COL_TEMP_MIN = 3;
    public static final int INDEX_COL_HUMIDITY = 4;
    public static final int INDEX_COL_PRESSURE = 5;
    public static final int INDEX_COL_DEGREE = 6;
    public static final int INDEX_COL_WIND_SPEED = 7;

    public static final int ID_DETAIL_LOADER = 333;
    Uri mUri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

       dateTv = findViewById(R.id.tv_date);
       descTv = findViewById(R.id.tv_description);
       maxTv = findViewById(R.id.tv_temp_high);
       minTv = findViewById(R.id.tv_temp_low);
       humidityTv = findViewById(R.id.tv_humidity);
       windTv = findViewById(R.id.tv_wind);
       pressureTv = findViewById(R.id.tv_pressure);

        mUri = getIntent().getData();
        if (mUri==null){
            throw new NullPointerException("Uri for DetailActivity can not be null");
        }
    }

    Intent createShareForecastIntent(){
        Intent shareIntent = new ShareCompat.IntentBuilder(this)
                .setType("text/plain")
                .setText(mWeatherSummary+FORECAST_SHARE_HASHTAG)
                .getIntent();
        return shareIntent;

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        new MenuInflater(this).inflate(R.menu.detail,menu);
        MenuItem menuItem = menu.findItem(R.id.action_share);
        menuItem.setIntent(createShareForecastIntent());
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_settings:
                openSettingActivity();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private void openSettingActivity() {
        Intent intent = new Intent(DetailActivity.this,SettingsActivity.class);
        startActivity(intent);
    }

    @NonNull
    @NotNull
    @Override
    public Loader<Cursor> onCreateLoader(int loader_id, @Nullable @org.jetbrains.annotations.Nullable Bundle args) {

        switch (loader_id){
            case ID_DETAIL_LOADER:
                return new CursorLoader(
                        this,
                        mUri,
                        projection,
                        null,
                        null,
                        null
                );
            default:
                throw new RuntimeException("Loader Not implemented: "+loader_id);
        }
    }

    @Override
    public void onLoadFinished(@NonNull @NotNull Loader<Cursor> loader, Cursor data) {

        boolean cursorHasValidData = false;
        if (data!=null && data.moveToFirst()){
            cursorHasValidData = true;
        }
        if (!cursorHasValidData){
            return;
        }

        long localDateMidNightGmt = data.getLong(INDEX_COL_DATE);

        String dateString = WeatherDateUtils.getFriendlyDateString(DetailActivity.this,localDateMidNightGmt,true);

        dateTv.setText(dateString);
        int weatherId = data.getInt(INDEX_COL_WEATHER_ID);

        String description = WeatherUtils.getStringForWeatherCondition(DetailActivity.this,weatherId);
        descTv.setText(description);

        double highInCelsius = data.getDouble(INDEX_COL_TEMP_HIGH);
        String highString = WeatherUtils.formatTemperature(this,highInCelsius);
        maxTv.setText(highString);

        double lowInCelsius = data.getDouble(INDEX_COL_TEMP_MIN);
        String lowString = WeatherUtils.formatTemperature(this,lowInCelsius);
        minTv.setText(lowString);


        double humidity = data.getDouble(INDEX_COL_HUMIDITY);

        String humidityString = getString(R.string.format_humidity,humidity);
        humidityTv.setText(humidityString);

        double pressure = data.getDouble(INDEX_COL_PRESSURE);

        String pressureString = getString(R.string.format_pressure,pressure);

        pressureTv.setText(pressureString);

        float wind = data.getFloat(INDEX_COL_WIND_SPEED);
        float windDirection = data.getFloat(INDEX_COL_DEGREE);
        String windString  = WeatherUtils.getFormattedWind(this,wind,windDirection);

        windTv.setText(windString);

        mWeatherSummary = String.format("%s - %s -%s/%s",data,description,highString,lowString);
    }

    @Override
    public void onLoaderReset(@NonNull @NotNull Loader<Cursor> loader) {

    }
}
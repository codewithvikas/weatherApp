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

import com.example.aac.WeatherDatabase;
import com.example.aac.WeatherEntity;
import com.example.data.WeatherContract;
import com.example.utils.Constants;
import com.example.utils.OpenWeatherJsonUtils;
import com.example.utils.WeatherDateUtils;
import com.example.utils.WeatherUtils;

import org.jetbrains.annotations.NotNull;

public class DetailActivity extends AppCompatActivity {

    private static final String FORECAST_SHARE_HASHTAG = "#SunshineApp";

    TextView dateTv,descTv,maxTv,minTv,humidityTv,windTv,pressureTv;
    String mWeatherSummary;

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

        final long date = getIntent().getLongExtra(Constants.DATE_EXTRA,0);
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                WeatherDatabase database = WeatherDatabase.getInstance(DetailActivity.this);
                WeatherEntity weatherEntity = database.weatherDao().loadWeatherByDate(date);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dateTv.setText(weatherEntity.getDate().toString());
                        String desc = WeatherUtils.getStringForWeatherCondition(DetailActivity.this,weatherEntity.getWeatherId());
                        descTv.setText(desc);
                        String maxTempString = WeatherUtils.formatTemperature(DetailActivity.this,weatherEntity.getMax());
                        maxTv.setText(maxTempString);
                        String minTempString = WeatherUtils.formatTemperature(DetailActivity.this,weatherEntity.getMin());
                        minTv.setText(minTempString);
                        String humidityString = getString(R.string.format_humidity,weatherEntity.getHumidity());
                        humidityTv.setText(humidityString);
                        String windString = WeatherUtils.getFormattedWind(DetailActivity.this,(float)weatherEntity.getWind(),(float)weatherEntity.getDegree());
                        windTv.setText(windString);
                        String pressureString = getString(R.string.format_pressure,weatherEntity.getPressure());
                        pressureTv.setText(pressureString);
                    }
                });

            }
        });
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

}
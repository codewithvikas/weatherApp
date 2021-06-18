package com.example.weatherapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ShareCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.aac.database.WeatherDatabase;
import com.example.aac.ui.WeatherDetailViewModel;
import com.example.aac.ui.WeatherDetailViewModelFactory;
import com.example.aac.database.WeatherEntity;
import com.example.utils.Constants;
import com.example.utils.WeatherDateUtils;
import com.example.utils.WeatherUtils;

public class DetailActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String FORECAST_SHARE_HASHTAG = "#SunshineApp";

    TextView dateTv,descTv,maxTv,minTv,humidityTv,windTv,pressureTv;
    String mWeatherSummary;
    private  long mDateLong;

    private static boolean PREFERENCE_HAVE_BEEN_UPDATE = false;

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

        mDateLong = getIntent().getLongExtra(Constants.DATE_EXTRA,0);
        updateView();
        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (PREFERENCE_HAVE_BEEN_UPDATE){
            //This part need to optimize
            updateView();
        }

    }

    void updateView(){
                WeatherDatabase database = WeatherDatabase.getInstance(DetailActivity.this);
        WeatherDetailViewModelFactory weatherDetailViewModelFactory = new WeatherDetailViewModelFactory(database,mDateLong);
        WeatherDetailViewModel weatherDetailViewModel = new ViewModelProvider(DetailActivity.this,weatherDetailViewModelFactory).get(WeatherDetailViewModel.class);

                LiveData<WeatherEntity> weatherEntityLiveData = weatherDetailViewModel.getWeatherEntityLiveData();

                weatherEntityLiveData.observe(DetailActivity.this, new Observer<WeatherEntity>() {
                    @Override
                    public void onChanged(WeatherEntity weatherEntity) {
                        String dateString = WeatherDateUtils.getFriendlyDateString(DetailActivity.this,weatherEntity.getDate().getTime(),true);
                        dateTv.setText(dateString);
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

    Intent createShareForecastIntent(){

        mWeatherSummary = dateTv.getText()+" - "+descTv.getText()+" - "+maxTv.getText()+" - "+minTv.getText()+
                " - "+humidityTv.getText()+" - "+pressureTv.getText()+" - "+windTv.getText();

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

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        PREFERENCE_HAVE_BEEN_UPDATE = true;
    }
}
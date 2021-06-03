package com.example.weatherapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ShareCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

public class DetailActivity extends AppCompatActivity {

    private static final String FORECAST_SHARE_HASHTAG = "#SunshineApp";

    TextView detailTextView;
    String mWeatherData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        detailTextView = findViewById(R.id.tv_detail_weather);

        Intent intent = getIntent();
        if (intent!=null){
            if (intent.hasExtra(Intent.EXTRA_TEXT)){
                String weatherOfDay = intent.getStringExtra(Intent.EXTRA_TEXT);
                mWeatherData = weatherOfDay;
                detailTextView.setText(mWeatherData);
            }
        }
    }

    Intent createShareForecastIntent(){
        Intent shareIntent = new ShareCompat.IntentBuilder(this)
                .setType("text/plain")
                .setText(mWeatherData+FORECAST_SHARE_HASHTAG)
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
}
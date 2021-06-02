package com.example.weatherapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class DetailActivity extends AppCompatActivity {

    private static final String FORECAST_SHARE_HASHTAG = "#SunshineApp";

    TextView detailTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        detailTextView = findViewById(R.id.tv_detail_weather);

        Intent intent = getIntent();
        if (intent!=null){
            if (intent.hasExtra(Intent.EXTRA_TEXT)){
                String weatherOfDay = intent.getStringExtra(Intent.EXTRA_TEXT);
                detailTextView.setText(weatherOfDay);
            }
        }
    }
}
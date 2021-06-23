package com.example.aac.network;

import android.app.IntentService;
import android.content.Intent;

import androidx.annotation.Nullable;


public class WeatherIntentService extends IntentService {
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     */
    public WeatherIntentService() {
        super("WeatherIntentService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
            String action = intent.getAction();
            WeatherTasks.executeTask(this,action);
    }
}

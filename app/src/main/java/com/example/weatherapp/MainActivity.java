package com.example.weatherapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.aac.WeatherDatabase;
import com.example.aac.WeatherEntity;
import com.example.aac.WeatherViewModel;
import com.example.data.WeatherContract;
import com.example.data.WeatherPreferences;
import com.example.utils.Constants;
import com.example.utils.NetworkUtils;
import com.example.utils.OpenWeatherJsonUtils;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;

import java.io.IOException;
import java.net.URL;
import java.util.List;

public class MainActivity extends AppCompatActivity implements ForeCastAdapter.ItemClickHandler, SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private ForeCastAdapter mForeCastAdapter;
    ProgressBar loadingIndicator;
    TextView errorTextView;
    RecyclerView recyclerView;

    private static  boolean PREFERENCE_HAVE_BEEN_UPDATED = false;
    private static final int  LOADER_ID = 11;

    public static final String[] projection = {
            WeatherContract.WeatherEntry.COLUMN_DATE,
            WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
            WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,
            WeatherContract.WeatherEntry.COLUMN_WEATHER_ID
    };

    public static final int INDEX_COLUMN_DATE = 0;
    public static final int INDEX_COLUMN_MAX = 1;
    public static final int INDEX_COLUMN_MIN = 2;
    public static final int INDEX_COLUMN_ID = 3;

    WeatherDatabase mDb;

    ItemTouchHelper.SimpleCallback recyclerViewItemTouchHelper = new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.RIGHT|ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(@NonNull @NotNull RecyclerView recyclerView, @NonNull @NotNull RecyclerView.ViewHolder viewHolder, @NonNull @NotNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull @NotNull RecyclerView.ViewHolder viewHolder, int direction) {
                 AppExecutors.getInstance().diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        WeatherEntity weatherEntity = mForeCastAdapter.getWeatherByPosition(viewHolder.getAdapterPosition());
                        mDb.weatherDao().deleteWeather(weatherEntity);
                    }
                });
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forecast);
        getSupportActionBar().setElevation(0);
        //FakeDataUtils.insertFakeData(this);

         mDb = WeatherDatabase.getInstance(this);
       // FakeDataUtils.insertFakeDataInRoom(mDb);

        recyclerView = findViewById(R.id.forecast_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);


        mForeCastAdapter = new ForeCastAdapter(this,this);

        recyclerView.setAdapter(mForeCastAdapter);

        new ItemTouchHelper(recyclerViewItemTouchHelper).attachToRecyclerView(recyclerView);

        loadingIndicator = findViewById(R.id.pb_loading_indecator);

        errorTextView = findViewById(R.id.tv_error_display_msg);

        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);

        downloadData(mDb);
        setupWeatherViewModel();

    }

    void setupWeatherViewModel(){
        WeatherViewModel weatherViewModel = new ViewModelProvider(MainActivity.this).get(WeatherViewModel.class);
        weatherViewModel.getWeathersLiveData().observe(MainActivity.this, new Observer<List<WeatherEntity>>() {
                            @Override
                            public void onChanged(List<WeatherEntity> weatherEntities) {
                                loadingIndicator.setVisibility(View.INVISIBLE);
                                mForeCastAdapter.swapCursor(weatherEntities);
                                if (weatherEntities!=null && weatherEntities.size() > 0){
                                    showDataView();
                                }
                                else {
                                    showErrorView();
                                }
                            }
                        });
                    }

    private void downloadData(WeatherDatabase db){
        loadingIndicator.setVisibility(View.VISIBLE);
        AppExecutors.getInstance().networkIO().execute(new Runnable() {
            @Override
            public void run() {
                String location = WeatherPreferences.getPreferredWeatherLocation(MainActivity.this);

                URL url = NetworkUtils.buildUrl(location);
                try {
                    String jsonResponse = NetworkUtils.getResponseFromHttpUrl(url);
                    List<WeatherEntity> weatherEntities = OpenWeatherJsonUtils.getFullWeatherEntitiesFromJson(MainActivity.this,jsonResponse);

                    Log.d(MainActivity.TAG,"Network ops done !!");
                    if (weatherEntities!=null){
                        AppExecutors.getInstance().diskIO().execute(new Runnable() {
                            @Override
                            public void run() {
                                db.weatherDao().insertAllWeather(weatherEntities);
                                Log.d(MainActivity.TAG,"Data Insertion called !!");
                            }
                        });
                    }
                    else {
                        throw new RuntimeException("Data not downloaded from Network");
                    }

                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (PREFERENCE_HAVE_BEEN_UPDATED){
           downloadData(mDb);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.forecast,menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_refresh:
                mForeCastAdapter.swapCursor(null);
                downloadData(mDb);
                return true;
            case R.id.action_open_map:
                openMapInLocation();
                return true;
            case R.id.action_settings:
                openSettingActivity();

            default:
                return super.onOptionsItemSelected(item);

                        }
    }

    private void openSettingActivity() {
        Intent intent = new Intent(MainActivity.this,SettingsActivity.class);
        startActivity(intent);
    }

    void openMapInLocation(){
        String query = WeatherPreferences.getPreferredWeatherLocation(this);
        Uri data = Uri.parse("geo:0,0?q="+ query);

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(data);

        if (intent.resolveActivity(getPackageManager())!=null){
            startActivity(intent);
        }
        else {
            Toast.makeText(this,"Could not open map. No any App is installed",Toast.LENGTH_SHORT).show();
        }
    }
    void showDataView(){
        recyclerView.setVisibility(View.VISIBLE);
        errorTextView.setVisibility(View.INVISIBLE);
    }
    void showErrorView(){
        errorTextView.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onClick(long date) {
        Intent intent = new Intent(this,DetailActivity.class);
        intent.putExtra(Constants.DATE_EXTRA,date);
        startActivity(intent);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        PREFERENCE_HAVE_BEEN_UPDATED = true;
    }
}
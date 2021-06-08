package com.example.weatherapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.data.WeatherContract;
import com.example.data.WeatherPreferences;
import com.example.utils.FakeDataUtils;

import org.jetbrains.annotations.NotNull;

public class MainActivity extends AppCompatActivity implements ForeCastAdapter.ItemClickHandler, LoaderManager.LoaderCallbacks<Cursor>, SharedPreferences.OnSharedPreferenceChangeListener {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forecast);
        getSupportActionBar().setElevation(0);
        FakeDataUtils.insertFakeData(this);

        recyclerView = findViewById(R.id.forecast_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);


        mForeCastAdapter = new ForeCastAdapter(this,this);

        recyclerView.setAdapter(mForeCastAdapter);

        loadingIndicator = findViewById(R.id.pb_loading_indecator);

        errorTextView = findViewById(R.id.tv_error_display_msg);

        getSupportLoaderManager().initLoader(LOADER_ID,null,this);

        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (PREFERENCE_HAVE_BEEN_UPDATED){
            getSupportLoaderManager().restartLoader(LOADER_ID,null,this);
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
                getSupportLoaderManager().restartLoader(LOADER_ID,null,this);
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
        intent.setData(WeatherContract.WeatherEntry.buildWeatherUriWithDate(date));
        startActivity(intent);
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int loaderId, @Nullable @org.jetbrains.annotations.Nullable Bundle args) {

        Log.d(TAG,"OnCreateLoader Called");
            switch (loaderId){
                case LOADER_ID:
                    Uri weatherUri = WeatherContract.WeatherEntry.CONTENT_URI;
                    String sortingOrder = WeatherContract.WeatherEntry.COLUMN_DATE+" ASC";

                    String selection = WeatherContract.WeatherEntry.getSqlSelectTodayOnwards();

                    return new CursorLoader(
                            this,
                            weatherUri,
                            projection,
                            selection,
                            null,
                            sortingOrder
                    );
                default:
                    throw new RuntimeException("Loader Not implemented: "+loaderId);
            }

    }

    @Override
    public void onLoadFinished(@NotNull Loader<Cursor> loader, Cursor data) {
        Log.d(TAG,"OnLoadFinished called");

        loadingIndicator.setVisibility(View.INVISIBLE);
        mForeCastAdapter.swapCursor(data);
        if (data.getCount()==0){
            showErrorView();
        }
        else {
            showDataView();
        }

    }

    @Override
    public void onLoaderReset(@NonNull @NotNull Loader<Cursor> loader) {
            Log.d(TAG,"OnLoaderReset called");
           mForeCastAdapter.swapCursor(null);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        PREFERENCE_HAVE_BEEN_UPDATED = true;
    }
}
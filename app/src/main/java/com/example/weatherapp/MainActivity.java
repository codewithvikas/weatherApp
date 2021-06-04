package com.example.weatherapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.AsyncTaskLoader;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.data.WeatherPreferences;
import com.example.utils.NetworkUtils;
import com.example.utils.OpenWeatherJsonUtils;
import com.example.utils.WeatherUtils;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;

import java.io.IOException;
import java.net.URL;
import java.util.List;

public class MainActivity extends AppCompatActivity implements ForeCastAdapter.ItemClickHandler, LoaderManager.LoaderCallbacks<String[]> {

    private static final String TAG = MainActivity.class.getSimpleName();
    private ForeCastAdapter mForeCastAdapter;
    ProgressBar loadingIndicator;
    TextView errorTextView;
    RecyclerView recyclerView;

    private static int LOADER_ID = 11;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forecast);

         recyclerView = findViewById(R.id.forecast_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        mForeCastAdapter = new ForeCastAdapter(this);

        recyclerView.setAdapter(mForeCastAdapter);

        loadingIndicator = findViewById(R.id.pb_loading_indecator);

        errorTextView = findViewById(R.id.tv_error_display_msg);

        getSupportLoaderManager().initLoader(LOADER_ID,null,this);
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
                mForeCastAdapter.setWeatherData(null);
                getSupportLoaderManager().restartLoader(LOADER_ID,null,this);
                return true;
            case R.id.action_open_map:
                openMapInLocation();
                return true;

            default:
                return super.onOptionsItemSelected(item);

                        }
    }

    void openMapInLocation(){
        String query = "Patna, Bihar";
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
    public void onClick(String weatherDay) {

        Intent intent = new Intent(this,DetailActivity.class);
        intent.putExtra(Intent.EXTRA_TEXT,weatherDay);
        startActivity(intent);
    }

    @NonNull
    @NotNull
    @Override
    public Loader<String[]> onCreateLoader(int id, @Nullable @org.jetbrains.annotations.Nullable Bundle args) {

        Log.d(TAG,"OnCreateLoader Called");
        return new AsyncTaskLoader<String[]>(this) {

            String[] mWeatherData;
            @Override
            protected void onStartLoading() {
                Log.d(TAG,"OnStartLoading Called");
                if (mWeatherData!=null){
                    deliverResult(mWeatherData);
                }
                else {
                    loadingIndicator.setVisibility(View.VISIBLE);
                    forceLoad();
                }

            }

            @Nullable
            @org.jetbrains.annotations.Nullable
            @Override
            public String[] loadInBackground() {
                Log.d(TAG,"LoadInBackGround called");

                String locationQuery = WeatherPreferences.getPreferredWeatherLocation(MainActivity.this);
                URL  networkUrl = NetworkUtils.buildUrl(locationQuery);
                    try {
                        String jsonResponse = NetworkUtils.getResponseFromHttpUrl(networkUrl);
                        if (jsonResponse==null){
                            return null;
                        }
                        try {
                            return OpenWeatherJsonUtils.getSimpleWeatherStringsFromJson(MainActivity.this,jsonResponse);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return null;
            }

            @Override
            public void deliverResult(@Nullable @org.jetbrains.annotations.Nullable String[] data) {
                mWeatherData = data;
                super.deliverResult(data);
            }
        };
    }

    @Override
    public void onLoadFinished(@NonNull @NotNull Loader<String[]> loader, String[] data) {
        Log.d(TAG,"OnLoadFinished called");

        loadingIndicator.setVisibility(View.INVISIBLE);
        mForeCastAdapter.setWeatherData(data);
        if (data==null){
            showErrorView();
        }
        else {
            showDataView();
        }

    }

    @Override
    public void onLoaderReset(@NonNull @NotNull Loader<String[]> loader) {
            Log.d(TAG,"OnLoaderReset called");
    }

}
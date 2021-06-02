package com.example.weatherapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
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

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;
import java.util.List;

public class MainActivity extends AppCompatActivity implements ForeCastAdapter.ItemClickHandler {

    private ForeCastAdapter mForeCastAdapter;
    ProgressBar loadingIndicator;
    TextView errorTextView;
    RecyclerView recyclerView;

    Toast mToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forecast);

        /*
         * Using findViewById, we get a reference to our TextView from xml. This allows us to
         * do things like set the text of the TextView.
         */

         recyclerView = findViewById(R.id.forecast_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        mForeCastAdapter = new ForeCastAdapter(this);

        recyclerView.setAdapter(mForeCastAdapter);

        loadingIndicator = findViewById(R.id.pb_loading_indecator);

        errorTextView = findViewById(R.id.tv_error_display_msg);

        // Completed (4) Delete the dummy weather data. You will be getting REAL data from the Internet in this lesson.
        /*
         * This String array contains dummy weather data. Later in the course, we're going to get
         * real weather data. For now, we want to get something on the screen as quickly as
         * possible, so we'll display this dummy data.
         */

        // Completed (3) Delete the for loop that populates the TextView with dummy data
        /*
         * Iterate through the array and append the Strings to the TextView. The reason why we add
         * the "\n\n\n" after the String is to give visual separation between each String in the
         * TextView. Later, we'll learn about a better way to display lists of data.
         */

        // Completed (9) Call loadWeatherData to perform the network request to get the weather
        loadWeatherData(WeatherPreferences.getPreferredWeatherLocation(this));
    }

    // Completed (8) Create a method that will get the user's preferred location and execute your new AsyncTask and call it loadWeatherData

    void loadWeatherData(String preferredLocation){
        new WeatherAsyncTask(this).execute(preferredLocation);
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
                loadWeatherData(WeatherPreferences.getPreferredWeatherLocation(this));
                return true;
            default:
                return super.onOptionsItemSelected(item);

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

        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
    }

    class WeatherAsyncTask extends AsyncTask<String,Void, String[]>{

        Context mContext;
        WeatherAsyncTask(Context context){
            mContext = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loadingIndicator.setVisibility(View.VISIBLE);

        }

        @Override
        protected String[] doInBackground(String... strings) {

            if (strings[0]!=null){
                URL networkUrl = NetworkUtils.buildUrl(strings[0]);
                try {
                    String jsonResponse = NetworkUtils.getResponseFromHttpUrl(networkUrl);
                    if (jsonResponse==null){
                        return null;
                    }
                    try {
                        return OpenWeatherJsonUtils.getSimpleWeatherStringsFromJson(mContext,jsonResponse);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String[] strings) {
            loadingIndicator.setVisibility(View.INVISIBLE);
            if (strings!=null){
                showDataView();
                mForeCastAdapter.setWeatherData(strings);
            }
            else {
                showErrorView();
            }


        }
    }
    // Completed (5) Create a class that extends AsyncTask to perform network requests
    // Completed (6) Override the doInBackground method to perform your network requests
    // Completed (7) Override the onPostExecute method to display the results of the network request
}
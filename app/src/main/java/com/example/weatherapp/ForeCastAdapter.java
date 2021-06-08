package com.example.weatherapp;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.data.WeatherContract;
import com.example.utils.WeatherDateUtils;
import com.example.utils.WeatherUtils;

public class ForeCastAdapter extends RecyclerView.Adapter<ForeCastAdapter.ForecastItemHolder> {


    ItemClickHandler itemClickHandler;
    Context mContext;

    ForeCastAdapter(ItemClickHandler itemClickHandler,Context mContext){
        this.itemClickHandler = itemClickHandler;
        this.mContext = mContext;
    }
    interface ItemClickHandler {
        void onClick(long date);
    }
    private Cursor mWeatherData;
    @NonNull
    @org.jetbrains.annotations.NotNull
    @Override
    public ForecastItemHolder onCreateViewHolder(@NonNull @org.jetbrains.annotations.NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.weather_list_item,parent,false);
        view.setFocusable(true);
        return new ForecastItemHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @org.jetbrains.annotations.NotNull ForeCastAdapter.ForecastItemHolder holder, int position) {

        mWeatherData.moveToPosition(position);
        long date = mWeatherData.getLong(MainActivity.INDEX_COLUMN_DATE);
        String dateString = WeatherDateUtils.getFriendlyDateString(mContext,date,false);

        int weatherId = mWeatherData.getInt(MainActivity.INDEX_COLUMN_ID);
        String description = WeatherUtils.getStringForWeatherCondition(mContext,weatherId);

        double highInCelsius = mWeatherData.getDouble(MainActivity.INDEX_COLUMN_MAX);
        double lowInCelsius = mWeatherData.getDouble(MainActivity.INDEX_COLUMN_MIN);

        String formatHighLowTemp = WeatherUtils.formatHighLows(mContext,highInCelsius,lowInCelsius);

        String weatherSummary  = dateString +" - "+ description +" - "+formatHighLowTemp;

        holder.dataTextView.setText(weatherSummary);
    }

    @Override
    public int getItemCount() {
        if (mWeatherData!=null){
            return mWeatherData.getCount();
        }
        return 0;
    }

    public void swapCursor(Cursor mWeatherData) {
        this.mWeatherData = mWeatherData;
        notifyDataSetChanged();
    }

    class ForecastItemHolder extends RecyclerView.ViewHolder{

        TextView dataTextView;
        public ForecastItemHolder(@NonNull @org.jetbrains.annotations.NotNull View itemView) {
            super(itemView);
            dataTextView = itemView.findViewById(R.id.tv_weather_data);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    long date = mWeatherData.getLong(MainActivity.INDEX_COLUMN_DATE);

                    itemClickHandler.onClick(date);
                }
            });
        }
    }
}

package com.example.weatherapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aac.database.WeatherEntity;
import com.example.utils.WeatherDateUtils;
import com.example.utils.WeatherUtils;

import java.util.Date;
import java.util.List;

public class ForeCastAdapter extends RecyclerView.Adapter<ForeCastAdapter.ForecastItemHolder> {


    private static final int VIEW_TYPE_TODAY = 1;
    private static final int VIEW_TYPE_FUTURE = 2;

    ItemClickHandler itemClickHandler;
    Context mContext;

    ForeCastAdapter(ItemClickHandler itemClickHandler,Context mContext){
        this.itemClickHandler = itemClickHandler;
        this.mContext = mContext;
    }
    interface ItemClickHandler {
        void onClick(long date);
    }
    private List<WeatherEntity> mWeatherData;
    @NonNull
    @org.jetbrains.annotations.NotNull
    @Override
    public ForecastItemHolder onCreateViewHolder(@NonNull @org.jetbrains.annotations.NotNull ViewGroup parent, int viewType) {
        if (viewType ==VIEW_TYPE_TODAY){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.today_weather_list_item,parent,false);
            view.setFocusable(true);
            return new ForecastItemHolder(view);
        }
        else if (viewType == VIEW_TYPE_FUTURE){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.weather_list_item,parent,false);
            view.setFocusable(true);
            return new ForecastItemHolder(view);
        }
        else {
            throw new IllegalArgumentException("Wrong View Type");
        }

    }

    @Override
    public void onBindViewHolder(@NonNull @org.jetbrains.annotations.NotNull ForeCastAdapter.ForecastItemHolder holder, int position) {

        WeatherEntity weatherEntity =  mWeatherData.get(position);
        Date date = weatherEntity.getDate();
        String dateString = WeatherDateUtils.getFriendlyDateString(mContext,date.getTime(),false);

        holder.dateTextView.setText(dateString);

        int weatherId = weatherEntity.getWeatherId();
        String description = WeatherUtils.getStringForWeatherCondition(mContext,weatherId);
        holder.descTextView.setText(description);

        double highInCelsius =  weatherEntity.getMax();
        String highTemp = WeatherUtils.formatTemperature(mContext,highInCelsius);
        holder.tempHighTextView.setText(highTemp);

        double lowInCelsius = weatherEntity.getMin();
        String lowTemp = WeatherUtils.formatTemperature(mContext,lowInCelsius);
        holder.tempLowTestView.setText(lowTemp);

        int iconId = WeatherUtils.getArtResourceForWeatherCondition(weatherId);
        holder.weatherIconImageView.setImageResource(iconId);
    }

    @Override
    public int getItemCount() {
        if (mWeatherData!=null){
            return mWeatherData.size();
        }
        return 0;
    }

    @Override
    public int getItemViewType(int position) {
        if (mContext.getResources().getBoolean(R.bool.use_today_layout) && position == 0){
            return  VIEW_TYPE_TODAY;
        }
        else {
            return VIEW_TYPE_FUTURE;
        }
    }

    public WeatherEntity getWeatherByPosition(int position){
        if (mWeatherData!=null){
            return mWeatherData.get(position);
        }
        return null;
    }

    public void swapCursor(List<WeatherEntity> mWeatherData) {
        this.mWeatherData = mWeatherData;
        notifyDataSetChanged();
    }

    class ForecastItemHolder extends RecyclerView.ViewHolder{

        TextView dateTextView;
        ImageView weatherIconImageView;
        TextView descTextView;
        TextView tempHighTextView;
        TextView tempLowTestView;

        public ForecastItemHolder(@NonNull @org.jetbrains.annotations.NotNull View itemView) {
            super(itemView);

            dateTextView = itemView.findViewById(R.id.tv_weather_date);
            weatherIconImageView = itemView.findViewById(R.id.weather_icon_iv);
            descTextView = itemView.findViewById(R.id.desc_tv);
            tempHighTextView = itemView.findViewById(R.id.tv_temp_high_list);
            tempLowTestView = itemView.findViewById(R.id.tv_temp_low_list);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    long date = mWeatherData.get(getAdapterPosition()).getDate().getTime();
                    itemClickHandler.onClick(date);
                }
            });
        }
    }
}

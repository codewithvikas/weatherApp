package com.example.weatherapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aac.WeatherEntity;
import com.example.utils.WeatherDateUtils;
import com.example.utils.WeatherUtils;

import java.util.Date;
import java.util.List;

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
    private List<WeatherEntity> mWeatherData;
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

        WeatherEntity weatherEntity =  mWeatherData.get(position);
        Date date = weatherEntity.getDate();
        String dateString = WeatherDateUtils.getFriendlyDateString(mContext,date.getTime(),false);

        int weatherId = weatherEntity.getWeatherId();
        String description = WeatherUtils.getStringForWeatherCondition(mContext,weatherId);

        double highInCelsius =  weatherEntity.getMax();
        double lowInCelsius = weatherEntity.getMin();
        String formatHighLowTemp = WeatherUtils.formatHighLows(mContext,highInCelsius,lowInCelsius);
        double pressure = weatherEntity.getPressure();

        String weatherSummary  = dateString +" - "+ description +" - "+formatHighLowTemp+" - "+pressure;

        holder.dataTextView.setText(weatherSummary);
    }

    @Override
    public int getItemCount() {
        if (mWeatherData!=null){
            return mWeatherData.size();
        }
        return 0;
    }

    public void swapCursor(List<WeatherEntity> mWeatherData) {
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
                    long date = mWeatherData.get(getAdapterPosition()).getDate().getTime();

                    itemClickHandler.onClick(date);
                }
            });
        }
    }
}

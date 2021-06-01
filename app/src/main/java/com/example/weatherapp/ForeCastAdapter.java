package com.example.weatherapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ForeCastAdapter extends RecyclerView.Adapter<ForeCastAdapter.ForecastItemHolder> {


    ItemClickHandler itemClickHandler;
    ForeCastAdapter(ItemClickHandler itemClickHandler){
        this.itemClickHandler = itemClickHandler;
    }
    interface ItemClickHandler {
        void onClick(String weatherDay);
    }
    private String[] mWeatherData;
    @NonNull
    @org.jetbrains.annotations.NotNull
    @Override
    public ForecastItemHolder onCreateViewHolder(@NonNull @org.jetbrains.annotations.NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.weather_list_item,parent,false);
        return new ForecastItemHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @org.jetbrains.annotations.NotNull ForeCastAdapter.ForecastItemHolder holder, int position) {

        String weatherForDay = mWeatherData[position];
        holder.dataTextView.setText(weatherForDay);
    }

    @Override
    public int getItemCount() {
        if (mWeatherData!=null){
            return mWeatherData.length;
        }
        return 0;
    }

    public void setWeatherData(String[] mWeatherData) {
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
                    itemClickHandler.onClick(mWeatherData[getAdapterPosition()]);
                }
            });
        }
    }
}

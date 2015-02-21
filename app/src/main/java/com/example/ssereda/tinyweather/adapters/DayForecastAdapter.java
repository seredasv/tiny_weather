package com.example.ssereda.tinyweather.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ssereda.tinyweather.R;
import com.example.ssereda.tinyweather.utils.DateUtils;
import com.example.ssereda.tinyweather.utils.WeatherIconMapper;
import com.survivingwithandroid.weather.lib.model.WeatherForecast;

import java.util.Date;
import java.util.GregorianCalendar;

public class DayForecastAdapter extends ArrayAdapter<WeatherForecast> {
    private WeatherForecast weatherForecast;
    private DateUtils dateUtils;

    public DayForecastAdapter(Context context, int resource, WeatherForecast weatherForecast) {
        super(context, resource);
        this.weatherForecast = weatherForecast;

        dateUtils = new DateUtils(new Date(), new GregorianCalendar());
    }

    @Override
    public int getCount() {
        return weatherForecast.getForecast().size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        ViewHolder holder;

        if (view == null) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            view = inflater.inflate(R.layout.item_day_forecast, parent, false);
            holder = new ViewHolder();

            holder.tvItemDayForecastTimestamp = (TextView) view.findViewById(R.id.tv_item_day_forecast_timestamp);
            holder.tvItemDayForecastTemperature = (TextView) view.findViewById(R.id.tv_item_day_forecast_temperature);
            holder.tvItemDayForecastWind = (TextView) view.findViewById(R.id.tv_item_day_forecast_wind);
            holder.ivItemDayForecastIcon = (ImageView) view.findViewById(R.id.iv_item_day_forecast_icon);

            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        if (weatherForecast != null) {
            dateUtils.sdfDay(position, holder.tvItemDayForecastTimestamp);
            holder.tvItemDayForecastTemperature.setText(String.valueOf((int) weatherForecast.getForecast().get(position).forecastTemp.day)
                    + " " + weatherForecast.getUnit().tempUnit);
            holder.tvItemDayForecastWind.setText(String.valueOf((int) weatherForecast.getForecast().get(position).weather.wind.getSpeed())
                    + " " + weatherForecast.getUnit().speedUnit);
            holder.ivItemDayForecastIcon.setImageResource(WeatherIconMapper.getWeatherResource(weatherForecast.getForecast().get(position).weather.currentCondition.getIcon(), weatherForecast.getForecast().get(position).weather.currentCondition.getWeatherId()));
        }

        return view;
    }

    public class ViewHolder {
        public TextView tvItemDayForecastTimestamp, tvItemDayForecastTemperature, tvItemDayForecastWind;
        public ImageView ivItemDayForecastIcon;
    }
}

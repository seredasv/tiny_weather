package com.example.ssereda.tinyweather.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ssereda.tinyweather.R;
import com.example.ssereda.tinyweather.utils.WeatherIconMapper;
import com.survivingwithandroid.weather.lib.model.WeatherHourForecast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

public class HourForecastAdapter extends ArrayAdapter<WeatherHourForecast> {
    private WeatherHourForecast weatherHourForecast;

    public HourForecastAdapter(Context context, int resource, WeatherHourForecast weatherHourForecast) {
        super(context, resource);
        this.weatherHourForecast = weatherHourForecast;
    }

    @Override
    public int getCount() {
        return weatherHourForecast.getHourForecast().size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        ViewHolder holder;

        if (view == null) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            view = inflater.inflate(R.layout.item_hour_forecast, parent, false);
            holder = new ViewHolder();

            holder.tvItemHourForecastTimestamp = (TextView) view.findViewById(R.id.tv_item_hour_forecast_timestamp);
            holder.tvItemHourForecastTemperature = (TextView) view.findViewById(R.id.tv_item_hour_forecast_temperature);
            holder.tvItemHourForecastWind = (TextView) view.findViewById(R.id.tv_item_hour_forecast_wind);
            holder.ivItemHourForecastIcon = (ImageView) view.findViewById(R.id.iv_item_hour_forecast_icon);

            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        Date date = new Date();
        Calendar gregorianCalendar = new GregorianCalendar();
        gregorianCalendar.setTime(date);
        gregorianCalendar.add(GregorianCalendar.HOUR, position + 1);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MMM HH", Locale.ENGLISH);

        if (weatherHourForecast != null) {
            holder.tvItemHourForecastTimestamp.setText(sdf.format(gregorianCalendar.getTime()) + ":00");
            holder.tvItemHourForecastTemperature.setText(String.valueOf((int) weatherHourForecast.getHourForecast().get(position).weather.temperature.getTemp())
                    + " " + weatherHourForecast.getUnit().tempUnit);
            holder.tvItemHourForecastWind.setText(String.valueOf((int) weatherHourForecast.getHourForecast().get(position).weather.wind.getSpeed())
                    + " " + weatherHourForecast.getUnit().speedUnit);
            holder.ivItemHourForecastIcon.setImageResource(WeatherIconMapper.getWeatherResource(weatherHourForecast.getHourForecast().get(position).weather.currentCondition.getIcon(), weatherHourForecast.getHourForecast().get(position).weather.currentCondition.getWeatherId()));

        }

        return view;
    }

    public class ViewHolder {
        public TextView tvItemHourForecastTimestamp, tvItemHourForecastTemperature, tvItemHourForecastWind;
        public ImageView ivItemHourForecastIcon;
    }
}

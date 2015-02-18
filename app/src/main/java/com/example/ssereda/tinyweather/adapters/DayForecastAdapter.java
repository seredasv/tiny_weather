package com.example.ssereda.tinyweather.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ssereda.tinyweather.R;
import com.example.ssereda.tinyweather.utils.ViewHolder;
import com.example.ssereda.tinyweather.utils.WeatherIconMapper;
import com.survivingwithandroid.weather.lib.model.WeatherForecast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

public class DayForecastAdapter extends ArrayAdapter<WeatherForecast> {
    private Context context;
    private WeatherForecast weatherForecast;

    public DayForecastAdapter(Context context, int resource, WeatherForecast weatherForecast) {
        super(context, resource);
        this.context = context;
        this.weatherForecast = weatherForecast;
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

        Date date = new Date();
        Calendar gregorianCalendar = new GregorianCalendar();
        gregorianCalendar.setTime(date);
        gregorianCalendar.add(GregorianCalendar.DAY_OF_WEEK, position + 1);
        SimpleDateFormat sdfDay = new SimpleDateFormat("EEEE", Locale.ENGLISH);

        if (weatherForecast != null) {
            holder.tvItemDayForecastTimestamp.setText(sdfDay.format(gregorianCalendar.getTime()));
            holder.tvItemDayForecastTemperature.setText(String.valueOf((int) weatherForecast.getForecast().get(position).forecastTemp.day)
                    + " " + weatherForecast.getUnit().tempUnit);
            holder.tvItemDayForecastWind.setText(String.valueOf((int) weatherForecast.getForecast().get(position).weather.wind.getSpeed())
                    + " " + weatherForecast.getUnit().speedUnit);
            holder.ivItemDayForecastIcon.setImageResource(WeatherIconMapper.getWeatherResource(weatherForecast.getForecast().get(position).weather.currentCondition.getIcon(), weatherForecast.getForecast().get(position).weather.currentCondition.getWeatherId()));

        }

        return view;
    }
}

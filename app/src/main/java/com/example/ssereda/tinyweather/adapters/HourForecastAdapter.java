package com.example.ssereda.tinyweather.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.ssereda.tinyweather.R;
import com.example.ssereda.tinyweather.utils.ViewHolder;
import com.survivingwithandroid.weather.lib.model.HourForecast;
import com.survivingwithandroid.weather.lib.model.WeatherHourForecast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class HourForecastAdapter extends ArrayAdapter<WeatherHourForecast> {
    private Context context;
    private WeatherHourForecast weatherHourForecast;

    public HourForecastAdapter(Context context, int resource, WeatherHourForecast weatherHourForecast) {
        super(context, resource);
        this.context = context;
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

            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        HourForecast forecast = weatherHourForecast.getHourForecast().get(position);
        Date d = new Date();
        Calendar gc =  new GregorianCalendar();
        gc.setTime(d);
        gc.add(GregorianCalendar.HOUR, position + 1);
        SimpleDateFormat sdfDay = new SimpleDateFormat("E");
        SimpleDateFormat sdfMonth = new SimpleDateFormat("dd/MMM HH");
        SimpleDateFormat sdfHour = new SimpleDateFormat("HH");

        if (weatherHourForecast != null) {
            holder.tvItemHourForecastTimestamp.setText(sdfHour.format(gc.getTime()) + ":00");
            holder.tvItemHourForecastTemperature.setText(String.valueOf((int) weatherHourForecast.getHourForecast().get(position).weather.temperature.getTemp()) + " " + weatherHourForecast.getUnit().tempUnit);
            holder.tvItemHourForecastWind.setText(String.valueOf((int) weatherHourForecast.getHourForecast().get(position).weather.wind.getSpeed()) + " " + weatherHourForecast.getUnit().speedUnit);
        }

        return view;
    }
}

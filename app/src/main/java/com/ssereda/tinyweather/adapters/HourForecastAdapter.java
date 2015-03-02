package com.ssereda.tinyweather.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ssereda.tinyweather.R;
import com.ssereda.tinyweather.utils.DateUtils;
import com.ssereda.tinyweather.utils.WeatherIconMapper;
import com.survivingwithandroid.weather.lib.model.WeatherHourForecast;

import java.util.Date;
import java.util.GregorianCalendar;

public class HourForecastAdapter extends ArrayAdapter<WeatherHourForecast> {
    private WeatherHourForecast weatherHourForecast;
    private DateUtils dateUtils;
    private Context context;

    public HourForecastAdapter(Context context, int resource, WeatherHourForecast weatherHourForecast) {
        super(context, resource);
        this.weatherHourForecast = weatherHourForecast;
        this.context = context;
        dateUtils = new DateUtils(new Date(), new GregorianCalendar());
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

        if (weatherHourForecast != null) {
            dateUtils.sdfHour(position, holder.tvItemHourForecastTimestamp);
            holder.tvItemHourForecastTemperature.setText(String.valueOf((int) weatherHourForecast.getHourForecast().get(position).weather.temperature.getTemp())
                    + " " + weatherHourForecast.getUnit().tempUnit);
            holder.tvItemHourForecastWind.setText(String.valueOf((int) weatherHourForecast.getHourForecast().get(position).weather.wind.getSpeed())
                    + " " + context.getResources().getString(R.string.wind_speed));
            holder.ivItemHourForecastIcon.setImageResource(WeatherIconMapper.getWeatherResource(weatherHourForecast.getHourForecast().get(position).weather.currentCondition.getIcon(), weatherHourForecast.getHourForecast().get(position).weather.currentCondition.getWeatherId()));

        }

        return view;
    }

    public class ViewHolder {
        public TextView tvItemHourForecastTimestamp, tvItemHourForecastTemperature, tvItemHourForecastWind;
        public ImageView ivItemHourForecastIcon;
    }
}

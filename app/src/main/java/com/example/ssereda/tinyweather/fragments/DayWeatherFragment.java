package com.example.ssereda.tinyweather.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.ssereda.tinyweather.MainActivity;
import com.example.ssereda.tinyweather.R;
import com.example.ssereda.tinyweather.adapters.DayForecastAdapter;
import com.survivingwithandroid.weather.lib.WeatherClient;
import com.survivingwithandroid.weather.lib.exception.WeatherLibException;
import com.survivingwithandroid.weather.lib.model.WeatherForecast;

public class DayWeatherFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_weather_day, null);

        ListView lvDayForecast = (ListView) view.findViewById(R.id.lv_day_forecast);

        if (WeatherFragment.placesID != null && WeatherFragment.placesID.length() > 0) {
            getForecastWeather(getActivity(), WeatherFragment.placesID, lvDayForecast);
        }

        return view;
    }

    public void getForecastWeather(final Context context, String cityID, final ListView listView) {
        MainActivity.weatherClient.getForecastWeather(cityID, new WeatherClient.ForecastWeatherEventListener() {
            @Override
            public void onWeatherRetrieved(WeatherForecast weatherForecast) {
                if (weatherForecast != null && weatherForecast.getForecast().size() > 0) {
                    DayForecastAdapter adapter = new DayForecastAdapter(context, R.layout.item_day_forecast, weatherForecast);
                    listView.setAdapter(adapter);
                }
            }

            @Override
            public void onWeatherError(WeatherLibException t) {
                Log.e("mylog", "weather error");
            }

            @Override
            public void onConnectionError(Throwable t) {
                Log.e("mylog", "connection error");
            }
        });
    }
}

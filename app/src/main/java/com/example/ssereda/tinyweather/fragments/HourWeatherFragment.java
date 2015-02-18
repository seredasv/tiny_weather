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
import com.example.ssereda.tinyweather.adapters.HourForecastAdapter;
import com.survivingwithandroid.weather.lib.WeatherClient;
import com.survivingwithandroid.weather.lib.exception.WeatherLibException;
import com.survivingwithandroid.weather.lib.model.WeatherHourForecast;

public class HourWeatherFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_weather_hour, null);

        ListView lvHourForecast = (ListView) view.findViewById(R.id.lv_hour_forecast);

        if (WeatherFragment.placesID != null && WeatherFragment.placesID.length() > 0) {
            getHourForecastWeather(getActivity(), WeatherFragment.placesID, lvHourForecast);
        }

        return view;
    }

    public static void getHourForecastWeather(final Context context, String cityID, final ListView listView) {
        MainActivity.weatherClient.getHourForecastWeather(cityID, new WeatherClient.HourForecastWeatherEventListener() {
                    @Override
                    public void onWeatherRetrieved(WeatherHourForecast weatherHourForecast) {
                        if (weatherHourForecast != null) {
                            HourForecastAdapter adapter = new HourForecastAdapter(context, R.layout.item_hour_forecast, weatherHourForecast);
                            listView.setAdapter(adapter);
                        }
                    }

                    @Override
                    public void onWeatherError(WeatherLibException wle) {
                        Log.e("mylog", "weather error");
                    }

                    @Override
                    public void onConnectionError(Throwable t) {
                        Log.e("mylog", "connection error");
                    }
                }
        );
    }
}

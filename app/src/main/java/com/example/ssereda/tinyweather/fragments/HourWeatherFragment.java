package com.example.ssereda.tinyweather.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.ssereda.tinyweather.R;
import com.example.ssereda.tinyweather.adapters.HourForecastAdapter;
import com.example.ssereda.tinyweather.utils.DBHelper;
import com.example.ssereda.tinyweather.utils.Weather;
import com.survivingwithandroid.weather.lib.WeatherClient;
import com.survivingwithandroid.weather.lib.exception.WeatherLibException;
import com.survivingwithandroid.weather.lib.model.WeatherHourForecast;

public class HourWeatherFragment extends Fragment {
    private WeatherClient weatherClient;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        weatherClient = Weather.getInstance().weatherClient(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_weather_hour, null);

        ListView lvHourForecast = (ListView) view.findViewById(R.id.lv_hour_forecast);

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("last_place_id", Context.MODE_PRIVATE);
        String placeID = sharedPreferences.getString(DBHelper.PLACES_ID, "");
        if (placeID != null && placeID.length() > 0) {
            getHourForecastWeather(getActivity(), placeID, lvHourForecast);
        }

        return view;
    }

    private void getHourForecastWeather(final Context context, String cityID, final ListView listView) {
        weatherClient.getHourForecastWeather(cityID, new WeatherClient.HourForecastWeatherEventListener() {
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

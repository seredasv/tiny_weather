package com.example.ssereda.tinyweather.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ssereda.tinyweather.R;
import com.example.ssereda.tinyweather.utils.DBHelper;
import com.example.ssereda.tinyweather.utils.Weather;
import com.example.ssereda.tinyweather.utils.WeatherIconMapper;
import com.survivingwithandroid.weather.lib.WeatherClient;
import com.survivingwithandroid.weather.lib.exception.WeatherLibException;
import com.survivingwithandroid.weather.lib.model.CurrentWeather;
import com.survivingwithandroid.weather.lib.model.DayForecast;
import com.survivingwithandroid.weather.lib.model.WeatherForecast;
import com.survivingwithandroid.weather.lib.request.WeatherRequest;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

public class WeatherFragment extends Fragment {
    private String placesID, placesName;
    private TextView tvWind, tvHumidity, tvCurrentTemperature, tvTemperature_1, tvTemperature_2,
            tvTemperature_3;
    private ImageView imageViewCurrentWeatherIcon;
    private Calendar gregorianCalendar;
    private SimpleDateFormat sdfTime;
    private Date date;
    private WeatherClient weatherClient;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            placesID = bundle.getString(DBHelper.PLACES_ID);
            placesName = bundle.getString(DBHelper.PLACES_NAME);
        }

        weatherClient = Weather.getInstance().weatherClient(getActivity());
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("last_place_id", Context.MODE_PRIVATE);

        if (sharedPreferences != null) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(DBHelper.PLACES_ID, placesID);
            editor.putString(DBHelper.PLACES_NAME, placesName);
            editor.apply();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_weather, null);

        Typeface typeface = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Roboto-Thin.ttf");

        imageViewCurrentWeatherIcon = (ImageView) view.findViewById(R.id.image_view_current_weather);
        TextView tvCurrentDayData = (TextView) view.findViewById(R.id.text_view_current_day_data);
        TextView tvDayData_1 = (TextView) view.findViewById(R.id.text_view_day_data_1);
        TextView tvDayData_2 = (TextView) view.findViewById(R.id.text_view_day_data_2);
        TextView tvDayData_3 = (TextView) view.findViewById(R.id.text_view_day_data_3);
        TextView tvPlaceName = (TextView) view.findViewById(R.id.text_view_place_name);
        tvWind = (TextView) view.findViewById(R.id.text_view_wind);
        tvHumidity = (TextView) view.findViewById(R.id.text_view_humidity);
        tvCurrentTemperature = (TextView) view.findViewById(R.id.text_view_current_temperature);
        tvCurrentTemperature.setTypeface(typeface);
        tvTemperature_1 = (TextView) view.findViewById(R.id.text_view_temperature_1);
        tvTemperature_2 = (TextView) view.findViewById(R.id.text_view_temperature_2);
        tvTemperature_3 = (TextView) view.findViewById(R.id.text_view_temperature_3);
        TextView tvWeatherUpdateTime = (TextView) view.findViewById(R.id.text_view_weather_update_time);
        ImageView imageViewUpdateWeather = (ImageView) view.findViewById(R.id.image_view_update_weather);
        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.material_toolbar);

        date = new Date();
        gregorianCalendar = new GregorianCalendar();
        gregorianCalendar.setTime(date);
        SimpleDateFormat sdfWeekDay = new SimpleDateFormat("EEEE", Locale.ENGLISH);
        SimpleDateFormat sdfWeekDayAndTime = new SimpleDateFormat("EEEE HH:mm", Locale.ENGLISH);
        sdfTime = new SimpleDateFormat("HH:mm:ss", Locale.ENGLISH);

        imageViewUpdateWeather.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (placesID != null && placesID.length() > 0) {
                    getCurrentCondition(placesID);
                    getForecastWeather(placesID);
                }

                setUpdatedText(R.id.text_view_weather_update_time);
            }
        });

        toolbar.setTitle(placesName);

        if (placesID != null && placesID.length() > 0) {
            getCurrentCondition(placesID);
            getForecastWeather(placesID);
        }

        tvCurrentDayData.setText(sdfWeekDayAndTime.format(gregorianCalendar.getTime()));
        tvWeatherUpdateTime.setText("last update: " + sdfTime.format(gregorianCalendar.getTime()));

        gregorianCalendar.add(GregorianCalendar.DAY_OF_WEEK, 1);
        tvDayData_1.setText(sdfWeekDay.format(gregorianCalendar.getTime()));

        gregorianCalendar.add(GregorianCalendar.DAY_OF_WEEK, 1);
        tvDayData_2.setText(sdfWeekDay.format(gregorianCalendar.getTime()));

        gregorianCalendar.add(GregorianCalendar.DAY_OF_WEEK, 1);
        tvDayData_3.setText(sdfWeekDay.format(gregorianCalendar.getTime()));

        if (placesName != null && placesName.length() > 0) {
            tvPlaceName.setText(placesName);
        }

        return view;
    }

    private void getCurrentCondition(String cityID) {
        if (weatherClient != null) {
            weatherClient.getCurrentCondition(new WeatherRequest(cityID), new WeatherClient.WeatherEventListener() {
                @Override
                public void onWeatherRetrieved(CurrentWeather currentWeather) {
                    tvWind.setText(String.valueOf((int) currentWeather.weather.wind.getSpeed())
                            + " " + currentWeather.getUnit().speedUnit);
                    tvCurrentTemperature.setText(String.valueOf((int) currentWeather.weather.temperature.getTemp())
                            + " " + currentWeather.getUnit().tempUnit);
                    tvHumidity.setText(String.valueOf((int) currentWeather.weather.currentCondition.getHumidity())
                            + " %");
                    imageViewCurrentWeatherIcon.setImageResource(WeatherIconMapper
                            .getWeatherResource(currentWeather.weather.currentCondition.getIcon(),
                                    currentWeather.weather.currentCondition.getWeatherId()));

//                setToolbarColor(currentWeather.weather.temperature.getTemp());
                }

                @Override
                public void onWeatherError(WeatherLibException weatherLibException) {
                    Log.e("mylog", "weather lib exception");
                }

                @Override
                public void onConnectionError(Throwable t) {
                    Log.e("mylog", "connection error");
                }
            });
        }
    }

    private void getForecastWeather(String cityID) {
        if (weatherClient != null) {
            weatherClient.getForecastWeather(cityID, new WeatherClient.ForecastWeatherEventListener() {
                @Override
                public void onWeatherRetrieved(WeatherForecast weatherForecast) {
                    List<DayForecast> forecast = weatherForecast.getForecast();
                    tvTemperature_1.setText((int) forecast.get(1).forecastTemp.day + " " + weatherForecast.getUnit().tempUnit);
                    tvTemperature_2.setText((int) forecast.get(2).forecastTemp.day + " " + weatherForecast.getUnit().tempUnit);
                    tvTemperature_3.setText((int) forecast.get(3).forecastTemp.day + " " + weatherForecast.getUnit().tempUnit);
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

    private void setUpdatedText(int id) {
        if (date != null) {
            date = null;
        }
        date = new Date();
        if (gregorianCalendar != null) {
            gregorianCalendar = null;
        }
        gregorianCalendar = new GregorianCalendar();
        gregorianCalendar.setTime(date);
        if (sdfTime != null) {
            sdfTime = null;
        }
        sdfTime = new SimpleDateFormat("HH:mm:ss", Locale.ENGLISH);

        if (getView() != null) {
            TextView textView = (TextView) getView().findViewById(id);
            textView.setText("last update: " + sdfTime.format(gregorianCalendar.getTime()));
        }
    }
}

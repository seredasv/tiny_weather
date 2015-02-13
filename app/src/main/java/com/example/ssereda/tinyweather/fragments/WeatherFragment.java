package com.example.ssereda.tinyweather.fragments;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.ssereda.tinyweather.MainActivity;
import com.example.ssereda.tinyweather.R;
import com.example.ssereda.tinyweather.utils.DBHelper;
import com.survivingwithandroid.weather.lib.WeatherClient;
import com.survivingwithandroid.weather.lib.exception.WeatherLibException;
import com.survivingwithandroid.weather.lib.model.CurrentWeather;
import com.survivingwithandroid.weather.lib.request.WeatherRequest;

import java.util.Calendar;

public class WeatherFragment extends Fragment {
    private int id;
    private String placesID, placesCountry, placesRegion, placesName;
    private TextView textViewWind, textViewTemperature, textViewHumidity, textViewCurrentTemperature;
    private Calendar calendar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            id = bundle.getInt(DBHelper.ID);
            placesID = bundle.getString(DBHelper.PLACES_ID);
            placesCountry = bundle.getString(DBHelper.PLACES_COUNTRY);
            placesRegion = bundle.getString(DBHelper.PLACES_REGION);
            placesName = bundle.getString(DBHelper.PLACES_NAME);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_weather, null);

        Typeface typeface = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Roboto-Thin.ttf");

        TextView textViewCurrentDayData = (TextView) view.findViewById(R.id.text_view_current_day_data);
        TextView textViewDayData_1 = (TextView) view.findViewById(R.id.text_view_day_data_1);
        TextView textViewDayData_2 = (TextView) view.findViewById(R.id.text_view_day_data_2);
        TextView textViewDayData_3 = (TextView) view.findViewById(R.id.text_view_day_data_3);
        TextView textViewPlaceName = (TextView) view.findViewById(R.id.text_view_place_name);
        textViewWind = (TextView) view.findViewById(R.id.text_view_wind);
        textViewTemperature = (TextView) view.findViewById(R.id.text_view_temperature);
        textViewHumidity = (TextView) view.findViewById(R.id.text_view_humidity);
        textViewCurrentTemperature = (TextView) view.findViewById(R.id.text_view_current_temperature);
        textViewCurrentTemperature.setTypeface(typeface);
        TextView textViewTemperature_1 = (TextView) view.findViewById(R.id.text_view_temperature_1);
        TextView textViewTemperature_2 = (TextView) view.findViewById(R.id.text_view_temperature_2);
        TextView textViewTemperature_3 = (TextView) view.findViewById(R.id.text_view_temperature_3);

        if (placesID != null && placesID.length() > 0) {
            getCurrentCondition(placesID);
        }

        calendar = Calendar.getInstance();
        String currentDayOfWeek = String.valueOf(getWeekDay(changeFirstDayOfWeek(calendar.get(Calendar.DAY_OF_WEEK) - 1)));
        String currentHour = String.valueOf(calendar.get(Calendar.HOUR_OF_DAY));
        String currentMinute = String.valueOf(calendar.get(Calendar.MINUTE));
        textViewCurrentDayData.setText(currentDayOfWeek + "  " + currentHour + ":" + currentMinute);

        calendar.add(Calendar.DAY_OF_WEEK, 1);
        String dayOfWeek_1 = String.valueOf(getWeekDay(changeFirstDayOfWeek(calendar.get(Calendar.DAY_OF_WEEK) - 1)));
        textViewDayData_1.setText(dayOfWeek_1);

        calendar.add(Calendar.DAY_OF_WEEK, 1);
        String dayOfWeek_2 = String.valueOf(getWeekDay(changeFirstDayOfWeek(calendar.get(Calendar.DAY_OF_WEEK) - 1)));
        textViewDayData_2.setText(dayOfWeek_2);

        calendar.add(Calendar.DAY_OF_WEEK, 1);
        String dayOfWeek_3 = String.valueOf(getWeekDay(changeFirstDayOfWeek(calendar.get(Calendar.DAY_OF_WEEK) - 1)));
        textViewDayData_3.setText(dayOfWeek_3);

        if (placesName != null && placesName.length() > 0) {
            textViewPlaceName.setText(placesName);
        }

        return view;
    }

    private void getCurrentCondition(String cityID) {
        MainActivity.weatherClient.getCurrentCondition(new WeatherRequest(cityID), new WeatherClient.WeatherEventListener() {
            @Override
            public void onWeatherRetrieved(CurrentWeather currentWeather) {
                float currentTemperature = currentWeather.weather.temperature.getTemp();
                float currentHumidity = currentWeather.weather.currentCondition.getHumidity();
                float currentWindSpeed = currentWeather.weather.wind.getSpeed();

                textViewWind.setText(String.valueOf(currentWindSpeed) + " m/s");
                textViewTemperature.setText(String.valueOf(currentTemperature));
                textViewCurrentTemperature.setText(String.valueOf(currentTemperature));
                textViewHumidity.setText(String.valueOf(currentHumidity) + " mm");

                Log.e("mylog", "descr: " + currentWeather.weather.currentCondition.getDescr());
                Log.e("mylog", "temp: " + String.format("%.0f", currentWeather.weather.temperature.getTemp()));
                Log.e("mylog", "press: " + String.valueOf(currentWeather.weather.currentCondition.getPressure()));
                Log.e("mylog", "wind: " + String.valueOf(currentWeather.weather.wind.getSpeed()));
                Log.e("mylog", "humidity: " + String.valueOf(currentWeather.weather.currentCondition.getHumidity()));
                Log.e("mylog", "condition: " + String.valueOf(currentWeather.weather.currentCondition.getCondition()));
                Log.e("mylog", "feels like: " + String.valueOf(currentWeather.weather.currentCondition.getFeelsLike()));
                Log.e("mylog", "press trend: " + String.valueOf(currentWeather.weather.currentCondition.getPressureTrend()));
                Log.e("mylog", "visibility: " + String.valueOf(currentWeather.weather.currentCondition.getVisibility()));
                Log.e("mylog", "weather code: " + String.valueOf(currentWeather.weather.currentCondition.getWeatherCode()));
                Log.e("mylog", "weather id: " + String.valueOf(currentWeather.weather.currentCondition.getWeatherId()));

//                weatherIcon.setImageResource(WeatherIconMapper.getWeatherResource(currentWeather.weather.currentCondition.getIcon(), currentWeather.weather.currentCondition.getWeatherId()));
//
//                setToolbarColor(currentWeather.weather.temperature.getTemp());
            }

            @Override
            public void onWeatherError(WeatherLibException weatherLibException) {
                Log.e("mylog", "weather lib exception");
                Log.e("mylog", String.valueOf(weatherLibException));
            }

            @Override
            public void onConnectionError(Throwable t) {
                Log.e("mylog", "connection error");
            }
        });
    }

    public String getWeekDay(int weekDay) {
        switch (weekDay) {
            case 1:
                return "Monday";
            case 2:
                return "Tuesday";
            case 3:
                return "Wednesday";
            case 4:
                return "Thursday";
            case 5:
                return "Friday";
            case 6:
                return "Saturday";
            case 7:
                return "Sunday";
            default:
                return "";
        }
    }

    public int changeFirstDayOfWeek(int dayOfWeek) {
        if (dayOfWeek == 0) {
            dayOfWeek = 7;
            return dayOfWeek;
        } else {
            return dayOfWeek;
        }
    }
}

package com.example.ssereda.tinyweather.fragments;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ssereda.tinyweather.MainActivity;
import com.example.ssereda.tinyweather.R;
import com.example.ssereda.tinyweather.utils.DBHelper;
import com.example.ssereda.tinyweather.utils.WeatherIconMapper;
import com.survivingwithandroid.weather.lib.WeatherClient;
import com.survivingwithandroid.weather.lib.exception.WeatherLibException;
import com.survivingwithandroid.weather.lib.model.CurrentWeather;
import com.survivingwithandroid.weather.lib.model.DayForecast;
import com.survivingwithandroid.weather.lib.model.WeatherForecast;
import com.survivingwithandroid.weather.lib.request.WeatherRequest;

import java.util.Calendar;
import java.util.List;

public class WeatherFragment extends Fragment {
    private int id;
    public static String placesID;
    private String placesCountry, placesRegion, placesName;
    private TextView textViewWind, textViewHumidity, textViewCurrentTemperature,
        textViewWeatherUpdateTime, textViewCurrentDayData, textViewTemperature_1,
            textViewTemperature_2, textViewTemperature_3;
    private ImageView imageViewCurrentWeatherIcon, imageViewUpdateWeather;
    private Calendar calendar;
    private View view;

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
        view = inflater.inflate(R.layout.fragment_weather, null);

        Typeface typeface = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Roboto-Thin.ttf");

        imageViewCurrentWeatherIcon = (ImageView) view.findViewById(R.id.image_view_current_weather);
        textViewCurrentDayData = (TextView) view.findViewById(R.id.text_view_current_day_data);
        TextView textViewDayData_1 = (TextView) view.findViewById(R.id.text_view_day_data_1);
        TextView textViewDayData_2 = (TextView) view.findViewById(R.id.text_view_day_data_2);
        TextView textViewDayData_3 = (TextView) view.findViewById(R.id.text_view_day_data_3);
        TextView textViewPlaceName = (TextView) view.findViewById(R.id.text_view_place_name);
        textViewWind = (TextView) view.findViewById(R.id.text_view_wind);
        textViewHumidity = (TextView) view.findViewById(R.id.text_view_humidity);
        textViewCurrentTemperature = (TextView) view.findViewById(R.id.text_view_current_temperature);
        textViewCurrentTemperature.setTypeface(typeface);
        textViewTemperature_1 = (TextView) view.findViewById(R.id.text_view_temperature_1);
        textViewTemperature_2 = (TextView) view.findViewById(R.id.text_view_temperature_2);
        textViewTemperature_3 = (TextView) view.findViewById(R.id.text_view_temperature_3);
        textViewWeatherUpdateTime = (TextView) view.findViewById(R.id.text_view_weather_update_time);
        imageViewUpdateWeather = (ImageView) view.findViewById(R.id.image_view_update_weather);
        imageViewUpdateWeather.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (placesID != null && placesID.length() > 0) {
                    getCurrentCondition(placesID);
                    getForecastWeather(placesID);
                }
                setUpdatedText("last update: " + getCurrentTimeAndData("", "", true), R.id.text_view_weather_update_time);
            }
        });

        MainActivity.toolbar.setTitle(placesName);

        if (placesID != null && placesID.length() > 0) {
            getCurrentCondition(placesID);
            getForecastWeather(placesID);
        }

        calendar = Calendar.getInstance();
        String currentDayOfWeek = String.valueOf(getWeekDayName(changeFirstDayOfWeek(calendar.get(Calendar.DAY_OF_WEEK) - 1)));
        textViewCurrentDayData.setText(getCurrentTimeAndData(currentDayOfWeek, " ", false));
        textViewWeatherUpdateTime.setText("last update: " + getCurrentTimeAndData("", "", true));

        calendar.add(Calendar.DAY_OF_WEEK, 1);
        String dayOfWeek_1 = String.valueOf(getWeekDayName(changeFirstDayOfWeek(calendar.get(Calendar.DAY_OF_WEEK) - 1)));
        textViewDayData_1.setText(dayOfWeek_1);

        calendar.add(Calendar.DAY_OF_WEEK, 1);
        String dayOfWeek_2 = String.valueOf(getWeekDayName(changeFirstDayOfWeek(calendar.get(Calendar.DAY_OF_WEEK) - 1)));
        textViewDayData_2.setText(dayOfWeek_2);

        calendar.add(Calendar.DAY_OF_WEEK, 1);
        String dayOfWeek_3 = String.valueOf(getWeekDayName(changeFirstDayOfWeek(calendar.get(Calendar.DAY_OF_WEEK) - 1)));
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
                int currentTemperature = (int) currentWeather.weather.temperature.getTemp();
                int currentHumidity = (int) currentWeather.weather.currentCondition.getHumidity();
                int currentWindSpeed = (int) currentWeather.weather.wind.getSpeed();

                textViewWind.setText(String.valueOf(currentWindSpeed) + " " + currentWeather.getUnit().speedUnit);
                textViewCurrentTemperature.setText(String.valueOf(currentTemperature) + " " + currentWeather.getUnit().tempUnit);
                textViewHumidity.setText(String.valueOf(currentHumidity) + " %");
                imageViewCurrentWeatherIcon.setImageResource(WeatherIconMapper.getWeatherResource(currentWeather.weather.currentCondition.getIcon(), currentWeather.weather.currentCondition.getWeatherId()));

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

    public void getForecastWeather(String cityID) {
        MainActivity.weatherClient.getForecastWeather(cityID, new WeatherClient.ForecastWeatherEventListener() {
            @Override
            public void onWeatherRetrieved(WeatherForecast weatherForecast) {
                List<DayForecast> forecast = weatherForecast.getForecast();
                textViewTemperature_1.setText((int) forecast.get(0).forecastTemp.day + " " + weatherForecast.getUnit().tempUnit);
                textViewTemperature_2.setText((int) forecast.get(1).forecastTemp.day + " " + weatherForecast.getUnit().tempUnit);
                textViewTemperature_3.setText((int) forecast.get(2).forecastTemp.day + " " + weatherForecast.getUnit().tempUnit);
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

    public void setUpdatedText(String text, int id) {
        if (getView() != null) {
            TextView textView = (TextView) getView().findViewById(id);
            textView.setText(text);
        }
    }

    public String getWeekDayName(int weekDay) {
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

    public String getCurrentTimeAndData(String currentDayOfWeek, String backspace, boolean showSeconds) {
        int tempHour, tempMinute, tempSeconds;
        String currentHour = null, currentMinute = null, currentSeconds = null;
        calendar = Calendar.getInstance();
        tempHour = calendar.get(Calendar.HOUR_OF_DAY);
        if (tempHour < 10) {
            currentHour = "0" + tempHour;
        } else {
            currentHour = String.valueOf(tempHour);
        }

        tempMinute = calendar.get(Calendar.MINUTE);
        if (tempMinute < 10) {
            currentMinute = "0" + tempMinute;
        } else {
            currentMinute = String.valueOf(tempMinute);
        }

        tempSeconds = calendar.get(Calendar.SECOND);
        if (tempSeconds < 10) {
            currentSeconds = "0" + tempSeconds;
        } else {
            currentSeconds = String.valueOf(tempSeconds);
        }

        if (showSeconds) {
            return currentDayOfWeek + backspace + currentHour + ":" + currentMinute + ":" + currentSeconds;
        } else {
            return currentDayOfWeek + backspace + currentHour + ":" + currentMinute;
        }
    }
}

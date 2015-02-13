package com.example.ssereda.tinyweather.utils;

import android.content.Context;
import android.location.Criteria;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.ListView;

import com.example.ssereda.tinyweather.MainActivity;
import com.example.ssereda.tinyweather.R;
import com.example.ssereda.tinyweather.adapters.CityAdapter;
import com.survivingwithandroid.weather.lib.WeatherClient;
import com.survivingwithandroid.weather.lib.exception.LocationProviderNotFoundException;
import com.survivingwithandroid.weather.lib.exception.WeatherLibException;
import com.survivingwithandroid.weather.lib.model.City;
import com.survivingwithandroid.weather.lib.model.CurrentWeather;
import com.survivingwithandroid.weather.lib.model.WeatherForecast;
import com.survivingwithandroid.weather.lib.model.WeatherHourForecast;
import com.survivingwithandroid.weather.lib.request.WeatherRequest;

import java.util.List;

public class Utils {
    public static String cityID;

    public static boolean isWifiConnected(Context context) {
        ConnectivityManager connManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return ((netInfo != null) && netInfo.isConnected());
    }

    public static boolean isMobileConnected(Context context) {
        ConnectivityManager connManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        return ((netInfo != null) && netInfo.isConnected());
    }

    public static void searchCityByLocation(final Context context, final ListView listView, Criteria criteria) {
        try {
            MainActivity.weatherClient.searchCityByLocation(WeatherClient.createDefaultCriteria(), new WeatherClient.CityEventListener() {
                //            MainActivity.weatherClient.searchCityByLocation(criteria, new WeatherClient.CityEventListener() {
                @Override
                public void onCityListRetrieved(List<City> cities) {
                    CityAdapter cityAdapter = new CityAdapter(context, R.layout.item_city_list, cities);
                    listView.setAdapter(cityAdapter);
                }

                @Override
                public void onWeatherError(WeatherLibException wle) {
                    Log.e("mylog", "weather lib exception");
                }

                @Override
                public void onConnectionError(Throwable t) {
                    Log.e("mylog", "on connection error");
                }
            });
        } catch (LocationProviderNotFoundException e) {
            Log.e("mylog", String.valueOf(e));
        }
    }

    public static void searchCity(CharSequence charSequence, final Context context, final ListView listView) {
        if (MainActivity.weatherClient != null) {
            MainActivity.weatherClient.searchCity(charSequence.toString(), new WeatherClient.CityEventListener() {
                @Override
                public void onCityListRetrieved(List<City> cities) {
                    CityAdapter cityAdapter = new CityAdapter(context, R.layout.item_city_list, cities);
                    listView.setAdapter(cityAdapter);
                }

                @Override
                public void onWeatherError(WeatherLibException e) {
                    Log.e("mylog", "weather error");
                }

                @Override
                public void onConnectionError(Throwable throwable) {
                    Log.e("mylog", "connection error");
                }
            });
        }
    }

    protected static void updateView(Object obj) {
        CurrentWeather weather = (CurrentWeather) obj;
//        cityText.setText(weather.location.getCity() + "," + weather.location.getCountry());
//        condDescr.setText(weather.currentCondition.getCondition() + "(" + weather.currentCondition.getDescr() + ")");
//        temp.setText("" + ((int) weather.temperature.getTemp()));
//        unitTemp.setText(weather.getUnit().tempUnit);
    }

    public static void getCurrentCondition(String cityID) {
        MainActivity.weatherClient.getCurrentCondition(new WeatherRequest(cityID), new WeatherClient.WeatherEventListener() {
            @Override
            public void onWeatherRetrieved(CurrentWeather currentWeather) {
                float currentTemp = currentWeather.weather.temperature.getTemp();
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

    public static void getHourForecastWeather(String cityID) {
        MainActivity.weatherClient.getHourForecastWeather(cityID, new WeatherClient.HourForecastWeatherEventListener() {
                    @Override
                    public void onWeatherRetrieved(WeatherHourForecast weatherHourForecast) {
                        updateView(weatherHourForecast);
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

    public void getForecastWeather(String cityID) {
        MainActivity.weatherClient.getForecastWeather(cityID, new WeatherClient.ForecastWeatherEventListener() {
            @Override
            public void onWeatherRetrieved(WeatherForecast weatherForecast) {
                updateView(weatherForecast);
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

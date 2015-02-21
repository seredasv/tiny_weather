package com.example.ssereda.tinyweather.utils;

import android.content.Context;
import android.location.Criteria;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.ssereda.tinyweather.adapters.CityAdapter;
import com.example.ssereda.tinyweather.adapters.DayForecastAdapter;
import com.example.ssereda.tinyweather.adapters.HourForecastAdapter;
import com.survivingwithandroid.weather.lib.WeatherClient;
import com.survivingwithandroid.weather.lib.WeatherConfig;
import com.survivingwithandroid.weather.lib.client.okhttp.WeatherDefaultClient;
import com.survivingwithandroid.weather.lib.exception.LocationProviderNotFoundException;
import com.survivingwithandroid.weather.lib.exception.WeatherLibException;
import com.survivingwithandroid.weather.lib.model.City;
import com.survivingwithandroid.weather.lib.model.CurrentWeather;
import com.survivingwithandroid.weather.lib.model.DayForecast;
import com.survivingwithandroid.weather.lib.model.WeatherForecast;
import com.survivingwithandroid.weather.lib.model.WeatherHourForecast;
import com.survivingwithandroid.weather.lib.provider.openweathermap.OpenweathermapProviderType;
import com.survivingwithandroid.weather.lib.request.WeatherRequest;

import java.util.List;

//            Open weather map -> OpenweathermapProviderType
//            Yahoo! Weather -> YahooWeatherProviderType
//            Weather underground -> WeatherundergroundProviderType
//            Forecast.io -> ForecastIOProviderType

public class Weather {
    private static Weather instance;
    private WeatherClient weatherClient;
    private Context context;

    private Weather(Context context) {
        this.context = context;
    }

    public static synchronized Weather getInstance(Context context) {
        if (instance == null) {
            instance = new Weather(context);
        }
        instance.weatherClient(context);
        return instance;
    }

    public WeatherConfig config() {
        WeatherConfig config = new WeatherConfig();
        config.unitSystem = WeatherConfig.UNIT_SYSTEM.M;
        config.lang = "en"; // If you want to use english
        config.maxResult = 10; // Max number of cities retrieved
        config.numDays = 10; // Max num of days in the forecast

        return config;
    }

    public WeatherClient weatherClient(Context context) {
        try {
            weatherClient = (new WeatherClient.ClientBuilder()).attach(context)
                    .httpClient(WeatherDefaultClient.class)
                    .provider(new OpenweathermapProviderType())
                    .config(config())
                    .build();
        } catch (Throwable t) {
            t.printStackTrace();
        }

        return weatherClient;
    }

    public void getForecastWeather(String cityID, final int dayID, final TextView textView) {
        weatherClient.getForecastWeather(cityID, new WeatherClient.ForecastWeatherEventListener() {
            @Override
            public void onWeatherRetrieved(WeatherForecast weatherForecast) {
                List<DayForecast> forecast = weatherForecast.getForecast();
                textView.setText((int) forecast.get(dayID).forecastTemp.day
                        + " " + weatherForecast.getUnit().tempUnit);
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

    public void getCurrentCondition(String cityID, final TextView tvWind, final TextView tvCurTemp,
                                    final TextView tvHumidity, final ImageView ivIcon) {
        weatherClient.getCurrentCondition(new WeatherRequest(cityID), new WeatherClient.WeatherEventListener() {
            @Override
            public void onWeatherRetrieved(CurrentWeather currentWeather) {
                tvWind.setText(String.valueOf((int) currentWeather.weather.wind.getSpeed())
                        + " " + currentWeather.getUnit().speedUnit);
                tvCurTemp.setText(String.valueOf((int) currentWeather.weather.temperature.getTemp())
                        + " " + currentWeather.getUnit().tempUnit);
                tvHumidity.setText(String.valueOf((int) currentWeather.weather.currentCondition.getHumidity())
                        + " %");
                ivIcon.setImageResource(WeatherIconMapper
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

    public void getHourForecastWeather(final Context context, String cityID, final ListView listView,
                                       final int layoutID) {
        weatherClient.getHourForecastWeather(cityID, new WeatherClient.HourForecastWeatherEventListener() {
                    @Override
                    public void onWeatherRetrieved(WeatherHourForecast weatherHourForecast) {
                        if (weatherHourForecast != null) {
                            HourForecastAdapter adapter = new HourForecastAdapter(context, layoutID,
                                    weatherHourForecast);
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

    public void getForecastWeather(final Context context, String cityID, final ListView listView,
                                   final int layoutID) {
        weatherClient.getForecastWeather(cityID, new WeatherClient.ForecastWeatherEventListener() {
            @Override
            public void onWeatherRetrieved(WeatherForecast weatherForecast) {
                if (weatherForecast != null && weatherForecast.getForecast().size() > 0) {
                    DayForecastAdapter adapter = new DayForecastAdapter(context, layoutID, weatherForecast);
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

    public void searchCityByLocation(final Context context, final ListView listView,
                                     final Criteria criteria, final int layoutID) {
        try {
            weatherClient.searchCityByLocation(WeatherClient.createDefaultCriteria(), new WeatherClient.CityEventListener() {
                //            weatherClient.searchCityByLocation(criteria, new WeatherClient.CityEventListener() {
                @Override
                public void onCityListRetrieved(List<City> cities) {
                    if (criteria != null) {
                        CityAdapter cityAdapter = new CityAdapter(context, layoutID, cities);
                        listView.setAdapter(cityAdapter);
                    }
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

    public void searchCity(CharSequence charSequence, final Context context, final ListView listView,
                           final int layoutID) {
        weatherClient.searchCity(charSequence.toString(), new WeatherClient.CityEventListener() {
            @Override
            public void onCityListRetrieved(List<City> cities) {
                CityAdapter cityAdapter = new CityAdapter(context, layoutID, cities);
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

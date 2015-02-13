package com.example.ssereda.tinyweather.fragments;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.location.Criteria;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.ssereda.tinyweather.MainActivity;
import com.example.ssereda.tinyweather.R;
import com.example.ssereda.tinyweather.adapters.CityAdapter;
import com.example.ssereda.tinyweather.utils.DBHelper;
import com.example.ssereda.tinyweather.utils.Utils;
import com.survivingwithandroid.weather.lib.WeatherClient;
import com.survivingwithandroid.weather.lib.exception.LocationProviderNotFoundException;
import com.survivingwithandroid.weather.lib.exception.WeatherLibException;
import com.survivingwithandroid.weather.lib.model.City;

import java.util.List;

public class AddCityFragment extends Fragment {
    TextView TVcityHeader;
    EditText ETenterCity;
    ListView LVcityList;
    LinearLayout linearLayoutTrackingOnOff;
    ImageView imageViewTrackingOnOff;
    boolean isTracking = true;
    private Criteria criteria;
    private Cursor cursor;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_city_add, null);

        TVcityHeader = (TextView) view.findViewById(R.id.tv_city_header);
        ETenterCity = (EditText) view.findViewById(R.id.et_enter_city);
        LVcityList = (ListView) view.findViewById(R.id.lv_city_list);
        linearLayoutTrackingOnOff = (LinearLayout) view.findViewById(R.id.linear_layout_tracking_on_off);
        imageViewTrackingOnOff = (ImageView) view.findViewById(R.id.image_view_tracking_on_off);
        LVcityList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                City city = (City) LVcityList.getAdapter().getItem(position);

                if (MainActivity.db != null && MainActivity.db.isOpen()) {
                    ContentValues contentValues = new ContentValues();
                    if (city.getId() != null) {
                        contentValues.put(DBHelper.PLACES_ID, city.getId());
                    }
                    if (city.getCountry() != null) {
                        contentValues.put(DBHelper.PLACES_COUNTRY, city.getCountry());
                    }
                    if (city.getRegion() != null) {
                        contentValues.put(DBHelper.PLACES_REGION, city.getRegion());
                    }
                    if (city.getName() != null) {
                        contentValues.put(DBHelper.PLACES_NAME, city.getName());
                    }
                    MainActivity.db.insert(DBHelper.TABLE_PLACES, null, contentValues);
                }

                Utils.createNavigationDrawerAdapter(getActivity());
            }
        });

        if (isTracking) {
            if (criteria == null) {
                criteria = new Criteria();
                criteria.setPowerRequirement(Criteria.POWER_LOW);
                criteria.setAccuracy(Criteria.ACCURACY_COARSE);
                criteria.setCostAllowed(false);
            }

            searchCityByLocation(getActivity(), LVcityList, criteria);
        }

        linearLayoutTrackingOnOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isTracking) {
                    imageViewTrackingOnOff.setBackgroundDrawable(getActivity().getResources().getDrawable(R.drawable.image_view_pin_off));

                    isTracking = false;

                    searchCityByLocation(getActivity(), LVcityList, null);
                } else {
                    imageViewTrackingOnOff.setBackgroundDrawable(getActivity().getResources().getDrawable(R.drawable.image_view_pin_on));

                    isTracking = true;

                    ETenterCity.setText("");
                    if (criteria == null) {
                        criteria = new Criteria();
                        criteria.setPowerRequirement(Criteria.POWER_LOW);
                        criteria.setAccuracy(Criteria.ACCURACY_COARSE);
                        criteria.setCostAllowed(false);
                    }

                    searchCityByLocation(getActivity(), LVcityList, criteria);
                }
            }
        });

        ETenterCity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                if (count > 3) {
                    searchCity(charSequence, getActivity(), LVcityList);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        return view;
    }

    public static void searchCityByLocation(final Context context, final ListView listView, final Criteria criteria) {
        try {
            MainActivity.weatherClient.searchCityByLocation(WeatherClient.createDefaultCriteria(), new WeatherClient.CityEventListener() {
                //            MainActivity.weatherClient.searchCityByLocation(criteria, new WeatherClient.CityEventListener() {
                @Override
                public void onCityListRetrieved(List<City> cities) {
                    if (criteria != null) {
                        CityAdapter cityAdapter = new CityAdapter(context, R.layout.item_city_list, cities);
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
}

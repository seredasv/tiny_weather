package com.example.ssereda.tinyweather.fragments;

import android.content.ContentValues;
import android.database.Cursor;
import android.location.Criteria;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.example.ssereda.tinyweather.adapters.NavigationDrawerAdapter;
import com.example.ssereda.tinyweather.utils.DBHelper;
import com.example.ssereda.tinyweather.utils.Utils;
import com.survivingwithandroid.weather.lib.model.City;

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

                createNavigationDrawerAdapter();
            }
        });

        if (isTracking) {
            if (criteria == null) {
                criteria = new Criteria();
                criteria.setPowerRequirement(Criteria.POWER_LOW);
                criteria.setAccuracy(Criteria.ACCURACY_COARSE);
                criteria.setCostAllowed(false);
            }

            Utils.searchCityByLocation(getActivity(), LVcityList, criteria);
        }

        linearLayoutTrackingOnOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isTracking) {
                    imageViewTrackingOnOff.setBackgroundDrawable(getActivity().getResources().getDrawable(R.drawable.image_view_pin_off));

                    isTracking = false;

                    Utils.searchCityByLocation(getActivity(), LVcityList, null);
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

                    Utils.searchCityByLocation(getActivity(), LVcityList, criteria);
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
                    Utils.searchCity(charSequence, getActivity(), LVcityList);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        return view;
    }

    private void createNavigationDrawerAdapter() {
        if (MainActivity.db != null && MainActivity.db.isOpen()) {
            String[] columns = new String[]{DBHelper.ID, DBHelper.PLACES_ID, DBHelper.PLACES_COUNTRY,
                    DBHelper.PLACES_REGION, DBHelper.PLACES_NAME};
            cursor = MainActivity.db.query(DBHelper.TABLE_PLACES, columns, null, null, null, null, null);
        }

        String[] from = new String[]{
                DBHelper.PLACES_NAME
        };
        int[] to = new int[]{
                R.id.label
        };

        if (MainActivity.adapter != null) {
            MainActivity.adapter = null;
        }
        MainActivity.adapter = new NavigationDrawerAdapter(getActivity(), R.layout.drawer_list_item, cursor, from, to, 0);
        MainActivity.drawerListView.setAdapter(MainActivity.adapter);
        MainActivity.adapter.changeCursor(cursor);
    }
}

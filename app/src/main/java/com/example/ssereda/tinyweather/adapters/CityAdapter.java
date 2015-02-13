package com.example.ssereda.tinyweather.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.ssereda.tinyweather.R;
import com.example.ssereda.tinyweather.utils.ViewHolder;
import com.survivingwithandroid.weather.lib.model.City;

import java.util.List;

public class CityAdapter extends ArrayAdapter<City> {
    private Context context;
    private List<City> cities;

    public CityAdapter(Context context, int resource, List<City> cities) {
        super(context, resource, cities);
        this.context = context;
        this.cities = cities;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        ViewHolder holder;

        if (view == null) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            view = inflater.inflate(R.layout.item_city_list, parent, false);
            holder = new ViewHolder();

            holder.TVitemCityList = (TextView) view.findViewById(R.id.tv_item_city_list);
            if (cities.get(position).getId() != null && cities.get(position).getCountry() != null
                    && cities.get(position).getName() != null) {
                holder.TVitemCityList.setText("id: " + cities.get(position).getId()
                        + ", country: " + cities.get(position).getCountry()
                        + ", name: " + cities.get(position).getName());
            }

            if (!(cities != null || cities.size() > 0)) {
                holder.TVitemCityList.setText("city not exist");
            }

            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        return view;
    }
}

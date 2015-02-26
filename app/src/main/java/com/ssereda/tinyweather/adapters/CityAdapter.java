package com.ssereda.tinyweather.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.ssereda.tinyweather.R;
import com.survivingwithandroid.weather.lib.model.City;

import java.util.List;

public class CityAdapter extends ArrayAdapter<City> {
    private List<City> cities;

    public CityAdapter(Context context, int resource, List<City> cities) {
        super(context, resource, cities);
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

            holder.tvItemCityList= (TextView) view.findViewById(R.id.tv_item_city_list);

            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        if (cities.get(position).getName() != null) {
            holder.tvItemCityList.setText(cities.get(position).getName());
        }

        if (!(cities != null || cities.size() > 0)) {
            holder.tvItemCityList.setText(getContext().getResources().getString(R.string.add_city_not_exist));
        }

        return view;
    }

    public class ViewHolder {
        public TextView tvItemCityList;
    }
}

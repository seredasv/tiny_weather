package com.example.ssereda.tinyweather.adapters;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.SimpleCursorAdapter;

import com.example.ssereda.tinyweather.MainActivity;
import com.example.ssereda.tinyweather.R;
import com.example.ssereda.tinyweather.fragments.WeatherFragment;
import com.example.ssereda.tinyweather.utils.DBHelper;
import com.example.ssereda.tinyweather.utils.Utils;


public class NavigationDrawerAdapter extends SimpleCursorAdapter {
    public static final String WEATHER_FRAGMENT = "weather_fragment";
    Context context;
    Cursor cursor;

    public NavigationDrawerAdapter(Context context, int layout, Cursor cursor, String[] from, int[] to, int flags) {
        super(context, layout, cursor, from, to, flags);
        this.context = context;
        this.cursor = cursor;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = super.getView(position, convertView, parent);
        LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.linear_layout_item_layout);
        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.drawerListView.setItemChecked(position, true);
                MainActivity.drawerListView.setSelection(position);

                Fragment fragment = new WeatherFragment();

                if (cursor.moveToPosition(position)) {
                    Bundle bundle = new Bundle();
                    bundle.putInt(DBHelper.ID, cursor.getInt(cursor.getColumnIndex(DBHelper.ID)));
                    bundle.putString(DBHelper.PLACES_ID, cursor.getString(cursor.getColumnIndex(DBHelper.PLACES_ID)));
                    bundle.putString(DBHelper.PLACES_COUNTRY, cursor.getString(cursor.getColumnIndex(DBHelper.PLACES_COUNTRY)));
                    bundle.putString(DBHelper.PLACES_REGION, cursor.getString(cursor.getColumnIndex(DBHelper.PLACES_REGION)));
                    bundle.putString(DBHelper.PLACES_NAME, cursor.getString(cursor.getColumnIndex(DBHelper.PLACES_NAME)));
                    fragment.setArguments(bundle);

//                    if (MainActivity.sharedPreferences != null) {
//                        SharedPreferences.Editor editor = MainActivity.sharedPreferences.edit();
//                        editor.putString(DBHelper.ID, cursor.getString(wardrobeID));
//                        editor.apply();
                }

                FragmentTransaction transaction = ((MainActivity) context).getSupportFragmentManager().beginTransaction();
                transaction.addToBackStack(WEATHER_FRAGMENT);
                if (fragment.isAdded()) {
                    transaction.show(fragment);
                    transaction.commit();
                } else {
                    transaction.replace(R.id.container, fragment, WEATHER_FRAGMENT);
                    transaction.commit();
                }

                if (MainActivity.drawerLayout != null) {
                    MainActivity.drawerLayout.closeDrawer(Gravity.START);
                }
            }
        });
        LinearLayout linearLayoutDeletePlace = (LinearLayout) view.findViewById(R.id.linear_layout_delete_place);
        linearLayoutDeletePlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cursor.moveToPosition(position)) {
                    String id = cursor.getString(cursor.getColumnIndex(DBHelper.ID));
                    MainActivity.db.delete(DBHelper.TABLE_PLACES, DBHelper.ID + " = ?",
                            new String[]{id});

                    Utils.createNavigationDrawerAdapter(context);
                }
            }
        });

        return view;
    }
}
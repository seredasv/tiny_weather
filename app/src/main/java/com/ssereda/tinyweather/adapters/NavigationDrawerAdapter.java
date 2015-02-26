package com.ssereda.tinyweather.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.ssereda.tinyweather.MainActivity;
import com.ssereda.tinyweather.R;
import com.ssereda.tinyweather.fragments.WeatherFragment;
import com.ssereda.tinyweather.utils.DBHelper;

public class NavigationDrawerAdapter extends SimpleCursorAdapter {
    private static final String WEATHER_FRAGMENT = "weather_fragment";
    private Context context;
    private Cursor cursor;
    private SQLiteDatabase db;
    private ListView listView;
    private DrawerLayout drawerLayout;
    private SharedPreferences sharedPreferences;

    public NavigationDrawerAdapter(Context context, int layout, Cursor cursor, String[] from,
                                   int[] to, int flags, SharedPreferences sharedPreferences) {
        super(context, layout, cursor, from, to, flags);
        this.context = context;
        this.cursor = cursor;
        this.sharedPreferences = sharedPreferences;

        db = DBHelper.getInstance(context).getWritableDatabase();
        listView = (ListView) ((MainActivity) context).findViewById(R.id.left_drawer);
        drawerLayout = (DrawerLayout) ((MainActivity) context).findViewById(R.id.drawer_layout);
    }

    @Override
    public void changeCursor(Cursor cursor) {
        super.changeCursor(cursor);
        this.cursor = cursor;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = super.getView(position, convertView, parent);
        LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.linear_layout_item_layout);
        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listView.setItemChecked(position, true);
                listView.setSelection(position);

                Fragment fragment = new WeatherFragment();

                if (cursor.moveToPosition(position)) {
                    Bundle bundle = new Bundle();
                    bundle.putInt(DBHelper.ID, cursor.getInt(cursor.getColumnIndex(DBHelper.ID)));
                    bundle.putString(DBHelper.PLACES_ID, cursor.getString(cursor.getColumnIndex(DBHelper.PLACES_ID)));
                    bundle.putString(DBHelper.PLACES_NAME, cursor.getString(cursor.getColumnIndex(DBHelper.PLACES_NAME)));
                    fragment.setArguments(bundle);

                    if (sharedPreferences != null) {
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString(DBHelper.ID, cursor.getString(cursor.getColumnIndex(DBHelper.ID)));
                        editor.apply();
                    }
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

                if (drawerLayout != null) {
                    drawerLayout.closeDrawer(Gravity.START);
                }
            }
        });
        LinearLayout linearLayoutDeletePlace = (LinearLayout) view.findViewById(R.id.linear_layout_delete_place);
        linearLayoutDeletePlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cursor.moveToPosition(position)) {
                    String id = cursor.getString(cursor.getColumnIndex(DBHelper.ID));
                    db.delete(DBHelper.TABLE_PLACES, DBHelper.ID + " = ?", new String[]{id});
                    updateDrawer();
                }
            }
        });

        return view;
    }

    private void updateDrawer() {
        if (db != null && db.isOpen()) {
            String[] columns = new String[]{DBHelper.ID, DBHelper.PLACES_ID, DBHelper.PLACES_NAME};
            Cursor cursor = db.query(DBHelper.TABLE_PLACES, columns, null, null, null, null, null);
            ((NavigationDrawerAdapter) listView.getAdapter()).changeCursor(cursor);
        }
    }
}
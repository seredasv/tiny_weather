package com.example.ssereda.tinyweather;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.ViewDragHelper;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.example.ssereda.tinyweather.adapters.NavigationDrawerAdapter;
import com.example.ssereda.tinyweather.fragments.AddCityFragment;
import com.example.ssereda.tinyweather.fragments.DayWeatherFragment;
import com.example.ssereda.tinyweather.fragments.HourWeatherFragment;
import com.example.ssereda.tinyweather.fragments.WeatherFragment;
import com.example.ssereda.tinyweather.utils.DBHelper;

import java.lang.reflect.Field;

public class MainActivity extends ActionBarActivity {
    int backStack = 1;
    private long back_pressed;
    private String ADD_CITY_FRAGMENT = "add_city_fragment";
    private String HOUR_WEATHER_FORECAST_FRAGMENT = "hour_weather_forecast_fragment";
    private String DAY_WEATHER_FORECAST_FRAGMENT = "day_weather_forecast_fragment";
    private NavigationDrawerAdapter adapter;
    private DrawerLayout drawerLayout;
    private ListView drawerListView;
    private Toolbar toolbar;
    private SQLiteDatabase db;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private FrameLayout frameLayout;
    private float lastTranslate = 0.0f;
    private SharedPreferences sharedPreferences;
    private Cursor cursorAdapter, cursorWeather;

    //TODO change icons size (cancel, update view, etc..)

    public void createNavigationDrawerAdapter(Context context) {
        if (db != null && db.isOpen()) {
            String[] columns = new String[]{DBHelper.ID, DBHelper.PLACES_ID, DBHelper.PLACES_NAME};
            cursorAdapter = db.query(DBHelper.TABLE_PLACES, columns, null, null, null, null, null);
        }

        String[] from = new String[]{
                DBHelper.PLACES_NAME
        };
        int[] to = new int[]{
                R.id.label
        };

        if (adapter != null) {
            adapter = null;
        }
        if (cursorAdapter != null) {
            adapter = new NavigationDrawerAdapter(context, R.layout.drawer_list_item, cursorAdapter,
                    from, to, 0, sharedPreferences);
            drawerListView.setAdapter(adapter);
            adapter.changeCursor(cursorAdapter);
        }
    }

    @Override
    public void onBackPressed() {
        int count = getSupportFragmentManager().getBackStackEntryCount();

        if (drawerLayout.isDrawerOpen(Gravity.START)) {
            drawerLayout.closeDrawer(Gravity.START);
        } else {
            if (count == backStack) {
                if (back_pressed + 2000 > System.currentTimeMillis()) {
                    Intent startMain = new Intent(Intent.ACTION_MAIN);
                    startMain.addCategory(Intent.CATEGORY_HOME);
                    startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(startMain);
                } else {
                    Toast.makeText(getBaseContext(), getResources().getString(R.string.toast_exit),
                            Toast.LENGTH_SHORT).show();
                }
                back_pressed = System.currentTimeMillis();
            } else {
                getSupportFragmentManager().popBackStack();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        db = DBHelper.getInstance(this).getWritableDatabase();

        sharedPreferences = getSharedPreferences("last_place_id", MODE_PRIVATE);

        toolbar = (Toolbar) findViewById(R.id.material_toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    Fragment fragment;
                    FragmentTransaction transaction;
                    switch (menuItem.getItemId()) {
                        case R.id.action_add_city:
                            fragment = new AddCityFragment();
                            transaction = getSupportFragmentManager().beginTransaction();
                            transaction.addToBackStack(ADD_CITY_FRAGMENT);
                            if (fragment.isAdded()) {
                                transaction.show(fragment);
                                transaction.commit();
                            } else {
                                transaction.replace(R.id.container, fragment, ADD_CITY_FRAGMENT);
                                transaction.commit();
                            }
                            break;
                        case R.id.action_day_forecast:
                            fragment = new DayWeatherFragment();
                            transaction = getSupportFragmentManager().beginTransaction();
                            transaction.addToBackStack(DAY_WEATHER_FORECAST_FRAGMENT);
                            if (fragment.isAdded()) {
                                transaction.show(fragment);
                                transaction.commit();
                            } else {
                                transaction.replace(R.id.container, fragment, DAY_WEATHER_FORECAST_FRAGMENT);
                                transaction.commit();
                            }
                            break;
                        case R.id.action_hour_forecast:
                            fragment = new HourWeatherFragment();
                            transaction = getSupportFragmentManager().beginTransaction();
                            transaction.addToBackStack(HOUR_WEATHER_FORECAST_FRAGMENT);
                            if (fragment.isAdded()) {
                                transaction.show(fragment);
                                transaction.commit();
                            } else {
                                transaction.replace(R.id.container, fragment, HOUR_WEATHER_FORECAST_FRAGMENT);
                                transaction.commit();
                            }
                            break;
                        default:
                            break;
                    }
                    return false;
                }
            });
        }

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerListView = (ListView) findViewById(R.id.left_drawer);
        frameLayout = (FrameLayout) findViewById(R.id.container);
        LinearLayout linearLayoutAddANewCity = (LinearLayout) findViewById(R.id.ll_add_a_new_city);
        linearLayoutAddANewCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new AddCityFragment();
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.addToBackStack(ADD_CITY_FRAGMENT);
                if (fragment.isAdded()) {
                    transaction.show(fragment);
                    transaction.commit();
                } else {
                    transaction.replace(R.id.container, fragment, ADD_CITY_FRAGMENT);
                    transaction.commit();
                }
                drawerLayout.closeDrawer(Gravity.START);
            }
        });

        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.app_name, R.string.app_name) {
            public void onDrawerClosed(View view) {
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);

                // slide total container if drawer is open
                float moveFactor = (drawerListView.getWidth() * slideOffset);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    frameLayout.setTranslationX(moveFactor);
                } else {
                    TranslateAnimation anim = new TranslateAnimation(lastTranslate, moveFactor, 0.0f, 0.0f);
                    anim.setDuration(0);
                    anim.setFillAfter(true);
                    frameLayout.startAnimation(anim);
                    lastTranslate = moveFactor;
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    toolbar.setTranslationX(moveFactor);
                } else {
                    TranslateAnimation anim = new TranslateAnimation(lastTranslate, moveFactor, 0.0f, 0.0f);
                    anim.setDuration(0);
                    anim.setFillAfter(true);
                    toolbar.startAnimation(anim);
                    lastTranslate = moveFactor;
                }
            }

            public void onDrawerOpened(View drawerView) {
            }
        };
        drawerLayout.setDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.setDrawerIndicatorEnabled(true);

        changeOpenDrawerTouchArea();
    }

    public void changeOpenDrawerTouchArea() {
        Field mDragger = null;
        try {
            mDragger = drawerLayout.getClass().getDeclaredField("mLeftDragger");
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        if (mDragger != null) {
            mDragger.setAccessible(true);
        }
        ViewDragHelper draggerObj = null;
        try {
            if (mDragger != null) {
                draggerObj = (ViewDragHelper) mDragger.get(drawerLayout);
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        Field mEdgeSize = null;
        try {
            if (draggerObj != null) {
                mEdgeSize = draggerObj.getClass().getDeclaredField("mEdgeSize");
            }
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        if (mEdgeSize != null) {
            mEdgeSize.setAccessible(true);
        }
        int edge = 0;
        try {
            if (mEdgeSize != null) {
                edge = mEdgeSize.getInt(draggerObj);
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        try {
            if (mEdgeSize != null) {
                mEdgeSize.setInt(draggerObj, edge * 5);
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        actionBarDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        actionBarDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onResume() {
        super.onResume();

        createNavigationDrawerAdapter(this);

        String lastPlaceID = sharedPreferences.getString(DBHelper.ID, "");
        if (lastPlaceID != null && lastPlaceID.length() > 0) {
            String[] columns = new String[]{DBHelper.ID, DBHelper.PLACES_ID, DBHelper.PLACES_NAME};
            if (db != null && db.isOpen()) {
                cursorWeather = db.query(DBHelper.TABLE_PLACES, columns, DBHelper.ID + " = ? ",
                        new String[]{lastPlaceID}, null, null, null);
                if (cursorWeather != null) {
                    if (cursorWeather.moveToFirst()) {
                        Fragment fragment = new WeatherFragment();
                        String placeID = cursorWeather.getString(cursorWeather.getColumnIndex(DBHelper.PLACES_ID));
                        String placeName = cursorWeather.getString(cursorWeather.getColumnIndex(DBHelper.PLACES_NAME));

                        toolbar.setTitle(placeName);

                        Bundle bundle = new Bundle();
                        bundle.putString(DBHelper.PLACES_ID, placeID);
                        bundle.putString(DBHelper.PLACES_NAME, placeName);
                        fragment.setArguments(bundle);

                        if (sharedPreferences != null) {
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString(DBHelper.ID, cursorWeather.getString(cursorWeather.getColumnIndex(DBHelper.ID)));
                            editor.apply();
                        }

                        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                        String WEATHER_FRAGMENT = "weather_fragment";
                        transaction.addToBackStack(WEATHER_FRAGMENT);
                        if (fragment.isAdded()) {
                            transaction.show(fragment);
                            transaction.commit();
                        } else {
                            transaction.replace(R.id.container, fragment, WEATHER_FRAGMENT);
                            transaction.commit();
                        }
                    }
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (cursorAdapter != null) {
            cursorAdapter.close();
        }
        if (cursorWeather != null) {
            cursorWeather.close();
        }
    }
}
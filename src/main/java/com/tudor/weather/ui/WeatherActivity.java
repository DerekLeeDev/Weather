package com.tudor.weather.ui;

import android.os.Bundle;

import com.tudor.weather.R;
import com.tudor.weather.base.BaseActivity;

public class WeatherActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
    }
}

package com.chainsaw.clearweather;

import java.util.EventListener;

import org.json.JSONObject;

public interface WeatherDataListener extends EventListener {
	void onDataReady(JSONObject weatherData);
}

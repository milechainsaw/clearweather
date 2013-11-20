package com.chainsaw.clearweather;

import java.util.EventListener;

public interface WeatherDataListener extends EventListener {
	void onDataReady(WeatherData weatherData);
}

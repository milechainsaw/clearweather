package com.chainsaw.clearweather;

import java.net.URL;
import java.net.URLConnection;

import org.json.JSONObject;

import android.location.Location;

public class OpenWeatherAPI {

	String serviceURL = "http://api.openweathermap.org/data/2.5/weather?";
	String API_KEY = "&APPID=3602ce9ed1ea7a9a94a18594b0893d05";
	URL fetchRequest;
	URLConnection serviceConnection;
	JSONObject weatherData;
	BackgroundFetch asyncLoader;
	WeatherDataListener listener = null;

	public OpenWeatherAPI() {
		asyncLoader = new BackgroundFetch() {
			@Override
			protected void onPostExecute(JSONObject result) {
				if (OpenWeatherAPI.this.listener != null)
					OpenWeatherAPI.this.listener.onDataReady(result);
			}
		};
	}

	void getData(Location location) {
		asyncLoader.execute(serviceURL + "lat="
				+ String.valueOf(location.getLatitude()) + "&" + "lon="
				+ String.valueOf(location.getLongitude()) + API_KEY);
	}

	void setListener(WeatherDataListener li) {
		this.listener = li;
	}

}

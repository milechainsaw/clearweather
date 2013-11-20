package com.chainsaw.clearweather;

import java.net.URL;
import java.net.URLConnection;

import org.json.JSONObject;

import android.content.Context;
import android.location.Location;
import android.view.View;
import android.widget.RemoteViews;

public class OpenWeatherAPI {

	String serviceURL = "http://api.openweathermap.org/data/2.5/weather?";
	String API_KEY = "&APPID=3602ce9ed1ea7a9a94a18594b0893d05";
	URL fetchRequest;
	URLConnection serviceConnection;
	JSONObject weatherData;
	BackgroundFetch asyncLoader;
	WeatherDataListener listener = null;

	public OpenWeatherAPI(final Context context) {
		final RemoteViews remote = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
	
		asyncLoader = new BackgroundFetch(context) {
			@Override
			protected void onPostExecute(WeatherData result) {
				remote.setViewVisibility(R.id.loading, View.INVISIBLE);
				if (OpenWeatherAPI.this.listener != null)
					OpenWeatherAPI.this.listener.onDataReady(result);
				
			}
			
			@Override
			protected void onProgressUpdate(Integer... values) {
				super.onProgressUpdate(values);
				remote.setViewVisibility(R.id.loading, View.VISIBLE);
				remote.setProgressBar(R.id.loading, 10, 5, true);
				ClearWeatherWidget.updateAll(context);
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

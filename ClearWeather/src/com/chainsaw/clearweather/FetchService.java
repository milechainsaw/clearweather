package com.chainsaw.clearweather;

import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

public class FetchService extends Service {

	@Override
	public int onStartCommand(Intent i, int flags, final int startId) {
		Log.i("FetchService", "Fetch started");
		final FetchService context = this;
		final Intent intent = i;
		final int widgetId;

		Bundle extras = intent.getExtras();
		if (extras != null)
			widgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID,
					AppWidgetManager.INVALID_APPWIDGET_ID);
		else
			widgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
		LocationData loc = new LocationData(context);
		Location location = loc.getData();

		WeatherDataListener listener = new WeatherDataListener() {
			@Override
			public void onDataReady(WeatherData weatherData) {

				RemoteViews remote = new RemoteViews(context.getPackageName(),
						R.layout.widget_layout);
				String temperature = "18°";
				String humidity = "55%";
				String name = "Here";
				String weatherType = "";

				AppWidgetManager appWidgetManager = AppWidgetManager
						.getInstance(context);

				SharedPreferences perfs = FetchService.this
						.getSharedPreferences("com.chainsaw.clearweather"
								+ String.valueOf(widgetId), MODE_PRIVATE);
				if (perfs.getBoolean("isCelsius", true)) {
					temperature = String.valueOf(weatherData.getTempC()) + "°C";
				} else {
					temperature = String.valueOf(weatherData.getTempF()) + "°F";
				}
				if (weatherData.getType() != null) {
					weatherType = weatherData.getType();
				}

				humidity = String.valueOf(weatherData.getHumidity()) + "%";
				name = weatherData.getCityName();

				if (weatherData.isDataAvailable()) {
					remote.setViewVisibility(R.id.temp, View.VISIBLE);
					remote.setViewVisibility(R.id.humidity, View.VISIBLE);
					remote.setViewVisibility(R.id.location, View.VISIBLE);
					remote.setViewVisibility(R.id.weather, View.VISIBLE);
					remote.setViewVisibility(R.id.loading, View.INVISIBLE);					
					
					remote.setTextViewText(R.id.temp, temperature);
					remote.setTextViewText(R.id.humidity, humidity);
					remote.setTextViewText(R.id.location, name);
					remote.setTextViewText(R.id.weather, weatherType);
				} else {
					remote.setViewVisibility(R.id.temp, View.INVISIBLE);
					remote.setViewVisibility(R.id.humidity, View.INVISIBLE);
					remote.setViewVisibility(R.id.location, View.INVISIBLE);
					remote.setViewVisibility(R.id.weather, View.VISIBLE);
					remote.setViewVisibility(R.id.loading, View.INVISIBLE);
					remote.setTextViewText(R.id.weather, weatherData.getType());
				}
				
								

				if (widgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {
					appWidgetManager.updateAppWidget(widgetId, remote);
				}
				stopSelf(startId);
			}
		};

		OpenWeatherAPI weatherData = new OpenWeatherAPI(
				this.getApplicationContext());
		weatherData.setListener(listener);
		weatherData.getData(location);

		return Service.START_NOT_STICKY;
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

}

package com.chainsaw.clearweather;

import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

public class FetchService extends Service {

	public static boolean running = false;
	public static WeatherData weatherData = null;

	@Override
	public int onStartCommand(Intent i, int flags, final int startId) {
		final Context context = this.getApplicationContext();
		final Intent intent = i;
		final int widgetId;

		Bundle extras = intent.getExtras();
		if (extras != null)
			widgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID,
					AppWidgetManager.INVALID_APPWIDGET_ID);
		else
			widgetId = AppWidgetManager.INVALID_APPWIDGET_ID;

		WeatherDataListener listener = new WeatherDataListener() {
			@Override
			public void onDataReady(WeatherData fetchedData) {
				FetchService.weatherData = fetchedData;
				fillData(context, widgetId);
				Log.i("FetchService", "returnData SENT");
				stopSelf(startId);
				FetchService.running = false;
			}
		};

		if (FetchService.weatherData == null) {
			runService(context, widgetId, extras, listener);

		} else {

			if (!FetchService.running
					&& (!FetchService.weatherData.isValid() || extras.getBoolean("force_update"))) {
				runService(context, widgetId, extras, listener);

			} else {
				FetchService.running = false;
				stopSelf(startId);
			}

			if (FetchService.weatherData.isValid()) {
				fillData(context, widgetId);
			}
		}

		return Service.START_NOT_STICKY;
	}

	private void runService(final Context context, final int widgetId, Bundle extras,
			WeatherDataListener listener) {
		FetchService.running = true;
		LocationData loc = new LocationData(context);
		Location location = loc.getData();
		OpenWeatherAPI weatherData = new OpenWeatherAPI(this.getApplicationContext(), widgetId);
		weatherData.setListener(listener);
		weatherData.getData(location);
		Log.i("FetchService", "Fetch started. Force=" + extras.getBoolean("force_update"));
	}

	private void fillData(Context context, int widgetId) {
		String temperature = "18°";
		String humidity = "55%";
		String name = "Here";
		String weatherType = "";

		SharedPreferences perfs = FetchService.this.getSharedPreferences(
				"com.chainsaw.clearweather" + String.valueOf(widgetId), MODE_PRIVATE);
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

		Intent returnData = new Intent();
		returnData.setAction(ClearWeatherWidget.WEATHER_DATA);
		returnData.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);
		if (weatherData.isDataAvailable()) {
			WeatherData.loadError = false;
			returnData.putExtra("temperature", temperature);
			returnData.putExtra("humidity", humidity);
			returnData.putExtra("location", name);
			returnData.putExtra("weather", weatherType);
		} else {
			WeatherData.loadError = true;
			returnData.putExtra("weather", weatherData.getType());
		}
		context.sendBroadcast(returnData);

	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

}

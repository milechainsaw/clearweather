package com.chainsaw.clearweather;

import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
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
		Intent returnData = new Intent();
		returnData.setAction(ClearWeatherWidget.WEATHER_DATA);
		returnData.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);
		if (weatherData.isDataAvailable()) {
			WeatherData.loadError = false;
		} else {
			WeatherData.loadError = true;
		}
		context.sendBroadcast(returnData);

	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

}

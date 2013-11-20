package com.chainsaw.clearweather;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;

public class FetchService extends Service {

	@Override
	public int onStartCommand(Intent i, int flags, final int startId) {
		final FetchService context = this;
		final Intent intent = i;
		final int widgetId;
		
		Bundle extras = intent.getExtras();
		if (extras != null)
			widgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID);
		else
			widgetId=0;
		Log.i("Service:widgetID", String.valueOf(widgetId));

		LocationData loc = new LocationData(context);
		Location location = loc.getData();

		WeatherDataListener listener = new WeatherDataListener() {
			@Override
			public void onDataReady(WeatherData weatherData) {
				int temperature = -10;
				int humidity = -10;
				String name = "null";
				
				AppWidgetManager appWidgetManager = AppWidgetManager
						.getInstance(context);

				RemoteViews remote = new RemoteViews(context.getPackageName(),
						R.layout.widget_layout);
				remote.setTextViewText(R.id.temp, String.valueOf(temperature)+"°C");
				remote.setTextViewText(R.id.humidity, String.valueOf(humidity)+"%");
				remote.setTextViewText(R.id.location, name);
				
				if (widgetId != 0) {
					appWidgetManager.updateAppWidget(widgetId, remote);
					// stopSelf(startId);
					Log.i("Service",
							"Should update..." + context.getPackageName()
									+ " temp:" + String.valueOf(temperature));
					
				}
			}
		};
		OpenWeatherAPI weatherData = new OpenWeatherAPI();
		weatherData.setListener(listener);
		weatherData.getData(location);

		return Service.START_NOT_STICKY;
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

}

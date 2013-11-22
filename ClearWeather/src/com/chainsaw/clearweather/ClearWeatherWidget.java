package com.chainsaw.clearweather;

import java.util.StringTokenizer;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.RemoteViews;

public class ClearWeatherWidget extends AppWidgetProvider {

	private static SparseBooleanArray DATA_WRITTEN = new SparseBooleanArray();

	public static final String WEATHER_DATA = "com.chainsaw.clearweather.WEATHER_DATA";

	@Override
	public void onReceive(Context context, Intent intent) {
		super.onReceive(context, intent);

		if (intent.getAction().equals(AppWidgetManager.ACTION_APPWIDGET_UPDATE)) {
			AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
			if (intent.hasExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS)) {
				this.onUpdate(context, appWidgetManager,
						intent.getIntArrayExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS));
			}
			if (intent.hasExtra(AppWidgetManager.EXTRA_APPWIDGET_ID)) {
				int[] ids = { intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
						AppWidgetManager.INVALID_APPWIDGET_ID) };
				this.onUpdate(context, appWidgetManager, ids);
			}
			appWidgetManager = null;
		}

		if (intent.getAction().equals(WEATHER_DATA)) {
			writeData(context, intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
					AppWidgetManager.INVALID_APPWIDGET_ID));
		}

	}

	private void writeData(Context context, int widgetId) {
		if (FetchService.weatherData != null) {
			RemoteViews remote = new RemoteViews(context.getPackageName(), R.layout.widget_layout);

			String temperature = "18°";
			String humidity = "55%";
			String name = "Here";
			String weatherType = "";

			SharedPreferences perfs = context.getSharedPreferences("com.chainsaw.clearweather"
					+ String.valueOf(widgetId), Context.MODE_PRIVATE);
			if (perfs.getBoolean("isCelsius", true)) {
				temperature = String.valueOf(FetchService.weatherData.getTempC()) + "°C";
			} else {
				temperature = String.valueOf(FetchService.weatherData.getTempF()) + "°F";
			}
			if (FetchService.weatherData.getType() != null) {
				weatherType = FetchService.weatherData.getType();
			}

			humidity = String.valueOf(FetchService.weatherData.getHumidity()) + "%";
			name = FetchService.weatherData.getCityName();

			remote.setTextViewText(R.id.temp, temperature);
			remote.setTextViewText(R.id.humidity, humidity);
			remote.setTextViewText(R.id.location, name);
			remote.setTextViewText(R.id.weather, weatherType);
			
			ClearWeatherWidget.DATA_WRITTEN.put(widgetId, true);

		//	AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
		//	appWidgetManager.updateAppWidget(widgetId, remote);
			updateWidget(context, widgetId);
		//	appWidgetManager = null;
		}
	}

	@Override
	public void onDeleted(Context context, int[] appWidgetIds) {
		SharedPreferences prefs = context.getSharedPreferences("com.chainsaw.clearweather",
				Context.MODE_PRIVATE);
		if (prefs.contains("WIDGET_IDS")) {
			String temp_ids = prefs.getString("WIDGET_IDS", null);
			StringTokenizer tokenizer = new StringTokenizer(temp_ids, ",");
			String newWidgetIds = "";
			
			while (tokenizer.hasMoreTokens()) {
				int savedId = Integer.parseInt(tokenizer.nextToken());
				for (int currentId : appWidgetIds) {
					if (savedId != currentId) {
						newWidgetIds = newWidgetIds.concat(String.valueOf(savedId) + ",");
					}
					if (savedId == currentId) {
						ClearWeatherWidget.DATA_WRITTEN.delete(savedId);
					}

				}
			}
			Log.i("newIds:"+newWidgetIds,"j");
			prefs.edit().putString("WIDGET_IDS", newWidgetIds).apply();
		}
		super.onDeleted(context, appWidgetIds);
	}

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		super.onUpdate(context, appWidgetManager, appWidgetIds);

		SharedPreferences prefs = context.getSharedPreferences("com.chainsaw.clearweather",
				Context.MODE_PRIVATE);
		if (prefs.contains("WIDGET_IDS")) {
			String ids = prefs.getString("WIDGET_IDS", null);
			StringTokenizer tokenizer = new StringTokenizer(ids, ",");

			for (int i = 0; i < appWidgetIds.length; i++) {
				int widgetId = appWidgetIds[i];
				while (tokenizer.hasMoreTokens()) {
					int savedId = Integer.parseInt(tokenizer.nextToken());
					if (widgetId == savedId) {
						RemoteViews remote = new RemoteViews(context.getPackageName(),
								R.layout.widget_layout);

						Intent forceFetch = new Intent(context, FetchService.class);
						forceFetch.setAction("fetch" + System.currentTimeMillis());
						forceFetch.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);
						forceFetch.putExtra("force_update", true);
						PendingIntent pendingIntent = PendingIntent.getService(context, 0,
								forceFetch, 0);
						remote.setOnClickPendingIntent(R.id.widget, pendingIntent);

						if (FetchService.weatherData == null || !FetchService.weatherData.isValid()) {
							Intent regularFetch = new Intent(context, FetchService.class);
							regularFetch.setAction("fetch" + System.currentTimeMillis());
							regularFetch.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);
							context.startService(regularFetch);
						} else {
							if (!ClearWeatherWidget.DATA_WRITTEN.get(widgetId)) {
								writeData(context, widgetId);
							}
							if (ClearWeatherWidget.DATA_WRITTEN.get(widgetId)) {
								weatherDisplay(context, remote, !WeatherData.loadError, widgetId);
							}
							//appWidgetManager.updateAppWidget(widgetId, remote);
						}
						
					}
				}

			}
		}

	}

	private void weatherDisplay(Context context, RemoteViews remote, boolean valid, int widgetId) {
		if (valid) {
			remote.setViewVisibility(R.id.temp, View.VISIBLE);
			remote.setViewVisibility(R.id.humidity, View.VISIBLE);
			remote.setViewVisibility(R.id.location, View.VISIBLE);
			remote.setViewVisibility(R.id.weather, View.VISIBLE);
			remote.setViewVisibility(R.id.loading, View.INVISIBLE);
		} else {
			remote.setViewVisibility(R.id.temp, View.INVISIBLE);
			remote.setViewVisibility(R.id.humidity, View.INVISIBLE);
			remote.setViewVisibility(R.id.location, View.INVISIBLE);
			remote.setViewVisibility(R.id.weather, View.VISIBLE);
			remote.setViewVisibility(R.id.loading, View.INVISIBLE);
		}
	}

	public static void updateWidget(Context context, int appWidgetId) {
		Intent intent = new Intent(context, ClearWeatherWidget.class);
		intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
		intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
		context.sendBroadcast(intent);

		AppWidgetManager manager = AppWidgetManager.getInstance(context);
		RemoteViews remote = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
		manager.updateAppWidget(appWidgetId, remote);

	}

	public static void updateAll(Context context) {
		AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
		ComponentName compName = new ComponentName(context, ClearWeatherWidget.class);
		Intent intent = new Intent(context, ClearWeatherWidget.class);
		intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
		intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS,
				appWidgetManager.getAppWidgetIds(compName));
		context.sendBroadcast(intent);
		appWidgetManager = null;
		compName = null;
	}

}

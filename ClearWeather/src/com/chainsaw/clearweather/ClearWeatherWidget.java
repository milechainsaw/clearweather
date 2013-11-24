package com.chainsaw.clearweather;

import java.util.StringTokenizer;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.RemoteViews;

public class ClearWeatherWidget extends AppWidgetProvider {

	private static PendingIntent alarm = null;
	// BAD IDEA!!!
	private static boolean UPDATE_IN_PROGRESS = false;
	//

	public static final String WEATHER_DATA = "com.chainsaw.clearweather.WEATHER_DATA";

	@Override
	public void onReceive(Context context, Intent intent) {
		super.onReceive(context, intent);
		AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
		if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
			setAlarm(context);
		}

		int[] ids = { AppWidgetManager.INVALID_APPWIDGET_ID };

		if (intent.getAction().equals(AppWidgetManager.ACTION_APPWIDGET_UPDATE)) {
			if (intent.hasExtra(AppWidgetManager.EXTRA_APPWIDGET_ID)) {
				ids[0] = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
						AppWidgetManager.INVALID_APPWIDGET_ID);
			}

			if (intent.hasExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS)) {
				ids = intent.getIntArrayExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS);
			}

		}

		if (intent.getAction().equals(WEATHER_DATA)) {
			RemoteViews remote = writeData(context, intent.getIntExtra(
					AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID),
					!WeatherData.loadError);
			appWidgetManager.updateAppWidget(intent.getIntExtra(
					AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID),
					remote);

		}

		this.onUpdate(context, appWidgetManager, ids);

	}

	private RemoteViews writeData(Context context, int widgetId, boolean valid) {
		RemoteViews remote = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
		if (FetchService.weatherData != null) {
			String temperature = "18°";
			String humidity = "55%";
			String name = "Here";
			String weatherType = "";
			String timestamp = "checked at " + FetchService.weatherData.getTimestamp();

			SharedPreferences perfs = context.getSharedPreferences("com.chainsaw.clearweather"
					+ String.valueOf(widgetId), Context.MODE_PRIVATE);

			if (FetchService.weatherData.getType() != null) {
				weatherType = FetchService.weatherData.getType();
			}

			humidity = String.valueOf(FetchService.weatherData.getHumidity());
			name = FetchService.weatherData.getCityName();

			if (perfs.getBoolean("isCelsius", true)) {
				temperature = String.valueOf(FetchService.weatherData.getTempC());
				remote.setTextViewText(R.id.tempUnit, "°C");
			} else {
				temperature = String.valueOf(FetchService.weatherData.getTempF());
				remote.setTextViewText(R.id.tempUnit, "°F");

			}

			remote.setTextViewText(R.id.temp, temperature);
			remote.setTextViewText(R.id.humidity, humidity);
			remote.setTextViewText(R.id.humidityUnit, "%");
			remote.setTextViewText(R.id.location, name);
			remote.setTextViewText(R.id.weather, weatherType);
			remote.setTextViewText(R.id.timestamp, timestamp);

			// AppWidgetManager appWidgetManager =
			// AppWidgetManager.getInstance(context);
			// appWidgetManager.updateAppWidget(widgetId, remote);
			// updateWidget(context, widgetId);
			// appWidgetManager = null;

			if (valid) {
				remote.setViewVisibility(R.id.temp, View.VISIBLE);
				remote.setViewVisibility(R.id.humidity, View.VISIBLE);
				remote.setViewVisibility(R.id.tempUnit, View.VISIBLE);
				remote.setViewVisibility(R.id.humidityUnit, View.VISIBLE);
				remote.setViewVisibility(R.id.location, View.VISIBLE);
				remote.setViewVisibility(R.id.weather, View.VISIBLE);
				remote.setViewVisibility(R.id.timestamp, View.VISIBLE);
				remote.setViewVisibility(R.id.loading, View.INVISIBLE);
			} else {
				remote.setTextViewText(R.id.weather, "couldn't get the data, sorry!");
				
				remote.setViewVisibility(R.id.temp, View.INVISIBLE);
				remote.setViewVisibility(R.id.tempUnit, View.INVISIBLE);
				remote.setViewVisibility(R.id.humidity, View.INVISIBLE);
				remote.setViewVisibility(R.id.humidityUnit, View.INVISIBLE);
				remote.setViewVisibility(R.id.location, View.INVISIBLE);
				remote.setViewVisibility(R.id.weather, View.VISIBLE);
				remote.setViewVisibility(R.id.timestamp, View.INVISIBLE);
				remote.setViewVisibility(R.id.loading, View.INVISIBLE);
			}
		} else {
			remote.setViewVisibility(R.id.temp, View.INVISIBLE);
			remote.setViewVisibility(R.id.tempUnit, View.INVISIBLE);
			remote.setViewVisibility(R.id.humidity, View.INVISIBLE);
			remote.setViewVisibility(R.id.humidityUnit, View.INVISIBLE);
			remote.setViewVisibility(R.id.location, View.INVISIBLE);
			remote.setViewVisibility(R.id.weather, View.INVISIBLE);
			remote.setViewVisibility(R.id.timestamp, View.INVISIBLE);
			remote.setViewVisibility(R.id.loading, View.VISIBLE);

		}
		remote.setOnClickPendingIntent(R.id.widget, makeForceIntent(context, widgetId));

		return remote;
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

				}
			}
			prefs.edit().putString("WIDGET_IDS", newWidgetIds).apply();
		}
		super.onDeleted(context, appWidgetIds);
	}

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		super.onUpdate(context, appWidgetManager, appWidgetIds);
		if (!UPDATE_IN_PROGRESS) {
			SharedPreferences prefs = context.getSharedPreferences("com.chainsaw.clearweather",
					Context.MODE_PRIVATE);
			if (prefs.contains("WIDGET_IDS")) {
				String ids = prefs.getString("WIDGET_IDS", null);
				StringTokenizer tokenizer = null;

				int savedId;
				for (int widgetId : appWidgetIds) {
					tokenizer = new StringTokenizer(ids, ",");
					UPDATE_IN_PROGRESS = true;
					while (tokenizer.hasMoreTokens()) {
						savedId = Integer.parseInt(tokenizer.nextToken());

						if (widgetId == savedId) {

							if (FetchService.weatherData == null
									|| !FetchService.weatherData.isValid()) {
								Intent regularFetch = new Intent(context, FetchService.class);
								regularFetch.setAction("fetch" + System.currentTimeMillis());
								regularFetch
										.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);
								context.startService(regularFetch);
							} else {
								RemoteViews remote = writeData(context, widgetId,
										!WeatherData.loadError);
								appWidgetManager.updateAppWidget(widgetId, remote);

							}

						}
					}

				}
				UPDATE_IN_PROGRESS = false;
			}
		}

	}

	private PendingIntent makeForceIntent(Context context, int widgetId) {
		Intent forceFetch = new Intent(context, FetchService.class);
		forceFetch.setAction("fetch" + System.currentTimeMillis());
		forceFetch.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);
		forceFetch.putExtra("force_update", true);
		PendingIntent pendingIntent = PendingIntent.getService(context, 0, forceFetch, 0);
		return pendingIntent;
	}

	public static void updateWidget(Context context, int appWidgetId) {
		Intent intent = new Intent(context, ClearWeatherWidget.class);
		intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
		intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
		context.sendBroadcast(intent);
	}

	public static void updateAll(Context context) {
		AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
		ComponentName compName = new ComponentName(context, ClearWeatherWidget.class);
		Intent intent = new Intent(context, ClearWeatherWidget.class);
		intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
		intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS,
				appWidgetManager.getAppWidgetIds(compName));
		context.sendBroadcast(intent);
	}

	public static void setAlarm(Context context) {
		Intent callToAlarm = new Intent(context, AlarmReceiver.class);
		alarm = PendingIntent.getBroadcast(context, 0, callToAlarm,
				PendingIntent.FLAG_CANCEL_CURRENT);
		AlarmManager alarms = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		alarms.setInexactRepeating(AlarmManager.RTC, System.currentTimeMillis()
				+ AlarmManager.INTERVAL_HALF_HOUR, AlarmManager.INTERVAL_HOUR, alarm);
		
	}

	@Override
	public void onDisabled(Context context) {
		AlarmManager m = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		m.cancel(alarm);
		super.onDisabled(context);
	}

}

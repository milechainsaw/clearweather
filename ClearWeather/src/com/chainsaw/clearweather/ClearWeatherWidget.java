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
			writeData(context, intent);

		}

	}

	private void writeData(Context context, Intent intent) {
		RemoteViews remote = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
		remote.setTextViewText(R.id.temp, intent.getStringExtra("temperature"));
		remote.setTextViewText(R.id.humidity, intent.getStringExtra("humidity"));
		remote.setTextViewText(R.id.location, intent.getStringExtra("location"));
		remote.setTextViewText(R.id.weather, intent.getStringExtra("weather"));
		int widgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
				AppWidgetManager.INVALID_APPWIDGET_ID);
		ClearWeatherWidget.DATA_WRITTEN.put(widgetId, true);
		Log.i("ARRAY:", widgetId + "=" + DATA_WRITTEN.get(widgetId));

		AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
		appWidgetManager.updateAppWidget(widgetId, remote);
		updateWidget(context, widgetId);
		appWidgetManager = null;
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
						newWidgetIds.concat(String.valueOf(savedId) + ",");
					}
					if (savedId == currentId) {
						ClearWeatherWidget.DATA_WRITTEN.delete(savedId);
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

		SharedPreferences prefs = context.getSharedPreferences("com.chainsaw.clearweather",
				Context.MODE_PRIVATE);
		if (prefs.contains("WIDGET_IDS")) {
			String ids = prefs.getString("WIDGET_IDS", null);
			StringTokenizer tokenizer = new StringTokenizer(ids, ",");
			Log.w("OK,I'm in", "STARTED TOKENIZER");

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

						Intent regularFetch = new Intent(context, FetchService.class);
						regularFetch.setAction("fetch" + System.currentTimeMillis());
						regularFetch.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);

						// context.startService(regularFetch);
						Log.w("OK,I'm in", "Just before IF, id=" + widgetId);
						Log.w("OK,I'm in",
								"And the DATA_WRITTEN IS:: " + DATA_WRITTEN.get(widgetId));

						if (FetchService.weatherData.isValid() && F)
						if (ClearWeatherWidget.DATA_WRITTEN.get(widgetId)) {
							Log.w("HEY!", "UUUYAA");
							weatherDisplay(context, remote, !WeatherData.loadError, widgetId);
						} else if (FetchService.weatherData == null
								|| !FetchService.weatherData.isValid()) {
							context.startService(regularFetch);
						}

						appWidgetManager.updateAppWidget(widgetId, remote);
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
		AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
		appWidgetManager.updateAppWidget(widgetId, remote);

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

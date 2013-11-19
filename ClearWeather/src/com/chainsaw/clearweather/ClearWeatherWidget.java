package com.chainsaw.clearweather;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

public class ClearWeatherWidget extends AppWidgetProvider {

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		super.onUpdate(context, appWidgetManager, appWidgetIds);

		for (int i = 0; i < appWidgetIds.length; i++) {
			int widgetId = appWidgetIds[i];
			RemoteViews remote = new RemoteViews(context.getPackageName(),
					R.layout.widget_layout);

			Intent servIntent = new Intent(context, FetchService.class);
			servIntent.setAction("fetch" + System.currentTimeMillis());
			servIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);
			PendingIntent pendingIntent = PendingIntent.getService(context, 0,
					servIntent, 0);
			remote.setOnClickPendingIntent(R.id.temp, pendingIntent);
			Log.i("Provider", "Intent NO:[" + String.valueOf(widgetId));

			appWidgetManager.updateAppWidget(widgetId, remote);

		}

	}

	@Override
	public void onEnabled(Context context) {
		super.onEnabled(context);

		// final RemoteViews remote = new RemoteViews(context.getPackageName(),
		// R.layout.widget_layout);
		//
		//
		// WeatherDataListener listener;
		//
		// LocationData loc = new LocationData(context);
		// OpenWeatherAPI weather = new OpenWeatherAPI();
		//
		// listener = new WeatherDataListener() {
		// @Override
		// public void onDataReady(JSONObject weatherData) {
		// try {
		// JSONObject mainObj = weatherData.getJSONObject("main");
		// WeatherData
		// .setTemp((int) (mainObj.getDouble("temp") - 273.15));
		// WeatherData.humidity = (int) (mainObj.getInt("humidity"));
		// WeatherData.timestamp = System.currentTimeMillis();
		// remote.setTextViewText(R.id.temp,
		// String.valueOf(WeatherData.getTempC()));
		//
		// } catch (JSONException e) {
		// e.printStackTrace();
		// }
		// }
		// };
		//
		// weather.setListener(listener);
		// weather.getData(loc.getData());

	}

	public static void updateWidget(Context context, int appWidgetId) {
		Intent intent = new Intent(context, ClearWeatherWidget.class);
		intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
		intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
		context.sendBroadcast(intent);
		Log.i("Provider", "Update Called...");
	}

}

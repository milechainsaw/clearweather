package com.chainsaw.clearweather;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
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
			remote.setOnClickPendingIntent(R.id.widget, pendingIntent);

			appWidgetManager.updateAppWidget(widgetId, remote);

		}

	}
	
		
	public static void updateWidget(Context context, int appWidgetId) {
		Intent intent = new Intent(context, ClearWeatherWidget.class);
		intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
		intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
		context.sendBroadcast(intent);
		Log.i("Provider", "Update Called...");
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

}

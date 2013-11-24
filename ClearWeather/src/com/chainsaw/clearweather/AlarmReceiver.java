package com.chainsaw.clearweather;

import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AlarmReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Intent fetchRequest = new Intent(context, FetchService.class);
		fetchRequest.setAction("fetch" + System.currentTimeMillis());
		fetchRequest.putExtra("force_update", true);
		fetchRequest.putExtra("from_alarm", true);
		fetchRequest.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
				AppWidgetManager.INVALID_APPWIDGET_ID);
		context.startService(fetchRequest);
	}

}

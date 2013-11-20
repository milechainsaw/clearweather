package com.chainsaw.clearweather;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class ClearWeatherSetup extends Activity {
	private int widgetId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setFinishOnTouchOutside(false);

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			widgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID,
					AppWidgetManager.INVALID_APPWIDGET_ID);
		}

		this.setContentView(R.layout.setup_dialog);

		Button cButton = (Button) findViewById(R.id.celsius);
		Button fButton = (Button) findViewById(R.id.fahrenheit);

		cButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finishSetup(true, widgetId);
			}
		});

		fButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finishSetup(false, widgetId);
			}

		});
	}

	void finishSetup(Boolean celsius,int id) {
		SharedPreferences perfs = ClearWeatherSetup.this.getSharedPreferences(
				"com.chainsaw.clearweather" + String.valueOf(id), MODE_PRIVATE);
		perfs.edit().putBoolean("isCelsius", celsius).apply();
		perfs = null;

		Intent resultValue = new Intent();
		resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);
		setResult(RESULT_OK, resultValue);
		ClearWeatherWidget.updateWidget(ClearWeatherSetup.this, widgetId);

		finish();

	}
}

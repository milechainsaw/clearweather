/*
The MIT License (MIT)

Copyright (c) 2013 Milenko Jovanovic

Permission is hereby granted, free of charge, to any person obtaining a copy of
this software and associated documentation files (the "Software"), to deal in
the Software without restriction, including without limitation the rights to
use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
the Software, and to permit persons to whom the Software is furnished to do so,
subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

*/
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

	void finishSetup(Boolean celsius, int id) {
		SharedPreferences perfs = ClearWeatherSetup.this.getSharedPreferences(
				"com.chainsaw.clearweather" + String.valueOf(id), MODE_PRIVATE);
		perfs.edit().putBoolean("isCelsius", celsius).apply();
		perfs = null;

		perfs = ClearWeatherSetup.this.getSharedPreferences("com.chainsaw.clearweather",
				MODE_PRIVATE);
		String old_ids = perfs.getString("WIDGET_IDS", "");
		String newData = old_ids.concat(String.valueOf(id) + ",");
		perfs.edit().putString("WIDGET_IDS", newData).apply();

		Intent resultValue = new Intent();
		resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, id);
		setResult(RESULT_OK, resultValue);
		ClearWeatherWidget.setAlarm(this.getApplicationContext());
		ClearWeatherWidget.updateWidget(ClearWeatherSetup.this, id);

		finish();

	}
}

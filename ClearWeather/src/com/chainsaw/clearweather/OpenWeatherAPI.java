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

import java.net.URL;
import java.net.URLConnection;
import java.util.Locale;

import org.json.JSONObject;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.location.Location;
import android.view.View;
import android.widget.RemoteViews;

public class OpenWeatherAPI {

	String serviceURL = "http://api.openweathermap.org/data/2.5/weather?";
	String LANG_PREFIX = "&lang=";
	String API_KEY = "&APPID=3602ce9ed1ea7a9a94a18594b0893d05";
	URL fetchRequest;
	URLConnection serviceConnection;
	JSONObject weatherData;
	BackgroundFetch asyncLoader;
	WeatherDataListener listener = null;

	public OpenWeatherAPI(final Context context, final int widgetId) {
		final RemoteViews remote = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
		final AppWidgetManager manager = AppWidgetManager.getInstance(context);

		asyncLoader = new BackgroundFetch(context) {
			@Override
			protected void onPostExecute(WeatherData result) {
				remote.setViewVisibility(R.id.loading, View.INVISIBLE);
				if (OpenWeatherAPI.this.listener != null)
					OpenWeatherAPI.this.listener.onDataReady(result);

			}

			@Override
			protected void onProgressUpdate(Integer... values) {
				remote.setViewVisibility(R.id.temp, View.INVISIBLE);
				remote.setViewVisibility(R.id.tempUnit, View.INVISIBLE);
				remote.setViewVisibility(R.id.humidity, View.INVISIBLE);
				remote.setViewVisibility(R.id.humidityUnit, View.INVISIBLE);
				remote.setViewVisibility(R.id.location, View.INVISIBLE);
				remote.setViewVisibility(R.id.weather, View.INVISIBLE);
				remote.setViewVisibility(R.id.timestamp, View.INVISIBLE);
				remote.setViewVisibility(R.id.loading, View.VISIBLE);
				remote.setProgressBar(R.id.loading, 10, 5, true);
				manager.updateAppWidget(widgetId, remote);

				super.onProgressUpdate(values);

			}
		};
	}

	void getData(Location location) {
		if (location == null) {
			location = new Location("loc");
			location.setLatitude(60.075);
			location.setLongitude(12.643);
		}
		String lang_code = Locale.getDefault().getLanguage();
		if(lang_code.equals("es")){
			lang_code="sp";
		}
		if(lang_code.equals("uk")){
			lang_code="ua";
		}
		if(lang_code.equals("sv")){
			lang_code="se";
		}
		
		asyncLoader.execute(serviceURL + "lat=" + String.valueOf(location.getLatitude()) + "&"
				+ "lon=" + String.valueOf(location.getLongitude()) + LANG_PREFIX + lang_code
				+ API_KEY);
	}

	void setListener(WeatherDataListener li) {
		this.listener = li;
	}

}

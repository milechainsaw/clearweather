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

import java.text.DateFormat;

import android.app.AlarmManager;
import android.content.Context;

public class WeatherData {

	private int temp;
	private int humidity;
	private String cityName;
	private String type;
	public static long timestamp = 0;
	private boolean dataAvailable = false;
	private static long TIME_VALID = AlarmManager.INTERVAL_HALF_HOUR;
	public static boolean loadError = false;
	public Context context;
	public static int newDataStatus = BackgroundFetch.NO_DATA;
	
	public WeatherData() {
	}
	
	public String getTimestamp(){
		DateFormat df = android.text.format.DateFormat.getTimeFormat(context);
		String formattedDate = df.format(WeatherData.timestamp);
		return formattedDate;
		
	}
	
	public WeatherData(boolean err) {
		temp=0;
		humidity=0;
		cityName="";
		type="";
		loadError=err;
	}

	public WeatherData(boolean dataPresent, int temperature, int humid,
			String name, String weatherType) {
		this.dataAvailable = dataPresent;
		this.humidity = humid;
		this.temp = temperature;
		this.cityName = name;
		this.type = weatherType;
		
		WeatherData.timestamp = System.currentTimeMillis();
	}

	public int getTempC() {
		return temp;
	}

	public int getTempF() {
		return (int) ((this.temp * 1.8) + 32);
	}

	public boolean isValid() {
		if (timestamp - TIME_VALID <= 0) {
			return false;
		} else {
			return true;
		}

	}

	public int getHumidity() {
		return humidity;
	}

	public String getCityName() {
		return cityName;
	}

	public String getType() {
		return type;
	}

	public boolean isDataAvailable() {
		return dataAvailable;
	}

	public String toString() {

		return String.valueOf(this.dataAvailable) + ", "
				+ String.valueOf(this.getTempC()) + ", "
				+ String.valueOf(this.getHumidity()) + ", " + this.cityName
				+ ", " + this.getType();

	}
}

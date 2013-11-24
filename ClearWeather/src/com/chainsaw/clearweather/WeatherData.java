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

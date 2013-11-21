package com.chainsaw.clearweather;

public class WeatherData {

	private int temp;
	private int humidity;
	private String cityName;
	private String type;
	private static long timestamp = 0;
	private boolean dataAvailable;
	private static long TIME_VALID = 600000;

	public WeatherData() {
	}

	public WeatherData(boolean dataPresent, int temperature, int humid,
			String name, String weatherType) {
		this.dataAvailable = dataPresent;
		this.humidity = humid;
		this.temp = temperature;
		this.cityName = name;
		this.type = weatherType;

		this.timestamp = System.currentTimeMillis();
	}

	public int getTempC() {
		return temp;
	}

	public int getTempF() {
		return (int) ((this.temp * 1.8) + 32);
	}

	public static boolean isValid() {
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

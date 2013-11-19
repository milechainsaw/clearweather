package com.chainsaw.clearweather;

public class WeatherData {

	private static int temp;
	static int humidity;
	static long timestamp = 0;
	private static long TIME_VALID = 600000;

	public WeatherData() {
	}
	
	static void setTemp(int t){
		temp = t;
	}
	
	static int getTempC(){
		return temp;		
	}
	
	static int getTempF(){
		return (int) ((temp * 1.8) +32);		
	}

	static boolean isValid() {
		if (timestamp - TIME_VALID <= 0) {
			return false;
		} else {
			return true;
		}

	}
}

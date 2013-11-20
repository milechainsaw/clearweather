package com.chainsaw.clearweather;

public class WeatherData {

	private  int temp;
	private int humidity;
	private String cityName;
	private long timestamp = 0;
	private long TIME_VALID = 600000;

	public WeatherData() {
	}
	public WeatherData(int temperature, int humid, String name){
		this.setHumidity(humid);
		this.setTemp(temperature);
		this.cityName = name;
		this.timestamp = System.currentTimeMillis();
	}
	
	
	public void setTemp(int t){
		this.temp = t;
	}
	
	public int getTempC(){
		return temp;		
	}
	
	public int getTempF(){
		return (int) ((this.temp * 1.8) +32);		
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

	public void setHumidity(int humidity) {
		this.humidity = humidity;
	}
	public String getCityName() {
		return cityName;
	}
	public void setCityName(String cityName) {
		this.cityName = cityName;
	}
}

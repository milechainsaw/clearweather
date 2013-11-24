package com.chainsaw.clearweather;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;

class BackgroundFetch extends AsyncTask<String, Integer, WeatherData> {

	JSONObject weatherData;
	StringBuffer buffer;
	WeatherData returnData = null;
	Context appContext;
	public static final int SERVER_TIMEOUT = 10000;
	public static final int NO_NETWORK = 0;
	public static final int SERVER_ERROR = 1;
	public static final int NO_DATA = 2;
	public static final int DONE = 3;
	private int fetchStatus;

	public BackgroundFetch(Context context) {
		appContext = context;
	}

	@Override
	protected WeatherData doInBackground(String... params) {
		publishProgress(10);

		HttpURLConnection con = null;
		InputStream is = null;

		if (!isNetworkAvailable()) {
			fetchStatus = NO_NETWORK;
		}

		if (isNetworkAvailable()) {

			try {
				con = (HttpURLConnection) (new URL(params[0])).openConnection();
				con.setRequestMethod("GET");
				con.setDoInput(true);
				con.setDoOutput(true);
				con.setConnectTimeout(SERVER_TIMEOUT);
				con.connect();
				buffer = new StringBuffer();
				is = con.getInputStream();
				BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
				String line = null;
				while ((line = br.readLine()) != null)
					buffer.append(line);
				is.close();
				con.disconnect();
			} catch (Throwable t) {
				fetchStatus = SERVER_ERROR;
				t.printStackTrace();
			} finally {
				try {
					is.close();
				} catch (Throwable t) {
					fetchStatus = SERVER_ERROR;
					t.printStackTrace();
				}
				try {
					con.disconnect();
				} catch (Throwable t) {
					fetchStatus = SERVER_ERROR;
					t.printStackTrace();
				}
			}

			try {
				weatherData = (buffer.toString() == null) ? new JSONObject() : new JSONObject(
						buffer.toString());
				fetchStatus = DONE;
			} catch (Throwable e) {
				fetchStatus = NO_DATA;
				e.printStackTrace();
			}

			is = null;
			con = null;

			try {
				if ((fetchStatus == DONE) && (weatherData != null)) {
					WeatherData.newDataStatus = DONE;
					JSONObject mainObj = weatherData.getJSONObject("main");
					JSONArray weatherObj = weatherData.getJSONArray("weather");
					returnData = new WeatherData(true,
							(int) (Math.round(mainObj.getDouble("temp") - 273.15)),
							((int) (Math.round(mainObj.getInt("humidity")))),
							weatherData.getString("name"),
							((JSONObject) weatherObj.get(0)).getString("description"));
				}
			} catch (Throwable e) {
				fetchStatus = NO_DATA;
				e.printStackTrace();
			}
		}
		switch (fetchStatus) {
		case NO_DATA:
			WeatherData.newDataStatus = NO_DATA;
			break;
		case SERVER_ERROR:
			WeatherData.newDataStatus = SERVER_ERROR;
			break;
		case NO_NETWORK:
			WeatherData.newDataStatus = NO_NETWORK;
			break;
		}
		if (returnData == null)
			WeatherData.newDataStatus = NO_DATA;
		
		return returnData;

	}

	private boolean isNetworkAvailable() {
		ConnectivityManager connectivityManager = (ConnectivityManager) appContext
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
		return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}
}
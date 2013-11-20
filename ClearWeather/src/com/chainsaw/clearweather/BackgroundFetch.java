package com.chainsaw.clearweather;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;

class BackgroundFetch extends AsyncTask<String, Void, WeatherData> {

	JSONObject weatherData;
	StringBuffer buffer;
	WeatherData returnData;

	@Override
	protected WeatherData doInBackground(String... params) {

		HttpURLConnection con = null;
		InputStream is = null;

		try {
			con = (HttpURLConnection) (new URL(params[0])).openConnection();
			con.setRequestMethod("GET");
			con.setDoInput(true);
			con.setDoOutput(true);
			con.connect();
			buffer = new StringBuffer();
			is = con.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(is,
					"UTF-8"));
			String line = null;
			while ((line = br.readLine()) != null)
				buffer.append(line + "\r\n");
			is.close();
			con.disconnect();

		} catch (Throwable t) {
			t.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (Throwable t) {
			}
			try {
				con.disconnect();
			} catch (Throwable t) {
			}
		}

		try {
			weatherData = new JSONObject(buffer.toString());
		} catch (JSONException e) {
			e.printStackTrace();
		}

		is = null;
		con = null;

		try {
			JSONObject mainObj = weatherData.getJSONObject("main");
			returnData = new WeatherData(
					((int) (mainObj.getDouble("temp") - 273.15)),
					((int) (mainObj.getInt("humidity"))), weatherData.getString("name"));
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return returnData;

	}
}
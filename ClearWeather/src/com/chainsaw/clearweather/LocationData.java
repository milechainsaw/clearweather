package com.chainsaw.clearweather;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;

public class LocationData {

	String locationProvider;
	LocationManager manager;
	Location location;

	LocationData(Context app) {
		locationProvider = LocationManager.PASSIVE_PROVIDER;
		manager = (LocationManager) app
				.getSystemService(Context.LOCATION_SERVICE);

	}

	Location getData(){		
		location = manager.getLastKnownLocation(locationProvider);
		return location;
	}
}

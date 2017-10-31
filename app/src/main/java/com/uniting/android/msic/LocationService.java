package com.uniting.android.msic;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;

/**
 * Created by jeep on 10/31/17.
 */

public class LocationService implements LocationListener {

  private final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 0;
  private final long MIN_TIME_BETWEEN_UPDATES = 0;
  private static final String TAG = "LocationService";

  private LocationManager locationManager;
  private Location location;

  public LocationService(Context context){
    init(context);
  }

  private void init(Context context){
    Log.d(TAG, "LocationService Initialized");
    this.locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    if(ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        && ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
      Log.d(TAG, "We have permission");
      this.locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BETWEEN_UPDATES,
          MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
    }else{
      Log.d(TAG, "This won't work!!!");
    }
  }

  public Location getLocation(){
    return this.location;
  }

  @Override
  public void onLocationChanged(Location location) {
    Log.d(TAG, "onLocationChanged called");
    this.location = location;
  }

  @Override
  public void onStatusChanged(String s, int i, Bundle bundle) {

  }

  @Override
  public void onProviderEnabled(String s) {

  }

  @Override
  public void onProviderDisabled(String s) {

  }
}

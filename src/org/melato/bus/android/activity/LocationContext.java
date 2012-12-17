/*-------------------------------------------------------------------------
 * Copyright (c) 2012, Alex Athanasopoulos.  All Rights Reserved.
 * alex@melato.org
 *-------------------------------------------------------------------------
 * This file is part of Athens Next Bus
 *
 * Athens Next Bus is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Athens Next Bus is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Athens Next Bus.  If not, see <http://www.gnu.org/licenses/>.
 *-------------------------------------------------------------------------
 */
package org.melato.bus.android.activity;

import org.melato.android.location.Locations;
import org.melato.bus.android.Info;
import org.melato.gps.Metric;
import org.melato.gps.PointTime;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;

/**
 * A location listener that attaches itself to an activity and maintains a current location.
 * Subclass and override setLocation() to do something with the locations.
 * The activity must call close() from its onDestroy() method
 * to remove the listener from the LocationManager.
 * @author Alex Athanasopoulos
 */
public class LocationContext implements LocationListener {
  protected Context context;
  private PointTime   location;
  private boolean enabledLocations;
  private Metric metric;

  public LocationContext(Context context) {
    super();
    this.context = context;
  }
  
  public Metric getMetric() {
    if ( metric == null ) {
      metric = Info.routeManager(context).getMetric();
    }
    return metric;    
  }
  
  public void setEnabledLocations(boolean enabled) {
    if ( enabledLocations == enabled )
      return;
    LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    //PlaybackManager locationManager = PlaybackManager.getInstance(context);
    if ( enabled ) {
      this.enabledLocations = true;
      SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
      // The preferences dialog seems to be putting in strings instead of integers
      long timeInterval = Integer.parseInt(prefs.getString(Pref.GPS_TIME, "1")) * 1000L;
      float minDistance = Float.parseFloat(prefs.getString(Pref.GPS_DISTANCE, "5"));
      locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, timeInterval, minDistance, this);
      Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
      if ( location == null )
        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
      onLocationChanged(location);
    } else {
      this.enabledLocations = false;
      locationManager.removeUpdates(this);      
    }
  }
  
  /** remove location updates. */
  public void close() {
    setEnabledLocations(false);
  }

  public void setLocation(PointTime point) {
    if ( point == null )
      return;
    location = point;
  }
    
  public PointTime getLocation() {
    return location;
  }
  
  @Override
  public void onLocationChanged(Location loc) {
    setLocation(Locations.location2Point(loc));
  }
  @Override
  public void onProviderDisabled(String provider) {
  }
  @Override
  public void onProviderEnabled(String provider) {
  }
  @Override
  public void onStatusChanged(String provider, int status, Bundle extras) {
  }
}
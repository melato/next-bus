/*-------------------------------------------------------------------------
 * Copyright (c) 2012,2013 Alex Athanasopoulos.  All Rights Reserved.
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
package org.melato.bus.android;

import org.melato.bus.android.activity.Pref;
import org.melato.bus.client.TrackHistory;
import org.melato.gps.PointTime;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;

/**
 * Maintains track history.
 * @author Alex Athanasopoulos
 *
 */
public class AndroidTrackHistory extends TrackHistory implements LocationListener {
  private Context context;
  
  public AndroidTrackHistory(Context context) {
    super(Info.routeManager(context));
    this.context = context.getApplicationContext();
  }
  
  protected void enableUpdates(boolean enabled) {
    LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    //PlaybackManager locationManager = PlaybackManager.getInstance(context);
    if ( enabled ) {
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
      locationManager.removeUpdates(this);      
    }
  }
  
  @Override
  public void onLocationChanged(Location loc) {
    PointTime p = null;
    if (loc != null) {
      p = new PointTime( (float) loc.getLatitude(), (float) loc.getLongitude());
      p.setTime(loc.getTime());
    }
    setLocation(p);
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
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
  static public int FAST_DURATION = 60000;
  GpsInterval normalInterval = new GpsInterval(3, 20);
  GpsInterval fastInterval = new GpsInterval(1, 5);
  boolean isFast;
  long  fastStartTime;
  
  static class GpsInterval {
    int seconds;
    int meters;
    public GpsInterval(int seconds, int meters) {
      super();
      this.seconds = seconds;
      this.meters = meters;
    }    
  }
  
  public AndroidTrackHistory(Context context) {
    super(Info.routeManager(context));
    this.context = context.getApplicationContext();
  }
  
  void setGpsInterval(LocationManager locationManager, GpsInterval interval) {
    locationManager.removeUpdates(this);      
    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, interval.seconds * 1000L, interval.meters, this);
  }

  public void setFast() {
    LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    setGpsInterval(locationManager, fastInterval);
    isFast = true;
    fastStartTime = System.currentTimeMillis();
  }
  
  public boolean isFast() {
    return isFast;
  }

  protected void enableUpdates(boolean enabled) {
    LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    //PlaybackManager locationManager = PlaybackManager.getInstance(context);
    if ( enabled ) {
      setGpsInterval(locationManager, normalInterval);
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
    if ( isFast && System.currentTimeMillis() - fastStartTime > FAST_DURATION) {
      LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
      setGpsInterval(locationManager, normalInterval);
      isFast = false;
      fastStartTime = System.currentTimeMillis();
    }
    
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
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
package org.melato.bus.android.map;

import java.util.List;

import org.melato.gps.Point2D;

import com.google.android.maps.GeoPoint;

public class RoutePoints {
  private int[] lat;
  private int[] lon;
  
  public int size() {
    return lat.length;
  }
  
  public int getLatitude6E(int i) {
    return lat[i];
  }
  public int getLongitude6E(int i) {
    return lon[i];
  }
  public GeoPoint getGeoPoint(int i) {
    return new GeoPoint(lat[i], lon[i]);
  }
  private static int mean(int[] coordinates) {
    long sum = 0;
    for( int i = 0; i < coordinates.length; i++ ) {
      sum += coordinates[i];
    }
    return (int) (sum / coordinates.length);
  }
  public GeoPoint getCenterGeoPoint() {
    return new GeoPoint(mean(lat), mean(lon));    
  }
  public boolean isInside(int i, int latMin, int latMax, int lonMin, int lonMax) {
    int lat = getLatitude6E(i);
    int lon = getLongitude6E(i);
    return latMin < lat && lat < latMax && lonMin < lon && lon < lonMax;     
  }
  
  public RoutePoints(int[] lat, int[] lon) {
    super();
    this.lat = lat;
    this.lon = lon;
  }

  public static RoutePoints createFromPoints(List<Point2D> waypoints) {
    int n = waypoints.size();
    int[] lat = new int[n];
    int[] lon = new int[n];
    for( int i = 0; i < n; i++ ) {
      Point2D p = waypoints.get(i);
      lat[i] = (int) (p.getLat()*1e6f);
      lon[i] = (int) (p.getLon()*1e6f);
    }
    return new RoutePoints(lat,lon); 
  }
}

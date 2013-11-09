/*-------------------------------------------------------------------------
 * Copyright (c) 2012,2013, Alex Athanasopoulos.  All Rights Reserved.
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
package org.melato.bus.model.cache;

import java.util.List;

import org.melato.gps.Point2D;

/** A compact way of representing the coordinates of all the stops in a route.
 * Used to cache route shapes and draw them on a map.
 * */
public class RoutePoints {
  private float[] lat;
  private float[] lon;
  
  public int size() {
    return lat.length;
  }
  
  public float getLat(int i) {
    return lat[i];
  }
  
  public float getLon(int i) {
    return lon[i];
  }
  
  public Point2D getPoint(int i, Point2D point) {
    point.setLat(getLat(i));
    point.setLon(getLon(i));
    return point;
  }
  
  public Point2D getPoint(int i) {
    Point2D point = new Point2D();
    return getPoint(i, point);
  }
  
  public Point2D[] toArray() {
    Point2D[] points = new Point2D[size()];
    for( int i = 0; i < points.length; i++ ) {
      points[i] = getPoint(i);
    }
    return points;
  }
  
  private static float mean(float[] coordinates) {
    double sum = 0;
    for( int i = 0; i < coordinates.length; i++ ) {
      sum += coordinates[i];
    }
    return (float) (sum / coordinates.length);
  }
  public Point2D getCenter() {
    return new Point2D(mean(lat), mean(lon));    
  }
  public boolean isInside(int i, GpsRectangle r) {
    float lat = this.lat[i];
    float lon = this.lon[i];
    return r.latMin < lat && lat < r.latMax && r.lonMin < lon && lon < r.lonMax;     
  }
  
  public RoutePoints(float[] lat, float[] lon) {
    super();
    this.lat = lat;
    this.lon = lon;
  }

  public static RoutePoints createFromPoints(List<Point2D> waypoints) {
    int n = waypoints.size();
    float[] lat = new float[n];
    float[] lon = new float[n];
    for( int i = 0; i < n; i++ ) {
      Point2D p = waypoints.get(i);
      lat[i] = p.getLat();
      lon[i] = p.getLon();
    }
    return new RoutePoints(lat,lon); 
  }

  @Override
  public String toString() {
    StringBuilder buf = new StringBuilder();
    buf.append("RoutePoints: " );
    buf.append( String.valueOf(size()));
    if ( size() > 0) {
      buf.append( " first=");
      buf.append(getLat(0));
      buf.append(";");
      buf.append(getLon(0));      
    }
    return buf.toString();
  }
  
}

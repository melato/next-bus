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
package org.melato.bus.android.map;

import java.util.List;

import org.melato.bus.model.cache.GpsRectangle;
import org.melato.bus.model.cache.RoutePoints;

import android.graphics.Path;
import android.graphics.Point;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Projection;

/** Draws routes or pieces of routes on a map. */
public class RoutePlotter {
  public static GpsRectangle findBoundaries(MapView view) {
    int latSpan = view.getLatitudeSpan();
    int lonSpan = view.getLongitudeSpan();
    GeoPoint center = view.getMapCenter();
    GpsRectangle r = new GpsRectangle();
    r.latMin = (center.getLatitudeE6() - latSpan / 2)/1e6f;
    r.latMax = (center.getLatitudeE6() + latSpan / 2)/1e6f;
    r.lonMin = (center.getLongitudeE6() - lonSpan / 2)/1e6f;
    r.lonMax = (center.getLongitudeE6() + lonSpan / 2)/1e6f;
    return r;
  }
    
  
  public static GeoPoint getGeoPoint(RoutePoints points, int i) {
    return new GeoPoint((int) (1e6f*points.getLat(i)), (int)(1e6f*points.getLon(i)));
  }
   
  public static Path getPath(Projection projection, RoutePoints points, GpsRectangle boundaries) {
    Path path = new Path();
    int size = points.size();
    if ( size == 0 )
      return path;
    Point p = new Point();
    boolean previousInside = false;
    projection.toPixels(getGeoPoint(points, 0), p);
    path.moveTo(p.x, p.y);
    for( int i = 0; i < size; i++ ) {
      boolean inside = points.isInside(i, boundaries);
      if ( previousInside ) {
        // draw from previous point
        projection.toPixels(getGeoPoint(points, i), p);
        path.lineTo(p.x, p.y );
      } else if ( inside && i > 0 ) {
        projection.toPixels(getGeoPoint(points, i-1), p);
        path.moveTo(p.x, p.y );          
        projection.toPixels(getGeoPoint(points, i), p);
        path.lineTo(p.x, p.y );
      } else {
        // do nothing
        // segment is outside the view area or we are at the beginning            
      }
      previousInside = inside;
    }
    return path;
  }

  public static Path getPath(Projection projection, List<GeoPoint> points) {
    Path path = new Path();
    int size = points.size();
    if ( size == 0 )
      return path;
    Point p = new Point();
    projection.toPixels(points.get(0), p);
    path.moveTo(p.x, p.y);
    for( int i = 0; i < size; i++ ) {
      projection.toPixels(points.get(i), p);
      path.lineTo(p.x, p.y );
    }
    return path;
  }
    

}

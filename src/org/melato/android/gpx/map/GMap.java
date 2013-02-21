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
package org.melato.android.gpx.map;

import org.melato.gps.Point2D;

import android.location.Location;

import com.google.android.maps.GeoPoint;

public class GMap {
  public static GeoPoint geoPoint(float lat, float lon) {
    return new GeoPoint( (int) (lat * 1E6f), (int)(lon * 1E6f));
  }
  public static GeoPoint geoPoint(Point2D p) {   
    return geoPoint(p.getLat(), p.getLon());    
  }
  public static GeoPoint geoPoint(Location loc) {
    if ( loc == null )
      return null;
    return geoPoint((float) loc.getLatitude(), (float) loc.getLongitude());
  }

  public static Point2D point(GeoPoint p) {   
    return new Point2D( p.getLatitudeE6() / 1E6f,  p.getLongitudeE6() / 1E6f);    
  }
}

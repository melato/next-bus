/*-------------------------------------------------------------------------
 * Copyright (c) 2012,2013, Alex Athanasopoulos.  All Rights Reserved.
 * alex@melato.org
 *-------------------------------------------------------------------------
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *-------------------------------------------------------------------------
 */
package org.melato.android.location;

import org.melato.gps.GpsPoint;
import org.melato.gps.Point2D;

import android.content.Intent;
import android.location.Location;
import android.net.Uri;

public class Locations {
  public static GpsPoint location2Point(Location loc) {
    if ( loc == null )
      return null;
    GpsPoint p = new GpsPoint( (float) loc.getLatitude(), (float) loc.getLongitude());
    p.setTime(loc.getTime());
    if ( loc.hasSpeed() ) {
      p.setSpeed(loc.getSpeed());
    }
    if ( loc.hasAltitude() ) {
      p.setElevation((float)loc.getAltitude());
    }
    return p;
  }
  public static Point2D getGeoUriPoint(Intent intent) {
    Uri uri = intent.getData();
    if ( uri == null )
      return null;
    String scheme = uri.getScheme();
    if ( ! "geo".equals(scheme)) {
      return null;
    }
    String value = uri.getSchemeSpecificPart();
    String[] fields = value.split(",");
    if ( fields.length == 2 ) {
      float lat = Float.parseFloat(fields[0]);
      float lon = Float.parseFloat(fields[1]);
      return new Point2D(lat,lon);
    }
    return null;
  }
}

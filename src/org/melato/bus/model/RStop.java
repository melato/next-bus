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
package org.melato.bus.model;

import java.io.Serializable;
import java.text.DecimalFormat;

/**
 * A stop on a route.
 * (Will probably merge with RouteStop)
 * @author Alex Athanasopoulos
 *
 */
public class RStop implements Serializable, Comparable<RStop> {
  private static final long serialVersionUID = 1L;
  private static DecimalFormat kmFormat = new DecimalFormat( "0.00" );
  private RouteId routeId;
  private Stop    stop;
  private int     stopIndex;
  private float   distance;
  public RStop(RouteId routeId, Stop stop, int stopIndex) {
    super();
    this.routeId = routeId;
    this.stop = stop;
    this.stopIndex = stopIndex;
  }
  public RouteId getRouteId() {
    return routeId;
  }
  public Stop getStop() {
    return stop;
  }
  public int getStopIndex() {
    return stopIndex;
  }
  public float getDistance() {
    return distance;
  }
  public void setDistance(float distance) {
    this.distance = distance;
  }  
  public static int compare(RStop x, RStop y) {
    if ( x.distance < y.distance )
      return -1;
    if ( x.distance > y.distance )
      return 1;
    return 0;
  }

  public static String formatDistance(float distance) {
    if ( Math.abs(distance) < 1000 ) {
      return String.valueOf( Math.round(distance)) + "m";
    } else {
      return kmFormat.format(distance/1000) + "Km";
    }
  }
  @Override
  public int compareTo(RStop o) {
    return compare(this, o);
  }
}

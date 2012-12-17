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
package org.melato.bus.client;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;

import org.melato.bus.model.Marker;
import org.melato.gps.Earth;
import org.melato.gps.Point2D;

/** Contains a Waypoint and a distance.
 * The distance may be the route distance of the waypoint from the beginning of a route,
 * or the distance between the waypoint and the current location.
 * */
public class WaypointDistance implements Comparable<WaypointDistance> {
  private Marker waypoint;
  private float distance;
  static DecimalFormat kmFormat = new DecimalFormat( "0.00" );
  
  public WaypointDistance(Marker waypoint, float distance) {
    this.waypoint = waypoint;
    this.distance = distance;
  }
  
  public Marker getWaypoint() {
    return waypoint;
  }
    
  public float getDistance() {
    return distance;
  }
  
  public void setDistance(float distance) {
    this.distance = distance;
  }

  public static String formatDistance(float distance) {
    if ( Math.abs(distance) < 1000 ) {
      return String.valueOf( Math.round(distance)) + "m";
    } else {
      return kmFormat.format(distance/1000) + "Km";
    }
  }
  
  public static int compare(WaypointDistance x, WaypointDistance y) {
    if ( x.distance < y.distance )
      return -1;
    if ( x.distance > y.distance )
      return 1;
    return 0;
  }

  @Override
  public int compareTo(WaypointDistance x) {
    return compare(this, x);
  }

  @Override
  public String toString() {
    return waypoint.getName() + " " + formatDistance(distance);
  }
  
  /**
   * Order waypoints according to closest distance to target. 
   * @param waypoints
   * @param target
   * @return
   */
  public static Marker[] sort(List<Marker> waypoints, Point2D target) {
    WaypointDistance[] array = createArray(waypoints, target );
    Arrays.sort(array);
    Marker[] result = new Marker[array.length];
    for( int i = 0; i < array.length; i++ ) {
      result[i] = array[i].getWaypoint();
    }
    return result;
  }

  public static void setDistance(WaypointDistance[] array, Point2D location) {
    for(WaypointDistance stop: array ) {
      stop.setDistance(Earth.distance(stop.getWaypoint(), location));          
    }
  }  
  
  public static WaypointDistance[] createArray(List<Marker> waypoints, Point2D target) {
    WaypointDistance[] array = new WaypointDistance[waypoints.size()];
    for( int i = 0; i < array.length; i++ ) {
      Marker p = waypoints.get(i); 
      float distance = target != null ? Earth.distance(p, target) : 0;
      array[i] = new WaypointDistance(p, distance);
    }
    return array;
  }
}

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

import java.util.Comparator;

import org.melato.bus.model.Route;
import org.melato.gpx.Waypoint;

/** Maintains information about a bus stop nearby. */
public class NearbyStop extends WaypointDistance {
  private Route     route;
  private int       group;

  public static class Comparer implements Comparator<NearbyStop> {

    @Override
    public int compare(NearbyStop s1, NearbyStop s2) {
      int d = WaypointDistance.compare(s1,  s2);
      if ( d != 0 )
        return d;
      // when two routes have the same stop, compare them by name.
      return s1.route.compareTo(s2.route);
    }
    
  }
  public NearbyStop(Waypoint waypoint, Route route) {
    super(waypoint, 0f);
    this.route = route;
  }
  
  public Route getRoute() {
    return route;
  }

  public int getGroup() {
    return group;
  }

  public void setGroup(int group) {
    this.group = group;
  }

  @Override
  public String toString() {
    String s = route + " " + getWaypoint().getName() + " (" + formatDistance(getDistance()) + ")";
    return s;
  }
}

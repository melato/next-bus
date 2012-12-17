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
package org.melato.bus.android.activity;

import java.io.Serializable;

import org.melato.bus.model.RouteId;
import org.melato.bus.model.Stop;

/** Identifies a route and a stop on this route. */
public class RouteStop implements Serializable {
  private static final long serialVersionUID = 1L;
  private RouteId routeId;
  private String  stopSymbol;
  private int     stopIndex;
  
  
  public RouteStop(RouteId routeId) {
    super();
    this.routeId = routeId;
  }
  
  public boolean hasStop() {
    return stopSymbol != null || stopIndex >= 0;
  }
  /** Get the time from the start of the route.
   * @param waypoints
   * @return time in seconds.
   */
  public int getTimeFromStart(Stop[] stops) {
    if ( stopIndex == -1 && stopSymbol == null )
      return 0;
    int index = getStopIndex(stops);
    int time = 0;
    for( int i = 0; i <= index; i++ ) {
      time += (int) (stops[i].getTime() / 1000);
    }
    return time;    
  }
  
  public int getStopIndex(Stop[] stops) {
    if ( stopIndex >= 0 && stopIndex < stops.length )
      return stopIndex;
    int size = stops.length;
    for( int i = 0; i < size; i++ ) {
      if ( stopSymbol.equals(stops[i].getSymbol())) {
        return i;
      }
    }
    return -1;
  }
  
  public String getStopName(Stop[] stops) {
    int index = getStopIndex(stops);
    if ( index >= 0 )
      return stops[index].getName();
    return null;
  }
  
  public RouteStop(RouteId routeId, String stopSymbol, int stopIndex) {
    super();
    this.routeId = routeId;
    this.stopSymbol = stopSymbol;
    this.stopIndex = stopIndex;
  }

  public RouteId getRouteId() {
    return routeId;
  }
  public String getStopSymbol() {
    return stopSymbol;
  }
  public void setStopSymbol(String stopSymbol) {
    this.stopSymbol = stopSymbol;
  }
  public int getStopIndex() {
    return stopIndex;
  }
  public void setStopIndex(int stopIndex) {
    this.stopIndex = stopIndex;
  }
  public void setRouteId(RouteId routeId) {
    this.routeId = routeId;
  }
  @Override
  public String toString() {
    String s = String.valueOf(routeId);
    if ( stopSymbol != null || stopIndex != -1) {
      s += " " + stopSymbol + " (" + stopIndex + ")";
    }
    return s;    
  }
  
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((routeId == null) ? 0 : routeId.hashCode());
    result = prime * result
        + ((stopSymbol == null) ? 0 : stopSymbol.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    RouteStop other = (RouteStop) obj;
    if (routeId == null) {
      if (other.routeId != null)
        return false;
    } else if (!routeId.equals(other.routeId))
      return false;
    if (stopSymbol == null) {
      if (other.stopSymbol != null)
        return false;
    } else if (!stopSymbol.equals(other.stopSymbol))
      return false;
    return true;
  }
}

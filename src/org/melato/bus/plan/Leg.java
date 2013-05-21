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
package org.melato.bus.plan;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;

import org.melato.bus.model.RStop;
import org.melato.bus.model.RouteId;
import org.melato.bus.model.Stop;

/** A leg is a portion of a route between two stops. */ 
public class Leg implements Serializable {
  private static final long serialVersionUID = 1L;
  public RouteId routeId;
  public Stop stop1;
  public Stop stop2;
  public Leg(RStop stop) {
    this.routeId = stop.getRouteId();
    this.stop1 = stop.getStop();      
  }
  
  public Leg(RouteId routeId, Stop stop1, Stop stop2) {
    super();
    this.routeId = routeId;
    this.stop1 = stop1;
    this.stop2 = stop2;
  }

  public RouteId getRouteId() {
    return routeId;
  }
  public Stop getStop1() {
    return stop1;
  }
  public Stop getStop2() {
    return stop2;
  }
  public void setStop1(Stop stop1) {
    this.stop1 = stop1;
  }
  public void setStop2(Stop stop2) {
    this.stop2 = stop2;
  }
  public RStop getRStop1() {
    return new RStop(routeId, stop1);
  }
  @Override
  public String toString() {
    StringBuilder buf = new StringBuilder();
    buf.append(routeId);
    buf.append( " " );
    buf.append(stop1.getName());
    if ( stop2 != null) {
      buf.append( " -> " );
      buf.append(stop2.getName());
    }
    return buf.toString();
  }
  
  /**
   * Find legs between two stops
   * @param routeId
   * @param stopList
   * @param symbol1
   * @param symbol2
   * @param legs
   */
  public static void findLegs(RouteId routeId, Collection<Stop> stopList, String symbol1, String symbol2, Collection<Leg> legs ) {
    Comparator<Stop> comparator = new Stop.IndexComparator();
    Stop[] stops = stopList.toArray(new Stop[0]);
    Arrays.sort(stops, comparator);
    for( int i = 0; i < stops.length; i++ ) {
      if ( stops[i].getSymbol().equals(symbol1)) {
        for( int j = i + 1; j < stops.length; j++ ) {
          if ( stops[i].getSymbol().equals(symbol1)) {
            Leg leg = new Leg(routeId, stops[i], stops[j]);
            legs.add(leg);
            break;
          }
        }
      }
    }
  }
  
  
}


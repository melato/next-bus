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
import java.util.ArrayList;
import java.util.List;

import org.melato.bus.model.RStop;
import org.melato.bus.model.Route;
import org.melato.bus.model.RouteId;
import org.melato.bus.model.RouteManager;
import org.melato.bus.model.Stop;
import org.melato.gps.Metric;
import org.melato.gps.Point2D;

public class Sequence implements Serializable {
  private static final long serialVersionUID = 2L;
  public static final float CLOSE_DISTANCE = 100;
  private List<LegGroup> legs;
  
  public List<LegGroup> getLegs() {
    return legs;
  }

  public void setLegs(List<LegGroup> legs) {
    this.legs = legs;
  }

  public Sequence() {
    super();
    legs = new ArrayList<LegGroup>();
    
  }

  public Sequence(List<LegGroup> legs) {
    super();
    this.legs = legs;
  }


  public void addStopAfter(RouteManager routeManager, RStop stop) {
    if ( legs.isEmpty() ) {
      legs.add(new LegGroup(new Leg(stop)));
      return;
    }
    LegGroup lastLeg = legs.get(legs.size()-1);
    if ( lastLeg.getRouteId().equals(stop.getRouteId())) {
      // if the last leg is for the same route.
      if ( lastLeg.getStop1().isBefore(stop.getStop())) {
        // replace stop2
        lastLeg.setStop2(stop.getStop());
      } else {
        // or replace stop1
        lastLeg.setStop1(stop.getStop());
      }
    } else {
      // add a new leg
      if ( lastLeg.getStop2() == null) {
        // we need to complete the previous leg,
        // by finding the intersection of the previous route and the new route        
        Stop s = findClosestStopAfterStop(routeManager, lastLeg.getRouteId(), lastLeg.getStop1(), stop.getStop());
        lastLeg.setStop2(s);
      }
      Leg leg = new Leg(stop);
      legs.add(new LegGroup(leg));
    }
  }
  
  public void addStopBefore(RouteManager routeManager, RStop stop) {
    if ( legs.isEmpty() ) {
      legs.add(new LegGroup(new Leg(stop.getRouteId(), stop.getStop(), stop.getStop())));
      return;
    }
    Leg firstLeg = legs.get(0).getLeg();
    if ( firstLeg.getRouteId().equals(stop.getRouteId())) {
      // if the first leg is for the same route.
      if ( stop.getStop().isBefore(firstLeg.getStop2())) {
        // replace stop1
        firstLeg.setStop1(stop.getStop());
      } else {
        // or replace both stops
        firstLeg.setStop1(stop.getStop());
        firstLeg.setStop2(stop.getStop());
      }
    } else {
      // add a new leg at the beginning
      Leg leg = new Leg(stop.getRouteId(), stop.getStop(), stop.getStop());
      legs.add(0, new LegGroup(leg));
    }
  }
  
  public static Stop findClosestStopAfterStop(RouteManager routeManager, RouteId routeId, Stop stop, Point2D point) {
    Metric metric = routeManager.getMetric();
    Stop[] stops = routeManager.getStops(routeId);
    int minIndex = -1;
    float minDistance = 0; 
    for( int i = stop.getIndex(); i < stops.length; i++ ) {
      float d = metric.distance(stops[i], point);
      if ( minIndex < 0 || d < minDistance) {
        minIndex = i;
        minDistance = d;
      } else {
        if ( minDistance < CLOSE_DISTANCE ) {
          break;
        }
      }
    }
    if ( minIndex == -1 )
      return stop;
    return stops[minIndex];    
  }
  
  
  public String getLabel(RouteManager routeManager) {
    StringBuilder buf = new StringBuilder();
    boolean first = true;
    for(LegGroup leg: legs ) {
      if ( ! first ) {
        buf.append("-");
      }
      first = false;
      Route route = routeManager.getRoute(leg.leg.getRouteId());      
      buf.append(route.getLabel());
    }
    return buf.toString();
  }  
}

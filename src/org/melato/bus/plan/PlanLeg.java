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

import java.util.Comparator;

import org.melato.bus.client.Formatting;
import org.melato.bus.model.Route;
import org.melato.bus.model.Schedule;
import org.melato.bus.model.Stop;

/** One leg of a plan, consisting on a ride on a single route. */
public class PlanLeg {
  private Route route;
  private Stop stop1;
  private Stop stop2;
  /** Next departure time, in seconds since midnight */ 
  private int departureTime;
  /** Nearest Departure time and the next one, in seconds since midnight */ 
  private int[]     nearestTimes;
  /** Distance from previous leg. */
  private float distanceBefore;
  
  /** Sort by route id. */
  public static class RouteComparator implements Comparator<PlanLeg> {
    @Override
    public int compare(PlanLeg s1, PlanLeg s2) {
      return s1.getRoute().getRouteId().compareTo(s2.getRoute().getRouteId());
    }    
  }
  
  public PlanLeg(Route route, Stop stop1, Stop stop2) {
    super();
    this.route = route;
    this.stop1 = stop1;
    this.stop2 = stop2;
  }
  
  public float getDistanceBefore() {
    return distanceBefore;
  }
  
  public void setDistanceBefore(float distanceBefore) {
    this.distanceBefore = distanceBefore;
  }

  
  public int getDepartureTime() {
    return departureTime;
  }

  public void setDepartureTime(int departureTime) {
    this.departureTime = departureTime;
  }

  public int[] getNearestTimes() {
    return nearestTimes;
  }

  public void setNearestTimes(int[] nearestTimes) {
    this.nearestTimes = nearestTimes;
  }

  public Route getRoute() {
    return route;
  }

  public Stop getStop1() {
    return stop1;
  }
  public Stop getStop2() {
    return stop2;
  }
  public String shortString() {
    return route.getLabel() + "(" + (int) distanceBefore + ")";    
  }
  public int getDuration() {
    return (int) ((getStop2().getTime() - getStop1().getTime())/1000L);    
  }
  private String formatTimes() {
    if ( nearestTimes == null || nearestTimes.length == 0 )
      return "";
    StringBuilder buf = new StringBuilder();
    for(int time: nearestTimes) {
      buf.append( " ");
      buf.append( Schedule.formatTimeMod24(time/60));      
    }
    return buf.toString();
  }
  @Override
  public String toString() {
    // 17:50 1260(1223,36) 140-2 ΓΕΝΙΚΟ ΚΡΑΤΙΚΟ -> ΣΚΑΛΑΚΙΑ
    return getRoute().getLabel()
        + formatTimes()
        + " " + getStop1().getName()
        + "(" +(int) getDistanceBefore() + ")" 
        + " -> " + getStop2().getName();
  }
  public String shortLabel() {
    // 140-2 (120m) 17:50 18:05 
    return getRoute().getLabel()
        + " " + Formatting.straightDistance(getDistanceBefore()) 
        + formatTimes();
  }
}

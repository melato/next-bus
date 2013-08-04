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
import java.util.Comparator;

import org.melato.bus.client.Formatting;
import org.melato.bus.model.Schedule;
import org.melato.gps.Metric;
import org.melato.gps.Point2D;

/** A plan is a sequence of walk or transit legs from one point to another, at a particular time. */ 
public class Plan implements Comparable<Plan>, Serializable {
  private static final long serialVersionUID = 1L;
  private Point2D origin;
  private Point2D destination;
  private PlanLeg[] legs;
  private float walkDistance;
  private float lastWalkDistance;
  /** Total plan duration, including walking (seconds) */
  private float duration;
  /** Departure time (seconds from midnight) */
  private int departureTime;
  /** Arrival time (seconds from midnight) */
  private int arrivalTime;

  /** Compares plans by duration. */
  public static class TimeComparator implements Comparator<Plan> {
    @Override
    public int compare(Plan o1, Plan o2) {
      return compareFloats(o1.duration, o2.duration);
    }    
  }
  public static int compareFloats(float a, float b) {
    float diff = a - b;
    if ( ! Float.isNaN(diff)) {
      return diff < 0 ? -1 : (diff > 0 ? 1 : 0);
    }
    if ( ! Float.isNaN(a)) {
      return -1;
    }
    if ( ! Float.isNaN(b)) {
      return 1;
    }
    return 0;
  }
  public Plan(Point2D origin, Point2D destination, PlanLeg[] legs) {
    super();
    this.origin = origin;
    this.destination = destination;
    this.legs = legs;
  }  
  public Plan(PlanLeg leg) {
    super();
    this.legs = new PlanLeg[]{leg};
  }
  
  public Plan(Point2D origin, Point2D destination) {
    this(origin, destination, new PlanLeg[0]);
  }
  
  @Override
  public int compareTo(Plan o) {
    return compareFloats(walkDistance, o.walkDistance);
  }

  public PlanLeg[] getLegs() {
    return legs;
  }  
  

  public Point2D getOrigin() {
    return origin;
  }
  public Point2D getDestination() {
    return destination;
  }
  public float getWalkDistance() {
    return walkDistance;
  }
  
  public float getLastWalkDistance() {
    return lastWalkDistance;
  }
    
  public int getDepartureTime() {
    return departureTime;
  }
  public void setDepartureTime(int departureTime) {
    this.departureTime = departureTime;
  }
  public void setDistances(Metric metric) {
    Point2D previous = origin;
    walkDistance = 0;
    for(PlanLeg leg: legs) {
      float d = metric.distance(previous, leg.getStop1());
      leg.setDistanceBefore(d);
      walkDistance += d;
      previous = leg.getStop2();
    }
    lastWalkDistance = metric.distance(previous,  destination);
    walkDistance += lastWalkDistance;
  }  
  public float getDuration() {
    return duration;
  }
  public void setDuration(float time) {
    this.duration = time;
  }  
  
  public int getArrivalTime() {
    return arrivalTime;
  }
  public void setArrivalTime(int arrivalTime) {
    this.arrivalTime = arrivalTime;
  }
  public String toString2() {
    StringBuilder buf = new StringBuilder();
    buf.append(Schedule.formatDuration((int) getDuration()));
    buf.append( " " );
    buf.append( String.valueOf((int) getWalkDistance()));
    buf.append( " " );
    
    for(PlanLeg leg: getLegs()) {
      buf.append(leg.shortString());
      buf.append( " -> " );
    }
    buf.append( "(" + (int) getLastWalkDistance() + ")");
    return buf.toString();
  }

  public String getLabel() {
    StringBuilder buf = new StringBuilder();
    for(PlanLeg leg: getLegs()) {
      if ( buf.length() > 0 ) {
        buf.append( " -> " );        
      }
      buf.append(leg.getRoute().getLabel());
    }
    return buf.toString();
  }
  
  @Override
  public String toString() {
    StringBuilder buf = new StringBuilder();
    for(PlanLeg leg: getLegs()) {
      buf.append(leg.shortLabel());
      buf.append( " -> " );        
    }
    buf.append( Formatting.straightDistance(getLastWalkDistance()));
    buf.append( " " );
    buf.append(Schedule.formatTime(getArrivalTime()/60));
    return buf.toString();
  }
  
  public String getDescription() {
    StringBuilder buf = new StringBuilder();
    boolean first = true;
    for(PlanLeg leg: getLegs()) {
      if ( ! first ) {
        buf.append( "-> " );        
      }
      buf.append(leg);
      first = false;
    }
    buf.append( "(" + (int) getLastWalkDistance() + ")");
    buf.append( " " );
    buf.append(Schedule.formatTime(getArrivalTime()/60));
    return buf.toString();
  }
  
}

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

import org.melato.bus.model.Route;
import org.melato.bus.model.RouteManager;
import org.melato.bus.model.Schedule;

/** A Leg at a particular schedule time. */
public class LegTime implements Comparable<LegTime>, Serializable {
  private static final long serialVersionUID = 1L;
  private Route route;
  public Leg leg;
  /** This is the scheduled time that the route starts from its starting point, in minutes. */
  int     time;
  /** Whether this is the last leg or not.  Provided as a convenience to a list display. */
  public boolean last;
  public LegTime(Leg leg, int time, RouteManager routeManager) {
    super();
    this.leg = leg;
    this.time = time;
    this.route = routeManager.getRoute(leg.getRouteId());
  }
      
  @Override
  public int compareTo(LegTime another) {
    return getTime1() - another.getTime1();
  }

  /** Seconds from midnight */
  int getTime1() {
    return time * 60 + leg.getStop1().getSecondsFromStart();
  }
  
  /** Seconds from midnight */
  int getTime2() {
    if ( leg.getStop2() != null) {
      return time * 60 + leg.getStop2().getSecondsFromStart();
    } else {
      return getTime1();
    }
  }
    
  public Route getRoute() {
    return route;
  }

  public Leg getLeg() {
    return leg;
  }

  public int getTime() {
    return time;
  }

  @Override
  public String toString() {
    StringBuilder buf = new StringBuilder();
    //buf.append( (leg.index+1) + " " );
    buf.append( route.getLabel());
    buf.append( " " );
    buf.append( Schedule.formatTime(getTime1()/60));
    buf.append( " -> " );
    buf.append( Schedule.formatTime(getTime2()/60));
    return buf.toString();
  }
}

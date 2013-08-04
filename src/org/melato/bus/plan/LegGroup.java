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
import java.util.List;

import org.melato.bus.model.RStop;
import org.melato.bus.model.RouteId;
import org.melato.bus.model.RouteManager;
import org.melato.bus.model.Stop;

/** A leg group is a leg and equivalent legs that have the same start/end stops. */
public class LegGroup implements Serializable {
  private static final long serialVersionUID = 1L;
  public Leg leg;
  private Leg[] equivalentLegs;
  
  public Leg getLeg() {
    return leg;
  }
  public Leg[] getLegs() {
    return equivalentLegs;
  }
  public LegGroup(Leg leg) {
    super();
    this.leg = leg;
  }  
  
  public RouteId getRouteId() {
    return leg.getRouteId();
  }
  public Stop getStop1() {
    return leg.getStop1();
  }
  public Stop getStop2() {
    return leg.getStop2();
  }
  public void setStop1(Stop stop1) {
    leg.setStop1(stop1);
    equivalentLegs = null;
  }
  public void setStop2(Stop stop2) {
    leg.setStop2(stop2);
    equivalentLegs = null;
  }
  public RStop getRStop1() {
    return leg.getRStop1();
  }
  Leg[] findEquivalentLegs(RouteManager routeManager) {
    if ( leg.getStop2() != null) {
      List<Leg> legs = routeManager.getLegsBetween(leg.getStop1().getSymbol(), leg.getStop2().getSymbol());
      return legs.toArray(new Leg[0]);
    } else {
      return new Leg[] {leg};
    }
  }
  public Leg[] getEquivalentLegs(RouteManager routeManager) {
    if ( equivalentLegs == null) {
      equivalentLegs = findEquivalentLegs(routeManager);
    }
    return equivalentLegs;
  }
  @Override
  public String toString() {
    return leg.toString();
  }
  
}


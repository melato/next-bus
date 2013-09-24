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

import org.melato.bus.model.Route;
import org.melato.bus.model.RouteManager;

public class SequenceLegAdapter implements LegAdapter {
  private SequenceItinerary.Leg leg;
  private SequenceItinerary.TransitLeg transit;
  private SequenceItinerary.WalkLeg walk;
  private RouteManager routeManager;

  
  public SequenceLegAdapter(SequenceItinerary.Leg leg, RouteManager routeManager) {
    super();
    this.routeManager = routeManager;
    this.leg = leg;
    if ( leg instanceof SequenceItinerary.TransitLeg ) {
      transit = (SequenceItinerary.TransitLeg) leg;
    }
    if ( leg instanceof SequenceItinerary.WalkLeg ) {
      walk = (SequenceItinerary.WalkLeg) leg;
    }
  }

  @Override
  public String getLabel() {
    Route route = routeManager.getRoute(transit.leg.routeId);
    return route.getLabel();
  }

  @Override
  public String getFromName() {
    return transit.leg.getStop1().getName();
  }

  @Override
  public String getToName() {
    return transit.leg.getStop2().getName();
  }

  @Override
  public int getStartTime() {
    return leg.getStartTime();
  }

  @Override
  public int getEndTime() {
    return leg.getEndTime();
  }

  @Override
  public int getDuration() {
    return leg.getDuration();
  }

  @Override
  public int getDiffTime() {
    return transit != null ? transit.diffTime : -1;
  }

  @Override
  public float getDistance() {
    return leg.distance;
  }

  @Override
  public boolean isWalk() {
    return walk != null;
  }
}


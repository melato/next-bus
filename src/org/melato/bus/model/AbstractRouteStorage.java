/*-------------------------------------------------------------------------
 * Copyright (c) 2012, Alex Athanasopoulos.  All Rights Reserved.
 * alex@melato.org
 *-------------------------------------------------------------------------
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *-------------------------------------------------------------------------
 */
package org.melato.bus.model;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.melato.gps.Point;
import org.melato.gpx.Waypoint;

/**
 * Provides dummy implementations of non-essential RouteStorage methods.
 * @author Alex Athanasopoulos
 */
public abstract class AbstractRouteStorage implements RouteStorage {

  @Override
  public MarkerInfo loadMarker(String symbol) {
    throw new UnsupportedOperationException();
  }

  
  @Override
  public void iterateNearbyRoutes(Point point, float latitudeDifference,
      float longitudeDifference, Collection<RouteId> collector) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void iterateNearbyStops(Point point, float latDiff, float lonDiff,
      Collection<Waypoint> collector) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void iterateAllRouteStops(RouteStopCallback callback) {
    throw new UnsupportedOperationException();
  }

  
  @Override
  public String getUri(RouteId routeId) {
    return null;
  }


  @Override
  public List<Route> loadPrimaryRoutes() {
    return Collections.emptyList();
  }
    
}

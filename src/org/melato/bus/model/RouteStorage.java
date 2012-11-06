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
import java.util.List;

import org.melato.gps.Point;
import org.melato.gpx.Waypoint;

/**
 * Provides low-level database access to routes.
 * @author Alex Athanasopoulos
 *
 */
public interface RouteStorage {
  /** Load a list of all routes. */
  List<Route> loadRoutes();

  /** Load the primary routes. */
  List<Route> loadPrimaryRoutes();
  
  /** Load a single route. */
  Route loadRoute(RouteId routeId);
  /** Load a route's schedule (for all days of the week) */
  Schedule loadSchedule(RouteId routeId);
  /** Load a route's stops as a list of consecutive waypoints. */
  List<Waypoint> loadWaypoints(RouteId routeId);
  /** Load information about a single stop. */
  MarkerInfo loadMarker(String symbol);
  /** Iterate over all stops that are within a certain latitude and longitude difference from a point.
   * The waypoint's links should contain the relevant route-ids.
   * */
  void iterateNearbyStops(Point point, float latitudeDifference, float longitudeDifference, Collection<Waypoint> collector);
  
  void iterateAllRouteStops(RouteStopCallback callback);

  /** Iterate over all routes that are within a certain latitude and longitude difference from a point.
   * The waypoint's links should contain the relevant route-ids.
   * */
  void iterateNearbyRoutes(Point point, float latitudeDifference, float longitudeDifference, Collection<RouteId> collector);
  /**
   * Return the original web URL for the route at the route provider's web site.
   * @param route
   * @return
   */
  String getUri(RouteId route);
}

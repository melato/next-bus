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
package org.melato.bus.model.cache;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.melato.bus.model.RouteId;
import org.melato.bus.model.RouteStopCallback;
import org.melato.gps.Point2D;

public class RoutePointsCollector implements RouteStopCallback {
  private Map<RouteId,RoutePoints> routes = new HashMap<RouteId,RoutePoints>();
  @Override
  public void add(RouteId routeId, List<Point2D> waypoints) {
    RoutePoints points = RoutePoints.createFromPoints(waypoints);
    routes.put(routeId,points);
  }
  public Map<RouteId, RoutePoints> getMap() {
    return routes;
  }
}

/*-------------------------------------------------------------------------
 * Copyright (c) 2012, Alex Athanasopoulos.  All Rights Reserved.
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
package org.melato.bus.model;

import java.util.List;

/**
 * Provides information about a system stop, which includes all the connected routes.
 * @author Alex Athanasopoulos
 *
 */
public class MarkerInfo {
  private Stop waypoint;
  private List<Route> routes;
  /** Return the stop's waypoint.
   * In addition to lat, lon, sym, name, the waypoint has a link for each of its routes, with the route-id.
   * @return
   */
  public Stop getStop() {
    return waypoint;
  }
  
  /** 
   * Return the list of routes that include this marker as a stop.
   * @return
   */
  public List<Route> getRoutes() {
    return routes;
  }
  public MarkerInfo(Stop waypoint, List<Route> routes) {
    super();
    this.waypoint = waypoint;
    this.routes = routes;
  }
}

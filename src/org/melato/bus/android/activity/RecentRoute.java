/*-------------------------------------------------------------------------
 * Copyright (c) 2012,2013 Alex Athanasopoulos.  All Rights Reserved.
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
package org.melato.bus.android.activity;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;

import org.melato.bus.client.Serialization;
import org.melato.bus.model.Route;
import org.melato.bus.model.RouteId;
import org.melato.bus.model.RouteManager;
import org.melato.bus.model.Stop;

import android.util.Log;

public class RecentRoute implements Serializable {
  private static final long serialVersionUID = 1L;
  private RouteStop routeStop;
  private Route route;
  private String stopName;   
  
  public RecentRoute(RouteStop stop, RouteManager routeManager ) {
    this.routeStop = stop;
    route = routeManager.getRoute(stop.getRouteId());
    Stop[] stops = routeManager.getStops(routeStop.getRouteId());
    stopName = routeStop.getStopName(stops);
  }
  public RouteId getRouteId() {
    return routeStop.getRouteId();
  }
  
  public String getStopName() {
    return stopName;
  }
  public void setStopName(String stopName) {
    this.stopName = stopName;
  }
  public RouteStop getRouteStop() {
    return routeStop;
  }
  public Route getRoute() {
    return route;
  }
  @Override
  public String toString() {
    if ( stopName != null ) {
      return route.getFullTitle() + " " + stopName;
    } else {
      return route.getFullTitle();
    }
  }
  public static RecentRoute[] read(File file) {
    RecentRoute[] routes = (RecentRoute[]) Serialization.read(RecentRoute[].class, file);
    if ( routes == null ) {
      routes = new RecentRoute[0];
    }
    return routes;
  }
  public static RecentRoute[] filter(RouteManager routeManager, RecentRoute[] routes ) {
    int n = 0;
    for( int i = 0; i < routes.length; i++ ) {
      Route route = routes[i].route;
      if (route != null) {
        route = routeManager.getRoute(route.getRouteId());
        routes[i].route = route;
      }
      if ( route != null) {
        n++;
      }
    }
    if ( n == routes.length )
      return routes;
    RecentRoute[] filteredRoutes = new RecentRoute[n];
    n = 0;
    for( int i = 0; i < routes.length; i++ ) {
      if ( routes[i].route != null) {
        filteredRoutes[n++] = routes[i];
      }
    }
    return filteredRoutes;
  }
  
  public static void write(RecentRoute[] routes, File file) throws IOException {
    Serialization.write(routes, file);
  }
}

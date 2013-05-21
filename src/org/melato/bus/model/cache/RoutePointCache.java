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

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.melato.bus.model.RouteId;
import org.melato.bus.model.RouteManager;
import org.melato.gps.Point2D;


/** Caches the coordinates of all routes in memory, for quick access.
 * */
public class RoutePointCache {
  private RouteManager routeManager;
  private Map<RouteId,RoutePoints> map = new HashMap<RouteId,RoutePoints>();
  private Thread loadThread;
  private boolean primaryLoaded;
  private boolean allLoaded;

  public RoutePointCache(RouteManager routeManager) {
    super();
    this.routeManager = routeManager;
  }

  /**
   * Load all routes, if they are not already loaded.
   */
  public void load(boolean all) {
    if ( allLoaded )
      return;
    if ( ! all && primaryLoaded )
      return;
    Thread thread = loadThread;
    if ( thread != null) {
      try {
        thread.join();
      } catch (InterruptedException e) {
      }
    } else {
      loadThread = Thread.currentThread();
      doLoad(all);
      primaryLoaded = true;
      if ( all ) {
        allLoaded = true;
      }
      loadThread = null;
    }
  }
  
  /**
   * Load primary or all routes.
   * If they are being loaded by another thread, wait for that thread to finish.
   * @param all
   */
  private void doLoad(boolean all) {
    RoutePointsCollector collector = new RoutePointsCollector();
    if (all) {
      routeManager.iterateAllRouteStops(collector);
    } else {
      routeManager.iteratePrimaryRouteStops(collector);
    }
    primaryLoaded = true;
    synchronized(this) {
      map = collector.getMap();
    }
  }
  
  public boolean isLoaded(boolean all) {
    return all ? allLoaded : primaryLoaded;
  }
  
  private RoutePoints loadRoute(RouteId routeId) {    
    Point2D[] stops = routeManager.getStops(routeId);
    return RoutePoints.createFromPoints(Arrays.asList(stops));
  }
  
  /**
   * Get the RoutePoints for a route, if we have them.
   * @param routeManager
   * @param routeId
   * @return
   */
  public synchronized RoutePoints getRoutePoints(RouteId routeId, boolean load) {
    RoutePoints points = map.get(routeId);
    if ( points == null && load ) {
      // if the route is not loaded, and we are not already loading all routes, load it immediately.
      // otherwise return null.  The map will be redrawn later.
      points = loadRoute(routeId);
      map.put(routeId, points);
    }
    return points;
  }  

}

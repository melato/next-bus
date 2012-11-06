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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.melato.gps.Earth;
import org.melato.gps.Point;
import org.melato.gpx.GPX;
import org.melato.gpx.Sequence;
import org.melato.gpx.Waypoint;
import org.melato.log.Clock;
import org.melato.log.Log;
import org.melato.progress.ProgressGenerator;
import org.melato.util.AbstractCollector;

/**
 * Provides read-access to routes, backed by a pluggable route database (RouteStorage).
 * For use on Java SE or Android
 * @author Alex Athanasopoulos
 *
 */
public class RouteManager {
  private RouteStorage storage;
  
  private List<Route> allRoutes;
  private List<Route> primaryRoutes;
  private Map<RouteId,Route> routeIndex;
  private RouteId cachedRouteId;
  private Route   cachedRoute;
  private List<Waypoint> cachedWaypoints;
  private Schedule cachedSchedule;
    
  public RouteManager(RouteStorage storage) {
    super();
    this.storage = storage;
  }
  
  private List<Route> compact(List<Route> list) {
    return Arrays.asList(list.toArray(new Route[list.size()]));
  }
  
  public List<Route> getRoutes() {
    if ( allRoutes == null ) {
      synchronized( this ) {
        if ( allRoutes == null ) {
          Clock clock = new Clock("getRoutes");
          allRoutes = compact(storage.loadRoutes());
          routeIndex = new HashMap<RouteId,Route>();
          for(Route route: allRoutes) {
            routeIndex.put(route.getRouteId(), route);            
          }
          Log.info(clock);
        }
      }
    }
    Log.info("allRoutes size=" + allRoutes.size());
    return allRoutes;
  }
  public Map<RouteId,Route> getRouteIndex() {
    getRoutes();
    return routeIndex;
  }
  
  public List<Route> getPrimaryRoutes() {
    if ( primaryRoutes == null ) {
      synchronized(this) {
        if ( primaryRoutes == null) {
          primaryRoutes = compact(storage.loadPrimaryRoutes());
        }
      }
    }
    return primaryRoutes;
  }
  
  public Route getRoute(RouteId routeId) {
    synchronized(this) {
      if ( isCached(routeId) && cachedRoute != null) {
        return cachedRoute;
      }
    }
    Route route = null;
    if (allRoutes != null) {
      route = routeIndex.get(routeId);
    } else {
      Log.info( "RouteManager.loadRoute: " + routeId );
      route = storage.loadRoute(routeId);
    }
    synchronized(this) {
      setCachedRouteId(routeId);
      cachedRoute = route;
    }
    return route;
  }
  
  public Schedule getSchedule(RouteId routeId) {
    synchronized(this) {
      if ( isCached(routeId) && cachedSchedule != null) {
        return cachedSchedule;
      }
    }
    Log.info( "RouteManager.loadSchedule: " + routeId );
    Schedule schedule = storage.loadSchedule(routeId);
    synchronized(this) {
      setCachedRouteId(routeId);
      cachedSchedule = schedule;
    }
    return schedule;
  }

  public Schedule getSchedule(Route route) {
    return getSchedule(route.getRouteId());
  }

  private boolean isCached(RouteId routeId) {
    return cachedRouteId != null && cachedRouteId.equals(routeId);
  }
  private synchronized void setCachedRouteId(RouteId routeId) {
    if ( ! isCached(routeId)) {
      cachedRouteId = routeId;
      cachedRoute = null;
      cachedWaypoints = null;    
      cachedSchedule = null;
    }
  }
  /**
   * Get the list or waypoints for the route.
   * Each waypoint is a route stop.
   * It defines:
   *  - lat, lon - The stops coordinates
   *  - sym - The stop symbol
   *  - name - The stop label  
   * */
  public List<Waypoint> getWaypoints(RouteId routeId) {
    synchronized(this) {
      if ( isCached(routeId) && cachedWaypoints != null) {
        return cachedWaypoints;
      }
    }
    List<Waypoint> waypoints = storage.loadWaypoints(routeId);
    synchronized(this) {
      setCachedRouteId(routeId);
      cachedWaypoints = waypoints;
    }
    return waypoints;
  }

  public List<Waypoint> getWaypoints(Route route) {
    return getWaypoints(route.getRouteId());
  }

  public GPX loadGPX(RouteId routeId) {
    List<Waypoint> waypoints = getWaypoints(routeId);
    GPX gpx = new GPX();
    org.melato.gpx.Route rte = new org.melato.gpx.Route();
    rte.path = new Sequence(waypoints);
    gpx.getRoutes().add(rte);
    return gpx;      
  }

  public GPX loadGPX(Route route) {
    return loadGPX(route.getRouteId());
  }

  public String getUri( Route route ) {
    return storage.getUri(route.getRouteId());
  }
  /**
   * Load marker information, which includes all routes that go through the given stop.
   * @param symbol
   * @return
   */
  public synchronized MarkerInfo loadMarker(String symbol) {
    return storage.loadMarker(symbol);
  }

  static class DistanceFilter extends AbstractCollector<Waypoint> {
    Collection<Waypoint> result;
    
    private Point center;
    private float distance;
    
    public DistanceFilter(List<Waypoint> result, Point center, float distance) {
      super();
      this.result = result;
      this.center = center;
      this.distance = distance;
    }

    @Override
    public boolean add(Waypoint p) {
      if ( Earth.distance(center, p) < distance ) {
        result.add(p);
        size++;
        return true;
      }
      return false;
    }    
  }
  
  public void iterateNearbyRoutes(Point point, float latitudeDifference,
      float longitudeDifference, Collection<RouteId> collector) {
    storage.iterateNearbyRoutes(point, latitudeDifference, longitudeDifference,
        collector);
  }

  public List<Waypoint> findNearbyStops(Point point, float distance) {
    List<Waypoint> result = new ArrayList<Waypoint>();
    DistanceFilter filter = new DistanceFilter(result, point, distance);
    float latDiff = Earth.latitudeForDistance(distance);
    float lonDiff = Earth.longitudeForDistance(distance, point.getLat());
    storage.iterateNearbyStops(point, latDiff, lonDiff, filter);
    return result;
  }

  

  public void iterateAllRouteStops(RouteStopCallback callback) {
    ProgressGenerator progress = ProgressGenerator.get();
    progress.setLimit( getRoutes().size() );
    storage.iterateAllRouteStops(callback);
  }

  public void benchmark() {
    iterateAllRouteStops(new RouteStopCallback() {

      @Override
      public void add(RouteId routeId, List<Point> waypoints) {
      }
      
    });
  }

  public RouteStorage getStorage() {
    return storage;
  }  
}

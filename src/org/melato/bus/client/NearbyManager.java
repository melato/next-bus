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
package org.melato.bus.client;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.melato.bus.model.RStop;
import org.melato.bus.model.Route;
import org.melato.bus.model.RouteId;
import org.melato.bus.model.RouteManager;
import org.melato.gps.Earth;
import org.melato.gps.Metric;
import org.melato.gps.Point2D;

/**
 * Provides access to nearby stops.
 * It caches the results to a file, so that subsequent calls from a nearby location are faster.
 * @author Alex Athanasopoulos
 *
 */
public class NearbyManager {
  static final float TARGET_DISTANCE = 1000f;
  /** Extra distance to cache. */
  static final float CACHE_DISTANCE = 100f;
  /** The cache file for the nearby waypoints (markers) */
  static final String NEARBY_FILE = "nearby.dat";
  /**
   * The cache file for the location used for the nearby waypoints.
   * It is a GPX file containing a single waypoint.
   * */
  static final String LOCATION_FILE = "location.dat";
  static final String LAT = "lat";
  static final String LON = "lon";
  
  private RouteManager routeManager;
  private File          cacheDir;
  
  public NearbyManager(RouteManager routeManager, File cacheDir) {
    super();
    this.routeManager = routeManager;
    this.cacheDir = cacheDir;
  }

  public Point2D getLastLocation() {
    File file = new File(cacheDir, LOCATION_FILE );
    Point2D location = null;
    if ( file.exists() ) {
      location = (Point2D) Serialization.read(Point2D.class, file);
      if ( location == null )
        file.delete();      
    }
    return location;
  }
  
  private void setLastLocation(Point2D location) {
    File file = new File(cacheDir, LOCATION_FILE );
    try {
      Serialization.write(location,  file);
    } catch( IOException e ) {
    }
  }
      
  private RStop[] filterDistance(List<RStop> waypoints, Point2D target) {
    Metric metric = routeManager.getMetric();
    RStop[] array = waypoints.toArray(new RStop[0]);
    for( RStop r: array) {
      r.setDistance(metric.distance(r.getStop(), target));
    }
    Arrays.sort(array);
    int size = 0;
    for( ; size < array.length; size++ ) {
      if ( array[size].getDistance() > TARGET_DISTANCE )
        break;
    }
    RStop[] result = new RStop[size];
    for( int i = 0; i < size; i++ ) {
      result[i] = array[i];
    }
    return result;
  }
  
  private List<RStop> readCache(Point2D location) {
    Point2D lastLocation = getLastLocation();
    File file = new File(cacheDir, NEARBY_FILE ); 
    if ( lastLocation != null && Earth.distance(lastLocation, location) < CACHE_DISTANCE ) {
      RStop[] markers = (RStop[]) Serialization.read(RStop[].class, file);
      if ( markers != null ) {
        return Arrays.asList(markers);
      } else {
        file.delete();
      }
    }
    return null;
  }

  private void writeCache(List<RStop> list, Point2D location) {
    File nearbyFile = new File(cacheDir, NEARBY_FILE );
    RStop[] markers = list.toArray(new RStop[0]);
    try {
      Serialization.write( markers, nearbyFile );
      setLastLocation(location);
    } catch( IOException e ) {
    }        
  }
  
  public RStop[] getNearbyWaypoints(Point2D location) {
    List<RStop> list = readCache(location);
    if ( list == null ) {
      // not in cache.  filter the global list
      list = routeManager.findNearbyStops(location, TARGET_DISTANCE + CACHE_DISTANCE);
      writeCache(list, location);
    }
    return filterDistance(list, location);
  }
    
  public NearbyStop[] getNearby(Point2D location) {
    RStop[] waypoints = getNearbyWaypoints(location);
    List<NearbyStop> nearby = new ArrayList<NearbyStop>();
    Set<RouteId> routes = new HashSet<RouteId>();
    Map<RouteId,Route> map = routeManager.getRouteIndex();
    Metric metric = routeManager.getMetric();
    for( RStop p: waypoints ) {
      RouteId routeId = p.getRouteId();
      if ( ! routes.contains( routeId )) {
        routes.add(routeId);
        Route route = map.get(routeId);
        if ( route != null ) {
          NearbyStop stop = new NearbyStop(p, route);
          p.setDistance(metric.distance(p.getStop(),  location));
          nearby.add(stop);
        }
      }
    }
    NearbyStop[] array = nearby.toArray(new NearbyStop[0]);
    // sort them by distance and name.
    Arrays.sort( array, new NearbyStop.Comparer() );
    return array;
  }

}

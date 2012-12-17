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
package org.melato.bus.client;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.melato.bus.model.Marker;
import org.melato.bus.model.Route;
import org.melato.bus.model.RouteId;
import org.melato.bus.model.RouteManager;
import org.melato.gps.Earth;
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
      
  private Marker[] filterDistance(List<Marker> waypoints, Point2D target) {    
    WaypointDistance[] array = WaypointDistance.createArray(waypoints, target);
    Arrays.sort(array);
    int size = 0;
    for( ; size < array.length; size++ ) {
      if ( array[size].getDistance() > TARGET_DISTANCE )
        break;
    }
    Marker[] result = new Marker[size];
    for( int i = 0; i < size; i++ ) {
      result[i] = array[i].getWaypoint();
    }
    return result;
  }
  
  private List<Marker> readCache(Point2D location) {
    Point2D lastLocation = getLastLocation();
    File file = new File(cacheDir, NEARBY_FILE ); 
    if ( lastLocation != null && Earth.distance(lastLocation, location) < CACHE_DISTANCE ) {
      Marker[] markers = (Marker[]) Serialization.read(Marker[].class, file);
      if ( markers != null ) {
        return Arrays.asList(markers);
      } else {
        file.delete();
      }
    }
    return null;
  }

  private void writeCache(List<Marker> list, Point2D location) {
    File nearbyFile = new File(cacheDir, NEARBY_FILE );
    Marker[] markers = list.toArray(new Marker[0]);
    try {
      Serialization.write( markers, nearbyFile );
      setLastLocation(location);
    } catch( IOException e ) {
    }        
  }
  
  public Marker[] getNearbyWaypoints(Point2D location) {
    List<Marker> list = readCache(location);
    if ( list == null ) {
      // not in cache.  filter the global list
      list = routeManager.findNearbyStops(location, TARGET_DISTANCE + CACHE_DISTANCE);
      writeCache(list, location);
    }
    return filterDistance(list, location);
  }
    
  public NearbyStop[] getNearby(Point2D location) {
    Marker[] waypoints = getNearbyWaypoints(location);
    List<NearbyStop> nearby = new ArrayList<NearbyStop>();
    Set<RouteId> links = new HashSet<RouteId>();
    Map<RouteId,Route> map = routeManager.getRouteIndex();
    for( Marker p: waypoints ) {
      for( RouteId link: p.getRoutes() ) {
        if ( ! links.contains( link )) {
          links.add(link);
          Route route = map.get(link);
          if ( route != null ) {
            NearbyStop stop = new NearbyStop(p, route);
            stop.setDistance(Earth.distance(p,  location));
            nearby.add(stop);
          }
        }
      }
    }
    NearbyStop[] array = nearby.toArray(new NearbyStop[0]);
    // sort them by distance and name.
    Arrays.sort( array, new NearbyStop.Comparer() );
    return array;
  }

}

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
package org.melato.bus.client;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.melato.bus.model.Route;
import org.melato.bus.model.RouteManager;
import org.melato.gps.Earth;
import org.melato.gps.Point;
import org.melato.gpx.GPX;
import org.melato.gpx.GPXParser;
import org.melato.gpx.GPXWriter;
import org.melato.gpx.Waypoint;
import org.melato.log.Clock;
import org.melato.log.Log;

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
  static final String NEARBY_FILE = "nearby.gpx";
  /**
   * The cache file for the location used for the nearby waypoints.
   * It is a GPX file containing a single waypoint.
   * */
  static final String LOCATION_FILE = "location.gpx";
  static final String LAT = "lat";
  static final String LON = "lon";
  
  private RouteManager routeManager;
  private File          cacheDir;
  
  public NearbyManager(RouteManager routeManager, File cacheDir) {
    super();
    this.routeManager = routeManager;
    this.cacheDir = cacheDir;
  }

  public Point getLastLocation() {
    File file = new File(cacheDir, LOCATION_FILE );
    if ( file.exists() ) {
      try {
        GPXParser parser = new GPXParser();
        GPX gpx = parser.parse(file);
        List<Waypoint> waypoints = gpx.getWaypoints();
        if ( ! waypoints.isEmpty() ) {
          return waypoints.get(0);
        }
      } catch( IOException e ) {
        file.delete();
      }
    }
    return null;
  }
  
  private void setLastLocation(Point location) {
    GPX gpx = new GPX();
    gpx.setWaypoints(Collections.singletonList(new Waypoint(location)));
    GPXWriter writer = new GPXWriter();
    File file = new File(cacheDir, LOCATION_FILE ); 
    try {
      writer.write(gpx,  file);
    } catch( IOException e ) {
    }
  }
      
  private Waypoint[] filterDistance(List<Waypoint> waypoints, Point target) {    
    WaypointDistance[] array = WaypointDistance.createArray(waypoints, target);
    Arrays.sort(array);
    int size = 0;
    for( ; size < array.length; size++ ) {
      if ( array[size].getDistance() > TARGET_DISTANCE )
        break;
    }
    Waypoint[] result = new Waypoint[size];
    for( int i = 0; i < size; i++ ) {
      result[i] = array[i].getWaypoint();
    }
    return result;
  }
  
  private List<Waypoint> readCache(Point location) {
    Clock clock = new Clock("readCache");
    Point lastLocation = getLastLocation();
    File file = new File(cacheDir, NEARBY_FILE ); 
    if ( lastLocation != null && Earth.distance(lastLocation, location) < CACHE_DISTANCE ) {
      try {
        GPXParser parser = new GPXParser();
        GPX gpx = parser.parse(file);
        Log.info(clock);
        return gpx.getWaypoints();
      } catch( IOException e ) {
        file.delete();
      } finally {
        
      }
    }
    return null;
  }

  private void writeCache(List<Waypoint> list, Point location) {
    File file = new File(cacheDir, NEARBY_FILE ); 
    GPX gpx = new GPX();
    gpx.setWaypoints(list);
    GPXWriter writer = new GPXWriter();
    try {
      writer.write(gpx,  file);
      setLastLocation(location);
    } catch( IOException e ) {
    }        
  }
  
  public Waypoint[] getNearbyWaypoints(Point location) {
    List<Waypoint> list = null;
    list = readCache(location);
    if ( list == null ) {
      // not in cache.  filter the global list
      Log.info( "querying database for nearby");
      list = routeManager.findNearbyStops(location, TARGET_DISTANCE + CACHE_DISTANCE);
      writeCache(list, location);
    }
    return filterDistance(list, location);
  }
    
  Map<String,Route> getRouteMap() {
    Map<String,Route> map = new HashMap<String,Route>();
    for( Route route: routeManager.getRoutes() ) {
      map.put( route.getRouteId().toString(), route);
    }
    return map;
  }
  public NearbyStop[] getNearby(Point location) {
    Log.info("getNearby location=" + location);
    Waypoint[] waypoints = getNearbyWaypoints(location);
    Log.info("getNearbyWaypoints: " + waypoints.length );
    List<NearbyStop> nearby = new ArrayList<NearbyStop>();
    Set<String> links = new HashSet<String>();
    Map<String,Route> map = getRouteMap();
    for( Waypoint p: waypoints ) {
      for( String link: p.getLinks() ) {
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
    Log.info( "nearby.length=" + array.length);
    // sort them by distance and name.
    Arrays.sort( array, new NearbyStop.Comparer() );
    return array;
  }

}

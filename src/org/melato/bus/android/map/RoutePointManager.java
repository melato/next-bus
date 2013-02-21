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
package org.melato.bus.android.map;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.melato.bus.android.Info;
import org.melato.bus.model.RouteId;
import org.melato.bus.model.RouteManager;
import org.melato.gps.Point2D;

import android.app.Activity;
import android.content.Context;

/** Caches the coordinates of all routes in memory, for quick access by the map.
 * The cache is static, so it's valid throughout the life of the app.
 * */
public class RoutePointManager {
  private static RoutePointManager instance;
  private RouteManager routeManager;
  private Map<RouteId,RoutePoints> map = new HashMap<RouteId,RoutePoints>();
  private boolean isLoading;
  private boolean allLoaded;
  /**
   * A callback to notify the map when data loading by a background thread has completed.
   * Use to redraw the map with the loaded routes.
   */
  private LoadListener loadListener;

  class LoadListener implements Runnable {
    Activity  activity;
    Runnable  action;
    
    public LoadListener(Activity activity, Runnable action) {
      super();
      this.activity = activity;
      this.action = action;
    }

    @Override
    public void run() {
      activity.runOnUiThread(action);
    }    
  }
  
  private RoutePointManager(Context context) {
    super();
    routeManager = Info.routeManager(context);
    load(false);
  }

  public synchronized static RoutePointManager getInstance(Context context) {
    if ( instance == null ) {
      instance = new RoutePointManager(context.getApplicationContext());
    }
    return instance;
  }
  
  public static void reload() {
    instance = null;
  }
  
  private void load(boolean all) {
    RoutePointsCollector collector = new RoutePointsCollector();
    if (all) {
      routeManager.iterateAllRouteStops(collector);
    } else {
      routeManager.iteratePrimaryRouteStops(collector);
    }
    synchronized(this) {
      map = collector.getMap();
    }
  }
  
  public boolean isLoaded() {
    return allLoaded;
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
  public synchronized RoutePoints getRoutePoints(RouteId routeId) {
    RoutePoints points = map.get(routeId);
    if ( points == null && ! isLoading ) {
      // if the route is not loaded, and we are not already loading all routes, load it immediately.
      // otherwise return null.  The map will be redrawn later.
      points = loadRoute(routeId);
      map.put(routeId, points);
    }
    return points;
  }  

  /** Run the specified action on the activity's UI thread, when the routes are loaded.
   * @param activity
   * @param action
   */
  public void setLoadListener(Activity activity, Runnable action) {
    loadListener = new LoadListener(activity, action);
  }
  
  private void invokeLoadListener() {
    if ( loadListener != null ) {
      loadListener.run();
    }
  }
  /**
   * Load all routes, in the background.
   * If the routes are not loaded, start a new thread that waits for the loading.
   * Then call the loadListener.
   * Otherwise call the loadListener immediately. 
   * @param activity
   * @param action
   */
  public void loadAll() {
    if ( isLoading )
      return;
    if ( allLoaded ) {
      invokeLoadListener();
    } else {
      new Thread( new RouteLoader()).start();
    }
  }
  
  class RouteLoader implements Runnable {
    public RouteLoader() {
      super();
    }

    @Override
    public void run() {
      isLoading = true;
      try {
        load(true);
      } finally {
        isLoading = false;
        allLoaded = true;
      }
      invokeLoadListener();
    }    
  }  
}

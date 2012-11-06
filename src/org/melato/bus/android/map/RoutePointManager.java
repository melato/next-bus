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
package org.melato.bus.android.map;

import java.util.HashMap;
import java.util.Map;

import org.melato.bus.android.Info;
import org.melato.bus.model.RouteId;
import org.melato.bus.model.RouteManager;

import android.app.Activity;
import android.content.Context;

/** Caches the coordinates of all routes in memory, for quick access by the map.
 * The cache is static, so it's valid throughout the life of the app.
 * */
public class RoutePointManager {
  private static RoutePointManager instance;
  private RouteManager routeManager;
  private Map<RouteId,RoutePoints> map = new HashMap<RouteId,RoutePoints>();
  private boolean loaded;

  private RoutePointManager(Context context) {
    super();
    routeManager = Info.routeManager(context); 
    new Thread(new RouteLoader()).start();
  }

  public synchronized static RoutePointManager getInstance(Context context) {
    if ( instance == null ) {
      instance = new RoutePointManager(context.getApplicationContext());
    }
    return instance;
  }
  
  class RouteLoader implements Runnable {
    public RouteLoader() {
      super();
    }

    @Override
    public void run() {
      load();
    }    
  }

  private void load() {
    RoutePointsCollector collector = new RoutePointsCollector();
    routeManager.iterateAllRouteStops(collector);
    synchronized(this) {
      map = collector.getMap();
      loaded =  true;
      this.notifyAll();
    }
  }
  
  public boolean isLoaded() {
    return loaded;
  }
  
  public boolean isLoading() {
    return ! isLoaded();
  }
  
  /**
   * Get the RoutePoints for a route, if we have them.
   * @param routeManager
   * @param routeId
   * @return
   */
  public synchronized RoutePoints getRoutePoints(RouteId routeId) {
    return map.get(routeId);
  }  

  /** Run the specified action when the routes are loaded.
   * If the routes are not loaded, start a new thread that waits for the loading.
   * Then run the action on the activity's UI thread.
   * Otherwise run the action immediately. 
   * @param activity
   * @param action
   */
  public void runWhenLoaded(Activity activity, Runnable action) {
    boolean isLoaded = false;
    synchronized (this) {
      isLoaded = isLoaded();
    }
    if ( isLoaded ) {
      action.run();
    } else {
      new Thread( new WaitForLoad(activity, action)).start();
    }
  }
  
  class WaitForLoad implements Runnable {
    Activity  activity;
    Runnable  action;
    
    public WaitForLoad(Activity activity, Runnable action) {
      super();
      this.activity = activity;
      this.action = action;
    }

    @Override
    public void run() {
      try {
        RoutePointManager rm = RoutePointManager.this;
        synchronized(rm) {
          if ( rm.isLoading() ) {
            rm.wait();
          }
        }
        activity.runOnUiThread(action);
      } catch (InterruptedException e) {
      }        
    }    
  }
  
}

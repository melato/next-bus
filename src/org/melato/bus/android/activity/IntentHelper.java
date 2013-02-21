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

import java.util.ArrayList;
import java.util.List;

import org.melato.bus.android.Info;
import org.melato.bus.model.Route;
import org.melato.bus.model.RouteGroup;
import org.melato.bus.model.RouteId;
import org.melato.bus.model.RouteManager;
import org.melato.gps.Point2D;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;


/**
 * puts and gets bus datatypes to an Intent.
 * @author Alex Athanasopoulos
 */
public class IntentHelper  {
  public static final String KEY_ROUTE_STOP = "org.melato.bus.android.routeStop";
  public static final String KEY_ROUTE = "org.melato.bus.android.route";
  public static final String KEY_ROUTE_COUNT = "org.melato.bus.android.route_count";
  public static final String KEY_LOCATION = "org.melato.bus.location";
  public static final String KEY_STOP = "org.melato.bus.stop";
  
  private Intent    intent;
  private Context   context;
  private RouteManager routeManager;

  public IntentHelper(Activity activity) {
    super();
    this.intent = activity.getIntent();
    this.context = activity;
  }
  
  public RouteManager getRouteManager() {
    if ( routeManager == null) {
      routeManager = Info.routeManager(context);
    }
    return routeManager;
  }

  public IntentHelper(Intent intent) {
    super();
    this.intent = intent;
  }

  public static void putLocation(Intent intent, Point2D p) {
    intent.putExtra(KEY_LOCATION, p);
  }
  
  public static Point2D getLocation(Intent intent) {
    return (Point2D) intent.getSerializableExtra(KEY_LOCATION);
  }
  
  public StopInfo getStopInfo() {
    return (StopInfo) intent.getSerializableExtra(KEY_STOP);
  }
  
  public void putStopInfo(StopInfo stop) {
    intent.putExtra(KEY_STOP, stop);
  }  

  public void putRoute(String key, RouteId routeId) {
    intent.putExtra(key, routeId);
  }
  
  /* RouteId vs RouteStop:
   * We prefer to use route stop, as it has additional information.
   * Always serialize RouteStop, except in the case of multiple routes.
   */
  
  public Route getRoute() {
    return getRoute(getRouteId());
  }
  
  private Route getRoute(RouteId routeId) {
    if ( routeId == null )
      return null;
    return getRouteManager().getRoute(routeId);
  }
  
  private Route getRoute(String key) {
    RouteId routeId = (RouteId) intent.getSerializableExtra(key);
    return getRoute(routeId);
  }

  public void putRoute(RouteId routeId) {
    putRouteStop(new RouteStop(routeId));
  }
  
  public void putRoute(Route route) {
    putRoute(route.getRouteId());
  }
  
  public void putRouteStop(RouteStop route) {
    intent.putExtra(KEY_ROUTE_STOP, route);
  }
  
  public RouteStop getRouteStop() {
    RouteStop routeStop = (RouteStop) intent.getSerializableExtra(KEY_ROUTE_STOP);
    /*
    if ( routeInfo == null ) {
      RouteId routeId = (RouteId) intent.getSerializableExtra(KEY_ROUTE);
      if ( routeId != null ) {
        routeInfo = new RouteInfo(routeId);
      }
    }
    */
    return routeStop;
  }
  
  public RouteId getRouteId() {
    RouteStop routeStop = getRouteStop();
    if ( routeStop != null )
      return routeStop.getRouteId();
    return null;
  }
  private String keyRoute(int index) {
    return KEY_ROUTE + "." + index;
  }

  public void putRoutes(RouteGroup group) {
    Route[] routes = group.getRoutes();
    intent.putExtra(KEY_ROUTE_COUNT, routes.length );
    for(int i = 0; i < routes.length; i++ ) {
      putRoute(keyRoute(i), routes[i].getRouteId());
    }
  }
  public List<Route> getRoutes() {
    Integer count = (Integer) intent.getSerializableExtra(KEY_ROUTE_COUNT);
    List<Route> routes = new ArrayList<Route>();
    if ( count != null ) {
      for(int i = 0; i < count; i++ ) {
        Route route = getRoute(keyRoute(i));
        if ( route != null ) {
          routes.add(route);
        }
      }
    }
    return routes;
  }
  
}
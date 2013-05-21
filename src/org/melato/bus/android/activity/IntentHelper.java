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
import org.melato.bus.model.RStop;
import org.melato.bus.model.Route;
import org.melato.bus.model.RouteGroup;
import org.melato.bus.model.RouteId;
import org.melato.bus.model.RouteManager;
import org.melato.gps.Point2D;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;


/**
 * puts and gets bus datatypes to an Intent.
 * @author Alex Athanasopoulos
 */
public class IntentHelper  {
  public static final String KEY_RSTOP = "org.melato.bus.android.rstop";
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
    putRStop(new RStop(routeId));
  }
  
  public void putRoute(Route route) {
    putRoute(route.getRouteId());
  }
  
  public void putRStop(RStop rstop) {
    intent.putExtra(KEY_RSTOP, rstop);
  }
  
  public RStop getRStop() {
    return (RStop) intent.getSerializableExtra(KEY_RSTOP);
  }
  public RouteId getRouteId() {
    RStop rstop = getRStop();
    if ( rstop != null )
      return rstop.getRouteId();
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
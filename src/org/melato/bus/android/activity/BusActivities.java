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
package org.melato.bus.android.activity;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.melato.bus.android.Info;
import org.melato.bus.android.R;
import org.melato.bus.android.app.BusPreferencesActivity;
import org.melato.bus.android.map.RouteMapActivity;
import org.melato.bus.model.Route;
import org.melato.bus.model.RouteId;
import org.melato.bus.model.RouteManager;
import org.melato.bus.model.xml.RouteHandler;
import org.melato.bus.model.xml.RouteWriter;
import org.melato.util.MRU;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.MenuItem;


/**
 * @author Alex Athanasopoulos
 */
public class BusActivities  {
  public static final int MRU_SIZE = 10;
  
  private static Class<? extends Activity> defaultView = ScheduleActivity.class;
  
  MRU<Route> mru;
  
  private Context context;

  private IntentHelper intentHelper;
  
  public BusActivities(Activity activity) {
    super();
    this.context = activity;    
    intentHelper = new IntentHelper(activity);
  }
  
  private RouteManager routeManager;
  
  
  public RouteManager getRouteManager() {
    if ( routeManager == null ) {
      routeManager = Info.routeManager(context);
    }
    return routeManager;
  }

  
  public RouteId getRouteId() {
    return intentHelper.getRouteId();
  }
  public Route getRoute() {
    return intentHelper.getRoute();
  }

  public void showRoute(Route route, RouteStop stop, Class<? extends Activity> activity) {
    getRecentRoutes().add(route);
    saveRecentRoutes();
    Intent intent = new Intent(context, activity);
    new IntentHelper(intent).putRouteStop(stop);
    context.startActivity(intent);    
  }
  public void showRoute(Route route, Class<? extends Activity> activity) {
    RouteStop stop = intentHelper.getRouteStop();
    if ( stop != null && ! route.getRouteId().equals(stop.getRouteId())) {
      stop = new RouteStop(route.getRouteId());
    }
    showRoute( route, stop, activity);
  }
  
  public void showRoute(Route route, RouteStop stop) {
    showRoute(route, stop, defaultView );      
  }
  public void showRoute(Route route) {
    showRoute(route, new RouteStop(route.getRouteId()));
  }
  
  public void showInBrowser(Route route) {
    Uri uri = Uri.parse(Info.routeManager(context).getUri(route));
    Intent browserIntent = new Intent(Intent.ACTION_VIEW, uri);
    context.startActivity(browserIntent);   
   }
  
  public boolean onOptionsItemSelected(MenuItem item) {
    boolean handled = false;
    Route route = getRoute();

    switch (item.getItemId()) {
      case R.id.recent_routes:
        RoutesActivity.showRecent(context);
        break;
      case R.id.all_routes:
        RoutesActivity.showAll(context);
        break;
      case R.id.schedule:
        defaultView = ScheduleActivity.class;
        showRoute(route, ScheduleActivity.class);
        handled = true;
        break;
      case R.id.stops:
        defaultView = StopsActivity.class;
        showRoute(route, StopsActivity.class);
        handled = true;
        break;
      case R.id.map:
        if ( route != null ) {
          defaultView = RouteMapActivity.class;
          showRoute(route, RouteMapActivity.class);
        } else {
          context.startActivity(new Intent(context, RouteMapActivity.class));    
        }
        handled = true;
        break;
      case R.id.pref:
        handled = true;
        context.startActivity( new Intent(context, BusPreferencesActivity.class));    
        break;
      /*
      case R.id.benchmark:
        handled = true;
        getRouteManager().benchmark();
        break;
      */
      case R.id.browse:
        showInBrowser(route);
        handled = true;
        break;
      case R.id.all_schedules:
        showRoute(route, SchedulesActivity.class);
        handled = true;
        break;
      default:
        break;
    }
    return handled;
  } 

  private File recentRoutesFile() {
    File cacheDir = context.getCacheDir();
    return new File(cacheDir, "recent-routes.xml");
  }
  public MRU<Route> getRecentRoutes() {
    if ( mru != null ) {
      return mru;
    }
    mru = new MRU<Route>(MRU_SIZE);
    try {
      List<Route> routes = RouteHandler.parseRoutes(recentRoutesFile());
      for( Route route: routes ) {
        mru.add(mru.size(), route);
      }
    } catch(Exception e) {
    }
    return mru;
  }
  void saveRecentRoutes() {
    if ( mru == null ) {
      return;
    }
    try {
      RouteWriter writer = new RouteWriter();
      writer.writeRoutes(mru, recentRoutesFile());
    } catch( IOException e ) {
      throw new RuntimeException(e);
    }
  }
}
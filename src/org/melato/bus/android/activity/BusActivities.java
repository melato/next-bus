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

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.melato.bus.android.Info;
import org.melato.bus.android.R;
import org.melato.bus.android.app.BusPreferencesActivity;
import org.melato.bus.android.map.RouteMapActivity;
import org.melato.bus.model.RStop;
import org.melato.bus.model.Route;
import org.melato.bus.model.RouteId;
import org.melato.bus.model.RouteManager;
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
  
  MRU<RecentRoute> mru;
  
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

  public void showRoute(RStop rstop, Class<? extends Activity> activity) {
    addRecent(rstop);
    Intent intent = new Intent(context, activity);
    new IntentHelper(intent).putRStop(rstop);
    context.startActivity(intent);    
  }
  
  public void showRoute(Route route, Class<? extends Activity> activity) {
    RStop stop = intentHelper.getRStop();
    if ( stop != null && ! route.getRouteId().equals(stop.getRouteId())) {
      stop = new RStop(route.getRouteId());
    }
    showRoute(stop, activity);
  }
  
  public void showRoute(RStop rstop) {
    showRoute(rstop, defaultView);
  }  
  
  public void showRoute(Route route) {
    showRoute(new RStop(route.getRouteId()));
  }
  
  public void showInBrowser(Route route) {
    Uri uri = Uri.parse(Info.routeManager(context).getUri(route));
    Intent browserIntent = new Intent(Intent.ACTION_VIEW, uri);
    context.startActivity(browserIntent);   
   }

  public void showNearby() {
    RStop rstop = intentHelper.getRStop();
    if ( rstop != null && rstop.getStop() != null) {
      NearbyActivity.start(context, rstop.getStop());
      return;
    }
    context.startActivity(new Intent(context, NearbyActivity.class));    
  }
  
  public boolean onOptionsItemSelected(MenuItem item) {
    boolean handled = false;
    Route route = getRoute();

    switch (item.getItemId()) {
      case R.id.recent_routes:
        RoutesActivity.showRecent(context);
        break;
      case R.id.nearby:
        showNearby();
        break;
      case R.id.all_routes:
        RoutesActivity.showAll(context);
        break;
      case R.id.schedule:
        if ( route != null ) {
          defaultView = ScheduleActivity.class;
          showRoute(route, ScheduleActivity.class);
          handled = true;
        }
        break;
      case R.id.stops:
        if ( route != null ) {
          defaultView = StopsActivity.class;
          showRoute(route, StopsActivity.class);
          handled = true;
        }
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
      case R.id.fast_gps:
        Info.trackHistory(context).setFast();
        break;
      default:
        break;
    }
    return handled;
  } 

  public File recentRoutesFile() {
    File cacheDir = context.getCacheDir();
    return new File(cacheDir, "recent-routes.dat");
  }
  private void addRecent(RStop rstop) {
    RecentRoute route = new RecentRoute(rstop, getRouteManager());
    MRU<RecentRoute> recent = getRecentRoutes();
    int n = recent.size();
    RouteId routeId = rstop.getRouteId();
    for( int i = n - 1; i >= 0; i-- ) {
      RecentRoute s = recent.get(i);
      if ( routeId.equals( s.getRouteId())) {
        recent.remove(i);
      }
    }
    mru.add(0, route);
    saveRecentRoutes();
  }
  public MRU<RecentRoute> getRecentRoutes() {
    if ( mru == null ) {
      mru = new MRU<RecentRoute>(MRU_SIZE);
      try {
        RecentRoute[] routes = RecentRoute.read(recentRoutesFile());
        for( RecentRoute route: routes ) {
          mru.add(mru.size(), route);
        }
      } catch(Exception e) {
      }
    }
    return mru;
  }
  private void saveRecentRoutes() {
    if ( mru == null ) {
      return;
    }
    try {
      RecentRoute.write(mru.toArray(new RecentRoute[0]), recentRoutesFile());
    } catch( IOException e ) {
      throw new RuntimeException(e);
    }
  }
  public boolean hasRecentRoutes() {
    List<RecentRoute> recent = getRecentRoutes();
    return recent.size() > 0;    
  }
}
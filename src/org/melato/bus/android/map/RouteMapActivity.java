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

import org.melato.android.AndroidLogger;
import org.melato.android.gpx.map.GMap;
import org.melato.bus.android.Info;
import org.melato.bus.android.R;
import org.melato.bus.android.activity.BusActivities;
import org.melato.bus.android.activity.Help;
import org.melato.bus.android.activity.IntentHelper;
import org.melato.bus.android.app.HelpActivity;
import org.melato.bus.model.RStop;
import org.melato.bus.model.Route;
import org.melato.bus.model.RouteId;
import org.melato.bus.model.Stop;
import org.melato.gps.Point2D;
import org.melato.log.Log;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;

/** An activity that displays a map with one or more routes. */
public class RouteMapActivity extends MapActivity {
  private BusActivities activities;
  private MapView map;
  private BaseRoutesOverlay routesOverlay;
  private boolean isShowingAll;
  private String title;
  static int defaultZoom = 15;
  private Route route;
  
  @Override
  protected boolean isRouteDisplayed() {
    return false;
  }
  
  public GeoPoint getCurrentLocation() {
    LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
    Location loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
    return GMap.geoPoint(loc);
  }
  
  @Override
  public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      Log.setLogger(new AndroidLogger(this));      
      activities = new BusActivities(this);
      routesOverlay = new RoutesOverlay(this);
      route = activities.getRoute();
      GeoPoint center = null;
      if ( route != null ) {
        title = route.getFullTitle();
        setTitle(title);
        routesOverlay.addRoute(route.getRouteId());
        routesOverlay.setSelectedRoute(route.getRouteId());
        IntentHelper intentHelper = new IntentHelper(this);
        RStop rstop = intentHelper.getRStop();
        Stop[] stops = Info.routeManager(this).getStops(route);
        int index = rstop.getStopIndex();
        if ( index >= 0 ) {
          routesOverlay.setSelectedStop(stops[index]);
          center = GMap.geoPoint(stops[index]);
        } else if ( stops.length > 0 ) {
          center = GMap.geoPoint(stops[0]);
        }
      }
      setContentView(R.layout.map);
      map = (MapView) findViewById(R.id.mapview);
      map.setBuiltInZoomControls(true);
      
      MapController mapController = map.getController();
      mapController.setZoom(defaultZoom);
      if ( center == null ) {
        Point2D dbCenter = activities.getRouteManager().getCenter();
        if ( dbCenter != null) {
          center = GMap.geoPoint(dbCenter);
        }
      }
      if ( center != null ) {
        mapController.setCenter(center);
      }
      map.getOverlays().add(routesOverlay);
      RoutePointManager.getInstance(this);
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    defaultZoom = map.getZoomLevel();
  }
  
    
  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.map_menu, menu);
    HelpActivity.addItem(menu, this, Help.MAP);
    if ( activities.getRoute() == null) {
      int[] ids = new int[] { R.id.schedule, R.id.stops };
      for( int id: ids ) {
        MenuItem item = menu.findItem(id);
        if ( item != null ) {
          item.setEnabled(false);
        }
      }
    }
    configurePinMenu(menu.findItem(R.id.pin));
    return true;
  }
  
  private void configurePinMenu(MenuItem pinMenu) {
    if ( route != null ) {
      boolean isPinned = BaseRoutesOverlay.isPinned(route.getRouteId());
      pinMenu.setEnabled(true);
      if (isPinned ) {
        pinMenu.setTitle(R.string.unpin_route);
      } else {
        pinMenu.setTitle(R.string.pin_route);
      }
    } else {
      pinMenu.setEnabled(false);
      pinMenu.setTitle(R.string.pin_route);
    }
  }
  
  class OnRoutesLoaded implements Runnable {
    @Override
    public void run() {
      if ( title != null )
        setTitle(title);
      else {
        setTitle(R.string.nearby_routes);
      }
      map.invalidate();
    }    
  }
  void showAllRoutes() {
    if ( ! isShowingAll ) {
      setTitle(R.string.loading);
    }
    routesOverlay.refresh(map);
    if ( ! isShowingAll ) {
      isShowingAll = true;
      RoutePointManager rm = RoutePointManager.getInstance(this);
      rm.setLoadListener(this, new OnRoutesLoaded());
      rm.loadAll();
    }
  }
  
  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    boolean handled = false;
    switch(item.getItemId()) {
      case R.id.refresh:
        showAllRoutes();
        handled = true;
        break;
      case R.id.pin:
        if ( route != null ) {
          RouteId routeId = route.getRouteId();
          boolean isPinned = BaseRoutesOverlay.isPinned(routeId);
          if ( isPinned ) {
            BaseRoutesOverlay.unpinRoute(routeId);
          } else {
            BaseRoutesOverlay.pinRoute(routeId);
          }
          configurePinMenu(item);
          map.invalidate();
        }
        handled = true;
        break;
      case R.id.unpin_all:
        BaseRoutesOverlay.unpinAll();
        handled = true;
        break;
    }
    if ( handled )
      return true;
    return activities.onOptionsItemSelected(item);
  }
    
}

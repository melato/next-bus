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

import org.melato.android.gpx.map.GMap;
import org.melato.bus.android.R;
import org.melato.bus.android.activity.BusActivities;
import org.melato.bus.android.app.HelpActivity;
import org.melato.bus.model.Route;

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
  private static int defaultZoom = 15;
  
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
      activities = new BusActivities(this);
      routesOverlay = new RoutesOverlay(this);
      Route route = activities.getRoute();
      if ( route != null ) {
        title = route.getFullTitle();
        setTitle(title);
        routesOverlay.addRoute(route.getRouteId());
        routesOverlay.setSelectedRoute(route.getRouteId());
      }

      setContentView(R.layout.map);
      map = (MapView) findViewById(R.id.mapview);
      map.setBuiltInZoomControls(true);
      
      MapController mapController = map.getController();
      mapController.setZoom(defaultZoom);
      GeoPoint center = routesOverlay.getCenter();
      if ( center == null ) {
        center = new GeoPoint( 37975086, 23735683); // hardcoded Syntagma Square.  Should move to the database.
      }
      if ( center != null ) {
        mapController.setCenter(center);
      }
      map.getOverlays().add(routesOverlay);
      setTitle(R.string.loading);
      RoutePointManager rm = RoutePointManager.getInstance(this);
      rm.runWhenLoaded(this, new OnRoutesLoaded());
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
    HelpActivity.addItem(menu, this, R.string.help_map);
    return true;
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
      rm.runWhenLoaded(this, new OnRoutesLoaded());
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
    }
    if ( handled )
      return true;
    return activities.onOptionsItemSelected(item);
  }
    
}

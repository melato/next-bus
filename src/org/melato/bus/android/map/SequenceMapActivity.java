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

import java.util.ArrayList;
import java.util.List;

import org.melato.android.gpx.map.GMap;
import org.melato.bus.android.Info;
import org.melato.bus.android.R;
import org.melato.bus.android.activity.Keys;
import org.melato.bus.android.activity.SequenceActivities;
import org.melato.bus.model.RouteManager;
import org.melato.bus.model.Stop;
import org.melato.bus.model.cache.RoutePoints;
import org.melato.bus.otp.OTP;
import org.melato.bus.otp.PlanConverter;
import org.melato.bus.otp.PlanConverter.MismatchException;
import org.melato.bus.plan.LegGroup;
import org.melato.bus.plan.RouteLeg;
import org.melato.bus.plan.Sequence;
import org.melato.gps.GlobalDistance;
import org.melato.gps.Metric;
import org.melato.gps.Point2D;

import android.content.Intent;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;

/** An activity that displays a map of a sequence. */
public class SequenceMapActivity extends MapActivity {
  private Sequence sequence;
  private OTP.Itinerary itinerary;
  private MapView map;
  private Rect boundary;
  private GeoPoint center;
  @Override
  protected boolean isRouteDisplayed() {
    return true;
  }

  Rect getBoundary(Rect boundary, List<GeoPoint> path) {
    if ( path.isEmpty()) {
      return boundary;
    }
    Rect r = null;
    if ( boundary == null ) {
      r = new Rect();
      GeoPoint p = path.get(0);
      r.top = r.bottom = p.getLatitudeE6();
      r.left = r.right = p.getLongitudeE6();      
    } else {
      r = new Rect(boundary);
    }
    for(GeoPoint p: path) {
      int lat = p.getLatitudeE6();
      int lon = p.getLongitudeE6();
      r.bottom = Math.min(r.bottom,  lat);
      r.top = Math.max(r.top,  lat);
      r.left = Math.min(r.left,  lon);
      r.right = Math.max(r.right,  lon);
    }
    return r;
  }
  Rect getBoundary(RoutePath[] paths) {
    Rect boundary = null;
    for(RoutePath path: paths) {
      boundary = getBoundary(boundary, path.points);
    }
    return boundary;
  }
  
  RoutePath[] loadPaths(Sequence sequence) {
    RouteManager routeManager = Info.routeManager(SequenceMapActivity.this);
    RoutePointManager routePointManager = RoutePointManager.getInstance(this);
    List<RoutePath> paths = new ArrayList<RoutePath>();
    for( LegGroup leg: sequence.getLegs()) {
      RouteLeg t = leg.leg;
      RoutePoints routePoints = routePointManager.getRoutePoints(t.getRouteId());
      RoutePath path = new RoutePath();
      path.route = routeManager.getRoute(t.getRouteId());
      Stop stop2 = t.getStop2();
      int index2 = stop2 != null ? stop2.getIndex() : routePoints.size() - 1;
      path.points = new RoutePointsGeoPointList(routePoints,
          t.getStop1().getIndex(), index2); 
      paths.add(path);
    }
    return paths.toArray(new RoutePath[0]);
  }
  
  class LoadTask extends AsyncTask<Sequence,Void,RoutePath[]> {
    @Override
    protected RoutePath[] doInBackground(Sequence... params) {
      return loadPaths(params[0]);
    }

    @Override
    protected void onPostExecute(RoutePath[] paths) {
      setPaths(paths);
    }
  }
  
  public static int computeZoom(float diameter) {
    int baseZoom = 14;
    float baseDistance = 5000f;
    if ( diameter < baseDistance ) {
      return baseZoom;
    }
    int z = baseZoom - Math.round((float) (Math.log(diameter/baseDistance ) / Math.log(2)));
    if ( z < 1 )
      z = 1;
    return z;
  }
  int computeZoom(Rect boundary) {
    Point2D p1 = GMap.point(new GeoPoint(boundary.top, boundary.left));
    Point2D p2 = GMap.point(new GeoPoint(boundary.bottom, boundary.right));
    Metric metric = new GlobalDistance();
    float distance = metric.distance(p1, p2);
    return computeZoom(distance);    
  }
  void setPaths(RoutePath[] paths) {
    boundary = getBoundary(paths);
    center = new GeoPoint((boundary.bottom+boundary.top)/2, (boundary.left+boundary.right)/2);
    int zoom = computeZoom(boundary);
    map.getOverlays().add(new RoutePathsOverlay(paths)); 
    map.getController().setCenter(center);
    map.getController().setZoom(zoom);
  }
  
  
  @Override
  protected void onCreate(Bundle icicle) {
    super.onCreate(icicle);
    Intent intent = getIntent();
    sequence = (Sequence) intent.getSerializableExtra(Keys.SEQUENCE);
    if ( sequence == null) {
      itinerary = (OTP.Itinerary) intent.getSerializableExtra(Keys.ITINERARY);
      try {
        sequence = new PlanConverter(Info.routeManager(this)).convertToSequence(itinerary);
      } catch (MismatchException e) {
        Toast.makeText(this, R.string.error_convert_route, Toast.LENGTH_SHORT).show();
      }      
    }
    if ( sequence == null ) {
      finish();
    }
    setTitle( sequence.getLabel(Info.routeManager(this)));
    setContentView(R.layout.map);
    map = (MapView) findViewById(R.id.mapview);
    map.setBuiltInZoomControls(true);
    new LoadTask().execute(sequence);
  }
  
  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    if ( itinerary != null ) {
      MenuInflater inflater = getMenuInflater();
      inflater.inflate(R.menu.sequence_map_menu, menu);
      //HelpActivity.addItem(menu, this, Help.PLAN);
    }
    return true;
  }
  
  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    boolean handled = false;
    switch(item.getItemId()) {
      case R.id.list:
        if ( itinerary != null) {
          SequenceActivities.showList(this, itinerary);
        }
        handled = true;
        break;
      default:
        break;
    }
    return handled ? true : false;
  }
}

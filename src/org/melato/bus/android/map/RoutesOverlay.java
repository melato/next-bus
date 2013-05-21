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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.melato.android.gpx.map.GMap;
import org.melato.bus.android.Info;
import org.melato.bus.android.R;
import org.melato.bus.android.activity.NearbyActivity;
import org.melato.bus.android.activity.UI;
import org.melato.bus.model.Route;
import org.melato.bus.model.RouteId;
import org.melato.bus.model.RouteManager;
import org.melato.bus.model.cache.RoutePoints;
import org.melato.gps.Point2D;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Projection;

/**
 * A map overlay that displays routes.
 * @author Alex Athanasopoulos
 */
public class RoutesOverlay extends BaseRoutesOverlay {
  private RouteManager routeManager;
  private float latDiff; 
  private float lonDiff;
  /** The minimum displayed latitude */
  private float latMin;
  /** The maximum displayed latitude */
  private float latMax;
  /** The minimum displayed longitude */
  private float lonMin;
  /** The maximum displayed longitude */
  private float lonMax;
  /** The routes currently displayed. */
  private List<RouteId> routes = new ArrayList<RouteId>();
  private RoutePointManager routePointManager;
  /** The single route that is displayed more prominently. */
  private RouteId selectedRoute;
  private Point2D selectedPoint;
  /** The primary routes, which are always displayed. */
  private List<Route> primaryRoutes;
  private Map<RouteId,Route> routeIndex;
  private Context context;

	public RoutesOverlay(Context context) {
    super();
    routeManager = Info.routeManager(context);
    this.context = context;
    routeIndex = routeManager.getRouteIndex();
    primaryRoutes = routeManager.getPrimaryRoutes();
  }

	@Override
  public void addRoute(RouteId routeId) {
	  routes.add(routeId);
	}
	
  @Override
  public void setSelectedRoute(RouteId routeId) {
    selectedRoute = routeId;
  }
  
	@Override
  public void setSelectedStop(Point2D point) {
	  selectedPoint = point;
  }

  private void findBoundaries(MapView view) {
    int latSpan = view.getLatitudeSpan();
    int lonSpan = view.getLongitudeSpan();
    latDiff = ((float) latSpan) / 1E6f / 2; 
    lonDiff = ((float) lonSpan) / 1E6f / 2;
    GeoPoint center = view.getMapCenter();
    latMin = (center.getLatitudeE6() - latSpan / 2)/1e6f;
    latMax = (center.getLatitudeE6() + latSpan / 2)/1e6f;
    lonMin = (center.getLongitudeE6() - lonSpan / 2)/1e6f;
    lonMax = (center.getLongitudeE6() + lonSpan / 2)/1e6f;
	}
	
	public void refresh(MapView view) {
	  selectedRoute = null;
    routes = new ArrayList<RouteId>();
    GeoPoint center = view.getMapCenter();
    routeManager.iterateNearbyRoutes(GMap.point(center), latDiff, lonDiff, routes);
	}
	
	List<RouteId> getMapRoutes(MapView view) {
    return routes;
	}
	
	private static GeoPoint getGeoPoint(RoutePoints points, int i) {
	  return new GeoPoint((int) (1e6f*points.getLat(i)), (int)(1e6f*points.getLon(i)));
	}
	
  Path getPath(Projection projection, RoutePoints points) {
    Path path = new Path();
    int size = points.size();
    if ( size == 0 )
      return path;
    Point p = new Point();
    boolean previousInside = false;
    projection.toPixels(getGeoPoint(points, 0), p);
    path.moveTo(p.x, p.y);
    for( int i = 0; i < size; i++ ) {
      boolean inside = points.isInside(i, latMin, latMax, lonMin, lonMax);
      if ( previousInside ) {
        // draw from previous point
        projection.toPixels(getGeoPoint(points, i), p);
        path.lineTo(p.x, p.y );
      } else if ( inside && i > 0 ) {
        projection.toPixels(getGeoPoint(points, i-1), p);
        path.moveTo(p.x, p.y );          
        projection.toPixels(getGeoPoint(points, i), p);
        path.lineTo(p.x, p.y );
      } else {
        // do nothing
        // segment is outside the view area or we are at the beginning            
      }
      previousInside = inside;
    }
    return path;
  }
	
  void drawPath(Canvas canvas, Paint paint, Projection projection, RoutePoints route ) {
    Path path = getPath(projection, route);
    canvas.drawPath(path, paint);    
  }

  Map<RouteId,Integer> routeColors = new HashMap<RouteId,Integer>();
  int[] colors = new int[] { Color.BLUE, Color.RED, Color.GREEN, Color.CYAN, Color.MAGENTA};
  int colorIndex = 0;
  
  int nextColor() {
    int color = colors[colorIndex];
    colorIndex = (colorIndex+1)%colors.length;
    return color;
  }

  int getRouteColor(RouteId routeId) {
    Integer color = routeColors.get(routeId);
    if ( color == null ) {
      color = nextColor();
      routeColors.put(routeId,  color);
    }
    return color;
  }

  void drawStart(Canvas canvas, Point p, Paint paint) {
    int size = 8;
    canvas.drawLine(p.x - size, p.y, p.x, p.y - size, paint);
    canvas.drawLine(p.x, p.y - size, p.x + size, p.y, paint);
    canvas.drawLine(p.x + size, p.y, p.x, p.y + size, paint);
    canvas.drawLine(p.x, p.y + size, p.x - size, p.y, paint);
  }
  public void draw(Canvas canvas, MapView view, boolean shadow){
    super.draw(canvas, view, shadow);
    findBoundaries(view);
    Paint   paint = new Paint();
    //paint.setDither(true);
    paint.setStyle(Paint.Style.STROKE);
    if ( routes.size() > 10 ) {
      paint.setStrokeWidth(2);
    } else {
      paint.setStrokeWidth(3);
      paint.setStrokeCap(Paint.Cap.ROUND);
      //paint.setStrokeJoin(Paint.Join.ROUND);
    }
    
    Projection projection = view.getProjection();
    routePointManager = RoutePointManager.getInstance(view.getContext());
    
    // paint all primary routes
    for( Route route: primaryRoutes ) {
      paint.setColor(UI.routeColor(route.getColor()));      
      RoutePoints points = routePointManager.getRoutePoints(route.getRouteId());
      if ( points != null ) {
        drawPath(canvas, paint, projection, points);
      }
    }
    // paint all pinned routes
    for( RouteId routeId: pinnedRoutes ) {
      paint.setColor(context.getResources().getColor(R.color.pinned_route));
      RoutePoints points = routePointManager.getRoutePoints(routeId);
      if ( points != null ) {
        drawPath(canvas, paint, projection, points);
      }
    }

    // paint any non-primary routes
    for( RouteId routeId: getMapRoutes(view)) {
      Route route = routeIndex.get(routeId);
      if ( route.isPrimary()) {
        // we've already drawn it
        continue;
      }
      int color = 0;
      if ( routeId.equals(selectedRoute)) {
        color = Color.BLUE;
      } else {
        color = UI.routeColor(route.getColor());
      }
      paint.setColor(color);
      RoutePoints points = routePointManager.getRoutePoints(routeId);
      if ( points != null ) {
        drawPath(canvas, paint, projection, points);
        // if route is null, the routepoint manager is loading
        // The RouteMapActivity will be waiting for it load
        // and it will invalidate the map view, causing this to draw again.
        
        if ( routeId.equals(selectedRoute) && points.size() > 0 ) {
          Point p = new Point();
          projection.toPixels(getGeoPoint(points, 0), p);
          drawStart(canvas, p, paint);      
        }
      }
    }
    if ( selectedPoint != null ) {
      Point p = new Point();
      projection.toPixels(GMap.geoPoint(selectedPoint), p);
      canvas.drawCircle(p.x, p.y, 4, paint);      
    }
	}

  @Override
  public boolean onTap(GeoPoint geoPoint, MapView mapView) {
    NearbyActivity.start(mapView.getContext(), GMap.point(geoPoint));
    return true;
  }
  
}

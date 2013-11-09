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

import org.melato.bus.android.activity.UI;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;

import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.Projection;

public class RoutePathsOverlay extends Overlay {
  private RoutePath[] paths;
    
  public RoutePathsOverlay(RoutePath[] paths) {
    super();
    this.paths = paths;
  }

  @Override
  public void draw(Canvas canvas, MapView mapView, boolean shadow) {
    super.draw(canvas, mapView, shadow);
    Paint   paint = new Paint();
    paint.setStyle(Paint.Style.STROKE);
    paint.setStrokeWidth(3);
    paint.setStrokeCap(Paint.Cap.ROUND);
    
    Projection projection = mapView.getProjection();
    for( RoutePath routePath: paths ) {
      paint.setColor(UI.routeColor(routePath.route.getColor()));
      Path path = RoutePlotter.getPath(projection, routePath.points);
      canvas.drawPath(path, paint);

      // draw a circle at the beginning of each route
      Point p = new Point();
      projection.toPixels(routePath.points.get(0), p);
      canvas.drawCircle(p.x, p.y, 4, paint);            
    }
  }
}

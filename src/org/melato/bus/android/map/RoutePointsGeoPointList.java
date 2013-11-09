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

import java.util.AbstractList;

import org.melato.bus.model.cache.RoutePoints;

import com.google.android.maps.GeoPoint;

public class RoutePointsGeoPointList extends AbstractList<GeoPoint> {
  private RoutePoints points;
  private int offset;
  private int size;
  
  public RoutePointsGeoPointList(RoutePoints points) {
    super();
    this.points = points;
    offset = 0;
    size = points.size();
  }

  public RoutePointsGeoPointList(RoutePoints points, int stop1, int stop2) {
    super();
    this.points = points;
    offset = stop1;
    size = stop2 - stop1 + 1;
  }

  @Override
  public GeoPoint get(int index) {
    return RoutePlotter.getGeoPoint(points, offset + index);
  }

  @Override
  public int size() {
    return size;
  }

}

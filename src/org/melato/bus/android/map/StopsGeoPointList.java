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

import org.melato.bus.model.Stop;

import com.google.android.maps.GeoPoint;

public class StopsGeoPointList extends AbstractList<GeoPoint> {
  private Stop[] stops;
  private int offset;
  private int size;
  
  public StopsGeoPointList(Stop[] stops, Stop stop1, Stop stop2) {
    super();
    this.stops = stops;
    offset = stop1.getIndex();
    size = stop2.getIndex() + 1 - offset;
  }

  @Override
  public GeoPoint get(int index) {
    Stop stop = stops[offset + index];
    return new GeoPoint((int) (1e6f*stop.getLat()), (int)(1e6f*stop.getLon()));    
  }

  @Override
  public int size() {
    return size;
  }

}

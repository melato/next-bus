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
package org.melato.bus.model;

import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Iterator;

import org.melato.gps.Point2D;

public class NearbyFilter extends AbstractCollection<Marker> {
  private Collection<Marker> result;
  private Point2D target;
  private float latDelta;
  private float lonDelta;
  
  public NearbyFilter(Point2D target, float latDiff, float lonDiff, Collection<Marker> result) {
    super();
    this.result = result;
    this.target = target;
    this.latDelta = latDiff; 
    this.lonDelta = lonDiff; 
  }
  private boolean isNearby(Marker p) {
    if ( Math.abs(p.getLat() - target.getLat()) > latDelta ) {
      return false;
    }
    if ( Math.abs(p.getLon() - target.getLon()) > lonDelta ) {
      return false;
    }
    return true;
  }
  @Override
  public boolean add(Marker p) {
    if ( isNearby(p)) {
      result.add(p);
      return true;        
    }
    return false;
  }

  @Override
  public Iterator<Marker> iterator() {
    throw new UnsupportedOperationException();
  }

  @Override
  public int size() {
    throw new UnsupportedOperationException();
  }
  
}

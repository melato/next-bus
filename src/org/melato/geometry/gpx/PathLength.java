/*-------------------------------------------------------------------------
 * Copyright (c) 2012, Alex Athanasopoulos.  All Rights Reserved.
 * alex@melato.org
 *-------------------------------------------------------------------------
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *-------------------------------------------------------------------------
 */
package org.melato.geometry.gpx;

import org.melato.gps.GlobalDistance;
import org.melato.gps.Metric;
import org.melato.gps.Point2D;

/**
 * Computes the length of a sequence of waypoints, as each point is added.
 * @author Alex Athanasopoulos
 *
 */
public class PathLength {
  private Metric metric;
  private double length;
  private Point2D last;  
  
  public PathLength() {
    this(new GlobalDistance());
  }
  
  public PathLength(Metric metric) {
    this.metric = metric;
  }

  public void add(Point2D p) {
    if (last != null) {
      float d = metric.distance(last, p);
      length += d;
    }
    last = p;
  }
  public float getLength() {
    return (float) length;
  }
}

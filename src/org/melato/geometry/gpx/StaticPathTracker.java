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

import org.melato.gps.Metric;
import org.melato.gps.PointTime;

/**
 * Stateless path tracking algorithm that does not take into account location history
 * Simply finds the closest stop and then interpolates between the closest and the next closest stops.
 * It is accurate on a straight path but may jump to the wrong waypoint on circular paths.
 * @param p
 * @return
 */
public class StaticPathTracker implements TrackingAlgorithm {
  private Path path;
  private Metric metric;

  private int   nearestIndex;
  private float pathPosition;
    
  @Override
  public void clearLocation() {
    nearestIndex = -1;
    pathPosition = 0;
  }

  public StaticPathTracker() {
    super();
    setPath(new Path());
  }
  
  @Override
  public void setPath(Path path) {
    this.path = path;
    this.metric = path.getMetric();
  }

  @Override
  public int getNearestIndex() {
    return nearestIndex;
  }

  @Override
  public float getPosition() {
    return pathPosition;
  }


  @Override
  public void setLocation(PointTime p) {
    nearestIndex = path.findNearestIndex(p);
    pathPosition = findPathLength(p);
  }

  float interpolatePosition(PointTime point, int index1, int index2) {
    float d1 = metric.distance(point, path.getWaypoints()[index1]);
    float d2 = metric.distance(point, path.getWaypoints()[index2]);
    float p1 = path.getLength(index1);
    float p2 = path.getLength(index2);
    return p1 + (p2-p1)*d1/(d1+d2);
  }
  
  private float findPathLength(PointTime p) {
    if ( path.size() < 2 )
      return Float.NaN;
    int nearest = path.findNearestIndex(p);
    if ( nearest == 0 ) {
      return interpolatePosition(p, 0, 1);
    }
    if ( nearest == path.size() - 1 ) {
      return interpolatePosition(p, nearest-1, nearest );
    }
    float d1 = metric.distance(p, path.getWaypoint(nearest-1));
    float d2 = metric.distance(p, path.getWaypoint(nearest+1));
    if ( d1 < d2 ) {
      return interpolatePosition(p, nearest-1, nearest );
    } else {
      return interpolatePosition(p, nearest, nearest+1 );
    }
  }
}

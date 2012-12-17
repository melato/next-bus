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
 * PathTracker algorithm that assumes the incoming positions follow the set path.
 * It uses the previous position(s) to determine if the location is moving as expected along the path
 * At any given point, the tracker is either in-path or out-of-path and also has a current path point.
 * The current path point is the point that we are approaching.
 * At the first point, it is out-of-path and the current path point is the closest one.
 * It goes in-path, if it is near the path and it is approaching a path waypoint.
 * It goes out-of-path, if it is not  approaching any path waypoint after the current one.
 */
public class SequentialPathTracker implements TrackingAlgorithm {
  private Path path;
  private Metric metric;
  
  
  /** The last location */
  private PointTime location;
  
  /** The index of the waypoint closest to the last location in path terms */
  private int   nearestIndex = -1;
  
/** The last computed path position */ 
  private float pathPosition;
  
  /**
   * The waypoint we're currently following.
   * May differ from nearestIndex by 1
   */
  private int   currentIndex = -1;

  /** The waypoint at the current index */
  private PointTime currentWaypoint;
  
  /** The distance from location to currentWaypoint */
  private float currentDistance;
  
  private boolean inPath;
  
  @Override
  public void clearLocation() {
    location = null;
    currentIndex = -1;
    nearestIndex = -1;
    pathPosition = 0;
    currentWaypoint = null;
    currentDistance = 0;
    inPath = false;
  }

  public SequentialPathTracker() {
    super();
    setPath(new Path());
  }
  
  @Override
  public void setPath(Path path) {
    clearLocation();
    this.path = path;
    this.metric = path.getMetric();
  }

  @Override
  public int getNearestIndex() {
    if ( nearestIndex == -1 && path.size() > 0 ) {
      //nearestIndex = path.findNearestIndex(pathPosition);
    }
    return nearestIndex;
  }

  @Override
  public float getPosition() {
    return pathPosition;
  }
  
  /**
   * Whether or not we've determined we are following the path.
   */
  public boolean isInPath() {
    return inPath;
  }

  public int getCurrentIndex() {
    return currentIndex;
  }
  
  private boolean isSameLocation(PointTime p1, PointTime p2) {
    return p1.getLat() == p2.getLat() && p1.getLon() == p2.getLon();
  }
  
  private void setCurrentPosition(PointTime point, int index) {
    location = point;
    currentIndex = index;
    currentWaypoint = path.getWaypoints()[currentIndex];      
    currentDistance = metric.distance(location, currentWaypoint);
    //Log.info( "currentIndex=" + currentIndex + " waypoint=" + currentWaypoint + " distance=" + currentDistance);
  }
  
  
  /**
   * Interpolate the position between two points.
   * Either index could be out of bounds, but not both.
   * @param path
   * @param point
   * @param index1
   * @param index2
   * @return
   */
  float interpolatePosition(Path path, PointTime point, int index1, int index2) {
    if ( index1 > index2 || index2 < 0 || index1 >= path.size() ) {
      throw new IllegalArgumentException();
    }
    if ( index1 < 0 ) {
      // we are before the start.  our position is negative
      return - metric.distance(point, path.getWaypoints()[index2]);
    } else if ( index2 >= path.size() ) {
      // we are past the end.
      return path.getLength(index2) + metric.distance(point, path.getWaypoints()[index2]);
    } else {
      // we are inside the path
      float d1 = metric.distance(point, path.getWaypoints()[index1]);
      float d2 = metric.distance(point, path.getWaypoints()[index2]);
      float p1 = path.getLength(index1);
      float p2 = path.getLength(index2);
      if ( index1 + 1 == index2 ) {
        // adjust the nearest index, since we just calculated the distances
        nearestIndex = (d1 < d2) ? index1 : index2;
      }
      return p1 + (p2-p1)*d1/(d1+d2);
    }
  }
  
  void getDistances(PointTime point, int[] indexes, float[] distances) {
    for( int i = 0; i < indexes.length; i++ ) {
      distances[i] = 0;
      int index = indexes[i];
      if ( 0 <= index && index < path.size() -1 ) {
        distances[i] = metric.distance(point, path.getWaypoint(indexes[i]));
      }
    }
  }
  void setInitialLocation(PointTime point) {
    inPath = false;
    setCurrentPosition(point, path.findNearestIndex(point));
    nearestIndex = currentIndex;
    //System.out.println( "sil: " + this );
    if ( currentIndex == 0 ) {
      // assume we're before the start
      //pathPosition = -currentDistance;
      pathPosition = 0;
    } else if ( currentIndex + 1 >= path.size() ) {
      pathPosition = path.getLength();
      // assume we're past the end
      //pathPosition += currentDistance;
    } else {
      float d1 = metric.distance(point, path.getWaypoints()[currentIndex-1]);
      float d2 = metric.distance(point, path.getWaypoints()[currentIndex+1]);
      if ( d1 < d2 ) {
        pathPosition = interpolatePosition(path, point, currentIndex - 1, currentIndex);
      } else {
        pathPosition = interpolatePosition(path, point, currentIndex, currentIndex + 1);
      }
    }
    //System.out.println( "silend: " + this );
  }
    
  @Override
  public void setLocation(PointTime point) {
    if ( location != null && isSameLocation(point, location)) {
      return;
    }
    if ( location == null ) {
      setInitialLocation(point);
    } else {
      //Log.info( "seq.tracker inPath=" + inPath + " currentIndex=" + currentIndex);
      if ( inPath ) {
        float d = metric.distance(point, currentWaypoint);
        //Log.info( "distance = " + d + " currentDistance=" + currentDistance );
        if ( d <= currentDistance ) {
          // we seem to be moving towards the current waypoint
          currentDistance = d;
          location = point;
          pathPosition = interpolatePosition(path, point, currentIndex - 1, currentIndex);
          //Log.info( "approaching " + currentWaypoint );
        } else {
          // we seem to be moving away from current waypoint
          // check if we're approaching one of the following waypoints
          for( int i = currentIndex; i + 1 < path.size(); i++ ) {
            PointTime nextWaypoint = path.getWaypoints()[i+1];
            float dNext = metric.distance(point, nextWaypoint);
            float dLocationNext = metric.distance(location, nextWaypoint);
            if ( dNext < dLocationNext ) {
              // ok, we're moving closer to the nextWaypoint
              pathPosition = interpolatePosition(path, point, i, i + 1);
              setCurrentPosition(point, i+1);
              //Log.info( "moved to: " + (i+1) + " " + currentWaypoint );
              return;
            }
          }
          //Log.info( "left path");
          // we are not approaching any path waypoint.  Assume we are no longer following it.
          setInitialLocation(point);
        }
      } else {
        // decide whether we're following the path if both:
        // A) nearestDistance is smaller than the distance
        // between the nearest waypoint and either of its neighbors
        // B) at least one of the three distances is decreasing.
        // This is not very accurate, but we also check along the way
        PointTime previousLocation = location;
        setInitialLocation(point);
        if ( isNear() || isApproaching(previousLocation, point, currentIndex-1, currentIndex+1)) {
          inPath = true;
        }
      }
    }
    //nearestIndex = path.findNearestIndex(location, currentIndex-1, currentIndex+1);
  }
  
  /**
   * return true if nearestDistance is smaller than the distance
   * between the nearest waypoint and either of its neighbors
   * @param point
   * @return
   */
  private boolean isNear() {
    if ( path.size() < 2 )
      return false;    
    if ( currentIndex > 0 && currentDistance < path.getLength(currentIndex-1,currentIndex))
      return true;
    if ( currentIndex < path.size() - 1 && currentDistance < path.getLength(currentIndex,currentIndex+1)) {
      return true;
    }
    return false;
  }
  
  /**
   * Return true if current is closer than previous to any path waypoint between index1 and index2, inclusive
   * @param previous
   * @param current
   * @param index1
   * @param index2
   * @return
   */
  private boolean isApproaching(PointTime previous, PointTime current, int index1, int index2) {
    for( int index = index1; index <= index2; index++ ) {
      if ( 0 <= index && index < path.size() ) {
        PointTime p = path.getWaypoint(index);
        if ( metric.distance(current, p) < metric.distance(previous, p) )
          return true;
      }
    }
    return false;
  }

  @Override
  public String toString() {
    StringBuilder buf = new StringBuilder();
    buf.append( "current=" + currentIndex );
    buf.append( " nearest=" + nearestIndex);
    buf.append( " position=" + getPosition());
    if ( currentIndex >= 0 ) {
      buf.append( " waypoint=" + path.getWaypoint(currentIndex));      
    }
    return buf.toString();
  }
}

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

import org.melato.gps.PointTime;


/**
 * PathTracker that tracks its position by looking for pairs of consecutive waypoints p1, p2
 * so that the current position moves from p1 to p2.
 */
public class DirectionalPathTracker extends BasePathTracker2 {
  
  private int findBestPair() {
    int bestIndex = -1;
    float bestDistance = 0;
    int n = path.size() - 1;
    for( int i = 0; i < n; i++ ) {
      float pairDistance = Math.min(current.distance(i), current.distance(i+1));
      if ( (bestIndex < 0 || pairDistance < bestDistance) && (isNear(i) || isNear(i+1)) && isMoving(i, i+1)) {
        bestIndex = i;
        bestDistance = pairDistance;
      }
    }
    return bestIndex;
  }
  
  /**
   * Return true if the current position is near the specified waypoint.
   * The point is near if the distance from the waypoint is smaller
   * than the largest distance between the waypoint and its two neighbors.
   * @return
   */
  private boolean isNear(int index) {
    if ( ! isValidIndex(index) ) {
      return false;
    }
    float limit = 0;
    if ( isValidIndex(index-1) && isValidIndex(index+1)) {
      limit = Math.max(path.getLength(index, index-1), path.getLength(index, index+1));
    } else if ( isValidIndex(index-1) ) {
      limit = path.getLength(index, index-1);
    } else if ( isValidIndex(index+1) ) {
      limit = path.getLength(index, index+1);
    } else {
      return false;
    }
    return current.distance(index) < limit;
  }
  
  private boolean setBestLocation() {
    int best = findBestPair();
    if ( best >= 0 ) {
      inPath = true;
      setCurrentIndex(best);
      pathPosition = interpolatePosition(best);
      return true;
    }
    return false;
  }
  
  @Override
  public void setLocation(PointTime point) {
    //Log.info( "setLocation: " + point );
    if ( ! setCurrentLocation(point)) {
      return;
    }
    if ( inPath ) {        
      if ( isMoving( currentIndex, currentIndex+1)) {
        // we seem to be moving as expected
        pathPosition = interpolatePosition(currentIndex);
        //Log.info( "approaching " + currentWaypoint );
      } else if ( isMoving(currentIndex+1, currentIndex+2) ) {
        // move to the next pair
        setCurrentIndex(currentIndex + 1);
        pathPosition = interpolatePosition(currentIndex);
      } else if ( isMoving(currentIndex-1, currentIndex) ) {
        // backtrack to the previous pair.  Sometimes it happens near currentIndex
        setCurrentIndex(currentIndex - 1);
        pathPosition = interpolatePosition(currentIndex);
      } else {
        boolean linger = false;
        boolean near = isNear(currentIndex) || isNear(currentIndex+1);
        //Log.info( "near=" + near + " best=" + best );
        if ( near ) {
          int best = findBestPair();
          if ( best < 0 ) {
            linger = true;
          }
        }
        if ( linger ) {
          // linger here, since there is no better place to go.
          pathPosition = interpolatePosition(currentIndex);
        } else {
          // Log.info( "left path, point=" + point );
          // we are not approaching any path waypoint.  Assume we are no longer following the route.
          inPath = false;
          setInitialLocation();
        }
      }
    } else {
      if ( ! setBestLocation() ) {
        setInitialLocation();
      }
    }
    //nearestIndex = path.findNearestIndex(location, currentIndex-1, currentIndex+1);
    //Log.info( this );
  }  
}

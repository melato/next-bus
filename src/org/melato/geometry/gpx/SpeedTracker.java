/*-------------------------------------------------------------------------
 * Copyright (c) 2012,2013, Alex Athanasopoulos.  All Rights Reserved.
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

import java.util.Date;

import org.melato.gps.PointTime;

/** Computes speed and expected times of arrival on a path */
public class SpeedTracker {
  protected PathTracker tracker;
  protected long  speedStartTime;
  protected float speedStartPosition;
  protected float speed = Float.NaN;

  public SpeedTracker(PathTracker tracker) {
    super();
    this.tracker = tracker;
  }
  
  public void compute() {
    speed = computeSpeed();
  }
  
  public float getSpeed() {
    return speed;    
  }
  
  /** Get the expected time to reach the given waypoint.
   * @param pathIndex
   * @return time in seconds from the last location
   */
  public float getRemainingTime(int pathIndex) {
    speed = computeSpeed();
    float time = (tracker.getPath().getLength(pathIndex) - tracker.getPosition())/getSpeed();
    return time;
  }
  
  public Date getETA(int pathIndex) {
    float time = getRemainingTime(pathIndex);
    if ( Float.isNaN(time)) {
      return null;
    }
    PointTime location = tracker.getLocation();
    if ( location == null )
      return null;
    return new Date(location.getTime() + (long) (time * 1000)); 
  }
  
  private float computeSpeed() {
    PointTime location = tracker.getLocation();
    if (location == null)
      return Float.NaN;
    long time = location.getTime();
    if ( speedStartTime == 0 ) {
      speedStartTime = time;
      speedStartPosition = tracker.getPosition();
      return 0f;
    } else {
      time -= speedStartTime;
      float distance = tracker.getPosition() - speedStartPosition;
      return distance * 1000f / time;
    }      
  }

  @Override
  public String toString() {
    return String.valueOf(getSpeed());
  }  
}

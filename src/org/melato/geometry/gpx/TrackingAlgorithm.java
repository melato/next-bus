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
 * Computes the position of track locations along a fixed path.
 * Assuming that the locations follow the path, the position at a certain location is the distance
 * from the start of the path, along the path.
 * 
 * Classes that implement this interface compute the location of a current point on a path (route) of waypoints.
 * The computation is approximate, since the incoming locations may follow a discontinuous crazy path.
 * A practical computation can assume a smooth track, such as that followed by a city bus with frequent stops,
 * or an intercity bus with fewer stops.
 * The computation must take into consideration:
 * <ul>
 * <li>paths that are circular
 * <li>paths that cross themselves
 * <li>Hairpin paths: paths that go somewhere and return in the reverse direction
 * <li>Paths that have a duplicated subsection (For example a figure-8 path with two stops at the junction)
 * <li>Paths that have duplicate waypoints (all of the above may or may not have duplicates)
 * </ul>
 * @author Alex Athanasopoulos
 *
 */
public interface TrackingAlgorithm {

  /**
   * Specify which path to track.
   * The Path has some useful computation methods.
   * The algorithm may use them, or use just the path's waypoints.
   * @param p
   */
  void setPath(Path path);

  /**
   * Clear all location history and start as if in a new instance.
   */
  void clearLocation();
  
  /**
   * Add a new location to the track, which becomes the current location.
   * The sequence of calls to setLocation() matters because it provides a series of past locations
   * that the path may use in its algorithm.
   * @param p
   */
  void setLocation(PointTime p);
  
  /**
   * Return the index of the waypoint nearest the current location, according to path distance.
   * This is not the same as the nearest point in a straight line.
   * @return
   */
  int getNearestIndex();

  /**
   * Return the path distance of the current location from the beginning of the path.
   * @param p
   * @return
   */
  float getPosition();

}
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
 * Matches actual track locations to a fixed path.
 * @author Alex Athanasopoulos
 *
 */
public class PathTracker {
  private TrackingAlgorithm tracker;
  private Path path;
  private PointTime location;

  public PathTracker(TrackingAlgorithm tracker) {
    super();
    this.tracker = tracker;
  }

  public PathTracker() {
    this(Algorithm.newTrackingAlgorithm());
  }
  
  public TrackingAlgorithm getTracker() {
    return tracker;
  }

  public void setPath(Path path) {
    this.path = path;
    tracker.setPath(path);
  }
  
  public Path getPath() {
    return path;
  }

  public PointTime getLocation() {
    return location;
  }

  public void clearLocation() {
    this.location = null;
    tracker.clearLocation();
  }

  /**
   * Add a new location to the track, which becomes the current location.
   * The sequence of calls to setLocation() matters because it provides a series of past locations
   * that the path may use in its algorithm.
   * @param p
   */
  public void setLocation(PointTime p) {
    this.location = p;
    tracker.setLocation(p);
  }

  /**
   * Return the index of the waypoint nearest the current location, along the current path.
   * @return
   */
  public int getNearestIndex() {
    return tracker.getNearestIndex();
  }

  /**
   * Return the distance of the current location from the beginning of the path, along the path.
   * If the current location is not in the path, the current position is approximately the position of the nearest waypoint.
   * @param p
   * @return
   */
  public float getPosition() {
    return tracker.getPosition();
  }
}
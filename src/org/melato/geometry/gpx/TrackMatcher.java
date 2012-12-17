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
 * Matches a track against a route and creates a matching score that can be used to select
 * the best route that matches the track.
 * @author Alex Athanasopoulos
 *
 */
public class TrackMatcher {
  private TrackMatchingAlgorithm algorithm = new SequenceTrackMatcher();

  public TrackMatcher(PointTime[] trackWaypoints, float targetDistance) {
    algorithm.setProximityDistance(targetDistance);
    algorithm.setTrack(trackWaypoints);
  }

  /** Compute the score for a route.
   * @param route The route, specified as a sequence of waypoints.
   * @param score
   */
  public Score computeScore(PointTime[] route) {
    return algorithm.computeScore(route);
  }

  public TrackMatchingAlgorithm getAlgorithm() {
    return algorithm;
  }
}

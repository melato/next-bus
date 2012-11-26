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

import java.util.List;

import org.melato.geometry.gpx.RouteMatcher.Approach;
import org.melato.gps.PointTime;

public class SequenceTrackMatcher implements TrackMatchingAlgorithm {
  private float proximityDistance;
  private RouteMatcher matcher;
  
  @Override
  public void setProximityDistance(float targetDistance) {
    this.proximityDistance = targetDistance;
  }

  @Override
  public void setTrack(PointTime[] track) {
    matcher = new RouteMatcher(track, proximityDistance);
  }

  
  /** Compute the score for a route.
   * @param route The route, specified as a sequence of waypoints.
   * @param score
   */
  public Score computeScore(PointTime[] route) {
    Score score = new Score();
    List<Approach> approaches = matcher.match(route);
    score.setCount(approaches.size());
    return score;
  }
  
  /** Get the names of the score components.  Use for debugging. */
  @Override
  public String[] getScoreFieldNames() {
    return new String[] {
        "id", "count"};      
  }
  /** Get the score components as an array (corresponding to the field names).  Use for debugging. */
  @Override
  public Object[] getFields(Score score) {
    return new Object[] {
        score.getId(),
        score.getCount()};
  }
}

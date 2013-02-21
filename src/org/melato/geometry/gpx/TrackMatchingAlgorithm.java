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

import org.melato.gps.PointTime;

/**
 * Matches a track against a route and creates a matching score that can be used to select
 * the best route that matches the track.
 * @author Alex Athanasopoulos
 *
 */
public interface TrackMatchingAlgorithm {
  /** Set the distance to use when determining if a track point matches a route point. */
  void setProximityDistance(float targetDistance);
  
  /** Define the track to match against. */
  void setTrack(PointTime[] track);
  
  /** Compute the score for a route. */
  Score computeScore(PointTime[] route);
  /** Return true if this is the worse score possible, e.g. total mismatch. */
  boolean isMinimal(Score score);
  
  /** Determine if two scores are close enough so that a strict comparison between them may not be accurate */
  boolean areClose(Score score1, Score score2 );
  
  /** Get the names of the score fields (used for debugging). */ 
  String[] getScoreFieldNames();
  /** Get the values of the score fields (used for debugging). */ 
  Object[] getFields(Score score);
  
}

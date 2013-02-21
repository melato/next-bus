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

import java.util.List;

import org.melato.geometry.gpx.RouteMatcher.Approach;
import org.melato.gps.PointTime;

/** A track matcher that matches the track stops to the route and returns
 * the number of track points from from the first to the last match. */
public class SequencePointTrackMatcher implements TrackMatchingAlgorithm {
  private float proximityDistance;
  private RouteMatcher matcher;
  private PointTime[] track;
  
  @Override
  public void setProximityDistance(float targetDistance) {
    this.proximityDistance = targetDistance;
  }

  @Override
  public void setTrack(PointTime[] track) {
    this.track = track;
    matcher = new RouteMatcher(track, proximityDistance);
  }

  /** A score that has an integer count and a time.  Higher count is better. */ 
  public class SequenceScore extends Score {
    /** Number of matched waypoints */
    private int     matches;
    /** Number of track points */
    private int     points;
    /** Length of time in milliseconds */
    private long    time;
    /** The number of gaps in the matched route points */
    private int gaps;
    /** The number of route points not matched inside the matched length */
    private int missing;
    public SequenceScore() {
    }

    public SequenceScore(Object id) {
      super(id);
    }

    public int getMatches() {
      return matches;
    }

    public void setMatches(int waypoints) {
      this.matches = waypoints;
    }

    public int getPoints() {
      return points;
    }

    public void setPoints(int points) {
      this.points = points;
    }

    public long getTime() {
      return time;
    }

    public void setTime(long time) {
      this.time = time;
    }
    
    public int getGaps() {
      return gaps;
    }

    public void setGaps(int gaps) {
      this.gaps = gaps;
    }
    
    public int getMissing() {
      return missing;
    }

    public void setMissing(int missing) {
      this.missing = missing;
    }

    @Override
    public int compareTo(Score score) {
      SequenceScore s = (SequenceScore)score;
      return s.points - points;
    }

    @Override
    public String toString() {
      return "SequenceScore [waypoints=" + matches + ", points=" + points
          + ", time=" + time + "]";
    }    
  }
  
  static int countGaps(Iterable<Approach> approaches) {
    int gaps = 0;
    int lastRouteIndex = -1;
    for( Approach approach: approaches ) {
      int routeIndex = approach.routeIndex;
      if ( lastRouteIndex >= 0 && lastRouteIndex + 1 != routeIndex ) {
        gaps++;
      }
      lastRouteIndex = routeIndex;
    }
    return gaps;
  }
  
  static int countMissing(Iterable<Approach> approaches) {
    int missing = 0;
    int lastRouteIndex = -1;
    for( Approach approach: approaches ) {
      int routeIndex = approach.routeIndex;
      if ( lastRouteIndex >= 0 ) {
        missing += routeIndex - lastRouteIndex - 1;
      }
      lastRouteIndex = routeIndex;
    }
    return missing;
  }
  
  static int countPassedPoints(Iterable<Approach> approaches) {
    int count = 0;
    Approach lastApproach = null;
    for( Approach approach: approaches ) {
      if ( lastApproach != null && approach.routeIndex == lastApproach.routeIndex + 1 ) {
        count += approach.trackIndex - lastApproach.trackIndex;
      }
      lastApproach = approach;
    }
    return count;
  }
  
  /** Compute the score for a route.
   * @param route The route, specified as a sequence of waypoints.
   * @param score
   */
  public Score computeScore(PointTime[] route) {
    SequenceScore score = new SequenceScore();
    List<Approach> approaches = matcher.match(route);
    int size = approaches.size();
    if ( size > 0 ) {
      int index1 = approaches.get(0).trackIndex;
      int index2 = approaches.get(size-1).trackIndex;
      score.setPoints(countPassedPoints(approaches));
      score.setGaps(countGaps(approaches));
      score.setMissing(countMissing(approaches));
      score.setMatches(size);
      score.setTime(track[index2].time - track[index1].time);
    }
    return score;
  }
  
  /** Get the names of the score components.  Use for debugging. */
  @Override
  public String[] getScoreFieldNames() {
    return new String[] {
        "id", "stops", "missing", "gaps", "points", "time"};      
  }
  /** Get the score components as an array (corresponding to the field names).  Use for debugging. */
  @Override
  public Object[] getFields(Score score) {
    SequenceScore s = (SequenceScore) score;
    return new Object[] {
        s.getId(),
        s.getMatches(),
        s.getMissing(),
        s.getGaps(),
        s.getPoints(),
        s.getTime()};
  }
  
  @Override
  public boolean isMinimal(Score score) {
    SequenceScore s = (SequenceScore) score;
    return s.getPoints() == 0;
  }

  @Override
  public boolean areClose(Score score1, Score score2) {
    SequenceScore s1 = (SequenceScore) score1;
    SequenceScore s2 = (SequenceScore) score2;
    return Math.abs(s1.getTime() - s2.getTime()) <= 5000L || Math.abs(s1.getPoints() - s2.getPoints()) <= 1; 
  }
}

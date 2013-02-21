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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.melato.gps.Earth;
import org.melato.gps.Point2D;
import org.melato.gps.PointTime;

/** Matches a track to one or more routes and returns a list of approaches.
 *  An approach is a pair of (route-index, track-index),
 *  where the distance between a route waypoint and the track is shorter than a threshold
 *  and is at a local minimum.
 *  The class puts the approaches in order and may remove some if they don't fit in the sequence
 *  specified by the track.
 */
public class RouteMatcher {
  private PointTime[] trackWaypoints;
  private Path  trackPath;
  private ProximityFinder proximity;
  private float startSpeed;
  public static float MAX_SPEED = 120f / 3600f * 1000f;  // remove speeds over 120 Km/h
  
  /**
   * An approach.
   * It sorts by track index, so that the approaches are in the order that they are encountered.
   * @author Alex Athanasopoulos
   *
   */
  public static class Approach implements Comparable<Approach> {
    /** The index of the route waypoint */ 
    public int routeIndex;
    /** The index of the track waypoint */ 
    public int trackIndex;
    boolean visited;
    public Approach(int routeIndex, int trackIndex) {
      super();
      this.routeIndex = routeIndex;
      this.trackIndex = trackIndex;
    }
    @Override
    public int compareTo(Approach a) {
      int d = trackIndex - a.trackIndex;
      if ( d != 0 )
        return d;
      return routeIndex - a.routeIndex;
    }
    @Override
    public String toString() {
      return "Approach [routeIndex=" + routeIndex + ", trackIndex="
          + trackIndex + ", visited=" + visited + "]";
    }    
  }
  
  public void setStartSpeed(float startSpeed) {
    this.startSpeed = startSpeed * 1000f / 3600f;
  }

  public RouteMatcher(PointTime[] track, float proximityDistance ) {
    this.trackWaypoints = track;
    this.trackPath = new Path(track);
    proximity = new ProximityFinder();
    proximity.setPath(this.trackPath);
    proximity.setTargetDistance(proximityDistance);
  }
  
  private int trim(PointTime[] waypoints, int index, int nextIndex) {
    for( ; index < nextIndex; index++ ) {
      PointTime p1 = waypoints[index];
      PointTime p2 = waypoints[index+1];
      float speed = Earth.distance(p1,  p2) - PointTime.timeDifference(p1,  p2);
      if ( speed > startSpeed ) {
        return index;
      }
    }
    return index;    
  }
  
  static class Sequence {
    int start;
    int last;
    int length;
    
    
    /** for debugging */
    @Override
    public String toString() {
      return "[start=" + start + " last=" + last + " lenght=" + length + "]";
    }
    void clearInside(Approach[] approaches) {
      Approach a = approaches[start];
      int routeIndex = a.routeIndex;
      for( int i = start; i <= last; i++ ) {
        Approach b = approaches[i];
        if ( b != null ) {
          if ( b.routeIndex == routeIndex + 1 || b.routeIndex == routeIndex ) {          
            routeIndex = b.routeIndex;
          } else {
            approaches[i] = null;
          }
        }
      }
    }
    void clearLeft(Approach[] approaches, int start) {
      int routeIndex = approaches[this.start].routeIndex;
      for( int i = start; i < this.start; i++ ) {
        Approach a = approaches[i];
        if ( a != null && a.routeIndex > routeIndex ) {
          approaches[i] = null;
        }
      }
    }
    void clearRight(Approach[] approaches, int end) {
      int routeIndex = approaches[this.start].routeIndex + length;
      for( int i = this.last + 1; i < end; i++ ) {
        Approach a = approaches[i];
        if ( a != null && a.routeIndex < routeIndex ) {
          approaches[i] = null;
        }
      }
    }
  }
  
  private static void findSequence(Approach[] approaches, int start, int end, Sequence sequence) {
    Approach a = approaches[start];
    sequence.start = start;
    sequence.last = start;
    sequence.length = 1;
    int routeIndex = a.routeIndex;
    int j = start + 1;
    for( ; j < end ;j++ ) {
      Approach b = approaches[j];
      if ( b != null && ! b.visited) {
        if ( b.routeIndex == routeIndex + 1 ) {
          routeIndex = b.routeIndex;
          sequence.length++;
        } else if ( b.routeIndex != routeIndex ) {
          continue;
        }
        sequence.last = j;
        b.visited = true;
      }
    }
    //System.out.println( "findSequence start=" + start + " end=" + end + " sequence=" + sequence);
  }

  /** remove approaches so that the remaining approaches are in non-decreasing order of route indexes.
   * approaches are removed by setting their place to null in the array.
   * */ 
  private static void removeOutOfOrder(Approach[] approaches, int start, int end) {
    if ( end <= start )
      return;
    for( int i = start; i < end; i++ ) {
      Approach a = approaches[i];
      if ( a != null ) {
        a.visited = false;
      }
    }
    Sequence bestSequence = null;
    Sequence sequence = new Sequence();
    
    // find the longest sub-sequence of sequential or equal route indexes
    for( int i = start; i < end; i++ ) {
      Approach a = approaches[i];
      if ( a != null ) {
        //System.out.println( "i=" + i + " visited=" + a.visited);
        if ( a.visited )
          continue;
        findSequence(approaches, i, end, sequence);
        if ( bestSequence == null || sequence.length > bestSequence.length ) {
          bestSequence = sequence;
          sequence = new Sequence();
        }
      }
    }
    if ( bestSequence == null ) {
      // there is nothing
      return;
    }
    bestSequence.clearInside(approaches);
    bestSequence.clearLeft(approaches, start);
    bestSequence.clearRight(approaches, end);
    
    //System.out.println( "a: " + toString( approaches, 0, approaches.length ));
    // do the same on each side
    removeOutOfOrder( approaches, start, bestSequence.start);
    removeOutOfOrder( approaches, bestSequence.last + 1, end );
    //System.out.println( "b: " + toString( approaches, 0, approaches.length ));
  }

  private static void removeDuplicates(Approach[] approaches) {
    // keep the last approach that has the first route index.
    int routeIndex = -1;
    int lastIndex = -1;
    int i = 0;
    for( ; i < approaches.length; i++ ) {
      Approach a = approaches[i];
      if ( a != null ) {
        if ( routeIndex == -1 ) {
          routeIndex = a.routeIndex;
          lastIndex = i;
        } else if ( routeIndex == a.routeIndex ) {
            approaches[lastIndex] = null;
        } else {
          routeIndex = a.routeIndex;
          i++;
          break;
        }
      }
    }
    
    // for subsequent route indexes, keep the first approach from approaches with equal route index.
    for( ; i < approaches.length; i++ ) {
      Approach a = approaches[i];
      if ( a != null ) {
        if ( routeIndex == a.routeIndex ) {
          approaches[i] = null;
        } else {
          routeIndex = a.routeIndex;
        }
      }      
    }
  }

  public static String toString( Approach[] approaches, int start, int end ) {
    StringBuilder buf = new StringBuilder();
    buf.append( "[");
    int count = 0;
    for( int i = start; i < end; i++ ) {
      Approach a = approaches[i];
      if ( a != null ) {
        if ( count > 0 ) {
          buf.append( " " );
        }
        count++;
        buf.append( String.valueOf(a.routeIndex) );
      }
    }
    buf.append("]");
    return buf.toString();
  }
  
  /**
   * Pack all non-null approaches at the beginning of an array
   * and return their number.
   * @param approaches
   * @return
   */
  public static int pack(Approach[] approaches) {
    int n = 0;
    for( int i = 0; i < approaches.length; i++ ) {
      Approach a = approaches[i];
      if ( a != null) {
        approaches[n++] = a;
      }
    }
    for( int i = n; i < approaches.length; i++ ) {
      approaches[i] = null;
    }
    return n;    
  }
  
  public void removeExcessiveSpeed(Approach[] approaches, Point2D[] route) {
    int size = pack(approaches);
    //System.out.println( "removeExcessiveSpeed: size=" + size);
    if ( size == 0 )
      return;
    Path routePath = new Path(route);
    for(int i = 1 ; i < size; i++ ) {
      PointTime p0 = (PointTime) trackPath.getWaypoint(approaches[i-1].trackIndex);
      PointTime p =  (PointTime) trackPath.getWaypoint(approaches[i].trackIndex);
      float time = PointTime.timeDifference(p0, p); 
      float distance = routePath.getLength(approaches[i-1].routeIndex, approaches[i].routeIndex);
      float speed = distance / time;
            
      if ( speed > MAX_SPEED ) {
        // remove the smaller half
        if ( 2 * i >= size ) {
          // remove the right half
          for( int j = i; j < size; j++ ) {
            approaches[j] = null;
          }
          return;
        } else {
          // remove the left half
          for( int j = 0; j < i; j++ ) {
            approaches[j] = null;
          }
        }
      }
    }
  }
  
  /**
   * Sort the approaches chronologically (by track index)
   * Remove duplicates (approaching the same waypoint twice in a row)
   * Remove out of order (approaching the 10th waypoint before the 2nd one). 
   * @param list
   */
  public static void filter(Approach[] approaches ) {
    Arrays.sort(approaches);
    removeOutOfOrder( approaches, 0, approaches.length );
    //System.out.println( "c: " + toString( approaches, 0, approaches.length ));
    removeDuplicates(approaches);
    //System.out.println( "d: " + toString( approaches, 0, approaches.length ));
  }
  
  /** Matches our track to the given route.
   * @param route The route, presented as a sequence of points.
   * @return The list of matched approaches.
   */
  public List<Approach> match(Point2D[] route) {
    List<Approach> list = new ArrayList<Approach>();
    List<Integer> nearby = new ArrayList<Integer>();
    int routeSize = route.length;
    // find track points that are nearby the route waypoint
    for( int i = 0; i < routeSize; i++ ) {
      nearby.clear();
      proximity.findNearby(route[i], nearby);
      //System.out.println( route.get(i).getName() + " nearby.size=" + nearby.size());
      int nearbySize = nearby.size();
      for( int j = 0; j < nearbySize; j++ ) {
        list.add( new Approach(i, nearby.get(j)));
      }
    }
    Approach[] approaches = list.toArray(new Approach[0]);
    //System.out.println( "match.approaches=" + approaches.length);
    filter(approaches);
    removeExcessiveSpeed(approaches, route);
    int size = pack(approaches);
    if ( size > 1 ) {
      int firstIndex = trim(trackWaypoints, approaches[0].trackIndex, approaches[1].trackIndex);      
      list.get(0).trackIndex = firstIndex;
    }
    return Arrays.asList(approaches).subList(0,  size);
  }
}

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

import java.util.ArrayList;
import java.util.List;

import org.melato.gps.LocalDistance;
import org.melato.gps.Metric;
import org.melato.gps.PointTime;
import org.melato.util.CircularList;


/** Computes rolling average speeds over multiple time intervals,
 * e.g. every 1 minute, every 5 minutes, every 1 and 5 minutes, etc.
 */
public class RollingSpeedManager {
  public class RollingSpeed {
    /** How many milliseconds to average over. */
    long    intervalTime;
    int     count; // the number of spans
    double  distance; // the distance covered by our spans
    long    time; // the time covered by our spans
    
    RollingSpeed(long intervalTime) {
      super();
      this.intervalTime = intervalTime;
    }    
    public float getSpeed() {
      return (float) (distance * 1000 / time);
    }    
    public long getIntervalTime() {
      return intervalTime;
    }
    public int getCount() {
      return count;
    }
    public double getDistance() {
      return distance;
    }
    public long getTime() {
      return time;
    }
    void add(Span sample) {
      count++;  // add the new sample
      distance += sample.distance;
      time += sample.timeMillis;
    }    
    
    @Override
    public String toString() {
      return distance + "/" + time + " (" + count + ")";
    }
    void recompute() {
      count = 0;
      distance = 0;
      time = 0;
      int size = spans.size();
      for( int i = 0; i < size; i++ ) {
        count++;
        Span s = spans.get(i);
        distance += s.distance;
        time += s.getTime();
        if ( time > intervalTime ) {
          break;
        }
      }
    }
    void trim() {
      // find out which samples are no longer needed.
      if ( time > intervalTime ) {
        for( int last = count - 1; last > 0; last-- ) {
          Span s = spans.get(last);
          long sampleTime = s.getTime();
          if ( time - sampleTime < intervalTime ) {
            break;
          }
          time -= sampleTime;
          distance -= s.getDistance();
          count--;
        }
      }      
    }
  }
  public class Span {
    float distance;    // the traveled distance since the previous sample.
    long  timeMillis;  // the traveled time since the previous sample.
    
    public Span(float distance, long timeMillis) {
      super();
      this.distance = distance;
      this.timeMillis = timeMillis;
    }
    public long getTime() {
      return timeMillis;      
    }
    public float getDistance() {
      return distance;
    }
    @Override
    public String toString() {
      return distance + "/" + timeMillis;
    }
    
  }
  private List<RollingSpeed> speeds = new ArrayList<RollingSpeed>();
  /** Recent spans.  The most recent span is [0]. */
  private List<Span> spans = new CircularList<Span>();

  private RollingSpeed maxInterval;
  
  private PointTime  lastPoint;
  private Metric  metric;

  public RollingSpeedManager( Metric metric ) {
    this.metric = metric;
  }
  
  private RollingSpeed addRollingSpeed(int seconds) {
    RollingSpeed interval = new RollingSpeed(seconds * 1000L);
    speeds.add(interval);
    interval.recompute();
    if ( maxInterval == null || maxInterval.intervalTime < interval.intervalTime ) {
      maxInterval = interval;
    }
    return interval;    
  }
  
  public RollingSpeed getRollingSpeed(int seconds) {
    long time = seconds * 1000L;
    int size = speeds.size();
    for( int i = 0; i < size; i++ ) {
      RollingSpeed speed = speeds.get(i);
      if ( speed.getIntervalTime() == time)
        return speed;
    }
    return addRollingSpeed(seconds);
  }
  
  public void addPoint(PointTime p) {
    if ( lastPoint == null ) {
      if ( metric == null ) {
        metric = new LocalDistance(p);
      }
      lastPoint = p;
      return;
    }
    Span span = new Span(metric.distance(lastPoint,  p),
        PointTime.timeDifferenceMillis(lastPoint, p));
    lastPoint = p;
    addSpan(span);
  }  

  public void addSpan(Span sample) {
    // insert the new sample at the beginning of the list.
    spans.add(0, sample);
    
    // adjust all speeds
    for (RollingSpeed interval: speeds) {
      interval.add(sample);
      interval.trim();
    }

    // remove samples that are no longer needed.
    // These are the samples with index >= maxInterval.count.
    for( int i = spans.size() - 1; i >= maxInterval.count; i-- ) {
      spans.remove(i);
    }
  }  
}

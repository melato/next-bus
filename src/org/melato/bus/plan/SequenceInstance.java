/*-------------------------------------------------------------------------
 * Copyright (c) 2012,2013, Alex Athanasopoulos.  All Rights Reserved.
 * alex@melato.org
 *-------------------------------------------------------------------------
 * This file is part of Athens Next Bus
 *
 * Athens Next Bus is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Athens Next Bus is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Athens Next Bus.  If not, see <http://www.gnu.org/licenses/>.
 *-------------------------------------------------------------------------
 */
package org.melato.bus.plan;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.melato.bus.client.Formatting;
import org.melato.bus.model.RStop;
import org.melato.bus.model.Schedule;
import org.melato.gps.GlobalDistance;

/** A sequence scheduled at a particular time */
public class SequenceInstance implements Serializable {
  private static final long serialVersionUID = 1L;
  int startTime;
  int endTime;
  SequenceSchedule schedule;
  int[] levelIndexes;
  
  public static interface SequenceInstanceLeg extends Serializable{
    
  }
  public static class LegInstance implements SequenceInstanceLeg, Serializable {
    private static final long serialVersionUID = 1L;
    private LegTime legTime;
    private int     waitSeconds;
    private String waitString = "wait";

    public LegInstance(LegTime legTime, LegTime previous) {
      super();
      this.legTime = legTime;
      if ( previous != null) {
        waitSeconds = legTime.getTime1()-previous.getTime2();
      } else {
        waitSeconds = -1;
      }
    }    
    
    public RStop getRStop() {
      return legTime.leg.getRStop1();
    }    
    
    public void setWaitString(String waitString) {
      this.waitString = waitString;
    }

    @Override
    public String toString() {
      String s = legTime.toString();
      if ( waitSeconds >= 0 ) {
        s += " (" + waitString + " " + Schedule.formatDuration(waitSeconds) + ")";
      }
      return s;
    }    
  }
  public static class WalkInstance implements SequenceInstanceLeg, Serializable {
    private static final long serialVersionUID = 1L;
    private float distance;
    
    public float getDistance() {
      return distance;
    }
    public WalkInstance(LegTime leg1, LegTime leg2) {
      distance = new GlobalDistance().distance(leg1.getLeg().getStop2(), leg2.getLeg().getStop1());
    }
    @Override
    public String toString() {
      return "Walk " + Formatting.straightDistance(distance) + " " + Walk.distanceDuration(distance);
    }    
  }
  
  public SequenceInstance(SequenceSchedule schedule, int[] indexes) {
    this.schedule = schedule;
    this.levelIndexes = indexes;
    LegTime firstLeg = schedule.levels[0].legTimes[indexes[0]];
    LegTime lastLeg = schedule.levels[indexes.length-1].legTimes[indexes[indexes.length-1]];
    startTime = firstLeg.getTime1();
    endTime = lastLeg.getTime2();
  }
      
  /** Start time in seconds. */
  public int getStartTime() {
    return startTime;
  }

  /** Start time in seconds. */
  public int getEndTime() {
    return endTime;
  }

  public SequenceInstanceLeg[] getLegInstances() {
    List<SequenceInstanceLeg> legs = new ArrayList<SequenceInstanceLeg>(); 
    LegTime previous = null;
    LegTime previous2 = null;
    LegTime[] previousLegs = null;
    for( int i = 0; i < levelIndexes.length; i++ ) {
      LegTime[] levelLegs = schedule.levels[i].legTimes;
      LegTime leg = levelLegs[levelIndexes[i]];
      if ( i > 0 ) {
        for( int j = levelIndexes[i-1] + 1; j < previousLegs.length; j++ ) {
          LegTime t = previousLegs[j];
          if ( t.getTime2() < leg.getTime1()) {
            legs.add(new LegInstance(t, previous2));
          }
          if ( t.getTime1() > leg.getTime1()) {
            break;
          }
        }
      }
      if ( previous != null) {
        legs.add(new WalkInstance(previous, leg));
      }
      legs.add(new LegInstance(leg, previous));
      previous2 = previous;
      previous = leg;
      previousLegs = levelLegs;
    }
    return legs.toArray(new SequenceInstanceLeg[0]);
  }

  @Override
  public String toString() {
    StringBuilder buf = new StringBuilder();
    buf.append( Schedule.formatTime(startTime/60));
    buf.append( " -> " );
    buf.append( Schedule.formatTime(endTime/60));
    buf.append( " (" );
    buf.append(Schedule.formatDuration(endTime-startTime));
    buf.append(")");
    return buf.toString();
  }
  
}

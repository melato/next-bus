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

import java.util.ArrayList;
import java.util.List;

import org.melato.bus.model.Schedule;
import org.melato.gps.GlobalDistance;

/** A sequence scheduled at a particular time */
public class SequenceInstance {
  int startTime;
  int endTime;
  SequenceSchedule schedule;
  int[] levelIndexes;
  
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

  public SequenceItinerary.TransitLeg createTransitLeg(LegTime legTime, LegTime previous) {
    SequenceItinerary.TransitLeg leg = new SequenceItinerary.TransitLeg();
    leg.leg = legTime.leg;
    leg.startTime = legTime.getTime1();
    leg.endTime = legTime.getTime2();
    leg.duration = leg.endTime - leg.startTime;
    if ( previous != null) {
      leg.diffTime = leg.startTime - previous.getTime2();
    }
    return leg;
  }    
  
  public SequenceItinerary.WalkLeg createWalkLeg(WalkModel walkModel, LegTime leg1, LegTime leg2) {
    SequenceItinerary.WalkLeg leg = new SequenceItinerary.WalkLeg();
    leg.startTime = leg1.getTime2();
    leg.endTime = leg2.getTime1();
    leg.distance = new GlobalDistance().distance(leg1.leg.getStop2(), leg2.leg.getStop1());
    leg.duration = Math.round(walkModel.duration(leg.distance));
    return leg;
  }    
  
  public SequenceItinerary getItinerary() {
    List<SequenceItinerary.Leg> legs = new ArrayList<SequenceItinerary.Leg>(); 
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
            legs.add(createTransitLeg(t, previous2));
          }
          if ( t.getTime1() > leg.getTime1()) {
            break;
          }
        }
      }
      if ( previous != null) {
        legs.add(createWalkLeg(schedule.getWalkModel(), previous, leg));
      }
      legs.add(createTransitLeg(leg, previous));
      previous2 = previous;
      previous = leg;
      previousLegs = levelLegs;
    }
    SequenceItinerary itinerary = new SequenceItinerary(legs.toArray(new SequenceItinerary.Leg[0]));
    return itinerary;
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

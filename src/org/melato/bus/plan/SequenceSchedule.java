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
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.melato.bus.model.DaySchedule;
import org.melato.bus.model.Route;
import org.melato.bus.model.RouteManager;
import org.melato.bus.model.Schedule;
import org.melato.bus.model.Schedule.ScheduleFactory;
import org.melato.gps.Metric;
import org.melato.progress.ProgressGenerator;

public class SequenceSchedule {
  public Level[] levels;
  private List<SequenceInstance> instances;

  /** Helper class for scheduling one leg and equivalent legs */
  public static class Level {
    private LegGroup leg;
    private Leg[] legs;
    public LegTime[] legTimes;
    private int[] times;
    private int walkTime;
    public Level(LegGroup leg) {
      super();
      this.leg = leg;
    }
    public void setWalkDistance(float distance) {
      this.walkTime = (int)Walk.duration(distance);
    }    
    public int getWalkTime() {
      return walkTime;
    }
    void compute(ScheduleFactory scheduleFactory, RouteManager routeManager) {
      ProgressGenerator progress = ProgressGenerator.get();
      Route route = routeManager.getRoute(leg.getLeg().getRouteId());
      progress.setText(route.getLabel());
      legs = leg.getEquivalentLegs(routeManager);
      List<LegTime> timeList = new ArrayList<LegTime>();
      for(int i = 0; i < legs.length; i++ ) {
        Leg leg = legs[i];
        Schedule schedule = routeManager.getSchedule(leg.getRouteId());
        DaySchedule daySchedule = scheduleFactory.getSchedule(schedule);
        if ( daySchedule != null) {
          int[] times = daySchedule.getTimes();
          for( int time: times ) {
            LegTime legTime = new LegTime(leg, time, routeManager);
            timeList.add(legTime);
          }
        }
      }
      legTimes = timeList.toArray(new LegTime[0]);
      Arrays.sort(legTimes);
      times = new int[legTimes.length];
      for( int i = 0; i < times.length; i++ ) {
        times[i] = legTimes[i].getTime1();
      }
    }
    
    int findTimeIndex(int time) {
      int pos = Arrays.binarySearch(times, time);
      if ( pos >= 0 )
        return pos;
      pos = -(pos+1);
      if ( pos < times.length ) {
        return pos;
      }
      return -1;
    }
  }
  
  public SequenceSchedule(Sequence sequence, ScheduleFactory scheduleFactory, RouteManager routeManager) {
    List<LegGroup> legs = sequence.getLegs();
    levels = new Level[legs.size()];
    Metric metric = routeManager.getMetric();
    for( int i = 0; i < levels.length; i++ ) {
      levels[i] = new Level(legs.get(i));
    }
    for( int i = 1; i < levels.length; i++ ) {
      float distance = metric.distance(levels[i-1].leg.getLeg().getStop2(), levels[i].leg.getLeg().getStop1());
      levels[i].setWalkDistance(distance);
    }
    ProgressGenerator progress = ProgressGenerator.get();
    progress.setLimit(levels.length);
    for( int i = 0; i < levels.length; i++ ) {
      progress.setPosition(i);
      levels[i].compute(scheduleFactory, routeManager);
    }
    instances = createInstances(levels);
  }

  public List<SequenceInstance> getInstances() {
    return instances;
  }

  public int getTimePosition(int minutes) {
    int size = instances.size();
    int seconds = minutes * 60;
    for( int i = 0; i < size; i++ ) {
      SequenceInstance instance = instances.get(i);
      if ( seconds < instance.getStartTime() ) {
        return i;
      }
    }
    return -1;
  }  
  
  public int getTimePosition(Date date) {
    return getTimePosition(Schedule.getTime(date));
  }  
  
  private List<SequenceInstance> createInstances(Level[] levels) {
    List<SequenceInstance> instances = new ArrayList<SequenceInstance>();
    if ( levels.length == 0 )
      return instances;
    Level firstLevel = levels[0];
    LegTime[] firstTimes = firstLevel.legTimes;
    int[] indexes = new int[levels.length];
    for(int firstIndex = 0; firstIndex < firstTimes.length; firstIndex++ ) {      
      LegTime firstLeg = firstTimes[firstIndex];
      indexes[0] = firstIndex;
      int time = firstLeg.getTime2();
      boolean complete = true;
      for( int i = 1; i < levels.length; i++ ) {
        time += levels[i].getWalkTime();
        int timeIndex = levels[i].findTimeIndex(time);
        if ( timeIndex >= 0 ) {
          indexes[i] = timeIndex;
          LegTime leg = levels[i].legTimes[timeIndex];
          time = leg.getTime2();
          //System.out.println( leg);
        } else {
          complete = false;
          break;
        }
      }      
      if ( complete ) {
        SequenceInstance instance = new SequenceInstance(this, indexes.clone());
        instances.add(instance);        
      } else {
        break;
      }
    }
    return instances;
    /*
    // use the last possible leg at the first level
    if ( levels.length > 1 ) {
      int time1 = legTimes[1].getTime1();
      while( firstIndex + 1 < levels[0].legTimes.length ) {
        if ( firstTimes[firstIndex+1].getTime2() < time1 ) {
          firstIndex++;
          legTimes[0] = firstTimes[firstIndex];
        } else {
          break;
        }
      }
    }
    */
  }  
}

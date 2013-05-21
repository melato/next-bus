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
package org.melato.bus.model.cache;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.melato.bus.model.DaySchedule;
import org.melato.bus.model.RouteId;
import org.melato.bus.model.RouteManager;
import org.melato.bus.model.Schedule;
import org.melato.util.DateId;


/** Caches schedule times for nearby and route planning. */
public class ScheduleCache {
  private RouteManager routeManager;
  private static final int CACHE_LENGTH = 5;
  private Map<RouteId,int[]> map = new HashMap<RouteId,int[]>();
  private int dateId;
  private static int[] empty = new int[0];

  public ScheduleCache(RouteManager routeManager) {
    super();
    this.routeManager = routeManager;
  }
  
  int findOffset(int[] times, int time) {
    for( int i = 0; i < times.length; i++ ) {
      if ( time < times[i] ) {
        return i;
      }
    }
    return -1;
  }
  int[] subset(int[] times, int offset, int len) {
    int n = times.length - offset;
    if ( n > len ) {
      n = len;
    }
    int[] tt = new int[n];
    for( int i = 0; i < n; i++ ) {
      tt[i] = times[offset+i];
    }
    return tt;
  }
  /** Get the two closest schedule times after the given time. */
  public int[] getNextTimes(RouteId routeId, Date date) {
    int dateId = DateId.dateId(date);
    if ( dateId != this.dateId) {
      map = new HashMap<RouteId,int[]>();
      this.dateId = dateId;
    }
    int time = Schedule.getTime(date);
    int[] times = map.get(routeId);
    int offset = 0;
    if ( times != null) {
      offset = findOffset(times, time);
      if ( offset < 0 )
        return empty;
      if ( offset + 2 <= times.length || times.length <= CACHE_LENGTH ) {
        return subset(times, offset, 2);
      }
    }
    times = loadNearestTimes(routeId, date);
    map.put(routeId, times);
    offset = findOffset(times, time);
    if ( offset < 0 ) {
      return empty;
    }
    return subset(times, offset, 2);
  }
  
  private int[] loadNearestTimes(RouteId routeId, Date date) {
    int[] times = null;
    int time = Schedule.getTime(date);
    DaySchedule daySchedule = routeManager.getDaySchedule(routeId, date);
    if ( daySchedule != null ) {
      times = daySchedule.getTimes();
      int index = daySchedule.getNextIndex(time);
      if ( index == -1 )
        return empty;
      index -= CACHE_LENGTH/2;
      if ( index + CACHE_LENGTH > times.length ) {
        index = times.length - CACHE_LENGTH;
      }
      if ( index < 0 )
        index = 0;
      return subset(times, index, CACHE_LENGTH);
    }
    return empty;
  }  
}

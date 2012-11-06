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
package org.melato.bus.client;

import java.util.AbstractList;
import java.util.Date;

import org.melato.bus.model.DaySchedule;
import org.melato.bus.model.Schedule;
import org.melato.log.Log;

public class TimeOfDayList extends AbstractList<TimeOfDay> {
  private int[] times;
  private int   timeOffset;
  private Date  currentTime;

  public TimeOfDayList(int[] times, Date currentTime) {
    this.times = times;
    this.currentTime = currentTime;
    Log.info( "TimeOfDayList times.length=" + times.length );
  }
  
  public void setTimeOffset(int timeOffset) {
    this.timeOffset = timeOffset;
  }

  public TimeOfDayList(Schedule schedule, Date currentTime) {
    this(schedule.getTimes(currentTime), currentTime);
  }
  public TimeOfDayList(DaySchedule schedule, Date currentTime) {
    this(schedule.getTimes(), currentTime);;
  }
  @Override
  public TimeOfDay get(int location) {
    return new TimeOfDay(times[location] + timeOffset / 60);
  }

  @Override
  public int size() {
    return times.length;
  }
  
  public int getDefaultPosition() {
    int time = Schedule.getTime(currentTime);
    for( int i = 1; i < times.length; i++ ) {
      if ( times[i] >= time )
        return i - 1;
    }
    return times.length - 1;
  }
  
  
}



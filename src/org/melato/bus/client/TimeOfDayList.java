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
package org.melato.bus.client;

import java.util.AbstractList;
import java.util.Date;

import org.melato.bus.model.DaySchedule;
import org.melato.bus.model.Schedule;

/** A list of times for displaying the schedule. */
public class TimeOfDayList extends AbstractList<TimeOfDay> {
  DaySchedule daySchedule;
  private int[] times;
  /** The time difference from the start to the desired stop, in seconds. */ 
  private int   timeOffset;
  private Date  currentTime;

  public void setTimeOffset(int timeOffset) {
    this.timeOffset = timeOffset;
  }

  public TimeOfDayList(Schedule schedule, Date currentTime) {
    this(schedule.getSchedule(currentTime), currentTime);
  }
  public TimeOfDayList(DaySchedule schedule, Date currentTime) {
    this.daySchedule = schedule;
    this.times = schedule.getTimes();
    this.currentTime = currentTime;
  }
  @Override
  public TimeOfDay get(int location) {
    return new TimeOfDay(times[location], timeOffset);
  }

  public boolean hasOffset() {
    return timeOffset != 0;
  }
  
  @Override
  public int size() {
    return times.length;
  }
  
  public int getDefaultPosition() {
    Date date = new Date(currentTime.getTime() - timeOffset * 1000L );
    return daySchedule.getClosestIndex(date);
  }
}
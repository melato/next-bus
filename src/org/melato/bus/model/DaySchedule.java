/*-------------------------------------------------------------------------
 * Copyright (c) 2012, Alex Athanasopoulos.  All Rights Reserved.
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
package org.melato.bus.model;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;


/** Maintains departure information for one route and one day.
 * May apply to several days of the week if they all have the same schedule every day. */
public class DaySchedule {
  public static final int SUNDAY = 1;
  public static final int MONDAY = 2;
  public static final int TUESDAY = 4;
  public static final int WEDNESDAY = 8;
  public static final int THURSDAY = 16;
  public static final int FRIDAY = 32;
  public static final int SATURDAY = 64;
  
  public static final int MONDAY_FRIDAY = (MONDAY | TUESDAY | WEDNESDAY | THURSDAY | FRIDAY);
  public static final int MONDAY_THURSDAY = (MONDAY | TUESDAY | WEDNESDAY | THURSDAY );
  public static final int SATURDAY_SUNDAY = SATURDAY | SUNDAY;
  public static final int EVERYDAY = 127;
  /** times are stored as minutes from midnight. */
  private int[] times;
  private ScheduleId scheduleId;
  
  public int[] getTimes() {
    return times;
  }
  
  public ScheduleId getScheduleId() {
    return scheduleId;
  }

  public boolean matchesDateId(int dateId) {
    return scheduleId.getDateId() == dateId;
  }
  
  public boolean matchesDayOfWeek(int dayOfWeek) {
    int bitmap = 1 << (dayOfWeek-Calendar.SUNDAY);
    return (scheduleId.getDays() & bitmap) != 0;
  }
  
  public boolean isWeekly() {
    return scheduleId.getDays() != 0;
  }
  public boolean isException() {
    return scheduleId.getDateId() != 0;
  }
  
  public DaySchedule(int[] times, ScheduleId scheduleId) {
    super();
    Arrays.sort(times);
    this.times = times;
    this.scheduleId = scheduleId;
  }
  
  public static DaySchedule findSchedule(DaySchedule[] schedules, int dayOfWeek) {
    int bitmap = 1 << (dayOfWeek-Calendar.SUNDAY);
    for( DaySchedule schedule: schedules ) {
      if ( (schedule.getScheduleId().getDays() & bitmap) != 0 ) {
        return schedule;
      }
    }
    return null;
  }
  public static DaySchedule findSchedule(DaySchedule[] schedules, Date date) {
    Calendar cal = new GregorianCalendar();
    cal.setTime(date);
    return findSchedule(schedules, cal.get(Calendar.DAY_OF_WEEK));
  }
}

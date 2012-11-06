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
package org.melato.bus.model;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;


/** Maintains departure information for one route and one day.
 * May apply to several days of the week if they all have the same schedule every day. */
public class DaySchedule {
  public static final int SUNDAY = 1;
  public static final int MONDAY_FRIDAY = 62;
  public static final int SATURDAY = 64;
  public static final int SATURDAY_SUNDAY = 65;
  public static final int EVERYDAY = 127;
  /** times are stored as minutes from midnight. */
  private int[] times;
  private int   days;  // days is a bitmap, bit 0 = sunday, bit 6 = saturday
  
  public int[] getTimes() {
    return times;
  }

  public int getDays() {
    return days;
  }

  public DaySchedule(int[] times, int days) {
    super();
    Arrays.sort(times);
    this.times = times;
    this.days = days;
  }
  
  public static DaySchedule findSchedule(DaySchedule[] schedules, int dayOfWeek) {
    int bitmap = 1 << (dayOfWeek-Calendar.SUNDAY);
    for( DaySchedule schedule: schedules ) {
      if ( (schedule.days & bitmap) != 0 ) {
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

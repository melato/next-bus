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

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;


/** A schedule maintains departure information for one route and for all days of the week. */
public class Schedule {
  private DaySchedule[] schedules;
  private String comment;

  static DecimalFormat d2Format = new DecimalFormat("00");
  
  /**
   * format a schedule time
   * @param time The time in minutes since midnight.
   * @return
   */
  public static String formatTime(int time) {
    return d2Format.format(time/60) + ":" + d2Format.format(time%60);
  }
  
  /** Parse a hh:mm time and return minutes since midnight.*/
  public static int parseTime(String time) {
    int p = time.indexOf(':');
    if ( p < 0 )
      throw new IllegalArgumentException( "Invalid time: " + time );
    return Integer.parseInt(time.substring(0,p)) * 60 + Integer.parseInt(time.substring(p+1));
  }
    
  /** Create an empty schedule. */
  public Schedule() {
  }

  public Schedule(DaySchedule[] schedules) {
    super();
    this.schedules = schedules;
  }

  public DaySchedule[] getSchedules() {
    return schedules;
  }
  
  public DaySchedule getSchedule(Date date) {
    return DaySchedule.findSchedule(schedules, date);    
  }
  /** Get the schedule times for a given day of the week. */
  public int[] getTimes( Date date ) {
    DaySchedule schedule = getSchedule(date);
    if ( schedule == null ) {
      return new int[0];
    }
    return schedule.getTimes();
  }
  
  /** Get the schedule times for a given day of the week. */
  public int[] getTimes( int dayOfWeek ) {
    DaySchedule schedule = DaySchedule.findSchedule(schedules, dayOfWeek);
    if ( schedule == null ) {
      return new int[0];
    }
    return schedule.getTimes();
  }
  
  /** Get the time in minutes since midnight */
  public static int getTime( Date date ) {
    Calendar cal = new GregorianCalendar();
    cal.setTime(date);
    int hour = cal.get(Calendar.HOUR_OF_DAY);
    int minute = cal.get(Calendar.MINUTE);
    return hour * 60 + minute;
  }

  @Override
  public String toString() {
    StringBuilder buf = new StringBuilder("Schedule:");
    for( DaySchedule ds: schedules ) {
      buf.append( " " + ds.getDays() + "=" + ds.getTimes().length);
    }
    return buf.toString();
  }

  public String getComment() {
    return comment;
  }

  public void setComment(String comment) {
    this.comment = comment;
  }

  
}

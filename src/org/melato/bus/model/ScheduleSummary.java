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
package org.melato.bus.model;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.melato.util.DateId;


/** Contains information for choosing the right schedule. */
public class ScheduleSummary {
  private ScheduleId[] scheduleIds;
  /** The # of minutes after midnight where the schedule is still considered the previous day's schedule. */
  private int dayChange;


  public ScheduleSummary(ScheduleId[] scheduleIds, int dayChange) {
    super();
    this.scheduleIds = scheduleIds;
    this.dayChange = dayChange;
  }

  public ScheduleId[] getScheduleIds() {
    return scheduleIds;
  }
  
  public ScheduleId getScheduleId(Date date) {
    Calendar cal = new GregorianCalendar();
    cal.setTime(date);
    cal.add(Calendar.MINUTE, -dayChange); // shift the day back.
    int dateId = DateId.dateId(cal);
    for( ScheduleId scheduleId: scheduleIds) {
      if (scheduleId.matchesDateId(dateId)) {
        return scheduleId;
      }
    }
    int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
    for( ScheduleId scheduleId: scheduleIds) {
      if (scheduleId.matchesDayOfWeek(dayOfWeek)) {
        return scheduleId;
      }
    }
    return null;
  }
  
  public int getDayChange() {
    return dayChange;
  }
}

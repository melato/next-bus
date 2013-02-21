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

import java.io.Serializable;
import java.util.Calendar;


/** A schedule maintains departure information for one route and for all days of the week. */
public class ScheduleId implements Comparable<ScheduleId>, Serializable {
  private static final long serialVersionUID = 1L;
  private int   days;  // days is a bitmap, bit 0 = sunday, bit 6 = saturday
  private int dateId;  // The date as an integer, e.g. 20121225
  
  
  private ScheduleId(int days, int dateId) {
    super();
    this.days = days;
    this.dateId = dateId;
  }
  
  public static ScheduleId forWeek(int days) {
    return new ScheduleId(days, 0);
  }
  public static ScheduleId forDate(int dateId) {
    return new ScheduleId(0, dateId);
  }
  
  public int getDays() {
    return days;
  }
  public int getDateId() {
    return dateId;
  }
  @Override
  public String toString() {
    if ( dateId != 0 )
      return "dateId:" + dateId;
    else
      return "days:" + days;
  }
  @Override
  public int compareTo(ScheduleId o) {
    if ( days != 0 && o.days != 0 ) {
      return days - o.days;
    }
    if ( days != 0 && o.days == 0 )
      return -1;
    if ( days == 0 && o.days != 0 )
      return 1;
    return dateId - o.dateId;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + dateId;
    result = prime * result + days;
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    ScheduleId other = (ScheduleId) obj;
    if (dateId != other.dateId)
      return false;
    if (days != other.days)
      return false;
    return true;
  }
  
  public boolean matchesDateId(int dateId) {
    return this.dateId == dateId;
  }
  
  public boolean matchesDayOfWeek(int dayOfWeek) {
    int bitmap = 1 << (dayOfWeek-Calendar.SUNDAY);
    return (days & bitmap) != 0;
  }
}

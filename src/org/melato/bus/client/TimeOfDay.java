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
package org.melato.bus.client;

import org.melato.bus.model.Schedule;

/**
 * An object for displaying a particular schedule time.
 * It mainly has a toString() method for use by an ArrayAdapter.
 * */
public class TimeOfDay {
  /** A time in minutes. */
  private int timeMinutes;
  
  /** An offset in seconds. */
  private int offsetSeconds; 
  
  
  public TimeOfDay(int minutes) {
    this.timeMinutes = minutes;
  }

  public TimeOfDay(int minutes, int offsetSeconds) {
    this.timeMinutes = minutes;
    this.offsetSeconds = offsetSeconds;
  }

  @Override
  public String toString() {
    return Schedule.formatTime(timeMinutes + offsetSeconds/60);
    /*
    if ( offsetSeconds == 0 ) {
      return Schedule.formatTime(timeMinutes);
    } else {
      return Schedule.formatTime(timeMinutes + offsetSeconds/60) + " (" + 
          Schedule.formatTime(timeMinutes) + "+" + Schedule.formatDuration(offsetSeconds) + ")";
    }
    */
  }    
}


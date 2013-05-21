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

/** A textual note about specific times of a route's schedule.
 * An exception may note a deviation from the published itinarary.
 * */
public class RouteException {
  /** The text of the exception. */
  private String  note;
  /** The days bitmap of the exception. */
  private int days;
  /** The times when the exception applies, in minutes since midnight. */
  private int[] times;
  
  public String getNote() {
    return note;
  }
  public void setNote(String note) {
    this.note = note;
  }
  public int getDays() {
    return days;
  }
  public void setDays(int days_bitmap) {
    this.days = days_bitmap;
  }
  public int[] getTimes() {
    return times;
  }
  public void setTimes(int[] times) {
    this.times = times;
  }
  @Override
  public String toString() {
    return note;
  }
}

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

import java.util.Comparator;

import org.melato.gps.Point2D;
import org.melato.gps.PointTime;

/** A route stop.
 * consider merging with RouteStop.  
 * @author Alex Athanasopoulos
 */
public class Stop extends PointTime {
  private static final long serialVersionUID = 1L;
  /** The stop is timed. */
  public static final int FLAG_TIMED = 0x1;
  /** This is the last stop in the route. */
  public static final int FLAG_LAST = 0x2;
  /** Stop is for pickup only */
  public static final int FLAG_NO_DROPOFF = 0x4;
  /** Stop is for dropoff only */
  public static final int FLAG_NO_PICKUP = 0x8;
  /** Stop is a station */
  public static final int FLAG_STATION = 0x10;
  private String name;
  private String symbol;
  private int   flags;
  private int   index;
  private float deviation = 1;
  
  public static class IndexComparator implements Comparator<Stop> {
    @Override
    public int compare(Stop s1, Stop s2) {
      return s1.getIndex() - s2.getIndex();
    }    
  }
  
  public Stop() {
    super();
  }
  public Stop(float lat, float lon) {
    super(lat, lon);
  }
  
  public Stop(Point2D p) {
    super(p);
  }
  public Stop(PointTime p) {
    super(p);
  }
  public String getName() {
    return name;
  }  
  public void setName(String name) {
    this.name = name;
  }  
  public int getFlags() {
    return flags;
  }
  public void setFlag(int flag) {
    this.flags = flags | flag;
  }
  public boolean isFlag(int flag) {
    return (flags & flag) != 0;
  }
  public void setFlags(int flags) {
    this.flags = flags;
  }
  public boolean isTimed() {
    return (flags & FLAG_TIMED) != 0; 
  }
  public boolean isLast() {
    return (flags & FLAG_LAST) != 0; 
  }
  public boolean isDropoff() {
    return (flags & FLAG_NO_DROPOFF) == 0; 
  }
  public boolean isPickup() {
    return (flags & FLAG_NO_PICKUP) == 0; 
  }
  public boolean isStation() {
    return isFlag(FLAG_STATION);
  }
  public String getSymbol() {
    return symbol;
  }
  public void setSymbol(String symbol) {
    this.symbol = symbol;
  }
  public float getDeviation() {
    return deviation;
  }
  public void setDeviation(float deviation) {
    this.deviation = deviation;
  }
  public int getIndex() {
    return index;
  }
  public void setIndex(int index) {
    this.index = index;
  }
  public boolean isBefore(Stop stop2) {
    return index < stop2.index;
  }
  @Override
  public String toString() {
    return symbol + ":" + name + " " + super.toString();
  }
  
  public int getSecondsFromStart() {
    return (int) (getTime() / 1000);
  }
  
}

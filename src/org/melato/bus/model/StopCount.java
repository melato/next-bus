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

public class StopCount {
  public int stops;
  public int timedStops;
  @Override
  public String toString() {
    return timedStops + "/" + stops;
  }
  /** Count the number of stops that are timed within a range of stops.
   * @param stops
   * @param start The first stop to count.  Stop 0 should not be counted, so start from 1.
   * @param end The stop after the last one to count.
   * @return
   */
  public StopCount(Stop[] stops, int start, int end) {
    this.stops = end - start;
    if ( this.stops < 0 ) {
      this.stops = 0;
    }
    timedStops = 0;
    for( int i = start; i < end; i++ ) {
      if ( stops[i].isTimed() ) {
        timedStops++;
      }
    }
  }    
}

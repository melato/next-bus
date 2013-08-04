/*-------------------------------------------------------------------------
 * Copyright (c) 2012,2013, Alex Athanasopoulos.  All Rights Reserved.
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
package org.melato.geometry.util;

/** A sub-sequence of increasing indexes */ 
public class Sequence {
  /** The first index in the sequence */
  int start;
  /** The last index in the sequence */
  int last;
  int length;
  
  
  /** for debugging */
  @Override
  public String toString() {
    return "[start=" + start + " last=" + last + " lenght=" + length + "]";
  }
  
  /** Remove out-of-order indexes inside the sequence */
  void clearInside(int[] indexes) {
    int a = indexes[start];
    for( int i = start; i <= last; i++ ) {
      int b = indexes[i];
      if ( b != -1 ) {
        if ( b == a + 1 || b == a ) {          
          a = b;
        } else {
          indexes[i] = -1;
        }
      }
    }
  }
  /** Remove out-of-order indexes to the left of the sequence */
  void clearLeft(int[] indexes, int start) {
    int first = indexes[this.start];
    for( int i = start; i < this.start; i++ ) {
      int a = indexes[i];
      if ( a != -1 && a > first ) {
        indexes[i] = -1;
      }
    }
  }
  /** Remove out-of-order indexes to the right of the sequence */
  void clearRight(int[] indexes, int end) {
    int last = indexes[this.start] + length;
    for( int i = this.last + 1; i < end; i++ ) {
      int a = indexes[i];
      if ( a != -1 && a < last ) {
        indexes[i] = -1;
      }
    }
  }
}


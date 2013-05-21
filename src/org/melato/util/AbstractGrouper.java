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
package org.melato.util;

/** Abstract class that facilitates spliting an array of objects into groups of contiguous items.
 */
public abstract class AbstractGrouper<T> {
  /** Determines whether two consecutive items are in the same group. */
  protected abstract boolean inSameGroup(T item, T nextItem);
  protected abstract void addGroup(T[] array, int start, int end);

  public void group(T[] array) {
    if ( array.length == 0 )
      return;
    int start = 0;
    for( int i = 1; i < array.length; i++ ) {
      if ( ! inSameGroup(array[i-1], array[i])) {
        addGroup(array, start, i);
        start = i;
      }
    }
    if ( start != array.length )
      addGroup(array, start, array.length);
  }
}

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

import java.util.Arrays;
import java.util.List;

/** Abstract class that facilitates spliting an array of objects into groups of contiguous items.
 */
public abstract class AbstractListGrouper<T> {
  /** Determines whether two consecutive items are in the same group. */
  protected abstract boolean inSameGroup(T item, T nextItem);
  protected void addGroup(List<T> group) {    
  }
  protected void addGroup(List<T> items, int start, int end) {
    addGroup(items.subList(start,  end));
  }

  public void group(List<T> items) {
    if ( items.isEmpty())
      return;
    int start = 0;
    int size = items.size();
    for( int i = 1; i < size; i++ ) {
      if ( ! inSameGroup(items.get(i-1), items.get(i))) {
        addGroup(items, start,  i);
        start = i;
      }
    }
    if ( start != size )
      addGroup(items, start, size);
  }
  
  public void group(T[] items) {
    group(Arrays.asList(items));
  }
}

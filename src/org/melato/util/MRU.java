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

/**
 * Most-Recently-Used list.
 * A list that keeps the last N added items, so that item(0) is the most recently added item.
 * @author Alex Athanasopoulos
 * @param <T>
 */
public class MRU<T> extends CircularList<T> {
  /** Recent items go to the front. */
  public MRU() {
    this(10);
  }
  
  public MRU(int maxSize) {
    super(maxSize);
  }

  private int findItem(T item) {
    int size = size();
    for( int i = 0; i < size; i++ ) {
      if ( item.equals(get(i))) {
        return i;
      }
    }
    return -1;
  }
  
  @Override
  public boolean add(T item) {
    boolean added = true;
    int index = findItem(item);
    if ( index >= 0 ) {
      added = false;
      remove(index);
    }
    add(0, item);
    return added;
  }
}

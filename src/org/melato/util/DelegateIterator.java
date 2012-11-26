/*-------------------------------------------------------------------------
 * Copyright (c) 2012, Alex Athanasopoulos.  All Rights Reserved.
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

import java.util.Iterator;

/**
 * Converts List<S> into a List<T>, provided S is a subclass of T.
 * (Since Java doesn't do this automatically)
 * @author Alex Athanasopoulos
 *
 * @param <S> Subclass of T
 * @param <T> Any class
 */
public class DelegateIterator<T> implements Iterator<T> {
  Iterator<? extends T> iterator;

  
  public DelegateIterator(Iterator<? extends T> iterator) {
    super();
    this.iterator = iterator;
  }

  @Override
  public boolean hasNext() {
    return iterator.hasNext();
  }

  @Override
  public T next() {
    T t = (T) iterator.next();
    return t;
  }

  @Override
  public void remove() {
    iterator.remove();
  }
  
}

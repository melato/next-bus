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

import java.util.AbstractList;

/** A fixed-size collection where old items are discarded. */
public class CircularList<T> extends AbstractList<T> {
  private Object[] data;
  /** The index of the first item in the list. */
  private int start = 0;
  /** The index past the last item in the list.
   * When the list is full, start == end
   * */
  private int end = 0;
  private int size;
  private boolean autoresize; 
  
  public CircularList( int length) {
    data = new Object[length];
  }
  
  public CircularList() {
    this(10);
    autoresize = true;
  }
  
  /** Return the number of items in the list.  Between 0 and size. */
  public int size() {
    return size;
  }
    
  /**
   * Get an item from the list.
   */
  @SuppressWarnings("unchecked")
  public T get(int i) {
    int size = size();    
    if ( i < 0 || i >= size ) {
      throw new ArrayIndexOutOfBoundsException();
    }
    return (T) data[(start + i) % data.length];
  }
  
  @Override
  public boolean add(T e) {
    if ( ! autoresize ) {
      data[end] = e;
      end = (end + 1) % data.length;
      if ( size == data.length ) {
        start = end;
      } else {
        size++;
      }      
    } else {
      add(size(), e);
    }
    return true;
  }
  
  @Override
  public void clear() {
    start = 0;
    end = 0;
    size = 0;
  }

  private void resize() {
    int start = this.start;
    int size = this.size;
    Object[] data = this.data;
    this.data = new Object[this.data.length*2];
    for( int i = 0; i < size; i++ ) {
      this.data[i] = data[(start+i)%data.length];
    }
    this.start = 0;
    end = size;
  }
  
  @Override
  public void add(int index, T e) {
    if ( data.length == size && autoresize ) {
      resize();
    }
    int length = data.length;
    //System.out.println( "start=" + start + " end=" + end + " index=" + index);
    if ( index > size / 2 + 1 ) {
      //System.out.println( "shift right" );
      // shift the right part to the right
      for( int j = size - 1; j >= index; j-- ) {
        // index is > 0, so j - 1 is >= 0
        data[(start + j) % length] = data[(start + j - 1) % length];
      }
      end = (end + 1) % length;
      if ( size == length ) {
        start = end;
      } else {
        size++;
      }
    } else {
      //System.out.println( "shift left" );
      // shift the left part to the left
      for( int j = 0; j < index; j++ ) {
        data[(start + j - 1 + length) % length] = data[(start + j) % length];
      }
      start = (start - 1 + length) % length;
      if ( size == length ) {
        end = start;
      } else {
        size++;
      }      
    }
    data[(start + index) % length] = e;
    //System.out.println( "start=" + start + " end=" + end + " 0=" + get(0));
  }

  @Override
  public T remove(int index) {
    T t = get(index);    
    int length = data.length;
    if ( index > size / 2 ) {
      // shift the right part to the left
      for( int j = index + 1; j < size; j++ ) {
        data[(start + j - 1) % length] = data[(start + j) % length];
      }
      end = (end - 1) % length;
    } else {
      // shift the left part to the right
      for( int j = index; j > 0; j-- ) {
        data[(start + j) % length] = data[(start + j - 1) % length];
      }
      start = (start + 1 + length) % length;
    }
    size--;
    return t;
  }
}

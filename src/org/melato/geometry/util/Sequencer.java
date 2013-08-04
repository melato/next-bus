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

public class Sequencer {
  private int[] array;
  private boolean[] visited;
  
  public Sequencer(int[] array) {
    super();
    this.array = array;
    visited = new boolean[array.length];
  }

  public void findSequence(int start, int end, Sequence sequence) {
    int a = array[start];
    sequence.start = start;
    sequence.last = start;
    sequence.length = 1;
    int j = start + 1;
    for( ; j < end ;j++ ) {
      int b = array[j];
      if ( b != -1 && ! visited[j]) {
        if ( b == a + 1 ) {
          a = b;
          sequence.length++;
        } else if ( b != a ) {
          continue;
        }
        sequence.last = j;
        visited[j] = true;
      }
    }
    System.out.println( "findSequence start=" + start + " end=" + end + " sequence=" + sequence);
  }
  
  private void filter() {
    System.out.println( "approaches sorted: " + toString(array, 0, array.length));
    removeOutOfOrder(0, array.length );
    //System.out.println( "approaches in-order: " + toString(approaches, 0, approaches.length));
    removeDuplicates();
    System.out.println( "approaches unique: " + toString(array, 0, array.length));
    
  }
  
  /** remove array so that the remaining array are in non-decreasing order.
   * array are removed by setting them to -1.
   * */ 
  private void removeOutOfOrder(int start, int end) {
    if ( end <= start )
      return;
    for( int i = start; i < end; i++ ) {
      visited[i] = false;
    }
    Sequence bestSequence = null;
    Sequence sequence = new Sequence();
    
    // find the longest sub-sequence of sequential or equal array
    for( int i = start; i < end; i++ ) {
      int a = array[i];
      if ( a != -1 ) {
        //System.out.println( "i=" + i + " visited=" + a.visited);
        if ( visited[i] )
          continue;
        findSequence(i, end, sequence);
        if ( bestSequence == null || sequence.length > bestSequence.length ) {
          bestSequence = sequence;
          sequence = new Sequence();
        }
      }
    }
    if ( bestSequence == null ) {
      // there is nothing
      return;
    }
    System.out.println( "best sequence: " + bestSequence);
    bestSequence.clearInside(array);
    bestSequence.clearLeft(array, start);
    bestSequence.clearRight(array, end);
    
    //System.out.println( "a: " + toString( approaches, 0, approaches.length ));
    // do the same on each side
    removeOutOfOrder( start, bestSequence.start);
    removeOutOfOrder( bestSequence.last + 1, end );
    //System.out.println( "b: " + toString( approaches, 0, approaches.length ));
  }

  private void removeDuplicates() {
    // keep the last position that has the lowest element
    int item = -1;
    int lastIndex = -1;
    int i = 0;
    for( ; i < array.length; i++ ) {
      int a = array[i];
      if ( a != -1 ) {
        if ( item == -1 ) {
          item = a;
          lastIndex = i;
        } else if ( item == a ) {
            array[lastIndex] = -1;
            lastIndex = i;
        } else {
          item = a;
          i++;
          break;
        }
      }
    }
    
    // for subsequent array, keep the first one among equal elements
    for( ; i < array.length; i++ ) {
      int a = array[i];
      if ( a != -1 ) {
        if ( item == a ) {
          array[i] = -1;
        } else {
          item = a;
        }
      }      
    }
  }

  public static String toString( int[] array, int start, int end ) {
    StringBuilder buf = new StringBuilder();
    buf.append( "[");
    int count = 0;
    for( int i = start; i < end; i++ ) {
      int a = array[i];
      if ( a != -1 ) {
        if ( count > 0 ) {
          buf.append( " " );
        }
        count++;
        buf.append( String.valueOf(a) );
      }
    }
    buf.append("]");
    return buf.toString();
  }
  
  /** Find a subsequence of increasing items by setting the remaining items to -1. */
  public static void filter(int[] items) {
    Sequencer sequencer = new Sequencer(items);
    sequencer.filter();
  }
}


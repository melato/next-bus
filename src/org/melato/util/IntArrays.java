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
import java.util.List;

public class IntArrays {
  public static List<Integer> asList(int[] array) {
    return new IntArrayList(array);
  }
  public static int[] toArray(List<Integer> list) {
    int[] array = new int[list.size()];
    for( int i = 0; i < array.length; i++ ) {
      array[i] = list.get(i);
    }
    return array;
  }
  
  private static class IntArrayList extends AbstractList<Integer> {
    private int[] array;
    
    
    public IntArrayList(int[] array) {
      this.array = array;
    }

    @Override
    public Integer get(int index) {
      return array[index];
    }

    @Override
    public int size() {
      return array.length;
    }

  }
  
}

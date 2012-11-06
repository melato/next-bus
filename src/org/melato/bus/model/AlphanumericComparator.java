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
package org.melato.bus.model;

import java.util.Comparator;

public class AlphanumericComparator implements Comparator<String> {
  public static final AlphanumericComparator INSTANCE = new AlphanumericComparator(); 
  private static int findDigitsEnd(String s, int start) {
    int len = s.length();
    for( int i = start; i < len; i++ ) {
      if ( ! Character.isDigit(s.charAt(i))) {
        return i;        
      }
    }
    return len;
  }
  /**
   * Compare numbers as numbers and strings as strings.
   * Compare two strings, character by character.
   * If a digit is found in both strings at the same position,
   * compare all consecutive digits as a single number.
   * For example:  a9b < a10b (ordinary string comparison would return a10 < a9).
   * @param x
   * @param y
   * @return
   */
  @Override
  public int compare(String s1, String s2) {
    int len1 = s1.length();
    int len2 = s2.length();
    for( int i = 0; i < len1 && i < len2; i++ ) {
      char c1 = s1.charAt(i);
      char c2 = s2.charAt(i);
      if ( Character.isDigit(c1) && Character.isDigit(c2)) {
        int end1 = findDigitsEnd(s1, i + 1 );
        int end2 = findDigitsEnd(s2, i + 1 );
        if ( end1 != end2 )
          return end1 - end2;
        // numbers are of the same length.  Compare them digit by digit.
        for( int j = i; j < end1; j++ ) {
          c1 = s1.charAt(j);
          c2 = s2.charAt(j);
          if ( c1 != c2 ) {
            return c1 - c2;
          }
        }
        i = end1 - 1;
      } else {
        if ( c1 != c2 ) {
          return c1 - c2;
        }
      }      
    }
    return len1 - len2;
  }
}

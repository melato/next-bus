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
package org.melato.math;

public class MeanStatistics {
  double  sx;
  double  sxx;
  int     n;
  public void add(float x) {
    sx += x;
    sxx += x * x;
    n++;
  }
  public int size() {
    return n;
  }
  public float mean() {
    return (float) (sx/n);
  }
  public float variance() {
    return (float) ((n*sxx-sx*sx)/(n*n));
  }
  public float standardDeviation() {
    return (float) Math.sqrt( variance() * n/(n-1));
  }
  public void mergeWith(MeanStatistics m) {
    sx += m.sx;
    sxx += m.sxx;
    n += m.n;
  }
}

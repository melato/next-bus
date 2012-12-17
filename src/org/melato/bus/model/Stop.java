/*-------------------------------------------------------------------------
 * Copyright (c) 2012, Alex Athanasopoulos.  All Rights Reserved.
 * alex@melato.org
 *-------------------------------------------------------------------------
 * This file is part of Athens Next Bus
 *
 * Athens Next Bus is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Athens Next Bus is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Athens Next Bus.  If not, see <http://www.gnu.org/licenses/>.
 *-------------------------------------------------------------------------
 */
package org.melato.bus.model;

import org.melato.gps.Point2D;
import org.melato.gps.PointTime;

/** A route stop.
 * consider merging with RouteStop.  
 * @author Alex Athanasopoulos
 */
public class Stop extends PointTime {
  private static final long serialVersionUID = 1L;
  String name;
  String symbol;
  float deviation = 1;
  public Stop() {
    super();
  }
  public Stop(float lat, float lon) {
    super(lat, lon);
  }
  
  public Stop(Point2D p) {
    super(p);
  }
  public Stop(PointTime p) {
    super(p);
  }
  public String getName() {
    return name;
  }
  public void setName(String name) {
    this.name = name;
  }
  public String getSymbol() {
    return symbol;
  }
  public void setSymbol(String symbol) {
    this.symbol = symbol;
  }
  public float getDeviation() {
    return deviation;
  }
  public void setDeviation(float deviation) {
    this.deviation = deviation;
  }
  @Override
  public String toString() {
    return symbol + ":" + name + " " + super.toString();
  }
  
}

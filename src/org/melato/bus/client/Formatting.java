/*-------------------------------------------------------------------------
 * Copyright (c) 2012,2013, Alex Athanasopoulos.  All Rights Reserved.
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
package org.melato.bus.client;

import java.text.DecimalFormat;

/** Various static methods to do formatting. */
public class Formatting {
  public static final DecimalFormat KM = new DecimalFormat( "0.00" );
  
  public static String distance(float distance) {
    if ( Math.abs(distance) < 1000 ) {
      return String.valueOf( Math.round(distance)) + "m";
    } else {
      return KM.format(distance/1000) + "Km";
    }
  }
  
  public static String straightDistance(float distance) {
    return "(" + distance(distance) + ")";
  }

  /** Set the bearing in the range (-180,180] */
  public static float normalizeBearing(float degrees) {
    degrees += 180; // adjust this between (0, 360]
    degrees = degrees % 360;
    if ( degrees <= 0 )
      degrees += 360;
    degrees -= 180;
    return degrees;
  }
  
  public static String bearing(float bearing) {
    bearing = normalizeBearing(bearing);
    return Math.round(bearing) + "°"; // degrees, unicode "\u00B0"
  }

  public static String routeDistance(float distance) {
    if ( Float.isNaN(distance)) {
      return "";
    }
    String sign = distance >= 0 ? "+" : "-"; 
    return sign + distance(Math.abs(distance));
  }
  
  public static String degrees(float degrees) {
    return String.valueOf(degrees);
  }
  
}

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
package org.melato.bus.plan;

import java.text.DecimalFormat;

import org.melato.bus.client.Formatting;
import org.melato.gps.GlobalDistance;
import org.melato.gps.Point2D;

public class Walk {
  public static final float OVERHEAD = 1.3f;
  /** Normal walk speed in m/s */
  public static final float SPEED = 5000f/3600;
  public static final DecimalFormat D2 = new DecimalFormat("00");
  private float distance;
  public Walk(Point2D point1, Point2D point2) {
    super();
    distance = new GlobalDistance().distance(point1, point2);
  }
  public static String formatDuration(float time) {
    int seconds = (int) time;
    if ( seconds >= 3600 ) {
      return D2.format(seconds/3600) + ":" + D2.format(seconds%60);
    } else {
      return D2.format(seconds/60) + "'";
    }
  }
  
  public static float duration(float distance) {
    return distance*OVERHEAD/SPEED;
  }
  public static String distanceDuration(float distance) {
    return formatDuration(duration(distance));
  }
  
  @Override
  public String toString() {
    return "Walk " + Formatting.straightDistance(distance) + " " + distanceDuration(distance);
  }
}

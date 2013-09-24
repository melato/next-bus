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

import java.io.Serializable;


public class WalkModel implements Serializable {
  private static final long serialVersionUID = 1L;
  public static final float OVERHEAD = 1.3f;
  /** walk speed in m/s */  
  private float speed;
    
  /**
   * 
   * @param speed walking speed, in m/s
   */
  public WalkModel(float speed) {
    super();
    this.speed = speed;
  }
  
  

  public WalkModel() {
    this( 5000f/3600);
  }



  public float duration(float distance) {
    return distance*OVERHEAD/speed;
  }
  public String distanceDuration(float distance) {
    return Walk.formatDuration(duration(distance));
  }  
}

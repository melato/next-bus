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

/** A sequence scheduled at a particular time */
public class SequenceItinerary implements Serializable {
  private static final long serialVersionUID = 1L;
  /** seconds from midnight */
  public int startTime;
  /** seconds from midnight */
  public int endTime;
  public Leg[] legs;
  
  public SequenceItinerary(Leg[] legs) {
    super();
    this.legs = legs;
    if ( legs.length > 0 ) {
      startTime = legs[0].startTime;
      endTime = legs[legs.length-1].endTime;
    }
  }

  public static class Leg implements Serializable {
    private static final long serialVersionUID = 1L;
    /** The start time, in seconds from midnight. */
    public int startTime;
    /** The end time, in seconds from midnight. */
    public int endTime;
    /** The leg distance from start to finish, in meters. */
    public float distance;
    /** The leg duration from start to finish, in seconds. */
    public int duration;
    
    
    public int getStartTime() {
      return startTime;
    }


    public int getEndTime() {
      return endTime;
    }


    public float getDistance() {
      return distance;
    }


    public int getDuration() {
      return duration;
    }
  }
  
  /** A leg using walking.  */
  public static class WalkLeg extends Leg {    
    private static final long serialVersionUID = 1L;
  }
  
  public static class TransitLeg extends Leg {
    private static final long serialVersionUID = 1L;
    public RouteLeg leg;
    /** The time from the end of the previous transit leg, or -1, in seconds. */
    public int diffTime = -1;
  }  
}

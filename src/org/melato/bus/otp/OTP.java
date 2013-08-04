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
package org.melato.bus.otp;

import java.io.Serializable;
import java.util.Date;


/** Contains various classes that represent an Open Trip Planner plan */
public class OTP {
  /** A plan is the result of an OTP query, consisting of several alternate itineraries to go from A to B. */
  public static class Plan implements Serializable {
    private static final long serialVersionUID = 1L;
    public Itinerary[] itineraries;
  }
  /** An itinerary is a series of legs with various modes (walk, bus, etc.) that go from A to B. */
  public static class Itinerary implements Serializable {
    private static final long serialVersionUID = 1L;
    /** The start time. */
    public Date startTime;
    /** The end time. */
    public Date endTime;
    public Leg[] legs;
  }
  
  /** A Leg is a portion of an itinerary at a particular time (and place).
   * There are subclasses for various modes.
   */ 
  public static class Leg implements Serializable {
    private static final long serialVersionUID = 1L;
    /** The start time. */
    public Date startTime;
    /** The end time. */
    public Date endTime;
    /** The leg distance from start to finish, in meters. */
    public float distance;
    /** The leg duration from start to finish, in seconds. */
    public int duration;
  }
  /** A leg using walking.  */
  public static class WalkLeg extends Leg {    
    private static final long serialVersionUID = 1L;
  }
  public static class Stop implements Serializable {
    private static final long serialVersionUID = 1L;
    /** The human-readable name of the stop. */
    public String name;
    /** The internal stop code. */
    public String stopCode;
    /** The stop id.  May be the same as the stop code. */
    public String id;
    /** The agency id. */
    public String agencyId;
  }
  /** A leg using transit. */
  public static class TransitLeg extends Leg {
    private static final long serialVersionUID = 1L;
    /** The route id of the transit route */
    public String routeId;
    /** The short label of the route */
    public String label;
    /** The starting stop. */
    public Stop from;
    /** The end stop. */
    public Stop to;
  }
  public static interface Planner {
    Plan plan(OTPRequest request) throws Exception;
  }
}

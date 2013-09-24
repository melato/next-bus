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

import org.melato.bus.model.Schedule;
import org.melato.bus.otp.OTP;

public class OTPLegAdapter implements LegAdapter {
  private OTP.Leg leg;
  private OTP.TransitLeg transit;
  private OTP.WalkLeg walk;

  
  public OTPLegAdapter(OTP.Leg leg) {
    super();
    this.leg = leg;
    if ( leg instanceof OTP.TransitLeg ) {
      transit = (OTP.TransitLeg) leg;
    }
    if ( leg instanceof OTP.WalkLeg ) {
      walk = (OTP.WalkLeg) leg;
    }
  }

  @Override
  public String getLabel() {
    return transit.label;
  }

  @Override
  public String getFromName() {
    return transit.from.name;
  }

  @Override
  public String getToName() {
    return transit.to.name;
  }

  @Override
  public int getStartTime() {
    return Schedule.getSeconds(leg.startTime);
  }

  @Override
  public int getEndTime() {
    return Schedule.getSeconds(leg.endTime);
  }

  @Override
  public int getDuration() {
    return leg.getDuration();
  }

  @Override
  public int getDiffTime() {
    return transit != null ? transit.diffTime : -1;
  }

  @Override
  public float getDistance() {
    return leg.distance;
  }

  @Override
  public boolean isWalk() {
    return walk != null;
  }
}


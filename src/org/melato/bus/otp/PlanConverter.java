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

import java.util.ArrayList;
import java.util.List;

import org.melato.bus.model.Route;
import org.melato.bus.model.RouteId;
import org.melato.bus.model.RouteManager;
import org.melato.bus.model.Stop;
import org.melato.bus.plan.RouteLeg;
import org.melato.bus.plan.Plan;
import org.melato.bus.plan.PlanLeg;
import org.melato.bus.plan.Sequence;
import org.melato.gps.Point2D;

/** Converts OTP structures to our model. */
public class PlanConverter {
  private RouteManager routeManager;
  
  public static class MismatchException extends Exception {
    private static final long serialVersionUID = 1L;

    public MismatchException(String message) {
      super(message);
    }
  }
    
  public PlanConverter(RouteManager routeManager) {
    super();
    this.routeManager = routeManager;
  }

  public void setRouteManager(RouteManager routeManager) {
    this.routeManager = routeManager;
  }

  private Stop findStop(RouteId route, Stop[] stops, int offset, String code) throws MismatchException {
    for( int i = offset; i < stops.length; i++ ) {
      Stop stop = stops[i];
      if ( stop.getSymbol().equals(code)) {
        return stop;
      }
    }
    throw new MismatchException("cannot find stop: " + route + "(" + offset + ") -" + code + "-");
  }

  public RouteLeg convertLeg(OTP.TransitLeg t) throws MismatchException {
    RouteId routeId = new RouteId(t.routeId);
    if ( routeManager.getRoute(routeId) == null) {
      throw new MismatchException("Unknown route: " + t.routeId);
    }
    Stop[] stops = routeManager.getStops(routeId);
    Stop stop1 = findStop(routeId, stops, 0, t.from.stopCode);
    Stop stop2 = findStop(routeId, stops, stop1.getIndex(), t.to.stopCode);
    return new RouteLeg(routeId, stop1, stop2);    
  }
  
  public Sequence convertToSequence(OTP.Itinerary it) throws MismatchException {
    Sequence sequence = new Sequence();
    for( OTP.Leg otpLeg: it.legs) {
      if ( otpLeg instanceof OTP.TransitLeg ) {
        OTP.TransitLeg t = (OTP.TransitLeg) otpLeg;
        sequence.addLeg(convertLeg(t));
      }
    }
    return sequence;    
  }
  
  public Plan convertItinerary(OTP.Itinerary it, Point2D origin, Point2D destination) throws MismatchException {
    List<PlanLeg> legs = new ArrayList<PlanLeg>();
    for(OTP.Leg otpLeg: it.legs){
      if ( otpLeg instanceof OTP.TransitLeg) {
        OTP.TransitLeg transitLeg = (OTP.TransitLeg)otpLeg;
        RouteId routeId = new RouteId(transitLeg.routeId);
        Route route = routeManager.getRoute(routeId);
        if ( route == null) {
          throw new MismatchException( "cannot find route: " + transitLeg.routeId);
        }
        Stop[] stops = routeManager.getStops(routeId);
        Stop stop1 = findStop(routeId, stops, 0, transitLeg.from.id);
        Stop stop2 = findStop(routeId, stops, stop1.getIndex() + 1, transitLeg.to.id);        
        PlanLeg leg = new PlanLeg(route, stop1, stop2);
        legs.add(leg);
      }
    }
    return new Plan(origin, destination, legs.toArray(new PlanLeg[0]));
  }
  
  public Plan[] convertPlan(OTP.Plan otp, OTPRequest request) {
    List<Plan> plans = new ArrayList<Plan>();
    for(OTP.Itinerary it: otp.itineraries) {
      try {
        Plan plan = convertItinerary(it, request.getFromPoint(), request.getToPoint());
        plans.add(plan);
      } catch(MismatchException e) {
        System.out.println(e);
      }
    }
    return plans.toArray(new Plan[0]);
  }
}

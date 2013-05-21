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

import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.melato.bus.otp.OTP.Itinerary;
import org.melato.bus.otp.OTP.Leg;
import org.melato.bus.otp.OTP.Plan;
import org.melato.bus.otp.OTP.TransitLeg;
import org.melato.bus.otp.OTP.WalkLeg;


/** Parses OTP plan file from JSON */
public class OTPParser {
  static String getStopId(JSONObject leg, String endPoint) throws JSONException {
    JSONObject stop = leg.getJSONObject(endPoint);
    JSONObject stopId = stop.getJSONObject("stopId");
    return stopId.getString("id");    
  }
  static Leg parseLeg(JSONObject json) throws JSONException {
    String mode = json.getString("mode");
    Leg leg = null;
    if ( "WALK".equals(mode)) {
      WalkLeg walk = new WalkLeg();
      leg = walk;
    } else if ( "BUS".equals(mode)) {
      TransitLeg transit = new TransitLeg();
      transit.routeId = json.getString("routeId");
      transit.fromStopId = getStopId(json, "from");
      transit.toStopId = getStopId(json, "to");
      leg = transit;
    }
    leg.distance = (float) json.getDouble("distance");
    leg.duration = (int) (json.getLong("duration")/1000L);
    leg.startTime = new Date(json.getLong("startTime"));
    leg.endTime = new Date(json.getLong("endTime"));
    return leg;    
  }
  static Itinerary parseItinerary(JSONObject json) throws JSONException {
    JSONArray legs = json.getJSONArray("legs");
    Itinerary itinerary = new Itinerary();
    itinerary.legs = new Leg[legs.length()];
    for( int i = 0; i < itinerary.legs.length; i++ ) {
      itinerary.legs[i] = parseLeg(legs.getJSONObject(i));
    }
    return itinerary;
  }
  public static Plan parse(String data) {
    try {
      JSONObject json = new JSONObject(data);
      JSONObject jsonPlan = json.getJSONObject("plan");
      JSONArray jsonItineraries = jsonPlan.getJSONArray("itineraries");
      Itinerary[] itineraries = new Itinerary[jsonItineraries.length()];
      for( int i = 0; i < itineraries.length; i++ ) {
        itineraries[i] = parseItinerary(jsonItineraries.getJSONObject(i));
      }
      Plan plan = new Plan();
      plan.itineraries = itineraries;
      return plan;
    } catch (JSONException e) {
      e.printStackTrace();
      return null;
    }
  }
}

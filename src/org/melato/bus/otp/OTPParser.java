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
import org.melato.bus.otp.OTP.Stop;
import org.melato.bus.otp.OTP.TransferLeg;
import org.melato.bus.otp.OTP.TransitLeg;
import org.melato.bus.otp.OTP.WalkLeg;


/** Parses OTP plan file from JSON */
public class OTPParser {
  static Stop getStop(JSONObject leg, String endPoint) throws JSONException {
    JSONObject jsonStop = leg.getJSONObject(endPoint);
    Stop stop = new Stop();
    stop.name = jsonStop.getString("name");
    stop.stopCode = jsonStop.getString("stopCode");
    JSONObject stopId = jsonStop.getJSONObject("stopId");
    stop.id = stopId.getString("id");
    stop.agencyId = stopId.getString("agencyId");
    return stop;
  }
  static Leg parseLeg(JSONObject json) throws JSONException {
    String mode = json.getString("mode");
    Leg leg = null;
    if ( "WALK".equals(mode)) {
      WalkLeg walk = new WalkLeg();
      leg = walk;
    } else if ( "TRANSFER".equals(mode)) {
      TransferLeg transfer = new TransferLeg();
      transfer.from = getStop(json, "from");
      transfer.to = getStop(json, "to");
      leg = transfer;
    } else {
      TransitLeg transit = new TransitLeg();
      transit.routeId = json.getString("routeId");
      transit.label = json.getString("routeShortName");
      transit.from = getStop(json, "from");
      transit.to = getStop(json, "to");
      leg = transit;
    }
    leg.distance = (float) json.getDouble("distance");
    leg.duration = (int) (json.getLong("duration")/1000L);
    leg.startTime = new Date(json.getLong("startTime"));
    leg.endTime = new Date(json.getLong("endTime"));
    leg.mode = mode;
    return leg;    
  }
  static Itinerary parseItinerary(JSONObject json) throws JSONException {
    JSONArray legs = json.getJSONArray("legs");
    Itinerary itinerary = new Itinerary();
    itinerary.legs = new Leg[legs.length()];
    itinerary.startTime = new Date(json.getLong("startTime"));    
    itinerary.endTime = new Date(json.getLong("endTime"));    
    for( int i = 0; i < itinerary.legs.length; i++ ) {
      itinerary.legs[i] = parseLeg(legs.getJSONObject(i));
    }
    return itinerary;
  }
  static JSONObject getObject(JSONObject json, String key) throws JSONException {
    if ( json.has(key) && ! json.isNull(key)) {
      return json.getJSONObject(key);
    }
    return null;
  }
  static OTP.Error parseError(JSONObject json) throws JSONException {
    json = getObject(json, "error");
    if (json == null) {
      return null;
    }
    OTP.Error error = new OTP.Error();
    error.id = json.getInt("id");
    error.msg = json.getString("msg");
    return error;
  }
  public static Plan parse(String data) {
    try {
      Plan plan = new Plan();
      plan.itineraries = new Itinerary[0];
      JSONObject json = new JSONObject(data);
      JSONObject jsonPlan = getObject(json, "plan");
      if ( jsonPlan != null) {
        JSONArray jsonItineraries = jsonPlan.getJSONArray("itineraries");
        if ( jsonItineraries != null) {
          Itinerary[] itineraries = new Itinerary[jsonItineraries.length()];
          for( int i = 0; i < itineraries.length; i++ ) {
            itineraries[i] = parseItinerary(jsonItineraries.getJSONObject(i));
          }
          plan.itineraries = itineraries;
        }
      }
      plan.error = parseError(json);
      return plan;
    } catch (JSONException e) {
      e.printStackTrace();
      return null;
    }
  }
}

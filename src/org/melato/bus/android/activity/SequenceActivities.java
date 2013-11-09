/*-------------------------------------------------------------------------
 * Copyright (c) 2012,2013 Alex Athanasopoulos.  All Rights Reserved.
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
package org.melato.bus.android.activity;

import org.melato.bus.android.map.SequenceMapActivity;
import org.melato.bus.otp.OTP;
import org.melato.bus.plan.Sequence;

import android.content.Context;
import android.content.Intent;

public class SequenceActivities {
  private static boolean useMap;
  
  public static void showItinerary(Context context, OTP.Itinerary itinerary) {
    if ( useMap ) {
      showMap(context, itinerary);      
    } else {
      showList(context, itinerary);      
    }
  }
  
  public static void showList(Context context, OTP.Itinerary itinerary) {
    useMap = false;
    Intent intent = new Intent(context, OTPItineraryActivity.class);
    intent.putExtra(Keys.ITINERARY, itinerary);
    context.startActivity(intent);
  }
  
  public static void showMap(Context context, OTP.Itinerary itinerary) {
    useMap = true;
    Intent intent = new Intent(context, SequenceMapActivity.class);
    intent.putExtra(Keys.ITINERARY, itinerary);
    context.startActivity(intent);
  }
  
  public static void showMap(Context context, Sequence sequence) {
    Intent intent = new Intent(context, SequenceMapActivity.class);
    intent.putExtra(Keys.SEQUENCE, sequence);
    context.startActivity(intent);    
  }
}

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
package org.melato.bus.android;

import org.melato.bus.android.activity.Pref;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/** Contains preference values related to planning an itinerary */
public class PlanOptions {
  private int maxWalk;
  private float walkSpeed;
  private boolean fewerTransfers;
  
  /** Get the walk speed in m/s */
  public float getWalkSpeedMetric() {
    return walkSpeed * 1000f / 3600f;
  }
  
  public int getMaxWalk() {
    return maxWalk;
  }

  /** Get the walk speed in Km/h */
  public float getWalkSpeed() {
    return walkSpeed;
  }

  public boolean isFewerTransfers() {
    return fewerTransfers;
  }

  static int getInt(SharedPreferences prefs, String key, int defaultValue) {
    try {
      String s = prefs.getString(key, null);
      if ( s != null ) {
        return Integer.parseInt(s);
      }
    } catch( ClassCastException e ) {          
    } catch( NumberFormatException e ) {          
    }
    return defaultValue;
  }
  static float getFloat(SharedPreferences prefs, String key, float defaultValue) {
    try {
      String s = prefs.getString(key, null);
      if ( s != null ) {
        return Float.parseFloat(s);
      }
    } catch( ClassCastException e ) {          
    } catch( NumberFormatException e ) {          
    }
    return defaultValue;
  }
  static boolean getBoolean(SharedPreferences prefs, String key, boolean defaultValue) {
    try {
      String s = prefs.getString(key, null);
      if ( s != null ) {
        return Boolean.parseBoolean(s);
      }
    } catch( Exception e ) {          
    }
    return defaultValue;
  }
  public PlanOptions(Context context) {
    SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
    maxWalk = getInt(settings, Pref.MAX_WALK_DISTANCE, 1000);
    walkSpeed = getFloat(settings, Pref.WALK_SPEED, 5.0f);
    fewerTransfers = getBoolean(settings, Pref.FEWER_TRANSFERS, true);    
  }
}


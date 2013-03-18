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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.melato.android.AndroidLogger;
import org.melato.bus.android.db.SqlRouteStorage;
import org.melato.bus.android.map.RoutePointManager;
import org.melato.bus.client.NearbyManager;
import org.melato.bus.model.Agency;
import org.melato.bus.model.RouteId;
import org.melato.bus.model.RouteManager;
import org.melato.log.Log;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

/** Provides access to global (static) objects. */
public class Info {
  public static final float MARK_PROXIMITY = 200f;
  private static RouteManager routeManager;
  private static AndroidTrackHistory trackHistory;
  private static Map<String,Drawable.ConstantState> agencyIcons = new HashMap<String,Drawable.ConstantState>();
  
  public static RouteManager routeManager(Context context) {
    if ( routeManager == null ) {
      synchronized(Info.class) {
        if ( routeManager == null ) {
          context = context.getApplicationContext();
          Log.setLogger(new AndroidLogger(context));
          routeManager = new RouteManager(new SqlRouteStorage(context));          
        }
      }
    }
    return routeManager;
  }
  
  public static boolean isValidDatabase(Context context) {
    File file = SqlRouteStorage.databaseFile(context);
    if ( ! file.exists() )
      return false;
    SqlRouteStorage storage = (SqlRouteStorage) routeManager(context).getStorage();
    if ( storage.checkVersion() )
      return true;
    reload();
    return false;
  }
  
  public static AndroidTrackHistory trackHistory(Context context) {
    if ( trackHistory == null ) {
      synchronized(Info.class) {
        if ( trackHistory == null ) {
          context = context.getApplicationContext();
          trackHistory = new AndroidTrackHistory(context);          
        }
      }
    }
    return trackHistory;
  }  
  
  public static NearbyManager nearbyManager(Context context) {
    File cacheDir = context.getCacheDir();
    return new NearbyManager(routeManager(context), cacheDir); 
  }
  /** Uncache any database data, in order to use a newly downloaded database. */
  public static void reload() {
    routeManager = null;
    trackHistory = null;
    RoutePointManager.reload();
  }
  
  public static synchronized Drawable getAgencyIcon(Context context, Agency agency) {
    String agencyName = agency.getName();
    Drawable.ConstantState state = agencyIcons.get(agencyName);
    if ( state == null && ! agencyIcons.containsKey(agencyName)) {
      byte[] icon = agency.getIcon();
      if ( icon != null) {
        Drawable drawable = new BitmapDrawable(context.getResources(), new ByteArrayInputStream(icon));
        state = drawable.getConstantState();
      }    
      agencyIcons.put(agencyName, state);
    }
    if ( state != null) {
      return state.newDrawable(context.getResources());
    } else {
      return null;
    }
  }
  public static synchronized Drawable getAgencyIcon(Context context, RouteId routeId) {
    Agency agency = Info.routeManager(context).getAgency(routeId);
    return getAgencyIcon(context, agency);
  }
}

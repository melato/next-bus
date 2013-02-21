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
package org.melato.bus.android.map;

import java.util.HashSet;
import java.util.Set;

import org.melato.bus.model.RouteId;
import org.melato.gps.Point2D;

import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

public abstract class BaseRoutesOverlay extends Overlay {
  /** Routes that always display, besides other routes */
  protected static Set<RouteId> pinnedRoutes = new HashSet<RouteId>();

  public abstract void addRoute(RouteId routeId);

  public abstract void setSelectedRoute(RouteId routeId);
  
  public abstract void setSelectedStop(Point2D stop);

  public abstract void refresh(MapView view);
  
  public static void pinRoute(RouteId routeId) {
    pinnedRoutes.add(routeId);
  }
  
  public static void unpinRoute(RouteId routeId) {
    pinnedRoutes.remove(routeId);
  }
    
  public static boolean isPinned(RouteId routeId) {
    return pinnedRoutes.contains(routeId); 
  }
  public static void unpinAll() {
    pinnedRoutes.clear();
  }
}
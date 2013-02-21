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

import java.util.Collections;
import java.util.List;

import org.melato.bus.model.Route;

import android.os.Bundle;

/**
 * Displays the list of recent routes
 * @author Alex Athanasopoulos
 *
 */
public class RouteGroupActivity extends RoutesActivity {
  private Route[] group;
  protected Object[] initialRoutes() {
    if ( group == null ) {
      IntentHelper helper = new IntentHelper(this);
      List<Route> routes = helper.getRoutes();
      if ( routes == null ) {
        routes = Collections.emptyList();
      }
      group = routes.toArray(new Route[0]);
    }
    return group;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    initialRoutes();
    if ( group.length > 0 ) {
      setTitle(group[0].getLabel());
    }
  }
  
 }
/*-------------------------------------------------------------------------
 * Copyright (c) 2012, Alex Athanasopoulos.  All Rights Reserved.
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

import java.util.List;

import org.melato.bus.android.app.UpdateActivity;
import org.melato.bus.android.db.SqlRouteStorage;
import org.melato.bus.model.Route;

import android.app.Activity;
import android.os.Bundle;

public class MainActivity extends Activity {
  protected BusActivities activities;
  
  private boolean checkUpdates() {
    if ( ! UpdateActivity.checkUpdates(this) ) {
      return false;
    }
    if ( ! SqlRouteStorage.databaseFile(this).exists()) {
      return false;
    }
    return true;
  }
  
  /** Called when the activity is first created. */  
  @Override
  public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      if ( ! checkUpdates() ) {
        finish();
        return;
      }
      activities = new BusActivities(this);
      List<Route> recent = activities.getRecentRoutes();
      if ( recent.size() > 0 ) {
        RoutesActivity.showRecent(this);
      } else {
        RoutesActivity.showAll(this);
      }
      finish();
  }


}

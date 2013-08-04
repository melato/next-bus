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

import org.melato.bus.android.R;
import org.melato.bus.android.app.HelpActivity;
import org.melato.bus.model.RStop;
import org.melato.bus.model.Route;
import org.melato.bus.model.Stop;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

/**
 * Displays the schedule for a route
 * 
 * @author Alex Athanasopoulos
 * 
 */
public class StopsActivity extends ListActivity {
  private BusActivities activities;
  private StopsContext stops;

  public StopsActivity() {
  }

  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    activities = new BusActivities(this);
    stops = new StopsContext(this);
    Route route = activities.getRoute();
    setTitle(route.getFullTitle());
    stops.setRoute(route);
    IntentHelper helper = new IntentHelper(this);
    RStop rstop = helper.getRStop();
    stops.setStop(rstop);
  }
  
  @Override
  protected void onDestroy() {
    stops.close();
    super.onDestroy();
  }

  private void showStop(int index) {
    Stop p = stops.getStops()[index];
    RStop rstop = new RStop(activities.getRouteId(), p);
    Intent intent = new Intent(this, StopActivity.class);
    IntentHelper helper = new IntentHelper(intent);
    helper.putRStop(rstop);
    startActivity(intent);    
  }
 
  
  @Override
  protected void onListItemClick(ListView l, View v, int position, long id) {
    super.onListItemClick(l, v, position, id);
    showStop( position );
    
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.stops_menu, menu);
    HelpActivity.addItem(menu, this, Help.STOPS);
    return true;
  }
  
  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    return activities.onOptionsItemSelected(item);
  }  
}
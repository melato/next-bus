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
import org.melato.bus.client.NearbyStop;
import org.melato.gps.Point2D;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

/** Displays lines near a location. */
public class NearbyActivity extends ListActivity {
  private BusActivities activities;
  private NearbyContext nearby;

  public NearbyActivity() {
  }

/** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      activities = new BusActivities(this);
      nearby = new NearbyContext(this);
  }
  
  @Override
  protected void onDestroy() {
    nearby.close();
    super.onDestroy();
  }

  @Override
  protected void onListItemClick(ListView l, View v, int position, long id) {
    super.onListItemClick(l, v, position, id);
    NearbyStop p = nearby.getStop(position);
    RouteStop stop = new RouteStop(p.getRoute().getRouteId(), p.getRStop().getStop().getSymbol(), -1);
    activities.showRoute(stop);
 }

  @Override
  public boolean onCreateOptionsMenu(Menu menu)
  {
     MenuInflater inflater = getMenuInflater();
     inflater.inflate(R.menu.nearby_routes_menu, menu);
     HelpActivity.addItem(menu, this, R.string.help_nearby);
    return true;
  }
  
  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    return activities.onOptionsItemSelected(item);
  }
  
  public static void start(Context context, Point2D center) {
    Intent intent = new Intent(context, NearbyActivity.class);
    IntentHelper.putLocation(intent, center);
    context.startActivity(intent);    
  }
 
}
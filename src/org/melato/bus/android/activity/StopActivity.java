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

import org.melato.android.ui.PropertiesDisplay;
import org.melato.android.util.Invokable;
import org.melato.bus.android.Info;
import org.melato.bus.android.R;
import org.melato.bus.android.app.HelpActivity;
import org.melato.bus.model.RStop;
import org.melato.bus.model.Route;
import org.melato.bus.model.RouteId;
import org.melato.bus.model.Stop;
import org.melato.bus.plan.Sequence;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

/**
 * Displays information about one stop.
 * @author Alex Athanasopoulos
 *
 */
public class StopActivity extends ListActivity implements OnItemClickListener
 {
  private StopContext stop;
  private PropertiesDisplay properties;
  private BusActivities activities;
  private RStop rstop;
  
  public StopActivity() {
  }

  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    stop = new StopContext(this);
    properties = stop.getProperties();
    activities = new BusActivities(this);
    IntentHelper intentHelper = new IntentHelper(this);
    rstop = intentHelper.getRStop();
    if ( rstop == null || rstop.getStop() == null) {
      return;
    }
    Route route = activities.getRoute();
    stop.setRoute(route);
    Stop[] waypoints = Info.routeManager(this).getStops(rstop.getRouteId());
    
    int index = rstop.getStopIndex();
    if ( index < 0 ) {
      index = findWaypointIndex(waypoints, rstop.getStop());
    }
    stop.setMarkerIndex(index);
    stop.addProperties();
    setTitle(route.getLabel() + " " + stop.getMarker().getName());
   
    setListAdapter(stop.createAdapter(R.layout.stop_item));
    getListView().setOnItemClickListener(this);
  }
  
  @Override
  protected void onDestroy() {
    stop.close();
    super.onDestroy();
  }  

  static int findWaypointIndex(Stop[] waypoints, Stop p) {
    int size = waypoints.length;
    for( int i = 0; i < size; i++ ) {
      if ( p.equals(waypoints[i])) {
        return i;
      }
    }
    return -1;
  }
  
  @Override
  protected void onListItemClick(ListView l, View v, int position, long id) {
    super.onListItemClick(l, v, position, id);
    Object obj = properties.getItem(position);
    if ( obj instanceof Route ) {
      activities.showRoute((Route) obj);
    }
  }
 
  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.stop_menu, menu);
    HelpActivity.addItem(menu,this, R.string.help_stop);
    return true;
  }
 
  private void showNearby() {
    Stop point = stop.getMarker();
    NearbyActivity.start(this, point);
  }
  /**
   * Start the Schedule activity for the given stop.
   */
  private void showStopSchedule() {
    activities.showRoute(rstop, ScheduleActivity.class);
  }
  private void addToSequence(boolean after) {
    Sequence sequence = Info.getSequence(this);
    RouteId routeId = rstop.getRouteId();
    Stop stop = this.stop.getMarker();
    if ( after ) {
      sequence.addStopAfter(Info.routeManager(this), new RStop(routeId, stop));
    } else {
      sequence.addStopBefore(Info.routeManager(this), new RStop(routeId, stop));
    }
    startActivity(new Intent(this, SequenceActivity.class));    
  }
  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    boolean handled = false;
    switch (item.getItemId()) {
      case R.id.nearby:
        showNearby();
        handled = true;
        break;
      case R.id.schedule:
        showStopSchedule();
        handled = true;
        break;
      case R.id.add_stop_after:
        addToSequence(true);
        handled = true;
        break;
      /**
      case R.id.add_stop_before:
        addToSequence(false);
        handled = true;
        break;
      */
      default:
        break;
    }
    if ( handled )
      return true;
    return activities.onOptionsItemSelected(item);
  }
  
  @Override
  public void onItemClick(AdapterView<?> parent, View view, int position,
      long id) {
    Object value = properties.getItem(position);
    if ( value instanceof Invokable) {
      ((Invokable)value).invoke(this);
    }
  }
}
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
import org.melato.bus.model.DaySchedule;
import org.melato.bus.model.RStop;
import org.melato.bus.model.Route;
import org.melato.bus.model.Schedule;
import org.melato.bus.model.ScheduleId;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Displays a list of all schedules for a route
 * @author Alex Athanasopoulos
 */
public class SchedulesActivity extends ListActivity {
  protected BusActivities activities;
  private Schedule schedule;
  private DaySchedule[] schedules;
  private RStop rstop;
  private Route route;

  public SchedulesActivity() {
  }
    
  class SchedulesAdapter extends ArrayAdapter<DaySchedule> {
    public SchedulesAdapter() {
      super(SchedulesActivity.this, R.layout.list_item, schedules);
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
      TextView view = (TextView) super.getView(position, convertView, parent);
      String name = ScheduleUtilities.getScheduleName(getContext(), this.getItem(position).getScheduleId());
      view.setText( name );
      return view;
    }
  }
  
  
/** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    IntentHelper helper = new IntentHelper(this);
    rstop = helper.getRStop();
    activities = new BusActivities(this);
    route = activities.getRoute();
    setTitle( route.getFullTitle() );
    schedule = activities.getRouteManager().getSchedule(route);
    schedules = schedule.getSchedules();
    setListAdapter(new SchedulesAdapter());
  }

  @Override
  protected void onListItemClick(ListView l, View v, int position, long id) {
    super.onListItemClick(l, v, position, id);
    ScheduleId scheduleId = schedules[position].getScheduleId();
    Intent intent = new Intent(this, ScheduleActivity.class);
    IntentHelper helper = new IntentHelper(intent);
    if ( rstop != null) {
      helper.putRStop(rstop);
    } else {
      helper.putRoute(route);
    }
    intent.putExtra(ScheduleActivity.KEY_SCHEDULE_ID, scheduleId);
    startActivity(intent);        
  }
  
  @Override
  public boolean onCreateOptionsMenu(Menu menu)
  {
     HelpActivity.addItem(menu, this, Help.SCHEDULES);
     return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    return activities.onOptionsItemSelected(item);
  }    
}
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

import org.melato.bus.android.Info;
import org.melato.bus.android.R;
import org.melato.bus.android.app.HelpActivity;
import org.melato.bus.model.Route;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Displays the list of recent routes
 * @author Alex Athanasopoulos
 *
 */
public class RecentRoutesActivity extends ListActivity {  
  protected BusActivities activities;
  private RecentRoute[] items = new RecentRoute[0];
  private ColorScheme colors;

  @Override
  public boolean onCreateOptionsMenu(Menu menu)
  {
     MenuInflater inflater = getMenuInflater();
     inflater.inflate(R.menu.recent_routes_menu, menu);
     HelpActivity.addItem(menu, this, R.string.help_recent);
     return true;
  }

  
  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedInstanceState) {      
      super.onCreate(savedInstanceState);
      activities = new BusActivities(this);
      items = RecentRoute.read(activities.recentRoutesFile());
      items = RecentRoute.filter(Info.routeManager(this), items);
      setListAdapter(new RecentRoutesAdapter());
      //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
      colors = UI.getColorScheme(this);
      //prefs.registerOnSharedPreferenceChangeListener(this);
  }

  @Override
  protected void onListItemClick(ListView l, View v, int position, long id) {
    super.onListItemClick(l, v, position, id);
    RecentRoute item = items[position];
    activities.showRoute(item.getRStop(Info.routeManager(this)));
  }

  class RecentRoutesAdapter extends ArrayAdapter<Object> {
    public RecentRoutesAdapter() {
      super(RecentRoutesActivity.this, R.layout.list_item, items);
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
      TextView view = (TextView) super.getView(position, convertView, parent);
      RecentRoute recentRoute = items[position];
      Route route = recentRoute.getRoute();
      view.setTextColor(colors.getColor(route));
      view.setBackgroundColor(colors.getBackground(route));
      return view;
    }
}
  
  public static void showRecent(Context context) {
    Intent intent = new Intent(context, RecentRoutesActivity.class);
    context.startActivity(intent);        
  }
  
  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    return activities.onOptionsItemSelected(item);
  } 
   
  
 }
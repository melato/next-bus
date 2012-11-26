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

import org.melato.bus.android.R;
import org.melato.bus.model.Route;
import org.melato.bus.model.RouteGroup;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Displays a list of routes
 * @author Alex Athanasopoulos
 *
 */
public abstract class RoutesActivity extends ListActivity {
  protected BusActivities activities;
  private Object[] items = new Object[0];
  private ColorScheme colors;

  protected abstract Object[] initialRoutes();
  
  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedInstanceState) {      
      super.onCreate(savedInstanceState);
      activities = new BusActivities(this);
      items = initialRoutes();
      setListAdapter(new RoutesAdapter());
      //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
      colors = UI.getColorScheme(this);
      //prefs.registerOnSharedPreferenceChangeListener(this);
  }

  void showGroup(RouteGroup group) {
    if ( group.getRoutes().length == 1 ) {
      activities.showRoute(group.getRoutes()[0]);
    } else {
      Intent intent = new Intent(this, RouteGroupActivity.class);
      IntentHelper helper = new IntentHelper(intent);
      helper.putRoutes(group);
      startActivity(intent);
    }
  }
  @Override
  protected void onListItemClick(ListView l, View v, int position, long id) {
    super.onListItemClick(l, v, position, id);
    Object item = items[position];
    if ( item instanceof Route ) {
      activities.showRoute((Route)item);
    } else if ( item instanceof RouteGroup ) {
      showGroup((RouteGroup)item);
    }
  }

  class RoutesAdapter extends ArrayAdapter<Object> {
    public RoutesAdapter() {
      super(RoutesActivity.this, R.layout.list_item, items);
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
      TextView view = (TextView) super.getView(position, convertView, parent);
      Route route = null;
      if ( items[position] instanceof Route ) {
        route = (Route) items[position];
      } else if ( items[position] instanceof RouteGroup ) {
        RouteGroup group = (RouteGroup) items[position];
        Route[] routes = group.getRoutes();
        if ( routes.length > 0 ) {
          route = routes[0];
          for( int i = 0; i < routes.length; i++ ) {
            if ( ! route.isSameColor(routes[i])) {
              route = null;
              break;
            }
          }
        }
      }
      view.setTextColor(colors.getColor(route));
      view.setBackgroundColor(colors.getBackground(route));
      return view;
    }
}
  
  public static void showRecent(Context context) {
    Intent intent = new Intent(context, RecentRoutesActivity.class);
    context.startActivity(intent);        
  }
  
  public static void showAll(Context context) {
    Intent intent = new Intent(context, AllRoutesActivity.class);
    context.startActivity(intent);        
  }
  
  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    boolean handled = false;

    switch (item.getItemId()) {
      case R.id.nearby:
        startActivity(new Intent(this, NearbyActivity.class));
        handled = true;
        break;
      case R.id.all_routes:
        showAll(this);
        handled = true;
        break;
      case R.id.recent_routes:
        showRecent(this);
        handled = true;
        break;
      default:
        break;
    }
    if ( ! handled ) {
      handled = activities.onOptionsItemSelected(item);
    }
    return handled;
  } 
 }
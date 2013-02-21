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
import org.melato.bus.client.Formatting;
import org.melato.bus.client.TrackContext;
import org.melato.bus.model.Route;
import org.melato.bus.model.Stop;
import org.melato.gps.Earth;
import org.melato.gps.PointTime;

import android.app.ListActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class StopsContext extends LocationContext {
  private TrackContext track;
  private int closestStop = -1;
  private boolean isSelected;
  private StopsAdapter adapter;
  private RouteStop markedStop;
  private int markedIndex = -1;

  private ListActivity list;

  public void setRoute(Route route) {
    history.setRoute(route.getRouteId());
    track = history.getTrackContext();
    list.setListAdapter(adapter = new StopsAdapter());
    start();
  }
  
  public void setStop(RouteStop stop) {
    markedStop = stop;
    if ( stop != null) {
      markedIndex = stop.getStopIndex(track.getStops());
    }
  }
  
  public StopsContext(ListActivity activity) {
    super(activity);
    this.list = activity;
  }

  @Override
  public void setLocation(PointTime point) {
    super.setLocation(point);
    if ( point != null) {
      track.setLocation(point);
      closestStop = track.getPathTracker().getNearestIndex();
    }
    adapter.notifyDataSetChanged();
    // scroll to the nearest stop, if we haven't done it yet.
    if ( ! isSelected && closestStop >= 0 ) {
      isSelected = true;
      list.setSelection(closestStop);
    }
  }
  
  public Stop[] getStops() {
    return track.getStops();
  }

  class StopsAdapter extends ArrayAdapter<Stop> {
    TextView view;

    public StopsAdapter() {
      super(context, R.layout.list_item, track.getStops()); 
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
      TextView view = (TextView) super.getView(position, convertView, parent);
      Stop waypoint = track.getStops()[position];
      String text = waypoint.getName();
      PointTime here = getLocation();
      if ( here != null && closestStop == position ) {
        float straightDistance = Earth.distance(here, waypoint); 
        text += " " + Formatting.straightDistance(straightDistance);
      }
      if ( position == markedIndex ) {
        view.setBackgroundColor(context.getResources().getColor(R.color.stop_background));
        view.setTextColor(context.getResources().getColor(R.color.list_highlighted_text));
      } else {
        UI.highlight(view, position == closestStop );        
      }
      view.setText( text );
      return view;
    }
  }

}

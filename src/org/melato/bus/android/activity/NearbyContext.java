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

import java.util.Arrays;

import org.melato.bus.android.Info;
import org.melato.bus.android.R;
import org.melato.bus.client.NearbyStop;
import org.melato.bus.client.WaypointDistance;
import org.melato.gps.Point2D;
import org.melato.gps.PointTime;

import android.app.ListActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class NearbyContext extends LocationContext {
  private NearbyStop[] stops = new NearbyStop[0];
  private boolean haveLocation;
  private ListActivity activity;
  private NearbyAdapter adapter;

  class NearbyAdapter extends ArrayAdapter<NearbyStop> {
    public NearbyAdapter() {
      super(context, R.layout.list_item, stops);
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
      TextView view = (TextView) super.getView(position, convertView, parent);
      int group = stops[position].getGroup();
      if ( group == 1 ) {
        view.setBackgroundColor(context.getResources().getColor(R.color.group1_background));
        view.setTextColor(context.getResources().getColor(R.color.group1_text));
      } else {
        view.setBackgroundColor(context.getResources().getColor(R.color.group2_background));
        view.setTextColor(context.getResources().getColor(R.color.group2_text));
      }
      return view;
    }
    
  }
  
  void colorStops(NearbyStop[] stops) {
    int group = 0;
    String groupSymbol = null;
    for( int i = 0; i < stops.length; i++ ) {
      String symbol = stops[i].getWaypoint().getSym();
      if ( ! symbol.equals(groupSymbol)) {
        group++;
        groupSymbol = symbol;
      }
      stops[i].setGroup(group % 2);      
    }
  }
  void load(Point2D location) {
    stops = Info.nearbyManager(context).getNearby(location);
    colorStops(stops);
  }
    
  public NearbyStop getStop(int index) {
    return stops[index];
  }
  
  public NearbyContext(ListActivity activity) {
    super(activity);
    this.activity = activity;
    Point2D center = IntentHelper.getLocation(activity.getIntent());
    if ( center != null) {
      setCenter(center);
    } else {
      Point2D p = Info.nearbyManager(activity).getLastLocation();
      if ( p != null ) {
        setCenter(p);
      }
      haveLocation = false;
      setEnabledLocations(true);
    }
  }

  private void setCenter(Point2D point) {
    if ( point == null )
      return;
    if ( haveLocation ) {
      WaypointDistance.setDistance(stops, point);
      Arrays.sort(stops);
      adapter.notifyDataSetChanged();
   } else {
      haveLocation = true;
      load(point);
      activity.setListAdapter(adapter = new NearbyAdapter());    
    }
  }
  
  public void setLocation(PointTime point) {
    super.setLocation(point);
    setCenter(point);
  }
 

}

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

import java.util.Arrays;
import java.util.Date;

import org.melato.android.location.Locations;
import org.melato.android.ui.BackgroundAdapter;
import org.melato.android.ui.ListLoader;
import org.melato.bus.android.Info;
import org.melato.bus.android.R;
import org.melato.bus.client.NearbyStop;
import org.melato.bus.model.DaySchedule;
import org.melato.bus.model.RouteManager;
import org.melato.bus.model.Stop;
import org.melato.gps.Metric;
import org.melato.gps.Point2D;
import org.melato.gps.PointTime;

import android.app.ListActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class NearbyContext extends LocationContext {
  private NearbyStop[] stops = new NearbyStop[0];
  private boolean haveLocation;
  private ListActivity activity;
  private NearbyAdapter adapter;
  private Date stopDate = new Date();

  class NearbyTimeLoader implements ListLoader {
    private RouteManager routeManager = Info.routeManager(context);
    private boolean loadTimes;
    
    
    public NearbyTimeLoader() {
      SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
      loadTimes = prefs.getBoolean(Pref.NEARBY_TIMES, true);
      //loadTimes = Boolean.parseBoolean(prefs.getString(Pref.NEARBY_TIMES, "true"));
    }

    @Override
    public boolean isLoaded(int position) {
      if ( ! loadTimes )
        return true;
      return stops[position].getNearestTimes() != null;
    }

    /** Return the time difference from the route start to the given stop.
     * @param stop
     * @return The time difference in seconds, or -1 if unknown.
     */
    int getTimeOffset(NearbyStop stop) {
      Stop s = stop.getRStop().getStop();
      int timeOffset = (int) (s.getTime() / 1000);
      if ( timeOffset == 0 && s.getIndex() > 0 )
        return -1;
      return timeOffset;
    }
    
    @Override
    public void load(int position) {
      NearbyStop stop = stops[position];
      int timeOffset = getTimeOffset(stop);
      int[] times = null;
      if ( timeOffset >= 0 ) {        
        Date date = new Date(stopDate.getTime() - timeOffset * 1000L);
        DaySchedule daySchedule = routeManager.getDaySchedule(stop.getRoute(), date);
        if ( daySchedule != null ) {
          int[] dayTimes = daySchedule.getTimes();
          int index = daySchedule.getClosestIndex(date);
          if ( index < 0 ) {
            times = new int[0];
          } else if ( index == 0 ) {
            times = new int[] { dayTimes[0] }; 
          } else if ( index == dayTimes.length-1) {
            times = new int[] { dayTimes[dayTimes.length-1] };
          } else {  
            times = new int[] {dayTimes[index], dayTimes[index+1]}; 
          }
          for(int i = 0; i < times.length; i++ ) {
            times[i] += timeOffset / 60;
          }
        }
      }
      if ( times == null)
        times = new int[0];
      stop.setNearestTimes(times);
    }
    
  }
  class NearbyAdapter extends BackgroundAdapter<NearbyStop> {
    public NearbyAdapter() {
      super(activity, new NearbyTimeLoader(), R.layout.list_item, stops);
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
      String symbol = stops[i].getRStop().getStop().getSymbol();
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
    Intent intent = activity.getIntent();
    Point2D center = IntentHelper.getLocation(intent);
    if (center == null) {
      center = Locations.getGeoUriPoint(intent);
    }
    if ( center != null) {
      setCenter(center);
    } else {
      Point2D p = Info.nearbyManager(activity).getLastLocation();
      if ( p != null ) {
        setCenter(p);
      }
      haveLocation = false;
      start();
    }
  }

  private void setCenter(Point2D point) {
    if ( point == null )
      return;
    if ( haveLocation ) {
      RouteManager routeManager = Info.routeManager(context);
      Metric metric = routeManager.getMetric();
      for(NearbyStop p: stops) {
        p.getRStop().setDistance(metric.distance(p.getRStop().getStop(), point));
      }
      Arrays.sort(stops, new NearbyStop.Comparer());
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

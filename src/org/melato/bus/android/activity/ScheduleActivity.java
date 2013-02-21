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

import java.util.Date;

import org.melato.bus.android.Info;
import org.melato.bus.android.R;
import org.melato.bus.android.app.HelpActivity;
import org.melato.bus.client.TimeOfDay;
import org.melato.bus.client.TimeOfDayList;
import org.melato.bus.model.DaySchedule;
import org.melato.bus.model.Schedule;
import org.melato.bus.model.ScheduleId;
import org.melato.bus.model.Stop;
import org.melato.util.DateId;

import android.app.Activity;
import android.content.Context;
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
 * Displays the schedule for a route
 * @author Alex Athanasopoulos
 *
 */
public class ScheduleActivity extends Activity {
  public static final String KEY_SCHEDULE_ID = "scheduleId";
  protected BusActivities activities;
  private Schedule schedule;
  private Date  currentTime = new Date();
  private DaySchedule daySchedule;
  private String  stopName;
  private int     timeOffset;
  
  
  
  private static int getFirstBit(int bitmap ) {
    if ( bitmap == 0 )
      return -1;
    for( int i = 0; i < 32; i++ ) {
      int bit = 1 << i;
      if ( (bitmap & bit) != 0 ) {
        return i;
      }
    }
    return -1;
  }
  private static int getLastBit(int bitmap ) {
    if ( bitmap == 0 )
      return -1;
    for( int i = 31; i >= 0; i-- ) {
      int bit = 1 << i;
      if ( (bitmap & bit) != 0 ) {
        return i;
      }
    }
    return -1;
  }
  private static boolean isContiguous( int bitmap, int first, int last ) {
    for( int i = first; i <= last; i++ ) {
      int bit = 1 << i;
      if ( (bitmap & bit) == 0 ) {
        return false;
      }
    }
    return true;    
  }

  private static final int[] DAY_RESOURCES = {
    R.string.days_Su,
    R.string.days_Mo,
    R.string.days_Tu,
    R.string.days_We,
    R.string.days_Th,
    R.string.days_Fr,
    R.string.days_Sa,
  };
  public static String getDayName(Context context, int bit) {
    if ( bit < 7 ) {
      return context.getResources().getString(DAY_RESOURCES[bit]);      
    }
    return "";
  }
  public static String getScheduleName(Context context, ScheduleId scheduleId) {
    int days = scheduleId.getDays();
    if ( days == 0 ) {
      return DateId.toString(scheduleId.getDateId());
    }
    int first = getFirstBit(days);
    int last = getLastBit(days);
    if ( first == last ) {
      return getDayName(context, first);      
    }
    if ( days == 127 ) {
      return context.getResources().getString(R.string.days_all);
    }
    if ( isContiguous(days, first, last)) {
      return getDayName(context, first) + "-" + getDayName(context,last);
    }
    StringBuilder buf = new StringBuilder();
    for( int i = first; i <= last; i++ ) {
      int bit = 1 << i;
      if ( (days & bit) != 0 ) { 
        if ( i > first ) {
          buf.append(",");
        }
        buf.append(getDayName(context,i));
      }
    }
    return buf.toString();
  }

  protected String getScheduleName() {
    if ( daySchedule == null )
      return "";
    return getScheduleName(this, daySchedule.getScheduleId());
  }
  
  private void setStopInfo(RouteStop stop) {
    Stop[] stops = Info.routeManager(this).getStops(stop.getRouteId());
    stopName = stop.getStopName(stops);
    timeOffset = stop.getTimeFromStart(stops);
    if ( timeOffset == 0 && stops.length > 0 ) {
      stopName = stops[0].getName();
    }
  }
  
/** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      activities = new BusActivities(this);
      IntentHelper helper = new IntentHelper(this);
      RouteStop routeStop = helper.getRouteStop();
      if ( routeStop == null )
        return;
      setStopInfo(routeStop);
      schedule = activities.getRouteManager().getSchedule(routeStop.getRouteId());
      ScheduleId scheduleId = (ScheduleId) getIntent().getSerializableExtra(KEY_SCHEDULE_ID);
      this.daySchedule = schedule.getSchedule(scheduleId);
      if ( daySchedule == null ) {
        daySchedule = schedule.getSchedule(currentTime); 
      }
      setContentView(R.layout.schedule);
      ListView listView = (ListView) findViewById(R.id.listView);
      TextView textView = (TextView) findViewById(R.id.textView);
      String scheduleText = getScheduleName();
      String title = helper.getRoute().getFullTitle();
      if ( stopName != null ) {
        scheduleText += " - " + stopName;
        if ( timeOffset > 0 ) {
          scheduleText += " (+" + Schedule.formatDuration(timeOffset) + ")";
        }
      }
      String comment = schedule.getComment();
      if ( comment != null ) {
        scheduleText += "\n" + comment; 
      }
      textView.setText(scheduleText);
      setTitle(title);
      if ( daySchedule != null ) {
        TimeOfDayList times = new TimeOfDayList(daySchedule,currentTime);
        times.setTimeOffset(timeOffset);
        ScheduleAdapter scheduleAdapter = new ScheduleAdapter(times);
        listView.setAdapter(scheduleAdapter);
        int pos = times.getDefaultPosition();
        if ( pos >= 0 ) {
          if ( pos > 0 )
            pos--;
          listView.setSelection(pos);
        }
      }
  }

  static class TextColor {
    int text;
    int background;
    TextColor( Context context, int textId, int backgroundId ) {
      text = context.getResources().getColor(textId);
      background = context.getResources().getColor(backgroundId);
    }
    public void apply(TextView view) {
      view.setBackgroundColor(background);
      view.setTextColor(text);
    }
  }
  class ScheduleAdapter extends ArrayAdapter<TimeOfDay> {
    TimeOfDayList times;
    int currentPosition;
    TextColor normalColor;
    TextColor selectedColor;
    public ScheduleAdapter(TimeOfDayList times) {
      super(ScheduleActivity.this, R.layout.list_item, times);
      this.times = times;
      currentPosition = times.getDefaultPosition();
      selectedColor = new TextColor(ScheduleActivity.this, R.color.list_highlighted_text, R.color.list_highlighted_background);
      if ( times.hasOffset() ) {
        normalColor = new TextColor(ScheduleActivity.this, R.color.stop_text, R.color.stop_background);
      } else {
        normalColor = new TextColor(ScheduleActivity.this, R.color.list_text, R.color.list_background);
        
      }
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
      TextView view = (TextView) super.getView(position, convertView, parent);
      if ( position == currentPosition ) {
        selectedColor.apply(view);
      } else {
        normalColor.apply(view);
      }
      return view;
    }
  }
  
  @Override
  public boolean onCreateOptionsMenu(Menu menu)
  {
     MenuInflater inflater = getMenuInflater();
     inflater.inflate(R.menu.schedule_menu, menu);
     HelpActivity.addItem(menu, this, R.string.help_schedule);
     return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    return activities.onOptionsItemSelected(item);
  }
  
 }
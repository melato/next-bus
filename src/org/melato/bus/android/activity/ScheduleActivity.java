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
import java.util.List;

import org.melato.bus.android.Info;
import org.melato.bus.android.R;
import org.melato.bus.android.activity.ExceptionActivity.ExceptionSpecifier;
import org.melato.bus.android.app.HelpActivity;
import org.melato.bus.client.TimeOfDay;
import org.melato.bus.client.TimeOfDayList;
import org.melato.bus.model.Agency;
import org.melato.bus.model.DaySchedule;
import org.melato.bus.model.RStop;
import org.melato.bus.model.RouteException;
import org.melato.bus.model.RouteId;
import org.melato.bus.model.Schedule;
import org.melato.bus.model.ScheduleId;
import org.melato.bus.model.Stop;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Displays the schedule for a route
 * @author Alex Athanasopoulos
 *
 */
public class ScheduleActivity extends Activity implements OnItemClickListener {
  public static final String KEY_SCHEDULE_ID = "scheduleId";
  protected BusActivities activities;
  private Schedule schedule;
  private Date  currentTime = new Date();
  private DaySchedule daySchedule;
  private String  stopName;
  private int     timeOffset;
  private ScheduleAdapter scheduleAdapter;
  
  protected String getScheduleName() {
    if ( daySchedule == null )
      return "";
    return ScheduleUtilities.getScheduleName(this, daySchedule.getScheduleId());
  }
  
  private void setStopInfo(RStop rstop) {
    Stop[] stops = Info.routeManager(this).getStops(rstop.getRouteId());
    Stop stop = rstop.getStop();
    if ( stop == null || stop.getIndex() > 0 && stop.getSecondsFromStart() == 0) {
      if ( stops.length > 0 ) {
        stop = stops[0];
      }
    }
    if ( stop != null) {
      stopName = stop.getName();
      timeOffset = stop.getSecondsFromStart();
    }
  }
  
/** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      activities = new BusActivities(this);
      IntentHelper helper = new IntentHelper(this);
      RStop rstop = helper.getRStop();
      if ( rstop == null )
        return;
      setStopInfo(rstop);
      schedule = activities.getRouteManager().getSchedule(rstop.getRouteId());
      ScheduleId scheduleId = (ScheduleId) getIntent().getSerializableExtra(KEY_SCHEDULE_ID);
      if ( scheduleId == null) {
        scheduleId = Info.getStickyScheduleId();
      } else {
        Info.setStickyScheduleId(scheduleId);
      }
      if ( scheduleId != null) {
        daySchedule = schedule.getSchedule(scheduleId);
      } else {
        daySchedule = schedule.getSchedule(currentTime);   
        if ( daySchedule != null) {
          scheduleId = daySchedule.getScheduleId();
        }
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
        List<RouteException> exceptions = schedule.getExceptions(daySchedule.getScheduleId());
        times.setExceptions(exceptions);
        scheduleAdapter = new ScheduleAdapter(times);
        listView.setAdapter(scheduleAdapter);
        listView.setOnItemClickListener(this);
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
    boolean hasException(int position) {
      return times.hasException(position);
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
     Agency agency = Info.routeManager(this).getAgency(activities.getRouteId());     
     Drawable drawable = Info.getAgencyIcon(this, agency);
     MenuItem browse = menu.findItem(R.id.browse);
     if (drawable != null) {
       browse.setIcon(drawable);
     }
     if ( agency.getLabel() != null) {
       browse.setTitle(agency.getLabel());
     }
     HelpActivity.addItem(menu, this, Help.SCHEDULE);
     return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    return activities.onOptionsItemSelected(item);
  }
  @Override
  public void onItemClick(AdapterView<?> parent, View view, int position,
      long id) {
    if ( scheduleAdapter.hasException(position)) {
      RouteId routeId = activities.getRouteId();
      ScheduleId scheduleId = daySchedule.getScheduleId();
      int time = daySchedule.getTimes()[position];
      ExceptionSpecifier exc = new ExceptionSpecifier(routeId, scheduleId, time);
      ExceptionActivity.showExceptions(this, exc);
    }
  }
}
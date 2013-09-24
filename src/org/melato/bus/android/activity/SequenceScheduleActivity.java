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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.melato.android.progress.ActivityProgressHandler;
import org.melato.android.progress.ProgressTitleHandler;
import org.melato.bus.android.Info;
import org.melato.bus.android.R;
import org.melato.bus.model.Schedule.DateScheduleFactory;
import org.melato.bus.model.Schedule.ScheduleFactory;
import org.melato.bus.model.Schedule.ScheduleIdScheduleFactory;
import org.melato.bus.model.ScheduleId;
import org.melato.bus.plan.Sequence;
import org.melato.bus.plan.SequenceInstance;
import org.melato.bus.plan.SequenceItinerary;
import org.melato.bus.plan.SequenceSchedule;
import org.melato.progress.ProgressGenerator;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Displays the schedule of a sequence for a whole day.
 * The schedule displays all starting times, end times and duration.
 * @author Alex Athanasopoulos
 */
public class SequenceScheduleActivity extends Activity implements OnItemClickListener {
  private Sequence sequence;
  private List<SequenceInstance> instances = new ArrayList<SequenceInstance>();
  private ActivityProgressHandler progressHandler;

  public SequenceScheduleActivity() {
  }
  
  class ScheduleTask extends AsyncTask<Void,Integer,SequenceSchedule> {
    ScheduleFactory scheduleFactory;

    @Override
    protected SequenceSchedule doInBackground(Void... params) {
      ProgressGenerator.setHandler(progressHandler);
      ProgressGenerator progress = ProgressGenerator.get();
      progress.setText(getString(R.string.computing));
      scheduleFactory = scheduleFactory();
      SequenceSchedule schedule = new SequenceSchedule(sequence, scheduleFactory,
            Info.routeManager(SequenceScheduleActivity.this),
            Info.walkModel(SequenceScheduleActivity.this)
            );
      instances = schedule.getInstances();
      return schedule;
    }

    @Override
    protected void onPostExecute(SequenceSchedule schedule) {
      ListView listView = (ListView) findViewById(R.id.listView);
      HighlightAdapter<SequenceInstance> adapter = new HighlightAdapter<SequenceInstance>(SequenceScheduleActivity.this, instances);
      listView.setAdapter(adapter);
      listView.setOnItemClickListener(SequenceScheduleActivity.this);
      int pos = schedule.getTimePosition(new Date());
      if ( pos >= 0 ) {
        adapter.setSelection(pos);
        listView.setSelection(pos);
      }
      progressHandler.end();
      setTitle( sequence.getLabel(Info.routeManager(SequenceScheduleActivity.this)));
      TextView textView = (TextView) findViewById(R.id.textView);
      textView.setText(ScheduleUtilities.getScheduleName(SequenceScheduleActivity.this, scheduleFactory));
      }    
  }
  
  public static ScheduleFactory scheduleFactory() {
    ScheduleId scheduleId = Info.getStickyScheduleId();
    if ( scheduleId != null) {
      return new ScheduleIdScheduleFactory(scheduleId);
    } else {
      return new DateScheduleFactory();
    }
  }
  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    progressHandler = new ProgressTitleHandler(this);
    sequence = Info.getSequence(this);
    setContentView(R.layout.schedule);
    new ScheduleTask().execute();
  }

  @Override
  public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
    SequenceInstance instance = instances.get(position);
    SequenceItinerary itinerary = instance.getItinerary();
    Intent intent = new Intent(this, SequenceInstanceActivity.class);
    intent.putExtra(SequenceInstanceActivity.KEY_ITINERARY, itinerary);
    startActivity(intent);
  }
  
  @Override
  protected void onDestroy() {
    super.onDestroy();
    Info.saveSequence(this);
  }

}
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
import java.util.List;

import org.melato.bus.android.Info;
import org.melato.bus.android.R;
import org.melato.bus.android.app.HelpActivity;
import org.melato.bus.client.Formatting;
import org.melato.bus.model.Route;
import org.melato.bus.model.RouteManager;
import org.melato.bus.model.Stop;
import org.melato.bus.plan.Leg;
import org.melato.bus.plan.LegGroup;
import org.melato.bus.plan.Sequence;
import org.melato.bus.plan.SequenceInstance.WalkInstance;
import org.melato.bus.plan.Walk;
import org.melato.gps.Point2D;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/**
 * Displays a sequence
 * @author Alex Athanasopoulos
 */
public class SequenceActivity extends ListActivity {
  private Sequence sequence;
  private List<SequenceItem> items;
  private ArrayAdapter<SequenceItem> adapter;

  public static interface SequenceItem {    
  }
  class LegItem implements SequenceItem {
    private LegGroup leg;
    private Route route;

    public LegItem(LegGroup leg, RouteManager routeManager) {
      super();
      this.leg = leg;
      route = routeManager.getRoute(leg.getLeg().getRouteId());
    }

    @Override
    public String toString() {
      Leg leg = this.leg.getLeg();
      StringBuilder buf = new StringBuilder();
      buf.append(route.getLabel());
      buf.append( " " );
      buf.append(leg.getStop1().getName());
      Stop stop2 = leg.getStop2();
      if ( stop2 != null) {
        buf.append( " -> " );
        buf.append(stop2.getName());
      }
      return buf.toString();
    }
    
    
  }
  
  public static class WalkItem implements SequenceItem {
    private float distance;
    private String label;
    public WalkItem(Point2D point1, Point2D point2, RouteManager routeManager, Context context) {
      super();
      distance = routeManager.getMetric().distance(point1, point2);
      System.out.println( "WalkItem: from=" + point1 + " to=" + point2 + " distance=" + distance);
      label = context.getString(R.string.walk_leg, Formatting.straightDistance(distance), Walk.distanceDuration(distance));
    }
    public WalkItem(WalkInstance walk, Context context) {
      this.distance = walk.getDistance();
      label = context.getString(R.string.walk_leg, Formatting.straightDistance(distance), Walk.distanceDuration(distance));
    }
    @Override
    public String toString() {
      return label;
    }
  }
  
  public List<SequenceItem> getSequenceItems(Sequence sequence, RouteManager routeManager) {
    List<SequenceItem> items = new ArrayList<SequenceItem>();
    Leg previous = null;
    for(LegGroup leg: sequence.getLegs() ) {
      if ( previous != null) {
        items.add(new WalkItem(previous.getStop2(), leg.getLeg().getStop1(), routeManager, this));
      }
      items.add(new LegItem(leg, routeManager));
      previous = leg.getLeg();
    }
    return items;
  }
  
  
  public SequenceActivity() {
  }
  
  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    sequence = Info.getSequence(this);
    resetList();
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    Info.saveSequence(this);
  }

  @Override
  protected void onListItemClick(ListView l, View v, int position, long id) {
    SequenceItem item = items.get(position);
    if ( item instanceof LegItem ) {
      Leg leg = ((LegItem) item).leg.getLeg();
      BusActivities activities = new BusActivities(this);
      activities.showRoute(leg.getRStop1());
    }
  }
  
  @Override
  public boolean onCreateOptionsMenu(Menu menu)
  {
     MenuInflater inflater = getMenuInflater();
     inflater.inflate(R.menu.sequence_menu, menu);
     HelpActivity.addItem(menu, this, R.string.help_sequence);
     return true;
  }

  private void resetList() {
    items = getSequenceItems(sequence, Info.routeManager(this));
    adapter = new ArrayAdapter<SequenceItem>(this, R.layout.list_item, items);
    setListAdapter(adapter);
    if ( sequence.getLegs().isEmpty()) {
      setTitle(R.string.empty_sequence);
    } else {
      setTitle( sequence.getLabel(Info.routeManager(this)));
    }
  }
  
  private void removeLast() {
    List<LegGroup> legs = sequence.getLegs();
    if ( ! legs.isEmpty()) {
      Leg last = legs.get(legs.size()-1).getLeg();
      if ( last.getStop2() != null) {
        last.setStop2(null);
      } else {
        legs.remove(legs.size()-1);
      }
      resetList();
    }
  }
  private void removeFirst() {
    List<LegGroup> legs = sequence.getLegs();
    if ( ! legs.isEmpty()) {
      legs.remove(0);
      resetList();
    }
  }
  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    boolean handled = false;
    switch(item.getItemId()) {
      case R.id.clear:
        sequence.getLegs().clear();
        resetList();
        handled = true;
        break;
      case R.id.remove_last:
        removeLast();
        handled = true;
        break;
      case R.id.remove_first:
        removeFirst();
        handled = true;
        break;
      case R.id.schedule:
        startActivity(new Intent(this, SequenceScheduleActivity.class));
        handled = true;
        break;
    }
    return handled ? true : false;
  }    
}
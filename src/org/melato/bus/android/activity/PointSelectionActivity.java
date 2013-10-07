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

import org.melato.android.location.Locations;
import org.melato.bus.android.Info;
import org.melato.bus.android.R;
import org.melato.bus.model.RStop;
import org.melato.bus.model.Route;
import org.melato.bus.model.Stop;
import org.melato.bus.plan.NamedPoint;
import org.melato.bus.plan.Sequence;
import org.melato.gps.Point2D;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

/**
 * This is an activity dialog that asks the user what to do with an incoming selected point.
 * It may do one of:
 *  - show nearby routes/stops
 *  - use as plan origin
 *  - use as plan destination
 *  - add to Sequence (requires RStop)
 **/
public class PointSelectionActivity extends Activity implements OnClickListener {
  public static final String POINT = "POINT";
  RStop rstop;
  NamedPoint point;
  /** Called when the activity is first created. */
  
  
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.point_selection);
    Intent intent = getIntent();
    IntentHelper intentHelper = new IntentHelper(intent);
    setTitle(R.string.point_selection);
    rstop = intentHelper.getRStop();
    if ( rstop == null) {
      point = (NamedPoint) intent.getSerializableExtra(POINT);
    }
    
    if ( rstop == null && point == null) {
      Point2D p = IntentHelper.getLocation(intent);
      if (p == null) {
        p = Locations.getGeoUriPoint(intent);
      }
      if ( p != null) {
        point = new NamedPoint(p);
      }
    }
    initButton(R.id.nearby);
    //initButton(R.id.add_stop_after);
    initButton(R.id.origin);
    initButton(R.id.destination);
  }
  
  private void initButton(int id) {
    Button button = (Button) findViewById(id);
    button.setOnClickListener(this);       
  }
  private void showNearby() {    
    finish();
    NearbyActivity.start(this, getPoint());
  }  
  private void showPlan() {
    finish();
    Intent intent = new Intent(this, PlanTabsActivity.class);
    intent.putExtra(PlanTabsActivity.KEY_TAB, PlanTabsActivity.TAB_SEARCH);
    startActivity(intent);    
  }
 
  private void addToSequence(boolean after) {
    Sequence sequence = Info.getSequence(this);
    if ( after ) {
      sequence.addStopAfter(Info.routeManager(this), rstop);
    } else {
      sequence.addStopBefore(Info.routeManager(this), rstop);
    }
    finish();
    startActivity(new Intent(this, SequenceActivity.class));    
  }
  
  public static void selectPoint(Context context, RStop rstop) {
    Intent intent = new Intent(context, PointSelectionActivity.class);
    new IntentHelper(intent).putRStop(rstop);
    context.startActivity(intent);    
  }
  
  public static void selectPoint(Context context, Point2D point) {
    Intent intent = new Intent(context, PointSelectionActivity.class);
    IntentHelper.putLocation(intent, point);
    context.startActivity(intent);    
  }
  
  NamedPoint getNamedPoint() {
    if ( rstop != null) {
      Stop stop = rstop.getStop();
      NamedPoint point = new NamedPoint(stop);
      Route route = Info.routeManager(this).getRoute(rstop.getRouteId());
      point.setName(stop.getName() + " " + route.getLabel());
      return point;
    } else {
      return point;
    }
  }
  
  Point2D getPoint() {
    if ( rstop != null) {
      return rstop.getStop();
    } else {
      return point;
    }
  }
  
  @Override
  public void onClick(View v) {
    Button button = (Button)v;
    NamedPoint p = null;
    switch(button.getId()) {
      case R.id.nearby:
        showNearby();
        break;
      case R.id.add:
        if ( rstop != null) {
          addToSequence(true);
        }
        break;
      case R.id.origin:
        p = getNamedPoint();
        if ( p != null) {
          PlanFragment.origin = p;
          showPlan();          
        }
        break;
      case R.id.destination:
        p = getNamedPoint();
        if ( p != null) {
          PlanFragment.destination = p;
          showPlan();          
        }
        break;
    }
  }

  
}
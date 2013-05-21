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

import java.util.List;

import org.melato.android.AndroidLogger;
import org.melato.android.location.Locations;
import org.melato.android.progress.ActivityProgressHandler;
import org.melato.android.progress.ProgressTitleHandler;
import org.melato.android.util.LabeledPoint;
import org.melato.bus.android.Info;
import org.melato.bus.android.R;
import org.melato.bus.model.RStop;
import org.melato.bus.model.Route;
import org.melato.bus.plan.Leg;
import org.melato.bus.plan.LegGroup;
import org.melato.bus.plan.Plan;
import org.melato.bus.plan.PlanLeg;
import org.melato.bus.plan.Planner;
import org.melato.bus.plan.Sequence;
import org.melato.gps.Point2D;
import org.melato.log.Log;
import org.melato.progress.ProgressGenerator;

import android.app.ListActivity;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/** Computes and displays a list of plans for going to a destination.
 * This is experimental.  It is not part of the production app yet.
 * */
public class PlanActivity extends ListActivity {
  private ActivityProgressHandler progress;
  private BusActivities activities;
  private Point2D origin;  
  private Point2D destination;
  private Plan[] plans;

  class PlanTask extends AsyncTask<Void,Void,Plan[]> {    
    @Override
    protected void onPreExecute() {
      //setTitle(R.string.computing);
    }

    @Override
    protected Plan[] doInBackground(Void... params) {
      ProgressGenerator.setHandler(progress);
      Planner planner = null;
      //planner = new Nearby1SingleRoutePlanner();
      planner.setRouteManager(Info.routeManager(PlanActivity.this));
      return planner.plan(origin, destination);
    }

    @Override
    protected void onPostExecute(Plan[] plans) {
      PlanActivity.this.plans = plans;
      setTitle(R.string.best_route);
      setListAdapter(new ArrayAdapter<Plan>(PlanActivity.this, R.layout.list_item, plans));
      progress.end();
    }
  }
  
  Point2D getOrigin() {
    Sequence sequence = Info.getSequence(this);
    List<LegGroup> legs = sequence.getLegs();
    if ( ! legs.isEmpty()) {
      Leg last = legs.get(legs.size()-1).getLeg();
      if ( last.getStop2() != null) {
        return last.getStop2();
      }
      return last.getStop1();
    }
    LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
    Location loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
    return Locations.location2Point(loc);    
  }
  
/** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    activities = new BusActivities(this);
    progress = new ProgressTitleHandler(this);
    Log.setLogger(new AndroidLogger(this));
    origin = getOrigin();
    //origin = new Point2D(37.9997f, 23.7848f);
    LabeledPoint point = Locations.getGeoUri(getIntent());
    Log.info("lp: " + point);
    if ( point != null) {
      Log.info("point: " + point.getLabel());
      destination = point.getPoint();
    }
    if ( destination == null) {
      setTitle("Missing Destination");
    } else if ( origin == null) {
      setTitle("Missing Origin");
    } else {
      new PlanTask().execute();      
    }
  }

  @Override
  protected void onListItemClick(ListView l, View v, int position, long id) {
    Plan plan = plans[position];
    PlanLeg[] legs = plan.getLegs();
    if ( legs.length > 0 ) {
      PlanLeg leg = legs[0];
      Route route = leg.getRoute();
      RStop rstop = new RStop(route.getRouteId(), leg.getStop1());
      activities.showRoute(rstop);
    }
  }

}
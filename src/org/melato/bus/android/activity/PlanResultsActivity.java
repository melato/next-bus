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
import org.melato.bus.model.RStop;
import org.melato.bus.model.Route;
import org.melato.bus.plan.Plan;
import org.melato.bus.plan.PlanLeg;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

/** Computes and displays a list of plans for going to a destination.
 * This is experimental.  It is not part of the production app yet.
 * */
public class PlanResultsActivity extends ListActivity {
  private BusActivities activities;
  private Plan[] plans;

/** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    activities = new BusActivities(this);
    //plans = PlanActivity.plans;    
    setListAdapter(new PlanAdapter());
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
  
  class PlanAdapter extends ArrayAdapter<Plan> {
    public PlanAdapter() {
      super(PlanResultsActivity.this, R.layout.list_item, plans); 
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
      TextView view = (TextView) super.getView(position, convertView, parent);
      Plan plan = plans[position];
      String text = plan.getLabel();
      view.setText( text );
      return view;
    }
  }
}
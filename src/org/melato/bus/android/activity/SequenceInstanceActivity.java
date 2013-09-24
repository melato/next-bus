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

import org.melato.bus.android.Info;
import org.melato.bus.android.R;
import org.melato.bus.model.RouteManager;
import org.melato.bus.plan.LegAdapter;
import org.melato.bus.plan.SequenceItinerary;
import org.melato.bus.plan.SequenceLegAdapter;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Displays a sequence instance:  An instance schedule at a particular time.
 * @author Alex Athanasopoulos
 */
public class SequenceInstanceActivity extends ListActivity {
  public static final String KEY_ITINERARY = "org.melato.bus.android.itinerary";
  /** This is really SequenceInstanceLeg[], but somehow its type does not survive serialization. */
  private SequenceItinerary itinerary;
  private RouteManager routeManager;

  public SequenceInstanceActivity() {
  }
  
  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    itinerary = (SequenceItinerary) getIntent().getSerializableExtra(KEY_ITINERARY);
    if ( itinerary == null) {
      finish();
    }
    routeManager = Info.routeManager(this);
    setListAdapter(new ItineraryAdapter());
  }

  class ItineraryAdapter extends ArrayAdapter<SequenceItinerary.Leg> {
    public ItineraryAdapter() {
      super(SequenceInstanceActivity.this, R.layout.list_item, itinerary.legs); 
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
      TextView view = (TextView) super.getView(position, convertView, parent);
      SequenceItinerary.Leg leg = itinerary.legs[position];
      LegAdapter adapter = new SequenceLegAdapter(leg, routeManager);
      String text = LegFormatter.label(adapter, SequenceInstanceActivity.this);
      view.setText( text );
      return view;
    }
  }
  
  @Override
  protected void onListItemClick(ListView l, View v, int position, long id) {
    SequenceItinerary.Leg leg = itinerary.legs[position];
    if ( leg instanceof SequenceItinerary.TransitLeg) {
      SequenceItinerary.TransitLeg transit = (SequenceItinerary.TransitLeg) leg;
      BusActivities activities = new BusActivities(this);
      activities.showRoute(transit.leg.getRStop1());    
    }
  }    
}
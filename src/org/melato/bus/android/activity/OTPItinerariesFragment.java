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
import org.melato.bus.model.Schedule;
import org.melato.bus.otp.OTP;
import org.melato.bus.otp.OTP.Leg;
import org.melato.bus.otp.OTP.TransitLeg;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

/** Displays the itineraries of an OTP plan */
public class OTPItinerariesFragment extends Fragment implements OnItemClickListener {
  private OTP.Plan plan;
  ListView listView;

  
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
          Bundle savedInstanceState) {
      View view = inflater.inflate(R.layout.planresults, container, false);
      listView = (ListView) view.findViewById(R.id.listView);
      listView.setOnItemClickListener(this);
      return view;
  }
  
  @Override
  public void onResume() {
    super.onResume();
    plan = PlanFragment.plan;    
    if ( plan != null) {
      listView.setAdapter(new ItinerariesAdapter());
    }
  }

  @Override
  public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    SequenceActivities.showItinerary(getActivity(), plan.itineraries[position]);
  }

  public void itineraryTimes(OTP.Itinerary itinerary, StringBuilder buf) {
    int startTime = Schedule.getSeconds(itinerary.startTime);
    int endTime = startTime + (int) (itinerary.endTime.getTime()-itinerary.startTime.getTime()) / 1000;
    buf.append(Schedule.formatTime(startTime/60));
    buf.append(" -> ");
    buf.append(Schedule.formatTime(endTime/60));
    buf.append(" (");
    buf.append(Schedule.formatDuration(endTime-startTime));
    buf.append(")"); 
  }
  
  public String itineraryLabel(OTP.Itinerary itinerary) {
    StringBuilder buf = new StringBuilder();
    itineraryTimes(itinerary, buf);
    for(Leg leg: itinerary.legs) {
      if ( leg instanceof TransitLeg ) {
        TransitLeg t = (TransitLeg)leg;
        buf.append( " " );        
        buf.append(t.label);
      }
    }
    return buf.toString();
  }    
  
  class ItinerariesAdapter extends ArrayAdapter<OTP.Itinerary> {
    public ItinerariesAdapter() {
      super(getActivity(), R.layout.list_item, plan.itineraries); 
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
      TextView view = (TextView) super.getView(position, convertView, parent);
      OTP.Itinerary itinerary = plan.itineraries[position];
      String text = itineraryLabel(itinerary);
      view.setText( text );
      return view;
    }
  }
}
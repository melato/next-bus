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
import org.melato.bus.model.Schedule;
import org.melato.bus.otp.OTP;
import org.melato.bus.otp.OTP.Leg;
import org.melato.bus.otp.OTP.TransitLeg;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

/** Displays the itineraries of an OTP plan */
public class OTPItinerariesActivity extends ListActivity {
  private OTP.Plan plan;

/** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    plan = PlanActivity.plan;    
    setListAdapter(new ItinerariesAdapter());
  }

  @Override
  protected void onListItemClick(ListView l, View v, int position, long id) {
    OTP.Itinerary itinerary = plan.itineraries[position];
    Intent intent = new Intent(this, OTPItineraryActivity.class);
    intent.putExtra(OTPItineraryActivity.ITINERARY, itinerary);
    startActivity(intent);
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
      super(OTPItinerariesActivity.this, R.layout.list_item, plan.itineraries); 
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
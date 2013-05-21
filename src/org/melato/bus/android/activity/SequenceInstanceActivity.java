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
import org.melato.bus.android.activity.SequenceActivity.WalkItem;
import org.melato.bus.plan.SequenceInstance.LegInstance;
import org.melato.bus.plan.SequenceInstance.WalkInstance;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/**
 * Displays a sequence instance:  An instance schedule at a particular time.
 * @author Alex Athanasopoulos
 */
public class SequenceInstanceActivity extends ListActivity {
  public static final String KEY_LEGS = "org.melato.bus.android.legs";
  private Object[] legs;

  public SequenceInstanceActivity() {
  }
  
  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    legs = (Object[]) getIntent().getSerializableExtra(KEY_LEGS);
    if ( legs == null) {
      finish();
    }
    // translate strings
    String waitString = getString(R.string.wait);
    for(int i = 0; i < legs.length; i++ ) {
      if ( legs[i] instanceof WalkInstance ) {
        legs[i] = new WalkItem((WalkInstance) legs[i], this);
      } else if ( legs[i] instanceof LegInstance) {
        LegInstance leg = (LegInstance) legs[i];
        leg.setWaitString(waitString);
      }
    }
    setListAdapter(new ArrayAdapter<Object>(this, R.layout.list_item, legs));
  }

  @Override
  protected void onListItemClick(ListView l, View v, int position, long id) {
    Object leg = legs[position];
    if ( leg instanceof LegInstance) {
      LegInstance legInstance = (LegInstance) leg;
      BusActivities activities = new BusActivities(this);
      activities.showRoute(legInstance.getRStop());    
    }
  }    
}
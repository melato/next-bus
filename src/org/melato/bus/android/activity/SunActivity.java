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


import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.melato.bus.android.Info;
import org.melato.bus.android.R;
import org.melato.bus.android.app.HelpActivity;
import org.melato.bus.model.RouteStorage;
import org.melato.sun.SunsetProvider;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.Menu;
import android.widget.ArrayAdapter;

/** An activity that shows the sunrise and sunset for the current date. */
public class SunActivity extends ListActivity {  
  DecimalFormat format2d = new DecimalFormat("00");
  List<String> items = new ArrayList<String>();
  
  void addProperty(int resourceId, String value) {
    items.add( getString(resourceId) + ": " + value );    
  }
  
  String formatTime(int time) {
    return format2d.format( time / 60 ) + ":" + format2d.format(time % 60);
  }
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setTitle(R.string.sunrise_sunset);
    RouteStorage storage = Info.routeManager(this).getStorage();
    if ( storage instanceof SunsetProvider) {
      SunsetProvider sun = (SunsetProvider) storage;
      Date date = new Date();      
      int[] values = sun.getSunriseSunset(date);
      if ( values != null) {
        DateFormat dateFormat = new SimpleDateFormat("d-M-y");
        addProperty(R.string.date, dateFormat.format(date));
        addProperty(R.string.sunrise, formatTime(values[0]));
        addProperty(R.string.sunset, formatTime(values[1]));
      }
    }
    setListAdapter( new ArrayAdapter<String>(this, R.layout.list_item, items));
  }
  
  @Override
  public boolean onCreateOptionsMenu(Menu menu)
  {
     HelpActivity.addItem(menu, this, Help.SUN);
     return true;
  }
}

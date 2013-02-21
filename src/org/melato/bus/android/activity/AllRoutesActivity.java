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

import org.melato.bus.android.R;
import org.melato.bus.android.app.HelpActivity;
import org.melato.bus.model.RouteGroup;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.EditText;
import android.widget.ListView;

/**
 * Displays the list of all routes
 * @author Alex Athanasopoulos
 *
 */
public class AllRoutesActivity extends RoutesActivity {
  private RouteGroup[] all_groups;
  private EditText editView;
  
  class TextListener implements TextWatcher {

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count,
        int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void afterTextChanged(Editable s) {
      String text = s.toString();
      int position = findPosition(text);
      if ( position >= 0 ) {
        ListView listView = getListView();
        listView.setSelectionFromTop(position, 0);
      }
    }    
  }
  
  protected Object[] initialRoutes() {
    if ( all_groups == null ) {
      List<RouteGroup> groups = RouteGroup.group(activities.getRouteManager().getRoutes());
      all_groups = groups.toArray(new RouteGroup[0]);
    }
    return all_groups;
  }
  
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.all_routes);
    editView = (EditText) findViewById(R.id.routeFilter);
    editView.addTextChangedListener(new TextListener());    
  }
  
  private int findPosition( String text ) {
    if ( text != null ) {
      text = text.trim();
    }
    if ( text.length() == 0 )
      text = null;
    if ( text == null ) {
      return -1;
    }
    text = text.toUpperCase();
    for( int i = 0; i < all_groups.length; i++ ) {
      if ( all_groups[i].getTitle().startsWith(text)) {
        return i;
      }      
    }
    return -1;
  }
  
  @Override
  public boolean onCreateOptionsMenu(Menu menu)
  {
     MenuInflater inflater = getMenuInflater();
     inflater.inflate(R.menu.all_routes_menu, menu);
     HelpActivity.addItem(menu, this, R.string.help_all);
     return true;
  }

  
}
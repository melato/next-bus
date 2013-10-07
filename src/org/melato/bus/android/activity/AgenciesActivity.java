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
import org.melato.bus.android.app.HelpActivity;
import org.melato.bus.model.Agency;

import android.app.ListActivity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

/**
 * Displays the list of all routes
 * @author Alex Athanasopoulos
 *
 */
public class AgenciesActivity extends ListActivity {
  private Agency[] agencies;
  
  class AgencyAdapter extends ArrayAdapter<Agency> {
    
    public AgencyAdapter() {
      super(AgenciesActivity.this, R.layout.agency_item, R.id.text, agencies);
    }
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
      View view = super.getView(position,convertView,parent);
      ImageView imageView = (ImageView) view.findViewById(R.id.icon);
      Agency agency = agencies[position];
      Drawable drawable = Info.getAgencyIcon(AgenciesActivity.this, agency);
      imageView.setImageDrawable(drawable);
      return view;
    }
  }
  public AgenciesActivity() {
  }

  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    LinearLayout box = new LinearLayout(this);
    box.setOrientation(LinearLayout.VERTICAL);
    agencies = Info.routeManager(this).getAgencies();
    setListAdapter(new AgencyAdapter());
  }
  
  @Override
  protected void onPause() {
    // This is called before the previous activity's onResume.
    super.onPause();
    getSelectedItemPosition();
  }
  
  @Override
  protected void onListItemClick(ListView l, View v, int position, long id) {
    super.onListItemClick(l, v, position, id);
    Info.setDefaultAgencyName(this, agencies[position].getName());
    startActivity(new Intent(this, AllRoutesActivity.class));      
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu)
  {
     HelpActivity.addItem(menu, this, Help.AGENCIES);
     return true;
  }

  
}

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

import org.melato.android.ui.PropertiesDisplay;
import org.melato.android.util.Invokable;
import org.melato.android.util.LocationField;
import org.melato.android.util.PhoneField;
import org.melato.android.util.UrlField;
import org.melato.bus.android.R;
import org.melato.bus.android.app.HelpActivity;
import org.melato.bus.model.Municipality;
import org.melato.gps.Point2D;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;

/**
 * Displays information about a municipality.
 * @author Alex Athanasopoulos
 *
 */
public class MunicipalityActivity extends ListActivity implements OnItemClickListener
{
  public static final String KEY_MUNICIPALITY = "org.melato.bus.municipality";
  private PropertiesDisplay properties;
  private Municipality municipality;
  
  public MunicipalityActivity() {
  }

  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    municipality = (Municipality) getIntent().getSerializableExtra(KEY_MUNICIPALITY);
    properties = new PropertiesDisplay(this);
    setTitle(municipality.getName());   
    properties.add(R.string.municipality, municipality.getName());
    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
    boolean prefMayor = prefs.getBoolean(Pref.MAYOR, true);
    if (prefMayor) {
      properties.add(R.string.mayor, municipality.getMayor());
    }    
    //properties.add(R.string.website, municipality.getWebsite());
    properties.add(new UrlField(municipality.getWebsite()));
    if ( municipality.hasDetails()) {
      //properties.add(R.string.police_phone, municipality.getPolice());
      String phone = municipality.getPhone();
      if ( phone != null) {
        properties.add(new PhoneField(getString(R.string.police_phone), phone));
      }
      Point2D point = municipality.getPoint();
      if ( point != null) {
        properties.add(new LocationField(getString(R.string.city_hall), point));
      }
      properties.add(municipality.getAddress());
      properties.add(municipality.getCity() + " " + municipality.getPostalCode());
    }
    ArrayAdapter<Object> adapter;
    adapter = properties.createAdapter(R.layout.stop_item, R.color.black, R.color.stop_link);
    setListAdapter(adapter);
    getListView().setOnItemClickListener(this);
  }
  
  public static void start(Context context, Municipality municipality) {
    Intent intent = new Intent(context, MunicipalityActivity.class);
    intent.putExtra(KEY_MUNICIPALITY, municipality);
    context.startActivity(intent);    
  }
  
  @Override
  public void onItemClick(AdapterView<?> parent, View view, int position,
      long id) {
    Object value = properties.getItem(position);
    if ( value instanceof Invokable) {
      ((Invokable)value).invoke(this);
    }
  }
  
  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    HelpActivity.addItem(menu, this, Help.MUNICIPALITY);
    return true;
  }
  
  
}
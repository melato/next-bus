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
package org.melato.bus.android.app;

import java.util.Locale;

import org.melato.bus.android.PlanOptions;
import org.melato.bus.android.R;
import org.melato.bus.android.activity.Pref;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.util.Log;

public class BusPreferencesActivity extends PreferenceActivity {
  private String lang;
  
  private void setTextSummary(String key, String summary) {
    EditTextPreference pref = (EditTextPreference) findPreference(key);
    if ( pref != null) {
      pref.setSummary(summary);
    }
  }
  
  @Override
  protected void onCreate( Bundle savedInstanceState ) 
  {
      super.onCreate( savedInstanceState );
      addPreferencesFromResource( R.layout.settings_plan );
      addPreferencesFromResource( R.layout.settings );
      
      SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
      lang = settings.getString(Pref.LANG, "");
  }
  @Override
  protected void onResume() {
    super.onResume();
    PlanOptions options = new PlanOptions(this);

    setTextSummary(Pref.WALK_SPEED, String.valueOf(Math.round(options.getWalkSpeed())) + " Km/h");
    setTextSummary(Pref.MAX_WALK_DISTANCE, String.valueOf(Math.round(options.getMaxWalk())) + " m");
    setTextSummary(Pref.MIN_TRANSFER_TIME,
        getString(R.string.min_transfer_time_summary, Math.round(options.getMinTransferTime() / 60f)));
  }
  void updateLocale(String lang) {
    Locale locale = new Locale(lang);
    Resources resources = getBaseContext().getResources(); 
    Configuration config = resources.getConfiguration();
    config.locale = locale;
    resources.updateConfiguration(config, resources.getDisplayMetrics());
    
  }
  @Override
  protected void onDestroy() {
    SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
    String lang = settings.getString(Pref.LANG, "");
    if ( ! this.lang.equals(lang)) {
      updateLocale(lang);      
    }
    super.onDestroy();
  }  
  
}

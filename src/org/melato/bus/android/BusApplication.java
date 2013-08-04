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
package org.melato.bus.android;

import java.util.Locale;

import org.melato.bus.android.activity.Pref;

import android.app.Application;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.preference.PreferenceManager;

public class BusApplication extends Application {
  private Locale locale;

  private void updateLocale(Configuration config) {
    config.locale = locale;
    Locale.setDefault(locale);
    Resources resources = getBaseContext().getResources(); 
    resources.updateConfiguration(config, resources.getDisplayMetrics());
  }
  @Override
  public void onConfigurationChanged(Configuration newConfig) {
    super.onConfigurationChanged(newConfig);
    if ( locale != null ) {
      updateLocale(newConfig);
    }
  }
  @Override
  public void onCreate() {
    super.onCreate();
    SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);

    Resources resources = getBaseContext().getResources(); 
    Configuration config = resources.getConfiguration();

    String lang = settings.getString(Pref.LANG, "");
    if (! "".equals(lang) && ! config.locale.getLanguage().equals(lang))
    {
        locale = new Locale(lang);
        updateLocale(config);
    }
  }
}

/*-------------------------------------------------------------------------
 * Copyright (c) 2012, Alex Athanasopoulos.  All Rights Reserved.
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

import org.melato.bus.android.R;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

public class AboutActivity extends HelpActivity {
  
  @Override
  protected void setHelpText(TextView view) {
    String appVersion = "";
    PackageInfo packageInfo;
   try {
     packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0 );
     appVersion = packageInfo.versionName;
   } catch (NameNotFoundException e) {
   } 
    String text = String.format(getString(R.string.help_about), appVersion ); 
    view.setText(text);
  }
  
  @Override
  public boolean onCreateOptionsMenu(Menu menu)
  {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.about_menu, menu);
    //HelpActivity.addItem(menu, this, R.string.terms_of_use);
    return true;
  }
  
  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    if ( item.getItemId() == R.id.terms ) {
      HelpActivity.showHelp(this, R.string.eula, R.string.terms_of_use);
      return true;
    }
    return super.onOptionsItemSelected(item);
  }
}

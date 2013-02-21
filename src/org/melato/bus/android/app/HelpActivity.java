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

import org.melato.bus.android.Info;
import org.melato.bus.android.R;
import org.melato.bus.android.db.SqlRouteStorage;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.widget.TextView;

public class HelpActivity extends Activity {
  public static final String KEY_ID = "help_id";
  public static final String KEY_TITLE = "help_title";
  public static final String KEY_MENU = "help_menu";
  public static final String KEY_FORMAT = "help_format";
  private boolean hasMenu = true;
  private boolean hasSubstitutions = false;
  
  protected void setHelpText(TextView view) {
    int helpId = getIntent().getIntExtra(KEY_ID, R.string.help_default);
    if ( hasSubstitutions ) {
      String appVersion = "?";
      PackageInfo packageInfo;
      try {
        packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        appVersion = packageInfo.versionName;
      } catch (NameNotFoundException e) {
        throw new RuntimeException(e);
      }
      SqlRouteStorage routeDB = (SqlRouteStorage) Info.routeManager(this).getStorage();
      String databaseDate = routeDB.getBuildDate();
      if ( databaseDate == null) {
        databaseDate = "?";
      }
      String text = String.format(getString(helpId), appVersion, databaseDate);
      view.setText(Html.fromHtml(text));
    } else {
      view.setText(helpId);
    }
  }
  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.help);
    int titleId = getIntent().getIntExtra(KEY_TITLE, R.string.help);
    setTitle(titleId);
    hasMenu = getIntent().getBooleanExtra(KEY_MENU, true);
    hasSubstitutions = getIntent().getBooleanExtra(KEY_FORMAT, false);
    TextView helpView = (TextView) findViewById(R.id.help);
    setHelpText(helpView);
    helpView.setMovementMethod(new ScrollingMovementMethod());    
  }
  
  public static void showHelp(Context context, int helpId) {
    showHelp(context, helpId, R.string.help, true);
  }
  public static void showHelp(Context context, int helpId, int titleId) {
    showHelp(context, helpId, titleId, true);
  }  
  public static void showHelp(Context context, int helpId, int titleId, boolean useMenu) {
    showHelp(context, helpId, titleId, useMenu, false);
  }
  public static void showHelp(Context context, int helpId, int titleId, boolean useMenu, boolean hasSubstitutions) {
    Intent intent = new Intent(context, HelpActivity.class);
    intent.putExtra(KEY_ID, helpId);
    intent.putExtra(KEY_TITLE, titleId);
    if ( ! useMenu )
      intent.putExtra(KEY_MENU, false);
    if ( hasSubstitutions ) {
      intent.putExtra(KEY_FORMAT, true);
    }
    context.startActivity(intent);
  }
  static class HelpListener implements OnMenuItemClickListener {
    Context context;
    int helpId;
    public HelpListener(Context context, int helpId) {
      super();
      this.context = context;
      this.helpId = helpId;
    }
    @Override
    public boolean onMenuItemClick(MenuItem item) {
      showHelp(context, helpId);
      return true;
    }
    
  }
  public static void addItem(Menu menu, Context context, int helpId) {
    MenuItem item = menu.add(R.string.help);
    item.setOnMenuItemClickListener(new HelpListener(context, helpId));
    item.setIcon(R.drawable.help);
  }
  
  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    if ( hasMenu ) {
      MenuInflater inflater = getMenuInflater();
      inflater.inflate(R.menu.help_menu, menu);
    }
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch( item.getItemId() ) {
      case R.id.about:
        HelpActivity.showHelp(this, R.string.help_about, R.string.about, false, true);
        //startActivity( new Intent(this, AboutActivity.class));
        break;
      case R.id.terms:
        HelpActivity.showHelp(this, R.string.eula, R.string.terms_of_use, false);
        break;
      case R.id.pref:
        startActivity( new Intent(this, BusPreferencesActivity.class));    
        break;
      default:
        return super.onOptionsItemSelected(item);    
    }
    return true;    
  }
  
  
}

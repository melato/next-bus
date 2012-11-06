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

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.widget.TextView;

public class HelpActivity extends Activity {
  public static final String KEY_ID = "help_id";
  public static final String KEY_TITLE = "help_title";
  
  protected void setHelpText(TextView view) {
    int helpId = getIntent().getIntExtra(KEY_ID, R.string.help_default);
    view.setText(helpId);
  }
  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.help);
    int titleId = getIntent().getIntExtra(KEY_TITLE, R.string.help);
    setTitle(titleId);
    TextView helpView = (TextView) findViewById(R.id.help);
    setHelpText(helpView);
    helpView.setMovementMethod(new ScrollingMovementMethod());    
  }
  
  public static void showHelp(Context context, int helpId) {
    showHelp(context, helpId, R.string.help);
  }
  public static void showHelp(Context context, int helpId, int titleId) {
    Intent intent = new Intent(context, HelpActivity.class);
    intent.putExtra(KEY_ID, helpId);
    intent.putExtra(KEY_TITLE, titleId);
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
  }
  
  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.help_menu, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    if ( item.getItemId() == R.id.about ) {
      startActivity( new Intent(this, AboutActivity.class));
      return true;
    }
    return super.onOptionsItemSelected(item);
  }
  
  
}

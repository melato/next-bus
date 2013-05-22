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
import org.melato.bus.android.app.BusPreferencesActivity;
import org.melato.bus.android.app.HelpActivity;
import org.melato.bus.android.app.UpdateActivity;
import org.melato.bus.android.map.RouteMapActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;

/** The main activity checks for updates and launches the next activity. */
public class HomeActivity extends Activity implements OnItemClickListener {
  static class LaunchItem {
    Class<? extends Activity> activity;
    int drawable;
    int text;
    public LaunchItem(Class<? extends Activity> activity, int drawable, int text) {
      super();
      this.activity = activity;
      this.drawable = drawable;
      this.text = text;
    }    
    protected LaunchItem(int drawable, int text) {
      super();
      this.drawable = drawable;
      this.text = text;
    }
    public void invoke(Context context) {
      context.startActivity(new Intent(context, activity));      
    }
  }
  static class About extends LaunchItem {
    
    public About() {
      super(R.drawable.about, R.string.about);
    }

    public void invoke(Context context) {
      HelpActivity.showHelp(context, R.string.help_about, R.string.about, false, true);        
    }
  }
  static class Twitter extends LaunchItem {
    
    public Twitter() {
      super(R.drawable.twitter, R.string.twitter);
    }

    public void invoke(Context context) {
      Uri uri = Uri.parse("http://twitter.com/athensnextbus");
      Intent intent = new Intent(Intent.ACTION_VIEW, uri);
      context.startActivity(intent);
    }
  }
  
  
  // references to our images
  private LaunchItem[] items = {
      new LaunchItem(AllRoutesActivity.class, R.drawable.all, R.string.all_routes),
      new LaunchItem(RecentRoutesActivity.class, R.drawable.recent, R.string.menu_recent_routes),
      new LaunchItem(NearbyActivity.class, R.drawable.nearby, R.string.menu_nearby_routes),
      new LaunchItem(RouteMapActivity.class, R.drawable.map, R.string.map),
      new LaunchItem(AgenciesActivity.class, R.drawable.agencies, R.string.menu_agencies),
      new LaunchItem(SequenceActivity.class, R.drawable.sequence, R.string.sequence),
      new LaunchItem(BusPreferencesActivity.class, R.drawable.preferences, R.string.pref_menu),
      new About(),
      //new Twitter(),
  };
  
  
  /** Called when the activity is first created. */  
  @Override
  public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      if ( ! UpdateActivity.checkUpdates(this) ) {
        finish();
        return;
      }
      setContentView(R.layout.home);
      GridView grid = (GridView) findViewById(R.id.gridView);
      grid.setAdapter(new ImageAdapter(this));
      grid.setOnItemClickListener(this);
  }


  void select(int position) {
    LaunchItem item = items[position];
    item.invoke(this);
  }
  @Override
  public void onItemClick(AdapterView<?> parent, View view, int position,
      long id) {
    select(position);
  }

  public class ImageAdapter extends BaseAdapter {
    private Context context;

    public ImageAdapter(Context c) {
        context = c;
    }

    public int getCount() {
        return items.length;
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        Button button;
        if (convertView == null) {  // if it's not recycled, initialize some attributes
            button = new Button(context);
            button.setLayoutParams(new GridView.LayoutParams(
                (int) getResources().getDimension(R.dimen.grid_width),                                                                                   
                (int) getResources().getDimension(R.dimen.grid_height)));
            button.setPadding(8, 8, 8, 8);
        } else {
            button = (Button) convertView;
        }
        LaunchItem item = items[position];
        button.setCompoundDrawablesWithIntrinsicBounds(0, item.drawable, 0, 0);
        button.setText(item.text);
        //button.setBackgroundColor(Color.BLACK);
        //button.setTextColor(Color.WHITE);
        button.setOnClickListener(new ButtonListener(position));
        return button;
    }
  }
  
  class ButtonListener implements OnClickListener {
    int pos;
    public ButtonListener(int pos) {
      super();
      this.pos = pos;
    }

    @Override
    public void onClick(View v) {
      select(pos);
    }
    
  }
}

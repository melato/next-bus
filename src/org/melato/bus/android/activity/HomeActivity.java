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

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.melato.android.util.Invokable;
import org.melato.bus.android.Info;
import org.melato.bus.android.R;
import org.melato.bus.android.app.BusPreferencesActivity;
import org.melato.bus.android.app.HelpActivity;
import org.melato.bus.android.app.UpdateActivity;
import org.melato.bus.android.map.RouteMapActivity;
import org.melato.bus.client.Menu;
import org.melato.bus.client.MenuStorage;
import org.melato.util.DateId;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
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
  List<LaunchItem> items = new ArrayList<LaunchItem>();
  static interface LaunchItem extends Invokable {
    public void init(Button button);
  }
  static class InternalLaunchItem implements LaunchItem {
    Class<? extends Activity> activity;
    int drawable;
    int text;
    public InternalLaunchItem(Class<? extends Activity> activity, int drawable, int text) {
      super();
      this.activity = activity;
      this.drawable = drawable;
      this.text = text;
    }    
    protected InternalLaunchItem(int drawable, int text) {
      super();
      this.drawable = drawable;
      this.text = text;
    }
    public void init(Button button) {
      button.setCompoundDrawablesWithIntrinsicBounds(0, drawable, 0, 0);
      button.setText(text);
      setButtonColors(button);
    }
    public void invoke(Context context) {
      context.startActivity(new Intent(context, activity));      
    }
  }
  static class MenuLaunchItem implements LaunchItem {
    Drawable drawable;
    Menu menu;
    
    public MenuLaunchItem(Context context, Menu menu) {      
      this.menu = menu;
      MenuStorage db = Info.menuManager(context);
      if ( menu.getIcon() != null) {
        byte[] icon = db.loadImage(menu.getIcon());
        if ( icon != null) { 
          Options options = new BitmapFactory.Options();
          options.inDensity = DisplayMetrics.DENSITY_DEFAULT;
          InputStream in = new ByteArrayInputStream(icon);
          drawable = Drawable.createFromResourceStream(context.getResources(), null, in, menu.icon, options);
        }
      }
    }
    public void invoke(Context context) {
      if ( "url".equals( menu.type)){ 
        Uri uri = Uri.parse(menu.target);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        context.startActivity(intent);
      } else if ("help".equals(menu.type)) {
        HelpActivity.showHelp(context, menu.target);
      }
    }
    @Override
    public void init(Button button) {
      button.setText(menu.getLabel());
      button.setCompoundDrawablesWithIntrinsicBounds(null, drawable, null, null);
      setButtonColors(button);
    }    
  }
  static void setButtonColors(Button button) {
    button.setBackgroundColor(Color.TRANSPARENT);
    button.setTextColor(Color.WHITE);
  }
  static class Help extends InternalLaunchItem {
    private String helpName;
    
    public Help(int icon, int label, String helpName) {
      super(icon, label);
      this.helpName = helpName;
    }

    public void invoke(Context context) {
      HelpActivity.showHelp(context, helpName);
    }
  }

  static class About extends Help {    
    public About() {
      super(R.drawable.about, R.string.about, "about");
    }
  }  
  
  // references to our images
  private InternalLaunchItem[] internalItems = {
      new InternalLaunchItem(AllRoutesActivity.class, R.drawable.all, R.string.all_routes),
      new InternalLaunchItem(RecentRoutesActivity.class, R.drawable.recent, R.string.menu_recent_routes),
      new InternalLaunchItem(AgenciesActivity.class, R.drawable.agencies, R.string.menu_agencies),
      new InternalLaunchItem(SequenceActivity.class, R.drawable.sequence, R.string.sequence),
      new InternalLaunchItem(PlanTabsActivity.class, R.drawable.plan, R.string.search),
      new InternalLaunchItem(NearbyActivity.class, R.drawable.nearby, R.string.menu_nearby_routes),
      new InternalLaunchItem(RouteMapActivity.class, R.drawable.map, R.string.map),
      new InternalLaunchItem(SunActivity.class, R.drawable.sun, R.string.sun),
      new InternalLaunchItem(BusPreferencesActivity.class, R.drawable.preferences, R.string.pref_menu),
      new About(),
  };
  
  void initMenus() {
    for( LaunchItem item: internalItems ) {
      items.add(item);
    }
    MenuStorage db = (MenuStorage) Info.routeManager(this).getStorage();
    int dateId = DateId.dateId(new Date());
    for(Menu menu: db.loadMenus() ) {
      if ( menu.isActive(dateId)) {
        items.add( new MenuLaunchItem(this, menu));
      }
    }    
  }
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
      initMenus();
      grid.setAdapter(new ImageAdapter(this));
      grid.setOnItemClickListener(this);
  }


  void select(int position) {
    Invokable item = items.get(position);
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
        return items.size();
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
        LaunchItem item = items.get(position);
        item.init(button);
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

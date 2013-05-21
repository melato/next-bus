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
import org.melato.bus.android.activity.ScheduleActivity.TextColor;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class HighlightAdapter <T> extends ArrayAdapter<T> {
  int selection;
  TextColor normalColor;
  TextColor selectedColor;
  
  public HighlightAdapter(Context context, List<T> items) {
    super(context, R.layout.list_item, items);
    selectedColor = new TextColor(context, R.color.list_highlighted_text, R.color.list_highlighted_background);
    normalColor = new TextColor(context, R.color.list_text, R.color.list_background);
  }  
  
  public void setSelection(int selection) {
    this.selection = selection;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    TextView view = (TextView) super.getView(position, convertView, parent);
    if ( position == selection ) {
      selectedColor.apply(view);
    } else {
      normalColor.apply(view);
    }
    return view;
  }
}

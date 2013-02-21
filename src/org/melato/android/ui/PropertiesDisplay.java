/*-------------------------------------------------------------------------
 * Copyright (c) 2012,2013, Alex Athanasopoulos.  All Rights Reserved.
 * alex@melato.org
 *-------------------------------------------------------------------------
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *-------------------------------------------------------------------------
 */
package org.melato.android.ui;

import java.util.ArrayList;
import java.util.List;

import org.melato.android.util.Invokable;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;


/**
 * Helper class for displaying a list of labeled items.
 * @author Alex Athanasopoulos
 */
public class PropertiesDisplay {
  private Context context;
  protected List<Object> items = new ArrayList<Object>();

  
  public PropertiesDisplay(Context context) {
    super();
    this.context = context;
  }

  public static String formatProperty( String label, Object value ) {
    String s = (value == null) ? "" : value.toString();
    if ( label != null && label.length() != 0 ) {
      s = label + ": " + s;
    }
    return s;
  }
  
  public String formatProperty( int labelResourceId, Object value ) {
    return formatProperty( context.getResources().getString(labelResourceId), value);
  }
  
  /** An item is a label and a value. */
  public static class Item {
    int   id;
    String label;
    Object value;
    
    public Item(Context context, int labelResourceId, Object value ) {
      this.id = labelResourceId;
      this.label = context.getResources().getString(labelResourceId);
      this.value = value;
    }
    public Item(String label, Object value) {
      super();
      this.label = label;
      this.value = value;
    }
    @Override
    public String toString() {
      return formatProperty(label, value);
    }
    public int getId() {
      return id;
    }
    public String getLabel() {
      return label;
    }
    public void setLabel(String label) {
      this.label = label;
    }
    public Object getValue() {
      return value;
    }
    public void setValue(Object value) {
      this.value = value;
    }        
  }
  
  public void add( Object item ) {
    items.add(item);
  }
  public void add( String label, Object value ) {
    items.add( new Item(label, value));
  }
  public void add( int labelResourceId, Object value ) {
    items.add( new Item(context, labelResourceId, value));
  }
  public void addText( String text ) {
    if ( text == null )
      text = "";
    items.add( text );
  }

  public Object getItem(int i) {
    return items.get(i);
  }
  
  class ItemAdapter extends ArrayAdapter<Object> {
    int normalColorId;
    int linkColorId;
    public ItemAdapter(int viewResourceId) {
      super(context, viewResourceId, items);
    }
    
    public void setNormalColorId(int normalColorId) {
      this.normalColorId = normalColorId;
    }

    public void setLinkColorId(int linkColorId) {
      this.linkColorId = linkColorId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
      TextView textView = (TextView) super.getView(position,convertView,parent);
      if ( normalColorId != 0 && linkColorId != 0) {
        Object p = items.get(position);
        if ( p instanceof Invokable ) {
          textView.setTextColor(context.getResources().getColor(linkColorId));
        } else {
          textView.setTextColor(context.getResources().getColor(normalColorId));
        }
      }
      return textView;
    }
  }
  public ArrayAdapter<Object> createAdapter(int listItemId) {
    return new ItemAdapter(listItemId);
  }
  public ArrayAdapter<Object> createAdapter(int listItemId, int normalColorId, int linkColorId) {
    ItemAdapter adapter = new ItemAdapter(listItemId);
    adapter.setNormalColorId(normalColorId);
    adapter.setLinkColorId(linkColorId);
    return adapter;
  }
}
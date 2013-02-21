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
package org.melato.android.util;

import android.content.Context;

// Taken from nosmoke.android
/** An item is a label and a value. */
public class LabeledValue {
  int   id;
  String label;
  Object value;
  
  public LabeledValue(Context context, int labelResourceId, Object value ) {
    this.id = labelResourceId;
    this.label = context.getResources().getString(labelResourceId);
    this.value = value;
  }
  public LabeledValue(String label, Object value) {
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
  public static String formatProperty( String label, Object value ) {
    String s = (value == null) ? "" : value.toString();
    if ( label != null && label.length() != 0 ) {
      s = label + ": " + s;
    }
    return s;
  }
  
}


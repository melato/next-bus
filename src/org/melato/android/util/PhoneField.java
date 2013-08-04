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
import android.content.Intent;
import android.net.Uri;

public class PhoneField implements Invokable {
  String label;
  String text;
  String phone;
  
  public PhoneField(String text) {
    this(null, text);
  }
  public PhoneField(String label, String text) {
    this.label = label;
    this.text = text;
    this.phone = parsePhone(text);
  }
  
  public boolean isValid() {
    return phone.length() >= 10;
  }
  
  public static String parsePhone(String text) {
    StringBuilder buf = new StringBuilder();
    for( char c: text.toCharArray()) {
      if ( Character.isDigit(c)) {
        buf.append(c);
      } else if ( " -.()+".indexOf(c) >= 0 ) {
          // ignore marks that can separate digits
      } else {
        break;
      }
    }
    return buf.toString();
  }

  public String getText() {
    return text;
  }

  public String getPhone() {
    return phone;
  }

  @Override
  public String toString() {
    if ( label != null) {
      return label + ": " + text;
    } else {
      return text;
    }
  }
  @Override
  public void invoke(Context context) {
    Uri uri = Uri.fromParts( "tel", getPhone(), null );
    Intent intent = new Intent(Intent.ACTION_DIAL, uri);
    context.startActivity(intent);       
  }
  
}

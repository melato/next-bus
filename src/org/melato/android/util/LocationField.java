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

import org.melato.gps.Point2D;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

// Taken from nosmoke.android
public class LocationField implements Invokable {
  protected String label;
  protected Point2D point;
  
  public LocationField(String label, Point2D point) {
    super();
    this.label = label;
    this.point = point;
  }

  @Override
  public String toString() {
    return label + ": " + point.toString();
  }

  @Override
  public void invoke(Context context) {
    Uri uri = Uri.parse("geo:" + point.getLat() + "," + point.getLon());
    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
    context.startActivity(intent);
  }    
}

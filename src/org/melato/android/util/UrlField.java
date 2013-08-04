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

public class UrlField implements Invokable {
  private String url;
  
  public UrlField(String url) {
    super();
    this.url = url;
  }
  
  public String getUrl() {
    return url;
  }

  @Override
  public String toString() {
    return url;
  }

  @Override
  public void invoke(Context context) {
    Uri uri = Uri.parse(getUrl());
    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
    context.startActivity(intent);   
  }    
}

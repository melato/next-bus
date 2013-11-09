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

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.melato.bus.android.Info;
import org.melato.bus.android.R;
import org.melato.bus.android.db.SqlRouteStorage;
import org.melato.progress.ProgressGenerator;
import org.melato.update.PortableUpdateManager;
import org.melato.update.UpdateFile;

import android.content.Context;

/** Checks for and/or downloads database updates. */
public class UpdateManager extends PortableUpdateManager {
  public static final String ROUTES_UPDATE = "ROUTES.zip";  
  public static final String ROUTES_ENTRY = "ROUTES.db";
  private Context context;
  
  public UpdateManager(Context context) {
    super();
    this.context = context;
    setIndexUrl(context.getResources().getString(R.string.update_url));
    setFilesDir(context.getFilesDir());
    if ( ! Info.isValidDatabase(context) ) {
      addMissingFile(ROUTES_UPDATE);
    }
  }
  
  public void update(List<UpdateFile> updates) throws IOException {
    ProgressGenerator progress = ProgressGenerator.get();
    for( UpdateFile f: updates ) {
      if ( ROUTES_UPDATE.equals(f.getName())) {
        File databaseFile = SqlRouteStorage.databaseFile(context);
        progress.setText(context.getString(R.string.routes_database));
        updateZipedFile(f, ROUTES_ENTRY, databaseFile);
        // delete the previous file, which may be at a different location
        // if it was created with app version <= 33 
        File oldFile = new File(context.getFilesDir(), SqlRouteStorage.DATABASE_NAME);
        if ( ! databaseFile.equals(oldFile)) {
          oldFile.delete();
        }        
        continue;
      }
    }
  }
}

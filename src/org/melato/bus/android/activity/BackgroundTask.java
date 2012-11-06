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
package org.melato.bus.android.activity;

import android.app.Activity;

public abstract class BackgroundTask implements Runnable {
  private Activity activity;
  
  public BackgroundTask(Activity activity) {
    this.activity = activity;
  }

  /** This is called in the UI thread when the background thread is done. */
  protected abstract void onForeground();

  public void run() {
    activity.runOnUiThread(new Runnable() {
      public void run() {
        onForeground();
      }
    });
  }
  public static void example(Activity activity) {
    
  }
}

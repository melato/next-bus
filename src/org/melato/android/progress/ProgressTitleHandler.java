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
package org.melato.android.progress;


import android.app.Activity;
import android.util.Log;
import android.view.Window;

/**
 * ProgressHandler that uses the built-in progress bar on an activity's title.
 * @author Alex Athanasopoulos
 *
 */
public class ProgressTitleHandler extends ActivityProgressHandler {
  private CharSequence activityTitle;
  private boolean replacedTitle;
  
  public ProgressTitleHandler(Activity activity) {
    super(activity);
    activity.requestWindowFeature(Window.FEATURE_PROGRESS);
  }
  
  /** Restore the activity's original title. */
  @Override
  public void end() {
    activity.setProgressBarVisibility(false);
    activity.setProgress(10000);
    if ( replacedTitle ) {
      activity.setTitle(activityTitle);
    }
  }
  @Override
  public void updateUI() {
    if ( limit == 0 || position >= limit - 1 || isCanceled() ) {      
      end();
    } else {
      activity.setProgressBarVisibility(true);
      if ( text != null ) {
        if ( ! replacedTitle ) {
          replacedTitle = true;
          activityTitle = activity.getTitle();
        }
        activity.setTitle(text);        
      }
      activity.setProgress(Math.round(position * 10000f / limit) );
    }
  }
}

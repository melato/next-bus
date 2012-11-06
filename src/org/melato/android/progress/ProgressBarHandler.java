/*-------------------------------------------------------------------------
 * Copyright (c) 2012, Alex Athanasopoulos.  All Rights Reserved.
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
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * ProgressHandler that is implemented with a ProgressBar and a TextView
 * @author Alex Athanasopoulos
 *
 */
public class ProgressBarHandler extends ActivityProgressHandler {
  private ProgressBar progressBar;
  private TextView status;
  private Button  cancelButton;

  public ProgressBarHandler(Activity activity, ProgressBar progressBar, TextView status) {
    super(activity);
    this.activity = activity;
    this.progressBar = progressBar;
    this.status = status;
  }

  public ProgressBarHandler(Activity activity, int progressBarId, int textViewId ) {
    super(activity);
    progressBar = (ProgressBar) activity.findViewById(progressBarId);
    status = (TextView) activity.findViewById(textViewId);
    Log.i( "melato.org", "progressBar: " + progressBar );
    Log.i( "melato.org", "status: " + status );
  }
  
  public void setCancelButton(Button button) {
    cancelButton = button;
    cancelButton.setOnClickListener(new OnClickListener() {
      public void onClick(View v) {
        cancel();
        v.setEnabled(false);
      }      
    });
  }
  public void setCancelButton(int buttonId) {
    Button button = (Button) activity.findViewById(buttonId);
    if ( button != null )
      setCancelButton(button);
  }
  

  @Override
  public void updateUI() {
    if ( status != null ) {
      status.setText( text );
      status.invalidate();
    }
    if ( progressBar != null ) {
      progressBar.setMax(limit);
      progressBar.setProgress(position);
      progressBar.invalidate();
    }
  }
}

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

import org.melato.progress.ProgressHandler;

import android.app.Activity;

/**
 * ProgressHandler that calls its updateUI method on an activity's ui thread.
 * It also controls the frequency of the updates so they are not too fast for the ui. 
 * @author Alex Athanasopoulos
 *
 */
public abstract class ActivityProgressHandler implements ProgressHandler {
  protected Activity activity;
  protected int   position;
  protected int   limit;
  protected long  updateTime;
  protected String text = "";
  protected boolean cancelled;
  protected int DELAY = 50;
  protected boolean busy;  
  
  private Runnable uiRunnable = new UIRunnable();

  class UIRunnable implements Runnable {

    @Override
    public void run() {
      busy = false;
      updateUI();
    }
    
  }
  public ActivityProgressHandler(Activity activity) {
    super();
    this.activity = activity;
  }

  public void cancel() {
    cancelled = true;
  }

  protected abstract void updateUI();
  
  private void update() {
    if ( busy )
      return;
    long now = System.currentTimeMillis();
    if ( now - updateTime < DELAY )
      return;
    if ( ! activity.hasWindowFocus() )
      return;
    updateTime = now;
    busy = true;
    activity.runOnUiThread(uiRunnable);
  }
  @Override
  public void setPosition(int pos) {
    this.position = pos;
    update();
  }

  @Override
  public void setLimit(int limit) {
    this.limit = limit;
    update();
  }
  
  @Override
  public void setText(String text) {
    this.text = text;    
    update();
  }

  @Override
  public boolean isCanceled() {
    return cancelled;
  }
}  

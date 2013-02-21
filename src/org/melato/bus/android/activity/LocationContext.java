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
package org.melato.bus.android.activity;

import org.melato.bus.android.Info;
import org.melato.bus.client.TrackHistory;
import org.melato.gps.PointTime;
import org.melato.gps.PointTimeListener;

import android.content.Context;

/**
 * A location listener that attaches itself to an activity and maintains a current location.
 * Subclass and override setLocation() to do something with the locations.
 * The activity must call close() from its onDestroy() method
 * to remove the listener from the LocationManager.
 * @author Alex Athanasopoulos
 */
public class LocationContext implements PointTimeListener {
  protected Context context;
  protected TrackHistory history;

  public LocationContext(Context context) {
    super();
    this.context = context;
    history = Info.trackHistory(context);
  }
 
  public void start() {
    history.addLocationListener(this);
  }
  /** remove location updates. */
  public void close() {
    history.removeLocationListener(this);
  }

  public PointTime getLocation() {
    return history.getLocation();
  }
  
  @Override
  public void setLocation(PointTime point) {
  }
}
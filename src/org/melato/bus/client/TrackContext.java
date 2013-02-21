/*-------------------------------------------------------------------------
 * Copyright (c) 2012,2013, Alex Athanasopoulos.  All Rights Reserved.
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
package org.melato.bus.client;

import org.melato.bus.model.Stop;
import org.melato.geometry.gpx.Path;
import org.melato.geometry.gpx.PathTracker;
import org.melato.gps.Metric;
import org.melato.gps.PointTime;
import org.melato.gps.PointTimeListener;

/** Keeps all the pieces needed to keep track of where we are on a route. */
public class TrackContext implements PointTimeListener {
  private Metric metric;
  private Stop[] stops;
  private Path path;
  private PathTracker pathTracker;

  public TrackContext(Metric metric) {
    this.metric = metric;
  }
  
  public void setStops(Stop[] stops) {
    this.stops = stops;
    path = new Path(metric);
    path.setWaypoints(stops);
    pathTracker = new PathTracker();
    pathTracker.setPath(path);
  }
  
  @Override
  public void setLocation(PointTime point) {
    if ( point != null) {
      pathTracker.setLocation(point);
    }
  }
  
  public Stop[] getStops() {
    return stops;
  }

  public Path getPath() {
    return path;
  }

  public PathTracker getPathTracker() {
    return pathTracker;
  }
}

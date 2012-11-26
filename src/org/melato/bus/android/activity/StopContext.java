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

import java.util.Date;

import org.melato.android.ui.PropertiesDisplay;
import org.melato.bus.android.R;
import org.melato.bus.model.Schedule;
import org.melato.bus.model.Stop;
import org.melato.geometry.gpx.PathTracker;
import org.melato.geometry.gpx.SpeedTracker;
import org.melato.gps.Earth;
import org.melato.gps.PointTime;
import org.melato.gpx.util.Path;

import android.content.Context;
import android.widget.ArrayAdapter;

public class StopContext extends LocationContext {
  public static final float WALK_OVERHEAD = 1.3f;
  public static final float WALK_SPEED = 5f;
  public static final float BIKE_OVERHEAD = 1.35f;
  public static final float BIKE_SPEED = 15f;

  private Stop[] waypoints;
  private int markerIndex;
  private Stop marker;
  private int timeFromStart = -1;

  private Path path;
  private PathTracker pathTracker;
  private SpeedTracker speed;
  private PropertiesDisplay properties;
  private ArrayAdapter<Object> adapter;
  
  private float straightDistance;


  public float getStraightDistance() {
    return straightDistance;
  }

  public Stop getMarker() {
    return marker;
  }
  
  
  public int getMarkerIndex() {
    return markerIndex;
  }

  public PathTracker getPathTracker() {
    return pathTracker;
  }

  public float getMarkerPosition() {
    return path.getLength(markerIndex);
  }

  public float getRouteDistance() {
    return path.getLength(markerIndex) - pathTracker.getPosition();
  }
  
  public void refresh() {
    if ( adapter != null )
      adapter.notifyDataSetChanged();
  }
  
  @Override
  public void setLocation(PointTime point) {
    super.setLocation(point);
    if ( point == null )
      return;
    straightDistance = Earth.distance(point, marker);
    pathTracker.setLocation(point);
    speed.compute();
    refresh();
  }

  public StopContext(Context context) {
    super(context);
    properties = new PropertiesDisplay(context);
    addProperties();
  }

  
  public PropertiesDisplay getProperties() {
    return properties;
  }

  public ArrayAdapter<Object> createAdapter(int listItemId) {
    adapter = properties.createAdapter(listItemId);
    return adapter;
  }

  public int getTimeFromStart() {
    if ( timeFromStart == -1 ) {
      timeFromStart = new RouteStop(null, null, markerIndex).getTimeFromStart(waypoints);
    }
    return timeFromStart;    
  }
  public void setWaypoints(Stop[] waypoints) {
    this.waypoints = waypoints;
    this.path = new Path(waypoints);
    pathTracker = new PathTracker();
    pathTracker.setPath(path);
    speed = new SpeedTracker(pathTracker);
    timeFromStart = -1;
  }
  
  public void setMarkerIndex(int index) {
    markerIndex = index;
    marker = waypoints[index];
    setEnabledLocations(true);
    timeFromStart = -1;
  }

  class StraightDistance {
    public String toString() {
      return properties.formatProperty( R.string.straight_distance, UI.straightDistance(getStraightDistance()));
    }
  }
  
  class RouteDistance {
    public String toString() {
      return properties.formatProperty( R.string.route_distance, UI.routeDistance(getRouteDistance()));
    }
  }
  
  class DistanceFromStart {
    public String toString() {
      String name = waypoints[0].getName();
      String label = String.format(context.getString(R.string.position_from_start), name);
      return PropertiesDisplay.formatProperty( label, UI.routeDistance(getMarkerPosition()));
    }
  }
  
  class TimeFromStart {
    public String toString() {
      String name = waypoints[0].getName();
      String label = String.format(context.getString(R.string.time_from_start), name);
      int seconds = getTimeFromStart();
      String value = seconds > 0 ? Schedule.formatTime(seconds/60) : "";
      return PropertiesDisplay.formatProperty( label, value);
    }
  }
  
  class Latitude {
    public String toString() {
      return properties.formatProperty( R.string.latitude, UI.degrees(getMarker().getLat()));
    }
  }
  
  class Longitude{
    public String toString() {
      return properties.formatProperty( R.string.longitude, UI.degrees(getMarker().getLon()));
    }
  }

  float getSpeed() {
    float speed = this.speed.getSpeed();
    if ( speed > 0.3f ) {
      // don't show speeds smaller than 0.3 m/s (about 1 Km/h)
      return speed;
    }
    return Float.NaN;
  }
  
  class PathSpeed {
    public String toString() {
      String label = context.getResources().getString(R.string.speed);
      String value = "";
      float speed = getSpeed() * 3600f/1000f;
      if ( ! Float.isNaN(speed)) {
        value = String.valueOf(Math.round(speed)) + " Km/h";
      }
      return PropertiesDisplay.formatProperty( label, value);
    }
  }
  
  class PathETA {
    public String toString() {
      String label = context.getResources().getString(R.string.ETA);
      String value = "";
      float speed = getSpeed();
      if ( ! Float.isNaN(speed)) {
        float time = StopContext.this.speed.getRemainingTime(getMarkerIndex());
        value = formatTime(time);
      }
      return PropertiesDisplay.formatProperty( label, value);
    }
  }
  
  class StraightETA {
    int labelId;
    float speed;
    float overhead;
    
    public StraightETA(int labelId, float speed, float overhead) {
      super();
      this.labelId = labelId;
      this.speed = speed;
      this.overhead = overhead;
    }

    public String toString() {
      String label = context.getResources().getString(labelId);
      float time = getStraightDistance() / (speed *1000/3600);
      return PropertiesDisplay.formatProperty( label, formatTime(time));
    }
  }
  
  String formatTime( float secondsFromNow ) {
    Date eta = new Date(System.currentTimeMillis() + (int) (secondsFromNow*1000));
    int minutes = Math.round(secondsFromNow/60);
    String sign = "";
    if ( minutes < 0 ) {
      // we may have negative times, such as the time to a previous stop.
      minutes = -minutes;
      sign = "-";
    }
      
    return Schedule.formatTime(Schedule.getTime(eta)) +
        " (" + sign + Schedule.formatTime(minutes) + ")";
    
  }
  
  
  public void addProperties() {
    properties.add(new StraightDistance());
    properties.add(new RouteDistance());
    properties.add(new DistanceFromStart());
    properties.add(new TimeFromStart());      

    properties.add( new PathSpeed());
    properties.add( new PathETA());
    properties.add(new StraightETA(R.string.walkETA, WALK_SPEED, WALK_OVERHEAD));
    properties.add(new Latitude());
    properties.add(new Longitude());    
    // properties.add(new StraightETA(R.string.bikeETA, BIKE_SPEED, BIKE_OVERHEAD));
  }
}

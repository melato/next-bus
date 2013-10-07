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

import java.util.Date;

import org.melato.android.ui.PropertiesDisplay;
import org.melato.android.ui.PropertiesDisplay.Item;
import org.melato.android.util.Invokable;
import org.melato.android.util.LabeledPoint;
import org.melato.android.util.LocationField;
import org.melato.bus.android.Info;
import org.melato.bus.android.R;
import org.melato.bus.client.Formatting;
import org.melato.bus.client.TrackContext;
import org.melato.bus.model.Municipality;
import org.melato.bus.model.Route;
import org.melato.bus.model.Schedule;
import org.melato.bus.model.Stop;
import org.melato.bus.model.StopCount;
import org.melato.bus.plan.WalkModel;
import org.melato.geometry.gpx.PathTracker;
import org.melato.geometry.gpx.SpeedTracker;
import org.melato.gps.Earth;
import org.melato.gps.PointTime;

import android.content.Context;
import android.widget.ArrayAdapter;

public class StopContext extends LocationContext {
  public static final float BIKE_OVERHEAD = 1.35f;
  public static final float BIKE_SPEED = 15f;
  public static final float MIN_SPEED = 1f / (3600f / 1000f); // 1 Km/h

  private TrackContext track;
  private SpeedTracker speed;
  private int markerIndex;
  private Stop marker;
  private StopCount previousStops;
  private StopCount followingStops;
  private int timeFromStart = -1;

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
    return track.getPathTracker();
  }

  public float getMarkerPosition() {
    return track.getPath().getLength(markerIndex);
  }

  public float getRouteDistance() {
    return track.getPath().getLength(markerIndex)
        - track.getPathTracker().getPosition();
  }

  public void refresh() {
    if (adapter != null)
      adapter.notifyDataSetChanged();
  }

  @Override
  public void setLocation(PointTime point) {
    super.setLocation(point);
    if (point == null)
      return;
    straightDistance = Earth.distance(point, marker);
    track.setLocation(point);
    speed.compute();
    refresh();
  }

  public StopContext(Context context) {
    super(context);
    properties = new PropertiesDisplay(context);
  }

  public PropertiesDisplay getProperties() {
    return properties;
  }

  public ArrayAdapter<Object> createAdapter(int listItemId) {
    adapter = properties.createAdapter(listItemId, R.color.black, R.color.stop_link);
    return adapter;
  }

  public int getTimeFromStart() {
    if (timeFromStart == -1) {
      if ( markerIndex >= 0 ) {
        Stop[] stops = track.getStops();
        timeFromStart = stops[markerIndex].getSecondsFromStart();
      } else {
        timeFromStart = 0;
      }
    }
    return timeFromStart;
  }

  public void setRoute(Route route) {
    history.setRoute(route.getRouteId());
    track = history.getTrackContext();
    speed = history.getSpeedTracker();
    timeFromStart = -1;
  }
  
  
  public void setMarkerIndex(int index) {
    markerIndex = index;
    Stop[] stops = track.getStops();
    marker = stops[index];
    previousStops = new StopCount(stops, 1, index + 1); // don't include the start stop.
    followingStops = new StopCount(stops, index + 1, stops.length);
    timeFromStart = -1;
    setLocation(history.getLocation());
    start();
  }

  public class MunicipalityField implements Invokable {
    Municipality m;
    
    public MunicipalityField(Municipality municipality) {
      super();
      this.m = municipality;
    }

    @Override
    public String toString() {
      return properties.formatProperty(R.string.municipality, m.getName());
    }

    @Override
    public void invoke(Context context) {
      MunicipalityActivity.start(StopContext.this.context, m);
    }    
  }
  
  class StraightDistance {
    public String toString() {
      return properties.formatProperty(R.string.straight_distance,
          Formatting.straightDistance(getStraightDistance()));
    }
  }

  class Bearing {
    public String toString() {
      String bearing = "";
      float travelBearing = history.getBearing();
      if (!Float.isNaN(travelBearing)) {
        float markerBearing = Earth.bearing(getLocation(), marker);
        int turn = Math.round(Formatting.normalizeBearing(markerBearing - travelBearing));
        if ( turn < 0 ) {
          bearing = context.getString(R.string.bearing_left, -turn );
        } else if ( turn > 0 ) {
          bearing = context.getString(R.string.bearing_right, turn );
        } else {
          bearing = context.getString(R.string.bearing_straight);
        }
      }
      return properties.formatProperty(R.string.bearing, bearing);
    }
  }

  class RouteDistance {
    public String toString() {
      return properties.formatProperty(R.string.route_distance,
          Formatting.routeDistance(getRouteDistance()));
    }
  }

  class DistanceFromStart {
    public String toString() {
      String name = track.getStops()[0].getName();
      String label = String.format(
          context.getString(R.string.position_from_start), name);
      return PropertiesDisplay.formatProperty(label,
          Formatting.routeDistance(getMarkerPosition()));
    }
  }

  class TimeFromStart {
    public String toString() {
      String name = track.getStops()[0].getName();
      String label = String.format(context.getString(R.string.time_from_start),
          name);
      int seconds = getTimeFromStart();
      String value = seconds > 0 ? Schedule.formatTime(seconds / 60) : "";
      return PropertiesDisplay.formatProperty(label, value);
    }
  }

  private float getPathSpeed() {
    float speed = this.speed.getSpeed();
    if (speed < MIN_SPEED) {
      return Float.NaN;
    }
    return speed;
  }

  class PathSpeed {
    public String toString() {
      String label = context.getResources().getString(R.string.speed);
      String value = "";
      float speed = getPathSpeed() * 3600f / 1000f;
      if (!Float.isNaN(speed)) {
        value = String.valueOf(Math.round(speed)) + " Km/h";
      }
      return PropertiesDisplay.formatProperty(label, value);
    }
  }

  class GpsMode {
    public String toString() {
      String label = context.getResources().getString(R.string.gps_speed);
      int resourceId = Info.trackHistory(context).isFast() ? R.string.gps_fast : R.string.gps_normal;
      String value = context.getResources().getString(resourceId);
      return PropertiesDisplay.formatProperty(label, value);
    }
  }

  class Speed60 {
    public String toString() {
      String label = context.getResources().getString(R.string.speed);
      String value = "";
      float speed = history.getSpeed60().getSpeed();
      if (speed < MIN_SPEED) {
        speed = Float.NaN;
      }
      if (!Float.isNaN(speed)) {
        value = String.valueOf(Math.round(speed * 3600f / 1000f)) + " Km/h";
      }
      return PropertiesDisplay.formatProperty(label, value);
    }
  }

  class PathETA {
    public String toString() {
      String label = context.getResources().getString(R.string.ETA);
      String value = "";
      float speed = getPathSpeed();
      if (!Float.isNaN(speed)) {
        float time = StopContext.this.speed.getRemainingTime(getMarkerIndex());
        value = formatTime(time);
      }
      return PropertiesDisplay.formatProperty(label, value);
    }
  }

  class StraightETA {
    int labelId;
    WalkModel walkModel;

    public StraightETA(int labelId, WalkModel walkModel ) {
      super();
      this.labelId = labelId;
      this.walkModel = walkModel;
    }

    public String toString() {
      String label = context.getResources().getString(labelId);
      float time = walkModel.duration(getStraightDistance());
      return PropertiesDisplay.formatProperty(label, formatTime(time));
    }
  }

  String formatTime(float secondsFromNow) {
    Date eta = new Date(System.currentTimeMillis()
        + (int) (secondsFromNow * 1000));
    int minutes = Math.round(secondsFromNow / 60);
    String sign = "";
    if (minutes < 0) {
      // we may have negative times, such as the time to a previous stop.
      minutes = -minutes;
      sign = "-";
    }

    return Schedule.formatTime(Schedule.getTime(eta)) + " (" + sign
        + Schedule.formatTime(minutes) + ")";

  }

  public void addProperties() {
    Stop stop = getMarker();
    Municipality municipality = Info.routeManager(context).getMunicipality(stop);
    if ( municipality != null) {
      if ( municipality.hasDetails() ) {
        properties.add(new MunicipalityField(municipality));
      } else {
        properties.add(new Item(context, R.string.municipality, municipality.getName()));
      }
    }
    properties.add(new StraightDistance());
    properties.add(new Bearing());
    properties.add(new GpsMode());
    properties.add(new RouteDistance());
    properties.add(new DistanceFromStart());
    properties.add(new TimeFromStart());
    properties.add(R.string.untimed_stops, context.getString(R.string.stop_counts,
        previousStops.missingRatio(),
        followingStops.missingRatio()));

    // properties.add( new PathSpeed());
    //properties.add(new Speed60());
    //properties.add(new PathETA());
    properties
        .add(new StraightETA(R.string.walkETA, Info.walkModel(context)));
    //properties.add(new LatitudeField(context.getString(R.string.latitude), getMarker()));
    //properties.add(new LongitudeField(context.getString(R.string.longitude), getMarker()));
    LabeledPoint p = new LabeledPoint(stop, stop.getName());
    properties.add(new LocationField(context.getString(R.string.coordinates), p));
    // properties.add(new StraightETA(R.string.bikeETA, BIKE_SPEED,
    // BIKE_OVERHEAD));
  }
}

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
package org.melato.bus.android.db;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.melato.bus.model.DaySchedule;
import org.melato.bus.model.Marker;
import org.melato.bus.model.MarkerInfo;
import org.melato.bus.model.Route;
import org.melato.bus.model.RouteId;
import org.melato.bus.model.RouteStopCallback;
import org.melato.bus.model.RouteStorage;
import org.melato.bus.model.Schedule;
import org.melato.bus.model.ScheduleId;
import org.melato.bus.model.Stop;
import org.melato.gps.Point2D;
import org.melato.progress.ProgressGenerator;
import org.melato.util.IntArrays;
import org.melato.util.VariableSubstitution;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

public class SqlRouteStorage implements RouteStorage {
  public static final String DATABASE_NAME = "ROUTES.db";
  private String databaseFile;
  private Map<String,String> properties;
  private int version;
  public static final int VERSION_HOLIDAYS = 2;
  public static final String PROPERTY_VERSION = "version";
  public static final String PROPERTY_LAT = "center_lat";
  public static final String PROPERTY_LON = "center_lon";
  public static final String PROPERTY_DAY_CHANGE = "day_change";
  
  private Map<String,String> loadProperties() {
    SQLiteDatabase db = getDatabase();
    try {
      String sql = "select name, value from properties";
      Cursor cursor = db.rawQuery(sql, null);
      Map<String,String> properties = new HashMap<String,String>();
      try {
        if ( cursor.moveToFirst() ) {
          do {
            properties.put( cursor.getString(0), cursor.getString(1));          
          } while( cursor.moveToNext());
        }
        return properties;
      } finally {
        cursor.close();
      }
    } finally {
      db.close();
    }
  }
  
  public String getProperty( String name, String defaultValue) {
    if ( properties == null ) {
      properties = loadProperties();
    }
    String value = properties.get(name);
    if ( value == null ) {
      value = defaultValue;
    }
    return value;
  }
  
  public String getProperty( String name) {
    return getProperty(name, null);
  }
  
  public int getVersion() {
    if ( version == 0 )
      version = Integer.parseInt(getProperty(PROPERTY_VERSION, "1"));
    return version;
  }

  @Override
  public Point2D getCenter() {
    String s_lat = getProperty(PROPERTY_LAT);
    String s_lon = getProperty(PROPERTY_LON);
    if ( s_lat != null && s_lon != null ) {
      return new Point2D( Float.parseFloat(s_lat), Float.parseFloat(s_lon));
    }
    return null;
  }    
  
  public int getDayChange() {
    String time = getProperty(PROPERTY_DAY_CHANGE);
    if ( time == null )
      return 4*60;
    return Integer.parseInt(time);
  }

  @Override
  public String getUri(RouteId routeId) {
    String urlTemplate = getProperty( "route_url");
    if ( urlTemplate != null ) {
      VariableSubstitution sub = new VariableSubstitution(VariableSubstitution.ANT_PATTERN);
      Map<String,String> vars = new HashMap<String,String>();
      vars.put( "name", routeId.getName());
      vars.put( "direction", routeId.getDirection());
      return sub.substitute(urlTemplate, vars);
    }
    return null;
  }
  public static File databaseFile(Context context) {
    File dir = context.getFilesDir();
    return new File(dir, DATABASE_NAME);    
  }
  public SqlRouteStorage(Context context) {
    databaseFile = databaseFile(context).toString();
  }
  public SqlRouteStorage(File databaseFile) {
    this.databaseFile = databaseFile.toString();
  }

  SQLiteDatabase getDatabase() {    
    try {
      return SQLiteDatabase.openDatabase(databaseFile,
          null, SQLiteDatabase.OPEN_READONLY);
    } catch (SQLiteException e) {
      String message = e.getMessage();
      if ( message != null && message.contains("attempt to write a readonly database")) {
        // see http://metakinisi.melato.org/node/554
        SQLiteDatabase database = SQLiteDatabase.openDatabase(databaseFile, null, SQLiteDatabase.OPEN_READWRITE);
        database.close();
        return SQLiteDatabase.openDatabase(databaseFile,
            null, SQLiteDatabase.OPEN_READONLY);
      }
      throw e;
    }
  }

  static private final String ROUTE_SELECT = "select routes.name, routes.label, routes.title, routes.direction," +
      " routes.color, routes.background_color," +
      " is_primary," +
      " routes._id from routes";
  
  private List<Route> loadRoutes(String where) {
    List<Route> routes = new ArrayList<Route>();
    SQLiteDatabase db = getDatabase();
    String sql = ROUTE_SELECT;
    if ( where != null ) {
      sql += " where " + where;
    }
    sql += " order by _id";
    Cursor cursor = db.rawQuery(sql, null);
    if ( cursor.moveToFirst() ) {
      do {
        routes.add(readBasic(cursor));
      } while( cursor.moveToNext() );
    }
    cursor.close();
    db.close();
    return routes;
  }

  @Override
  public List<Route> loadRoutes() {
    return loadRoutes(null);
  }

  @Override
  public List<Route> loadPrimaryRoutes() {
    return loadRoutes("routes.is_primary = 1");
  }

  private Route readBasic(Cursor cursor) {
    Route route = new Route();
    RouteId routeId = new RouteId(cursor.getString(0), cursor.getString(3));
    route.setRouteId(routeId);
    route.setLabel(cursor.getString(1));
    route.setTitle(cursor.getString(2));
    route.setColor(cursor.getInt(4));
    route.setBackgroundColor(cursor.getInt(5));
    if ( ! cursor.isNull(6)) {
      int primary = cursor.getInt(6);
      if ( primary == 1 )
        route.setPrimary(true);      
    }
    return route;
  }

  private Route loadBasic(Cursor cursor) {
    try {
      if ( cursor.moveToFirst() ) {
        return readBasic(cursor);
      }
      return null;
    } finally {
      cursor.close();
    }
  }
  
  private Route loadBasic(SQLiteDatabase db, RouteId routeId) {    
    String sql = ROUTE_SELECT + " where " + whereClause(routeId);
    Cursor cursor = db.rawQuery( sql, null);
    return loadBasic(cursor);
  }  
  
  private Schedule loadSchedule(SQLiteDatabase db, RouteId routeId) {
    String sql = "select schedules._id, days, minutes from schedule_times" +
        "\njoin schedules on schedules._id = schedule_times.schedule" +
        "\njoin routes on routes._id = schedules.route" +
        "\nwhere " + whereClause(routeId) +
        "\norder by schedules._id, minutes";
    Cursor cursor = db.rawQuery( sql, null);
    List<DaySchedule> daySchedules = new ArrayList<DaySchedule>();
    Map<Integer,DaySchedule> scheduleIds = new HashMap<Integer,DaySchedule>();
    try {
      if ( cursor.moveToFirst() ) {
        int lastScheduleId = 0;
        int lastDays = 0;
        List<Integer> times = new ArrayList<Integer>();
        do {
          int scheduleId = cursor.getInt(0);
          int days = cursor.getInt(1);
          int minutes = cursor.getInt(2);
          if ( scheduleId != lastScheduleId ) {
            if ( ! times.isEmpty() ) {
              DaySchedule daySchedule = new DaySchedule(IntArrays.toArray(times), ScheduleId.forWeek(lastDays));
              scheduleIds.put(lastScheduleId, daySchedule);
              if ( lastDays != 0 ) {
                daySchedules.add( daySchedule );
              }
              times.clear();
            }
            lastScheduleId = scheduleId;
            lastDays = days;
          }
          times.add(minutes);
        } while ( cursor.moveToNext() );
        if ( ! times.isEmpty() ) {
          DaySchedule daySchedule = new DaySchedule(IntArrays.toArray(times), ScheduleId.forWeek(lastDays));
          scheduleIds.put(lastScheduleId, daySchedule);
          if ( lastDays != 0 ) {
            daySchedules.add( daySchedule );
          }
        }
      }
    } finally {
      cursor.close();
    }    
    if ( getVersion() >= VERSION_HOLIDAYS ) {
      sql = "select date_id, schedule from schedule_exceptions" +
          "\njoin routes on routes._id = schedule_exceptions.route" +
          "\nwhere " + whereClause(routeId) +
          "\norder by date_id";
      cursor = db.rawQuery( sql, null);
      try {
        if ( cursor.moveToFirst() ) {
          do {
            int dateId = cursor.getInt(0);
            int scheduleId = cursor.getInt(1);
            DaySchedule daySchedule = scheduleIds.get(scheduleId);
            daySchedules.add(new DaySchedule(daySchedule.getTimes(), ScheduleId.forDate(dateId)));
          } while ( cursor.moveToNext() );
        }
      } finally {
        cursor.close();
      }
    }
    Schedule schedule = new Schedule(daySchedules.toArray(new DaySchedule[0]));
    schedule.setDayChange(getDayChange());
    return schedule;
  }

  public Schedule loadSchedule(RouteId routeId) {
    SQLiteDatabase db = getDatabase();
    try {
      Schedule schedule = loadSchedule(db, routeId);
      schedule.setComment(loadScheduleComment(db, routeId));
      return schedule;
    } finally {
      db.close();
    }
  }
  
  @Override
  public Route loadRoute(RouteId routeId) {
    SQLiteDatabase db = getDatabase();
    try {
      Route route = loadBasic(db, routeId);
      return route;
    } finally {
      db.close();
    }
  }
  
  public String loadScheduleComment(SQLiteDatabase db, RouteId routeId) {
    String sql = "select schedule_comment from routes where " + whereClause(routeId);
    Cursor cursor = db.rawQuery(sql,  null);
    try {
      if ( cursor.moveToFirst()) {
        return cursor.getString(0);
      }
      return null;
    } finally {
      cursor.close();
    }
  }
  
  protected String quote(String s) {
    if ( s.indexOf('\'') < 0 )
      return s;
    return s.replaceAll( "'", "''" );
  }
  
  private String format(String sql, RouteId routeId ) {
    return String.format(Locale.US, sql, quote(routeId.getName()), quote(routeId.getDirection()));
  }
  private String whereClause(RouteId routeId) {
    return format("routes.name = '%s' and routes.direction = '%s'", routeId);    
  }
  
  /** use for benchmarking */
  public void iterateWaypoints(RouteId routeId) {
    SQLiteDatabase db = getDatabase();
    String sql = "select lat, lon, stops.seq from markers" +
        "\njoin stops on markers._id = stops.marker" +
        "\njoin routes on routes._id = stops.route" +
        "\nwhere " + whereClause(routeId) + 
        "\norder by stops.seq";
    Cursor cursor = db.rawQuery( sql, null);
    try {      
      if ( cursor.moveToFirst() ) {
        do {
          cursor.getFloat(0);
          cursor.getFloat(1);
        } while ( cursor.moveToNext() );
      }
    } finally {
      cursor.close();
      db.close();
    }
  }

  @Override
  public List<Stop> loadStops(RouteId routeId) {
    SQLiteDatabase db = getDatabase();
    String sql = "select lat, lon, markers.symbol, markers.name, stops.duration from markers" +
        "\njoin stops on markers._id = stops.marker" +
        "\njoin routes on routes._id = stops.route" +
        "\nwhere " + whereClause(routeId) + 
        "\norder by stops._id";
    Cursor cursor = db.rawQuery( sql, null);
    try {
      List<Stop> stops = new ArrayList<Stop>();
      if ( cursor.moveToFirst() ) {
        do {
          Stop p = new Stop(cursor.getFloat(0), cursor.getFloat(1));
          p.setSymbol(cursor.getString(2));
          p.setName(cursor.getString(3));
          p.setTime(1000L * cursor.getInt(4));
          stops.add(p);
        } while ( cursor.moveToNext() );
      }
      return stops;
    } finally {
      cursor.close();
      db.close();
    }
  }

  private void iterateRouteStops(String where, RouteStopCallback callback) {
    //Clock clock = new Clock();
    String sql = "select lat, lon, routes._id, routes.name, routes.direction from markers" +
        "\njoin stops on markers._id = stops.marker" +
        "\njoin routes on routes._id = stops.route " +
        where +
        "\norder by routes._id, stops.seq";
    
    SQLiteDatabase db = getDatabase();
    Cursor cursor = db.rawQuery( sql, null);
    ProgressGenerator progress = ProgressGenerator.get();
    int count = 0;
    try {
      int last_route_id = -1;
      RouteId routeId = null;
      List<Point2D> waypoints = null;
      if ( cursor.moveToFirst() ) {
        //Log.info( clock.lap( "all.RouteStops.moveToFirst"));
        do {
          Point2D p = new Point2D(cursor.getFloat(0), cursor.getFloat(1));
          int route_id = cursor.getInt(2);
          if ( route_id != last_route_id ) {
            if ( routeId != null) {
              callback.add(routeId, waypoints );
              progress.setPosition(count++);
            }
            last_route_id = route_id;
            routeId = new RouteId(cursor.getString(3), cursor.getString(4));
            waypoints = new ArrayList<Point2D>();
          }
          waypoints.add(p);
        } while ( cursor.moveToNext() );
        if ( routeId != null) {
          callback.add(routeId, waypoints );
        }
      }
      //Log.info( clock.lap( "all.RouteStops.cursor"));
    } finally {
      cursor.close();
      db.close();
    }
  }

  @Override
  public void iterateAllRouteStops(RouteStopCallback callback) {
    iterateRouteStops("", callback);
  }

  @Override
  public void iteratePrimaryRouteStops(RouteStopCallback callback) {
    iterateRouteStops("where routes.is_primary = 1", callback);
  }

  @Override
  public void iterateNearbyStops(Point2D point, float latDiff, float lonDiff,
      Collection<Marker> collector) {
    float lat1 = point.getLat() - latDiff;
    float lat2 = point.getLat() + latDiff;
    float lon1 = point.getLon() - lonDiff;
    float lon2 = point.getLon() + lonDiff;
    SQLiteDatabase db = getDatabase();
    String sql = "select lat, lon, markers.symbol, markers.name, routes.name, routes.direction, markers._id from markers" +
        "\njoin stops on markers._id = stops.marker" +
        "\njoin routes on routes._id = stops.route" +
        "\nwhere lat > %f and lat < %f and lon > %f and lon < %f" +
        "\norder by markers._id";
    Cursor cursor = db.rawQuery(
        String.format( Locale.US, sql, lat1, lat2, lon1, lon2),
        null);
    //Clock clock = new Clock("sql.iterateNearbyStops");
    if ( cursor.moveToFirst() ) {
      int lastMarkerId = -1;
      List<RouteId> routes = new ArrayList<RouteId>();
      Marker marker = null;
      do {
        int markerId = cursor.getInt(6);
        if ( markerId != lastMarkerId) {
          lastMarkerId = markerId;
          if ( marker != null ) {
            marker.setRoutes(routes.toArray(new RouteId[0]));
            collector.add(marker);
            marker = null;
          }
        }
        if ( marker == null ) {
          marker = new Marker(cursor.getFloat(0), cursor.getFloat(1));
          // can check the filter here.
          /*
          if ( Earth.distance(point,  p) > distance ) {
            p = null;
            continue;
          } 
          */         
          marker.setSymbol(cursor.getString(2));
          marker.setName(cursor.getString(3));
          routes.clear();
        }
        String routeName = cursor.getString(4);
        String direction = cursor.getString(5);
        routes.add(new RouteId(routeName, direction));
      } while ( cursor.moveToNext() );
      if ( marker != null ) {
        marker.setRoutes(routes.toArray(new RouteId[0]));
        collector.add(marker);
      }
    }
    cursor.close();
    db.close();
    //Log.info(clock);    
  }

  @Override
  public void iterateNearbyRoutes(Point2D point, float latDiff, float lonDiff,
      Collection<RouteId> collector) {
    float lat1 = point.getLat() - latDiff;
    float lat2 = point.getLat() + latDiff;
    float lon1 = point.getLon() - lonDiff;
    float lon2 = point.getLon() + lonDiff;
    SQLiteDatabase db = getDatabase();
    String sql = "select distinct routes.name, routes.direction from markers" +
        "\njoin stops on markers._id = stops.marker" +
        "\njoin routes on routes._id = stops.route" +
        "\nwhere lat > %f and lat < %f and lon > %f and lon < %f";
    Cursor cursor = db.rawQuery(
        String.format( Locale.US, sql, lat1, lat2, lon1, lon2),
        null);
    //Clock clock = new Clock("sql.iterateNearbyRoutes");
    if ( cursor.moveToFirst() ) {
      do {
        RouteId routeId = new RouteId( cursor.getString(0), cursor.getString(1));
        collector.add(routeId);
      } while ( cursor.moveToNext() );
    }
    cursor.close();
    db.close();
    //Log.info(clock);    
  }

  private List<Route> loadRoutesForMarker(SQLiteDatabase db, String symbol) {    
    List<Route> routes = new ArrayList<Route>();
    String sql = ROUTE_SELECT +
        "\njoin stops on routes._id = stops.route" +
        "\njoin markers on markers._id = stops.marker" +
        "\nwhere markers.symbol = '%s'";
    Cursor cursor = db.rawQuery(
        String.format( Locale.US, sql, quote(symbol)),
        null);
    try {
      Set<Integer> set = new HashSet<Integer>();
      if ( cursor.moveToFirst() ) {
        do {
          int id = cursor.getInt(4);
          if ( set.add(id)) { // skip duplicates.
            Route route = readBasic(cursor);
            routes.add(route);
          }
        } while(cursor.moveToNext());      
      }
    } finally {
      cursor.close();
    }
    return routes;
  }
  
  private Stop loadStop(SQLiteDatabase db, String symbol) {
    String sql = "select lat, lon, symbol, name, _id from markers" +
        "\nwhere symbol = '%s'";
    Cursor cursor = db.rawQuery(
        String.format(Locale.US, sql, quote(symbol)), null);
    try {
      if ( cursor.moveToFirst() ) {
        Stop p = new Stop(cursor.getFloat(0), cursor.getFloat(1));
        p.setSymbol(cursor.getString(2));
        p.setName(cursor.getString(3));
        return p;
      }
      return null;
    } finally {
      cursor.close();
    }
  }
  
  @Override
  public MarkerInfo loadMarker(String symbol) {
    SQLiteDatabase db = getDatabase();
    try {
      Stop waypoint = loadStop(db, symbol);
      if ( waypoint == null )
        return null;
      List<Route> routes = loadRoutesForMarker(db, symbol);
      return new MarkerInfo(waypoint, routes);      
    } finally {
      db.close();
    }
  }
}

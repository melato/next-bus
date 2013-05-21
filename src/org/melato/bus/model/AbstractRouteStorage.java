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
package org.melato.bus.model;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.melato.gps.Point2D;

/**
 * Provides dummy implementations of non-essential RouteStorage methods.
 * @author Alex Athanasopoulos
 */
public abstract class AbstractRouteStorage implements RouteStorage {


  public static List<RouteId> extractRouteIds(List<Route> routes) {
    RouteId[] routeIds = new RouteId[routes.size()];
    for( int i = 0; i < routeIds.length; i++ ) {
      routeIds[i] = routes.get(i).getRouteId();
    }
    return Arrays.asList(routeIds);
  }
  
  @Override
  public List<RouteId> loadRouteIds() {
    List<Route> routes = loadRoutes();
    return extractRouteIds(routes);
  }
  
  @Override
  public String getUri(RouteId routeId) {
    return null;
  }


  @Override
  public List<Route> loadPrimaryRoutes() {
    return Collections.emptyList();
  }


  @Override
  public Point2D getCenter() {
    return null;
  }


  private ScheduleSummary toSummary(Schedule schedule) {
    DaySchedule[] schedules = schedule.getSchedules();
    ScheduleId[] scheduleIds = new ScheduleId[schedules.length];
    for( int i = 0; i < schedules.length; i++ ) {
      scheduleIds[i] = schedules[i].getScheduleId();
    }
    return new ScheduleSummary(scheduleIds, schedule.getDayChange());    
  }
  
  @Override
  public ScheduleSummary loadScheduleSummary(RouteId routeId) {
    Schedule schedule = loadSchedule(routeId);
    return toSummary(schedule);
  }


  @Override
  public DaySchedule loadDaySchedule(RouteId routeId, ScheduleId scheduleId) {
    Schedule schedule = loadSchedule(routeId);
    return schedule.getSchedule(scheduleId);
  }

  @Override
  public DaySchedule loadDaySchedule(RouteId routeId, Date date) {
    Schedule schedule = loadSchedule(routeId);
    ScheduleSummary summary = toSummary(schedule);
    ScheduleId scheduleId = summary.getScheduleId(date);
    if ( scheduleId != null) {
      return schedule.getSchedule(scheduleId);
    }
    return null;
  }

  @Override
  public List<Agency> loadAgencies() {
    return Collections.emptyList();
  }

  @Override
  public String getDefaultAgencyName() {
    return null;
  }
}

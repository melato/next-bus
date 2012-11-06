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
package org.melato.bus.model.xml;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.melato.bus.model.DaySchedule;
import org.melato.bus.model.Route;
import org.melato.bus.model.Schedule;
import org.melato.xml.XMLWriter;


/**
 * Writes routes to XML
 * @author Alex Athanasopoulos
 */
public class ScheduledRouteWriter extends RouteWriter {
  public static final String SCHEDULE = "schedule";
  public static final String SCHEDULE_COMMENT = "schedule_comment";
  private void writeSchedules(Schedule schedule, XMLWriter xml) {
    if ( schedule == null ) {
      return;
    }
    if ( schedule.getComment() != null ) {
      xml.println();
      xml.tagOpen(SCHEDULE_COMMENT);
      xml.text(schedule.getComment());
      xml.tagEnd(SCHEDULE_COMMENT);      
    }
    for( DaySchedule daySchedule: schedule.getSchedules() ) {
      xml.println();
      xml.tagOpen( SCHEDULE, false );
      xml.tagAttribute(DAYS, String.valueOf(daySchedule.getDays()));
      xml.tagClose();
      for( int time: daySchedule.getTimes() ) {
        xml.println();
        xml.tagOpen( TIME, false );
        xml.tagAttribute( VALUE, Schedule.formatTime(time));
        xml.tagClose(true);
      }
      xml.println();
      xml.tagEnd( SCHEDULE );
      xml.println();
    }
  }
  public void write(ScheduledRoute sr, XMLWriter xml) {
    Route route = sr.getRoute();
    routeBegin(route, xml);
    routeBody(route, xml);
    writeSchedules(sr.getSchedule(), xml);    
    routeEnd(route, xml);
  }
  public void write(ScheduledRoute sr, File file) throws IOException {
    XMLWriter xml = new XMLWriter(file);
    begin(xml);
    write(sr, xml);
    end(xml);
    xml.close();
  }
  public void writeScheduledRoutes(List<ScheduledRoute> routes, File file) throws IOException {
    XMLWriter xml = new XMLWriter(file);
    try {
      begin(xml);
      for( ScheduledRoute route: routes ) {
        write(route, xml);
      }
      end(xml);
    } finally {
      xml.close();
    }
  }
  
}

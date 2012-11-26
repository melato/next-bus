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
package org.melato.bus.model.xml;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.melato.bus.model.Route;
import org.melato.bus.model.Schedule;
import org.melato.xml.XMLStringHandler;
import org.xml.sax.SAXException;

/**
 * XML element handler for route
 * @author Alex Athanasopoulos
 */
public class ScheduledRouteHandler extends RouteHandler {
  private List<ScheduledRoute> scheduledRoutes = new ArrayList<ScheduledRoute>();
  private ScheduleHandler scheduleHandler = new ScheduleHandler();
  private XMLStringHandler commentHandler = new XMLStringHandler();
  
  public ScheduledRouteHandler() {
    super();
    setHandler( ScheduledRouteWriter.SCHEDULE, scheduleHandler );
    setHandler( ScheduledRouteWriter.SCHEDULE_COMMENT, commentHandler );
  }

  protected void addRoute(Route route) {
    Schedule schedule = scheduleHandler.getSchedule();
    schedule.setComment(commentHandler.getText());
    ScheduledRoute sr = new ScheduledRoute(route, schedule);
    scheduledRoutes.add(sr);
  }
  
  @Override
  public void end() throws SAXException {
    super.end();
    scheduleHandler.clear();
  }
  
  public static List<ScheduledRoute> parseScheduledRoutes(InputStream in) throws IOException, SAXException {
    ScheduledRouteHandler ScheduledRouteHandler = new ScheduledRouteHandler();
    ScheduledRouteHandler.parse(in);
    return ScheduledRouteHandler.getScheduledRoutes();
  }
  public static List<ScheduledRoute> parseScheduledRoutes(File file) throws IOException {
    try {
      return parseScheduledRoutes(new FileInputStream(file));
    } catch(SAXException e ) {
      throw new RuntimeException(e);
    }
  }
  public List<ScheduledRoute> getScheduledRoutes() {
    return scheduledRoutes;
  }
  
}

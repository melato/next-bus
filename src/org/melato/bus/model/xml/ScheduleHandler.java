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

import java.util.ArrayList;
import java.util.List;

import org.melato.bus.model.DaySchedule;
import org.melato.bus.model.Schedule;
import org.melato.xml.XMLMappingHandler;
import org.melato.xml.XMLTag;
import org.xml.sax.SAXException;

/**
 * XML element handler for schedules.
 * Handles the schedule element, which contains information for one day-schedule.
 * @author Alex Athanasopoulos
 */
public class ScheduleHandler extends XMLMappingHandler {
  TimeHandler timeHandler = new TimeHandler();
  int days;
  List<DaySchedule> schedules = new ArrayList<DaySchedule>();
  ScheduleHandler() {
    setHandler( RouteWriter.TIME, timeHandler );
  }

  public void clear() {
    timeHandler.clear();
    schedules.clear();
    days = 0;
  }
  
  public Schedule getSchedule() {
    return new Schedule( schedules.toArray( new DaySchedule[0] ) );
  }
  @Override
  public void start(XMLTag tag) throws SAXException {
    days = Integer.parseInt(tag.getRequiredAttribute(RouteWriter.DAYS));
    timeHandler.clear();
  }
  
  @Override
  public void end() throws SAXException {
    DaySchedule schedule = new DaySchedule(timeHandler.getTimes(), days);
    schedules.add(schedule);
  }  
}

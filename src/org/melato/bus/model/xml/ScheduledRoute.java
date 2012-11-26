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

import org.melato.bus.model.Route;
import org.melato.bus.model.Schedule;

/** An object that holds a route and its schedule. */
public class ScheduledRoute {
  private Route route;
  private Schedule schedule;
  public ScheduledRoute(Route route, Schedule schedule) {
    super();
    this.route = route;
    this.schedule = schedule;
  }
  public Route getRoute() {
    return route;
  }
  public Schedule getSchedule() {
    return schedule;
  }
}

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
package org.melato.bus.model;

import java.io.Serializable;

/**
 * A RouteId is a persistent id for the route.
 * It is used as the database id in the XmlRouteModel, and as a cross-database route id.
 * @author Alex Athanasopoulos
 *
 */
public class RouteId implements Serializable {
  private static final long serialVersionUID = 1L;
  /** The internal name, e.g. 301b */
  private String  name;
  /** The direction of the route, "1" for outgoing, "2" for incoming.  */
  private String direction;
    
  public RouteId() {
  }
  public RouteId(String name, String direction) {
    super();
    this.name = name;
    this.direction = direction;
  }
  public RouteId(String stringId) {
    String[] fields = stringId.split("-");
    name = fields[0];
    direction = fields[1];    
  }
  
  @Override
  public String toString() {
    return name + "-" + direction;
  }
  
  public String getName() {
    return name;
  }
  public String getDirection() {
    return direction;
  }
  
  public void setName(String name) {
    this.name = name;
  }
  public void setDirection(String direction) {
    this.direction = direction;
  }
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((direction == null) ? 0 : direction.hashCode());
    result = prime * result + ((name == null) ? 0 : name.hashCode());
    return result;
  }
  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    RouteId other = (RouteId) obj;
    if (direction == null) {
      if (other.direction != null)
        return false;
    } else if (!direction.equals(other.direction))
      return false;
    if (name == null) {
      if (other.name != null)
        return false;
    } else if (!name.equals(other.name))
      return false;
    return true;
  }
}

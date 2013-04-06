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

import java.io.Serializable;


/**
 * Contains basic information about a bus route in a certain direction,
 * including its schedule. 
 * @author Alex Athanasopoulos
 *
 */
public class Route implements Cloneable, Serializable, Comparable<Route> {
  private static final long serialVersionUID = 1L;

  private RouteId routeId;
  /** The internal agency name */
  private String  agencyName;
  
  /** The label that the user sees, e.g. "301B"
   * The label is usually name in uppercase.
   * */
  private String label;
  
  /** The longer descriptive title of the bus line. */
  private String      title; // e.g. "Γραμμή 304 ΣΤ. ΝΟΜΙΣΜΑΤΟΚΟΠΕΙΟ - ΑΡΤΕΜΙΣ (ΒΡΑΥΡΩΝΑ)"
  private int color = 0x0000ff;
  private int backgroundColor = 0;
  private boolean primary;
  
  public Route() {
    super();
  }

  @Override
  public Route clone() {
    try {
      return (Route) super.clone();
    } catch( CloneNotSupportedException e ) {
      throw new RuntimeException(e);
    }
  }
  
  public String getAgencyName() {
    return agencyName;
  }

  public void setAgencyName(String agencyName) {
    this.agencyName = agencyName;
  }

  public String getLabel() {
    return label;
  }

  public void setLabel(String label) {
    this.label = label;
  }

  public String getTitle() {
    return title;
  }
  public void setTitle(String title) {
    this.title = title;
  }
  
  public int getColor() {
    return color;
  }

  public void setColor(int color) {
    this.color = color;
  }

  public int getBackgroundColor() {
    return backgroundColor;
  }

  public void setBackgroundColor(int backgroundColor) {
    this.backgroundColor = backgroundColor;
  }

  public boolean isPrimary() {
    return primary;
  }

  public void setPrimary(boolean primary) {
    this.primary = primary;
  }

  public String getDirection() {
    return routeId.getDirection();
  }
  
  public String getFullTitle() {
    return getLabel() + " " + getTitle();
  }
  
  @Override
  public String toString() {
    return getFullTitle();
  }

  @Override
  public int compareTo(Route r) {
    int d = AlphanumericComparator.INSTANCE.compare(label, r.label);
    if ( d != 0 )
      return d;
    return AlphanumericComparator.INSTANCE.compare(getDirection(), r.getDirection());
  }
  
  /** This is an external route id, used for storing in caches, etc.
   * */
  public RouteId getRouteId() {
    return routeId;
  }

  public void setRouteId(RouteId routeId) {
    this.routeId = routeId;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((routeId == null) ? 0 : routeId.hashCode());
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
    Route other = (Route) obj;
    if (routeId == null) {
      if (other.routeId != null)
        return false;
    } else if (!routeId.equals(other.routeId))
      return false;
    return true;
  }
  
  public boolean isSameColor(Route route) {
    return color == route.color && backgroundColor == route.backgroundColor;
  }

}

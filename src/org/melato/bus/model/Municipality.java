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

import org.melato.gps.Point2D;

public class Municipality implements Serializable, Comparable<Municipality> {
  private static final long serialVersionUID = 1L;
  public String name;
  public String mayor;
  public String phone;
  public String website;
  public String address;
  public String postalCode;
  public String city;
  public Point2D point;

  public String getName() {
    return name;
  }
  
  public String getMayor() {
    return mayor;
  }

  public String getPhone() {
    return phone;
  }

  public void setPhone(String phone) {
    this.phone = phone;
  }

  public void setMayor(String mayor) {
    this.mayor = mayor;
  }

  public String getWebsite() {
    return website;
  }

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public String getPostalCode() {
    return postalCode;
  }

  public void setPostalCode(String postalCode) {
    this.postalCode = postalCode;
  }

  public String getCity() {
    return city;
  }

  public void setCity(String city) {
    this.city = city;
  }

  public Point2D getPoint() {
    return point;
  }

  public void setPoint(Point2D point) {
    this.point = point;
  }

  public void setWebsite(String website) {
    this.website = website;
  }


  public void setName(String name) {
    this.name = name;
  }


  @Override
  public String toString() {
    return name != null ? name : "";
  }

  public Municipality(String name) {
    super();
    this.name = name;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
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
    Municipality other = (Municipality) obj;
    if (name == null) {
      if (other.name != null)
        return false;
    } else if (!name.equals(other.name))
      return false;
    return true;
  }
  
  public boolean hasDetails() {
    return mayor != null;
  }

  @Override
  public int compareTo(Municipality o) {
    return name.compareTo(o.name);
  }
  
  
}
